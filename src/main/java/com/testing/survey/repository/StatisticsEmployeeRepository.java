package com.testing.survey.repository;

import com.testing.survey.entity.temp.EmployeeTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface StatisticsEmployeeRepository extends JpaRepository<EmployeeTemp, Long> {

    @Query("SELECT e FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId " +
            "AND (o.oCode = :orgCode OR o.upCode = :orgCode)")
    List<EmployeeTemp> findEmployeesByPeriodAndOrganization(
            @Param("periodId") Long periodId,
            @Param("orgCode") String orgCode
    );

    @Query("SELECT AVG(e.scoreSelf) FROM EmployeeTemp e " +
            "WHERE e.periodId = :periodId")
    Double calculateAverageSelfScore(@Param("periodId") Long periodId);

    @Query("SELECT AVG(e.scoreOthers) FROM EmployeeTemp e " +
            "WHERE e.periodId = :periodId")
    Double calculateAverageOthersScore(@Param("periodId") Long periodId);

    @Query("SELECT " +
            "SUM(CASE WHEN e.completedSelf = true THEN 1 ELSE 0 END) * 100.0 / COUNT(e) " +
            "FROM EmployeeTemp e " +
            "WHERE e.periodId = :periodId")
    Double calculateSelfCompletionRate(@Param("periodId") Long periodId);

    @Query("SELECT " +
            "SUM(CASE WHEN e.completedOthers = true THEN 1 ELSE 0 END) * 100.0 / COUNT(e) " +
            "FROM EmployeeTemp e " +
            "WHERE e.periodId = :periodId")
    Double calculateOthersCompletionRate(@Param("periodId") Long periodId);

    @Query("SELECT e.repGradeName, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "WHERE e.periodId = :periodId " +
            "GROUP BY e.repGradeName")
    List<Object[]> calculateGradeStatistics(@Param("periodId") Long periodId);

    @Query("SELECT e.personTypeName, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "WHERE e.periodId = :periodId " +
            "GROUP BY e.personTypeName")
    List<Object[]> calculatePersonTypeStatistics(@Param("periodId") Long periodId);

    @Query("SELECT e.sex, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "WHERE e.periodId = :periodId " +
            "GROUP BY e.sex")
    List<Object[]> calculateSexStatistics(@Param("periodId") Long periodId);

    @Query("SELECT o.orgName, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId " +
            "GROUP BY o.orgName")
    List<Object[]> calculateOrganizationStatistics(@Param("periodId") Long periodId);

    @Query("SELECT DISTINCT e.personTypeName FROM EmployeeTemp e WHERE e.isDeleted = false")
    List<String> findDistinctPersonTypes();

    @Query("SELECT DISTINCT e.repGradeName FROM EmployeeTemp e WHERE e.isDeleted = false")
    List<String> findDistinctGrades();

    @Query("SELECT DISTINCT e.sex FROM EmployeeTemp e WHERE e.isDeleted = false")
    List<String> findDistinctSexes();

    @Query("SELECT AVG(e.scoreSelf) FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes")
    Double calculateAverageSelfScoreByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT AVG(e.scoreOthers) FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes")
    Double calculateAverageOthersScoreByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT " +
            "SUM(CASE WHEN e.completedSelf = true THEN 1 ELSE 0 END) * 100.0 / COUNT(e) " +
            "FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes")
    Double calculateSelfCompletionRateByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT " +
            "SUM(CASE WHEN e.completedOthers = true THEN 1 ELSE 0 END) * 100.0 / COUNT(e) " +
            "FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes")
    Double calculateOthersCompletionRateByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT e.repGradeName, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes " +
            "GROUP BY e.repGradeName")
    List<Object[]> calculateGradeStatisticsByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT e.personTypeName, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes " +
            "GROUP BY e.personTypeName")
    List<Object[]> calculatePersonTypeStatisticsByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT e.sex, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes " +
            "GROUP BY e.sex")
    List<Object[]> calculateSexStatisticsByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT o.orgName, AVG(e.scoreSelf) as selfScore, AVG(e.scoreOthers) as othersScore " +
            "FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId AND o.oCode IN :orgCodes " +
            "GROUP BY o.orgName")
    List<Object[]> calculateOrganizationStatisticsByOrganizations(
            @Param("periodId") Long periodId,
            @Param("orgCodes") List<String> orgCodes
    );

    @Query("SELECT e FROM EmployeeTemp e " +
            "JOIN OrganizationTemp o ON e.organizationId = o.id " +
            "WHERE e.periodId = :periodId " +
            "AND o.oCode IN :orgCodes")
    List<EmployeeTemp> findEmployeesByPeriodAndOrganizations(
                    @Param("periodId") Long periodId,
                    @Param("orgCodes") List<String> orgCodes
            );

    @Query("SELECT e FROM EmployeeTemp e WHERE e.periodId = :periodId")
    List<EmployeeTemp> findAllByPeriodId(@Param("periodId") Long periodId);

}