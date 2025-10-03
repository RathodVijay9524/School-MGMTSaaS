package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.GradeRequest;
import com.vijay.User_Master.dto.GradeResponse;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import org.springframework.ai.tool.annotation.Tool;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.GradeService;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final WorkerRepository workerRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<GradeResponse> getAllGrades(Pageable pageable) {
        log.info("Fetching all grades with pagination");
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        // Use owner-based query
        Page<Grade> gradePage = gradeRepository.findByOwner_Id(ownerId, pageable);
        
        return gradePage.map(this::mapToResponse);
    }

    @Override
    @Tool(name = "createGrade", description = "Create a new grade for a student with marks, percentage, letter grade and academic details")
    public GradeResponse createGrade(GradeRequest request) {
        log.info("Creating grade for student ID: {}", request.getStudentId());
        
        // Validate marks
        if (request.getMarksObtained() > request.getTotalMarks()) {
            throw new BadApiRequestException("Marks obtained cannot exceed total marks");
        }
        
        // Get entities
        Worker student = workerRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
            
        Subject subject = subjectRepository.findById(request.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        
        Exam exam = null;
        if (request.getExamId() != null) {
            exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", request.getExamId()));
        }
        
        Assignment assignment = null;
        if (request.getAssignmentId() != null) {
            assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", request.getAssignmentId()));
        }
        
        Worker gradedBy = null;
        if (request.getGradedByTeacherId() != null) {
            gradedBy = workerRepository.findById(request.getGradedByTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getGradedByTeacherId()));
        }
        
        // Calculate percentage
        Double percentage = (request.getMarksObtained() / request.getTotalMarks()) * 100.0;
        
        // Determine letter grade
        String letterGrade = request.getLetterGrade() != null 
            ? request.getLetterGrade() 
            : calculateLetterGrade(percentage);
        
        // Calculate grade point (assuming 4.0 scale)
        Double gradePoint = calculateGradePoint(percentage);
        
        // Determine pass/fail status
        Grade.GradeStatus status = percentage >= (subject.getPassingMarks() * 100.0 / subject.getTotalMarks())
            ? Grade.GradeStatus.PASS 
            : Grade.GradeStatus.FAIL;
        
        // Create grade
        // Get the current logged-in user as owner
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
            .orElseThrow(() -> new RuntimeException("Owner user not found"));
        
        Grade grade = Grade.builder()
            .student(student)
            .subject(subject)
            .exam(exam)
            .assignment(assignment)
            .gradedBy(gradedBy)
            .marksObtained(request.getMarksObtained())
            .totalMarks(request.getTotalMarks())
            .percentage(percentage)
            .letterGrade(letterGrade)
            .semester(request.getSemester())
            .academicYear(request.getAcademicYear())
            .remarks(request.getRemarks())
            .owner(owner) // Set the owner for multi-tenancy
            .build();
        Grade savedGrade = gradeRepository.save(grade);
        log.info("Grade created successfully with ID: {}", savedGrade.getId());
        
        return mapToResponse(savedGrade);
    }

    @Override
    @Tool(name = "updateGrade", description = "Update grade details")
    public GradeResponse updateGrade(Long id, GradeRequest request) {
        log.info("Updating grade with ID: {}", id);
        
        Grade grade = gradeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
        
        // Validate marks
        if (request.getMarksObtained() > request.getTotalMarks()) {
            throw new BadApiRequestException("Marks obtained cannot exceed total marks");
        }
        
        // Calculate percentage
        Double percentage = (request.getMarksObtained() / request.getTotalMarks()) * 100.0;
        String letterGrade = calculateLetterGrade(percentage);
        
        grade.setMarksObtained(request.getMarksObtained());
        grade.setTotalMarks(request.getTotalMarks());
        grade.setPercentage(percentage);
        grade.setLetterGrade(letterGrade);
        grade.setFeedback(request.getFeedback());
        grade.setRemarks(request.getRemarks());
        grade.setPublished(request.isPublished());
        
        Grade updated = gradeRepository.save(grade);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
        return mapToResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeResponse> getGradesByStudent(Long studentId, Pageable pageable) {
        return gradeRepository.findByStudent_Id(studentId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByStudentAndSubject(Long studentId, Long subjectId) {
        return gradeRepository.findByStudent_IdAndSubject_Id(studentId, subjectId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByStudentAndSemester(Long studentId, String semester) {
        return gradeRepository.findByStudent_IdAndSemester(studentId, semester).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getPublishedGrades(Long studentId) {
        return gradeRepository.findPublishedGrades(studentId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(description = "Calculate the overall GPA for a specific student")
    public Double calculateStudentGPA(Long studentId) {
        Double gpa = gradeRepository.calculateOverallGPA(studentId);
        return gpa != null ? gpa : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateSubjectAverage(Long studentId, Long subjectId) {
        Double average = gradeRepository.calculateAveragePercentage(studentId, subjectId);
        return average != null ? average : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getFailingGrades(Long studentId) {
        return gradeRepository.findFailingGrades(studentId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void publishGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
        grade.setPublished(true);
        gradeRepository.save(grade);
    }

    @Override
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
        gradeRepository.delete(grade);
    }

    // Helper Methods
    
    private String calculateLetterGrade(Double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B+";
        else if (percentage >= 60) return "B";
        else if (percentage >= 50) return "C";
        else if (percentage >= 40) return "D";
        else return "F";
    }
    
    private GradeResponse mapToResponse(Grade grade) {
        return GradeResponse.builder()
            .id(grade.getId())
            .studentId(grade.getStudent().getId())
            .studentName(grade.getStudent().getName())
            .admissionNumber(grade.getStudent().getUsername())
            .subjectId(grade.getSubject().getId())
            .subjectName(grade.getSubject().getSubjectName())
            .examId(grade.getExam() != null ? grade.getExam().getId() : null)
            .examName(grade.getExam() != null ? grade.getExam().getExamName() : null)
            .assignmentId(grade.getAssignment() != null ? grade.getAssignment().getId() : null)
            .assignmentTitle(grade.getAssignment() != null ? grade.getAssignment().getTitle() : null)
            .gradeType(grade.getGradeType())
            .marksObtained(grade.getMarksObtained())
            .totalMarks(grade.getTotalMarks())
            .percentage(grade.getPercentage())
            .letterGrade(grade.getLetterGrade())
            .status(grade.getStatus())
            .semester(grade.getSemester())
            .academicYear(grade.getAcademicYear())
            .gradeDate(grade.getGradeDate())
            .gradedByTeacherId(grade.getGradedBy() != null ? grade.getGradedBy().getId() : null)
            .gradedByTeacherName(grade.getGradedBy() != null ? grade.getGradedBy().getName() : null)
            .feedback(grade.getFeedback())
            .remarks(grade.getRemarks())
            .isPublished(grade.isPublished())
            .gradeTypeDisplay(grade.getGradeType().toString())
            .statusDisplay(grade.getStatus().toString())
            .isPassed(grade.getStatus() == Grade.GradeStatus.PASS)
            .build();
    }
    
    private Long getCurrentOwnerId() {
        // Get the logged-in user ID for multi-tenancy
        return 1L; // For now, using karina's ID. In real implementation, get from security context
    }
    
    private Double calculateGradePoint(Double percentage) {
        if (percentage >= 90) return 4.0;
        if (percentage >= 80) return 3.7;
        if (percentage >= 70) return 3.3;
        if (percentage >= 60) return 3.0;
        if (percentage >= 50) return 2.7;
        if (percentage >= 40) return 2.3;
        if (percentage >= 30) return 2.0;
        return 0.0;
    }
}

