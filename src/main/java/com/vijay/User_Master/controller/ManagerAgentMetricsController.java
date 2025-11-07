package com.vijay.User_Master.controller;

import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.entity.AlertRule;
import com.vijay.User_Master.entity.WebhookSubscription;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import com.vijay.User_Master.repository.AlertRuleRepository;
import com.vijay.User_Master.repository.WebhookSubscriptionRepository;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    private final AlertRuleRepository alertRuleRepository;
    private final WebhookSubscriptionRepository webhookSubscriptionRepository;

    private static class SummaryCacheEntry {
        long atMs;
        Map<String, Object> body;
    }
    private final Map<String, SummaryCacheEntry> summaryCache = new HashMap<>();
    @Value("${metrics.summary.ttl-ms:30000}")
    private long SUMMARY_TTL_MS; // configurable TTL

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestParam(required = false) String manager,
                                     @RequestParam(required = false, defaultValue = "PT24H") String sinceDuration,
                                     @RequestParam(required = false) Long ownerId,
                                     @RequestParam(required = false) String status) {
        Duration window = Duration.parse(sinceDuration);
        LocalDateTime cutoff = LocalDateTime.now().minus(window);

        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");

        // Cache key and lookup
        String cacheKey = String.join("|",
                String.valueOf(scopedOwnerId),
                manager == null ? "" : manager,
                sinceDuration,
                status == null ? "" : status
        );
        SummaryCacheEntry entry = summaryCache.get(cacheKey);
        long nowMs = System.currentTimeMillis();
        if (entry != null && nowMs - entry.atMs <= SUMMARY_TTL_MS) {
            return ResponseEntity.ok(entry.body);
        }
        List<AgentRun> runs = agentRunRepository.findAll();
        if (manager != null && !manager.isBlank()) {
            runs = runs.stream()
                    .filter(r -> r.getAgentName() != null && r.getAgentName().toLowerCase().contains(manager.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (scopedOwnerId != null) {
            Long finalOwnerId = scopedOwnerId;
            runs = runs.stream().filter(r -> Objects.equals(r.getOwnerId(), finalOwnerId)).collect(Collectors.toList());
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
        if (status != null && !status.isBlank()) {
            runs = runs.stream().filter(r -> r.getStatus() != null && r.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
        }
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
        body.put("filters", Map.of("manager", manager, "sinceDuration", sinceDuration, "ownerId", scopedOwnerId, "status", status));
        body.put("totalRuns", totalRuns);
        body.put("completedRuns", completedRuns);
        body.put("runningRuns", runningRuns);
        body.put("failedSteps", failedSteps);
        body.put("successRate", totalRuns == 0 ? 0 : (double) completedRuns / totalRuns);
        body.put("avgDurationMs", avgDurationMs);
        SummaryCacheEntry newEntry = new SummaryCacheEntry();
        newEntry.atMs = nowMs;
        newEntry.body = body;
        summaryCache.put(cacheKey, newEntry);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/failures")
    public ResponseEntity<?> failures(@RequestParam(defaultValue = "20") int limit,
                                      @RequestParam(required = false) Long ownerId) {
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        List<AgentStep> steps = agentStepRepository.findAll();
        List<Map<String, Object>> failed = steps.stream()
                .filter(s -> "ERROR".equalsIgnoreCase(s.getStatus()))
                .filter(s -> scopedOwnerId == null || (s.getAgentRun() != null && Objects.equals(s.getAgentRun().getOwnerId(), scopedOwnerId)))
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
    public ResponseEntity<?> alerts(@RequestParam(defaultValue = "PT24H") String sinceDuration, @RequestParam(defaultValue = "10") int limit,
                                    @RequestParam(required = false) Long ownerId,
                                    @RequestParam(required = false, defaultValue = "ERROR") String status) {
        Duration window = Duration.parse(sinceDuration);
        LocalDateTime cutoff = LocalDateTime.now().minus(window);
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        List<AgentStep> steps = agentStepRepository.findAll();
        // Load active, unmuted rules for owner
        LocalDateTime now = LocalDateTime.now();
        List<AlertRule> rules = scopedOwnerId == null ? alertRuleRepository.findAll() : alertRuleRepository.findByOwnerId(scopedOwnerId);
        List<AlertRule> activeRules = rules.stream()
                .filter(r -> Boolean.TRUE.equals(r.getActive()))
                .filter(r -> r.getMutedUntil() == null || now.isAfter(r.getMutedUntil()))
                .collect(Collectors.toList());

        List<Map<String, Object>> alerts = steps.stream()
                .filter(s -> status == null || status.isBlank() || (s.getStatus() != null && s.getStatus().equalsIgnoreCase(status)))
                .filter(s -> s.getFinishedAt() != null && !s.getFinishedAt().isBefore(cutoff))
                .filter(s -> scopedOwnerId == null || (s.getAgentRun() != null && Objects.equals(s.getAgentRun().getOwnerId(), scopedOwnerId)))
                // enforce rules: must match at least one active rule by eventType and optional agentName scope
                .filter(s -> {
                    String evt = ("ERROR".equalsIgnoreCase(s.getStatus())) ? "RUN_STEP_FAILED" : s.getStatus();
                    String agentName = s.getAgentRun() != null ? s.getAgentRun().getAgentName() : null;
                    return activeRules.stream().anyMatch(r ->
                            (r.getEventType() == null || r.getEventType().equalsIgnoreCase(evt)) &&
                            (r.getAgentName() == null || (agentName != null && agentName.toLowerCase().contains(r.getAgentName().toLowerCase())))
                    );
                })
                .sorted(Comparator.comparing(AgentStep::getFinishedAt).reversed())
                .limit(Math.max(1, Math.min(limit, 100)))
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("type", ("ERROR".equalsIgnoreCase(s.getStatus())) ? "RUN_STEP_FAILED" : s.getStatus());
                    m.put("runId", s.getAgentRun() != null ? s.getAgentRun().getRunId() : null);
                    m.put("node", s.getNodeName());
                    m.put("error", s.getError());
                    m.put("at", s.getFinishedAt());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    // ================= SSE REAL-TIME ALERTS =================
    @GetMapping(path = "/stream/alerts", produces = "text/event-stream")
    public SseEmitter streamAlerts(@RequestParam(defaultValue = "PT1H") String sinceDuration,
                                   @RequestParam(defaultValue = "5000") long intervalMs,
                                   @RequestParam(defaultValue = "60000") long timeoutMs,
                                   @RequestParam(required = false) Long ownerId,
                                   @RequestParam(required = false, defaultValue = "ERROR") String status) {
        SseEmitter emitter = new SseEmitter(timeoutMs);
        Duration window = Duration.parse(sinceDuration);
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) {
            emitter.completeWithError(new RuntimeException("ownerId required"));
            return emitter;
        }
        final LocalDateTime[] lastCutoff = new LocalDateTime[]{LocalDateTime.now().minus(window)};

        Thread t = new Thread(() -> {
            try {
                while (true) {
                    LocalDateTime cutoff = lastCutoff[0];
                    List<AgentStep> steps = agentStepRepository.findAll();
                    LocalDateTime now = LocalDateTime.now();
                    List<AlertRule> rules = scopedOwnerId == null ? alertRuleRepository.findAll() : alertRuleRepository.findByOwnerId(scopedOwnerId);
                    List<AlertRule> activeRules = rules.stream()
                            .filter(r -> Boolean.TRUE.equals(r.getActive()))
                            .filter(r -> r.getMutedUntil() == null || now.isAfter(r.getMutedUntil()))
                            .collect(Collectors.toList());
                    List<AgentStep> recent = steps.stream()
                            .filter(s -> status == null || status.isBlank() || (s.getStatus() != null && s.getStatus().equalsIgnoreCase(status)))
                            .filter(s -> s.getFinishedAt() != null && !s.getFinishedAt().isBefore(cutoff))
                            .filter(s -> scopedOwnerId == null || (s.getAgentRun() != null && Objects.equals(s.getAgentRun().getOwnerId(), scopedOwnerId)))
                            .filter(s -> {
                                String evt = ("ERROR".equalsIgnoreCase(s.getStatus())) ? "RUN_STEP_FAILED" : s.getStatus();
                                String agentName = s.getAgentRun() != null ? s.getAgentRun().getAgentName() : null;
                                return activeRules.stream().anyMatch(r ->
                                        (r.getEventType() == null || r.getEventType().equalsIgnoreCase(evt)) &&
                                        (r.getAgentName() == null || (agentName != null && agentName.toLowerCase().contains(r.getAgentName().toLowerCase())))
                                );
                            })
                            .sorted(Comparator.comparing(AgentStep::getFinishedAt))
                            .collect(Collectors.toList());
                    for (AgentStep s : recent) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("type", ("ERROR".equalsIgnoreCase(s.getStatus())) ? "RUN_STEP_FAILED" : s.getStatus());
                        m.put("runId", s.getAgentRun() != null ? s.getAgentRun().getRunId() : null);
                        m.put("node", s.getNodeName());
                        m.put("error", s.getError());
                        m.put("at", s.getFinishedAt());
                        emitter.send(SseEmitter.event().name("alert").data(m));
                        if (s.getFinishedAt() != null && s.getFinishedAt().isAfter(lastCutoff[0])) {
                            lastCutoff[0] = s.getFinishedAt();
                        }
                    }
                    Thread.sleep(Math.max(1000, intervalMs));
                }
            } catch (Exception ex) {
                try { emitter.complete(); } catch (Exception ignored) {}
            }
        }, "alerts-sse-thread");
        t.setDaemon(true);
        t.start();
        return emitter;
    }

    // ================= PAGINATED RUNS/STEPS =================
    @GetMapping("/runs")
    public ResponseEntity<?> runs(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String manager,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false, defaultValue = "PT30D") String sinceDuration,
                                  @RequestParam(required = false) Long ownerId) {
        Duration window = Duration.parse(sinceDuration);
        LocalDateTime cutoff = LocalDateTime.now().minus(window);
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        List<AgentRun> all = agentRunRepository.findAll();
        List<AgentRun> filtered = all.stream()
                .filter(r -> r.getLastHeartbeat() == null || !r.getLastHeartbeat().isBefore(cutoff))
                .filter(r -> manager == null || manager.isBlank() || (r.getAgentName() != null && r.getAgentName().toLowerCase().contains(manager.toLowerCase())))
                .filter(r -> status == null || status.isBlank() || (r.getStatus() != null && r.getStatus().equalsIgnoreCase(status)))
                .filter(r -> scopedOwnerId == null || Objects.equals(r.getOwnerId(), scopedOwnerId))
                .sorted(Comparator.comparing(AgentRun::getLastHeartbeat, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .collect(Collectors.toList());
        int from = Math.max(0, page * Math.max(1, size));
        int to = Math.min(filtered.size(), from + Math.max(1, size));
        List<Map<String, Object>> items = filtered.subList(from, to).stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("runId", r.getRunId());
            m.put("agentName", r.getAgentName());
            m.put("status", r.getStatus());
            m.put("currentNode", r.getCurrentNode());
            m.put("lastHeartbeat", r.getLastHeartbeat());
            return m;
        }).collect(Collectors.toList());
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("page", page);
        resp.put("size", size);
        resp.put("total", filtered.size());
        resp.put("items", items);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/steps")
    public ResponseEntity<?> steps(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String runId,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false, defaultValue = "PT7D") String sinceDuration,
                                   @RequestParam(required = false) Long ownerId) {
        Duration window = Duration.parse(sinceDuration);
        LocalDateTime cutoff = LocalDateTime.now().minus(window);
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        List<AgentStep> all = agentStepRepository.findAll();
        List<AgentStep> filtered = all.stream()
                .filter(s -> s.getFinishedAt() != null && !s.getFinishedAt().isBefore(cutoff))
                .filter(s -> runId == null || runId.isBlank() || (s.getAgentRun() != null && runId.equals(s.getAgentRun().getRunId())))
                .filter(s -> status == null || status.isBlank() || (s.getStatus() != null && s.getStatus().equalsIgnoreCase(status)))
                .filter(s -> scopedOwnerId == null || (s.getAgentRun() != null && Objects.equals(s.getAgentRun().getOwnerId(), scopedOwnerId)))
                .sorted(Comparator.comparing(AgentStep::getFinishedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .collect(Collectors.toList());
        int from = Math.max(0, page * Math.max(1, size));
        int to = Math.min(filtered.size(), from + Math.max(1, size));
        List<Map<String, Object>> items = filtered.subList(from, to).stream().map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("runId", s.getAgentRun() != null ? s.getAgentRun().getRunId() : null);
            m.put("node", s.getNodeName());
            m.put("status", s.getStatus());
            m.put("error", s.getError());
            m.put("startedAt", s.getStartedAt());
            m.put("finishedAt", s.getFinishedAt());
            return m;
        }).collect(Collectors.toList());
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("page", page);
        resp.put("size", size);
        resp.put("total", filtered.size());
        resp.put("items", items);
        return ResponseEntity.ok(resp);
    }

    // ================= WEBHOOKS & ALERT RULES =================
    @PostMapping("/webhooks/register")
    public ResponseEntity<?> registerWebhook(@RequestParam String eventType,
                                             @RequestParam String url,
                                             @RequestParam(required = false) Long ownerId,
                                             @RequestParam(required = false) String agentName) {
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        WebhookSubscription sub = WebhookSubscription.builder()
                .ownerId(scopedOwnerId)
                .eventType(eventType)
                .url(url)
                .active(true)
                .createdAt(LocalDateTime.now())
                .agentName(agentName)
                .build();
        webhookSubscriptionRepository.save(sub);
        return ResponseEntity.ok(Map.of("id", sub.getId()));
    }

    @GetMapping("/webhooks")
    public ResponseEntity<?> listWebhooks(@RequestParam(required = false) Long ownerId) {
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        List<WebhookSubscription> subs = webhookSubscriptionRepository.findByOwnerId(scopedOwnerId);
        return ResponseEntity.ok(subs);
    }

    @DeleteMapping("/webhooks/{id}")
    public ResponseEntity<?> deleteWebhook(@PathVariable Long id, @RequestParam(required = false) Long ownerId) {
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        var subOpt = webhookSubscriptionRepository.findById(id);
        if (subOpt.isEmpty()) return ResponseEntity.ok(Map.of("deleted", true));
        if (!Objects.equals(subOpt.get().getOwnerId(), scopedOwnerId)) return ResponseEntity.status(403).body("forbidden");
        webhookSubscriptionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @PostMapping("/alerts/rules")
    public ResponseEntity<?> createAlertRule(@RequestParam String eventType,
                                             @RequestParam(defaultValue = "true") boolean active,
                                             @RequestParam(required = false) Long ownerId,
                                             @RequestParam(required = false) String agentName) {
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        AlertRule rule = AlertRule.builder()
                .ownerId(scopedOwnerId)
                .eventType(eventType)
                .active(active)
                .agentName(agentName)
                .createdAt(LocalDateTime.now())
                .build();
        alertRuleRepository.save(rule);
        return ResponseEntity.ok(Map.of("id", rule.getId()));
    }

    @PostMapping("/alerts/rules/{id}/mute")
    public ResponseEntity<?> muteRule(@PathVariable Long id, @RequestParam(defaultValue = "PT1H") String untilDuration,
                                      @RequestParam(required = false) Long ownerId) {
        Duration d = Duration.parse(untilDuration);
        AlertRule rule = alertRuleRepository.findById(id).orElse(null);
        if (rule == null) return ResponseEntity.badRequest().body("Invalid id");
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null || !Objects.equals(rule.getOwnerId(), scopedOwnerId)) return ResponseEntity.status(403).body("forbidden");
        rule.setMutedUntil(LocalDateTime.now().plus(d));
        alertRuleRepository.save(rule);
        return ResponseEntity.ok(Map.of("mutedUntil", rule.getMutedUntil()));
    }

    @PostMapping("/alerts/rules/{id}/unmute")
    public ResponseEntity<?> unmuteRule(@PathVariable Long id, @RequestParam(required = false) Long ownerId) {
        AlertRule rule = alertRuleRepository.findById(id).orElse(null);
        if (rule == null) return ResponseEntity.badRequest().body("Invalid id");
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null || !Objects.equals(rule.getOwnerId(), scopedOwnerId)) return ResponseEntity.status(403).body("forbidden");
        rule.setMutedUntil(null);
        alertRuleRepository.save(rule);
        return ResponseEntity.ok(Map.of("muted", false));
    }

    @PostMapping("/alerts/dispatch")
    public ResponseEntity<?> dispatchAlerts(@RequestParam(defaultValue = "PT1H") String sinceDuration,
                                            @RequestParam(required = false) Long ownerId,
                                            @RequestParam(required = false, defaultValue = "ERROR") String status) {
        Duration window = Duration.parse(sinceDuration);
        LocalDateTime cutoff = LocalDateTime.now().minus(window);
        Long scopedOwnerId = ownerId != null ? ownerId : (CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null);
        if (scopedOwnerId == null) return ResponseEntity.status(401).body("ownerId required");
        List<AgentStep> steps = agentStepRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        List<AlertRule> rules = scopedOwnerId == null ? alertRuleRepository.findAll() : alertRuleRepository.findByOwnerId(scopedOwnerId);
        List<AlertRule> activeRules = rules.stream()
                .filter(r -> Boolean.TRUE.equals(r.getActive()))
                .filter(r -> r.getMutedUntil() == null || now.isAfter(r.getMutedUntil()))
                .collect(Collectors.toList());
        List<AgentStep> recent = steps.stream()
                .filter(s -> status == null || status.isBlank() || (s.getStatus() != null && s.getStatus().equalsIgnoreCase(status)))
                .filter(s -> s.getFinishedAt() != null && !s.getFinishedAt().isBefore(cutoff))
                .filter(s -> scopedOwnerId == null || (s.getAgentRun() != null && Objects.equals(s.getAgentRun().getOwnerId(), scopedOwnerId)))
                .filter(s -> {
                    String evt = ("ERROR".equalsIgnoreCase(s.getStatus())) ? "RUN_STEP_FAILED" : s.getStatus();
                    String agentName = s.getAgentRun() != null ? s.getAgentRun().getAgentName() : null;
                    return activeRules.stream().anyMatch(r ->
                            (r.getEventType() == null || r.getEventType().equalsIgnoreCase(evt)) &&
                            (r.getAgentName() == null || (agentName != null && agentName.toLowerCase().contains(r.getAgentName().toLowerCase())))
                    );
                })
                .collect(Collectors.toList());
        if (recent.isEmpty()) return ResponseEntity.ok(Map.of("dispatched", 0));
        RestTemplate rt = new RestTemplate();
        int count = 0;
        for (AgentStep s : recent) {
            Long oid = s.getAgentRun() != null ? s.getAgentRun().getOwnerId() : scopedOwnerId;
            List<WebhookSubscription> subs = webhookSubscriptionRepository.findByOwnerIdAndActiveTrueAndEventType(oid, ("ERROR".equalsIgnoreCase(s.getStatus())) ? "RUN_STEP_FAILED" : s.getStatus());
            for (WebhookSubscription sub : subs) {
                try {
                    String agentName = s.getAgentRun() != null ? s.getAgentRun().getAgentName() : null;
                    if (sub.getAgentName() != null && (agentName == null || !agentName.toLowerCase().contains(sub.getAgentName().toLowerCase()))) {
                        continue; // skip if scoping doesn't match
                    }
                    Map<String, Object> payload = new LinkedHashMap<>();
                    payload.put("type", ("ERROR".equalsIgnoreCase(s.getStatus())) ? "RUN_STEP_FAILED" : s.getStatus());
                    payload.put("runId", s.getAgentRun() != null ? s.getAgentRun().getRunId() : null);
                    payload.put("node", s.getNodeName());
                    payload.put("error", s.getError());
                    payload.put("at", s.getFinishedAt());
                    rt.postForEntity(sub.getUrl(), payload, String.class);
                    count++;
                } catch (Exception ignored) {}
            }
        }
        return ResponseEntity.ok(Map.of("dispatched", count));
    }
}
