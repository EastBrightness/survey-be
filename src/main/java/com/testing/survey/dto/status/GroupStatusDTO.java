package com.testing.survey.dto.status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupStatusDTO {
    private String groupName;
    private double selfEvaluationRate;
    private double otherEvaluationRate;
}