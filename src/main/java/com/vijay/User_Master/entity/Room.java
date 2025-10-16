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
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Room extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "occupied_beds")
    @Builder.Default
    private Integer occupiedBeds = 0;

    @Column(name = "available_beds")
    @Builder.Default
    private Integer availableBeds = 0;

    @Column(name = "room_fees_per_month")
    private Double roomFeesPerMonth;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities; // JSON string of room-specific amenities

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "ac_available")
    @Builder.Default
    private Boolean acAvailable = false;

    @Column(name = "attached_bathroom")
    @Builder.Default
    private Boolean attachedBathroom = false;

    @Column(name = "balcony_available")
    @Builder.Default
    private Boolean balconyAvailable = false;

    @Column(name = "furniture_included")
    @Builder.Default
    private Boolean furnitureIncluded = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Bed> beds = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HostelResident> residents = new ArrayList<>();

    // Helper methods
    public void calculateCapacity() {
        if (beds != null) {
            this.capacity = beds.size();
            this.occupiedBeds = residents.stream().mapToInt(resident -> resident.getIsActive() ? 1 : 0).sum();
            this.availableBeds = this.capacity - this.occupiedBeds;
        }
    }

    public boolean hasAvailableBeds() {
        return availableBeds > 0;
    }

    public double getOccupancyPercentage() {
        if (capacity == 0) return 0.0;
        return (double) occupiedBeds / capacity * 100;
    }

    public enum RoomType {
        SINGLE,
        DOUBLE,
        TRIPLE,
        QUAD,
        DORMITORY,
        SUITE,
        DELUXE
    }
}
