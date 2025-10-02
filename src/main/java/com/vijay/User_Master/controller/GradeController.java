package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.GradeRequest;
import com.vijay.User_Master.dto.GradeResponse;
import com.vijay.User_Master.service.GradeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Grade & Assessment Management REST Controller
 */
@RestController
@RequestMapping("/api/v1/grades")
@AllArgsConstructor
@Slf4j
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> createGrade(@Valid @RequestBody GradeRequest request) {
        log.info("Creating grade for student ID: {}", request.getStudentId());
        GradeResponse response = gradeService.createGrade(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        log.info("Updating grade with ID: {}", id);
        GradeResponse response = gradeService.updateGrade(id, request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getGradeById(@PathVariable Long id) {
        GradeResponse response = gradeService.getGradeById(id);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getGradesByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("gradeDate").descending());
        Page<GradeResponse> response = gradeService.getGradesByStudent(studentId, pageable);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getGradesByStudentAndSubject(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        List<GradeResponse> response = gradeService.getGradesByStudentAndSubject(studentId, subjectId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getGradesByStudentAndSemester(
            @PathVariable Long studentId,
            @PathVariable String semester) {
        List<GradeResponse> response = gradeService.getGradesByStudentAndSemester(studentId, semester);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> getPublishedGrades(@PathVariable Long studentId) {
        List<GradeResponse> response = gradeService.getPublishedGrades(studentId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/gpa")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> calculateStudentGPA(@PathVariable Long studentId) {
        Double gpa = gradeService.calculateStudentGPA(studentId);
        return ExceptionUtil.createBuildResponse(new GPAResponse(studentId, gpa), HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<?> calculateSubjectAverage(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        Double average = gradeService.calculateSubjectAverage(studentId, subjectId);
        return ExceptionUtil.createBuildResponse(new AverageResponse(studentId, subjectId, average), HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/failing")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> getFailingGrades(@PathVariable Long studentId) {
        List<GradeResponse> response = gradeService.getFailingGrades(studentId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER', 'TEACHER')")
    public ResponseEntity<?> publishGrade(@PathVariable Long id) {
        gradeService.publishGrade(id);
        return ExceptionUtil.createBuildResponseMessage("Grade published successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ExceptionUtil.createBuildResponseMessage("Grade deleted successfully", HttpStatus.OK);
    }

    // Helper response classes
    private record GPAResponse(Long studentId, Double gpa) {}
    private record AverageResponse(Long studentId, Long subjectId, Double average) {}
}

