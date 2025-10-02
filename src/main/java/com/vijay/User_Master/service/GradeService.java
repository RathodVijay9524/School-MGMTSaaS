package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.GradeRequest;
import com.vijay.User_Master.dto.GradeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GradeService {

    GradeResponse createGrade(GradeRequest request);
    
    GradeResponse updateGrade(Long id, GradeRequest request);
    
    GradeResponse getGradeById(Long id);
    
    Page<GradeResponse> getGradesByStudent(Long studentId, Pageable pageable);
    
    List<GradeResponse> getGradesByStudentAndSubject(Long studentId, Long subjectId);
    
    List<GradeResponse> getGradesByStudentAndSemester(Long studentId, String semester);
    
    List<GradeResponse> getPublishedGrades(Long studentId);
    
    Double calculateStudentGPA(Long studentId);
    
    Double calculateSubjectAverage(Long studentId, Long subjectId);
    
    List<GradeResponse> getFailingGrades(Long studentId);
    
    void publishGrade(Long id);
    
    void deleteGrade(Long id);
}

