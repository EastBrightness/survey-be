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

    // 자기 자신에 대한 자가평가 응답 조회
    List<SurveyResponse> findByPeriodIdAndRespondentNumberAndEvaluationType(
            Long periodId, String respondentNumber, SurveyResponse.EvaluationType evaluationType);

    // 타인에 의한 평가 응답 조회 (자신이 평가를 받은 경우)
    List<SurveyResponse> findByPeriodIdAndTestedNumberAndEvaluationType(
            Long periodId, String testedNumber, SurveyResponse.EvaluationType evaluationType);

    // 특정 직원이 평가받은 모든 응답 조회 (추가 메소드)
    List<SurveyResponse> findByTestedNumber(String testedNumber);

    // 전체 직원의 평가 총점 조회 (백분위 계산용)
    @Query("SELECT sr.testedNumber, SUM(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId " +
            "GROUP BY sr.testedNumber")
    List<Object[]> findTotalScoresByPeriodId(@Param("periodId") Long periodId);

    // 같은 계급 직원들의 평가 총점 조회 (백분위 계산용)
    @Query("SELECT sr.testedNumber, SUM(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId AND sr.testedRank = :rank " +
            "GROUP BY sr.testedNumber")
    List<Object[]> findTotalScoresByPeriodIdAndRank(
            @Param("periodId") Long periodId, @Param("rank") String rank);

    // 텍스트 피드백 조회
    @Query("SELECT DISTINCT sr.textAnswer FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId AND sr.testedNumber = :testedNumber " +
            "AND sr.textAnswer IS NOT NULL AND sr.textAnswer <> ''")
    List<String> findDistinctTextAnswersByPeriodIdAndTestedNumber(
            @Param("periodId") Long periodId, @Param("testedNumber") String testedNumber);

    // 통계 조회용 메소드
    @Query("SELECT sr.questionId, sr.evaluationType, AVG(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId " +
            "AND (sr.evaluationType = 'SELF' OR sr.evaluationType = 'OTHERS') " +
            "GROUP BY sr.questionId, sr.evaluationType " +
            "ORDER BY sr.questionId, sr.evaluationType")
    List<Object[]> calculateQuestionStatisticsBothTypes(@Param("periodId") Long periodId);

    @Query("SELECT sr FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId " +
            "AND (sr.evaluationType = 'SELF' OR sr.evaluationType = 'OTHERS') " +
            "ORDER BY sr.questionId")
    List<SurveyResponse> findAllByPeriodId(@Param("periodId") Long periodId);

    // 문항 카테고리별 응답 통계 조회
    @Query("SELECT sr.category, sr.evaluationType, AVG(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId " +
            "AND (sr.evaluationType = 'SELF' OR sr.evaluationType = 'OTHERS') " +
            "GROUP BY sr.category, sr.evaluationType " +
            "ORDER BY sr.category, sr.evaluationType")
    List<Object[]> calculateCategoryStatistics(@Param("periodId") Long periodId);
}