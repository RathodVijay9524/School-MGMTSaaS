package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "workers")
@EntityListeners(AuditingEntityListener.class)
public class Worker extends BaseModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String phoNo;
    private boolean isDeleted;
    private LocalDateTime deletedOn;
    @Column(length = 1000)
    private String about;

    // Basic Information
    private String firstName;
    private String lastName;
    
    // Student-specific fields
    private String admissionNumber;
    private String rollNumber;
    private String section;
    private String bloodGroup;
    private String fatherName;
    private String fatherPhone;
    private String motherName;
    private String motherPhone;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String profileImageUrl;
    
    // Teacher-specific fields
    private String employeeId;
    private String designation;
    private String department;
    private String emergencyContactName;
    private String emergencyContactPhone;
    
    // Fee-related fields
    private Double totalFees;
    private Double feesPaid;
    private Double feesBalance;
    
    // Class relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_class_id")
    private SchoolClass currentClass;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workers_roles",
            joinColumns = @JoinColumn(name = "worker_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    // Additional fields for complete compatibility
    private String parentEmail;
    private String parentPhone;
    private String imageName;
    private String status;
    
    // Account status relationship
    // Convenience methods for backward compatibility
    public String getFirstName() {
        return firstName != null ? firstName : (name != null ? name.split(" ")[0] : null);
    }
    
    public String getLastName() {
        return lastName != null ? lastName : (name != null && name.split(" ").length > 1 ? name.split(" ")[1] : null);
    }
    
    public String getAdmissionNumber() {
        return admissionNumber != null ? admissionNumber : username;
    }
    
    public String getEmployeeId() {
        return employeeId != null ? employeeId : username;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl != null ? profileImageUrl : imageName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : phoNo;
    }
    
    public String getParentEmail() {
        return parentEmail;
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public void setTotalFees(Double totalFees) {
        this.totalFees = totalFees;
    }
    
    public void setFeesPaid(Double feesPaid) {
        this.feesPaid = feesPaid;
    }
    
    public void setFeesBalance(Double feesBalance) {
        this.feesBalance = feesBalance;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
    
    // Account status relationship
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = true)
    private AccountStatus accountStatus;

}
