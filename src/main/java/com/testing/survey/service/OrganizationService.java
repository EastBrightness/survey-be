package com.testing.survey.service;


import com.testing.survey.entity.eval.EvalEmployee;
import com.testing.survey.entity.eval.EvalOrganization;
import com.testing.survey.entity.org.Employee;
import com.testing.survey.entity.org.Organization;
import com.testing.survey.repository.EmployeeRepository;
import com.testing.survey.repository.EvalEmployeeRepository;
import com.testing.survey.repository.EvalOrganizationRepository;
import com.testing.survey.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Service class
@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final EvalOrganizationRepository evalOrganizationRepository;
    private final EvalEmployeeRepository evalEmployeeRepository;

    public List<Organization> getSubOrganizations(String upCode) {
        List<Organization> organizations = organizationRepository.findByUpCode(upCode);
        return organizations != null ? organizations : new ArrayList<>();
    }

    public void saveEvaluations(List<Long> organizationIds, String periodId) {
        for (Long orgId : organizationIds) {
            // Save or update organization evaluation
            EvalOrganization evalOrg = evalOrganizationRepository
                    .findByOrganizationIdAndPeriodId(orgId, periodId)
                    .orElse(new EvalOrganization());

            evalOrg.setOrganizationId(orgId);
            evalOrg.setPeriodId(periodId);
            evalOrganizationRepository.save(evalOrg);

            // Save or update employee evaluations
//            List<Employee> employees = employeeRepository.findByOrganizationId(orgId);
//            for (Employee emp : employees) {
//                EvalEmployee evalEmp = evalEmployeeRepository
//                        .findByPersonIdAndPeriodId(emp.getPersonId(), periodId)
//                        .orElse(new EvalEmployee());
//
//                evalEmp.setPersonId(emp.getPersonId());
//                evalEmp.setPeriodId(periodId);
//                evalEmployeeRepository.save(evalEmp);
//            }
        }
    }
}