package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.SchoolClassRequest;
import com.vijay.User_Master.dto.SchoolClassResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.SchoolClassService;
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
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .academicYear(request.getAcademicYear())
                .classLevel(1) // Set default class level
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
        schoolClass.setDescription(request.getDescription());
        schoolClass.setCapacity(request.getCapacity());
        schoolClass.setAcademicYear(request.getAcademicYear());
        
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
                .description(schoolClass.getDescription())
                .capacity(schoolClass.getCapacity())
                .academicYear(schoolClass.getAcademicYear())
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