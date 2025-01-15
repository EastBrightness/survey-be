package com.testing.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO 클래스
@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private Long periodId;
    private String category;
    private String content;
    private Boolean targetYn;
    private AnswerDTO answers;
}
