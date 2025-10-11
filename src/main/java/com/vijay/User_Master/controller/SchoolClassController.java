package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.SchoolClassRequest;
import com.vijay.User_Master.dto.SchoolClassResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.service.SchoolClassService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/school-classes")
@RequiredArgsConstructor
@Slf4j
public class SchoolClassController {
    
    private final SchoolClassService schoolClassService;
    
    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody SchoolClassRequest request) {
        log.info("Creating class: {}", request.getClassName());
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SchoolClassResponse response = schoolClassService.createClass(request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all classes - page: {}, size: {}", page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        PageableResponse<SchoolClassResponse> response = schoolClassService.getAllClasses(ownerId, page, size, sortBy, sortDir);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        log.info("Getting class by ID: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SchoolClassResponse response = schoolClassService.getClassById(id, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClass(@PathVariable Long id, @RequestBody SchoolClassRequest request) {
        log.info("Updating class: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SchoolClassResponse response = schoolClassService.updateClass(id, request, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        log.info("Deleting class: {}", id);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        schoolClassService.deleteClass(id, ownerId);
        return ExceptionUtil.createBuildResponse("Class deleted successfully", HttpStatus.OK);
    }
    
    @GetMapping("/active")
    public ResponseEntity<?> getActiveClasses() {
        log.info("Getting active classes");
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<SchoolClassResponse> response = schoolClassService.getActiveClasses(ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> getClassesByTeacher(@PathVariable Long teacherId) {
        log.info("Getting classes for teacher: {}", teacherId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<SchoolClassResponse> response = schoolClassService.getClassesByTeacher(teacherId, ownerId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PostMapping("/{classId}/subjects")
    public ResponseEntity<?> assignSubjectsToClass(@PathVariable Long classId, @RequestBody List<Long> subjectIds) {
        log.info("Assigning {} subjects to class: {}", subjectIds.size(), classId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        schoolClassService.assignSubjectsToClass(classId, subjectIds, ownerId);
        return ExceptionUtil.createBuildResponse("Subjects assigned to class successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{classId}/subjects/{subjectId}")
    public ResponseEntity<?> removeSubjectFromClass(@PathVariable Long classId, @PathVariable Long subjectId) {
        log.info("Removing subject {} from class: {}", subjectId, classId);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        schoolClassService.removeSubjectFromClass(classId, subjectId, ownerId);
        return ExceptionUtil.createBuildResponse("Subject removed from class successfully", HttpStatus.OK);
    }
}