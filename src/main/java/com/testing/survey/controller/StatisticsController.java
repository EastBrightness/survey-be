package com.testing.survey.controller;

import com.testing.survey.dto.statistics.StatisticsRequestDTO;
import com.testing.survey.dto.statistics.StatisticsResponseDTO;
import com.testing.survey.entity.eval.EvaluationPeriod;
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

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

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
}