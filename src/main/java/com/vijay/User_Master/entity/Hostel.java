package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hostels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Hostel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hostel_name", nullable = false)
    private String hostelName;

    @Column(name = "hostel_code", unique = true, nullable = false)
    private String hostelCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "occupied_beds")
    @Builder.Default
    private Integer occupiedBeds = 0;

    @Column(name = "available_beds")
    @Builder.Default
    private Integer availableBeds = 0;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "total_beds")
    private Integer totalBeds;

    @Column(name = "warden_name")
    private String wardenName;

    @Column(name = "warden_contact")
    private String wardenContact;

    @Column(name = "warden_email")
    private String wardenEmail;

    @Column(name = "mess_available")
    @Builder.Default
    private Boolean messAvailable = false;

    @Column(name = "mess_timings")
    private String messTimings;

    @Column(name = "rules_and_regulations", columnDefinition = "TEXT")
    private String rulesAndRegulations;

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities; // JSON string of amenities

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "fees_per_month")
    private Double feesPerMonth;

    @Column(name = "security_deposit")
    private Double securityDeposit;

    @Column(name = "laundry_available")
    @Builder.Default
    private Boolean laundryAvailable = false;

    @Column(name = "wifi_available")
    @Builder.Default
    private Boolean wifiAvailable = false;

    @Column(name = "gym_available")
    @Builder.Default
    private Boolean gymAvailable = false;

    @Column(name = "library_available")
    @Builder.Default
    private Boolean libraryAvailable = false;

    // Relationships
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HostelResident> residents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Helper methods
    public void calculateCapacity() {
        if (rooms != null) {
            this.totalRooms = rooms.size();
            this.totalBeds = rooms.stream().mapToInt(room -> room.getBeds().size()).sum();
            this.occupiedBeds = residents.stream().mapToInt(resident -> resident.getIsActive() ? 1 : 0).sum();
            this.availableBeds = this.totalBeds - this.occupiedBeds;
        }
    }

    public boolean hasAvailableBeds() {
        return availableBeds > 0;
    }

    public double getOccupancyPercentage() {
        if (totalBeds == 0) return 0.0;
        return (double) occupiedBeds / totalBeds * 100;
    }
}
