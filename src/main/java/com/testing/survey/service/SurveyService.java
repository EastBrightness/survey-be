package com.testing.survey.service;

import com.testing.survey.entity.Answer;
import com.testing.survey.dto.AnswerDTO;
import com.testing.survey.entity.Question;
import com.testing.survey.dto.QuestionDTO;
import com.testing.survey.repository.AnswerRepository;
import com.testing.survey.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Service 클래스
@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionDTO createQuestion(QuestionDTO dto) {
        Question question = new Question();
        question.setPeriodId(dto.getPeriodId());
        question.setCategory(dto.getCategory());
        question.setContent(dto.getContent());
        question.setTargetYn(dto.getTargetYn());

        Question savedQuestion = questionRepository.save(question);

        if (dto.getAnswers() != null) {
            Answer answer = new Answer();
            answer.setPeriodId(dto.getPeriodId());
            answer.setQuestionId(savedQuestion.getId());
            answer.setAnswer1(dto.getAnswers().getAnswer1());
            answer.setAnswer2(dto.getAnswers().getAnswer2());
            answer.setAnswer3(dto.getAnswers().getAnswer3());
            answer.setAnswer4(dto.getAnswers().getAnswer4());
            answer.setAnswer5(dto.getAnswers().getAnswer5());

            answerRepository.save(answer);
        }

        return convertToDTO(savedQuestion);
    }

    public QuestionDTO updateQuestion(Long id, QuestionDTO dto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("com.testing.survey.entity.Question not found"));

        question.setCategory(dto.getCategory());
        question.setContent(dto.getContent());
        question.setTargetYn(dto.getTargetYn());

        Question updatedQuestion = questionRepository.save(question);

        if (dto.getAnswers() != null) {
            Answer answer = answerRepository.findByPeriodIdAndQuestionId(dto.getPeriodId(), id)
                    .orElseGet(Answer::new);

            answer.setPeriodId(dto.getPeriodId());
            answer.setQuestionId(id);
            answer.setAnswer1(dto.getAnswers().getAnswer1());
            answer.setAnswer2(dto.getAnswers().getAnswer2());
            answer.setAnswer3(dto.getAnswers().getAnswer3());
            answer.setAnswer4(dto.getAnswers().getAnswer4());
            answer.setAnswer5(dto.getAnswers().getAnswer5());

            answerRepository.save(answer);
        }

        return convertToDTO(updatedQuestion);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public List<QuestionDTO> getQuestions(Long periodId, Boolean targetYn) {
        List<Question> questions = questionRepository.findByPeriodIdAndTargetYnOrderById(periodId, targetYn);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setPeriodId(question.getPeriodId());
        dto.setCategory(question.getCategory());
        dto.setContent(question.getContent());
        dto.setTargetYn(question.getTargetYn());

        answerRepository.findByPeriodIdAndQuestionId(question.getPeriodId(), question.getId())
                .ifPresent(answer -> {
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
                });

        return dto;
    }
}
