package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SchoolClassRequest;
import com.vijay.User_Master.dto.SchoolClassResponse;
import com.vijay.User_Master.dto.PageableResponse;

import java.util.List;

public interface SchoolClassService {
    SchoolClassResponse createClass(SchoolClassRequest request, Long ownerId);
    PageableResponse<SchoolClassResponse> getAllClasses(Long ownerId, int page, int size, String sortBy, String sortDir);
    SchoolClassResponse getClassById(Long id, Long ownerId);
    SchoolClassResponse updateClass(Long id, SchoolClassRequest request, Long ownerId);
    void deleteClass(Long id, Long ownerId);
    List<SchoolClassResponse> getActiveClasses(Long ownerId);
    List<SchoolClassResponse> getClassesByTeacher(Long teacherId, Long ownerId);
}