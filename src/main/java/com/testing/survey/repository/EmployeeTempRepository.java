package com.testing.survey.repository;

import com.testing.survey.entity.temp.EmployeeTemp;
import org.springframework.data.jpa.repository.JpaRepository;
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

}

