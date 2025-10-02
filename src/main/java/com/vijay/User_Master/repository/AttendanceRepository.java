package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find by student and date
    Optional<Attendance> findByStudent_IdAndAttendanceDateAndSession(
            Long studentId, LocalDate attendanceDate, Attendance.AttendanceSession session);
    
    // Find by student
    Page<Attendance> findByStudent_Id(Long studentId, Pageable pageable);
    
    // Find by class and date
    List<Attendance> findBySchoolClass_IdAndAttendanceDate(Long classId, LocalDate attendanceDate);
    
    // Find by date range
    List<Attendance> findByStudent_IdAndAttendanceDateBetween(
            Long studentId, LocalDate startDate, LocalDate endDate);
    
    // Find by class and date range
    List<Attendance> findBySchoolClass_IdAndAttendanceDateBetween(
            Long classId, LocalDate startDate, LocalDate endDate);
    
    // Find absent students on a date
    @Query("SELECT a FROM Attendance a WHERE a.schoolClass.id = :classId AND " +
           "a.attendanceDate = :date AND a.status = 'ABSENT'")
    List<Attendance> findAbsentStudents(@Param("classId") Long classId, @Param("date") LocalDate date);
    
    // Get attendance percentage for student
    @Query("SELECT (COUNT(a) * 100.0 / " +
           "(SELECT COUNT(a2) FROM Attendance a2 WHERE a2.student.id = :studentId)) " +
           "FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT'")
    Double calculateAttendancePercentage(@Param("studentId") Long studentId);
    
    // Find by subject
    List<Attendance> findBySubject_IdAndAttendanceDate(Long subjectId, LocalDate date);
    
    // Check if attendance exists
    boolean existsByStudent_IdAndAttendanceDateAndSession(
            Long studentId, LocalDate attendanceDate, Attendance.AttendanceSession session);
    
    // Multi-tenancy: Find by business owner
    Page<Attendance> findByOwner_Id(Long ownerId, Pageable pageable);
    
    Page<Attendance> findByOwner_IdAndAttendanceDate(Long ownerId, LocalDate date, Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.owner.id = :ownerId AND " +
           "a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByOwnerAndDateRange(@Param("ownerId") Long ownerId, 
                                              @Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.owner.id = :ownerId AND " +
           "a.student.currentClass.id = :classId AND a.attendanceDate = :date")
    List<Attendance> findByOwnerClassAndDate(@Param("ownerId") Long ownerId, 
                                              @Param("classId") Long classId, 
                                              @Param("date") LocalDate date);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Attendance> findByIdAndOwner_Id(Long id, Long ownerId);
}

