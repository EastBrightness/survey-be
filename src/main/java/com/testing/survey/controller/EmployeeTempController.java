package com.testing.survey.controller;

import com.testing.survey.dto.EmployeeTempDTO;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.service.EmployeeTempService;
import com.testing.survey.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeTempController {
    private final EmployeeTempService employeeService;
    private final OrganizationService organizationService;

    @GetMapping("/organization/{orgName}")
    public ResponseEntity<List<EmployeeTempDTO>> getEmployeesByOrganization(
            @PathVariable String orgName) {
        return ResponseEntity.ok(employeeService.getEmployeesByOrganization(orgName));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeTemp>> searchEmployees(@RequestParam String name) {
        return ResponseEntity.ok(employeeService.searchEmployeesByName(name));
    }

    @PutMapping("/{employeeNumber}")
    public ResponseEntity<Void> updateEmployee(
            @PathVariable String employeeNumber,
            @RequestBody EmployeeTempDTO employeeDTO) {
        employeeService.updateEmployee(employeeNumber, employeeDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-random")
    public ResponseEntity<Void> assignRandomEvaluators() {
        employeeService.assignRandomEvaluators();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-by-organizations")
    public ResponseEntity<Void> deleteEmployeesByOrganizations(@RequestBody List<String> organizationNames) {
        employeeService.deleteOrganizationEmployees(organizationNames);
//        organizationService.
        return ResponseEntity.ok().build();
    }

    // 직원 단일 조회 엔드포인트 추가
    @GetMapping("only/{employeeNumber}")
    public ResponseEntity<EmployeeTempDTO> getEmployee(@PathVariable String employeeNumber) {
        return employeeService.findByEmployeeNumber(employeeNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tested/{employeeNumber}")
    public ResponseEntity<EmployeeTempDTO> getTestedEmployee(@PathVariable String employeeNumber) {
        return employeeService.findByEmployeeNumber(employeeNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}