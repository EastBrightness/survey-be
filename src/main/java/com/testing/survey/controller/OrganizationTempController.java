package com.testing.survey.controller;

import com.testing.survey.dto.OrganizationTempDTO;
import com.testing.survey.service.OrganizationTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationTempController {
    private final OrganizationTempService organizationService;

    @GetMapping("/tree")
    public ResponseEntity<List<OrganizationTempDTO>> getOrganizationTree(
            @RequestParam(defaultValue = "1") String rootOCode) {
        return ResponseEntity.ok(organizationService.getOrganizationTree(rootOCode));
    }

    @PostMapping("/{oCode}/toggle-status")
    public ResponseEntity<Void> toggleOrganizationStatus(
            @PathVariable String oCode,
            @RequestParam boolean isDeleted) {
        organizationService.toggleOrganizationStatus(oCode, isDeleted);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteOrganizations(@RequestBody List<String> organizationNames) {
        organizationService.deleteOrganizations(organizationNames);
        return ResponseEntity.ok().build();
    }
}