package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.SchoolClassRequest;
import com.vijay.User_Master.dto.SchoolClassResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.SchoolClassService;
import org.springframework.ai.tool.annotation.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolClassServiceImpl implements SchoolClassService {
    
    private final SchoolClassRepository schoolClassRepository;
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
                .classLevel(request.getClassLevel())
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
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        schoolClass.setClassName(request.getClassName());
        schoolClass.setSection(request.getSection());
        schoolClass.setClassLevel(request.getClassLevel());
        schoolClass.setCapacity(request.getCapacity());
        schoolClass.setRoomNumber(request.getRoomNumber());
        schoolClass.setAcademicYear(request.getAcademicYear());
        schoolClass.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            schoolClass.setStatus(SchoolClass.ClassStatus.valueOf(request.getStatus()));
        }
        
        SchoolClass updatedClass = schoolClassRepository.save(schoolClass);
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
                .academicYear(schoolClass.getAcademicYear())
                .description(schoolClass.getDescription())
                .status(schoolClass.getStatus() != null ? schoolClass.getStatus().toString() : null)
                .isActive(schoolClass.isActive())
                .createdOn(schoolClass.getCreatedOn() != null ? 
                    schoolClass.getCreatedOn().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                    null)
                .updatedOn(schoolClass.getUpdatedOn() != null ? 
                    schoolClass.getUpdatedOn().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
                    null)
                .build();
    }
}