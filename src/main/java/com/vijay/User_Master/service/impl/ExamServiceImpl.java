package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.ExamRequest;
import com.vijay.User_Master.dto.ExamResponse;
import com.vijay.User_Master.dto.ExamStatistics;
import com.vijay.User_Master.entity.Exam;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import org.springframework.ai.tool.annotation.Tool;
import com.vijay.User_Master.repository.ExamRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.ExamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Exam management
 */
@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final WorkerRepository workerRepository;

    @Override
    @Tool(name = "createExam", description = "Create a new exam with name, code, subject, class, date, time and total marks")
    public ExamResponse createExam(ExamRequest request, Long ownerId) {
        log.info("Creating exam: {} for owner: {}", request.getExamName(), ownerId);
        
        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Check if exam code already exists for this owner
        if (examRepository.existsByExamCodeAndOwner_Id(request.getExamCode(), ownerId)) {
            throw new RuntimeException("Exam code already exists: " + request.getExamCode());
        }
        
        // Validate subject exists and belongs to owner
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getSubjectId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Subject not found or does not belong to owner"));
        
        // Validate class exists and belongs to owner
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getClassId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found or does not belong to owner"));
        
        // Validate supervisor exists and belongs to owner (if provided)
        Worker supervisor = null;
        if (request.getSupervisorId() != null) {
            supervisor = workerRepository.findById(request.getSupervisorId())
                    .filter(w -> w.getOwner() != null && w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                    .orElseThrow(() -> new RuntimeException("Supervisor not found or does not belong to owner"));
        }
        
        Exam exam = Exam.builder()
                .examName(request.getExamName())
                .examCode(request.getExamCode())
                .examType(request.getExamType())
                .subject(subject)
                .schoolClass(schoolClass)
                .examDate(request.getExamDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationMinutes(request.getDurationMinutes())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .roomNumber(request.getRoomNumber())
                .supervisor(supervisor)
                .status(request.getStatus())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .instructions(request.getInstructions())
                .syllabus(request.getSyllabus())
                .resultsPublished(request.isResultsPublished())
                .resultPublishDate(request.getResultPublishDate())
                .notes(request.getNotes())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Exam savedExam = examRepository.save(exam);
        log.info("Exam created successfully with ID: {}", savedExam.getId());
        
        return convertToResponse(savedExam);
    }

    @Override
    @Tool(name = "updateExam", description = "Update exam details")
    public ExamResponse updateExam(Long id, ExamRequest request, Long ownerId) {
        log.info("Updating exam: {} for owner: {}", id, ownerId);
        
        Exam exam = examRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        // Update fields
        exam.setExamName(request.getExamName());
        exam.setExamCode(request.getExamCode());
        exam.setExamType(request.getExamType());
        exam.setExamDate(request.getExamDate());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setTotalMarks(request.getTotalMarks());
        exam.setPassingMarks(request.getPassingMarks());
        exam.setRoomNumber(request.getRoomNumber());
        exam.setStatus(request.getStatus());
        exam.setSemester(request.getSemester());
        exam.setAcademicYear(request.getAcademicYear());
        exam.setInstructions(request.getInstructions());
        exam.setSyllabus(request.getSyllabus());
        exam.setResultsPublished(request.isResultsPublished());
        exam.setResultPublishDate(request.getResultPublishDate());
        exam.setNotes(request.getNotes());
        
        Exam updatedExam = examRepository.save(exam);
        log.info("Exam updated successfully");
        
        return convertToResponse(updatedExam);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getExamById", description = "Get exam details by exam ID")
    public ExamResponse getExamById(Long id, Long ownerId) {
        log.info("Getting exam: {} for owner: {}", id, ownerId);
        
        Exam exam = examRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        return convertToResponse(exam);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getAllExams", description = "Get all exams for owner with pagination")
    public Page<ExamResponse> getAllExams(Long ownerId, Pageable pageable) {
        log.info("Getting all exams for owner: {}", ownerId);
        
        Page<Exam> exams = examRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        return exams.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponse> getExamsByClass(Long classId, Long ownerId, Pageable pageable) {
        log.info("Getting exams for class: {} and owner: {}", classId, ownerId);
        
        Page<Exam> exams = examRepository.findByOwner_IdAndSchoolClass_IdAndIsDeletedFalse(ownerId, classId, pageable);
        return exams.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsBySubject(Long subjectId, Long ownerId) {
        log.info("Getting exams for subject: {} and owner: {}", subjectId, ownerId);
        
        List<Exam> exams = examRepository.findByOwner_IdAndSubject_IdAndIsDeletedFalse(ownerId, subjectId);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByType(Exam.ExamType examType, Long ownerId) {
        log.info("Getting exams for type: {} and owner: {}", examType, ownerId);
        
        List<Exam> exams = examRepository.findByOwner_IdAndExamTypeAndIsDeletedFalse(ownerId, examType);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponse> getExamsByStatus(Exam.ExamStatus status, Long ownerId, Pageable pageable) {
        log.info("Getting exams for status: {} and owner: {}", status, ownerId);
        
        Page<Exam> exams = examRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, status, pageable);
        return exams.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByDateRange(LocalDate startDate, LocalDate endDate, Long ownerId) {
        log.info("Getting exams for date range: {} to {} for owner: {}", startDate, endDate, ownerId);
        
        List<Exam> exams = examRepository.findByOwner_IdAndExamDateBetweenAndIsDeletedFalse(ownerId, startDate, endDate);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getUpcomingExams(Long ownerId) {
        log.info("Getting upcoming exams for owner: {}", ownerId);
        
        List<Exam> exams = examRepository.findUpcomingExamsByOwner(ownerId, LocalDate.now());
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getOverdueExams(Long ownerId) {
        log.info("Getting overdue exams for owner: {}", ownerId);
        
        List<Exam> exams = examRepository.findOverdueExamsByOwner(ownerId, LocalDate.now());
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsBySemester(String semester, Long ownerId) {
        log.info("Getting exams for semester: {} and owner: {}", semester, ownerId);
        
        List<Exam> exams = examRepository.findByOwner_IdAndSemesterAndIsDeletedFalse(ownerId, semester);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByAcademicYear(String academicYear, Long ownerId) {
        log.info("Getting exams for academic year: {} and owner: {}", academicYear, ownerId);
        
        List<Exam> exams = examRepository.findByOwner_IdAndAcademicYearAndIsDeletedFalse(ownerId, academicYear);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsBySupervisor(Long supervisorId, Long ownerId) {
        log.info("Getting exams for supervisor: {} and owner: {}", supervisorId, ownerId);
        
        List<Exam> exams = examRepository.findByOwner_IdAndSupervisor_IdAndIsDeletedFalse(ownerId, supervisorId);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByDate(LocalDate examDate, Long ownerId) {
        log.info("Getting exams for date: {} and owner: {}", examDate, ownerId);
        
        List<Exam> exams = examRepository.findByOwner_IdAndExamDateAndIsDeletedFalse(ownerId, examDate);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponse> searchExams(String keyword, Long ownerId, Pageable pageable) {
        log.info("Searching exams with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Exam> exams = examRepository.searchByOwner(ownerId, keyword, pageable);
        return exams.map(this::convertToResponse);
    }

    @Override
    @Tool(name = "deleteExam", description = "Soft delete an exam")
    public void deleteExam(Long id, Long ownerId) {
        log.info("Deleting exam: {} for owner: {}", id, ownerId);
        
        Exam exam = examRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        exam.setDeleted(true);
        examRepository.save(exam);
        
        log.info("Exam deleted successfully");
    }

    @Override
    public void restoreExam(Long id, Long ownerId) {
        log.info("Restoring exam: {} for owner: {}", id, ownerId);
        
        Exam exam = examRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        exam.setDeleted(false);
        examRepository.save(exam);
        
        log.info("Exam restored successfully");
    }

    @Override
    @Tool(name = "publishExamResults", description = "Publish exam results")
    public ExamResponse publishExamResults(Long id, Long ownerId) {
        log.info("Publishing results for exam: {} for owner: {}", id, ownerId);
        
        Exam exam = examRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        exam.setResultsPublished(true);
        exam.setResultPublishDate(LocalDate.now());
        exam.setStatus(Exam.ExamStatus.COMPLETED);
        
        Exam updatedExam = examRepository.save(exam);
        log.info("Exam results published successfully");
        
        return convertToResponse(updatedExam);
    }

    @Override
    public ExamResponse cancelExam(Long id, String reason, Long ownerId) {
        log.info("Cancelling exam: {} for owner: {}", id, ownerId);
        
        Exam exam = examRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        exam.setStatus(Exam.ExamStatus.CANCELLED);
        exam.setNotes(exam.getNotes() + "\nCancelled: " + reason);
        
        Exam updatedExam = examRepository.save(exam);
        log.info("Exam cancelled successfully");
        
        return convertToResponse(updatedExam);
    }

    @Override
    public ExamResponse rescheduleExam(Long id, LocalDate newDate, Long ownerId) {
        log.info("Rescheduling exam: {} to date: {} for owner: {}", id, newDate, ownerId);
        
        Exam exam = examRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        LocalDate oldDate = exam.getExamDate();
        exam.setExamDate(newDate);
        exam.setStatus(Exam.ExamStatus.RESCHEDULED);
        exam.setNotes(exam.getNotes() + "\nRescheduled from " + oldDate + " to " + newDate);
        
        Exam updatedExam = examRepository.save(exam);
        log.info("Exam rescheduled successfully");
        
        return convertToResponse(updatedExam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamStatistics getExamStatistics(Long ownerId) {
        log.info("Getting exam statistics for owner: {}", ownerId);
        
        long totalExams = examRepository.countByOwner_IdAndIsDeletedFalse(ownerId);
        
        // Count by status
        long scheduledExams = examRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Exam.ExamStatus.SCHEDULED, Pageable.unpaged()).getTotalElements();
        long ongoingExams = examRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Exam.ExamStatus.ONGOING, Pageable.unpaged()).getTotalElements();
        long completedExams = examRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Exam.ExamStatus.COMPLETED, Pageable.unpaged()).getTotalElements();
        long cancelledExams = examRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Exam.ExamStatus.CANCELLED, Pageable.unpaged()).getTotalElements();
        long postponedExams = examRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Exam.ExamStatus.POSTPONED, Pageable.unpaged()).getTotalElements();
        long rescheduledExams = examRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Exam.ExamStatus.RESCHEDULED, Pageable.unpaged()).getTotalElements();
        
        // Count by exam type
        List<Exam> allExams = examRepository.findByOwner_IdAndIsDeletedFalse(ownerId, Pageable.unpaged()).getContent();
        long midtermExams = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.MIDTERM).count();
        long finalExams = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.FINAL).count();
        long unitTests = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.UNIT_TEST).count();
        long quarterlyExams = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.QUARTERLY).count();
        long halfYearlyExams = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.HALF_YEARLY).count();
        long annualExams = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.ANNUAL).count();
        long surpriseTests = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.SURPRISE_TEST).count();
        long practicalExams = allExams.stream().filter(e -> e.getExamType() == Exam.ExamType.PRACTICAL).count();
        
        // Count results published
        long examsWithResultsPublished = allExams.stream().filter(Exam::isResultsPublished).count();
        long examsWithoutResultsPublished = allExams.stream().filter(e -> !e.isResultsPublished()).count();
        
        // Count upcoming and overdue
        long upcomingExamsCount = examRepository.findUpcomingExamsByOwner(ownerId, LocalDate.now()).size();
        long overdueExamsCount = examRepository.findOverdueExamsByOwner(ownerId, LocalDate.now()).size();
        
        // Calculate averages
        double averageExamDuration = allExams.stream()
                .filter(e -> e.getDurationMinutes() != null)
                .mapToInt(Exam::getDurationMinutes)
                .average()
                .orElse(0.0);
        
        double averageTotalMarks = allExams.stream()
                .filter(e -> e.getTotalMarks() != null)
                .mapToDouble(Exam::getTotalMarks)
                .average()
                .orElse(0.0);
        
        return ExamStatistics.builder()
                .totalExams(totalExams)
                .scheduledExams(scheduledExams)
                .ongoingExams(ongoingExams)
                .completedExams(completedExams)
                .cancelledExams(cancelledExams)
                .postponedExams(postponedExams)
                .rescheduledExams(rescheduledExams)
                .midtermExams(midtermExams)
                .finalExams(finalExams)
                .unitTests(unitTests)
                .quarterlyExams(quarterlyExams)
                .halfYearlyExams(halfYearlyExams)
                .annualExams(annualExams)
                .surpriseTests(surpriseTests)
                .practicalExams(practicalExams)
                .examsWithResultsPublished(examsWithResultsPublished)
                .examsWithoutResultsPublished(examsWithoutResultsPublished)
                .upcomingExamsCount(upcomingExamsCount)
                .overdueExamsCount(overdueExamsCount)
                .averageExamDuration(averageExamDuration)
                .averageTotalMarks(averageTotalMarks)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamCalendar(LocalDate month, Long ownerId) {
        log.info("Getting exam calendar for month: {} and owner: {}", month, ownerId);
        
        LocalDate startOfMonth = month.withDayOfMonth(1);
        LocalDate endOfMonth = month.withDayOfMonth(month.lengthOfMonth());
        
        List<Exam> exams = examRepository.findByOwner_IdAndExamDateBetweenAndIsDeletedFalse(ownerId, startOfMonth, endOfMonth);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private ExamResponse convertToResponse(Exam exam) {
        LocalDate today = LocalDate.now();
        boolean isUpcoming = exam.getExamDate().isAfter(today) && exam.getStatus() == Exam.ExamStatus.SCHEDULED;
        boolean isOngoing = exam.getStatus() == Exam.ExamStatus.ONGOING;
        boolean isCompleted = exam.getStatus() == Exam.ExamStatus.COMPLETED;
        boolean isOverdue = exam.getExamDate().isBefore(today) && exam.getStatus() == Exam.ExamStatus.SCHEDULED;
        
        long daysUntilExam = exam.getExamDate().isAfter(today) ? 
                ChronoUnit.DAYS.between(today, exam.getExamDate()) : 0;
        long daysOverdue = isOverdue ? ChronoUnit.DAYS.between(exam.getExamDate(), today) : 0;
        
        String durationDisplay = exam.getDurationMinutes() != null ? 
                String.format("%d minutes", exam.getDurationMinutes()) : "Not specified";
        
        String supervisorDisplay = exam.getSupervisor() != null ? 
                exam.getSupervisor().getName() : "Not assigned";
        
        return ExamResponse.builder()
                .id(exam.getId())
                .examName(exam.getExamName())
                .examCode(exam.getExamCode())
                .examType(exam.getExamType())
                .subjectId(exam.getSubject().getId())
                .subjectName(exam.getSubject().getSubjectName())
                .classId(exam.getSchoolClass().getId())
                .className(exam.getSchoolClass().getClassName())
                .examDate(exam.getExamDate())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .durationMinutes(exam.getDurationMinutes())
                .totalMarks(exam.getTotalMarks())
                .passingMarks(exam.getPassingMarks())
                .roomNumber(exam.getRoomNumber())
                .supervisorId(exam.getSupervisor() != null ? exam.getSupervisor().getId() : null)
                .supervisorName(supervisorDisplay)
                .status(exam.getStatus())
                .semester(exam.getSemester())
                .academicYear(exam.getAcademicYear())
                .instructions(exam.getInstructions())
                .syllabus(exam.getSyllabus())
                .resultsPublished(exam.isResultsPublished())
                .resultPublishDate(exam.getResultPublishDate())
                .notes(exam.getNotes())
                .isDeleted(exam.isDeleted())
                .createdOn(exam.getCreatedOn())
                .updatedOn(exam.getUpdatedOn())
                .isUpcoming(isUpcoming)
                .isOngoing(isOngoing)
                .isCompleted(isCompleted)
                .isOverdue(isOverdue)
                .daysUntilExam(daysUntilExam)
                .daysOverdue(daysOverdue)
                .statusDisplay(exam.getStatus().name().replace("_", " "))
                .typeDisplay(exam.getExamType().name().replace("_", " "))
                .durationDisplay(durationDisplay)
                .supervisorDisplay(supervisorDisplay)
                .build();
    }
}
