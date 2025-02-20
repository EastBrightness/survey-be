package com.testing.survey.service;

import com.testing.survey.dto.status.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportDepartmentStatus(DepartmentStatusDTO data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("부서 현황");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("부서명");
            headerRow.createCell(1).setCellValue("자가평가 실시율(%)");
            headerRow.createCell(2).setCellValue("자가평가 미실시율(%)");
            headerRow.createCell(3).setCellValue("타인평가 실시율(%)");
            headerRow.createCell(4).setCellValue("타인평가 미실시율(%)");

            // 데이터 입력
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(data.getDepartmentName());
            dataRow.createCell(1).setCellValue(data.getSelfEvaluationRate());
            dataRow.createCell(2).setCellValue(data.getSelfEvaluationRemainRate());
            dataRow.createCell(3).setCellValue(data.getOtherEvaluationRate());
            dataRow.createCell(4).setCellValue(data.getOtherEvaluationRemainRate());

            return writeWorkbookToByteArray(workbook);
        }
    }

    public byte[] exportGroupStatus(List<GroupStatusDTO> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("그룹 현황");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("그룹명");
            headerRow.createCell(1).setCellValue("자가평가 실시율(%)");
            headerRow.createCell(2).setCellValue("타인평가 실시율(%)");

            // 데이터 입력
            int rowNum = 1;
            for (GroupStatusDTO group : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(group.getGroupName());
                row.createCell(1).setCellValue(group.getSelfEvaluationRate());
                row.createCell(2).setCellValue(group.getOtherEvaluationRate());
            }

            return writeWorkbookToByteArray(workbook);
        }
    }

    public byte[] exportPersonStatus(List<PersonStatusDTO> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("개인 현황");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("사번");
            headerRow.createCell(1).setCellValue("이름");
            headerRow.createCell(2).setCellValue("소속");
            headerRow.createCell(3).setCellValue("직책");
            headerRow.createCell(4).setCellValue("계급");
            headerRow.createCell(5).setCellValue("자가평가 대상");
            headerRow.createCell(6).setCellValue("타인평가 대상");
            headerRow.createCell(7).setCellValue("자가평가 완료");
            headerRow.createCell(8).setCellValue("타인평가 실시율(%)");

            // 데이터 입력
            int rowNum = 1;
            for (PersonStatusDTO person : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(person.getEmployeeNumber());
                row.createCell(1).setCellValue(person.getPersonName());
                row.createCell(2).setCellValue(person.getOrganizationName());
                row.createCell(3).setCellValue(person.getJobName());
                row.createCell(4).setCellValue(person.getGradeName());
                row.createCell(5).setCellValue(person.getSelfEvaluationTarget() ? "예" : "아니오");
                row.createCell(6).setCellValue(person.getOtherEvaluationTarget() ? "예" : "아니오");
                row.createCell(7).setCellValue(person.getCompletedSelf() ? "완료" : "미완료");
                row.createCell(8).setCellValue(person.getOtherEvaluationRate());
            }

            return writeWorkbookToByteArray(workbook);
        }
    }

    public byte[] exportEvaluationDetail(List<EvaluationDetailDTO> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("평가 상세");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("평가대상자 사번");
            headerRow.createCell(1).setCellValue("평가대상자명");
            headerRow.createCell(2).setCellValue("소속");
            headerRow.createCell(3).setCellValue("직책");
            headerRow.createCell(4).setCellValue("계급");
            headerRow.createCell(5).setCellValue("평가상태");

            // 데이터 입력
            int rowNum = 1;
            for (EvaluationDetailDTO detail : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(detail.getTestedNumber());
                row.createCell(1).setCellValue(detail.getTestedName());
                row.createCell(2).setCellValue(detail.getOrganization());
                row.createCell(3).setCellValue(detail.getPosition());
                row.createCell(4).setCellValue(detail.getGrade());
                row.createCell(5).setCellValue(detail.getIsCompleted() ? "완료" : "미완료");
            }

            return writeWorkbookToByteArray(workbook);
        }
    }

    private byte[] writeWorkbookToByteArray(Workbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        return bos.toByteArray();
    }
}