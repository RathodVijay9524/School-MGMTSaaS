package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.AgentRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentRunRepository extends JpaRepository<AgentRun, Long> {
    Optional<AgentRun> findByRunId(String runId);
    boolean existsByRunId(String runId);
}
