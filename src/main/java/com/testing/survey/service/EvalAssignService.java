package com.testing.survey.service;

import com.testing.survey.dto.EvalAssignDTO;
import com.testing.survey.entity.temp.EvalAssign;
import com.testing.survey.repository.EmployeeTempRepository;
import com.testing.survey.repository.EvalAssignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvalAssignService {
    private final EvalAssignRepository evalAssignRepository;
    private final EmployeeTempRepository employeeTempRepository;

    public List<EvalAssignDTO> getAssignmentsByTested(String tested) {
        return evalAssignRepository.findByTested(tested).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAssignments(String tested) {
        evalAssignRepository.deleteByTested(tested);
    }

    private EvalAssignDTO convertToDTO(EvalAssign entity) {
        EvalAssignDTO dto = new EvalAssignDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}