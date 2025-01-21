package com.testing.survey.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EvaluationRequest {
    private List<Long> organizationIds;
    private String periodId;
}