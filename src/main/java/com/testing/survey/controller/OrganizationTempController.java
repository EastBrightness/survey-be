package com.testing.survey.controller;

import com.testing.survey.dto.OrganizationTempDTO;
import com.testing.survey.entity.temp.OrganizationTemp;
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


    @GetMapping("/deleted")
    public ResponseEntity<List<OrganizationTemp>> getDeletedOrganizations() {
        return ResponseEntity.ok(organizationService.getDeletedOrganizations());
    }

    @PostMapping("/{oCode}/restore")
    public ResponseEntity<Void> restoreOrganization(@PathVariable String oCode) {
        organizationService.restoreOrganization(oCode);
        return ResponseEntity.ok().build();
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