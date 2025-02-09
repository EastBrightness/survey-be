package com.testing.survey.controller;

import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.entity.temp.OrganizationTemp;
import com.testing.survey.service.SurveyManageService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SurveyManageController {

    private final SurveyManageService surveyManageService;

    @GetMapping("control/organizations")
    public ResponseEntity<List<OrganizationTemp>> getOrganizations() {
        return ResponseEntity.ok(surveyManageService.getAllOrganizations());
    }

    @GetMapping("/employees/{orgId}")
    public ResponseEntity<List<EmployeeTemp>> getEmployeesByOrganization(@PathVariable Long orgId) {
        return ResponseEntity.ok(surveyManageService.getEmployeesByOrganization(orgId));
    }

    @GetMapping("/export/{orgId}")
    public ResponseEntity<ByteArrayResource> exportToExcel(@PathVariable Long orgId) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Survey Status");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Employee No.", "Name", "Department", "Self Evaluation", "Others Evaluation"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Add data rows
            List<EmployeeTemp> employees = surveyManageService.getEmployeesByOrganization(orgId);
            int rowNum = 1;
            for (EmployeeTemp emp : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getEmployeeNumber());
                row.createCell(1).setCellValue(emp.getPersonName());
                row.createCell(2).setCellValue(emp.getOrganizationName());
                row.createCell(3).setCellValue(emp.getCompletedSelf() ? "완료" : "미완료");
                row.createCell(4).setCellValue(emp.getCompletedOthers() ? "완료" : "미완료");
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=survey_status.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/send-emails")
    public ResponseEntity<?> sendEmails(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String type = (String) request.get("type");
        Long orgId = Long.parseLong(request.get("organizationId").toString());

        surveyManageService.sendReminderEmails(orgId, message, type);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/incomplete-count")
    public ResponseEntity<Map<String, Integer>> getIncompleteCount() {
        Map<String, Integer> counts = surveyManageService.getIncompleteCount();
        return ResponseEntity.ok(counts);
    }

    @PostMapping("/send-global-emails")
    public ResponseEntity<?> sendGlobalEmails(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String type = (String) request.get("type");

        surveyManageService.sendGlobalReminderEmails(message, type);
        return ResponseEntity.ok().build();
    }
}