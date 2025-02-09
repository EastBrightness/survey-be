package com.testing.survey.service;

import com.testing.survey.dto.EmployeeTempDTO;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.entity.temp.EvalAssign;
import com.testing.survey.repository.EmployeeTempRepository;
import com.testing.survey.repository.EvalAssignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmployeeTempService {
    private final EmployeeTempRepository employeeTempRepository;
    private final EvalAssignRepository evalAssignRepository;

    public List<EmployeeTempDTO> getEmployeesByOrganization(String orgName) {
        return employeeTempRepository.findByOrganizationNameAndIsDeletedFalse(orgName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateEmployee(String employeeNumber, EmployeeTempDTO dto) {
        EmployeeTemp employee = employeeTempRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // null이 아닌 필드만 업데이트
        if (dto.getPersonName() != null) employee.setPersonName(dto.getPersonName());
        if (dto.getType() != null) employee.setType(dto.getType());
        if (dto.getOrganizationName() != null) employee.setOrganizationName(dto.getOrganizationName());
        if (dto.getGradeName() != null) employee.setGradeName(dto.getGradeName());
        if (dto.getRepGradeName() != null) employee.setRepGradeName(dto.getRepGradeName());
        if (dto.getJobName() != null) employee.setJobName(dto.getJobName());
        if (dto.getJobDate() != null) employee.setJobDate(dto.getJobDate());
        if (dto.getIsDeleted() != null) employee.setIsDeleted(dto.getIsDeleted());
        if (dto.getSelfYn() != null) employee.setSelfYn(dto.getSelfYn());
        if (dto.getOthersTested() != null) employee.setOthersTested(dto.getOthersTested());
        if (dto.getOthersTester() != null) employee.setOthersTester(dto.getOthersTester());
        if (dto.getCompletedSelf() != null) employee.setCompletedSelf(dto.getCompletedSelf());
        if (dto.getCompletedOthers() != null) employee.setCompletedOthers(dto.getCompletedOthers());

        employeeTempRepository.save(employee);
    }

    @Transactional
    public void assignRandomEvaluators() {
        // 기존 배정 초기화
        evalAssignRepository.deleteAll();

        // 활성 상태의 평가자만 가져오기
        List<EmployeeTemp> activeTesters = employeeTempRepository.findByOthersTesterTrueAndIsDeletedFalse();

        // 부서별로 평가자 그룹화 (null 조직은 제외)
        Map<String, List<EmployeeTemp>> testersByOrg = activeTesters.stream()
                .filter(e -> e.getOrganizationName() != null)
                .collect(Collectors.groupingBy(EmployeeTemp::getOrganizationName));

        // 활성 상태의 평가 대상자만 가져오기
        List<EmployeeTemp> activeTestTargets = employeeTempRepository.findByOthersTestedTrueAndIsDeletedFalse();

        for (EmployeeTemp target : activeTestTargets) {
            // 대상자의 조직이 null이면 건너뛰기
            if (target.getOrganizationName() == null) continue;

            // 같은 부서의 평가자 목록 가져오기
            List<EmployeeTemp> sameOrgTesters = testersByOrg
                    .getOrDefault(target.getOrganizationName(), new ArrayList<>())
                    .stream()
                    .filter(t -> !t.getEmployeeNumber().equals(target.getEmployeeNumber()))
                    .collect(Collectors.toList());

            if (!sameOrgTesters.isEmpty()) {
                // 평가자 수의 절반을 무작위로 선택 (최소 1명)
                int numberOfTesters = Math.max(1, sameOrgTesters.size() / 2);
                Collections.shuffle(sameOrgTesters);

                for (int i = 0; i < numberOfTesters && i < sameOrgTesters.size(); i++) {
                    EvalAssign assignment = new EvalAssign();
                    assignment.setTested(target.getEmployeeNumber());
                    assignment.setTester(sameOrgTesters.get(i).getEmployeeNumber());
                    evalAssignRepository.save(assignment);
                }
            }
        }
    }

    @Transactional
    public void deleteOrganizationEmployees(List<String> organizationNames) {
        List<EmployeeTemp> employees = employeeTempRepository.findByOrganizationNameIn(organizationNames);
        employees.forEach(emp -> emp.setIsDeleted(true));
        employeeTempRepository.saveAll(employees);
    }

    private EmployeeTempDTO convertToDTO(EmployeeTemp employee) {
        EmployeeTempDTO dto = new EmployeeTempDTO();
        BeanUtils.copyProperties(employee, dto);

        // 근무 기간 계산
        if (employee.getJobDate() != null) {
            long months = ChronoUnit.MONTHS.between(
                    employee.getJobDate(),
                    LocalDateTime.now()
            );
            dto.setWorkingMonths(months);
        }

        return dto;
    }

    public List<EmployeeTemp> searchEmployeesByName(String name) {
        return employeeTempRepository.findByPersonNameContainingOrderByOrganizationNameAsc(name);
    }

    @Transactional
    public void updateEmployee(String employeeNumber, EmployeeTemp updatedEmployee) {
        EmployeeTemp employee = employeeTempRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Update only allowed fields
        employee.setOrganizationName(updatedEmployee.getOrganizationName());
        employee.setIsDeleted(updatedEmployee.getIsDeleted());
        employee.setOthersTested(updatedEmployee.getOthersTested());
        employee.setOthersTester(updatedEmployee.getOthersTester());

        employeeTempRepository.save(employee);
    }


}