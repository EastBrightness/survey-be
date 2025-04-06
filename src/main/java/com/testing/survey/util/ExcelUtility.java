package com.testing.survey.util;


import com.testing.survey.dto.statistics.StatisticsRequestDTO;
import com.testing.survey.entity.SurveyResponse;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.entity.temp.EvalAssign;
import com.testing.survey.repository.EvalAssignRepository;
import com.testing.survey.repository.SurveyResponseRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelUtility {

    public static byte[] generateEvaluationStatisticsExcel(
            List<EmployeeTemp> employees,
            List<Long> selfQuestionIds,
            List<Long> othersQuestionIds,
            Map<String, Map<Long, Integer>> selfResponsesByEmployee,
            Map<String, Map<Long, Integer>> othersResponsesByEmployee,
            Map<String, Map<String, Double>> questionStats,
            StatisticsRequestDTO request,
            SurveyResponseRepository surveyResponseRepository,
            EvalAssignRepository evalAssignRepository
    ) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("평가 통계");

            // 스타일 설정
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // 카테고리별 문항 그룹화
            Map<String, List<Long>> selfQuestionsByCategory = groupQuestionsByCategory(
                    selfQuestionIds,
                    surveyResponseRepository
            );

            Map<String, List<Long>> othersQuestionsByCategory = groupQuestionsByCategory(
                    othersQuestionIds,
                    surveyResponseRepository
            );

            // 카테고리 목록 (정렬된)
            List<String> selfCategories = new ArrayList<>(selfQuestionsByCategory.keySet());
            Collections.sort(selfCategories);

            List<String> othersCategories = new ArrayList<>(othersQuestionsByCategory.keySet());
            Collections.sort(othersCategories);

            // 헤더 생성
            List<String> allHeaders = new ArrayList<>();

            // 기본 헤더
            allHeaders.add("순번");
            allHeaders.add("소속");
            allHeaders.add("신분");
            allHeaders.add("통계계급");
            allHeaders.add("성명");
            allHeaders.add("사원번호");
            allHeaders.add("성별");
            allHeaders.add("종합실시율");

            // 자가평가 점수 헤더 (카테고리별)
            int selfQuestionTotalCount = 0;
            for (String category : selfCategories) {
                List<Long> categoryQuestions = selfQuestionsByCategory.get(category);
                for (int i = 0; i < categoryQuestions.size(); i++) {
                    allHeaders.add(category + " 문항" + (i + 1));
                }
                selfQuestionTotalCount += categoryQuestions.size();
            }

            // 자가평가 총점 헤더
            allHeaders.add("자가평가 총점");

            // 타인평가 점수 헤더 (카테고리별)
            int othersQuestionTotalCount = 0;
            for (String category : othersCategories) {
                List<Long> categoryQuestions = othersQuestionsByCategory.get(category);
                for (int i = 0; i < categoryQuestions.size(); i++) {
                    allHeaders.add(category + " 문항" + (i + 1));
                }
                othersQuestionTotalCount += categoryQuestions.size();
            }

            // 백분위, 참여인원 관련 헤더
            allHeaders.add("전체 백분위");
            allHeaders.add("동일계급 백분위");
            allHeaders.add("참여지정인원");
            allHeaders.add("실시인원");
            allHeaders.add("실시율");

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < allHeaders.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(allHeaders.get(i));
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            for (int i = 0; i < employees.size(); i++) {
                EmployeeTemp emp = employees.get(i);
                Row row = sheet.createRow(i + 1);

                int cellIndex = 0;

                // 1. 순번
                Cell cellNum = row.createCell(cellIndex++);
                cellNum.setCellValue(i + 1);
                cellNum.setCellStyle(dataStyle);

                // 2. 소속
                Cell cellOrg = row.createCell(cellIndex++);
                cellOrg.setCellValue(emp.getOrganizationName());

                // 3. 신분
                Cell cellType = row.createCell(cellIndex++);
                cellType.setCellValue(emp.getPersonTypeName());

                // 4. 통계계급
                Cell cellGrade = row.createCell(cellIndex++);
                cellGrade.setCellValue(emp.getRepGradeName());

                // 5. 성명
                Cell cellName = row.createCell(cellIndex++);
                cellName.setCellValue(emp.getPersonName());

                // 6. 사원번호
                Cell cellEmpNum = row.createCell(cellIndex++);
                cellEmpNum.setCellValue(emp.getEmployeeNumber());

                // 7. 성별
                Cell cellSex = row.createCell(cellIndex++);
                cellSex.setCellValue("M".equals(emp.getSex()) ? "남성" : "여성");

                // 8. 종합실시율 계산
                // 2-2. 종합실시율 = (자가평가 실시 할 경우 1 + 해당 평가자에 대한 타인평가 실시인원) / (1 + 해당 평가자에 대한 타인평가 배정인원) * 100
                Cell cellRate = row.createCell(cellIndex++);

                // 타인평가 배정 및 실시 인원 계산
                List<EvalAssign> assignments = evalAssignRepository.findByTested(emp.getEmployeeNumber());
                int totalAssignments = assignments.size(); // 타인평가 배정인원
                int completedAssignments = (int) assignments.stream()
                        .filter(EvalAssign::getIsCompleted)
                        .count(); // 타인평가 실시인원

                double completionRate = 0;
                boolean selfCompleted = emp.getCompletedSelf() != null && emp.getCompletedSelf();

                if (emp.getSelfYn() != null && emp.getSelfYn()) {
                    // 자가평가 대상일 경우
                    completionRate = ((selfCompleted ? 1 : 0) + completedAssignments) /
                            (double)(1 + totalAssignments) * 100;
                } else {
                    // 자가평가 대상이 아닐 경우
                    completionRate = totalAssignments > 0 ?
                            (completedAssignments / (double)totalAssignments * 100) : 0;
                }

                cellRate.setCellValue(String.format("%.1f%%", completionRate));

                // 9. 자가평가 문항 점수 (카테고리별)
                int totalSelfScore = 0;
                Map<Long, Integer> selfResponses = selfResponsesByEmployee.getOrDefault(emp.getEmployeeNumber(), Map.of());

                for (String category : selfCategories) {
                    List<Long> categoryQuestions = selfQuestionsByCategory.get(category);
                    for (Long questionId : categoryQuestions) {
                        Cell cellSelfQ = row.createCell(cellIndex++);
                        Integer score = selfResponses.get(questionId);
                        if (score != null) {
                            cellSelfQ.setCellValue(score);
                            totalSelfScore += score;
                        } else {
                            cellSelfQ.setCellValue("-");
                        }
                    }
                }

                // 10. 자가평가 총점
                Cell cellTotalSelfScore = row.createCell(cellIndex++);
                cellTotalSelfScore.setCellValue(totalSelfScore);

                // 11. 타인평가 문항 점수 (카테고리별)
                // 타인평가는 응답자들의 평균값 계산
                Map<Long, List<Integer>> othersResponseScores = getOthersResponseScores(
                        emp.getEmployeeNumber(),
                        surveyResponseRepository);

                for (String category : othersCategories) {
                    List<Long> categoryQuestions = othersQuestionsByCategory.get(category);
                    for (Long questionId : categoryQuestions) {
                        Cell cellOthersQ = row.createCell(cellIndex++);
                        List<Integer> scores = othersResponseScores.getOrDefault(questionId, Collections.emptyList());

                        if (!scores.isEmpty()) {
                            double avgScore = scores.stream()
                                    .mapToInt(Integer::intValue)
                                    .average()
                                    .orElse(0);
                            cellOthersQ.setCellValue(Math.round(avgScore * 100) / 100.0);
                        } else {
                            cellOthersQ.setCellValue("-");
                        }
                    }
                }

                // 12. 전체 백분위
                Cell cellOverallPercentile = row.createCell(cellIndex++);
                Double overallPercentile = calculateOverallPercentile(
                        emp.getEmployeeNumber(),
                        totalSelfScore,
                        othersResponseScores,
                        surveyResponseRepository);
                cellOverallPercentile.setCellValue(Math.round(overallPercentile * 100) / 100.0);

                // 13. 동일계급 백분위
                Cell cellSameRankPercentile = row.createCell(cellIndex++);
                Double sameRankPercentile = calculateSameRankPercentile(
                        emp.getEmployeeNumber(),
                        totalSelfScore,
                        othersResponseScores,
                        emp.getRepGradeName(),
                        surveyResponseRepository);
                cellSameRankPercentile.setCellValue(Math.round(sameRankPercentile * 100) / 100.0);

                // 14. 참여지정인원
                Cell cellDesignatedCount = row.createCell(cellIndex++);
                cellDesignatedCount.setCellValue(totalAssignments);

                // 15. 실시인원
                Cell cellCompletedCount = row.createCell(cellIndex++);
                cellCompletedCount.setCellValue(completedAssignments);

                // 16. 실시율
                Cell cellCompletionRate = row.createCell(cellIndex++);
                double assignmentCompletionRate = totalAssignments > 0 ?
                        (completedAssignments / (double)totalAssignments * 100) : 0;
                cellCompletionRate.setCellValue(String.format("%.1f%%", assignmentCompletionRate));
            }

            // 열 너비 자동 조정
            for (int i = 0; i < allHeaders.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 엑셀 파일을 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
    }

    // 문항을 카테고리별로 그룹화하는 메소드
    private static Map<String, List<Long>> groupQuestionsByCategory(
            List<Long> questionIds,
            SurveyResponseRepository surveyResponseRepository) {

        Map<String, List<Long>> questionsByCategory = new HashMap<>();

        for (Long questionId : questionIds) {
            Optional<SurveyResponse> response = surveyResponseRepository.findById(questionId);
            String category = response.map(SurveyResponse::getCategory)
                    .orElse("기타");

            questionsByCategory.computeIfAbsent(category, k -> new ArrayList<>())
                    .add(questionId);
        }

        // 각 카테고리 내에서 질문 ID 정렬
        for (List<Long> questions : questionsByCategory.values()) {
            Collections.sort(questions);
        }

        return questionsByCategory;
    }

    // 타인평가 응답 점수를 모으는 메소드
    private static Map<Long, List<Integer>> getOthersResponseScores(
            String employeeNumber,
            SurveyResponseRepository surveyResponseRepository) {

        List<SurveyResponse> responses = surveyResponseRepository.findByTestedNumber(employeeNumber);

        return responses.stream()
                .filter(r -> r.getEvaluationType() == SurveyResponse.EvaluationType.OTHERS)
                .filter(r -> r.getRespondentScore() != null)
                .collect(Collectors.groupingBy(
                        SurveyResponse::getQuestionId,
                        Collectors.mapping(SurveyResponse::getRespondentScore, Collectors.toList())
                ));
    }

    // 전체 백분위 계산 메소드
    private static double calculateOverallPercentile(
            String employeeNumber,
            int selfScore,
            Map<Long, List<Integer>> othersScores,
            SurveyResponseRepository surveyResponseRepository) {

        // 타인평가 평균점수 계산
        double othersAvgScore = othersScores.values().stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        // 자가평가 + 타인평가 총점
        double totalScore = selfScore + othersAvgScore;

        // 모든 직원의 총점 가져오기
        List<Object[]> allScores = surveyResponseRepository.findTotalScoresByPeriodId(null); // periodId는 적절하게 수정 필요

        // 동일한 방식으로 총점 변환
        Map<String, Double> employeeScores = new HashMap<>();
        for (Object[] score : allScores) {
            String empNum = (String) score[0];
            Double scoreValue = ((Number) score[1]).doubleValue();
            if (empNum != null) {
                employeeScores.put(empNum, scoreValue);
            }
        }

        // 백분위 계산
        long lowerScoreCount = employeeScores.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(employeeNumber))
                .filter(entry -> entry.getValue() < totalScore)
                .count();

        int totalEmployees = employeeScores.size();
        if (totalEmployees <= 1) {
            return 100.0;
        }

        return (double) (lowerScoreCount) / (totalEmployees - 1) * 100;
    }

    // 동일계급 백분위 계산 메소드
    private static double calculateSameRankPercentile(
            String employeeNumber,
            int selfScore,
            Map<Long, List<Integer>> othersScores,
            String rank,
            SurveyResponseRepository surveyResponseRepository) {

        // 타인평가 평균점수 계산
        double othersAvgScore = othersScores.values().stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        // 자가평가 + 타인평가 총점
        double totalScore = selfScore + othersAvgScore;

        // 동일 계급 직원의 총점 가져오기
        List<Object[]> sameRankScores = surveyResponseRepository.findTotalScoresByPeriodIdAndRank(null, rank); // periodId는 적절하게 수정 필요

        // 동일한 방식으로 총점 변환
        Map<String, Double> employeeScores = new HashMap<>();
        for (Object[] score : sameRankScores) {
            String empNum = (String) score[0];
            Double scoreValue = ((Number) score[1]).doubleValue();
            if (empNum != null) {
                employeeScores.put(empNum, scoreValue);
            }
        }

        // 백분위 계산
        long lowerScoreCount = employeeScores.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(employeeNumber))
                .filter(entry -> entry.getValue() < totalScore)
                .count();

        int totalEmployees = employeeScores.size();
        if (totalEmployees <= 1) {
            return 100.0;
        }

        return (double) (lowerScoreCount) / (totalEmployees - 1) * 100;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}