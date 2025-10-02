package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.IDCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IDCardRepository extends JpaRepository<IDCard, Long> {

    Optional<IDCard> findByCardNumber(String cardNumber);
    
    Optional<IDCard> findByStudent_IdAndStatus(Long studentId, IDCard.CardStatus status);
    
    Optional<IDCard> findByTeacher_IdAndStatus(Long teacherId, IDCard.CardStatus status);
    
    boolean existsByCardNumber(String cardNumber);
    
    // Find by card type
    Page<IDCard> findByCardTypeAndIsDeletedFalse(IDCard.CardType cardType, Pageable pageable);
    
    // Find by status
    Page<IDCard> findByStatusAndIsDeletedFalse(IDCard.CardStatus status, Pageable pageable);
    
    // Find expired cards
    @Query("SELECT i FROM IDCard i WHERE i.expiryDate < :currentDate AND i.status = 'ACTIVE' AND i.isDeleted = false")
    List<IDCard> findExpiredCards(@Param("currentDate") LocalDate currentDate);
    
    // Find cards expiring soon
    @Query("SELECT i FROM IDCard i WHERE i.expiryDate BETWEEN :startDate AND :endDate AND i.status = 'ACTIVE' AND i.isDeleted = false")
    List<IDCard> findCardsExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find lost/damaged cards
    @Query("SELECT i FROM IDCard i WHERE i.status IN ('LOST', 'DAMAGED') AND i.isDeleted = false")
    List<IDCard> findLostOrDamagedCards();
    
    // Find active student cards
    @Query("SELECT i FROM IDCard i WHERE i.student.id = :studentId AND i.status = 'ACTIVE' AND i.isDeleted = false")
    Optional<IDCard> findActiveStudentCard(@Param("studentId") Long studentId);
    
    // Find active teacher cards
    @Query("SELECT i FROM IDCard i WHERE i.teacher.id = :teacherId AND i.status = 'ACTIVE' AND i.isDeleted = false")
    Optional<IDCard> findActiveTeacherCard(@Param("teacherId") Long teacherId);
}

