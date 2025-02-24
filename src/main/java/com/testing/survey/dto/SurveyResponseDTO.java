package com.testing.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SurveyResponseDTO {
    private Long questionId;
    private Integer selectedAnswer;
    private String testedNumber;  // 타인평가시에만 사용
    private String textAnswer;
}