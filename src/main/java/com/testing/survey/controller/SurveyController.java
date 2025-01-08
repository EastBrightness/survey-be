package com.testing.survey.controller;

import com.testing.survey.dto.QuestionDTO;
import com.testing.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller 클래스
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    @PostMapping("/questions")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(surveyService.createQuestion(questionDTO));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(surveyService.updateQuestion(id, questionDTO));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        surveyService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions(@RequestParam Long periodId) {
        return ResponseEntity.ok(surveyService.getQuestions(periodId));
    }
}
