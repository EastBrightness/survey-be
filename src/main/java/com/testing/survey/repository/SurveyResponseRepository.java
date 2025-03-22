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

    // Find self-evaluation responses for a specific employee in a period
    List<SurveyResponse> findByPeriodIdAndRespondentNumberAndEvaluationType(
            Long periodId, String respondentNumber, SurveyResponse.EvaluationType evaluationType);

    // Find responses where the employee is being evaluated by others
    List<SurveyResponse> findByPeriodIdAndTestedNumberAndEvaluationType(
            Long periodId, String testedNumber, SurveyResponse.EvaluationType evaluationType);

    // Get all evaluation scores for all employees for percentile calculation
    @Query("SELECT sr.testedNumber, SUM(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId " +
            "GROUP BY sr.testedNumber")
    List<Object[]> findTotalScoresByPeriodId(@Param("periodId") Long periodId);

    // Get all evaluation scores for employees with the same rank
    @Query("SELECT sr.testedNumber, SUM(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId AND sr.testedRank = :rank " +
            "GROUP BY sr.testedNumber")
    List<Object[]> findTotalScoresByPeriodIdAndRank(
            @Param("periodId") Long periodId, @Param("rank") String rank);

    // Get text feedback for an employee
    @Query("SELECT DISTINCT sr.textAnswer FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId AND sr.testedNumber = :testedNumber " +
            "AND sr.textAnswer IS NOT NULL AND sr.textAnswer <> ''")
    List<String> findDistinctTextAnswersByPeriodIdAndTestedNumber(
            @Param("periodId") Long periodId, @Param("testedNumber") String testedNumber);

}