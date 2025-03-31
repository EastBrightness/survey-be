package com.testing.survey.service;

import com.testing.survey.dto.statistics.StatisticsRequestDTO;
import com.testing.survey.dto.statistics.StatisticsResponseDTO;
import com.testing.survey.entity.SurveyResponse;
import com.testing.survey.entity.eval.EvaluationPeriod;
import com.testing.survey.repository.StatisticsEmployeeRepository;
import com.testing.survey.repository.StatisticsEvaluationPeriodRepository;
import com.testing.survey.repository.StatisticsOrganizationRepository;
import com.testing.survey.repository.StatisticsSurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsEvaluationPeriodRepository evaluationPeriodRepository;
    private final StatisticsEmployeeRepository employeeRepository;
    private final StatisticsOrganizationRepository organizationRepository;
    private final StatisticsSurveyResponseRepository surveyResponseRepository;

    @Transactional(readOnly = true)
    public List<String> getAvailableYears() {
        return evaluationPeriodRepository.findDistinctYears();
    }

    @Transactional(readOnly = true)
    public List<EvaluationPeriod> getEvaluationsByYear(String year) {
        return evaluationPeriodRepository.findEvaluationsByYear(year);
    }

    @Transactional(readOnly = true)
    public List<String> getPersonTypes() {
        return employeeRepository.findDistinctPersonTypes();
    }

    @Transactional(readOnly = true)
    public List<String> getGrades() {
        return employeeRepository.findDistinctGrades();
    }

    @Transactional(readOnly = true)
    public List<String> getSexes() {
        return employeeRepository.findDistinctSexes();
    }

    @Transactional(readOnly = true)
    public StatisticsResponseDTO calculateStatistics(StatisticsRequestDTO request) {
        // 평가 기간 ID 찾기 (요청된 연도와 평가명으로)
        EvaluationPeriod period = evaluationPeriodRepository.findEvaluationsByYear(request.getYear()).stream()
                .filter(p -> p.getEvaluationName().equals(request.getEvaluationName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 평가 기간을 찾을 수 없습니다."));

        // 계급별, 신분별, 성별 통계 계산
        Map<String, Map<String, Double>> gradeStats = convertToDualMap(
                employeeRepository.calculateGradeStatistics(period.getId())
        );

        Map<String, Map<String, Double>> personTypeStats = convertToDualMap(
                employeeRepository.calculatePersonTypeStatistics(period.getId())
        );

        Map<String, Map<String, Double>> sexStats = convertToDualMap(
                employeeRepository.calculateSexStatistics(period.getId())
        );

        // 조직별 통계 계산
        Map<String, Map<String, Double>> organizationStats = convertToDualMap(
                employeeRepository.calculateOrganizationStatistics(period.getId())
        );

        // 문항별 통계 계산 (자가평가와 타인평가 구분)
        Map<String, Map<String, Double>> questionStats = convertToQuestionMap(
                surveyResponseRepository.calculateQuestionStatisticsBothTypes(period.getId())
        );

        return StatisticsResponseDTO.builder()
                // 전체 평가 결과 종합
                .averageSelfScore(employeeRepository.calculateAverageSelfScore(period.getId()))
                .averageOthersScore(employeeRepository.calculateAverageOthersScore(period.getId()))
                .selfCompletionRate(employeeRepository.calculateSelfCompletionRate(period.getId()))
                .othersCompletionRate(employeeRepository.calculateOthersCompletionRate(period.getId()))

                // 분류별 통계
                .gradeStatistics(gradeStats)
                .personTypeStatistics(personTypeStats)
                .sexStatistics(sexStats)
                .organizationStatistics(organizationStats)
                .questionStatistics(questionStats)
                .build();
    }

    // 쿼리 결과를 자가평가/타인평가로 구분된 이중 Map으로 변환
    private Map<String, Map<String, Double>> convertToDualMap(List<Object[]> statResults) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (Object[] row : statResults) {
            String key = String.valueOf(row[0]);
            double selfScore = ((Number) row[1]).doubleValue();
            double othersScore = ((Number) row[2]).doubleValue();

            Map<String, Double> scores = new HashMap<>();
            scores.put("self", selfScore);
            scores.put("others", othersScore);

            result.put(key, scores);
        }

        return result;
    }

    // 문항별 통계 결과를 Map으로 변환
    private Map<String, Map<String, Double>> convertToQuestionMap(List<Object[]> statResults) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (Object[] row : statResults) {
            String questionId = String.valueOf(row[0]);
            SurveyResponse.EvaluationType type = (SurveyResponse.EvaluationType) row[1];
            double score = ((Number) row[2]).doubleValue();

            result.computeIfAbsent(questionId, k -> new HashMap<>());

            if (type == SurveyResponse.EvaluationType.SELF) {
                result.get(questionId).put("self", score);
            } else if (type == SurveyResponse.EvaluationType.OTHERS) {
                result.get(questionId).put("others", score);
            }
        }

        return result;
    }

    // 엑셀 다운로드를 위한 메서드 (추후 구현)
    public byte[] generateExcelReport(StatisticsRequestDTO request) {
        // TODO: Apache POI를 사용한 엑셀 리포트 생성 로직
        throw new UnsupportedOperationException("엑셀 다운로드 기능 준비 중");
    }
}