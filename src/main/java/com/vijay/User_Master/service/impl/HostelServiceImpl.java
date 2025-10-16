package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.HostelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HostelServiceImpl implements HostelService {

    private final HostelRepository hostelRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final HostelResidentRepository residentRepository;
    private final UserRepository userRepository;

    // ==================== HOSTEL OPERATIONS ====================

    @Override
    public HostelDTO createHostel(HostelRequestDTO request, Long ownerId) {
        log.info("Creating hostel: {} for owner: {}", request.getHostelName(), ownerId);

        // Validate hostel code uniqueness
        if (!isHostelCodeAvailable(request.getHostelCode(), ownerId)) {
            throw new RuntimeException("Hostel code already exists: " + request.getHostelCode());
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));

        Hostel hostel = Hostel.builder()
                .hostelName(request.getHostelName())
                .hostelCode(request.getHostelCode())
                .description(request.getDescription())
                .address(request.getAddress())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .capacity(request.getCapacity())
                .wardenName(request.getWardenName())
                .wardenContact(request.getWardenContact())
                .wardenEmail(request.getWardenEmail())
                .messAvailable(request.getMessAvailable())
                .messTimings(request.getMessTimings())
                .rulesAndRegulations(request.getRulesAndRegulations())
                .amenities(request.getAmenities())
                .isActive(request.getIsActive())
                .feesPerMonth(request.getFeesPerMonth())
                .securityDeposit(request.getSecurityDeposit())
                .laundryAvailable(request.getLaundryAvailable())
                .wifiAvailable(request.getWifiAvailable())
                .gymAvailable(request.getGymAvailable())
                .libraryAvailable(request.getLibraryAvailable())
                .occupiedBeds(0)
                .availableBeds(0)
                .totalRooms(0)
                .totalBeds(0)
                .owner(owner)
                .build();

        Hostel savedHostel = hostelRepository.save(hostel);
        log.info("Hostel created successfully with ID: {}", savedHostel.getId());

        return convertToHostelDTO(savedHostel);
    }

    @Override
    public HostelDTO updateHostel(Long id, HostelRequestDTO request, Long ownerId) {
        log.info("Updating hostel: {} for owner: {}", id, ownerId);

        Hostel hostel = hostelRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Hostel not found: " + id));

        // Update fields
        hostel.setHostelName(request.getHostelName());
        hostel.setDescription(request.getDescription());
        hostel.setAddress(request.getAddress());
        hostel.setContactNumber(request.getContactNumber());
        hostel.setEmail(request.getEmail());
        hostel.setCapacity(request.getCapacity());
        hostel.setWardenName(request.getWardenName());
        hostel.setWardenContact(request.getWardenContact());
        hostel.setWardenEmail(request.getWardenEmail());
        hostel.setMessAvailable(request.getMessAvailable());
        hostel.setMessTimings(request.getMessTimings());
        hostel.setRulesAndRegulations(request.getRulesAndRegulations());
        hostel.setAmenities(request.getAmenities());
        hostel.setIsActive(request.getIsActive());
        hostel.setFeesPerMonth(request.getFeesPerMonth());
        hostel.setSecurityDeposit(request.getSecurityDeposit());
        hostel.setLaundryAvailable(request.getLaundryAvailable());
        hostel.setWifiAvailable(request.getWifiAvailable());
        hostel.setGymAvailable(request.getGymAvailable());
        hostel.setLibraryAvailable(request.getLibraryAvailable());

        Hostel updatedHostel = hostelRepository.save(hostel);
        log.info("Hostel updated successfully: {}", updatedHostel.getId());

        return convertToHostelDTO(updatedHostel);
    }

    @Override
    @Transactional(readOnly = true)
    public HostelDTO getHostelById(Long id, Long ownerId) {
        Hostel hostel = hostelRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Hostel not found: " + id));

        return convertToHostelDTO(hostel);
    }

    @Override
    public void deleteHostel(Long id, Long ownerId) {
        log.info("Deleting hostel: {} for owner: {}", id, ownerId);

        Hostel hostel = hostelRepository.findByIdAndOwnerIdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Hostel not found: " + id));

        // Soft delete hostel
        hostel.setIsDeleted(true);
        hostelRepository.save(hostel);

        log.info("Hostel deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HostelDTO> getAllHostels(Long ownerId, Pageable pageable) {
        Page<Hostel> hostels = hostelRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId, pageable);
        return hostels.map(this::convertToHostelDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HostelDTO> getActiveHostels(Long ownerId) {
        List<Hostel> hostels = hostelRepository.findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByHostelName(ownerId);
        return hostels.stream().map(this::convertToHostelDTO).collect(Collectors.toList());
    }

    @Override
    public Page<HostelDTO> searchHostels(Long ownerId, String searchTerm, Pageable pageable) {
        try {
            Page<Hostel> hostels = hostelRepository.searchHostels(ownerId, searchTerm, pageable);
            return hostels.map(this::convertToHostelDTO);
        } catch (Exception e) {
            log.error("Error searching hostels: {}", e.getMessage());
            // If repository method doesn't exist, implement simple search
            List<Hostel> allHostels = hostelRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId);
            List<Hostel> filteredHostels = allHostels.stream()
                    .filter(hostel -> 
                        hostel.getHostelName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        hostel.getHostelCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (hostel.getDescription() != null && hostel.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .collect(Collectors.toList());
            
            // Convert to Page
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), filteredHostels.size());
            List<Hostel> pageContent = filteredHostels.subList(start, end);
            
            Page<Hostel> page = new PageImpl<>(pageContent, pageable, filteredHostels.size());
            return page.map(this::convertToHostelDTO);
        }
    }

    @Override
    public List<HostelDTO> getHostelsWithAvailableBeds(Long ownerId) {
        try {
            List<Hostel> hostels = hostelRepository.findHostelsWithAvailableBeds(ownerId);
            return hostels.stream().map(this::convertToHostelDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting hostels with available beds: {}", e.getMessage());
            // Fallback to simple filtering
            List<Hostel> allHostels = hostelRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId);
            return allHostels.stream()
                    .filter(hostel -> hostel.getAvailableBeds() > 0)
                    .map(this::convertToHostelDTO)
                    .collect(Collectors.toList());
        }
    }

    // ==================== UTILITY METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public boolean isHostelCodeAvailable(String hostelCode, Long ownerId) {
        return !hostelRepository.existsByHostelCodeAndOwnerIdAndIsDeletedFalse(hostelCode, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoomNumberAvailable(String roomNumber, Long hostelId, Long ownerId) {
        return !roomRepository.existsByRoomNumberAndHostelIdAndIsDeletedFalse(roomNumber, hostelId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBedNumberAvailable(String bedNumber, Long roomId, Long ownerId) {
        return !bedRepository.existsByBedNumberAndRoomIdAndIsDeletedFalse(bedNumber, roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResidentIdAvailable(String residentId, Long ownerId) {
        return !residentRepository.existsByResidentIdAndOwnerIdAndIsDeletedFalse(residentId, ownerId);
    }

    @Override
    public void updateHostelCapacity(Long hostelId, Long ownerId) {
        Hostel hostel = hostelRepository.findByIdAndOwnerIdAndIsDeletedFalse(hostelId, ownerId)
                .orElseThrow(() -> new RuntimeException("Hostel not found: " + hostelId));

        hostel.calculateCapacity();
        hostelRepository.save(hostel);
    }

    @Override
    public void updateRoomCapacity(Long roomId, Long ownerId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        room.calculateCapacity();
        roomRepository.save(room);
    }

    // ==================== CONVERSION METHODS ====================

    private HostelDTO convertToHostelDTO(Hostel hostel) {
        return HostelDTO.builder()
                .id(hostel.getId())
                .hostelName(hostel.getHostelName())
                .hostelCode(hostel.getHostelCode())
                .description(hostel.getDescription())
                .address(hostel.getAddress())
                .contactNumber(hostel.getContactNumber())
                .email(hostel.getEmail())
                .capacity(hostel.getCapacity())
                .occupiedBeds(hostel.getOccupiedBeds())
                .availableBeds(hostel.getAvailableBeds())
                .totalRooms(hostel.getTotalRooms())
                .totalBeds(hostel.getTotalBeds())
                .wardenName(hostel.getWardenName())
                .wardenContact(hostel.getWardenContact())
                .wardenEmail(hostel.getWardenEmail())
                .messAvailable(hostel.getMessAvailable())
                .messTimings(hostel.getMessTimings())
                .rulesAndRegulations(hostel.getRulesAndRegulations())
                .amenities(hostel.getAmenities())
                .isActive(hostel.getIsActive())
                .feesPerMonth(hostel.getFeesPerMonth())
                .securityDeposit(hostel.getSecurityDeposit())
                .laundryAvailable(hostel.getLaundryAvailable())
                .wifiAvailable(hostel.getWifiAvailable())
                .gymAvailable(hostel.getGymAvailable())
                .libraryAvailable(hostel.getLibraryAvailable())
                .occupancyPercentage(hostel.getOccupancyPercentage())
                .hasAvailableBeds(hostel.hasAvailableBeds())
                .ownerId(hostel.getOwner().getId())
                .ownerName(hostel.getOwner().getName())
                .createdOn(hostel.getCreatedOn())
                .updatedOn(hostel.getUpdatedOn())
                .build();
    }

    // ==================== PLACEHOLDER IMPLEMENTATIONS ====================
    // These methods need to be implemented based on business requirements

    @Override
    public RoomDTO createRoom(RoomRequestDTO request, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public RoomDTO updateRoom(Long id, RoomRequestDTO request, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public RoomDTO getRoomById(Long id, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteRoom(Long id, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<RoomDTO> getRoomsByHostel(Long hostelId, Long ownerId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<RoomDTO> getAvailableRooms(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<RoomDTO> getRoomsWithAvailableBeds(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getRoomStatistics(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<RoomDTO> getRoomsByType(Long hostelId, String roomType, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<RoomDTO> getRoomsByFloor(Long hostelId, Integer floorNumber, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BedDTO createBed(BedRequestDTO request, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BedDTO updateBed(Long id, BedRequestDTO request, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BedDTO getBedById(Long id, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteBed(Long id, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<BedDTO> getBedsByRoom(Long roomId, Long ownerId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<BedDTO> getAvailableBeds(Long roomId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<BedDTO> getAvailableBedsInHostel(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BedDTO markBedForMaintenance(Long bedId, String notes, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BedDTO completeBedMaintenance(Long bedId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<BedDTO> getBedsNeedingMaintenance(Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BedDTO getNextAvailableBed(Long roomId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public HostelResidentDTO createResident(HostelResidentRequestDTO request, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public HostelResidentDTO updateResident(Long id, HostelResidentRequestDTO request, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public HostelResidentDTO getResidentById(Long id, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteResident(Long id, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<HostelResidentDTO> getResidentsByHostel(Long hostelId, Long ownerId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<HostelResidentDTO> getCurrentResidents(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<HostelResidentDTO> getResidentsByRoom(Long roomId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public HostelResidentDTO checkInResident(HostelResidentRequestDTO request, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public HostelResidentDTO checkOutResident(Long residentId, LocalDate checkOutDate, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<HostelResidentDTO> getResidentsWithOverdueFees(Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<HostelResidentDTO> getResidentsDueForCheckOut(Long ownerId, LocalDate startDate, LocalDate endDate) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public HostelResidentDTO updateFeePayment(Long residentId, Double amount, LocalDate paymentDate, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getFeeCollectionStatistics(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getMonthlyRevenueStatistics(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getOccupancyReport(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getRevenueReport(Long hostelId, Long ownerId, LocalDate startDate, LocalDate endDate) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Map<String, Object>> getResidentDemographics(Long hostelId, Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getMaintenanceReport(Long ownerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<HostelDTO> getHostelsByAmenities(Long ownerId, Map<String, Boolean> amenities) {
        try {
            List<Hostel> allHostels = hostelRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId);
            
            List<Hostel> filteredHostels = allHostels.stream()
                    .filter(hostel -> {
                        if (amenities.containsKey("wifiAvailable") && amenities.get("wifiAvailable") && !hostel.getWifiAvailable()) {
                            return false;
                        }
                        if (amenities.containsKey("gymAvailable") && amenities.get("gymAvailable") && !hostel.getGymAvailable()) {
                            return false;
                        }
                        if (amenities.containsKey("laundryAvailable") && amenities.get("laundryAvailable") && !hostel.getLaundryAvailable()) {
                            return false;
                        }
                        if (amenities.containsKey("libraryAvailable") && amenities.get("libraryAvailable") && !hostel.getLibraryAvailable()) {
                            return false;
                        }
                        if (amenities.containsKey("messAvailable") && amenities.get("messAvailable") && !hostel.getMessAvailable()) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            
            return filteredHostels.stream().map(this::convertToHostelDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error filtering hostels by amenities: {}", e.getMessage());
            return new ArrayList<>(); // Return empty list instead of throwing exception
        }
    }

    @Override
    public List<HostelDTO> getHostelsByFeeRange(Long ownerId, Double minFee, Double maxFee) {
        try {
            List<Hostel> allHostels = hostelRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId);
            
            List<Hostel> filteredHostels = allHostels.stream()
                    .filter(hostel -> {
                        if (hostel.getFeesPerMonth() == null) return false;
                        return hostel.getFeesPerMonth() >= minFee && hostel.getFeesPerMonth() <= maxFee;
                    })
                    .collect(Collectors.toList());
            
            return filteredHostels.stream().map(this::convertToHostelDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error filtering hostels by fee range: {}", e.getMessage());
            return new ArrayList<>(); // Return empty list instead of throwing exception
        }
    }

    @Override
    public Map<String, Object> getHostelStatistics(Long ownerId) {
        try {
            List<Hostel> allHostels = hostelRepository.findByOwnerIdAndIsDeletedFalseOrderByCreatedOnDesc(ownerId);
            
            int totalHostels = allHostels.size();
            int activeHostelCount = (int) allHostels.stream().filter(Hostel::getIsActive).count();
            int totalCapacity = allHostels.stream().mapToInt(hostel -> hostel.getCapacity() != null ? hostel.getCapacity() : 0).sum();
            int totalOccupiedBeds = allHostels.stream().mapToInt(hostel -> hostel.getOccupiedBeds() != null ? hostel.getOccupiedBeds() : 0).sum();
            int totalAvailableBeds = allHostels.stream().mapToInt(hostel -> hostel.getAvailableBeds() != null ? hostel.getAvailableBeds() : 0).sum();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalHostels", totalHostels);
            statistics.put("activeHostels", activeHostelCount);
            statistics.put("totalCapacity", totalCapacity);
            statistics.put("totalOccupiedBeds", totalOccupiedBeds);
            statistics.put("totalAvailableBeds", totalAvailableBeds);
            statistics.put("occupancyRate", totalCapacity > 0 ? (double) totalOccupiedBeds / totalCapacity * 100 : 0.0);
            
            return statistics;
        } catch (Exception e) {
            log.error("Error getting hostel statistics: {}", e.getMessage());
            // Return empty statistics instead of throwing exception
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalHostels", 0);
            statistics.put("activeHostels", 0);
            statistics.put("totalCapacity", 0);
            statistics.put("totalOccupiedBeds", 0);
            statistics.put("totalAvailableBeds", 0);
            statistics.put("occupancyRate", 0.0);
            return statistics;
        }
    }

    @Override
    public Map<String, Object> getHostelStatisticsById(Long hostelId, Long ownerId) {
        try {
            Hostel hostel = hostelRepository.findByIdAndOwnerIdAndIsDeletedFalse(hostelId, ownerId)
                    .orElseThrow(() -> new RuntimeException("Hostel not found: " + hostelId));
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("hostelId", hostel.getId());
            statistics.put("hostelName", hostel.getHostelName());
            statistics.put("capacity", hostel.getCapacity());
            statistics.put("occupiedBeds", hostel.getOccupiedBeds());
            statistics.put("availableBeds", hostel.getAvailableBeds());
            statistics.put("totalRooms", hostel.getTotalRooms());
            statistics.put("totalBeds", hostel.getTotalBeds());
            statistics.put("occupancyPercentage", hostel.getOccupancyPercentage());
            statistics.put("hasAvailableBeds", hostel.hasAvailableBeds());
            statistics.put("isActive", hostel.getIsActive());
            statistics.put("feesPerMonth", hostel.getFeesPerMonth());
            
            return statistics;
        } catch (Exception e) {
            log.error("Error getting hostel statistics by ID: {}", e.getMessage());
            // Return empty statistics instead of throwing exception
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("error", "Failed to get hostel statistics");
            return statistics;
        }
    }
}