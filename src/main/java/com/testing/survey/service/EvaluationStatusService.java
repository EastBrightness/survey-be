package com.testing.survey.service;

import com.testing.survey.dto.status.DepartmentStatusDTO;
import com.testing.survey.dto.status.EvaluationDetailDTO;
import com.testing.survey.dto.status.GroupStatusDTO;
import com.testing.survey.dto.status.PersonStatusDTO;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.entity.temp.EvalAssign;
import com.testing.survey.repository.EmployeeTempRepository;
import com.testing.survey.repository.EvalAssignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationStatusService {
    private final EmployeeTempRepository employeeRepository;
    private final EvalAssignRepository evalAssignRepository;

    public DepartmentStatusDTO getDepartmentStatus(String departmentName) {
        // Get all organization names that start with the departmentName (including the department itself)
        List<String> allOrganizations = employeeRepository.findDistinctOrganizationsByDepartment(departmentName);

        // Get all employees in these organizations
        List<EmployeeTemp> allEmployees = employeeRepository.findByOrganizationNameInAndIsDeletedFalse(allOrganizations);

        // Count statistics
        long totalSelfCount = allEmployees.stream().filter(e -> e.getSelfYn()).count();
        long completedSelfCount = allEmployees.stream().filter(e -> e.getCompletedSelf()).count();

        long totalOthersCount = allEmployees.stream().filter(e -> e.getOthersTester()).count();
        long completedOthersCount = allEmployees.stream().filter(e -> e.getCompletedOthers()).count();

        return DepartmentStatusDTO.builder()
                .departmentName(departmentName)
                .selfEvaluationRate(calculateRate(completedSelfCount, totalSelfCount))
                .selfEvaluationRemainRate(calculateRate(totalSelfCount - completedSelfCount, totalSelfCount))
                .otherEvaluationRate(calculateRate(completedOthersCount, totalOthersCount))
                .otherEvaluationRemainRate(calculateRate(totalOthersCount - completedOthersCount, totalOthersCount))
                .totalSelfCount(totalSelfCount)
                .completedSelfCount(completedSelfCount)
                .remainingSelfCount(totalSelfCount - completedSelfCount)
                .totalOthersCount(totalOthersCount)
                .completedOthersCount(completedOthersCount)
                .remainingOthersCount(totalOthersCount - completedOthersCount)
                .build();
    }

    public List<GroupStatusDTO> getGroupStatus(String departmentName) {
        List<String> groups = employeeRepository.findDistinctOrganizationsByDepartment(departmentName);

        return groups.stream().map(group -> {
            List<EmployeeTemp> groupEmployees = employeeRepository.findByOrganizationNameAndIsDeletedFalse(group);

            long completedSelfCount = groupEmployees.stream().filter(e -> e.getCompletedSelf()).count();
            long completedOthersCount = groupEmployees.stream().filter(e -> e.getCompletedOthers()).count();

            return GroupStatusDTO.builder()
                    .groupName(group)
                    .selfEvaluationRate(calculateRate(completedSelfCount, groupEmployees.size()))
                    .otherEvaluationRate(calculateRate(completedOthersCount, groupEmployees.size()))
                    .build();
        }).collect(Collectors.toList());
    }

    public List<PersonStatusDTO> getPersonStatus(String organizationName) {
        List<EmployeeTemp> employees = employeeRepository.findByOrganizationNameAndIsDeletedFalse(organizationName);

        return employees.stream().map(emp -> {
            List<EvalAssign> assignments = evalAssignRepository.findByTester(emp.getEmployeeNumber());
            long completedAssignments = assignments.stream().filter(EvalAssign::getIsCompleted).count();

            return PersonStatusDTO.builder()
                    .employeeNumber(emp.getEmployeeNumber())
                    .personName(emp.getPersonName())
                    .organizationName(emp.getOrganizationName())
                    .jobName(emp.getJobName())
                    .gradeName(emp.getGradeName())
                    .selfEvaluationTarget(emp.getSelfYn())
                    .otherEvaluationTarget(emp.getOthersTested())
                    .completedSelf(emp.getCompletedSelf())
                    .otherEvaluationRate(calculateRate(completedAssignments, assignments.size()))
                    .build();
        }).collect(Collectors.toList());
    }

    public List<EvaluationDetailDTO> getEvaluationDetail(String testerNumber) {
        List<EvalAssign> assignments = evalAssignRepository.findByTester(testerNumber);

        return assignments.stream().map(assignment -> {
            EmployeeTemp tested = employeeRepository.findByEmployeeNumber(assignment.getTested())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            return EvaluationDetailDTO.builder()
                    .testedNumber(tested.getEmployeeNumber())
                    .testedName(tested.getPersonName())
                    .organization(tested.getOrganizationName())
                    .position(tested.getJobName())
                    .grade(tested.getGradeName())
                    .isCompleted(assignment.getIsCompleted())
                    .build();
        }).collect(Collectors.toList());
    }

    private double calculateRate(long completed, long total) {
        return total == 0 ? 0.0 : (completed * 100.0) / total;
    }
}