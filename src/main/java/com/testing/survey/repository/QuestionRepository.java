package com.testing.survey.repository;

import com.testing.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository 인터페이스
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByPeriodIdAndTargetYnOrderById(Long periodId, Boolean targetYn);
}
