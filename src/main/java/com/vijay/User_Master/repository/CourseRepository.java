package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);
    
    boolean existsByCourseCode(String courseCode);
    
    // Find by class
    List<Course> findBySchoolClass_IdAndIsDeletedFalse(Long classId);
    
    // Find by teacher
    List<Course> findByTeacher_IdAndIsDeletedFalse(Long teacherId);
    
    // Find by subject
    List<Course> findBySubject_IdAndIsDeletedFalse(Long subjectId);
    
    // Find by status
    Page<Course> findByStatusAndIsDeletedFalse(Course.CourseStatus status, Pageable pageable);
    
    // Find by semester
    List<Course> findBySemesterAndIsDeletedFalse(String semester);
    
    // Search courses
    @Query("SELECT c FROM Course c WHERE c.isDeleted = false AND " +
           "(LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchCourses(@Param("keyword") String keyword, Pageable pageable);
    
    // Find ongoing courses
    @Query("SELECT c FROM Course c WHERE c.status = 'ONGOING' AND c.isDeleted = false")
    List<Course> findOngoingCourses();
}

