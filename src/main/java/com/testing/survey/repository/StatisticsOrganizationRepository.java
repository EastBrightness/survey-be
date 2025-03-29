package com.testing.survey.repository;

import com.testing.survey.entity.temp.OrganizationTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsOrganizationRepository extends JpaRepository<OrganizationTemp, Long> {

    @Query("SELECT o FROM OrganizationTemp o WHERE o.upCode IS NULL")
    List<OrganizationTemp> findRootOrganizations();

    @Query("SELECT o FROM OrganizationTemp o WHERE o.upCode = :parentCode")
    List<OrganizationTemp> findSubOrganizations(String parentCode);
}