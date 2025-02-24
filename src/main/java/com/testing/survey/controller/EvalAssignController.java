package com.testing.survey.controller;

import com.testing.survey.dto.EvalAssignDTO;
import com.testing.survey.service.EvalAssignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eval-assigns")
@RequiredArgsConstructor
public class EvalAssignController {
    private final EvalAssignService evalAssignService;

    @GetMapping("/tested/{tested}")
    public ResponseEntity<List<EvalAssignDTO>> getAssignmentsByTested(
            @PathVariable String tested) {
        return ResponseEntity.ok(evalAssignService.getAssignmentsByTested(tested));
    }

    @PutMapping("/{tester}/{tested}")
    public ResponseEntity<Void> updateAssignmentStatus(
            @PathVariable String tester,
            @PathVariable String tested,
            @RequestParam boolean isCompleted) {
        evalAssignService.updateAssignmentStatus(tester, tested, isCompleted);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tester/{tester}")
    public ResponseEntity<List<EvalAssignDTO>> getAssignmentsByTester(
            @PathVariable String tester) {
        return ResponseEntity.ok(evalAssignService.getAssignmentsByTester(tester));
    }

    @DeleteMapping("/tested/{tested}")
    public ResponseEntity<Void> deleteAssignments(@PathVariable String tested) {
        evalAssignService.deleteAssignments(tested);
        return ResponseEntity.ok().build();
    }
}