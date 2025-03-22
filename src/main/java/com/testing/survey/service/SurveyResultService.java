package com.testing.survey.service;

import com.testing.survey.dto.SurveyResultResponseDTO;
import com.testing.survey.entity.SurveyResponse;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.repository.EmployeeTempRepository;
import com.testing.survey.repository.SurveyResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SurveyResultService {

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private EmployeeTempRepository employeeTempRepository;

    // For demonstration, we're setting these values as constants
    private final Long currentPeriodId = 1L;

    @Transactional
    public SurveyResultResponseDTO getSurveyResult(String employeeNumber) {
        // Mark result as viewed
        employeeTempRepository.updateSawResultByEmployeeNumber(employeeNumber);

        // Get employee info
        EmployeeTemp employee = employeeTempRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

// In SurveyResultService
// Get self-evaluation scores
        List<SurveyResponse> selfResponses = surveyResponseRepository
                .findByPeriodIdAndRespondentNumberAndEvaluationType(
                        currentPeriodId, employeeNumber, SurveyResponse.EvaluationType.SELF);

// Get others-evaluation scores
        List<SurveyResponse> othersResponses = surveyResponseRepository
                .findByPeriodIdAndTestedNumberAndEvaluationType(
                        currentPeriodId, employeeNumber, SurveyResponse.EvaluationType.OTHERS);

        // Calculate self-evaluation total score
        double selfEvaluationScore = selfResponses.stream()
                .mapToDouble(SurveyResponse::getRespondentScore)
                .sum();

        // Calculate others-evaluation average score
        // Group by question ID to average scores for the same question
        Map<Long, Double> othersScoresByQuestion = othersResponses.stream()
                .collect(Collectors.groupingBy(
                        SurveyResponse::getQuestionId,
                        Collectors.averagingDouble(SurveyResponse::getRespondentScore)
                ));

        double othersEvaluationScore = othersScoresByQuestion.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Calculate total score for percentile calculations
        double totalScore = selfEvaluationScore + othersEvaluationScore;

        // Calculate percentiles
        double overallPercentile = calculateOverallPercentile(employeeNumber, totalScore);
        double sameRankPercentile = calculateSameRankPercentile(employeeNumber,
                totalScore,
                employee.getGradeName());

        // Calculate scores by category
        Map<String, Double> selfScoresByCategory = calculateScoresByCategory(selfResponses);
        Map<String, Double> othersScoresByCategory = calculateOthersScoresByCategory(othersResponses);

        // Get text feedback
        List<String> textFeedback = surveyResponseRepository
                .findDistinctTextAnswersByPeriodIdAndTestedNumber(currentPeriodId, employeeNumber);

        // Build the response DTO
        return SurveyResultResponseDTO.builder()
                .employeeName(employee.getPersonName())
                .employeeNumber(employeeNumber)
                .selfEvaluationScore(selfEvaluationScore)
                .othersEvaluationScore(Math.round(othersEvaluationScore * 100) / 100.0) // Round to 2 decimal places
                .totalScore(totalScore)
                .overallPercentile(overallPercentile)
                .sameRankPercentile(sameRankPercentile)
                .selfScoresByCategory(selfScoresByCategory)
                .othersScoresByCategory(othersScoresByCategory)
                .textFeedback(textFeedback)
                .build();
    }

    private double calculateOverallPercentile(String employeeNumber, double employeeScore) {
        List<Object[]> allScores = surveyResponseRepository.findTotalScoresByPeriodId(currentPeriodId);

        // Convert to map of employee number to score
        Map<String, Double> employeeScores = new HashMap<>();
        for (Object[] score : allScores) {
            String empNum = (String) score[0];
            Double scoreValue = ((Number) score[1]).doubleValue();
            if (empNum != null) {
                employeeScores.put(empNum, scoreValue);
            } else {
                // Log or handle the case of null employee number
                System.err.println("Warning: Null employee number encountered");
            }
        }


        // Count how many employees have lower scores
        long lowerScoreCount = employeeScores.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(employeeNumber))
                .filter(entry -> entry.getValue() < employeeScore)
                .count();

        // Calculate percentile
        int totalEmployees = employeeScores.size();
        if (totalEmployees <= 1) {
            return 100.0; // If there's only one employee, they're at the 100th percentile
        }

        return (double) lowerScoreCount / (totalEmployees - 1) * 100;
    }

    private double calculateSameRankPercentile(String employeeNumber, double employeeScore, String rank) {
        List<Object[]> sameRankScores = surveyResponseRepository
                .findTotalScoresByPeriodIdAndRank(currentPeriodId, rank);

        // Convert to map of employee number to score
        Map<String, Double> employeeScores = new ConcurrentHashMap<>();
        for (Object[] score : sameRankScores) {
            String empNum = (String) score[0];
            Double scoreValue = ((Number) score[1]).doubleValue();
            employeeScores.put(empNum, scoreValue);
        }

        // Count how many employees of the same rank have lower scores
        long lowerScoreCount = employeeScores.entrySet().stream()
                .filter(entry -> entry.getKey() != null && !entry.getKey().equals(employeeNumber))
                .filter(entry -> entry.getValue() < employeeScore)
                .count();

        // Calculate percentile
        int totalSameRankEmployees = employeeScores.size();
        if (totalSameRankEmployees <= 1) {
            return 100.0; // If there's only one employee in the rank, they're at the 100th percentile
        }

        return (double) lowerScoreCount / (totalSameRankEmployees - 1) * 100;
    }

    private Map<String, Double> calculateScoresByCategory(List<SurveyResponse> responses) {
        return responses.stream()
                .collect(Collectors.groupingBy(
                        SurveyResponse::getCategory,
                        Collectors.summingDouble(SurveyResponse::getRespondentScore)
                ));
    }

    private Map<String, Double> calculateOthersScoresByCategory(List<SurveyResponse> responses) {
        // First group by category and question ID to get average scores per question
        Map<String, Map<Long, Double>> scoresByCategory = responses.stream()
                .collect(Collectors.groupingBy(
                        SurveyResponse::getCategory,
                        Collectors.groupingBy(
                                SurveyResponse::getQuestionId,
                                Collectors.averagingDouble(SurveyResponse::getRespondentScore)
                        )
                ));

        // Then sum up the averages for each category
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, Map<Long, Double>> entry : scoresByCategory.entrySet()) {
            double sum = entry.getValue().values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
            result.put(entry.getKey(), sum);
        }

        return result;
    }
}