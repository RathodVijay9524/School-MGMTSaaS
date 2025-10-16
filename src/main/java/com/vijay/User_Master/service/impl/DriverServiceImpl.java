package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.DriverRequest;
import com.vijay.User_Master.dto.DriverResponse;
import com.vijay.User_Master.entity.Driver;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.DriverRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.DriverService;
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
 * Service implementation for Driver management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @Override
    public DriverResponse createDriver(DriverRequest request, Long ownerId) {
        log.info("Creating new driver for owner: {}", ownerId);
        
        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        
        Driver driver = Driver.builder()
                .employeeId(request.getEmployeeId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .licenseNumber(request.getLicenseNumber())
                .licenseType(request.getLicenseType())
                .licenseExpiryDate(request.getLicenseExpiryDate())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .yearsOfExperience(request.getYearsOfExperience())
                .salary(request.getSalary())
                .medicalCertificateExpiry(request.getMedicalCertificateExpiry())
                .joiningDate(request.getJoiningDate())
                .employmentStatus(request.getEmploymentStatus())
                .isAvailable(request.getIsAvailable())
                .isOnDuty(request.getIsOnDuty())
                .accidentsCount(request.getAccidentsCount())
                .violationsCount(request.getViolationsCount())
                .performanceRating(request.getPerformanceRating())
                .lastPerformanceReview(request.getLastPerformanceReview())
                .trainingCompleted(request.getTrainingCompleted())
                .notes(request.getNotes())
                .owner(owner)
                .build();
        
        Driver savedDriver = driverRepository.save(driver);
        log.info("Driver created successfully with ID: {}", savedDriver.getId());
        
        return mapToResponse(savedDriver);
    }

    @Override
    public DriverResponse updateDriver(Long id, DriverRequest request, Long ownerId) {
        log.info("Updating driver with ID: {} for owner: {}", id, ownerId);
        
        Driver driver = driverRepository.findById(id)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        // Update driver fields
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setEmail(request.getEmail());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseType(request.getLicenseType());
        driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setAddress(request.getAddress());
        driver.setYearsOfExperience(request.getYearsOfExperience());
        driver.setSalary(request.getSalary());
        driver.setMedicalCertificateExpiry(request.getMedicalCertificateExpiry());
        driver.setJoiningDate(request.getJoiningDate());
        driver.setEmploymentStatus(request.getEmploymentStatus());
        driver.setIsAvailable(request.getIsAvailable());
        driver.setIsOnDuty(request.getIsOnDuty());
        driver.setAccidentsCount(request.getAccidentsCount());
        driver.setViolationsCount(request.getViolationsCount());
        driver.setPerformanceRating(request.getPerformanceRating());
        driver.setLastPerformanceReview(request.getLastPerformanceReview());
        driver.setTrainingCompleted(request.getTrainingCompleted());
        driver.setNotes(request.getNotes());
        
        Driver updatedDriver = driverRepository.save(driver);
        log.info("Driver updated successfully with ID: {}", updatedDriver.getId());
        
        return mapToResponse(updatedDriver);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponse getDriverById(Long id, Long ownerId) {
        log.info("Getting driver with ID: {} for owner: {}", id, ownerId);
        
        Driver driver = driverRepository.findById(id)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        return mapToResponse(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponse> getAllDrivers(Long ownerId, Pageable pageable) {
        log.info("Getting all drivers for owner: {} with pagination", ownerId);
        
        Page<Driver> drivers = driverRepository.findByOwnerIdAndIsDeletedFalseOrderByFirstNameAsc(ownerId, pageable);
        
        List<DriverResponse> responses = drivers.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, drivers.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getActiveDrivers(Long ownerId) {
        log.info("Getting active drivers for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findByOwnerIdAndEmploymentStatusAndIsDeletedFalseOrderByFirstNameAsc(ownerId, "ACTIVE");
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getAvailableDrivers(Long ownerId) {
        log.info("Getting available drivers for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findAvailableDrivers(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByBus(Long busId, Long ownerId) {
        log.info("Getting drivers by bus: {} for owner: {}", busId, ownerId);
        
        List<Driver> drivers = driverRepository.findByBusIdAndIsDeletedFalseOrderByFirstNameAsc(busId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByRoute(Long routeId, Long ownerId) {
        log.info("Getting drivers by route: {} for owner: {}", routeId, ownerId);
        
        List<Driver> drivers = driverRepository.findByRouteIdAndIsDeletedFalseOrderByFirstNameAsc(routeId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithExpiringLicenses(Long ownerId, java.sql.Date expiryDate) {
        log.info("Getting drivers with expiring licenses for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithExpiringLicenses(ownerId, expiryDate);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithExpiredLicenses(Long ownerId) {
        log.info("Getting drivers with expired licenses for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithExpiredLicenses(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByLicenseType(Long ownerId, String licenseType) {
        log.info("Getting drivers by license type: {} for owner: {}", licenseType, ownerId);
        
        List<Driver> drivers = driverRepository.findByOwnerIdAndLicenseTypeAndIsDeletedFalseOrderByFirstNameAsc(ownerId, licenseType);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByExperienceRange(Long ownerId, Integer minExperience, Integer maxExperience) {
        log.info("Getting drivers by experience range: {}-{} for owner: {}", minExperience, maxExperience, ownerId);
        
        List<Driver> drivers = driverRepository.findDriversByExperienceRange(ownerId, minExperience, maxExperience);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByPerformanceRating(Long ownerId, Double minRating) {
        log.info("Getting drivers by performance rating: {} for owner: {}", minRating, ownerId);
        
        List<Driver> drivers = driverRepository.findDriversByPerformanceRating(ownerId, minRating);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithAccidents(Long ownerId) {
        log.info("Getting drivers with accidents for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithAccidents(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithViolations(Long ownerId) {
        log.info("Getting drivers with violations for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithViolations(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversOnDuty(Long ownerId) {
        log.info("Getting drivers on duty for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversOnDuty(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithoutBus(Long ownerId) {
        log.info("Getting drivers without bus for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithoutBus(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithoutRoute(Long ownerId) {
        log.info("Getting drivers without route for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithoutRoute(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversBySalaryRange(Long ownerId, Double minSalary, Double maxSalary) {
        log.info("Getting drivers by salary range: {}-{} for owner: {}", minSalary, maxSalary, ownerId);
        
        List<Driver> drivers = driverRepository.findDriversBySalaryRange(ownerId, minSalary, maxSalary);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByAgeRange(Long ownerId, Integer minAge, Integer maxAge) {
        log.info("Getting drivers by age range: {}-{} for owner: {}", minAge, maxAge, ownerId);
        
        List<Driver> drivers = driverRepository.findDriversByAgeRange(ownerId, minAge, maxAge);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByGender(Long ownerId, String gender) {
        log.info("Getting drivers by gender: {} for owner: {}", gender, ownerId);
        
        List<Driver> drivers = driverRepository.findByOwnerIdAndGenderAndIsDeletedFalseOrderByFirstNameAsc(ownerId, gender);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversNeedingTraining(Long ownerId) {
        log.info("Getting drivers needing training for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversNeedingTraining(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getTopPerformingDrivers(Long ownerId) {
        log.info("Getting top performing drivers for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findTopPerformingDrivers(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponse> searchDrivers(Long ownerId, String keyword, Pageable pageable) {
        log.info("Searching drivers with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Driver> drivers = driverRepository.searchDrivers(ownerId, keyword, pageable);
        
        List<DriverResponse> responses = drivers.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, drivers.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDriverStatistics(Long ownerId) {
        log.info("Getting driver statistics for owner: {}", ownerId);
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            Long totalDrivers = driverRepository.countByOwnerIdAndIsDeletedFalse(ownerId);
            Long activeDrivers = driverRepository.countByOwnerIdAndEmploymentStatusAndIsDeletedFalse(ownerId, "ACTIVE");
            Long availableDrivers = driverRepository.countAvailableDrivers(ownerId);
            Long driversOnDuty = driverRepository.countDriversOnDuty(ownerId);
            
            stats.put("totalDrivers", totalDrivers);
            stats.put("activeDrivers", activeDrivers);
            stats.put("availableDrivers", availableDrivers);
            stats.put("driversOnDuty", driversOnDuty);
            
            return stats;
        } catch (Exception e) {
            log.error("Error getting driver statistics: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithExpiringMedicalCertificates(Long ownerId, java.sql.Date expiryDate) {
        log.info("Getting drivers with expiring medical certificates for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithExpiringMedicalCertificates(ownerId, expiryDate);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversWithExpiredMedicalCertificates(Long ownerId) {
        log.info("Getting drivers with expired medical certificates for owner: {}", ownerId);
        
        List<Driver> drivers = driverRepository.findDriversWithExpiredMedicalCertificates(ownerId);
        
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDriver(Long id, Long ownerId) {
        log.info("Deleting driver with ID: {} for owner: {}", id, ownerId);
        
        Driver driver = driverRepository.findById(id)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        driver.setIsDeleted(true);
        driverRepository.save(driver);
        
        log.info("Driver deleted successfully with ID: {}", id);
    }

    @Override
    public void restoreDriver(Long id, Long ownerId) {
        log.info("Restoring driver with ID: {} for owner: {}", id, ownerId);
        
        Driver driver = driverRepository.findById(id)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        driver.setIsDeleted(false);
        driverRepository.save(driver);
        
        log.info("Driver restored successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmployeeId(Long ownerId, String employeeId) {
        return driverRepository.existsByEmployeeIdAndOwnerIdAndIsDeletedFalse(employeeId, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByLicenseNumber(Long ownerId, String licenseNumber) {
        return driverRepository.existsByLicenseNumberAndOwnerIdAndIsDeletedFalse(licenseNumber, ownerId);
    }

    @Override
    public DriverResponse assignDriverToBus(Long driverId, Long busId, Long ownerId) {
        log.info("Assigning driver {} to bus {} for owner: {}", driverId, busId, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        // TODO: Implement bus assignment logic when Bus entity is available
        
        return mapToResponse(driver);
    }

    @Override
    public DriverResponse assignDriverToRoute(Long driverId, Long routeId, Long ownerId) {
        log.info("Assigning driver {} to route {} for owner: {}", driverId, routeId, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        // TODO: Implement route assignment logic when Route entity is available
        
        return mapToResponse(driver);
    }

    @Override
    public DriverResponse removeDriverFromBus(Long driverId, Long ownerId) {
        log.info("Removing driver {} from bus for owner: {}", driverId, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        // TODO: Implement bus removal logic when Bus entity is available
        
        return mapToResponse(driver);
    }

    @Override
    public DriverResponse removeDriverFromRoute(Long driverId, Long ownerId) {
        log.info("Removing driver {} from route for owner: {}", driverId, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        // TODO: Implement route removal logic when Route entity is available
        
        return mapToResponse(driver);
    }

    @Override
    public DriverResponse updateDriverAvailability(Long driverId, boolean isAvailable, Long ownerId) {
        log.info("Updating availability for driver {} to {} for owner: {}", driverId, isAvailable, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        driver.setIsAvailable(isAvailable);
        
        Driver updatedDriver = driverRepository.save(driver);
        
        return mapToResponse(updatedDriver);
    }

    @Override
    public DriverResponse updateDriverDutyStatus(Long driverId, boolean isOnDuty, String location, Long ownerId) {
        log.info("Updating duty status for driver {} to {} for owner: {}", driverId, isOnDuty, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        driver.setIsOnDuty(isOnDuty);
        if (location != null) {
            driver.setCurrentLocation(location);
        }
        
        Driver updatedDriver = driverRepository.save(driver);
        
        return mapToResponse(updatedDriver);
    }

    @Override
    public DriverResponse updateDriverPerformance(Long driverId, Double performanceRating, String review, Long ownerId) {
        log.info("Updating performance for driver {} to {} for owner: {}", driverId, performanceRating, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        driver.setPerformanceRating(performanceRating);
        if (review != null) {
            driver.setNotes(driver.getNotes() + "\nPerformance Review: " + review);
        }
        
        Driver updatedDriver = driverRepository.save(driver);
        
        return mapToResponse(updatedDriver);
    }

    @Override
    public DriverResponse recordAccident(Long driverId, String accidentDetails, Long ownerId) {
        log.info("Recording accident for driver {} for owner: {}", driverId, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        driver.setAccidentsCount(driver.getAccidentsCount() + 1);
        if (accidentDetails != null) {
            driver.setNotes(driver.getNotes() + "\nAccident: " + accidentDetails);
        }
        
        Driver updatedDriver = driverRepository.save(driver);
        
        return mapToResponse(updatedDriver);
    }

    @Override
    public DriverResponse recordViolation(Long driverId, String violationDetails, Long ownerId) {
        log.info("Recording violation for driver {} for owner: {}", driverId, ownerId);
        
        Driver driver = driverRepository.findById(driverId)
                .filter(d -> d.getOwner().getId().equals(ownerId) && !d.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        driver.setViolationsCount(driver.getViolationsCount() + 1);
        if (violationDetails != null) {
            driver.setNotes(driver.getNotes() + "\nViolation: " + violationDetails);
        }
        
        Driver updatedDriver = driverRepository.save(driver);
        
        return mapToResponse(updatedDriver);
    }

    /**
     * Map Driver entity to DriverResponse DTO
     */
    private DriverResponse mapToResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .employeeId(driver.getEmployeeId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .phoneNumber(driver.getPhoneNumber())
                .email(driver.getEmail())
                .licenseNumber(driver.getLicenseNumber())
                .licenseType(driver.getLicenseType())
                .licenseExpiryDate(driver.getLicenseExpiryDate())
                .dateOfBirth(driver.getDateOfBirth())
                .address(driver.getAddress())
                .yearsOfExperience(driver.getYearsOfExperience())
                .salary(driver.getSalary())
                .medicalCertificateExpiry(driver.getMedicalCertificateExpiry())
                .joiningDate(driver.getJoiningDate())
                .employmentStatus(driver.getEmploymentStatus())
                .isAvailable(driver.getIsAvailable())
                .isOnDuty(driver.getIsOnDuty())
                .accidentsCount(driver.getAccidentsCount())
                .violationsCount(driver.getViolationsCount())
                .performanceRating(driver.getPerformanceRating())
                .lastPerformanceReview(driver.getLastPerformanceReview())
                .trainingCompleted(driver.getTrainingCompleted())
                .notes(driver.getNotes())
                .createdOn(driver.getCreatedOn() != null ? new java.sql.Date(driver.getCreatedOn().getTime()) : null)
                .updatedOn(driver.getUpdatedOn() != null ? new java.sql.Date(driver.getUpdatedOn().getTime()) : null)
                .build();
    }
}
