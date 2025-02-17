package com.testing.survey.controller;

import com.testing.survey.dto.status.DepartmentStatusDTO;
import com.testing.survey.dto.status.EvaluationDetailDTO;
import com.testing.survey.dto.status.GroupStatusDTO;
import com.testing.survey.dto.status.PersonStatusDTO;
import com.testing.survey.service.EvaluationStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-status")
@RequiredArgsConstructor
public class EvaluationStatusController {
    private final EvaluationStatusService evaluationStatusService;

    @GetMapping("/department/{departmentName}")
    public ResponseEntity<DepartmentStatusDTO> getDepartmentStatus(@PathVariable String departmentName) {
        return ResponseEntity.ok(evaluationStatusService.getDepartmentStatus(departmentName));
    }

    @GetMapping("/group/{groupName}")
    public ResponseEntity<List<GroupStatusDTO>> getGroupStatus(@PathVariable String groupName) {
        return ResponseEntity.ok(evaluationStatusService.getGroupStatus(groupName));
    }

    @GetMapping("/person/{organizationName}")
    public ResponseEntity<List<PersonStatusDTO>> getPersonStatus(@PathVariable String organizationName) {
        return ResponseEntity.ok(evaluationStatusService.getPersonStatus(organizationName));
    }

    @GetMapping("/evaluation-detail/{testerNumber}")
    public ResponseEntity<List<EvaluationDetailDTO>> getEvaluationDetail(@PathVariable String testerNumber) {
        return ResponseEntity.ok(evaluationStatusService.getEvaluationDetail(testerNumber));
    }
}
