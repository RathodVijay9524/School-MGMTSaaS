package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "book_issues")
@EntityListeners(AuditingEntityListener.class)
public class BookIssue extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Library book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Worker student; // Worker with ROLE_STUDENT
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Worker teacher; // Worker with ROLE_TEACHER
    
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    private LocalDate returnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status; // ISSUED, RETURNED, OVERDUE, LOST, DAMAGED
    
    private Integer daysOverdue;
    
    private Double lateFee;
    
    private Double damageFee;
    
    private boolean fineCollected;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_user_id")
    private User issuedBy; // Librarian who issued the book
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_to_user_id")
    private User returnedTo; // Librarian who received the book
    
    @Column(length = 500)
    private String issueRemarks;
    
    @Column(length = 500)
    private String returnRemarks;
    
    @Enumerated(EnumType.STRING)
    private BookCondition bookConditionOnIssue;
    
    @Enumerated(EnumType.STRING)
    private BookCondition bookConditionOnReturn;
    
    private Integer renewalCount;
    
    private LocalDate lastRenewalDate;
    
    // Business Owner (Multi-tenancy)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum IssueStatus {
        ISSUED, RETURNED, OVERDUE, LOST, DAMAGED, RENEWED
    }

    public enum BookCondition {
        EXCELLENT, GOOD, FAIR, POOR, DAMAGED
    }
}

