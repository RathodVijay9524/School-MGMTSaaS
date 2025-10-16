package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.service.HostelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hostels")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HostelController {

    private final HostelService hostelService;

    // ==================== HOSTEL ENDPOINTS ====================

    @PostMapping
    public ResponseEntity<HostelDTO> createHostel(
            @Valid @RequestBody HostelRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            log.info("Creating hostel: {} for owner: {}", request.getHostelName(), ownerId);
            HostelDTO hostel = hostelService.createHostel(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(hostel);
        } catch (Exception e) {
            log.error("Error creating hostel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<HostelDTO>> getAllHostels(
            @RequestHeader("X-User-ID") Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<HostelDTO> hostels;
            if (search != null && !search.trim().isEmpty()) {
                hostels = hostelService.searchHostels(ownerId, search, pageable);
            } else {
                hostels = hostelService.getAllHostels(ownerId, pageable);
            }

            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            log.error("Error fetching hostels: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<HostelDTO>> getActiveHostels(@RequestHeader("X-User-ID") Long ownerId) {
        try {
            List<HostelDTO> hostels = hostelService.getActiveHostels(ownerId);
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            log.error("Error fetching active hostels: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<HostelDTO>> getHostelsWithAvailableBeds(@RequestHeader("X-User-ID") Long ownerId) {
        try {
            List<HostelDTO> hostels = hostelService.getHostelsWithAvailableBeds(ownerId);
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            log.error("Error fetching hostels with available beds: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HostelDTO> getHostelById(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            HostelDTO hostel = hostelService.getHostelById(id, ownerId);
            return ResponseEntity.ok(hostel);
        } catch (Exception e) {
            log.error("Error fetching hostel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HostelDTO> updateHostel(
            @PathVariable Long id,
            @Valid @RequestBody HostelRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            HostelDTO hostel = hostelService.updateHostel(id, request, ownerId);
            return ResponseEntity.ok(hostel);
        } catch (Exception e) {
            log.error("Error updating hostel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHostel(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            hostelService.deleteHostel(id, ownerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting hostel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getHostelStatistics(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            Map<String, Object> statistics = hostelService.getHostelStatisticsById(id, ownerId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error fetching hostel statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAllHostelStatistics(@RequestHeader("X-User-ID") Long ownerId) {
        try {
            Map<String, Object> statistics = hostelService.getHostelStatistics(ownerId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error fetching all hostel statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HostelDTO>> searchHostels(
            @RequestHeader("X-User-ID") Long ownerId,
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<HostelDTO> hostels = hostelService.searchHostels(ownerId, searchTerm, pageable);
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            log.error("Error searching hostels: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-amenities")
    public ResponseEntity<List<HostelDTO>> getHostelsByAmenities(
            @RequestHeader("X-User-ID") Long ownerId,
            @RequestParam(required = false) Boolean wifiAvailable,
            @RequestParam(required = false) Boolean gymAvailable,
            @RequestParam(required = false) Boolean laundryAvailable,
            @RequestParam(required = false) Boolean libraryAvailable,
            @RequestParam(required = false) Boolean messAvailable) {
        try {
            Map<String, Boolean> amenities = new HashMap<>();
            if (wifiAvailable != null) amenities.put("wifiAvailable", wifiAvailable);
            if (gymAvailable != null) amenities.put("gymAvailable", gymAvailable);
            if (laundryAvailable != null) amenities.put("laundryAvailable", laundryAvailable);
            if (libraryAvailable != null) amenities.put("libraryAvailable", libraryAvailable);
            if (messAvailable != null) amenities.put("messAvailable", messAvailable);
            
            List<HostelDTO> hostels = hostelService.getHostelsByAmenities(ownerId, amenities);
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            log.error("Error filtering hostels by amenities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-fee-range")
    public ResponseEntity<List<HostelDTO>> getHostelsByFeeRange(
            @RequestHeader("X-User-ID") Long ownerId,
            @RequestParam Double minFee,
            @RequestParam Double maxFee) {
        try {
            List<HostelDTO> hostels = hostelService.getHostelsByFeeRange(ownerId, minFee, maxFee);
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            log.error("Error filtering hostels by fee range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== ROOM ENDPOINTS ====================

    @PostMapping("/{hostelId}/rooms")
    public ResponseEntity<RoomDTO> createRoom(
            @PathVariable Long hostelId,
            @Valid @RequestBody RoomRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            request.setHostelId(hostelId);
            RoomDTO room = hostelService.createRoom(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(room);
        } catch (Exception e) {
            log.error("Error creating room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{hostelId}/rooms")
    public ResponseEntity<Page<RoomDTO>> getRoomsByHostel(
            @PathVariable Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "roomNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<RoomDTO> rooms = hostelService.getRoomsByHostel(hostelId, ownerId, pageable);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("Error fetching rooms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/rooms/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @PathVariable Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            List<RoomDTO> rooms = hostelService.getAvailableRooms(hostelId, ownerId);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("Error fetching available rooms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/rooms/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            RoomDTO room = hostelService.getRoomById(roomId, ownerId);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            log.error("Error fetching room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{hostelId}/rooms/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            request.setHostelId(hostelId);
            RoomDTO room = hostelService.updateRoom(roomId, request, ownerId);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            log.error("Error updating room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{hostelId}/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            hostelService.deleteRoom(roomId, ownerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ==================== BED ENDPOINTS ====================

    @PostMapping("/{hostelId}/rooms/{roomId}/beds")
    public ResponseEntity<BedDTO> createBed(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @Valid @RequestBody BedRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            request.setRoomId(roomId);
            BedDTO bed = hostelService.createBed(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(bed);
        } catch (Exception e) {
            log.error("Error creating bed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{hostelId}/rooms/{roomId}/beds")
    public ResponseEntity<Page<BedDTO>> getBedsByRoom(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @RequestHeader("X-User-ID") Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bedNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<BedDTO> beds = hostelService.getBedsByRoom(roomId, ownerId, pageable);
            return ResponseEntity.ok(beds);
        } catch (Exception e) {
            log.error("Error fetching beds: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/rooms/{roomId}/beds/available")
    public ResponseEntity<List<BedDTO>> getAvailableBeds(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            List<BedDTO> beds = hostelService.getAvailableBeds(roomId, ownerId);
            return ResponseEntity.ok(beds);
        } catch (Exception e) {
            log.error("Error fetching available beds: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/beds/available")
    public ResponseEntity<List<BedDTO>> getAvailableBedsInHostel(
            @PathVariable Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            List<BedDTO> beds = hostelService.getAvailableBedsInHostel(hostelId, ownerId);
            return ResponseEntity.ok(beds);
        } catch (Exception e) {
            log.error("Error fetching available beds in hostel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/rooms/{roomId}/beds/{bedId}")
    public ResponseEntity<BedDTO> getBedById(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @PathVariable Long bedId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            BedDTO bed = hostelService.getBedById(bedId, ownerId);
            return ResponseEntity.ok(bed);
        } catch (Exception e) {
            log.error("Error fetching bed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{hostelId}/rooms/{roomId}/beds/{bedId}")
    public ResponseEntity<BedDTO> updateBed(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @PathVariable Long bedId,
            @Valid @RequestBody BedRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            request.setRoomId(roomId);
            BedDTO bed = hostelService.updateBed(bedId, request, ownerId);
            return ResponseEntity.ok(bed);
        } catch (Exception e) {
            log.error("Error updating bed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{hostelId}/rooms/{roomId}/beds/{bedId}")
    public ResponseEntity<Void> deleteBed(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @PathVariable Long bedId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            hostelService.deleteBed(bedId, ownerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting bed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{hostelId}/rooms/{roomId}/beds/{bedId}/maintenance")
    public ResponseEntity<BedDTO> markBedForMaintenance(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @PathVariable Long bedId,
            @RequestBody Map<String, String> maintenanceRequest,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            String notes = maintenanceRequest.get("notes");
            BedDTO bed = hostelService.markBedForMaintenance(bedId, notes, ownerId);
            return ResponseEntity.ok(bed);
        } catch (Exception e) {
            log.error("Error marking bed for maintenance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{hostelId}/rooms/{roomId}/beds/{bedId}/maintenance/complete")
    public ResponseEntity<BedDTO> completeBedMaintenance(
            @PathVariable Long hostelId,
            @PathVariable Long roomId,
            @PathVariable Long bedId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            BedDTO bed = hostelService.completeBedMaintenance(bedId, ownerId);
            return ResponseEntity.ok(bed);
        } catch (Exception e) {
            log.error("Error completing bed maintenance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ==================== RESIDENT ENDPOINTS ====================

    @PostMapping("/{hostelId}/residents")
    public ResponseEntity<HostelResidentDTO> checkInResident(
            @PathVariable Long hostelId,
            @Valid @RequestBody HostelResidentRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            request.setHostelId(hostelId);
            HostelResidentDTO resident = hostelService.checkInResident(request, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(resident);
        } catch (Exception e) {
            log.error("Error checking in resident: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{hostelId}/residents")
    public ResponseEntity<Page<HostelResidentDTO>> getResidentsByHostel(
            @PathVariable Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "checkInDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<HostelResidentDTO> residents = hostelService.getResidentsByHostel(hostelId, ownerId, pageable);
            return ResponseEntity.ok(residents);
        } catch (Exception e) {
            log.error("Error fetching residents: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/residents/current")
    public ResponseEntity<List<HostelResidentDTO>> getCurrentResidents(
            @PathVariable Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            List<HostelResidentDTO> residents = hostelService.getCurrentResidents(hostelId, ownerId);
            return ResponseEntity.ok(residents);
        } catch (Exception e) {
            log.error("Error fetching current residents: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/residents/{residentId}")
    public ResponseEntity<HostelResidentDTO> getResidentById(
            @PathVariable Long hostelId,
            @PathVariable Long residentId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            HostelResidentDTO resident = hostelService.getResidentById(residentId, ownerId);
            return ResponseEntity.ok(resident);
        } catch (Exception e) {
            log.error("Error fetching resident: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{hostelId}/residents/{residentId}")
    public ResponseEntity<HostelResidentDTO> updateResident(
            @PathVariable Long hostelId,
            @PathVariable Long residentId,
            @Valid @RequestBody HostelResidentRequestDTO request,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            request.setHostelId(hostelId);
            HostelResidentDTO resident = hostelService.updateResident(residentId, request, ownerId);
            return ResponseEntity.ok(resident);
        } catch (Exception e) {
            log.error("Error updating resident: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{hostelId}/residents/{residentId}/checkout")
    public ResponseEntity<HostelResidentDTO> checkOutResident(
            @PathVariable Long hostelId,
            @PathVariable Long residentId,
            @RequestBody Map<String, String> checkoutRequest,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            LocalDate checkOutDate = LocalDate.parse(checkoutRequest.get("checkOutDate"));
            HostelResidentDTO resident = hostelService.checkOutResident(residentId, checkOutDate, ownerId);
            return ResponseEntity.ok(resident);
        } catch (Exception e) {
            log.error("Error checking out resident: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{hostelId}/residents/{residentId}")
    public ResponseEntity<Void> deleteResident(
            @PathVariable Long hostelId,
            @PathVariable Long residentId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            hostelService.deleteResident(residentId, ownerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting resident: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{hostelId}/residents/overdue-fees")
    public ResponseEntity<List<HostelResidentDTO>> getResidentsWithOverdueFees(
            @PathVariable Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            List<HostelResidentDTO> residents = hostelService.getResidentsWithOverdueFees(ownerId);
            return ResponseEntity.ok(residents);
        } catch (Exception e) {
            log.error("Error fetching residents with overdue fees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{hostelId}/fee-statistics")
    public ResponseEntity<Map<String, Object>> getFeeStatistics(
            @PathVariable Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            Map<String, Object> statistics = hostelService.getFeeCollectionStatistics(hostelId, ownerId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error fetching fee statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== UTILITY ENDPOINTS ====================

    @GetMapping("/check-hostel-code")
    public ResponseEntity<Map<String, Boolean>> checkHostelCodeAvailability(
            @RequestParam String hostelCode,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            boolean available = hostelService.isHostelCodeAvailable(hostelCode, ownerId);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            log.error("Error checking hostel code availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-room-number")
    public ResponseEntity<Map<String, Boolean>> checkRoomNumberAvailability(
            @RequestParam String roomNumber,
            @RequestParam Long hostelId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            boolean available = hostelService.isRoomNumberAvailable(roomNumber, hostelId, ownerId);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            log.error("Error checking room number availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-bed-number")
    public ResponseEntity<Map<String, Boolean>> checkBedNumberAvailability(
            @RequestParam String bedNumber,
            @RequestParam Long roomId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            boolean available = hostelService.isBedNumberAvailable(bedNumber, roomId, ownerId);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            log.error("Error checking bed number availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-resident-id")
    public ResponseEntity<Map<String, Boolean>> checkResidentIdAvailability(
            @RequestParam String residentId,
            @RequestHeader("X-User-ID") Long ownerId) {
        try {
            boolean available = hostelService.isResidentIdAvailable(residentId, ownerId);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            log.error("Error checking resident ID availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
