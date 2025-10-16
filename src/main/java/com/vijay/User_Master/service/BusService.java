package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.BusRequest;
import com.vijay.User_Master.dto.BusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Bus management
 */
public interface BusService {

    /**
     * Create a new bus
     */
    BusResponse createBus(BusRequest request, Long ownerId);

    /**
     * Update an existing bus
     */
    BusResponse updateBus(Long id, BusRequest request, Long ownerId);

    /**
     * Get bus by ID
     */
    BusResponse getBusById(Long id, Long ownerId);

    /**
     * Get all buses for owner with pagination
     */
    Page<BusResponse> getAllBuses(Long ownerId, Pageable pageable);

    /**
     * Get active buses for owner
     */
    List<BusResponse> getActiveBuses(Long ownerId);

    /**
     * Get available buses (not under maintenance and active)
     */
    List<BusResponse> getAvailableBuses(Long ownerId);

    /**
     * Get buses with available seats
     */
    List<BusResponse> getBusesWithAvailableSeats(Long ownerId);

    /**
     * Get buses needing service
     */
    List<BusResponse> getBusesNeedingService(Long ownerId);

    /**
     * Get buses with expired documents
     */
    List<BusResponse> getBusesWithExpiredDocuments(Long ownerId);

    /**
     * Get buses by fuel type
     */
    List<BusResponse> getBusesByFuelType(Long ownerId, String fuelType);

    /**
     * Get buses by make
     */
    List<BusResponse> getBusesByMake(Long ownerId, String make);

    /**
     * Get buses by capacity range
     */
    List<BusResponse> getBusesByCapacityRange(Long ownerId, Integer minCapacity, Integer maxCapacity);

    /**
     * Get buses by year range
     */
    List<BusResponse> getBusesByYearRange(Long ownerId, Integer startYear, Integer endYear);

    /**
     * Get buses under maintenance
     */
    List<BusResponse> getBusesUnderMaintenance(Long ownerId);

    /**
     * Search buses by keyword
     */
    Page<BusResponse> searchBuses(Long ownerId, String keyword, Pageable pageable);

    /**
     * Get bus statistics
     */
    Map<String, Object> getBusStatistics(Long ownerId);

    /**
     * Delete bus (soft delete)
     */
    void deleteBus(Long id, Long ownerId);

    /**
     * Restore bus (undo soft delete)
     */
    void restoreBus(Long id, Long ownerId);

    /**
     * Check if bus number exists for owner
     */
    boolean existsByBusNumber(Long ownerId, String busNumber);

    /**
     * Check if registration number exists for owner
     */
    boolean existsByRegistrationNumber(Long ownerId, String registrationNumber);

    /**
     * Assign bus to route
     */
    BusResponse assignBusToRoute(Long busId, Long routeId, Long ownerId);

    /**
     * Remove bus from route
     */
    BusResponse removeBusFromRoute(Long busId, Long ownerId);

    /**
     * Update bus maintenance status
     */
    BusResponse updateMaintenanceStatus(Long busId, boolean isUnderMaintenance, String reason, Long ownerId);

    /**
     * Update bus capacity
     */
    BusResponse updateBusCapacity(Long busId, Integer newCapacity, Long ownerId);
}
