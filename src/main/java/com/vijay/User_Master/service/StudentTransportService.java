package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.StudentTransportRequest;
import com.vijay.User_Master.dto.StudentTransportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Service interface for StudentTransport management
 */
public interface StudentTransportService {

    /**
     * Create a new student transport assignment
     */
    StudentTransportResponse createStudentTransport(StudentTransportRequest request, Long ownerId);

    /**
     * Update an existing student transport assignment
     */
    StudentTransportResponse updateStudentTransport(Long id, StudentTransportRequest request, Long ownerId);

    /**
     * Get student transport by ID
     */
    StudentTransportResponse getStudentTransportById(Long id, Long ownerId);

    /**
     * Get all student transports for owner with pagination
     */
    Page<StudentTransportResponse> getAllStudentTransports(Long ownerId, Pageable pageable);

    /**
     * Get student transports by student
     */
    List<StudentTransportResponse> getStudentTransportsByStudent(Long studentId, Long ownerId);

    /**
     * Get active student transport by student
     */
    StudentTransportResponse getActiveTransportByStudent(Long studentId, Long ownerId);

    /**
     * Get student transports by bus
     */
    List<StudentTransportResponse> getStudentTransportsByBus(Long busId, Long ownerId);

    /**
     * Get active student transports by bus
     */
    List<StudentTransportResponse> getActiveStudentTransportsByBus(Long busId, Long ownerId);

    /**
     * Get student transports by route
     */
    List<StudentTransportResponse> getStudentTransportsByRoute(Long routeId, Long ownerId);

    /**
     * Get active student transports by route
     */
    List<StudentTransportResponse> getActiveStudentTransportsByRoute(Long routeId, Long ownerId);

    /**
     * Get student transports by driver
     */
    List<StudentTransportResponse> getStudentTransportsByDriver(Long driverId, Long ownerId);

    /**
     * Get active student transports by driver
     */
    List<StudentTransportResponse> getActiveStudentTransportsByDriver(Long driverId, Long ownerId);

    /**
     * Get student transports by pickup stop
     */
    List<StudentTransportResponse> getStudentTransportsByPickupStop(Long pickupStopId, Long ownerId);

    /**
     * Get student transports by drop stop
     */
    List<StudentTransportResponse> getStudentTransportsByDropStop(Long dropStopId, Long ownerId);

    /**
     * Get student transports with overdue payments
     */
    List<StudentTransportResponse> getTransportsWithOverduePayments(Long ownerId);

    /**
     * Get student transports by payment status
     */
    List<StudentTransportResponse> getStudentTransportsByPaymentStatus(Long ownerId, String paymentStatus);

    /**
     * Get student transports by assignment status
     */
    List<StudentTransportResponse> getStudentTransportsByAssignmentStatus(Long ownerId, String assignmentStatus);

    /**
     * Get active student transports
     */
    List<StudentTransportResponse> getActiveStudentTransports(Long ownerId);

    /**
     * Get student transports by fare range
     */
    List<StudentTransportResponse> getStudentTransportsByFareRange(Long ownerId, Double minFare, Double maxFare);

    /**
     * Get student transports by assignment date range
     */
    List<StudentTransportResponse> getStudentTransportsByAssignmentDateRange(Long ownerId, Date startDate, Date endDate);

    /**
     * Get student transports by effective date range
     */
    List<StudentTransportResponse> getStudentTransportsByEffectiveDateRange(Long ownerId, Date startDate, Date endDate);

    /**
     * Get student transports by attendance rate
     */
    List<StudentTransportResponse> getStudentTransportsByAttendanceRate(Long ownerId, Double minAttendance);

    /**
     * Get student transports with low attendance
     */
    List<StudentTransportResponse> getStudentTransportsWithLowAttendance(Long ownerId, Double threshold);

    /**
     * Get student transports with incidents
     */
    List<StudentTransportResponse> getStudentTransportsWithIncidents(Long ownerId);

    /**
     * Get student transports with complaints
     */
    List<StudentTransportResponse> getStudentTransportsWithComplaints(Long ownerId);

    /**
     * Get student transports by parent phone
     */
    List<StudentTransportResponse> getStudentTransportsByParentPhone(Long ownerId, String parentPhone);

    /**
     * Get student transports by parent email
     */
    List<StudentTransportResponse> getStudentTransportsByParentEmail(Long ownerId, String parentEmail);

    /**
     * Search student transports by keyword
     */
    Page<StudentTransportResponse> searchStudentTransports(Long ownerId, String keyword, Pageable pageable);

    /**
     * Get student transport statistics
     */
    Map<String, Object> getStudentTransportStatistics(Long ownerId);

    /**
     * Delete student transport (soft delete)
     */
    void deleteStudentTransport(Long id, Long ownerId);

    /**
     * Restore student transport (undo soft delete)
     */
    void restoreStudentTransport(Long id, Long ownerId);

    /**
     * Check if transport ID exists for owner
     */
    boolean existsByTransportId(Long ownerId, String transportId);

    /**
     * Check if student has active transport
     */
    boolean hasActiveTransport(Long studentId, Long ownerId);

    /**
     * Assign student to transport
     */
    StudentTransportResponse assignStudentToTransport(StudentTransportRequest request, Long ownerId);

    /**
     * Update student transport assignment
     */
    StudentTransportResponse updateStudentTransportAssignment(Long transportId, StudentTransportRequest request, Long ownerId);

    /**
     * Remove student from transport
     */
    StudentTransportResponse removeStudentFromTransport(Long transportId, Long ownerId);

    /**
     * Update payment status
     */
    StudentTransportResponse updatePaymentStatus(Long transportId, String paymentStatus, Double amount, Long ownerId);

    /**
     * Record transport attendance
     */
    StudentTransportResponse recordAttendance(Long transportId, boolean attended, Long ownerId);

    /**
     * Record transport incident
     */
    StudentTransportResponse recordIncident(Long transportId, String incidentDetails, Long ownerId);

    /**
     * Record transport complaint
     */
    StudentTransportResponse recordComplaint(Long transportId, String complaintDetails, Long ownerId);

    /**
     * Get student transport by multiple criteria
     */
    List<StudentTransportResponse> getStudentTransportsByCriteria(Long ownerId, Long busId, Long routeId, 
                                                                 Long driverId, String paymentStatus, 
                                                                 String assignmentStatus);

    /**
     * Generate transport ID
     */
    String generateTransportId(Long ownerId);

    /**
     * Calculate transport fees
     */
    Map<String, Object> calculateTransportFees(Long transportId, Long ownerId);

    /**
     * Update transport timing
     */
    StudentTransportResponse updateTransportTiming(Long transportId, StudentTransportRequest request, Long ownerId);
}
