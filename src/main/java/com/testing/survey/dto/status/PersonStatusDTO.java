package com.testing.survey.dto.status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonStatusDTO {
    private String employeeNumber;
    private String personName;
    private String organizationName;
    private String jobName;
    private String gradeName;
    private Boolean selfEvaluationTarget;
    private Boolean otherEvaluationTarget;
    private Boolean completedSelf;
    private double otherEvaluationRate;
}