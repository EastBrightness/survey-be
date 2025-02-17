package com.testing.survey.dto.status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentStatusDTO {
    private String departmentName;
    private double selfEvaluationRate;
    private double selfEvaluationRemainRate;
    private double otherEvaluationRate;
    private double otherEvaluationRemainRate;
}