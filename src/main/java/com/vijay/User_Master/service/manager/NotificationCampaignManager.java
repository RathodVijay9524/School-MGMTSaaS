package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationCampaignManager {

    private final AgentRunRepository agentRunRepository;
    private final AgentStepRepository agentStepRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Data
    @Builder
    public static class CampaignState {
        private String campaignName;
        private String channel; // SMS | WHATSAPP | EMAIL
        private String audienceType; // ALL | CLASS | STUDENT_IDS
        private List<Long> classIds;
        private List<Long> studentIds;
        private String scheduledAt; // ISO string
        private Integer targetCount;
        private Integer sentCount;
        private Integer failedCount;
        private Boolean completed;
        private Map<String, String> timestamps;
    }

    private String newRunId() { return UUID.randomUUID().toString().replace("-", ""); }

    private void saveStep(AgentRun run, String node, Object input, Object output, String status, String error) {
        try {
            AgentStep step = AgentStep.builder()
                    .agentRun(run)
                    .nodeName(node)
                    .inputJson(input != null ? mapper.writeValueAsString(input) : null)
                    .outputJson(output != null ? mapper.writeValueAsString(output) : null)
                    .status(status)
                    .error(error)
                    .startedAt(LocalDateTime.now())
                    .finishedAt(LocalDateTime.now())
                    .build();
            agentStepRepository.save(step);
        } catch (Exception e) {
            log.warn("Failed to serialize step: {}", e.getMessage());
        }
    }

    private void persistState(AgentRun run, CampaignState state, String currentNode, String status) {
        try { run.setStateJson(mapper.writeValueAsString(state)); } catch (Exception ignored) { run.setStateJson(null);} 
        run.setCurrentNode(currentNode);
        run.setStatus(status);
        run.setLastHeartbeat(LocalDateTime.now());
        agentRunRepository.save(run);
    }

    private CampaignState readState(AgentRun run) {
        try {
            if (run.getStateJson() == null) return CampaignState.builder().build();
            return mapper.readValue(run.getStateJson(), CampaignState.class);
        } catch (Exception e) { return CampaignState.builder().build(); }
    }

    @Tool(name = "notifStartCampaign", description = "Start a notification campaign. Inputs: campaignName, channel(SMS|WHATSAPP|EMAIL), audienceType(ALL|CLASS|STUDENT_IDS). Returns runId.")
    public String start(String campaignName, String channel, String audienceType) {
        Long ownerId = CommonUtils.getLoggedInUser() != null ? CommonUtils.getLoggedInUser().getId() : null;
        String runId = newRunId();
        AgentRun run = AgentRun.builder()
                .runId(runId)
                .agentName("NotificationCampaign")
                .ownerId(ownerId)
                .status("RUNNING")
                .currentNode("start_campaign")
                .build();
        agentRunRepository.save(run);

        CampaignState state = CampaignState.builder()
                .campaignName(campaignName)
                .channel(channel != null ? channel.toUpperCase() : "SMS")
                .audienceType(audienceType != null ? audienceType.toUpperCase() : "ALL")
                .classIds(new ArrayList<>())
                .studentIds(new ArrayList<>())
                .targetCount(0)
                .sentCount(0)
                .failedCount(0)
                .completed(false)
                .timestamps(new HashMap<>())
                .build();
        state.getTimestamps().put("STARTED", LocalDateTime.now().toString());
        persistState(run, state, "start_campaign", "RUNNING");
        saveStep(run, "start_campaign", Map.of("name", campaignName, "channel", channel, "audience", audienceType), Map.of("ok", true), "OK", null);
        return runId;
    }

    @Tool(name = "notifSelectAudience", description = "Select campaign audience. Inputs: runId, classIdsCsv?, studentIdsCsv?. Returns target count.")
    public String selectAudience(String runId, String classIdsCsv, String studentIdsCsv) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        CampaignState state = readState(run);
        if ((classIdsCsv == null || classIdsCsv.isBlank()) && (studentIdsCsv == null || studentIdsCsv.isBlank())) {
            return "No change: target=" + (state.getTargetCount() != null ? state.getTargetCount() : 0);
        }
        List<Long> classIds = state.getClassIds() != null ? state.getClassIds() : new ArrayList<>();
        List<Long> studentIds = state.getStudentIds() != null ? state.getStudentIds() : new ArrayList<>();
        if (classIdsCsv != null && !classIdsCsv.isBlank()) {
            for (String s : classIdsCsv.split(",")) {
                try { classIds.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        if (studentIdsCsv != null && !studentIdsCsv.isBlank()) {
            for (String s : studentIdsCsv.split(",")) {
                try { studentIds.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        state.setClassIds(classIds);
        state.setStudentIds(studentIds);
        int target = (classIds.size() * 30) + studentIds.size(); // naive: ~30 students per class
        state.setTargetCount(target);
        persistState(run, state, "select_audience", "RUNNING");
        saveStep(run, "select_audience", Map.of("classes", classIds, "students", studentIds), Map.of("target", target), "OK", null);
        return "Target audience: " + target;
    }

    @Tool(name = "notifSchedule", description = "Schedule campaign. Inputs: runId, scheduledAt(ISO). Returns status.")
    public String schedule(String runId, String scheduledAt) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        CampaignState state = readState(run);
        if (state.getScheduledAt() != null && !state.getScheduledAt().isBlank()) {
            return "Already scheduled at: " + state.getScheduledAt();
        }
        state.setScheduledAt(scheduledAt != null ? scheduledAt : LocalDateTime.now().plusMinutes(10).toString());
        state.getTimestamps().put("SCHEDULED", LocalDateTime.now().toString());
        persistState(run, state, "schedule", "RUNNING");
        saveStep(run, "schedule", Map.of("scheduledAt", state.getScheduledAt()), Map.of("ok", true), "OK", null);
        return "Scheduled at: " + state.getScheduledAt();
    }

    @Tool(name = "notifSend", description = "Send campaign. Inputs: runId. Returns send summary.")
    public String send(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        CampaignState state = readState(run);
        if (Boolean.TRUE.equals(state.getCompleted())) {
            return "Already sent: sent=" + (state.getSentCount() != null ? state.getSentCount() : 0);
        }
        int target = state.getTargetCount() != null ? state.getTargetCount() : 0;
        int sent = (int) Math.round(target * 0.95); // assume 95% success
        int failed = Math.max(0, target - sent);
        state.setSentCount(sent);
        state.setFailedCount(failed);
        state.setCompleted(true);
        state.getTimestamps().put("SENT", LocalDateTime.now().toString());
        persistState(run, state, "send", "COMPLETED");
        saveStep(run, "send", Map.of(), Map.of("sent", sent, "failed", failed), "OK", null);
        return "Sent: " + sent + ", Failed: " + failed;
    }

    @Tool(name = "notifStats", description = "Get campaign stats. Inputs: runId. Returns summary JSON.")
    public String stats(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        CampaignState state = readState(run);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("campaignName", state.getCampaignName());
        summary.put("channel", state.getChannel());
        summary.put("audienceType", state.getAudienceType());
        summary.put("target", state.getTargetCount());
        summary.put("sent", state.getSentCount());
        summary.put("failed", state.getFailedCount());
        summary.put("completed", state.getCompleted());
        try { return mapper.writeValueAsString(summary); } catch (Exception e) { return summary.toString(); }
    }

    @Tool(name = "notifGetRunState", description = "Get campaign run state. Inputs: runId. Returns state JSON.")
    public String getRunState(String runId) {
        AgentRun run = agentRunRepository.findByRunId(runId).orElse(null);
        if (run == null) return "Invalid runId";
        return run.getStateJson();
    }
}
