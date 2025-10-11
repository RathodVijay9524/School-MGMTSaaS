package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.SubjectRequest;
import com.vijay.User_Master.dto.SubjectResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.service.SubjectService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Slf4j
public class SubjectController {

    private final SubjectService subjectService;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;

    @PostMapping
    public ResponseEntity<?> createSubject(@Valid @RequestBody SubjectRequest request) {
        log.info("Creating subject: {}", request.getSubjectName());
        
        Long ownerId = getCorrectOwnerId();
        SubjectResponse response = subjectService.createSubject(request, ownerId);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(
            @PathVariable Long id, 
            @Valid @RequestBody SubjectRequest request) {
        log.info("Updating subject: {}", id);
        
        Long ownerId = getCorrectOwnerId();
        SubjectResponse response = subjectService.updateSubject(id, request, ownerId);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        log.info("Getting subject: {}", id);
        
        Long ownerId = getCorrectOwnerId();
        SubjectResponse response = subjectService.getSubjectById(id, ownerId);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Getting all subjects - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long ownerId = getCorrectOwnerId();
        PageableResponse<SubjectResponse> response = subjectService.getAllSubjects(ownerId, pageable);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveSubjects() {
        log.info("Getting all active subjects");
        
        Long ownerId = getCorrectOwnerId();
        List<SubjectResponse> response = subjectService.getAllActiveSubjects(ownerId);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSubjects(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching subjects with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Long ownerId = getCorrectOwnerId();
        PageableResponse<SubjectResponse> response = subjectService.searchSubjects(keyword, ownerId, pageable);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<?> getSubjectsByDepartment(@PathVariable String department) {
        log.info("Getting subjects by department: {}", department);
        
        Long ownerId = getCorrectOwnerId();
        List<SubjectResponse> response = subjectService.getSubjectsByDepartment(department, ownerId);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getSubjectsByType(@PathVariable Subject.SubjectType type) {
        log.info("Getting subjects by type: {}", type);
        
        Long ownerId = getCorrectOwnerId();
        List<SubjectResponse> response = subjectService.getSubjectsByType(type, ownerId);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getSubjectsByClass(@PathVariable Long classId) {
        log.info("Getting subjects for class: {}", classId);
        
        Long ownerId = getCorrectOwnerId();
        List<SubjectResponse> response = subjectService.getSubjectsByClass(classId, ownerId);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        log.info("Deleting subject: {}", id);
        
        Long ownerId = getCorrectOwnerId();
        subjectService.deleteSubject(id, ownerId);
        
        return ExceptionUtil.createBuildResponse("Subject deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreSubject(@PathVariable Long id) {
        log.info("Restoring subject: {}", id);
        
        Long ownerId = getCorrectOwnerId();
        subjectService.restoreSubject(id, ownerId);
        
        return ExceptionUtil.createBuildResponse("Subject restored successfully", HttpStatus.OK);
    }

    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting deleted subjects");
        
        Pageable pageable = PageRequest.of(page, size);
        Long ownerId = getCorrectOwnerId();
        PageableResponse<SubjectResponse> response = subjectService.getDeletedSubjects(ownerId, pageable);
        
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    /**
     * Get the correct owner ID for the logged-in user.
     * If the logged-in user is a worker (like a student), return their owner's ID.
     * If the logged-in user is a direct owner, return their own ID.
     */
    private Long getCorrectOwnerId() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long userId = loggedInUser.getId();
        
        // Check if the logged-in user is a worker
        Worker worker = workerRepository.findById(userId).orElse(null);
        if (worker != null && worker.getOwner() != null) {
            // User is a worker, return their owner's ID
            Long ownerId = worker.getOwner().getId();
            log.info("Logged-in user is worker ID: {}, returning owner ID: {}", userId, ownerId);
            return ownerId;
        }
        
        // User is a direct owner, return their own ID
        log.info("Logged-in user is direct owner ID: {}", userId);
        return userId;
    }
}
