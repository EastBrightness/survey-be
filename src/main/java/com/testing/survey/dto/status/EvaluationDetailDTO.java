package com.testing.survey.dto.status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvaluationDetailDTO {
    private String testedNumber;
    private String testedName;
    private String organization;
    private String position;
    private String grade;
    private Boolean isCompleted;
}