package com.testing.survey.dto.statistics;

import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class StatisticsResponseDTO {
    // 전체 평가 결과 종합
    private Double averageSelfScore;
    private Double averageOthersScore;
    private Double selfCompletionRate;
    private Double othersCompletionRate;

    // 분류별 평가 결과 - 각 통계는 key별로 자가평가(self)와 타인평가(others) 점수를 포함
    private Map<String, Map<String, Double>> gradeStatistics;
    private Map<String, Map<String, Double>> personTypeStatistics;
    private Map<String, Map<String, Double>> sexStatistics;
    private Map<String, Map<String, Double>> organizationStatistics;
    private Map<String, Map<String, Double>> questionStatistics;
}