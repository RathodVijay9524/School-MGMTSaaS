package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SubjectRequest;
import com.vijay.User_Master.dto.SubjectResponse;
import com.vijay.User_Master.dto.PageableResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubjectService {

    SubjectResponse createSubject(SubjectRequest request, Long ownerId);
    
    SubjectResponse updateSubject(Long id, SubjectRequest request, Long ownerId);
    
    SubjectResponse getSubjectById(Long id, Long ownerId);
    
    PageableResponse<SubjectResponse> getAllSubjects(Long ownerId, Pageable pageable);
    
    List<SubjectResponse> getAllActiveSubjects(Long ownerId);
    
    PageableResponse<SubjectResponse> searchSubjects(String keyword, Long ownerId, Pageable pageable);
    
    List<SubjectResponse> getSubjectsByDepartment(String department, Long ownerId);
    
    List<SubjectResponse> getSubjectsByType(com.vijay.User_Master.entity.Subject.SubjectType type, Long ownerId);
    
    List<SubjectResponse> getSubjectsByClass(Long classId, Long ownerId);
    
    void deleteSubject(Long id, Long ownerId);
    
    void restoreSubject(Long id, Long ownerId);
    
    PageableResponse<SubjectResponse> getDeletedSubjects(Long ownerId, Pageable pageable);
}
