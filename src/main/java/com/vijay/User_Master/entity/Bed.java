package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "beds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Bed extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bed_number", nullable = false)
    private String bedNumber;

    @Column(name = "bed_type")
    @Enumerated(EnumType.STRING)
    private BedType bedType;

    @Column(name = "is_occupied")
    @Builder.Default
    private Boolean isOccupied = false;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "bed_fees_per_month")
    private Double bedFeesPerMonth;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;

    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;

    @Column(name = "is_under_maintenance")
    @Builder.Default
    private Boolean isUnderMaintenance = false;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToOne(mappedBy = "bed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HostelResident resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public boolean isAvailableForAllocation() {
        return isAvailable && !isOccupied && !isUnderMaintenance && isActive;
    }

    public void markAsOccupied() {
        this.isOccupied = true;
        this.isAvailable = false;
    }

    public void markAsAvailable() {
        this.isOccupied = false;
        this.isAvailable = true;
    }

    public void markForMaintenance(String notes) {
        this.isUnderMaintenance = true;
        this.isAvailable = false;
        this.maintenanceNotes = notes;
        this.lastMaintenanceDate = LocalDateTime.now();
    }

    public void completeMaintenance() {
        this.isUnderMaintenance = false;
        this.isAvailable = true;
        this.lastMaintenanceDate = LocalDateTime.now();
    }

    public enum BedType {
        SINGLE,
        BUNK_TOP,
        BUNK_BOTTOM,
        DOUBLE,
        KING,
        QUEEN
    }
}
