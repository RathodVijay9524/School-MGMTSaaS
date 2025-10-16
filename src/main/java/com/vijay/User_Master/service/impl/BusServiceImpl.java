package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.BusRequest;
import com.vijay.User_Master.dto.BusResponse;
import com.vijay.User_Master.entity.Bus;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.BusRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.BusService;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for Bus management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final UserRepository userRepository;

    @Override
    public BusResponse createBus(BusRequest request, Long ownerId) {
        log.info("Creating new bus for owner: {}", ownerId);
        
        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        
        // Check if bus number already exists for this owner
        if (busRepository.existsByBusNumberAndOwnerIdAndIsDeletedFalse(request.getBusNumber(), ownerId)) {
            throw new RuntimeException("Bus number already exists for this owner");
        }
        
        // Check if registration number already exists for this owner
        if (busRepository.existsByRegistrationNumberAndOwnerIdAndIsDeletedFalse(request.getRegistrationNumber(), ownerId)) {
            throw new RuntimeException("Registration number already exists for this owner");
        }
        
        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber())
                .busName(request.getBusName())
                .capacity(request.getCapacity())
                .make(request.getMake())
                .model(request.getModel())
                .yearOfManufacture(request.getYearOfManufacture())
                .registrationNumber(request.getRegistrationNumber())
                .insuranceExpiryDate(request.getInsuranceExpiryDate())
                .fitnessExpiryDate(request.getFitnessExpiryDate())
                .lastServiceDate(request.getLastServiceDate())
                .nextServiceDate(request.getNextServiceDate())
                .fuelCapacity(request.getFuelCapacity())
                .purchaseDate(request.getPurchaseDate())
                .purchasePrice(request.getPurchasePrice())
                .depreciationRate(request.getDepreciationRate())
                .currentValue(request.getCurrentValue())
                .gpsTrackingEnabled(request.getGpsTrackingEnabled())
                .cctvEnabled(request.getCctvEnabled())
                .wifiAvailable(request.getWifiAvailable())
                .acAvailable(request.getAcAvailable())
                .isActive(request.getIsActive())
                .isAvailable(request.getIsAvailable())
                .isUnderMaintenance(request.getIsUnderMaintenance())
                .notes(request.getNotes())
                .owner(owner)
                .build();
        
        Bus savedBus = busRepository.save(bus);
        log.info("Bus created successfully with ID: {}", savedBus.getId());
        
        return mapToResponse(savedBus);
    }

    @Override
    public BusResponse updateBus(Long id, BusRequest request, Long ownerId) {
        log.info("Updating bus with ID: {} for owner: {}", id, ownerId);
        
        Bus bus = busRepository.findById(id)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        
        // Check if bus number already exists for another bus of this owner
        if (!bus.getBusNumber().equals(request.getBusNumber()) && 
            busRepository.existsByBusNumberAndOwnerIdAndIsDeletedFalse(request.getBusNumber(), ownerId)) {
            throw new RuntimeException("Bus number already exists for this owner");
        }
        
        // Check if registration number already exists for another bus of this owner
        if (!bus.getRegistrationNumber().equals(request.getRegistrationNumber()) && 
            busRepository.existsByRegistrationNumberAndOwnerIdAndIsDeletedFalse(request.getRegistrationNumber(), ownerId)) {
            throw new RuntimeException("Registration number already exists for this owner");
        }
        
        // Update bus fields
        bus.setBusNumber(request.getBusNumber());
        bus.setBusName(request.getBusName());
        bus.setCapacity(request.getCapacity());
        bus.setMake(request.getMake());
        bus.setModel(request.getModel());
        bus.setYearOfManufacture(request.getYearOfManufacture());
        bus.setRegistrationNumber(request.getRegistrationNumber());
        bus.setInsuranceExpiryDate(request.getInsuranceExpiryDate());
        bus.setFitnessExpiryDate(request.getFitnessExpiryDate());
        bus.setLastServiceDate(request.getLastServiceDate());
        bus.setNextServiceDate(request.getNextServiceDate());
        bus.setFuelCapacity(request.getFuelCapacity());
        bus.setPurchaseDate(request.getPurchaseDate());
        bus.setPurchasePrice(request.getPurchasePrice());
        bus.setDepreciationRate(request.getDepreciationRate());
        bus.setCurrentValue(request.getCurrentValue());
        bus.setGpsTrackingEnabled(request.getGpsTrackingEnabled());
        bus.setCctvEnabled(request.getCctvEnabled());
        bus.setWifiAvailable(request.getWifiAvailable());
        bus.setAcAvailable(request.getAcAvailable());
        bus.setIsActive(request.getIsActive());
        bus.setIsAvailable(request.getIsAvailable());
        bus.setIsUnderMaintenance(request.getIsUnderMaintenance());
        bus.setNotes(request.getNotes());
        
        Bus updatedBus = busRepository.save(bus);
        log.info("Bus updated successfully with ID: {}", updatedBus.getId());
        
        return mapToResponse(updatedBus);
    }

    @Override
    @Transactional(readOnly = true)
    public BusResponse getBusById(Long id, Long ownerId) {
        log.info("Getting bus with ID: {} for owner: {}", id, ownerId);
        
        Bus bus = busRepository.findById(id)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        
        return mapToResponse(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusResponse> getAllBuses(Long ownerId, Pageable pageable) {
        log.info("Getting all buses for owner: {} with pagination", ownerId);
        
        Page<Bus> buses = busRepository.findByOwnerIdAndIsDeletedFalseOrderByBusNumberAsc(ownerId, pageable);
        
        List<BusResponse> responses = buses.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, buses.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getActiveBuses(Long ownerId) {
        log.info("Getting active buses for owner: {}", ownerId);
        
        List<Bus> buses = busRepository.findByOwnerIdAndIsActiveTrueAndIsDeletedFalseOrderByBusNumberAsc(ownerId);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getAvailableBuses(Long ownerId) {
        log.info("Getting available buses for owner: {}", ownerId);
        
        List<Bus> buses = busRepository.findByOwnerIdAndIsActiveTrueAndIsAvailableTrueAndIsUnderMaintenanceFalseAndIsDeletedFalseOrderByBusNumberAsc(ownerId);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesWithAvailableSeats(Long ownerId) {
        log.info("Getting buses with available seats for owner: {}", ownerId);
        
        List<Bus> buses = busRepository.findBusesWithAvailableSeats(ownerId);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesNeedingService(Long ownerId) {
        log.info("Getting buses needing service for owner: {}", ownerId);
        
        List<Bus> buses = busRepository.findBusesNeedingService(ownerId);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesWithExpiredDocuments(Long ownerId) {
        log.info("Getting buses with expired documents for owner: {}", ownerId);
        
        List<Bus> buses = busRepository.findBusesWithExpiredDocuments(ownerId);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesByFuelType(Long ownerId, String fuelType) {
        log.info("Getting buses by fuel type: {} for owner: {}", fuelType, ownerId);
        
        List<Bus> buses = busRepository.findByOwnerIdAndFuelTypeAndIsActiveTrueAndIsDeletedFalseOrderByBusNumberAsc(ownerId, fuelType);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesByMake(Long ownerId, String make) {
        log.info("Getting buses by make: {} for owner: {}", make, ownerId);
        
        List<Bus> buses = busRepository.findByOwnerIdAndMakeAndIsActiveTrueAndIsDeletedFalseOrderByBusNumberAsc(ownerId, make);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesByCapacityRange(Long ownerId, Integer minCapacity, Integer maxCapacity) {
        log.info("Getting buses by capacity range: {}-{} for owner: {}", minCapacity, maxCapacity, ownerId);
        
        List<Bus> buses = busRepository.findBusesByCapacityRange(ownerId, minCapacity, maxCapacity);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesByYearRange(Long ownerId, Integer startYear, Integer endYear) {
        log.info("Getting buses by year range: {}-{} for owner: {}", startYear, endYear, ownerId);
        
        List<Bus> buses = busRepository.findBusesByYearRange(ownerId, startYear, endYear);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getBusesUnderMaintenance(Long ownerId) {
        log.info("Getting buses under maintenance for owner: {}", ownerId);
        
        List<Bus> buses = busRepository.findByOwnerIdAndIsUnderMaintenanceTrueAndIsDeletedFalseOrderByBusNumberAsc(ownerId);
        
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusResponse> searchBuses(Long ownerId, String keyword, Pageable pageable) {
        log.info("Searching buses with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Bus> buses = busRepository.searchBuses(ownerId, keyword, pageable);
        
        List<BusResponse> responses = buses.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, buses.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBusStatistics(Long ownerId) {
        log.info("Getting bus statistics for owner: {}", ownerId);
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            Long totalBuses = busRepository.countByOwnerIdAndIsDeletedFalse(ownerId);
            Long activeBuses = busRepository.countByOwnerIdAndIsActiveTrueAndIsDeletedFalse(ownerId);
            Long availableBuses = busRepository.countByOwnerIdAndIsActiveTrueAndIsAvailableTrueAndIsDeletedFalse(ownerId);
            Long busesUnderMaintenance = busRepository.countByOwnerIdAndIsUnderMaintenanceTrueAndIsDeletedFalse(ownerId);
            
            stats.put("totalBuses", totalBuses);
            stats.put("activeBuses", activeBuses);
            stats.put("availableBuses", availableBuses);
            stats.put("busesUnderMaintenance", busesUnderMaintenance);
            
            return stats;
        } catch (Exception e) {
            log.error("Error getting bus statistics: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public void deleteBus(Long id, Long ownerId) {
        log.info("Deleting bus with ID: {} for owner: {}", id, ownerId);
        
        Bus bus = busRepository.findById(id)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        
        bus.setIsDeleted(true);
        busRepository.save(bus);
        
        log.info("Bus deleted successfully with ID: {}", id);
    }

    @Override
    public void restoreBus(Long id, Long ownerId) {
        log.info("Restoring bus with ID: {} for owner: {}", id, ownerId);
        
        Bus bus = busRepository.findById(id)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        
        bus.setIsDeleted(false);
        busRepository.save(bus);
        
        log.info("Bus restored successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBusNumber(Long ownerId, String busNumber) {
        return busRepository.existsByBusNumberAndOwnerIdAndIsDeletedFalse(busNumber, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRegistrationNumber(Long ownerId, String registrationNumber) {
        return busRepository.existsByRegistrationNumberAndOwnerIdAndIsDeletedFalse(registrationNumber, ownerId);
    }

    @Override
    public BusResponse assignBusToRoute(Long busId, Long routeId, Long ownerId) {
        log.info("Assigning bus {} to route {} for owner: {}", busId, routeId, ownerId);
        
        Bus bus = busRepository.findById(busId)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", busId));
        
        // TODO: Implement route assignment logic when Route entity is available
        
        return mapToResponse(bus);
    }

    @Override
    public BusResponse removeBusFromRoute(Long busId, Long ownerId) {
        log.info("Removing bus {} from route for owner: {}", busId, ownerId);
        
        Bus bus = busRepository.findById(busId)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", busId));
        
        // TODO: Implement route removal logic when Route entity is available
        
        return mapToResponse(bus);
    }

    @Override
    public BusResponse updateMaintenanceStatus(Long busId, boolean isUnderMaintenance, String reason, Long ownerId) {
        log.info("Updating maintenance status for bus {} to {} for owner: {}", busId, isUnderMaintenance, ownerId);
        
        Bus bus = busRepository.findById(busId)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", busId));
        
        bus.setIsUnderMaintenance(isUnderMaintenance);
        if (reason != null) {
            bus.setNotes(bus.getNotes() + "\nMaintenance: " + reason);
        }
        
        Bus updatedBus = busRepository.save(bus);
        
        return mapToResponse(updatedBus);
    }

    @Override
    public BusResponse updateBusCapacity(Long busId, Integer newCapacity, Long ownerId) {
        log.info("Updating capacity for bus {} to {} for owner: {}", busId, newCapacity, ownerId);
        
        Bus bus = busRepository.findById(busId)
                .filter(b -> b.getOwner().getId().equals(ownerId) && !b.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", busId));
        
        bus.setCapacity(newCapacity);
        
        Bus updatedBus = busRepository.save(bus);
        
        return mapToResponse(updatedBus);
    }

    /**
     * Map Bus entity to BusResponse DTO
     */
    private BusResponse mapToResponse(Bus bus) {
        return BusResponse.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .busName(bus.getBusName())
                .capacity(bus.getCapacity())
                .make(bus.getMake())
                .model(bus.getModel())
                .yearOfManufacture(bus.getYearOfManufacture())
                .registrationNumber(bus.getRegistrationNumber())
                .insuranceExpiryDate(bus.getInsuranceExpiryDate())
                .fitnessExpiryDate(bus.getFitnessExpiryDate())
                .lastServiceDate(bus.getLastServiceDate())
                .nextServiceDate(bus.getNextServiceDate())
                .fuelCapacity(bus.getFuelCapacity())
                .purchaseDate(bus.getPurchaseDate())
                .purchasePrice(bus.getPurchasePrice())
                .depreciationRate(bus.getDepreciationRate())
                .currentValue(bus.getCurrentValue())
                .gpsTrackingEnabled(bus.getGpsTrackingEnabled())
                .cctvEnabled(bus.getCctvEnabled())
                .wifiAvailable(bus.getWifiAvailable())
                .acAvailable(bus.getAcAvailable())
                .isActive(bus.getIsActive())
                .isAvailable(bus.getIsAvailable())
                .isUnderMaintenance(bus.getIsUnderMaintenance())
                .notes(bus.getNotes())
                .createdOn(bus.getCreatedOn() != null ? new java.sql.Date(bus.getCreatedOn().getTime()) : null)
                .updatedOn(bus.getUpdatedOn() != null ? new java.sql.Date(bus.getUpdatedOn().getTime()) : null)
                .build();
    }
}