package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.dto.ChatRequest;
import com.vijay.User_Master.dto.ChatResponse;
import com.vijay.User_Master.entity.Grade;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AttendanceService;
import com.vijay.User_Master.service.ChatIntegrationService;
import com.vijay.User_Master.service.GradeService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ai.tool.annotation.Tool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orchestrator that identifies at-risk students by combining Attendance and Grades,
 * then drafts intervention email texts using the existing ChatIntegrationService.
 *
 * Note: This implementation avoids adding LangGraph4j for now and uses plain Spring services.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AtRiskStudentAgentManager {

    private final AttendanceService attendanceService;
    private final GradeService gradeService;
    private final WorkerRepository workerRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final ChatIntegrationService chatIntegrationService;

    @Data
    @Builder
    public static class Result {
        private Long classId;
        private Integer attendanceThreshold;
        private Long subjectId;
        private List<Long> lowAttendanceStudentIds;
        private List<Long> atRiskStudentIds;
        private List<String> emailDrafts;
        private String finalReport;
    }

    public String runAtRiskAnalysis(Long classId, int attendanceThreshold, Long subjectId) {
        log.info("Running At-Risk Student analysis for classId={}, threshold={}, subjectId={}",
                classId, attendanceThreshold, subjectId);

        // 1) Resolve class and list students in the class (ROLE_STUDENT)
        SchoolClass schoolClass = schoolClassRepository.findById(classId).orElse(null);
        if (schoolClass == null) {
            return "Class not found";
        }
        Long ownerId = schoolClass.getOwner() != null ? schoolClass.getOwner().getId() : null;
        List<Worker> ownerWorkers = ownerId != null
                ? workerRepository.findByOwner_IdAndIsDeletedFalse(ownerId)
                : List.of();

        Set<String> studentRoleNames = Set.of("ROLE_STUDENT");
        List<Worker> classStudents = ownerWorkers.stream()
                .filter(w -> w.getCurrentClass() != null && Objects.equals(w.getCurrentClass().getId(), classId))
                .filter(w -> w.getRoles() != null && w.getRoles().stream().map(Role::getName).anyMatch(studentRoleNames::contains))
                .collect(Collectors.toList());

        if (classStudents.isEmpty()) {
            return "Project Complete: No students found in the selected class.";
        }

        // 2) Find students below attendance threshold
        List<Worker> lowAttendance = classStudents.stream()
                .filter(w -> {
                    Double pct = attendanceService.calculateAttendancePercentage(w.getId());
                    return pct != null && pct < attendanceThreshold;
                })
                .collect(Collectors.toList());

        if (lowAttendance.isEmpty()) {
            return String.format(
                    "Project Complete:\n- Found %d students in class %s-%s.\n- None below %d%% attendance.",
                    classStudents.size(), schoolClass.getClassName(), schoolClass.getSection(), attendanceThreshold);
        }

        // 3) Filter by failing grades in the subject
        List<Worker> atRisk = lowAttendance.stream()
                .filter(w -> {
                    try {
                        var grades = gradeService.getGradesByStudentAndSubject(w.getId(), subjectId);
                        return grades != null && grades.stream().anyMatch(gr -> gr.getStatus() == Grade.GradeStatus.FAIL);
                    } catch (Exception ex) {
                        log.warn("Error checking grades for student {}: {}", w.getId(), ex.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

        // 4) Draft emails via ChatIntegrationService (no sending here)
        List<String> drafts = new ArrayList<>();
        for (Worker s : atRisk) {
            Double pct = attendanceService.calculateAttendancePercentage(s.getId());
            String studentName = (s.getFirstName() != null ? s.getFirstName() : "Student") +
                    (s.getLastName() != null ? (" " + s.getLastName()) : "");
            String parentName = s.getFatherName() != null ? ("Mr./Ms. " + s.getFatherName()) : "Parent";

            String prompt = String.format(
                    "Draft a supportive email to %s regarding %s's performance. " +
                    "Attendance is approximately %.0f%% and they are currently failing the target subject. " +
                    "Use a concerned but respectful tone and propose a parent-teacher meeting next week.",
                    parentName, studentName, pct != null ? pct : 0.0);

            ChatRequest req = ChatRequest.builder()
                    .message(prompt)
                    .provider(null) // Use default provider in chat service
                    .model(null)
                    .temperature(0.4)
                    .maxTokens(600)
                    .userId("system-manager")
                    .build();
            ChatResponse resp = chatIntegrationService.sendMessage(req);
            String body = (resp != null && resp.getError() == null) ? resp.getResponse() :
                    ("Dear " + parentName + ",\n\n" +
                            "We are concerned about " + studentName + "'s recent attendance and academic performance. " +
                            "We would like to schedule a meeting to discuss a supportive plan.\n\nRegards,\nSchool");
            drafts.add(body);
        }

        // 5) Build final report
        drafts.sort(Comparator.comparingInt(String::length));
        String report;
        if (atRisk.isEmpty()) {
            report = String.format(
                    "Project Complete:\n- Found %d students with low attendance (< %d%%).\n- None with failing grades for the selected subject.",
                    lowAttendance.size(), attendanceThreshold);
        } else {
            report = String.format(
                    "Project Complete:\n- Found %d students with low attendance (< %d%%).\n- Identified %d at-risk students (also failing the subject).\n- Drafted %d parent emails (not yet sent).",
                    lowAttendance.size(), attendanceThreshold, atRisk.size(), drafts.size());
        }

        log.info("At-Risk analysis completed: {} at-risk students", atRisk.size());
        return report;
    }

    /**
     * MCP Tool entrypoint so the LLM agent can invoke this workflow directly.
     * Parameters are nullable-friendly for tool invocation; applies a default threshold of 80 when not provided.
     */
    @Tool(description = "Run At-Risk Student analysis for a class. Inputs: classId (Long), attendanceThreshold (Integer, optional, default 80), subjectId (Long). Returns a summary report string.")
    public String atRiskAnalysisTool(Long classId, Integer attendanceThreshold, Long subjectId) {
        int threshold = attendanceThreshold != null ? attendanceThreshold : 80;
        return runAtRiskAnalysis(classId, threshold, subjectId);
    }
}
