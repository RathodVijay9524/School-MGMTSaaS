package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.SubjectRequest;
import com.vijay.User_Master.dto.SubjectResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.service.SubjectService;
import com.vijay.User_Master.Helper.CommonUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Slf4j
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectRequest request) {
        log.info("Creating subject: {}", request.getSubjectName());
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubjectResponse response = subjectService.createSubject(request, ownerId);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long id, 
            @Valid @RequestBody SubjectRequest request) {
        log.info("Updating subject: {}", id);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubjectResponse response = subjectService.updateSubject(id, request, ownerId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable Long id) {
        log.info("Getting subject: {}", id);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SubjectResponse response = subjectService.getSubjectById(id, ownerId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<SubjectResponse>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all subjects - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        PageableResponse<SubjectResponse> response = subjectService.getAllSubjects(ownerId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubjectResponse>> getAllActiveSubjects() {
        log.info("Getting all active subjects");
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<SubjectResponse> response = subjectService.getAllActiveSubjects(ownerId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PageableResponse<SubjectResponse>> searchSubjects(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching subjects with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        PageableResponse<SubjectResponse> response = subjectService.searchSubjects(keyword, ownerId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<SubjectResponse>> getSubjectsByDepartment(@PathVariable String department) {
        log.info("Getting subjects by department: {}", department);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<SubjectResponse> response = subjectService.getSubjectsByDepartment(department, ownerId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SubjectResponse>> getSubjectsByType(@PathVariable Subject.SubjectType type) {
        log.info("Getting subjects by type: {}", type);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<SubjectResponse> response = subjectService.getSubjectsByType(type, ownerId);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        log.info("Deleting subject: {}", id);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        subjectService.deleteSubject(id, ownerId);
        
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restoreSubject(@PathVariable Long id) {
        log.info("Restoring subject: {}", id);
        
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        subjectService.restoreSubject(id, ownerId);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deleted")
    public ResponseEntity<PageableResponse<SubjectResponse>> getDeletedSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting deleted subjects");
        
        Pageable pageable = PageRequest.of(page, size);
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        PageableResponse<SubjectResponse> response = subjectService.getDeletedSubjects(ownerId, pageable);
        
        return ResponseEntity.ok(response);
    }
}
