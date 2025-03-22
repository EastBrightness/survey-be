package com.testing.survey.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultResponseDTO {
    private String employeeName;
    private String employeeNumber;
    private double selfEvaluationScore;
    private double othersEvaluationScore;
    private double totalScore;
    private double overallPercentile;
    private double sameRankPercentile;
    private Map<String, Double> selfScoresByCategory;
    private Map<String, Double> othersScoresByCategory;
    private List<String> textFeedback;
}