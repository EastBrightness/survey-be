package com.testing.survey.service;

import com.testing.survey.dto.AnswerDTO;
import com.testing.survey.dto.QuestionDTO;
import com.testing.survey.entity.Answer;
import com.testing.survey.entity.Question;
import com.testing.survey.repository.AnswerRepository;
import com.testing.survey.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public List<QuestionDTO> getQuestionsByPeriodAndType(Long periodId, boolean isSelfEvaluation) {
        // periodId와 평가 유형(자가/타인)에 따른 문항 조회
        List<Question> questions = questionRepository.findByPeriodIdAndTargetYn(
                periodId,
                !isSelfEvaluation  // targetYn이 true면 타인평가 문항
        );

        return questions.stream()
                .map(question -> {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setId(question.getId());
                    dto.setPeriodId(question.getPeriodId());
                    dto.setCategory(question.getCategory());
                    dto.setContent(question.getContent());
                    dto.setTargetYn(question.getTargetYn());

                    // 해당 문항의 답변 옵션 조회
                    Answer answer = answerRepository.findByPeriodIdAndQuestionId(
                            periodId,
                            question.getId()
                    ).orElse(null);

                    if (answer != null) {
                        AnswerDTO answerDTO = new AnswerDTO();
                        answerDTO.setId(answer.getId());
                        answerDTO.setPeriodId(answer.getPeriodId());
                        answerDTO.setQuestionId(answer.getQuestionId());
                        answerDTO.setAnswer1(answer.getAnswer1());
                        answerDTO.setAnswer2(answer.getAnswer2());
                        answerDTO.setAnswer3(answer.getAnswer3());
                        answerDTO.setAnswer4(answer.getAnswer4());
                        answerDTO.setAnswer5(answer.getAnswer5());
                        dto.setAnswers(answerDTO);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
