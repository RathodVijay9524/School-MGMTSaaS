package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.IDCardRequest;
import com.vijay.User_Master.dto.IDCardResponse;
import com.vijay.User_Master.entity.IDCard;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.IDCardRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.IDCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class IDCardServiceImpl implements IDCardService {

    private final IDCardRepository idCardRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    @Tool(name = "generateStudentIDCard", description = "Generate ID card for a student")
    public IDCardResponse generateStudentIDCard(Long studentId, LocalDate expiryDate) {
        log.info("Generating student ID card for student ID: {}", studentId);
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Find student worker
        Worker student = workerRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Create ID card
        IDCard idCard = IDCard.builder()
                .cardNumber("ID-STU-" + System.currentTimeMillis())
            .cardType(IDCard.CardType.STUDENT_ID)
            .issueDate(LocalDate.now())
                .expiryDate(expiryDate)
            .status(IDCard.CardStatus.ACTIVE)
                .student(student)
                .owner(owner)
            .isDeleted(false)
            .build();
        
        IDCard savedCard = idCardRepository.save(idCard);
        log.info("Student ID card generated successfully with ID: {}", savedCard.getId());
        
        return mapToResponse(savedCard);
    }

    @Override
    @Tool(name = "generateTeacherIDCard", description = "Generate ID card for a teacher")
    public IDCardResponse generateTeacherIDCard(Long teacherId, LocalDate expiryDate) {
        log.info("Generating teacher ID card for teacher ID: {}", teacherId);
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Find teacher worker
        Worker teacher = workerRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        // Create ID card
        IDCard idCard = IDCard.builder()
                .cardNumber("ID-TCH-" + System.currentTimeMillis())
                .cardType(IDCard.CardType.TEACHER_ID)
                .issueDate(LocalDate.now())
                .expiryDate(expiryDate)
                .status(IDCard.CardStatus.ACTIVE)
                .teacher(teacher)
                .owner(owner)
                .isDeleted(false)
                .build();
        
        IDCard savedCard = idCardRepository.save(idCard);
        log.info("Teacher ID card generated successfully with ID: {}", savedCard.getId());
        
        return mapToResponse(savedCard);
    }

    @Override
    @Tool(name = "createIDCard", description = "Create ID card for student or teacher with card number, type, issue and expiry dates")
    public IDCardResponse createIDCard(IDCardRequest request) {
        log.info("Creating custom ID card");
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Create ID card from request
        IDCard idCard = IDCard.builder()
                .cardNumber("ID-" + request.getCardType().name().replace("_ID", "") + "-" + System.currentTimeMillis())
                .cardType(request.getCardType())
                .issueDate(request.getIssueDate())
                .expiryDate(request.getExpiryDate())
            .status(IDCard.CardStatus.ACTIVE)
                .student(request.getStudentId() != null ? workerRepository.findById(request.getStudentId()).orElse(null) : null)
                .teacher(request.getTeacherId() != null ? workerRepository.findById(request.getTeacherId()).orElse(null) : null)
                .owner(owner)
            .isDeleted(false)
            .build();
        
        IDCard savedCard = idCardRepository.save(idCard);
        log.info("Custom ID card created successfully with ID: {}", savedCard.getId());
        
        return mapToResponse(savedCard);
    }

    @Override
    @Tool(name = "getIDCardById", description = "Get ID card details by card ID")
    public IDCardResponse getIDCardById(Long id) {
        log.info("Fetching ID card by ID: {}", id);
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        IDCard idCard = idCardRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("ID card not found"));
        
        return mapToResponse(idCard);
    }

    @Override
    public IDCardResponse getIDCardByNumber(String cardNumber) {
        log.info("Fetching ID card by number: {}", cardNumber);
        
        IDCard idCard = idCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("ID card not found"));
        
        return mapToResponse(idCard);
    }

    @Override
    public IDCardResponse getActiveStudentCard(Long studentId) {
        log.info("Fetching active student card for student ID: {}", studentId);
        
        IDCard idCard = idCardRepository.findActiveStudentCard(studentId)
                .orElseThrow(() -> new RuntimeException("Active student card not found"));
        
        return mapToResponse(idCard);
    }

    @Override
    public IDCardResponse getActiveTeacherCard(Long teacherId) {
        log.info("Fetching active teacher card for teacher ID: {}", teacherId);
        
        IDCard idCard = idCardRepository.findActiveTeacherCard(teacherId)
                .orElseThrow(() -> new RuntimeException("Active teacher card not found"));
        
        return mapToResponse(idCard);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IDCardResponse> getAllIDCards(Pageable pageable) {
        log.info("Fetching all ID cards with pagination");
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        // Use owner-based query
        Page<IDCard> idCardPage = idCardRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        
        return idCardPage.map(this::mapToResponse);
    }

    @Override
    public List<IDCardResponse> getExpiredCards() {
        log.info("Fetching expired ID cards");
        
        List<IDCard> expiredCards = idCardRepository.findExpiredCards(LocalDate.now());
        return expiredCards.stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<IDCardResponse> getCardsExpiringSoon() {
        log.info("Fetching cards expiring soon");
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        
        List<IDCard> expiringCards = idCardRepository.findCardsExpiringSoon(startDate, endDate);
        return expiringCards.stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public IDCardResponse reportLost(Long cardId, String reason) {
        log.info("Reporting ID card as lost: {}", cardId);
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        IDCard idCard = idCardRepository.findByIdAndOwner_IdAndIsDeletedFalse(cardId, ownerId)
                .orElseThrow(() -> new RuntimeException("ID card not found"));
        
        idCard.setStatus(IDCard.CardStatus.LOST);
        idCard.setUpdatedBy(loggedInUser.getId().intValue());
        idCard.setUpdatedOn(new java.util.Date());
        
        IDCard savedCard = idCardRepository.save(idCard);
        log.info("ID card reported as lost successfully");
        
        return mapToResponse(savedCard);
    }

    @Override
    public IDCardResponse reportDamaged(Long cardId, String reason) {
        log.info("Reporting ID card as damaged: {}", cardId);
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        IDCard idCard = idCardRepository.findByIdAndOwner_IdAndIsDeletedFalse(cardId, ownerId)
                .orElseThrow(() -> new RuntimeException("ID card not found"));
        
        idCard.setStatus(IDCard.CardStatus.DAMAGED);
        idCard.setUpdatedBy(loggedInUser.getId().intValue());
        idCard.setUpdatedOn(new java.util.Date());
        
        IDCard savedCard = idCardRepository.save(idCard);
        log.info("ID card reported as damaged successfully");
        
        return mapToResponse(savedCard);
    }

    @Override
    public IDCardResponse reissueIDCard(Long oldCardId, Double replacementFee) {
        log.info("Reissuing ID card: {}", oldCardId);
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        IDCard oldCard = idCardRepository.findByIdAndOwner_IdAndIsDeletedFalse(oldCardId, loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("Original ID card not found"));
        
        // Create new card
        IDCard newCard = IDCard.builder()
                .cardNumber(oldCard.getCardType() == IDCard.CardType.STUDENT_ID ? 
                    "ID-STU-" + System.currentTimeMillis() : "ID-TCH-" + System.currentTimeMillis())
                .cardType(oldCard.getCardType())
                .issueDate(LocalDate.now())
                .expiryDate(oldCard.getExpiryDate())
                .status(IDCard.CardStatus.ACTIVE)
                .student(oldCard.getStudent())
                .teacher(oldCard.getTeacher())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        IDCard savedCard = idCardRepository.save(newCard);
        log.info("ID card reissued successfully with new ID: {}", savedCard.getId());
        
        return mapToResponse(savedCard);
    }

    @Override
    public String generateIDCardPDF(Long id) {
        log.info("Generating PDF for ID card: {}", id);
        
        // Placeholder implementation - in real scenario, you would generate actual PDF
        return "IDCard_" + id + "_" + System.currentTimeMillis() + ".pdf";
    }

    @Override
    public String generateQRCode(Long id) {
        log.info("Generating QR code for ID card: {}", id);
        
        // Placeholder implementation - in real scenario, you would generate actual QR code
        return "QR_" + id + "_" + System.currentTimeMillis();
    }

    @Override
    public void cancelIDCard(Long id) {
        log.info("Canceling ID card: {}", id);
        
        // Get the current logged-in user for multi-tenancy
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getId();
        
        IDCard idCard = idCardRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("ID card not found"));
        
        idCard.setStatus(IDCard.CardStatus.CANCELLED);
        idCard.setUpdatedBy(loggedInUser.getId().intValue());
        idCard.setUpdatedOn(new java.util.Date());
        
        idCardRepository.save(idCard);
        log.info("ID card canceled successfully");
    }
    
    private IDCardResponse mapToResponse(IDCard card) {
        return IDCardResponse.builder()
            .id(card.getId())
                .cardNumber(card.getCardNumber())
            .cardType(card.getCardType())
            .issueDate(card.getIssueDate())
            .expiryDate(card.getExpiryDate())
            .status(card.getStatus())
                .build();
    }
}