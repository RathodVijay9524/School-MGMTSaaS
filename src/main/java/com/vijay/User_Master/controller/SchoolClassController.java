package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.SchoolClassRequest;
import com.vijay.User_Master.dto.SchoolClassResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.service.SchoolClassService;
import com.vijay.User_Master.Helper.CommonUtils;
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
    public ResponseEntity<SchoolClassResponse> createClass(@RequestBody SchoolClassRequest request) {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SchoolClassResponse response = schoolClassService.createClass(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<PageableResponse<SchoolClassResponse>> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        PageableResponse<SchoolClassResponse> response = schoolClassService.getAllClasses(ownerId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SchoolClassResponse> getClassById(@PathVariable Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SchoolClassResponse response = schoolClassService.getClassById(id, ownerId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SchoolClassResponse> updateClass(@PathVariable Long id, @RequestBody SchoolClassRequest request) {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        SchoolClassResponse response = schoolClassService.updateClass(id, request, ownerId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClass(@PathVariable Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        schoolClassService.deleteClass(id, ownerId);
        return ResponseEntity.ok("Class deleted successfully");
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<SchoolClassResponse>> getActiveClasses() {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<SchoolClassResponse> response = schoolClassService.getActiveClasses(ownerId);
        return ResponseEntity.ok(response);
    }
}