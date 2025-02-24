package com.testing.survey.repository;

import com.testing.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    List<SurveyResponse> findByRespondentNumberAndPeriodId(String respondentNumber, Long periodId);

    @Query("SELECT CASE WHEN COUNT(sr) > 0 THEN true ELSE false END FROM SurveyResponse sr " +
            "WHERE sr.respondentNumber = :respondentNumber AND sr.testedNumber = :testedNumber " +
            "AND sr.periodId = :periodId")
    boolean hasCompletedEvaluation(@Param("respondentNumber") String respondentNumber,
                                   @Param("testedNumber") String testedNumber,
                                   @Param("periodId") Long periodId);
}