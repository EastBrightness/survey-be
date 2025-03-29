package com.testing.survey.controller;

import com.testing.survey.dto.statistics.StatisticsRequestDTO;
import com.testing.survey.dto.statistics.StatisticsResponseDTO;
import com.testing.survey.entity.eval.EvaluationPeriod;
import com.testing.survey.service.StatisticsService;
import lombok.RequiredArgsConstructor;
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
        byte[] excelContent = statisticsService.generateExcelReport(request);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=evaluation_statistics.xlsx")
                .body(excelContent);
    }
}