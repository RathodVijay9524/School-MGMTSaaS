package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.IDCardRequest;
import com.vijay.User_Master.dto.IDCardResponse;
import com.vijay.User_Master.entity.IDCard;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.IDCardRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.IDCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class IDCardServiceImpl implements IDCardService {

    private final IDCardRepository idCardRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    @Override
    public IDCardResponse generateStudentIDCard(Long studentId, LocalDate expiryDate) {
        log.info("Auto-generating ID card for student ID: {}", studentId);
        
        Worker student = workerRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        
        // Check if active card already exists
        Optional<IDCard> existingCard = idCardRepository.findActiveStudentCard(studentId);
        if (existingCard.isPresent()) {
            throw new BadApiRequestException("Active ID card already exists for this student");
        }
        
        // AUTOMATIC CARD NUMBER GENERATION
        String cardNumber = generateCardNumber("STU", studentId);
        String qrCode = generateQRCodeData(student);
        String barcodeData = student.getAdmissionNumber();
        
        IDCard idCard = IDCard.builder()
            .student(student)
            .cardType(IDCard.CardType.STUDENT_ID)
            .cardNumber(cardNumber)
            .issueDate(LocalDate.now())
            .expiryDate(expiryDate != null ? expiryDate : LocalDate.now().plusYears(1))
            .status(IDCard.CardStatus.ACTIVE)
            .photoUrl(student.getProfileImageUrl())
            .barcodeData(barcodeData)
            .qrCodeData(qrCode)
            .studentClass(student.getCurrentClass().getClassName())
            .section(student.getSection())
              .rollNumber(student.getRollNumber() != null ? Integer.parseInt(student.getRollNumber()) : null)
            .bloodGroup(student.getBloodGroup())
            .emergencyContactName(student.getFatherName())
            .emergencyContactPhone(student.getFatherPhone())
            .address(student.getAddress())
            .phoneNumber(student.getPhoneNumber())
            .dateOfBirth(student.getDateOfBirth())
            .isDeleted(false)
            .build();
        
        IDCard saved = idCardRepository.save(idCard);
        log.info("Student ID card generated with number: {}", cardNumber);
        
        return mapToResponse(saved);
    }

    @Override
    public IDCardResponse generateTeacherIDCard(Long teacherId, LocalDate expiryDate) {
        log.info("Auto-generating ID card for teacher ID: {}", teacherId);
        
        Worker teacher = workerRepository.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));
        
        // Check if active card already exists
        Optional<IDCard> existingCard = idCardRepository.findActiveTeacherCard(teacherId);
        if (existingCard.isPresent()) {
            throw new BadApiRequestException("Active ID card already exists for this teacher");
        }
        
        // AUTOMATIC CARD NUMBER GENERATION
        String cardNumber = generateCardNumber("TCH", teacherId);
        String qrCode = generateQRCodeData(teacher);
        String barcodeData = teacher.getEmployeeId();
        
        IDCard idCard = IDCard.builder()
            .teacher(teacher)
            .cardType(IDCard.CardType.TEACHER_ID)
            .cardNumber(cardNumber)
            .issueDate(LocalDate.now())
            .expiryDate(expiryDate != null ? expiryDate : LocalDate.now().plusYears(3))
            .status(IDCard.CardStatus.ACTIVE)
            .photoUrl(teacher.getProfileImageUrl())
            .barcodeData(barcodeData)
            .qrCodeData(qrCode)
            .designation(teacher.getDesignation())
            .department(teacher.getDepartment())
            .employeeId(teacher.getEmployeeId())
            .address(teacher.getAddress())
            .phoneNumber(teacher.getPhoneNumber())
            .dateOfBirth(teacher.getDateOfBirth())
            .emergencyContactName(teacher.getEmergencyContactName())
            .emergencyContactPhone(teacher.getEmergencyContactPhone())
            .isDeleted(false)
            .build();
        
        IDCard saved = idCardRepository.save(idCard);
        log.info("Teacher ID card generated with number: {}", cardNumber);
        
        return mapToResponse(saved);
    }

    @Override
    public IDCardResponse reissueIDCard(Long oldCardId, Double replacementFee) {
        log.info("Reissuing ID card for old card ID: {}", oldCardId);
        
        IDCard oldCard = idCardRepository.findById(oldCardId)
            .orElseThrow(() -> new ResourceNotFoundException("IDCard", "id", oldCardId));
        
        // Mark old card as replaced
        oldCard.setStatus(IDCard.CardStatus.REPLACED);
        idCardRepository.save(oldCard);
        
        // Generate new card
        if (oldCard.getCardType() == IDCard.CardType.STUDENT_ID) {
            IDCardResponse newCard = generateStudentIDCard(
                oldCard.getStudent().getId(), 
                LocalDate.now().plusYears(1));
            
            // Update with replacement info
            IDCard newCardEntity = idCardRepository.findById(newCard.getId()).get();
            newCardEntity.setReplacedBy(oldCard);
            newCardEntity.setReplacementDate(LocalDate.now());
            newCardEntity.setReplacementFee(replacementFee);
            idCardRepository.save(newCardEntity);
            
            return mapToResponse(newCardEntity);
        } else {
            return generateTeacherIDCard(oldCard.getTeacher().getId(), LocalDate.now().plusYears(3));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public IDCardResponse getIDCardById(Long id) {
        IDCard idCard = idCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("IDCard", "id", id));
        return mapToResponse(idCard);
    }

    @Override
    @Transactional(readOnly = true)
    public IDCardResponse getActiveStudentCard(Long studentId) {
        IDCard idCard = idCardRepository.findActiveStudentCard(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("IDCard", "studentId", studentId));
        return mapToResponse(idCard);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IDCardResponse> getAllIDCards(Pageable pageable) {
        return idCardRepository.findByStatusAndIsDeletedFalse(IDCard.CardStatus.ACTIVE, pageable)
            .map(this::mapToResponse);
    }

    @Override
    public IDCardResponse reportLost(Long cardId, String reason) {
        IDCard card = idCardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("IDCard", "id", cardId));
        card.setStatus(IDCard.CardStatus.LOST);
        card.setReplacementReason(reason);
        IDCard updated = idCardRepository.save(card);
        return mapToResponse(updated);
    }

    @Override
    public String generateQRCode(Long id) {
        IDCard card = idCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("IDCard", "id", id));
        return card.getQrCodeData();
    }

    // Helper Methods
    
    private String generateCardNumber(String prefix, Long id) {
        int year = LocalDate.now().getYear();
        return String.format("ID-%s-%d-%04d", prefix, year, id);
    }
    
    private String generateQRCodeData(Worker worker) {
        return String.format("WORKER|%s|%s|%s|%s", 
            worker.getUsername(),
            worker.getName(),
            worker.getEmail(),
            worker.getEmail());
    }
    
    private IDCardResponse mapToResponse(IDCard card) {
        boolean isExpired = card.getExpiryDate().isBefore(LocalDate.now());
        int daysToExpiry = (int) ChronoUnit.DAYS.between(LocalDate.now(), card.getExpiryDate());
        
        IDCardResponse.IDCardResponseBuilder builder = IDCardResponse.builder()
            .id(card.getId())
            .cardType(card.getCardType())
            .cardNumber(card.getCardNumber())
            .issueDate(card.getIssueDate())
            .expiryDate(card.getExpiryDate())
            .status(card.getStatus())
            .photoUrl(card.getPhotoUrl())
            .barcodeData(card.getBarcodeData())
            .qrCodeData(card.getQrCodeData())
            .address(card.getAddress())
            .phoneNumber(card.getPhoneNumber())
            .dateOfBirth(card.getDateOfBirth())
            .frontSideImageUrl(card.getFrontSideImageUrl())
            .backSideImageUrl(card.getBackSideImageUrl())
            .pdfUrl(card.getPdfUrl())
            .remarks(card.getRemarks())
            .isExpired(isExpired)
            .daysToExpiry(daysToExpiry)
            .cardTypeDisplay(card.getCardType().toString())
            .statusDisplay(card.getStatus().toString());
        
        // Add student-specific fields
        if (card.getStudent() != null) {
            builder
                .studentId(card.getStudent().getId())
                .studentName(card.getStudent().getFirstName() + " " + card.getStudent().getLastName())
                .admissionNumber(card.getStudent().getAdmissionNumber())
                .studentClass(card.getStudentClass())
                .section(card.getSection())
                .rollNumber(card.getRollNumber())
                .bloodGroup(card.getBloodGroup())
                .emergencyContactName(card.getEmergencyContactName())
                .emergencyContactPhone(card.getEmergencyContactPhone());
        }
        
        // Add teacher-specific fields
        if (card.getTeacher() != null) {
            builder
                .teacherId(card.getTeacher().getId())
                .teacherName(card.getTeacher().getFirstName() + " " + card.getTeacher().getLastName())
                .employeeId(card.getEmployeeId())
                .designation(card.getDesignation())
                .department(card.getDepartment());
        }
        
        return builder.build();
    }

    // Implement remaining interface methods
    @Override public IDCardResponse createIDCard(IDCardRequest request) { return null; }
    @Override public IDCardResponse getIDCardByNumber(String cardNumber) { return null; }
    @Override public IDCardResponse getActiveTeacherCard(Long teacherId) { return null; }
    @Override public List<IDCardResponse> getExpiredCards() { return null; }
    @Override public List<IDCardResponse> getCardsExpiringSoon() { return null; }
    @Override public IDCardResponse reportDamaged(Long cardId, String reason) { return null; }
    @Override public String generateIDCardPDF(Long id) { return null; }
    @Override public void cancelIDCard(Long id) { }
    
    private Long getCurrentOwnerId() {
        // Get the logged-in user ID for multi-tenancy
        return 1L; // For now, using karina's ID. In real implementation, get from security context
    }
}

