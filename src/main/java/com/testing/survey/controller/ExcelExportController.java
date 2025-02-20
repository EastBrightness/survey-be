package com.testing.survey.controller;

import com.testing.survey.service.EvaluationStatusService;
import com.testing.survey.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelExportController {

    private final EvaluationStatusService evaluationStatusService;
    private final ExcelExportService excelExportService;

    @GetMapping("/department/{departmentName}")
    public ResponseEntity<byte[]> exportDepartmentStatus(@PathVariable String departmentName) throws IOException {
        var status = evaluationStatusService.getDepartmentStatus(departmentName);
        byte[] excelContent = excelExportService.exportDepartmentStatus(status);

        return createExcelResponse(excelContent, "부서현황.xlsx");
    }

    @GetMapping("/group/{departmentName}")
    public ResponseEntity<byte[]> exportGroupStatus(@PathVariable String departmentName) throws IOException {
        var status = evaluationStatusService.getGroupStatus(departmentName);
        byte[] excelContent = excelExportService.exportGroupStatus(status);

        return createExcelResponse(excelContent, "그룹현황.xlsx");
    }

    @GetMapping("/person/{organizationName}")
    public ResponseEntity<byte[]> exportPersonStatus(@PathVariable String organizationName) throws IOException {
        var status = evaluationStatusService.getPersonStatus(organizationName);
        byte[] excelContent = excelExportService.exportPersonStatus(status);

        return createExcelResponse(excelContent, "개인현황.xlsx");
    }

    @GetMapping("/evaluation-detail/{testerNumber}")
    public ResponseEntity<byte[]> exportEvaluationDetail(@PathVariable String testerNumber) throws IOException {
        var status = evaluationStatusService.getEvaluationDetail(testerNumber);
        byte[] excelContent = excelExportService.exportEvaluationDetail(status);

        return createExcelResponse(excelContent, "평가상세.xlsx");
    }

    private ResponseEntity<byte[]> createExcelResponse(byte[] content, String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", encodedFileName);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(content);
    }
}