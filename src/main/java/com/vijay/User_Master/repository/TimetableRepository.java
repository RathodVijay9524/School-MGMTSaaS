package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}

