package com.testing.survey.controller;

import com.testing.survey.dto.EvaluationRequest;
import com.testing.survey.entity.org.Organization;
import com.testing.survey.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller class
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<List<Organization>> getOrganizations(@RequestParam String upCode) {
        return ResponseEntity.ok(organizationService.getSubOrganizations(upCode));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Void> saveEvaluations(@RequestBody EvaluationRequest request) {
        organizationService.saveEvaluations(request.getOrganizationIds(), request.getPeriodId());
        return ResponseEntity.ok().build();
    }
}