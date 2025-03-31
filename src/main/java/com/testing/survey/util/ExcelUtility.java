package com.testing.survey.util;


import com.testing.survey.dto.statistics.StatisticsRequestDTO;
import com.testing.survey.entity.temp.EmployeeTemp;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class ExcelUtility {
    public static byte[] generateEvaluationStatisticsExcel(
            List<EmployeeTemp> employees,
            List<Integer> selfQuestionIds,
            List<Integer> othersQuestionIds,
            Map<String, Map<Integer, Integer>> selfResponsesByEmployee,
            Map<String, Map<Integer, Integer>> othersResponsesByEmployee,
            Map<String, Map<String, Double>> questionStats,
            StatisticsRequestDTO request
    ) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("평가 통계");

            // 스타일 설정
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // 헤더 생성
            String[] baseHeaders = {
                    "순번", "소속", "신분", "통계계급", "성명", "군번", "성별", "종합실시율"
            };

            // 자가평가 문항 헤더 생성
            String[] selfHeaders = new String[selfQuestionIds.size()];
            for (int i = 0; i < selfQuestionIds.size(); i++) {
                selfHeaders[i] = "자가평가 문항" + (i+1);
            }

            // 타인평가 문항 헤더 생성
            String[] othersHeaders = new String[othersQuestionIds.size() + 3]; // +3 for 참여지정인원, 실시인원, 실시율
            for (int i = 0; i < othersQuestionIds.size(); i++) {
                othersHeaders[i] = "타인평가 문항" + (i+1);
            }
            othersHeaders[othersQuestionIds.size()] = "참여지정인원";
            othersHeaders[othersQuestionIds.size() + 1] = "실시인원";
            othersHeaders[othersQuestionIds.size() + 2] = "실시율";

            // 전체 헤더 배열 생성
            String[] allHeaders = new String[baseHeaders.length + selfHeaders.length + othersHeaders.length];
            System.arraycopy(baseHeaders, 0, allHeaders, 0, baseHeaders.length);
            System.arraycopy(selfHeaders, 0, allHeaders, baseHeaders.length, selfHeaders.length);
            System.arraycopy(othersHeaders, 0, allHeaders, baseHeaders.length + selfHeaders.length, othersHeaders.length);

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < allHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(allHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            for (int i = 0; i < employees.size(); i++) {
                EmployeeTemp emp = employees.get(i);
                Row row = sheet.createRow(i + 1);

                // 기본 정보 설정
                int cellIndex = 0;

                // 순번
                Cell cellNum = row.createCell(cellIndex++);
                cellNum.setCellValue(i + 1);
                cellNum.setCellStyle(dataStyle);

                // 소속
                Cell cellOrg = row.createCell(cellIndex++);
                cellOrg.setCellValue(emp.getOrganizationName());

                // 신분
                Cell cellType = row.createCell(cellIndex++);
                cellType.setCellValue(emp.getPersonTypeName());

                // 통계계급
                Cell cellGrade = row.createCell(cellIndex++);
                cellGrade.setCellValue(emp.getRepGradeName());

                // 성명
                Cell cellName = row.createCell(cellIndex++);
                cellName.setCellValue(emp.getPersonName());

                // 군번
                Cell cellEmpNum = row.createCell(cellIndex++);
                cellEmpNum.setCellValue(emp.getEmployeeNumber());

                // 성별
                Cell cellSex = row.createCell(cellIndex++);
                cellSex.setCellValue("M".equals(emp.getSex()) ? "남성" : "여성");

                // 종합실시율
                Cell cellRate = row.createCell(cellIndex++);
                boolean selfCompleted = emp.getCompletedSelf() != null && emp.getCompletedSelf();
                boolean othersCompleted = emp.getCompletedOthers() != null && emp.getCompletedOthers();
                double completionRate = 0;

                if ((emp.getSelfYn() != null && emp.getSelfYn()) &&
                        (emp.getOthersTested() != null && emp.getOthersTested())) {
                    // 자가평가와 타인평가 모두 대상
                    completionRate = ((selfCompleted ? 1 : 0) + (othersCompleted ? 1 : 0)) / 2.0 * 100;
                } else if (emp.getSelfYn() != null && emp.getSelfYn()) {
                    // 자가평가만 대상
                    completionRate = (selfCompleted ? 100 : 0);
                } else if (emp.getOthersTested() != null && emp.getOthersTested()) {
                    // 타인평가만 대상
                    completionRate = (othersCompleted ? 100 : 0);
                }

                cellRate.setCellValue(String.format("%.1f%%", completionRate));

                // 자가평가 문항 점수
                Map<Integer, Integer> selfResponses = selfResponsesByEmployee.getOrDefault(emp.getEmployeeNumber(), Map.of());
                for (int j = 0; j < selfQuestionIds.size(); j++) {
                    Cell cellSelfQ = row.createCell(cellIndex++);
                    Integer questionId = selfQuestionIds.get(j);
                    Integer score = selfResponses.get(questionId);
                    if (score != null) {
                        cellSelfQ.setCellValue(score);
                    } else {
                        cellSelfQ.setCellValue("-");
                    }
                }

                // 타인평가 문항 점수
                Map<Integer, Integer> othersResponses = othersResponsesByEmployee.getOrDefault(emp.getEmployeeNumber(), Map.of());
                for (int j = 0; j < othersQuestionIds.size(); j++) {
                    Cell cellOthersQ = row.createCell(cellIndex++);
                    Integer questionId = othersQuestionIds.get(j);
                    Integer score = othersResponses.get(questionId);
                    if (score != null) {
                        cellOthersQ.setCellValue(score);
                    } else {
                        cellOthersQ.setCellValue("-");
                    }
                }

                // 참여지정인원
                Cell cellDesignated = row.createCell(cellIndex++);
                cellDesignated.setCellValue(emp.getOthersTester() != null && emp.getOthersTester() ? "참여" : "미참여");

                // 실시인원
                Cell cellCompleted = row.createCell(cellIndex++);
                cellCompleted.setCellValue(emp.getCompletedOthers() != null && emp.getCompletedOthers() ? "완료" : "미완료");

                // 실시율
                Cell cellOthersRate = row.createCell(cellIndex++);
                if (emp.getOthersTester() != null && emp.getOthersTester()) {
                    cellOthersRate.setCellValue(emp.getCompletedOthers() != null && emp.getCompletedOthers() ? "100%" : "0%");
                } else {
                    cellOthersRate.setCellValue("-");
                }
            }

            // 열 너비 자동 조정
            for (int i = 0; i < allHeaders.length; i++) {
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