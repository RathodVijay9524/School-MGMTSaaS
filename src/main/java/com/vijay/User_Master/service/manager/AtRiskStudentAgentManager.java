package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.config.chat.AiToolProvider;
import com.vijay.User_Master.entity.Grade;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AttendanceService;
import com.vijay.User_Master.service.GradeService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orchestrates querying attendance and grade services to surface at-risk students
 * without delegating to any external chat integration. Generates deterministic
 * email drafts using a simple template so MCP tooling can consume the outputs.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AtRiskStudentAgentManager implements AiToolProvider {

    private final AttendanceService attendanceService;
    private final GradeService gradeService;
    private final WorkerRepository workerRepository;
    private final SchoolClassRepository schoolClassRepository;

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

    public Result runAtRiskAnalysis(Long classId, int attendanceThreshold, Long subjectId) {
        log.info("Running At-Risk Student analysis for classId={}, threshold={}, subjectId={}",
                classId, attendanceThreshold, subjectId);

        SchoolClass schoolClass = schoolClassRepository.findById(classId).orElse(null);
        if (schoolClass == null) {
            return Result.builder()
                    .classId(classId)
                    .attendanceThreshold(attendanceThreshold)
                    .subjectId(subjectId)
                    .finalReport("Class not found")
                    .lowAttendanceStudentIds(List.of())
                    .atRiskStudentIds(List.of())
                    .emailDrafts(List.of())
                    .build();
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
            return Result.builder()
                    .classId(classId)
                    .attendanceThreshold(attendanceThreshold)
                    .subjectId(subjectId)
                    .finalReport("Project Complete: No students found in the selected class.")
                    .lowAttendanceStudentIds(List.of())
                    .atRiskStudentIds(List.of())
                    .emailDrafts(List.of())
                    .build();
        }

        List<Worker> lowAttendance = classStudents.stream()
                .filter(w -> {
                    Double pct = attendanceService.calculateAttendancePercentage(w.getId());
                    return pct != null && pct < attendanceThreshold;
                })
                .collect(Collectors.toList());

        if (lowAttendance.isEmpty()) {
            String report = String.format(
                    "Project Complete:\n- Found %d students in class %s-%s.\n- None below %d%% attendance.",
                    classStudents.size(), schoolClass.getClassName(), schoolClass.getSection(), attendanceThreshold);
            return Result.builder()
                    .classId(classId)
                    .attendanceThreshold(attendanceThreshold)
                    .subjectId(subjectId)
                    .finalReport(report)
                    .lowAttendanceStudentIds(List.of())
                    .atRiskStudentIds(List.of())
                    .emailDrafts(List.of())
                    .build();
        }

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

        List<String> drafts = new ArrayList<>();
        for (Worker student : atRisk) {
            Double pct = attendanceService.calculateAttendancePercentage(student.getId());
            drafts.add(buildParentEmail(student, pct));
        }
        drafts.sort(Comparator.comparingInt(String::length));

        String report;
        if (atRisk.isEmpty()) {
            report = String.format(
                    "Project Complete:\n- Found %d students with low attendance (< %d%%).\n- None with failing grades for the selected subject.",
                    lowAttendance.size(), attendanceThreshold);
        } else {
            report = String.format(
                    "Project Complete:\n- Found %d students with low attendance (< %d%%).\n- Identified %d at-risk students (also failing the subject).\n- Prepared %d parent email drafts.",
                    lowAttendance.size(), attendanceThreshold, atRisk.size(), drafts.size());
        }

        log.info("At-Risk analysis completed: {} at-risk students", atRisk.size());
        return Result.builder()
                .classId(classId)
                .attendanceThreshold(attendanceThreshold)
                .subjectId(subjectId)
                .lowAttendanceStudentIds(lowAttendance.stream().map(Worker::getId).toList())
                .atRiskStudentIds(atRisk.stream().map(Worker::getId).toList())
                .emailDrafts(drafts)
                .finalReport(report)
                .build();
    }

    @Tool(description = "Run At-Risk Student analysis for a class. Inputs: classId (Long), attendanceThreshold (Integer, optional, default 80), subjectId (Long). Returns a summary report string.")
    public String atRiskAnalysisTool(Long classId, Integer attendanceThreshold, Long subjectId) {
        int threshold = attendanceThreshold != null ? attendanceThreshold : 80;
        Result result = runAtRiskAnalysis(classId, threshold, subjectId);
        return result.getFinalReport();
    }

    private String buildParentEmail(Worker student, Double attendancePct) {
        String studentName = (student.getFirstName() != null ? student.getFirstName() : "Student") +
                (student.getLastName() != null ? " " + student.getLastName() : "");
        String parentName = student.getFatherName() != null ? "Mr./Ms. " + student.getFatherName() : "Parent";
        double pct = attendancePct != null ? attendancePct : 0.0;
        return String.format(
                "Dear %s,\n\n" +
                        "We are reaching out regarding %s's attendance, currently around %.0f%%, " +
                        "and recent academic performance in the selected subject. We recommend scheduling a meeting " +
                        "with the class teacher next week to discuss additional support.\n\n" +
                        "Regards,\nSchool Administration",
                parentName, studentName, pct);
    }
}
