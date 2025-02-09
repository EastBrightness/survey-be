package com.testing.survey.repository;

import com.testing.survey.entity.temp.OrganizationTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationTempRepository extends JpaRepository<OrganizationTemp, Long> {
    List<OrganizationTemp> findByUpCodeAndIsDeletedFalse(String upCode);
    Optional<OrganizationTemp> findByoCodeAndIsDeletedFalse(String oCode);

    List<OrganizationTemp> findByFullNameIn(List<String> fullNames);

    List<OrganizationTemp> findAllByIsDeletedFalse();
    boolean existsByoCode(String oCode);

    List<OrganizationTemp> findByIsDeletedTrueOrderByFullNameAsc();
    Optional<OrganizationTemp> findByoCode(String oCode);
}