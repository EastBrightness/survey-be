package com.testing.survey.repository;

import com.testing.survey.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByPeriodIdAndQuestionId(Long periodId, Long questionId);
}
