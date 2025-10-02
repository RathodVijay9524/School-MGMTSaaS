package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.TransferCertificateRequest;
import com.vijay.User_Master.dto.TransferCertificateResponse;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.entity.TransferCertificate;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.AttendanceRepository;
import com.vijay.User_Master.repository.FeeRepository;
import com.vijay.User_Master.repository.GradeRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.TransferCertificateRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.TransferCertificateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class TransferCertificateServiceImpl implements TransferCertificateService {

    private final TransferCertificateRepository tcRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final FeeRepository feeRepository;

    @Override
    public TransferCertificateResponse generateTC(TransferCertificateRequest request) {
        log.info("Generating TC for student ID: {}", request.getStudentId());
        
        // Check if TC already exists for student
        if (tcRepository.existsByStudent_Id(request.getStudentId())) {
            throw new BadApiRequestException("Transfer Certificate already exists for this student");
        }
        
        Worker student = workerRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        
        User issuedBy = null;
        if (request.getIssuedByUserId() != null) {
            issuedBy = userRepository.findById(request.getIssuedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getIssuedByUserId()));
        }
        
        // AUTOMATIC DATA COLLECTION
        String tcNumber = generateTCNumber();
        String attendancePercentage = calculateAttendancePercentage(request.getStudentId());
        String overallGPA = calculateOverallGPA(request.getStudentId());
        String overallGrade = calculateOverallGrade(request.getStudentId());
        boolean feeCleared = checkFeesClearance(request.getStudentId());
        Double pendingFees = getPendingFeesAmount(request.getStudentId());
        boolean libraryCleared = true; // TODO: Implement library clearance check
        
        TransferCertificate tc = TransferCertificate.builder()
            .student(student)
            .tcNumber(tcNumber)
            .issueDate(LocalDate.now())
            .lastAttendanceDate(request.getLastAttendanceDate())
            .reasonForLeaving(request.getReasonForLeaving())
            .reasonDetails(request.getReasonDetails())
            .lastClassStudied(student.getCurrentClass().getClassName() + " - " + student.getSection())
            .lastClassPassed(request.getLastClassPassed())
            .dateOfPromotion(request.getDateOfPromotion())
            .academicYearOfLeaving(request.getAcademicYearOfLeaving())
            .attendancePercentage(attendancePercentage)
            .overallGPA(overallGPA)
            .overallGrade(overallGrade)
            .conduct(request.getConduct())
            .conductRemarks(request.getConductRemarks())
            .characterRemarks(request.getCharacterRemarks())
            .feeCleared(feeCleared)
            .pendingFeeAmount(pendingFees)
            .libraryCleared(libraryCleared)
            .generalRemarks(request.getGeneralRemarks())
            .achievements(request.getAchievements())
            .issuedBy(issuedBy)
            .status(TransferCertificate.TCStatus.DRAFT)
            .isDeleted(false)
            .build();
        
        TransferCertificate savedTC = tcRepository.save(tc);
        log.info("TC generated successfully with number: {}", tcNumber);
        
        return mapToResponse(savedTC);
    }

    @Override
    public TransferCertificateResponse approveTC(Long id, Long approvedByUserId) {
        log.info("Approving TC with ID: {}", id);
        
        TransferCertificate tc = tcRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TransferCertificate", "id", id));
        
        User approvedBy = userRepository.findById(approvedByUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", approvedByUserId));
        
        tc.setApprovedBy(approvedBy);
        tc.setApprovalDate(LocalDate.now());
        tc.setStatus(TransferCertificate.TCStatus.APPROVED);
        
        TransferCertificate updated = tcRepository.save(tc);
        return mapToResponse(updated);
    }

    @Override
    public TransferCertificateResponse issueTC(Long id) {
        log.info("Issuing TC with ID: {}", id);
        
        TransferCertificate tc = tcRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TransferCertificate", "id", id));
        
        if (tc.getStatus() != TransferCertificate.TCStatus.APPROVED) {
            throw new BadApiRequestException("TC must be approved before issuing");
        }
        
        tc.setStatus(TransferCertificate.TCStatus.ISSUED);
        
        // Mark student as TRANSFERRED
        Worker student = tc.getStudent();
        student.setStatus("TRANSFERRED");
        workerRepository.save(student);
        
        TransferCertificate updated = tcRepository.save(tc);
        return mapToResponse(updated);
    }

    @Override
    public String generateTCPDF(Long id) {
        log.info("Generating TC PDF for ID: {}", id);
        // TODO: Implement PDF generation using iText or JasperReports
        return "/pdfs/tc/" + id + ".pdf";
    }

    @Override
    @Transactional(readOnly = true)
    public TransferCertificateResponse getTCById(Long id) {
        TransferCertificate tc = tcRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TransferCertificate", "id", id));
        return mapToResponse(tc);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferCertificateResponse getTCByStudentId(Long studentId) {
        TransferCertificate tc = tcRepository.findByStudent_Id(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("TransferCertificate", "studentId", studentId));
        return mapToResponse(tc);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferCertificateResponse getTCByNumber(String tcNumber) {
        TransferCertificate tc = tcRepository.findByTcNumber(tcNumber)
            .orElseThrow(() -> new ResourceNotFoundException("TransferCertificate", "tcNumber", tcNumber));
        return mapToResponse(tc);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferCertificateResponse> getAllTCs(Pageable pageable) {
        log.info("Fetching all TCs with pagination");
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        // Use owner-based query
        Page<TransferCertificate> tcPage = tcRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        
        return tcPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferCertificateResponse> getPendingApprovals() {
        return tcRepository.findPendingApprovals().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void cancelTC(Long id, String reason) {
        TransferCertificate tc = tcRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TransferCertificate", "id", id));
        tc.setStatus(TransferCertificate.TCStatus.CANCELLED);
        tc.setGeneralRemarks(tc.getGeneralRemarks() + " | Cancelled: " + reason);
        tcRepository.save(tc);
    }

    @Override
    public void deleteTC(Long id) {
        TransferCertificate tc = tcRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TransferCertificate", "id", id));
        tcRepository.delete(tc);
    }

    // AUTOMATIC DATA GENERATION HELPERS
    
    private String generateTCNumber() {
        int year = LocalDate.now().getYear();
        long count = tcRepository.count() + 1;
        return String.format("TC/%d/%04d", year, count);
    }
    
    private String calculateAttendancePercentage(Long studentId) {
        Double percentage = attendanceRepository.calculateAttendancePercentage(studentId);
        return percentage != null ? String.format("%.2f%%", percentage) : "N/A";
    }
    
    private String calculateOverallGPA(Long studentId) {
        Double gpa = gradeRepository.calculateOverallGPA(studentId);
        return gpa != null ? String.format("%.2f", gpa) : "N/A";
    }
    
    private String calculateOverallGrade(Long studentId) {
        Double gpa = gradeRepository.calculateOverallGPA(studentId);
        if (gpa == null) return "N/A";
        if (gpa >= 90) return "A+";
        else if (gpa >= 80) return "A";
        else if (gpa >= 70) return "B+";
        else if (gpa >= 60) return "B";
        else if (gpa >= 50) return "C";
        else return "D";
    }
    
    private boolean checkFeesClearance(Long studentId) {
        List<com.vijay.User_Master.entity.Fee> pendingFees = feeRepository.findPendingFees(studentId);
        return pendingFees.isEmpty();
    }
    
    private Double getPendingFeesAmount(Long studentId) {
        Worker student = workerRepository.findById(studentId).orElse(null);
        return student != null ? student.getFeesBalance() : 0.0;
    }
    
    private TransferCertificateResponse mapToResponse(TransferCertificate tc) {
        boolean canBeIssued = tc.isFeeCleared() && tc.isLibraryCleared() 
            && tc.getStatus() == TransferCertificate.TCStatus.APPROVED;
        
        return TransferCertificateResponse.builder()
            .id(tc.getId())
            .studentId(tc.getStudent().getId())
            .studentName(tc.getStudent().getFirstName() + " " + tc.getStudent().getLastName())
            .admissionNumber(tc.getStudent().getAdmissionNumber())
            .tcNumber(tc.getTcNumber())
            .issueDate(tc.getIssueDate())
            .lastAttendanceDate(tc.getLastAttendanceDate())
            .reasonForLeaving(tc.getReasonForLeaving())
            .reasonDetails(tc.getReasonDetails())
            .lastClassStudied(tc.getLastClassStudied())
            .lastClassPassed(tc.getLastClassPassed())
            .academicYearOfLeaving(tc.getAcademicYearOfLeaving())
            .attendancePercentage(tc.getAttendancePercentage())
            .overallGPA(tc.getOverallGPA())
            .overallGrade(tc.getOverallGrade())
            .conduct(tc.getConduct())
            .conductRemarks(tc.getConductRemarks())
            .characterRemarks(tc.getCharacterRemarks())
            .feeCleared(tc.isFeeCleared())
            .pendingFeeAmount(tc.getPendingFeeAmount())
            .libraryCleared(tc.isLibraryCleared())
            .pendingBooks(tc.getPendingBooks())
            .generalRemarks(tc.getGeneralRemarks())
            .achievements(tc.getAchievements())
            .status(tc.getStatus())
            .approvalDate(tc.getApprovalDate())
            .pdfUrl(tc.getPdfUrl())
            .issuedByUserId(tc.getIssuedBy() != null ? tc.getIssuedBy().getId() : null)
            .issuedByName(tc.getIssuedBy() != null ? tc.getIssuedBy().getUsername() : null)
            .approvedByUserId(tc.getApprovedBy() != null ? tc.getApprovedBy().getId() : null)
            .approvedByName(tc.getApprovedBy() != null ? tc.getApprovedBy().getUsername() : null)
            .canBeIssued(canBeIssued)
            .statusDisplay(tc.getStatus().toString())
            .build();
    }
    
    private Long getCurrentOwnerId() {
        // Get the logged-in user ID for multi-tenancy
        return 1L; // For now, using karina's ID. In real implementation, get from security context
    }
}


            .build();
    }
    
    private Long getCurrentOwnerId() {
        // Get the logged-in user ID for multi-tenancy
        return 1L; // For now, using karina's ID. In real implementation, get from security context
    }
}

