package com.testing.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SurveySubmissionDTO {
    private List<SurveyResponseDTO> responses;
    private String evaluationType;  // "SELF" 또는 "OTHERS"
}