package com.testing.survey.controller;

import com.testing.survey.dto.statistics.StatisticsRequestDTO;
import com.testing.survey.dto.statistics.StatisticsResponseDTO;
import com.testing.survey.entity.eval.EvaluationPeriod;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.repository.EmployeeTempRepository;
import com.testing.survey.repository.StatisticsEvaluationPeriodRepository;
import com.testing.survey.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final EmployeeTempRepository employeeTempRepository;
    private final StatisticsEvaluationPeriodRepository statisticsEvaluationPeriodRepository;

    @GetMapping("/years")
    public ResponseEntity<List<String>> getAvailableYears() {
        return ResponseEntity.ok(statisticsService.getAvailableYears());
    }

    @GetMapping("/evaluations")
    public ResponseEntity<List<EvaluationPeriod>> getEvaluationsByYear(
            @RequestParam String year
    ) {
        return ResponseEntity.ok(statisticsService.getEvaluationsByYear(year));
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<String>>> getFilterOptions() {
        Map<String, List<String>> filterOptions = new HashMap<>();
        filterOptions.put("personTypes", statisticsService.getPersonTypes());
        filterOptions.put("grades", statisticsService.getGrades());
        filterOptions.put("sexes", statisticsService.getSexes());

        return ResponseEntity.ok(filterOptions);
    }

    @PostMapping("/calculate")
    public ResponseEntity<StatisticsResponseDTO> calculateStatistics(
            @RequestBody StatisticsRequestDTO request
    ) {
        return ResponseEntity.ok(statisticsService.calculateStatistics(request));
    }

    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestBody StatisticsRequestDTO request
    ) {
        try {
            byte[] excelContent = statisticsService.generateExcelReport(request);

            // 적절한 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=evaluation_statistics.xlsx");
            headers.setContentLength(excelContent.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelContent);
        } catch (Exception e) {
            // 오류 로깅
//            log.error("엑셀 파일 생성 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/search-employee")
    public ResponseEntity<List<Map<String, String>>> searchEmployeeByName(
            @RequestParam String name,
            @RequestParam String year,
            @RequestParam String evaluationName) {

        // 평가 기간 ID 찾기
        Long periodId = statisticsEvaluationPeriodRepository.findEvaluationsByYear(year).stream()
                .filter(p -> p.getEvaluationName().equals(evaluationName))
                .findFirst()
                .map(EvaluationPeriod::getId)
                .orElseThrow(() -> new RuntimeException("해당 평가 기간을 찾을 수 없습니다."));

        // 직원 검색 - 해당 평가 기간에 참여한 직원들만 필터링
        List<EmployeeTemp> employees = employeeTempRepository.findByPersonNameContainingAndPeriodId(name, periodId);

        List<Map<String, String>> result = employees.stream()
                .map(emp -> {
                    Map<String, String> employeeInfo = new HashMap<>();
                    employeeInfo.put("employeeNumber", emp.getEmployeeNumber());
                    employeeInfo.put("personName", emp.getPersonName());
                    employeeInfo.put("organizationName", emp.getOrganizationName());
                    employeeInfo.put("jobName", emp.getJobName());
                    employeeInfo.put("displayText",
                            String.format("%s | %s | %s",
                                    emp.getOrganizationName(),
                                    emp.getJobName(),
                                    emp.getPersonName()));
                    return employeeInfo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/export-excel-by-employee")
    public ResponseEntity<byte[]> exportExcelByEmployee(@RequestBody Map<String, String> request) {
        String employeeNumber = request.get("employeeNumber");
        String year = request.get("year");
        String evaluationName = request.get("evaluationName");

        if (employeeNumber == null || employeeNumber.isEmpty() ||
                year == null || year.isEmpty() ||
                evaluationName == null || evaluationName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 평가 기간 ID 찾기
        Long periodId = statisticsEvaluationPeriodRepository.findEvaluationsByYear(year).stream()
                .filter(p -> p.getEvaluationName().equals(evaluationName))
                .findFirst()
                .map(EvaluationPeriod::getId)
                .orElseThrow(() -> new RuntimeException("해당 평가 기간을 찾을 수 없습니다."));

        byte[] excelContent = statisticsService.generateExcelReportForEmployee(employeeNumber, periodId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + employeeNumber + "_" + year + "_" + evaluationName + "_statistics.xlsx");
        headers.setContentLength(excelContent.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }
}