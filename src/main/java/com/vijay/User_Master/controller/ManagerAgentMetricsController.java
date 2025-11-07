package com.vijay.User_Master.controller;

import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager-agents/metrics")
@RequiredArgsConstructor
public class ManagerAgentMetricsController {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestParam(required = false) String manager,
                                     @RequestParam(required = false, defaultValue = "PT24H") String sinceDuration) {
        Duration window = Duration.parse(sinceDuration);
        LocalDateTime cutoff = LocalDateTime.now().minus(window);

        List<AgentRun> runs = agentRunRepository.findAll();
        if (manager != null && !manager.isBlank()) {
            runs = runs.stream()
                    .filter(r -> r.getAgentName() != null && r.getAgentName().toLowerCase().contains(manager.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<AgentStep> allSteps = agentStepRepository.findAll();
        // Steps filtered by time window and belonging to the filtered runs
        Set<String> runIds = runs.stream().map(AgentRun::getRunId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<AgentStep> steps = allSteps.stream()
                .filter(s -> s.getFinishedAt() != null && !s.getFinishedAt().isBefore(cutoff))
                .filter(s -> s.getAgentRun() != null && runIds.contains(s.getAgentRun().getRunId()))
                .collect(Collectors.toList());

        long totalRuns = runs.size();
        long completedRuns = runs.stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus())).count();
        long runningRuns = runs.stream().filter(r -> "RUNNING".equalsIgnoreCase(r.getStatus())).count();
        long failedSteps = steps.stream().filter(s -> "ERROR".equalsIgnoreCase(s.getStatus())).count();

        Map<String, List<AgentStep>> stepsByRun = steps.stream()
                .filter(s -> s.getAgentRun() != null && s.getAgentRun().getRunId() != null)
                .collect(Collectors.groupingBy(s -> s.getAgentRun().getRunId()));

        List<Long> durationsMs = new ArrayList<>();
        for (Map.Entry<String, List<AgentStep>> e : stepsByRun.entrySet()) {
            LocalDateTime minStart = e.getValue().stream().map(AgentStep::getStartedAt).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
            LocalDateTime maxEnd = e.getValue().stream().map(AgentStep::getFinishedAt).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null);
            if (minStart != null && maxEnd != null) {
                durationsMs.add(Duration.between(minStart, maxEnd).toMillis());
            }
        }
        double avgDurationMs = durationsMs.isEmpty() ? 0 : durationsMs.stream().mapToLong(Long::longValue).average().orElse(0);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("filters", Map.of("manager", manager, "sinceDuration", sinceDuration));
        body.put("totalRuns", totalRuns);
        body.put("completedRuns", completedRuns);
        body.put("runningRuns", runningRuns);
        body.put("failedSteps", failedSteps);
        body.put("successRate", totalRuns == 0 ? 0 : (double) completedRuns / totalRuns);
        body.put("avgDurationMs", avgDurationMs);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/failures")
    public ResponseEntity<?> failures(@RequestParam(defaultValue = "20") int limit) {
        List<AgentStep> steps = agentStepRepository.findAll();
        List<Map<String, Object>> failed = steps.stream()
                .filter(s -> "ERROR".equalsIgnoreCase(s.getStatus()))
                .sorted(Comparator.comparing(AgentStep::getFinishedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .limit(Math.max(1, Math.min(limit, 200)))
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("runId", s.getAgentRun() != null ? s.getAgentRun().getRunId() : null);
                    m.put("node", s.getNodeName());
                    m.put("error", s.getError());
                    m.put("finishedAt", s.getFinishedAt());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(failed);
    }

    @GetMapping("/alerts")
    public ResponseEntity<?> alerts(@RequestParam(defaultValue = "PT24H") String sinceDuration, @RequestParam(defaultValue = "10") int limit) {
        Duration window = Duration.parse(sinceDuration);
        LocalDateTime cutoff = LocalDateTime.now().minus(window);
        List<AgentStep> steps = agentStepRepository.findAll();
        List<Map<String, Object>> alerts = steps.stream()
                .filter(s -> "ERROR".equalsIgnoreCase(s.getStatus()))
                .filter(s -> s.getFinishedAt() != null && !s.getFinishedAt().isBefore(cutoff))
                .sorted(Comparator.comparing(AgentStep::getFinishedAt).reversed())
                .limit(Math.max(1, Math.min(limit, 100)))
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("type", "RUN_STEP_FAILED");
                    m.put("runId", s.getAgentRun() != null ? s.getAgentRun().getRunId() : null);
                    m.put("node", s.getNodeName());
                    m.put("error", s.getError());
                    m.put("at", s.getFinishedAt());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }
}
