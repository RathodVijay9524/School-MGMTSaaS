package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.CourseRequest;
import com.vijay.User_Master.dto.CourseResponse;
import com.vijay.User_Master.dto.CourseStatistics;
import com.vijay.User_Master.entity.Course;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.BusinessRuleViolationException;
import com.vijay.User_Master.exceptions.EntityNotFoundException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.CourseRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for Course management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final WorkerRepository workerRepository;

    @Override
    @Tool(name = "createCourse", description = "Create a new course with course code, name, subject, class, teacher, dates, credits and maximum students")
    public CourseResponse createCourse(CourseRequest request, Long ownerId) {
        log.info("Creating course: {} for owner: {}", request.getCourseCode(), ownerId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner", ownerId));
        
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new BusinessRuleViolationException("Course code '" + request.getCourseCode() + "' already exists");
        }
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessRuleViolationException("End date cannot be before start date");
        }
        
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getSubjectId(), ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getClassId(), ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "id", request.getClassId()));
        
        Worker teacher = null;
        if (request.getTeacherId() != null) {
            teacher = workerRepository.findById(request.getTeacherId())
                    .filter(w -> w.getOwner() != null && w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getTeacherId()));
        }
        
        Course course = Course.builder()
                .courseCode(request.getCourseCode().toUpperCase())
                .courseName(request.getCourseName())
                .description(request.getDescription())
                .subject(subject)
                .schoolClass(schoolClass)
                .teacher(teacher)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .semester(request.getSemester())
                .credits(request.getCredits())
                .maxStudents(request.getMaxStudents())
                .enrolledStudents(request.getEnrolledStudents() != null ? request.getEnrolledStudents() : 0)
                .status(request.getStatus())
                .syllabus(request.getSyllabus())
                .schedule(request.getSchedule())
                .classroom(request.getClassroom())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getId());
        
        return convertToResponse(savedCourse);
    }

    @Override
    @Tool(name = "updateCourse", description = "Update course details")
    public CourseResponse updateCourse(Long id, CourseRequest request, Long ownerId) {
        log.info("Updating course: {} for owner: {}", id, ownerId);
        
        Course course = courseRepository.findById(id)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", id));
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessRuleViolationException("End date cannot be before start date");
        }
        
        if (!course.getCourseCode().equals(request.getCourseCode().toUpperCase())) {
            if (courseRepository.existsByCourseCode(request.getCourseCode())) {
                throw new BusinessRuleViolationException("Course code '" + request.getCourseCode() + "' already exists");
            }
            course.setCourseCode(request.getCourseCode().toUpperCase());
        }
        
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setSemester(request.getSemester());
        course.setCredits(request.getCredits());
        course.setMaxStudents(request.getMaxStudents());
        course.setStatus(request.getStatus());
        course.setSyllabus(request.getSyllabus());
        course.setSchedule(request.getSchedule());
        course.setClassroom(request.getClassroom());
        
        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated successfully");
        
        return convertToResponse(updatedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    @Tool(name = "getCourseById", description = "Get course details by course ID")
    public CourseResponse getCourseById(Long id, Long ownerId) {
        Course course = courseRepository.findById(id)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", id));
        return convertToResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseByCourseCode(String courseCode, Long ownerId) {
        Course course = courseRepository.findByCourseCode(courseCode.toUpperCase())
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "courseCode", courseCode));
        return convertToResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllCourses(Long ownerId, Pageable pageable) {
        Page<Course> courses = courseRepository.findAll(pageable);
        return courses.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByClass(Long classId, Long ownerId) {
        return courseRepository.findBySchoolClass_IdAndIsDeletedFalse(classId).stream()
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTeacher(Long teacherId, Long ownerId) {
        return courseRepository.findByTeacher_IdAndIsDeletedFalse(teacherId).stream()
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesBySubject(Long subjectId, Long ownerId) {
        return courseRepository.findBySubject_IdAndIsDeletedFalse(subjectId).stream()
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getCoursesByStatus(Course.CourseStatus status, Long ownerId, Pageable pageable) {
        return courseRepository.findByStatusAndIsDeletedFalse(status, pageable).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesBySemester(String semester, Long ownerId) {
        return courseRepository.findBySemesterAndIsDeletedFalse(semester).stream()
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getOngoingCourses(Long ownerId) {
        return courseRepository.findOngoingCourses().stream()
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchCourses(String keyword, Long ownerId, Pageable pageable) {
        return courseRepository.searchCourses(keyword, pageable).map(this::convertToResponse);
    }

    @Override
    public CourseResponse enrollStudent(Long courseId, Long studentId, Long ownerId) {
        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        // Check if course is full
        if (course.getEnrolledStudents() >= course.getMaxStudents()) {
            throw new BusinessRuleViolationException("Course is full. Cannot enroll more students.");
        }
        
        course.setEnrolledStudents(course.getEnrolledStudents() + 1);
        return convertToResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse unenrollStudent(Long courseId, Long studentId, Long ownerId) {
        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        
        if (course.getEnrolledStudents() <= 0) {
            throw new BusinessRuleViolationException("No students enrolled in this course.");
        }
        
        course.setEnrolledStudents(course.getEnrolledStudents() - 1);
        return convertToResponse(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getEnrolledStudents(Long courseId, Long ownerId) {
        courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        return new ArrayList<>(); // TODO: Implement with student-course junction table
    }

    @Override
    public CourseResponse assignTeacher(Long courseId, Long teacherId, Long ownerId) {
        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        
        Worker teacher = workerRepository.findById(teacherId)
                .filter(w -> w.getOwner() != null && w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));
        
        course.setTeacher(teacher);
        return convertToResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse removeTeacher(Long courseId, Long ownerId) {
        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        
        course.setTeacher(null);
        return convertToResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse changeCourseStatus(Long courseId, Course.CourseStatus newStatus, Long ownerId) {
        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        
        course.setStatus(newStatus);
        return convertToResponse(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long id, Long ownerId) {
        Course course = courseRepository.findById(id)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", id));
        
        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public void restoreCourse(Long id, Long ownerId) {
        Course course = courseRepository.findById(id)
                .filter(c -> c.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new EntityNotFoundException("Course", id));
        
        course.setDeleted(false);
        courseRepository.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseStatistics getCourseStatistics(Long ownerId) {
        List<Course> allCourses = courseRepository.findAll().stream()
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .collect(Collectors.toList());
        
        long totalCourses = allCourses.size();
        Map<String, Long> statusMap = allCourses.stream()
                .collect(Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting()));
        
        long totalEnrolled = allCourses.stream().mapToLong(Course::getEnrolledStudents).sum();
        long totalSeats = allCourses.stream().mapToLong(Course::getMaxStudents).sum();
        
        Map<String, Long> coursesBySubject = allCourses.stream()
                .collect(Collectors.groupingBy(c -> c.getSubject().getSubjectName(), Collectors.counting()));
        
        Map<String, Long> coursesByClass = allCourses.stream()
                .collect(Collectors.groupingBy(c -> c.getSchoolClass().getClassName(), Collectors.counting()));
        
        return CourseStatistics.builder()
                .totalCourses(totalCourses)
                .activeCourses(totalCourses - statusMap.getOrDefault("CANCELLED", 0L))
                .plannedCourses(statusMap.getOrDefault("PLANNED", 0L))
                .ongoingCourses(statusMap.getOrDefault("ONGOING", 0L))
                .completedCourses(statusMap.getOrDefault("COMPLETED", 0L))
                .cancelledCourses(statusMap.getOrDefault("CANCELLED", 0L))
                .totalEnrolledStudents(totalEnrolled)
                .totalAvailableSeats(totalSeats - totalEnrolled)
                .averageEnrollmentPercentage(totalSeats > 0 ? (double) totalEnrolled / totalSeats * 100 : 0.0)
                .coursesBySubject(coursesBySubject)
                .coursesByClass(coursesByClass)
                .coursesByStatus(statusMap)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCourseCodeExists(String courseCode, Long ownerId) {
        return courseRepository.existsByCourseCode(courseCode.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCourseFull(Long courseId, Long ownerId) {
        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        return course.getEnrolledStudents() >= course.getMaxStudents();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableSeats(Long courseId, Long ownerId) {
        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getOwner().getId().equals(ownerId) && !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId));
        return course.getMaxStudents() - course.getEnrolledStudents();
    }

    private CourseResponse convertToResponse(Course course) {
        LocalDate now = LocalDate.now();
        Integer availableSeats = course.getMaxStudents() - course.getEnrolledStudents();
        Double enrollmentPercentage = (double) course.getEnrolledStudents() / course.getMaxStudents() * 100;
        long totalDurationDays = ChronoUnit.DAYS.between(course.getStartDate(), course.getEndDate());
        
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .subjectId(course.getSubject().getId())
                .subjectName(course.getSubject().getSubjectName())
                .subjectCode(course.getSubject().getSubjectCode())
                .classId(course.getSchoolClass().getId())
                .className(course.getSchoolClass().getClassName())
                .classGrade(String.valueOf(course.getSchoolClass().getClassLevel()))
                .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getName() : null)
                .teacherEmail(course.getTeacher() != null ? course.getTeacher().getEmail() : null)
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .semester(course.getSemester())
                .credits(course.getCredits())
                .maxStudents(course.getMaxStudents())
                .enrolledStudents(course.getEnrolledStudents())
                .status(course.getStatus())
                .syllabus(course.getSyllabus())
                .schedule(course.getSchedule())
                .classroom(course.getClassroom())
                .ownerId(course.getOwner().getId())
                .ownerName(course.getOwner().getName())
                .isDeleted(course.isDeleted())
                .createdOn(course.getCreatedOn())
                .updatedOn(course.getUpdatedOn())
                .availableSeats(availableSeats)
                .enrollmentPercentage(enrollmentPercentage)
                .isFullyEnrolled(course.getEnrolledStudents() >= course.getMaxStudents())
                .hasAvailableSeats(availableSeats > 0)
                .isOngoing(course.getStatus() == Course.CourseStatus.ONGOING)
                .isCompleted(course.getStatus() == Course.CourseStatus.COMPLETED)
                .isUpcoming(course.getStartDate().isAfter(now))
                .daysUntilStart(ChronoUnit.DAYS.between(now, course.getStartDate()))
                .daysUntilEnd(ChronoUnit.DAYS.between(now, course.getEndDate()))
                .totalDurationDays(totalDurationDays)
                .statusDisplay(course.getStatus().name().replace("_", " "))
                .enrollmentDisplay(String.format("%d/%d (%.1f%%)", course.getEnrolledStudents(), course.getMaxStudents(), enrollmentPercentage))
                .build();
    }
}
