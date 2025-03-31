package com.testing.survey.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsRequestDTO {
    private String year;
    private String evaluationName;
    private String organizationCode;
    private String personType;
    private String grade;
    private String sex;
}