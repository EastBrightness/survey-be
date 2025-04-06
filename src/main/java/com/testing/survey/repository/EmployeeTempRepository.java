package com.testing.survey.repository;

import com.testing.survey.entity.temp.EmployeeTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeTempRepository extends JpaRepository<EmployeeTemp, Long> {
    Optional<EmployeeTemp> findByEmployeeNumber(String employeeNumber);
    List<EmployeeTemp> findByOrganizationNameAndIsDeletedFalse(String organizationName);

    @Query("SELECT e FROM EmployeeTemp e WHERE e.organizationName = :orgName " +
            "AND FUNCTION('DATEDIFF', CURRENT_DATE, e.jobDate) >= 60 " +
            "AND e.isDeleted = false")
    List<EmployeeTemp> findEligibleEmployees(@Param("orgName") String orgName);

    @Query("SELECT DISTINCT e.organizationName FROM EmployeeTemp e " +
            "WHERE e.organizationName LIKE CONCAT(:departmentName, '%') " +
            "AND e.isDeleted = false " +
            "ORDER BY e.organizationName")
    List<String> findDistinctOrganizationsByDepartment(@Param("departmentName") String departmentName);

    List<EmployeeTemp> findByOrganizationNameIn(List<String> organizationNames);

    List<EmployeeTemp> findByOthersTestedTrue();
    List<EmployeeTemp> findByOthersTesterTrue();

    // 활성 상태의 평가자 조회
    List<EmployeeTemp> findByOthersTesterTrueAndIsDeletedFalse();

    // 활성 상태의 평가 대상자 조회
    List<EmployeeTemp> findByOthersTestedTrueAndIsDeletedFalse();

    List<EmployeeTemp> findByOrganizationIdAndIsDeletedFalse(Long organizationId);

    @Query("SELECT o.email FROM OcTable o WHERE o.employeeNumber = :employeeNumber")
    String findEmailByEmployeeNumber(@Param("employeeNumber") String employeeNumber);


    List<EmployeeTemp> findAllByIsDeletedFalse();

    List<EmployeeTemp> findByPersonNameContainingOrderByOrganizationNameAsc(String name);

    List<EmployeeTemp> findByOrganizationNameInAndIsDeletedFalse(List<String> organizationNames);

    @Modifying
    @Query("UPDATE EmployeeTemp e SET e.sawResult = true WHERE e.employeeNumber = :employeeNumber")
    void updateSawResultByEmployeeNumber(@Param("employeeNumber") String employeeNumber);


    @Modifying
    @Query("UPDATE EmployeeTemp e SET e.scoreSelf = :selfScore, e.scoreOthers = :othersScore WHERE e.employeeNumber = :employeeNumber")
    void updateEmployeeScores(
            @Param("employeeNumber") String employeeNumber,
            @Param("selfScore") Double selfScore,
            @Param("othersScore") Double othersScore
    );
    // 이름과 평가 기간 ID로 직원 검색
    List<EmployeeTemp> findByPersonNameContainingAndPeriodId(String name, Long periodId);


    List<EmployeeTemp> findByPersonNameContaining(String name);


    // 직원 번호와 평가 기간으로 직원 정보 조회
    Optional<EmployeeTemp> findByEmployeeNumberAndPeriodId(String employeeNumber, Long periodId);
}

