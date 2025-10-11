package com.vijay.User_Master.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vijay.User_Master.dto.SchoolClassRequest;
import com.vijay.User_Master.dto.SchoolClassResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.SchoolClassService;
import org.springframework.ai.tool.annotation.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolClassServiceImpl implements SchoolClassService {
    
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    
    @Override
    @Tool(name = "createSchoolClass", description = "Create a new school class with name, description, capacity and academic year")
    public SchoolClassResponse createClass(SchoolClassRequest request, Long ownerId) {
        // Get the owner user
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Check if class name already exists for this owner
        if (schoolClassRepository.existsByClassNameAndOwner_Id(request.getClassName(), ownerId)) {
            throw new RuntimeException("Class name already exists: " + request.getClassName());
        }
        
        SchoolClass schoolClass = SchoolClass.builder()
                .className(request.getClassName())
                .section(request.getSection())
                .classLevel(request.getClassLevel() != null ? request.getClassLevel() : 1)
                .capacity(request.getCapacity())
                .roomNumber(request.getRoomNumber())
                .academicYear(request.getAcademicYear())
                .description(request.getDescription())
                .owner(owner)
                .status(request.getStatus() != null ? SchoolClass.ClassStatus.valueOf(request.getStatus()) : SchoolClass.ClassStatus.ACTIVE)
                .isActive(true)
                .isDeleted(false)
                .build();
        
        SchoolClass savedClass = schoolClassRepository.save(schoolClass);
        return convertToResponse(savedClass);
    }
    
    @Override
    public PageableResponse<SchoolClassResponse> getAllClasses(Long ownerId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SchoolClass> classPage = schoolClassRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        
        List<SchoolClassResponse> content = classPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageableResponse.<SchoolClassResponse>builder()
                .content(content)
                .page(classPage.getNumber())
                .size(classPage.getSize())
                .totalElements(classPage.getTotalElements())
                .totalPages(classPage.getTotalPages())
                .first(classPage.isFirst())
                .last(classPage.isLast())
                .build();
    }
    
    @Override
    public SchoolClassResponse getClassById(Long id, Long ownerId) {
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        return convertToResponse(schoolClass);
    }
    
    @Override
    public SchoolClassResponse updateClass(Long id, SchoolClassRequest request, Long ownerId) {
        log.info("Attempting to update class with ID: {} for owner: {}", id, ownerId);
        log.info("Received status in request: {}", request.getStatus());
        
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        log.info("Class status before update: {}", schoolClass.getStatus());
        
        // Check if class name already exists for other classes (excluding current class)
        if (!schoolClass.getClassName().equals(request.getClassName()) && 
            schoolClassRepository.existsByClassNameAndOwner_Id(request.getClassName(), ownerId)) {
            throw new RuntimeException("Class name already exists: " + request.getClassName());
        }
        
        // Update all fields
        schoolClass.setClassName(request.getClassName());
        schoolClass.setSection(request.getSection());
        schoolClass.setClassLevel(request.getClassLevel());
        schoolClass.setCapacity(request.getCapacity());
        schoolClass.setRoomNumber(request.getRoomNumber());
        schoolClass.setAcademicYear(request.getAcademicYear());
        schoolClass.setDescription(request.getDescription());
        
        // Update status if provided
        if (request.getStatus() != null) {
            schoolClass.setStatus(SchoolClass.ClassStatus.valueOf(request.getStatus()));
            log.info("Class status after setting from request (before save): {}", schoolClass.getStatus());
        } else {
            log.warn("Status is null in request, keeping existing status: {}", schoolClass.getStatus());
        }
        
        SchoolClass updatedClass = schoolClassRepository.save(schoolClass);
        log.info("Class status after save: {}", updatedClass.getStatus());
        log.info("Class with ID: {} updated successfully.", updatedClass.getId());
        
        return convertToResponse(updatedClass);
    }
    
    @Override
    public void deleteClass(Long id, Long ownerId) {
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        schoolClass.setDeleted(true);
        schoolClassRepository.save(schoolClass);
    }
    
    @Override
    public List<SchoolClassResponse> getActiveClasses(Long ownerId) {
        List<SchoolClass> classes = schoolClassRepository.findByOwner_IdAndIsDeletedFalse(ownerId);
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private SchoolClassResponse convertToResponse(SchoolClass schoolClass) {
        return SchoolClassResponse.builder()
                .id(schoolClass.getId())
                .className(schoolClass.getClassName())
                .section(schoolClass.getSection())
                .classLevel(schoolClass.getClassLevel())
                .capacity(schoolClass.getCapacity())
                .roomNumber(schoolClass.getRoomNumber())
                .description(schoolClass.getDescription())
                .academicYear(schoolClass.getAcademicYear())
                .status(schoolClass.getStatus())
                .isActive(schoolClass.isActive())
                .createdOn(schoolClass.getCreatedOn() != null ? 
                    schoolClass.getCreatedOn().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                    null)
                .updatedOn(schoolClass.getUpdatedOn() != null ? 
                    schoolClass.getUpdatedOn().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                    null)
                .build();
    }

    @Override
    public List<SchoolClassResponse> getClassesByTeacher(Long teacherId, Long ownerId) {
        log.info("Getting classes for teacher: {} and owner: {}", teacherId, ownerId);
        
        List<SchoolClass> classes = schoolClassRepository.findByClassTeacher_IdAndIsDeletedFalse(teacherId);
        
        // Filter by owner (multi-tenancy)
        classes = classes.stream()
                .filter(schoolClass -> schoolClass.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
        
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignSubjectsToClass(Long classId, List<Long> subjectIds, Long ownerId) {
        log.info("Assigning {} subjects to class {} for owner {}", subjectIds.size(), classId, ownerId);
        
        // Verify class exists and belongs to owner
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(classId, ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Verify all subjects exist and belong to owner
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        if (subjects.size() != subjectIds.size()) {
            throw new RuntimeException("One or more subjects not found");
        }
        
        // Verify all subjects belong to the same owner
        subjects.forEach(subject -> {
            if (!subject.getOwner().getId().equals(ownerId)) {
                throw new RuntimeException("Subject does not belong to owner: " + subject.getSubjectName());
            }
        });
        
        // Get current subjects and add new ones
        Set<Subject> currentSubjects = schoolClass.getSubjects() != null ? schoolClass.getSubjects() : new HashSet<>();
        currentSubjects.addAll(subjects);
        
        // Update the class with new subjects
        schoolClass.setSubjects(currentSubjects);
        schoolClassRepository.save(schoolClass);
        
        log.info("Successfully assigned {} subjects to class {}", subjectIds.size(), classId);
    }

    @Override
    @Transactional
    public void removeSubjectFromClass(Long classId, Long subjectId, Long ownerId) {
        log.info("Removing subject {} from class {} for owner {}", subjectId, classId, ownerId);
        
        // Verify class exists and belongs to owner
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(classId, ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Get current subjects
        Set<Subject> currentSubjects = schoolClass.getSubjects();
        if (currentSubjects == null || currentSubjects.isEmpty()) {
            log.warn("Class {} has no subjects assigned", classId);
            return;
        }
        
        // Find and remove the subject
        Subject subjectToRemove = currentSubjects.stream()
                .filter(subject -> subject.getId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Subject not found in class"));
        
        currentSubjects.remove(subjectToRemove);
        schoolClass.setSubjects(currentSubjects);
        schoolClassRepository.save(schoolClass);
        
        log.info("Successfully removed subject {} from class {}", subjectId, classId);
    }
}