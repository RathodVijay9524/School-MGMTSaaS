package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.AgentStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentStepRepository extends JpaRepository<AgentStep, Long> {
    List<AgentStep> findByAgentRun_IdOrderByIdAsc(Long runDbId);
}
