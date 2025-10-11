package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.SubjectRequest;
import com.vijay.User_Master.dto.SubjectResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.SubjectService;
import org.springframework.ai.tool.annotation.Tool;
// import com.vijay.User_Master.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;

    @Override
    @Tool(name = "createSubject", description = "Create a new subject with code, name, description, credits and department")
    public SubjectResponse createSubject(SubjectRequest request, Long ownerId) {
        log.info("Creating subject: {} for owner: {}", request.getSubjectName(), ownerId);
        
        // Get the owner user
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Check if subject code already exists
        if (subjectRepository.existsBySubjectCodeAndOwner_Id(request.getSubjectCode(), ownerId)) {
            throw new RuntimeException("Subject code already exists: " + request.getSubjectCode());
        }
        
        Subject subject = Subject.builder()
                .subjectCode(request.getSubjectCode())
                .subjectName(request.getSubjectName())
                .description(request.getDescription())
                .type(request.getType() != null ? request.getType() : Subject.SubjectType.CORE)
                .credits(request.getCredits())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .department(request.getDepartment())
                .status(request.getStatus() != null ? request.getStatus() : Subject.SubjectStatus.ACTIVE)
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Subject savedSubject = subjectRepository.save(subject);
        log.info("Subject created successfully with ID: {}", savedSubject.getId());
        
        return convertToResponse(savedSubject);
    }

    @Override
    public SubjectResponse updateSubject(Long id, SubjectRequest request, Long ownerId) {
        log.info("Updating subject: {} for owner: {}", id, ownerId);
        
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        
        // Check if subject code already exists for other subjects (excluding current subject)
        if (!subject.getSubjectCode().equals(request.getSubjectCode()) && 
            subjectRepository.existsBySubjectCodeAndOwner_Id(request.getSubjectCode(), ownerId)) {
            throw new RuntimeException("Subject code already exists: " + request.getSubjectCode());
        }
        
        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        subject.setDescription(request.getDescription());
        subject.setType(request.getType());
        subject.setCredits(request.getCredits());
        subject.setTotalMarks(request.getTotalMarks());
        subject.setPassingMarks(request.getPassingMarks());
        subject.setDepartment(request.getDepartment());
        subject.setStatus(request.getStatus());
        
        Subject updatedSubject = subjectRepository.save(subject);
        log.info("Subject updated successfully: {}", updatedSubject.getId());
        
        return convertToResponse(updatedSubject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long id, Long ownerId) {
        log.info("Getting subject: {} for owner: {}", id, ownerId);
        
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        
        return convertToResponse(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<SubjectResponse> getAllSubjects(Long ownerId, Pageable pageable) {
        log.info("Getting all subjects for owner: {}", ownerId);
        
        Page<Subject> subjects = subjectRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        
        List<SubjectResponse> subjectResponses = subjects.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageableResponse.<SubjectResponse>builder()
                .content(subjectResponses)
                .pageNumber(subjects.getNumber())
                .pageSize(subjects.getSize())
                .totalElements(subjects.getTotalElements())
                .totalPages(subjects.getTotalPages())
                .lastPage(subjects.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllActiveSubjects(Long ownerId) {
        log.info("Getting all active subjects for owner: {}", ownerId);
        
        List<Subject> subjects = subjectRepository.findByOwner_IdAndIsDeletedFalse(ownerId);
        
        return subjects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<SubjectResponse> searchSubjects(String keyword, Long ownerId, Pageable pageable) {
        log.info("Searching subjects with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Subject> subjects = subjectRepository.searchSubjects(keyword, pageable);
        
        // Filter by owner after search
        List<Subject> ownerFilteredSubjects = subjects.getContent().stream()
                .filter(subject -> subject.getOwner() != null && subject.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
        
        List<SubjectResponse> subjectResponses = ownerFilteredSubjects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageableResponse.<SubjectResponse>builder()
                .content(subjectResponses)
                .pageNumber(subjects.getNumber())
                .pageSize(subjects.getSize())
                .totalElements((long) ownerFilteredSubjects.size())
                .totalPages((int) Math.ceil((double) ownerFilteredSubjects.size() / subjects.getSize()))
                .lastPage(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByDepartment(String department, Long ownerId) {
        log.info("Getting subjects by department: {} for owner: {}", department, ownerId);
        
        List<Subject> subjects = subjectRepository.findByDepartmentAndIsDeletedFalse(department);
        
        // Filter by owner
        return subjects.stream()
                .filter(subject -> subject.getOwner() != null && subject.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByType(Subject.SubjectType type, Long ownerId) {
        log.info("Getting subjects by type: {} for owner: {}", type, ownerId);
        
        List<Subject> subjects = subjectRepository.findByTypeAndIsDeletedFalse(type);
        
        // Filter by owner
        return subjects.stream()
                .filter(subject -> subject.getOwner() != null && subject.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubject(Long id, Long ownerId) {
        log.info("Deleting subject: {} for owner: {}", id, ownerId);
        
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        
        subject.setDeleted(true);
        subjectRepository.save(subject);
        
        log.info("Subject deleted successfully: {}", id);
    }

    @Override
    public void restoreSubject(Long id, Long ownerId) {
        log.info("Restoring subject: {} for owner: {}", id, ownerId);
        
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedTrue(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Deleted subject not found"));
        
        subject.setDeleted(false);
        subjectRepository.save(subject);
        
        log.info("Subject restored successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<SubjectResponse> getDeletedSubjects(Long ownerId, Pageable pageable) {
        log.info("Getting deleted subjects for owner: {}", ownerId);
        
        Page<Subject> subjects = subjectRepository.findByOwner_IdAndIsDeletedTrue(ownerId, pageable);
        
        List<SubjectResponse> subjectResponses = subjects.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageableResponse.<SubjectResponse>builder()
                .content(subjectResponses)
                .pageNumber(subjects.getNumber())
                .pageSize(subjects.getSize())
                .totalElements(subjects.getTotalElements())
                .totalPages(subjects.getTotalPages())
                .lastPage(subjects.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByClass(Long classId, Long ownerId) {
        log.info("Getting subjects for class: {} and owner: {}", classId, ownerId);
        
        // First verify the class exists and belongs to the owner
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(classId, ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Get subjects associated with this class
        List<Subject> subjects = new ArrayList<>(schoolClass.getSubjects());
        
        // Filter by owner and active status
        List<Subject> filteredSubjects = subjects.stream()
                .filter(subject -> subject.getOwner().getId().equals(ownerId) && !subject.isDeleted())
                .collect(Collectors.toList());
        
        return filteredSubjects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private SubjectResponse convertToResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .description(subject.getDescription())
                .type(subject.getType())
                .credits(subject.getCredits())
                .totalMarks(subject.getTotalMarks())
                .passingMarks(subject.getPassingMarks())
                .department(subject.getDepartment())
                .status(subject.getStatus())
                .fullSubjectName(subject.getSubjectCode() + " - " + subject.getSubjectName())
                .passingPercentage(subject.getTotalMarks() != null && subject.getPassingMarks() != null && subject.getTotalMarks() > 0 
                    ? (double) subject.getPassingMarks() / subject.getTotalMarks() * 100 
                    : null)
                .build();
    }
}
