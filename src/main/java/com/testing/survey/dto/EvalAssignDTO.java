package com.testing.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class EvalAssignDTO {
    private Long id;
    private String tested;
    private String tester;
    private Boolean isCompleted = false;
}