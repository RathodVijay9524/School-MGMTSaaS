package com.vijay.User_Master.config;

import com.vijay.User_Master.service.impl.AssignmentServiceImpl;
import com.vijay.User_Master.service.impl.AttendanceServiceImpl;
import com.vijay.User_Master.service.impl.AuthServiceImpl;
import com.vijay.User_Master.service.impl.DashboardServiceImpl;
import com.vijay.User_Master.service.impl.EventServiceImpl;
import com.vijay.User_Master.service.impl.ExamServiceImpl;
import com.vijay.User_Master.service.impl.FeeServiceImpl;
import com.vijay.User_Master.service.impl.FileServiceImpl;
import com.vijay.User_Master.service.impl.GradeServiceImpl;
import com.vijay.User_Master.service.impl.HomeServiceImpl;
import com.vijay.User_Master.service.impl.IDCardServiceImpl;
import com.vijay.User_Master.service.impl.LibraryServiceImpl;
import com.vijay.User_Master.service.impl.RefreshTokenServiceImpl;
import com.vijay.User_Master.service.impl.RoleManagementServiceImpl;
import com.vijay.User_Master.service.impl.RoleServiceImpl;
import com.vijay.User_Master.service.impl.SchoolClassServiceImpl;
import com.vijay.User_Master.service.impl.SchoolNotificationServiceImpl;
import com.vijay.User_Master.service.impl.SMSServiceImpl;
import com.vijay.User_Master.service.impl.SubjectServiceImpl;
import com.vijay.User_Master.service.impl.TimetableServiceImpl;
import com.vijay.User_Master.service.impl.TransferCertificateServiceImpl;
import com.vijay.User_Master.service.impl.UserServiceImpl;
import com.vijay.User_Master.service.impl.WhatsAppServiceImpl;
import com.vijay.User_Master.service.impl.WorkerUserServiceImpl;
import com.vijay.User_Master.service.impl.RagEnhancedServiceImpl;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
class ToolConfig {

    static record Note(String title, String body, LocalDateTime createdAt) {}

    static class NotesService {
        @Tool(description = "Create a note with a title and body; returns the created note")
        public Note createNote(String title, String body) {
            return new Note(title, body, LocalDateTime.now());
        }

        @Tool(description = "List sample FAQs for the product")
        public List<String> listFaqs() {
            return List.of(
                    "How do I reset my password?",
                    "Where can I see my orders?",
                    "How do I contact support?"
            );
        }
    }

    @Bean
    ToolCallbackProvider notesTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(new NotesService())
                .build();
    }

    /**
     * Single bean containing all school management tools
     * Combines all service implementations with @Tool annotations
     */
    @Bean
    ToolCallbackProvider allSchoolManagementTools(
        DashboardServiceImpl dashboardService,
        WorkerUserServiceImpl workerService,
        AttendanceServiceImpl attendanceService,
        FeeServiceImpl feeService,
        SchoolClassServiceImpl classService,
        SubjectServiceImpl subjectService,
        GradeServiceImpl gradeService,
        ExamServiceImpl examService,
        AssignmentServiceImpl assignmentService,
        LibraryServiceImpl libraryService,
        EventServiceImpl eventService,
        TransferCertificateServiceImpl tcService,
        SMSServiceImpl smsService,
        WhatsAppServiceImpl whatsAppService,
        SchoolNotificationServiceImpl notificationService,
        UserServiceImpl userService,
        RoleManagementServiceImpl roleService,
        RoleServiceImpl roleServiceAsync,
        IDCardServiceImpl idCardService,
        TimetableServiceImpl timetableService,
        FileServiceImpl fileService,
        HomeServiceImpl homeService,
        RagEnhancedServiceImpl ragEnhancedService
    ) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(
                    dashboardService,
                    workerService,
                    attendanceService,
                    feeService,
                    classService,
                    subjectService,
                    gradeService,
                    examService,
                    assignmentService,
                    libraryService,
                    eventService,
                    tcService,
                    smsService,
                    whatsAppService,
                    notificationService,
                    userService,
                    roleService,
                    roleServiceAsync,
                    idCardService,
                    timetableService,
                    fileService,
                    homeService,
                    ragEnhancedService
                )
                .build();
    }
}
