package com.testing.survey.repository;

import com.testing.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsSurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    @Query("SELECT sr.questionId, sr.evaluationType, AVG(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId " +
            "AND (sr.evaluationType = 'SELF' OR sr.evaluationType = 'OTHERS') " +
            "GROUP BY sr.questionId, sr.evaluationType " +
            "ORDER BY sr.questionId, sr.evaluationType")
    List<Object[]> calculateQuestionStatisticsBothTypes(@Param("periodId") Long periodId);

    @Query("SELECT sr.questionId, sr.evaluationType, AVG(sr.respondentScore) " +
            "FROM SurveyResponse sr " +
            "JOIN EmployeeTemp e ON sr.respondentNumber = e.employeeNumber " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE sr.periodId = :periodId " +
            "AND o.oCode IN :orgCodes " +
            "AND (sr.evaluationType = 'SELF' OR sr.evaluationType = 'OTHERS') " +
            "GROUP BY sr.questionId, sr.evaluationType " +
            "ORDER BY sr.questionId, sr.evaluationType")
    List<Object[]> calculateQuestionStatisticsByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );    @Query("SELECT sr FROM SurveyResponse sr " +
            "WHERE sr.periodId = :periodId " +
            "AND (sr.evaluationType = 'SELF' OR sr.evaluationType = 'OTHERS') " +
            "ORDER BY sr.questionId")
    List<SurveyResponse> findAllByPeriodId(@Param("periodId") Long periodId);

}