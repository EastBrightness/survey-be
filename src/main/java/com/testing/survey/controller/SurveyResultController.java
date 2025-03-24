package com.testing.survey.controller;

import com.testing.survey.dto.SurveyResultResponseDTO;
import com.testing.survey.service.SurveyResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/survey")
public class SurveyResultController {

    @Autowired
    private SurveyResultService surveyResultService;

    @GetMapping("/result/{employeeNumber}/{periodId}")
    public ResponseEntity<SurveyResultResponseDTO> getSurveyResult(
            @PathVariable String employeeNumber,
            @PathVariable Long periodId) {
        SurveyResultResponseDTO result = surveyResultService.getSurveyResult(employeeNumber, periodId);
        return ResponseEntity.ok(result);
    }
}