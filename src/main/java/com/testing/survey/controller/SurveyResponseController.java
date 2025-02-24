package com.testing.survey.controller;

import com.testing.survey.dto.QuestionDTO;
import com.testing.survey.dto.SurveySubmissionDTO;
import com.testing.survey.service.EmployeeTempService;
import com.testing.survey.service.QuestionService;
import com.testing.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyResponseController {
    private final SurveyService surveyService;
    private final QuestionService questionService;
    private final EmployeeTempService employeeService;

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions(
            @RequestParam Long periodId,
            @RequestParam String evaluationType) {
        return ResponseEntity.ok(questionService.getQuestionsByPeriodAndType(
                periodId,
                "SELF".equals(evaluationType)
        ));
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submitSurvey(
            @RequestBody SurveySubmissionDTO submission,
            @RequestParam String respondentNumber,
            @RequestParam Long periodId) {
        surveyService.processSurveySubmission(submission, respondentNumber, periodId);
        return ResponseEntity.ok().build();
    }
}