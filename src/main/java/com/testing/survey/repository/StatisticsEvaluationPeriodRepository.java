package com.testing.survey.repository;


import com.testing.survey.entity.eval.EvaluationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsEvaluationPeriodRepository extends JpaRepository<EvaluationPeriod, Long> {

    @Query("SELECT DISTINCT ep.standardYear FROM EvaluationPeriod ep WHERE ep.isDeleted = false")
    List<String> findDistinctYears();

    @Query("SELECT ep FROM EvaluationPeriod ep " +
            "WHERE ep.standardYear = :year AND ep.isDeleted = false")
    List<EvaluationPeriod> findEvaluationsByYear(String year);
}