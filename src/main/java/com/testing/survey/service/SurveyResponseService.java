package com.testing.survey.service;

import com.testing.survey.dto.EmployeeTempDTO;
import com.testing.survey.dto.SurveyResponseDTO;
import com.testing.survey.dto.SurveySubmissionDTO;
import com.testing.survey.entity.SurveyResponse;
import com.testing.survey.repository.SurveyResponseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SurveyResponseService {
    private final SurveyResponseRepository surveyResponseRepository;
    private final EmployeeTempService employeeService;
    private final EvalAssignService evalAssignService;

    public void processSurveySubmission(SurveySubmissionDTO submission,
                                        String respondentNumber,
                                        Long periodId) {
        boolean isSelfEvaluation = "SELF".equals(submission.getEvaluationType());

        // 응답 저장
        for (SurveyResponseDTO response : submission.getResponses()) {
            if (response.getSelectedAnswer() != null) {  // 선택된 응답만 저장
                SurveyResponse surveyResponse = new SurveyResponse();
                surveyResponse.setPeriodId(periodId);
                surveyResponse.setQuestionId(response.getQuestionId());
                surveyResponse.setRespondentNumber(respondentNumber);
                surveyResponse.setSelectedAnswer(response.getSelectedAnswer());
                surveyResponse.setTextAnswer(response.getTextAnswer());
                surveyResponse.setEvaluationType(isSelfEvaluation ?
                        SurveyResponse.EvaluationType.SELF : SurveyResponse.EvaluationType.OTHERS);

                if (!isSelfEvaluation) {
                    surveyResponse.setTestedNumber(response.getTestedNumber());
                }

                surveyResponseRepository.save(surveyResponse);
            }
        }

        // 완료 상태 업데이트
        if (isSelfEvaluation) {
            EmployeeTempDTO employeeDTO = new EmployeeTempDTO();
            employeeDTO.setCompletedSelf(true);
            employeeService.updateEmployee(respondentNumber, employeeDTO);
        } else {
            // 타인평가 완료 처리
            updateOthersEvaluationStatus(respondentNumber,
                    submission.getResponses().get(0).getTestedNumber());
        }
    }

    private void updateOthersEvaluationStatus(String respondentNumber, String testedNumber) {
        // 해당 평가 완료 처리
        evalAssignService.updateCompletionStatus(respondentNumber, testedNumber, true);

        // 모든 할당된 평가가 완료되었는지 확인
        boolean allCompleted = evalAssignService.checkAllEvaluationsCompleted(respondentNumber);
        if (allCompleted) {
            EmployeeTempDTO employeeDTO = new EmployeeTempDTO();
            employeeDTO.setCompletedOthers(true);
            employeeService.updateEmployee(respondentNumber, employeeDTO);
        }
    }
}