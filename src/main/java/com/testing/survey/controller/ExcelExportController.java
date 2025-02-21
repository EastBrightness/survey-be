package com.testing.survey.controller;
import com.testing.survey.service.EvaluationStatusService;
import com.testing.survey.service.ExcelExportService;
import com.testing.survey.dto.status.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelExportController {
    private final EvaluationStatusService evaluationStatusService;
    private final ExcelExportService excelExportService;

    @GetMapping("/department/{departmentName}")
    public ResponseEntity<byte[]> exportDepartmentStatus(@PathVariable String departmentName) throws IOException {
        DepartmentStatusDTO status = evaluationStatusService.getDepartmentStatus(departmentName);
        byte[] excelContent = excelExportService.exportDepartmentStatus(status);
        return createExcelResponse(excelContent, "부서현황.xls");
    }

    @GetMapping("/group/{departmentName}")
    public ResponseEntity<byte[]> exportGroupStatus(@PathVariable String departmentName) throws IOException {
        List<GroupStatusDTO> status = evaluationStatusService.getGroupStatus(departmentName);
        byte[] excelContent = excelExportService.exportGroupStatus(status);
        return createExcelResponse(excelContent, "그룹현황.xls");
    }

    @GetMapping("/person/{organizationName}")
    public ResponseEntity<byte[]> exportPersonStatus(@PathVariable String organizationName) throws IOException {
        List<PersonStatusDTO> status = evaluationStatusService.getPersonStatus(organizationName);
        byte[] excelContent = excelExportService.exportPersonStatus(status);
        return createExcelResponse(excelContent, "개인현황.xls");
    }

    @GetMapping("/evaluation-detail/{testerNumber}")
    public ResponseEntity<byte[]> exportEvaluationDetail(@PathVariable String testerNumber) throws IOException {
        List<EvaluationDetailDTO> status = evaluationStatusService.getEvaluationDetail(testerNumber);
        byte[] excelContent = excelExportService.exportEvaluationDetail(status);
        return createExcelResponse(excelContent, "평가상세.xls");
    }

    private ResponseEntity<byte[]> createExcelResponse(byte[] content, String fileName) {
        String encodedFileName;
        try {
            encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");  // 공백을 %20으로 변경
        } catch (Exception e) {
            encodedFileName = fileName;
        }

        HttpHeaders headers = new HttpHeaders();
        // application/vnd.ms-excel로 Content-Type 변경
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        // Content-Disposition 헤더 직접 설정
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s",
                        fileName, encodedFileName));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(content);
    }
}