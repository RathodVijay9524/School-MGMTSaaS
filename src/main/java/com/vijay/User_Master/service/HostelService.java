package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HostelService {

    // Hostel CRUD operations
    HostelDTO createHostel(HostelRequestDTO hostelRequest, Long ownerId);
    HostelDTO updateHostel(Long id, HostelRequestDTO hostelRequest, Long ownerId);
    HostelDTO getHostelById(Long id, Long ownerId);
    void deleteHostel(Long id, Long ownerId);
    Page<HostelDTO> getAllHostels(Long ownerId, Pageable pageable);
    List<HostelDTO> getActiveHostels(Long ownerId);
    Page<HostelDTO> searchHostels(Long ownerId, String searchTerm, Pageable pageable);

    // Hostel statistics
    Map<String, Object> getHostelStatistics(Long ownerId);
    Map<String, Object> getHostelStatisticsById(Long hostelId, Long ownerId);
    List<HostelDTO> getHostelsWithAvailableBeds(Long ownerId);
    List<HostelDTO> getHostelsByAmenities(Long ownerId, Map<String, Boolean> amenities);
    List<HostelDTO> getHostelsByFeeRange(Long ownerId, Double minFee, Double maxFee);

    // Room CRUD operations
    RoomDTO createRoom(RoomRequestDTO roomRequest, Long ownerId);
    RoomDTO updateRoom(Long id, RoomRequestDTO roomRequest, Long ownerId);
    RoomDTO getRoomById(Long id, Long ownerId);
    void deleteRoom(Long id, Long ownerId);
    Page<RoomDTO> getRoomsByHostel(Long hostelId, Long ownerId, Pageable pageable);
    List<RoomDTO> getAvailableRooms(Long hostelId, Long ownerId);
    List<RoomDTO> getRoomsWithAvailableBeds(Long hostelId, Long ownerId);

    // Room statistics
    Map<String, Object> getRoomStatistics(Long hostelId, Long ownerId);
    List<RoomDTO> getRoomsByType(Long hostelId, String roomType, Long ownerId);
    List<RoomDTO> getRoomsByFloor(Long hostelId, Integer floorNumber, Long ownerId);

    // Bed CRUD operations
    BedDTO createBed(BedRequestDTO bedRequest, Long ownerId);
    BedDTO updateBed(Long id, BedRequestDTO bedRequest, Long ownerId);
    BedDTO getBedById(Long id, Long ownerId);
    void deleteBed(Long id, Long ownerId);
    Page<BedDTO> getBedsByRoom(Long roomId, Long ownerId, Pageable pageable);
    List<BedDTO> getAvailableBeds(Long roomId, Long ownerId);
    List<BedDTO> getAvailableBedsInHostel(Long hostelId, Long ownerId);

    // Bed management
    BedDTO markBedForMaintenance(Long bedId, String notes, Long ownerId);
    BedDTO completeBedMaintenance(Long bedId, Long ownerId);
    List<BedDTO> getBedsNeedingMaintenance(Long ownerId);
    BedDTO getNextAvailableBed(Long roomId, Long ownerId);

    // Hostel Resident CRUD operations
    HostelResidentDTO createResident(HostelResidentRequestDTO residentRequest, Long ownerId);
    HostelResidentDTO updateResident(Long id, HostelResidentRequestDTO residentRequest, Long ownerId);
    HostelResidentDTO getResidentById(Long id, Long ownerId);
    void deleteResident(Long id, Long ownerId);
    Page<HostelResidentDTO> getResidentsByHostel(Long hostelId, Long ownerId, Pageable pageable);
    List<HostelResidentDTO> getCurrentResidents(Long hostelId, Long ownerId);
    List<HostelResidentDTO> getResidentsByRoom(Long roomId, Long ownerId);

    // Resident management
    HostelResidentDTO checkInResident(HostelResidentRequestDTO residentRequest, Long ownerId);
    HostelResidentDTO checkOutResident(Long residentId, LocalDate checkOutDate, Long ownerId);
    List<HostelResidentDTO> getResidentsWithOverdueFees(Long ownerId);
    List<HostelResidentDTO> getResidentsDueForCheckOut(Long ownerId, LocalDate startDate, LocalDate endDate);

    // Fee management
    HostelResidentDTO updateFeePayment(Long residentId, Double amount, LocalDate paymentDate, Long ownerId);
    Map<String, Object> getFeeCollectionStatistics(Long hostelId, Long ownerId);
    Map<String, Object> getMonthlyRevenueStatistics(Long hostelId, Long ownerId);

    // Reports and analytics
    Map<String, Object> getOccupancyReport(Long hostelId, Long ownerId);
    Map<String, Object> getRevenueReport(Long hostelId, Long ownerId, LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getResidentDemographics(Long hostelId, Long ownerId);
    Map<String, Object> getMaintenanceReport(Long ownerId);

    // Utility methods
    boolean isHostelCodeAvailable(String hostelCode, Long ownerId);
    boolean isRoomNumberAvailable(String roomNumber, Long hostelId, Long ownerId);
    boolean isBedNumberAvailable(String bedNumber, Long roomId, Long ownerId);
    boolean isResidentIdAvailable(String residentId, Long ownerId);
    void updateHostelCapacity(Long hostelId, Long ownerId);
    void updateRoomCapacity(Long roomId, Long ownerId);
}
