package com.testing.survey.repository;

import com.testing.survey.entity.temp.EvalAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvalAssignRepository extends JpaRepository<EvalAssign, Long> {
    List<EvalAssign> findByTested(String tested);
    List<EvalAssign> findByTester(String tester);
    void deleteByTested(String tested);
}