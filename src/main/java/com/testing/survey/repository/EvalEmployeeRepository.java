package com.testing.survey.repository;

import com.testing.survey.entity.eval.EvalEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvalEmployeeRepository extends JpaRepository<EvalEmployee, Long> {
    Optional<EvalEmployee> findByPersonIdAndPeriodId(Long personId, String periodId);

}