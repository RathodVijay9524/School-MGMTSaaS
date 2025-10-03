package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.AssignmentRequest;
import com.vijay.User_Master.dto.AssignmentResponse;
import com.vijay.User_Master.dto.AssignmentStatistics;
import com.vijay.User_Master.entity.Assignment;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import org.springframework.ai.tool.annotation.Tool;
import com.vijay.User_Master.repository.AssignmentRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Assignment management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final WorkerRepository workerRepository;

    @Override
    @Tool(name = "createAssignment", description = "Create a new assignment with title, description, subject, class, due date and total marks")
    public AssignmentResponse createAssignment(AssignmentRequest request, Long ownerId) {
        log.info("Creating assignment: {} for owner: {}", request.getTitle(), ownerId);
        
        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Validate subject exists and belongs to owner
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getSubjectId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Subject not found or does not belong to owner"));
        
        // Validate class exists and belongs to owner
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getClassId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found or does not belong to owner"));
        
        // Validate teacher exists and belongs to owner
        Worker teacher = workerRepository.findById(request.getTeacherId())
                .filter(w -> w.getOwner() != null && w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new RuntimeException("Teacher not found or does not belong to owner"));
        
        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(subject)
                .schoolClass(schoolClass)
                .assignedBy(teacher)
                .assignmentType(request.getAssignmentType())
                .assignedDate(request.getAssignedDate())
                .dueDate(request.getDueDate())
                .totalMarks(request.getTotalMarks())
                .status(request.getStatus())
                .attachmentUrl(request.getAttachmentUrl())
                .instructions(request.getInstructions())
                .submissionsCount(request.getSubmissionsCount() != null ? request.getSubmissionsCount() : 0)
                .totalStudents(request.getTotalStudents())
                .allowLateSubmission(request.isAllowLateSubmission())
                .latePenaltyPercentage(request.getLatePenaltyPercentage())
                .notes(request.getNotes())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment created successfully with ID: {}", savedAssignment.getId());
        
        return convertToResponse(savedAssignment);
    }

    @Override
    @Tool(name = "updateAssignment", description = "Update assignment details")
    public AssignmentResponse updateAssignment(Long id, AssignmentRequest request, Long ownerId) {
        log.info("Updating assignment: {} for owner: {}", id, ownerId);
        
        Assignment assignment = assignmentRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        // Update fields
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setAssignmentType(request.getAssignmentType());
        assignment.setAssignedDate(request.getAssignedDate());
        assignment.setDueDate(request.getDueDate());
        assignment.setTotalMarks(request.getTotalMarks());
        assignment.setStatus(request.getStatus());
        assignment.setAttachmentUrl(request.getAttachmentUrl());
        assignment.setInstructions(request.getInstructions());
        assignment.setSubmissionsCount(request.getSubmissionsCount());
        assignment.setTotalStudents(request.getTotalStudents());
        assignment.setAllowLateSubmission(request.isAllowLateSubmission());
        assignment.setLatePenaltyPercentage(request.getLatePenaltyPercentage());
        assignment.setNotes(request.getNotes());
        
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment updated successfully");
        
        return convertToResponse(updatedAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long id, Long ownerId) {
        log.info("Getting assignment: {} for owner: {}", id, ownerId);
        
        Assignment assignment = assignmentRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        return convertToResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentResponse> getAllAssignments(Long ownerId, Pageable pageable) {
        log.info("Getting all assignments for owner: {}", ownerId);
        
        Page<Assignment> assignments = assignmentRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        return assignments.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentResponse> getAssignmentsByClass(Long classId, Long ownerId, Pageable pageable) {
        log.info("Getting assignments for class: {} and owner: {}", classId, ownerId);
        
        Page<Assignment> assignments = assignmentRepository.findBySchoolClass_IdAndIsDeletedFalse(classId, pageable);
        return assignments.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsBySubject(Long subjectId, Long ownerId) {
        log.info("Getting assignments for subject: {} and owner: {}", subjectId, ownerId);
        
        List<Assignment> assignments = assignmentRepository.findBySubject_IdAndIsDeletedFalse(subjectId);
        return assignments.stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId, Long ownerId) {
        log.info("Getting assignments for teacher: {} and owner: {}", teacherId, ownerId);
        
        List<Assignment> assignments = assignmentRepository.findByAssignedBy_IdAndIsDeletedFalse(teacherId);
        return assignments.stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentResponse> getAssignmentsByStatus(Assignment.AssignmentStatus status, Long ownerId, Pageable pageable) {
        log.info("Getting assignments with status: {} for owner: {}", status, ownerId);
        
        Page<Assignment> assignments = assignmentRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, status, pageable);
        return assignments.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByType(Assignment.AssignmentType type, Long ownerId) {
        log.info("Getting assignments with type: {} for owner: {}", type, ownerId);
        
        List<Assignment> assignments = assignmentRepository.findByAssignmentTypeAndIsDeletedFalse(type);
        return assignments.stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getOverdueAssignments(Long ownerId) {
        log.info("Getting overdue assignments for owner: {}", ownerId);
        
        List<Assignment> assignments = assignmentRepository.findOverdueAssignments(LocalDateTime.now());
        return assignments.stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getUpcomingAssignments(Long ownerId) {
        log.info("Getting upcoming assignments for owner: {}", ownerId);
        
        List<Assignment> assignments = assignmentRepository.findUpcomingAssignments(LocalDateTime.now());
        return assignments.stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Long ownerId) {
        log.info("Getting assignments between {} and {} for owner: {}", startDate, endDate, ownerId);
        
        List<Assignment> assignments = assignmentRepository.findByDueDateBetweenAndIsDeletedFalse(startDate, endDate);
        return assignments.stream()
                .filter(a -> a.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentResponse> searchAssignments(String keyword, Long ownerId, Pageable pageable) {
        log.info("Searching assignments with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Assignment> assignments = assignmentRepository.searchByOwner(ownerId, keyword, pageable);
        return assignments.map(this::convertToResponse);
    }

    @Override
    public void deleteAssignment(Long id, Long ownerId) {
        log.info("Deleting assignment: {} for owner: {}", id, ownerId);
        
        Assignment assignment = assignmentRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        assignment.setDeleted(true);
        assignmentRepository.save(assignment);
        
        log.info("Assignment deleted successfully");
    }

    @Override
    public void restoreAssignment(Long id, Long ownerId) {
        log.info("Restoring assignment: {} for owner: {}", id, ownerId);
        
        Assignment assignment = assignmentRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        assignment.setDeleted(false);
        assignmentRepository.save(assignment);
        
        log.info("Assignment restored successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentStatistics getAssignmentStatistics(Long ownerId) {
        log.info("Getting assignment statistics for owner: {}", ownerId);
        
        long totalAssignments = assignmentRepository.countByOwner_IdAndIsDeletedFalse(ownerId);
        
        // Count by status
        long assignedCount = assignmentRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Assignment.AssignmentStatus.ASSIGNED, Pageable.unpaged()).getTotalElements();
        long inProgressCount = assignmentRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Assignment.AssignmentStatus.IN_PROGRESS, Pageable.unpaged()).getTotalElements();
        long submittedCount = assignmentRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Assignment.AssignmentStatus.SUBMITTED, Pageable.unpaged()).getTotalElements();
        long gradedCount = assignmentRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Assignment.AssignmentStatus.GRADED, Pageable.unpaged()).getTotalElements();
        long overdueCount = getOverdueAssignments(ownerId).size();
        long cancelledCount = assignmentRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Assignment.AssignmentStatus.CANCELLED, Pageable.unpaged()).getTotalElements();
        
        // Count by type
        long homeworkCount = getAssignmentsByType(Assignment.AssignmentType.HOMEWORK, ownerId).size();
        long projectCount = getAssignmentsByType(Assignment.AssignmentType.PROJECT, ownerId).size();
        long essayCount = getAssignmentsByType(Assignment.AssignmentType.ESSAY, ownerId).size();
        long practicalCount = getAssignmentsByType(Assignment.AssignmentType.PRACTICAL, ownerId).size();
        long presentationCount = getAssignmentsByType(Assignment.AssignmentType.PRESENTATION, ownerId).size();
        long groupProjectCount = getAssignmentsByType(Assignment.AssignmentType.GROUP_PROJECT, ownerId).size();
        long quizCount = getAssignmentsByType(Assignment.AssignmentType.QUIZ, ownerId).size();
        
        return AssignmentStatistics.builder()
                .totalAssignments(totalAssignments)
                .assignedCount(assignedCount)
                .inProgressCount(inProgressCount)
                .submittedCount(submittedCount)
                .gradedCount(gradedCount)
                .overdueCount(overdueCount)
                .cancelledCount(cancelledCount)
                .homeworkCount(homeworkCount)
                .projectCount(projectCount)
                .essayCount(essayCount)
                .practicalCount(practicalCount)
                .presentationCount(presentationCount)
                .groupProjectCount(groupProjectCount)
                .quizCount(quizCount)
                .build();
    }

    private AssignmentResponse convertToResponse(Assignment assignment) {
        LocalDateTime now = LocalDateTime.now();
        boolean isOverdue = assignment.getDueDate().isBefore(now) && 
                           (assignment.getStatus() == Assignment.AssignmentStatus.ASSIGNED || 
                            assignment.getStatus() == Assignment.AssignmentStatus.IN_PROGRESS);
        boolean isUpcoming = assignment.getDueDate().isAfter(now) && 
                            assignment.getStatus() == Assignment.AssignmentStatus.ASSIGNED;
        
        long daysUntilDue = ChronoUnit.DAYS.between(now, assignment.getDueDate());
        long daysOverdue = isOverdue ? ChronoUnit.DAYS.between(assignment.getDueDate(), now) : 0;
        
        double submissionPercentage = assignment.getTotalStudents() != null && assignment.getTotalStudents() > 0 
                ? (double) assignment.getSubmissionsCount() / assignment.getTotalStudents() * 100 
                : 0.0;
        
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .subjectId(assignment.getSubject().getId())
                .subjectName(assignment.getSubject().getSubjectName())
                .classId(assignment.getSchoolClass().getId())
                .className(assignment.getSchoolClass().getClassName())
                .teacherId(assignment.getAssignedBy().getId())
                .teacherName(assignment.getAssignedBy().getName())
                .assignmentType(assignment.getAssignmentType())
                .assignedDate(assignment.getAssignedDate())
                .dueDate(assignment.getDueDate())
                .totalMarks(assignment.getTotalMarks())
                .status(assignment.getStatus())
                .attachmentUrl(assignment.getAttachmentUrl())
                .instructions(assignment.getInstructions())
                .submissionsCount(assignment.getSubmissionsCount())
                .totalStudents(assignment.getTotalStudents())
                .allowLateSubmission(assignment.isAllowLateSubmission())
                .latePenaltyPercentage(assignment.getLatePenaltyPercentage())
                .notes(assignment.getNotes())
                .isDeleted(assignment.isDeleted())
                .createdOn(assignment.getCreatedOn())
                .updatedOn(assignment.getUpdatedOn())
                .isOverdue(isOverdue)
                .isUpcoming(isUpcoming)
                .daysUntilDue(daysUntilDue)
                .daysOverdue(daysOverdue)
                .statusDisplay(assignment.getStatus().name().replace("_", " "))
                .typeDisplay(assignment.getAssignmentType().name().replace("_", " "))
                .submissionPercentage(submissionPercentage)
                .ownerName(assignment.getOwner().getName())
                .build();
    }
}
