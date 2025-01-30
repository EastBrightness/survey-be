package com.testing.survey.service;

import com.testing.survey.dto.OrganizationTempDTO;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.entity.temp.OrganizationTemp;
import com.testing.survey.repository.EmployeeTempRepository;
import com.testing.survey.repository.OrganizationTempRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationTempService {
    private final OrganizationTempRepository organizationTempRepository;
    private final EmployeeTempRepository employeeTempRepository;
    private final EvalAssignService evalAssignService;

    public List<OrganizationTempDTO> getOrganizationTree(String rootOCode) {
        List<OrganizationTempDTO> result = new ArrayList<>();
        buildOrgTree(rootOCode, result);
        return result;
    }

    @Transactional
    public void toggleOrganizationStatus(String oCode, boolean isDeleted) {
        OrganizationTemp org = organizationTempRepository.findByoCodeAndIsDeletedFalse(oCode)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        org.setIsDeleted(isDeleted);
        organizationTempRepository.save(org);
    }

    @Transactional
    public void deleteOrganizations(List<String> organizationNames) {
        // 조직들의 isDeleted를 true로 설정
        List<OrganizationTemp> organizations = organizationTempRepository
                .findByFullNameIn(organizationNames);

        organizations.forEach(org -> org.setIsDeleted(true));
        organizationTempRepository.saveAll(organizations);

        // 해당 조직의 직원들도 isDeleted를 true로 설정
        List<EmployeeTemp> employees = employeeTempRepository
                .findByOrganizationNameIn(organizationNames);

        employees.forEach(emp -> emp.setIsDeleted(true));
        employeeTempRepository.saveAll(employees);
    }


    private void buildOrgTree(String currentOCode, List<OrganizationTempDTO> result) {
        List<OrganizationTemp> children = organizationTempRepository.findByUpCodeAndIsDeletedFalse(currentOCode);

        for (OrganizationTemp org : children) {
            OrganizationTempDTO dto = convertToDTO(org);
            List<EmployeeTemp> employees = employeeTempRepository.findEligibleEmployees(org.getFullName());
            dto.setEmployeeCount(employees.size());
            result.add(dto);
            buildOrgTree(org.getOCode(), result);
        }
    }

    private OrganizationTempDTO convertToDTO(OrganizationTemp entity) {
        OrganizationTempDTO dto = new OrganizationTempDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}