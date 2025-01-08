package com.testing.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDTO {
    private Long id;
    private Long periodId;
    private Long questionId;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
}
