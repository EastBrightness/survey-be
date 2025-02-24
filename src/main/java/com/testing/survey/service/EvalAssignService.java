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

    @Transactional(readOnly = true)
    public List<EvalAssignDTO> getAssignmentsByTested(String tested) {
        List<EvalAssign> assignments = evalAssignRepository.findByTested(tested);
        return assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAssignments(String tested) {
        evalAssignRepository.deleteByTested(tested);
    }

    private EvalAssignDTO convertToDTO(EvalAssign evalAssign) {
        EvalAssignDTO dto = new EvalAssignDTO();
        dto.setId(evalAssign.getId());
        dto.setTested(evalAssign.getTested());
        dto.setTester(evalAssign.getTester());
        dto.setIsCompleted(evalAssign.getIsCompleted());
        return dto;
    }


    // 추가: 평가 완료 상태 업데이트
    @Transactional
    public void updateCompletionStatus(String tester, String tested, boolean completed) {
        EvalAssign assignment = evalAssignRepository.findByTesterAndTested(tester, tested)
                .orElseThrow(() -> new RuntimeException("평가 할당 정보를 찾을 수 없습니다."));
        assignment.setIsCompleted(completed);
        evalAssignRepository.save(assignment);
    }

    // 추가: 평가자의 모든 평가가 완료되었는지 확인
    public boolean checkAllEvaluationsCompleted(String tester) {
        List<EvalAssign> assignments = evalAssignRepository.findByTester(tester);
        return !assignments.isEmpty() &&
                assignments.stream().allMatch(EvalAssign::getIsCompleted);
    }


    @Transactional(readOnly = true)
    public List<EvalAssignDTO> getAssignmentsByTester(String tester) {
        List<EvalAssign> assignments = evalAssignRepository.findByTester(tester);
        return assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAssignmentStatus(String tester, String tested, boolean isCompleted) {
        evalAssignRepository.findByTesterAndTested(tester, tested)
                .ifPresent(assignment -> {
                    assignment.setIsCompleted(isCompleted);
                    evalAssignRepository.save(assignment);
                });
    }

}