package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // Find by class
    List<Timetable> findBySchoolClass_IdAndIsDeletedFalse(Long classId);
    
    // Find by class and day
    List<Timetable> findBySchoolClass_IdAndDayOfWeekAndIsDeletedFalse(
            Long classId, Timetable.DayOfWeek dayOfWeek);
    
    // Find by teacher
    List<Timetable> findByTeacher_IdAndIsDeletedFalse(Long teacherId);
    
    // Find by teacher and day
    List<Timetable> findByTeacher_IdAndDayOfWeekAndIsDeletedFalse(
            Long teacherId, Timetable.DayOfWeek dayOfWeek);
    
    // Find by subject
    List<Timetable> findBySubject_IdAndIsDeletedFalse(Long subjectId);
    
    // Find by academic year and semester
    List<Timetable> findByAcademicYearAndSemesterAndIsDeletedFalse(String academicYear, String semester);
    
    // Find by status
    List<Timetable> findByStatusAndIsDeletedFalse(Timetable.TimetableStatus status);
    
    // Check for time conflicts
    @Query("SELECT t FROM Timetable t WHERE t.teacher.id = :teacherId AND " +
           "t.dayOfWeek = :dayOfWeek AND t.status = 'ACTIVE' AND t.isDeleted = false AND " +
           "((t.startTime < :endTime AND t.endTime > :startTime))")
    List<Timetable> findTeacherTimeConflicts(
            @Param("teacherId") Long teacherId,
            @Param("dayOfWeek") Timetable.DayOfWeek dayOfWeek,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime);
    
    // Find by room number
    List<Timetable> findByRoomNumberAndIsDeletedFalse(String roomNumber);

    // Multi-tenant queries
    Page<Timetable> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    @Query("SELECT t FROM Timetable t WHERE t.owner.id = :ownerId AND t.isDeleted = false AND " +
           "(LOWER(t.schoolClass.className) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.subject.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.teacher.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Timetable> searchByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    long countByOwner_IdAndIsDeletedFalse(Long ownerId);
    
    @Query("SELECT t FROM Timetable t WHERE t.owner.id = :ownerId AND t.status = :status AND t.isDeleted = false")
    Page<Timetable> findByOwner_IdAndStatusAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("status") Timetable.TimetableStatus status, Pageable pageable);
    
    @Query("SELECT t FROM Timetable t WHERE t.owner.id = :ownerId AND t.dayOfWeek = :dayOfWeek AND t.isDeleted = false")
    List<Timetable> findByOwner_IdAndDayOfWeekAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("dayOfWeek") Timetable.DayOfWeek dayOfWeek);
    
    @Query("SELECT t FROM Timetable t WHERE t.owner.id = :ownerId AND t.schoolClass.id = :classId AND t.isDeleted = false")
    List<Timetable> findByOwner_IdAndSchoolClass_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("classId") Long classId);
    
    @Query("SELECT t FROM Timetable t WHERE t.owner.id = :ownerId AND t.teacher.id = :teacherId AND t.isDeleted = false")
    List<Timetable> findByOwner_IdAndTeacher_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("teacherId") Long teacherId);
    
    @Query("SELECT t FROM Timetable t WHERE t.owner.id = :ownerId AND t.subject.id = :subjectId AND t.isDeleted = false")
    List<Timetable> findByOwner_IdAndSubject_IdAndIsDeletedFalse(@Param("ownerId") Long ownerId, @Param("subjectId") Long subjectId);
    
    @Query("SELECT t FROM Timetable t WHERE t.owner.id = :ownerId AND t.teacher.id = :teacherId AND " +
           "t.dayOfWeek = :dayOfWeek AND t.status = 'ACTIVE' AND t.isDeleted = false AND " +
           "((t.startTime < :endTime AND t.endTime > :startTime))")
    List<Timetable> findOwnerTeacherTimeConflicts(
            @Param("ownerId") Long ownerId,
            @Param("teacherId") Long teacherId,
            @Param("dayOfWeek") Timetable.DayOfWeek dayOfWeek,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime);
    
    // SECURITY: Find by ID and Owner (prevents cross-school access)
    Optional<Timetable> findByIdAndOwner_Id(Long id, Long ownerId);
    
    Optional<Timetable> findByIdAndOwner_IdAndIsDeletedFalse(Long id, Long ownerId);
}

