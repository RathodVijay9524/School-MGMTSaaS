package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.CourseRequest;
import com.vijay.User_Master.dto.CourseResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourseServiceTest extends ServiceTestBase {

    @Mock
    private CourseService courseService;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void createCourse_withValidRequest_returnsCourseResponse() {
        CourseRequest request = new CourseRequest();
        request.setCourseName("Mathematics 101");
        request.setDescription("Basic Mathematics");

        CourseResponse mockResponse = new CourseResponse();
        mockResponse.setId(1L);
        mockResponse.setCourseName("Mathematics 101");

        when(courseService.createCourse(request, OWNER_ID)).thenReturn(mockResponse);

        CourseResponse response = courseService.createCourse(request, OWNER_ID);

        assertNotNull(response);
        assertEquals("Mathematics 101", response.getCourseName());
        verify(courseService).createCourse(request, OWNER_ID);
    }

    @Test
    void updateCourse_withValidData_returnsUpdatedCourse() {
        CourseRequest request = new CourseRequest();
        request.setCourseName("Updated Course");

        CourseResponse mockResponse = new CourseResponse();
        mockResponse.setId(1L);
        mockResponse.setCourseName("Updated Course");

        when(courseService.updateCourse(1L, request, OWNER_ID)).thenReturn(mockResponse);

        CourseResponse response = courseService.updateCourse(1L, request, OWNER_ID);

        assertNotNull(response);
        assertEquals("Updated Course", response.getCourseName());
    }

    @Test
    void getCourseById_withValidId_returnsCourse() {
        CourseResponse mockResponse = new CourseResponse();
        mockResponse.setId(1L);
        mockResponse.setCourseName("Mathematics 101");

        when(courseService.getCourseById(1L, OWNER_ID)).thenReturn(mockResponse);

        CourseResponse response = courseService.getCourseById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getCourseById_withInvalidId_throwsException() {
        when(courseService.getCourseById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Course not found"));

        assertThrows(RuntimeException.class, () ->
                courseService.getCourseById(999L, OWNER_ID));
    }

    @Test
    void getAllCourses_withValidOwner_returnsPagedCourses() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CourseResponse> courses = new ArrayList<>();
        courses.add(new CourseResponse());
        Page<CourseResponse> page = new PageImpl<>(courses, pageable, 1);

        when(courseService.getAllCourses(OWNER_ID, pageable)).thenReturn(page);

        Page<CourseResponse> response = courseService.getAllCourses(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getCoursesByDepartment_withValidDepartmentId_returnsCourses() {
        List<CourseResponse> mockCourses = new ArrayList<>();
        mockCourses.add(new CourseResponse());

        when(courseService.getCoursesByDepartment(1L, OWNER_ID)).thenReturn(mockCourses);

        List<CourseResponse> response = courseService.getCoursesByDepartment(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getCoursesByInstructor_withValidInstructorId_returnsCourses() {
        List<CourseResponse> mockCourses = new ArrayList<>();
        mockCourses.add(new CourseResponse());

        when(courseService.getCoursesByInstructor(1L, OWNER_ID)).thenReturn(mockCourses);

        List<CourseResponse> response = courseService.getCoursesByInstructor(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void searchCourses_withKeyword_returnsMatchingCourses() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CourseResponse> courses = new ArrayList<>();
        courses.add(new CourseResponse());
        Page<CourseResponse> page = new PageImpl<>(courses, pageable, 1);

        when(courseService.searchCourses("Math", OWNER_ID, pageable)).thenReturn(page);

        Page<CourseResponse> response = courseService.searchCourses("Math", OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void deleteCourse_withValidId_succeeds() {
        doNothing().when(courseService).deleteCourse(1L, OWNER_ID);

        courseService.deleteCourse(1L, OWNER_ID);

        verify(courseService).deleteCourse(1L, OWNER_ID);
    }

    @Test
    void publishCourse_withValidId_returnsPublishedCourse() {
        CourseResponse mockResponse = new CourseResponse();
        mockResponse.setId(1L);
        mockResponse.setStatus("PUBLISHED");

        when(courseService.publishCourse(1L, OWNER_ID)).thenReturn(mockResponse);

        CourseResponse response = courseService.publishCourse(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals("PUBLISHED", response.getStatus());
    }

    @Test
    void enrollStudent_withValidData_succeeds() {
        doNothing().when(courseService).enrollStudent(1L, 100L, OWNER_ID);

        courseService.enrollStudent(1L, 100L, OWNER_ID);

        verify(courseService).enrollStudent(1L, 100L, OWNER_ID);
    }

    @Test
    void getCourseStatistics_returnsStatistics() {
        Object mockStats = new Object();

        when(courseService.getCourseStatistics(OWNER_ID)).thenReturn(mockStats);

        Object response = courseService.getCourseStatistics(OWNER_ID);

        assertNotNull(response);
        verify(courseService).getCourseStatistics(OWNER_ID);
    }
}
