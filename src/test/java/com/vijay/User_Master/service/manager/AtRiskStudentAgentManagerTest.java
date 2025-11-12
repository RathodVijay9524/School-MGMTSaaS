package com.vijay.User_Master.service.manager;

import com.vijay.User_Master.dto.GradeResponse;
import com.vijay.User_Master.entity.Grade;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AttendanceService;
import com.vijay.User_Master.service.GradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtRiskStudentAgentManagerTest {

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private GradeService gradeService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @InjectMocks
    private AtRiskStudentAgentManager manager;

    @Test
    void runAtRiskAnalysis_returnsClassNotFoundResult() {
        Long classId = 101L;
        when(schoolClassRepository.findById(classId)).thenReturn(Optional.empty());

        AtRiskStudentAgentManager.Result result = manager.runAtRiskAnalysis(classId, 80, 22L);

        assertEquals("Class not found", result.getFinalReport());
        assertTrue(result.getLowAttendanceStudentIds().isEmpty());
        assertTrue(result.getAtRiskStudentIds().isEmpty());
        assertTrue(result.getEmailDrafts().isEmpty());
    }

    @Test
    void runAtRiskAnalysis_identifiesAtRiskStudentAndDraftsEmail() {
        Long classId = 202L;
        Long ownerId = 303L;
        Long subjectId = 404L;
        Long studentId = 505L;

        User owner = User.builder().id(ownerId).build();
        SchoolClass schoolClass = SchoolClass.builder()
                .id(classId)
                .className("Class 10")
                .section("A")
                .owner(owner)
                .build();

        Role studentRole = new Role();
        studentRole.setName("ROLE_STUDENT");

        Worker student = Worker.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Smith")
                .fatherName("Doe")
                .currentClass(schoolClass)
                .roles(Set.of(studentRole))
                .owner(owner)
                .build();

        when(schoolClassRepository.findById(classId)).thenReturn(Optional.of(schoolClass));
        when(workerRepository.findByOwner_IdAndIsDeletedFalse(ownerId)).thenReturn(List.of(student));
        when(attendanceService.calculateAttendancePercentage(studentId)).thenReturn(70.0);
        when(gradeService.getGradesByStudentAndSubject(studentId, subjectId))
                .thenReturn(List.of(GradeResponse.builder().status(Grade.GradeStatus.FAIL).build()));

        AtRiskStudentAgentManager.Result result = manager.runAtRiskAnalysis(classId, 80, subjectId);

        assertEquals(List.of(studentId), result.getLowAttendanceStudentIds());
        assertEquals(List.of(studentId), result.getAtRiskStudentIds());
        assertEquals(1, result.getEmailDrafts().size());
        String expectedDraft = "Dear Mr./Ms. Doe,\n\n" +
                "We are reaching out regarding John Smith's attendance, currently around 70%, " +
                "and recent academic performance in the selected subject. We recommend scheduling a meeting " +
                "with the class teacher next week to discuss additional support.\n\n" +
                "Regards,\nSchool Administration";
        assertEquals(expectedDraft, result.getEmailDrafts().get(0));
        assertTrue(result.getFinalReport().contains("Identified 1 at-risk students"));
    }
}

