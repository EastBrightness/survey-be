package com.testing.survey.repository;

import com.testing.survey.entity.eval.EvalOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvalOrganizationRepository extends JpaRepository<EvalOrganization, Long> {
    Optional<EvalOrganization> findByOrganizationIdAndPeriodId(Long organizationId, String periodId);

//    void deleteByPeriodId(String periodId);

}