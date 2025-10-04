package com.vijay.User_Master.rag;

import com.vijay.User_Master.dto.ExamResponse;
import com.vijay.User_Master.dto.WorkerResponse;
import com.vijay.User_Master.dto.SubjectResponse;
import com.vijay.User_Master.entity.Document;
import com.vijay.User_Master.service.ExamService;
import com.vijay.User_Master.service.WorkerUserService;
import com.vijay.User_Master.service.SubjectService;
import com.vijay.User_Master.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple RAG Service for intelligent school data retrieval and response generation
 * Uses keyword matching and data aggregation instead of vector embeddings
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleRagService {

    private final ExamService examService;
    private final WorkerUserService workerUserService;
    private final SubjectService subjectService;
    private final DocumentService documentService;

    /**
     * Generate intelligent response based on school data
     */
    public String generateIntelligentResponse(String query, Long ownerId) {
        log.info("Generating intelligent response for query: {} by owner: {}", query, ownerId);
        
        try {
            String queryLower = query.toLowerCase();
            StringBuilder response = new StringBuilder();
            
            // Check for exam-related queries
            if (queryLower.contains("exam") || queryLower.contains("test") || queryLower.contains("assessment")) {
                response.append(getExamInformation(query, ownerId));
            }
            
            // Check for student-related queries
            if (queryLower.contains("student") || queryLower.contains("pupil") || queryLower.contains("classmate")) {
                response.append(getStudentInformation(query, ownerId));
            }
            
            // Check for subject-related queries
            if (queryLower.contains("subject") || queryLower.contains("course") || queryLower.contains("curriculum")) {
                response.append(getSubjectInformation(query, ownerId));
            }
            
            // Check for general school information
            if (queryLower.contains("school") || queryLower.contains("policy") || queryLower.contains("rule")) {
                response.append(getSchoolInformation(query, ownerId));
            }
            
            // Check for document-related queries
            if (queryLower.contains("document") || queryLower.contains("file") || queryLower.contains("report") || 
                queryLower.contains("policy") || queryLower.contains("manual") || queryLower.contains("guide")) {
                response.append(getDocumentInformation(query, ownerId));
            }
            
            // If no specific category matched, provide general assistance
            if (response.length() == 0) {
                response.append(getGeneralAssistance(query, ownerId));
            }
            
            return response.toString();
            
        } catch (Exception e) {
            log.error("Error generating intelligent response: {}", e.getMessage(), e);
            return "I apologize, but I encountered an error while processing your request. Please try again later.";
        }
    }

    /**
     * Get exam-related information
     */
    private String getExamInformation(String query, Long ownerId) {
        try {
            List<ExamResponse> exams = examService.getAllExams(ownerId, null).getContent();
            
            if (exams.isEmpty()) {
                return "📚 **Exam Information:**\nNo exams are currently scheduled in your school.\n\n";
            }
            
            StringBuilder examInfo = new StringBuilder();
            examInfo.append("📚 **Exam Information:**\n\n");
            
            // Count total exams
            examInfo.append("Total Exams: ").append(exams.size()).append("\n\n");
            
            // List upcoming exams
            List<ExamResponse> upcomingExams = exams.stream()
                    .filter(exam -> exam.getExamDate().isAfter(java.time.LocalDate.now()))
                    .collect(Collectors.toList());
            
            if (!upcomingExams.isEmpty()) {
                examInfo.append("**Upcoming Exams:**\n");
                for (ExamResponse exam : upcomingExams) {
                    examInfo.append("• ").append(exam.getExamName())
                            .append(" (").append(exam.getExamCode()).append(")")
                            .append(" - ").append(exam.getExamDate())
                            .append(" - ").append(exam.getSubjectName())
                            .append("\n");
                }
                examInfo.append("\n");
            }
            
            // Group by subject
            Map<String, Long> examsBySubject = exams.stream()
                    .collect(Collectors.groupingBy(
                            exam -> exam.getSubjectName() != null ? exam.getSubjectName() : "Unknown",
                            Collectors.counting()
                    ));
            
            if (!examsBySubject.isEmpty()) {
                examInfo.append("**Exams by Subject:**\n");
                examsBySubject.forEach((subject, count) -> 
                    examInfo.append("• ").append(subject).append(": ").append(count).append(" exams\n"));
            }
            
            return examInfo.toString();
            
        } catch (Exception e) {
            log.error("Error getting exam information: {}", e.getMessage());
            return "📚 **Exam Information:**\nUnable to retrieve exam information at this time.\n\n";
        }
    }

    /**
     * Get student-related information
     */
    private String getStudentInformation(String query, Long ownerId) {
        try {
            List<WorkerResponse> students = workerUserService.getWorkersBySuperUserId(ownerId, 0, 1000, "name", "asc").getContent().stream()
                    .filter(worker -> worker.getRoles().stream()
                            .anyMatch(role -> role.getName().equals("ROLE_STUDENT")))
                    .collect(Collectors.toList());
            
            if (students.isEmpty()) {
                return "👥 **Student Information:**\nNo students are currently enrolled in your school.\n\n";
            }
            
            StringBuilder studentInfo = new StringBuilder();
            studentInfo.append("👥 **Student Information:**\n\n");
            
            // Count total students
            studentInfo.append("Total Students: ").append(students.size()).append("\n\n");
            
            // Count by class
            Map<String, Long> studentsByClass = students.stream()
                    .collect(Collectors.groupingBy(
                            student -> student.getCurrentClassName() != null ? student.getCurrentClassName() : "Unassigned",
                            Collectors.counting()
                    ));
            
            if (!studentsByClass.isEmpty()) {
                studentInfo.append("**Students by Class:**\n");
                studentsByClass.forEach((className, count) -> 
                    studentInfo.append("• ").append(className).append(": ").append(count).append(" students\n"));
                studentInfo.append("\n");
            }
            
            // List some sample students
            studentInfo.append("**Sample Students:**\n");
            students.stream().limit(5).forEach(student -> 
                studentInfo.append("• ").append(student.getName())
                        .append(" (").append(student.getUsername()).append(")")
                        .append(" - ").append(student.getCurrentClassName())
                        .append("\n"));
            
            return studentInfo.toString();
            
        } catch (Exception e) {
            log.error("Error getting student information: {}", e.getMessage());
            return "👥 **Student Information:**\nUnable to retrieve student information at this time.\n\n";
        }
    }

    /**
     * Get subject-related information
     */
    private String getSubjectInformation(String query, Long ownerId) {
        try {
            List<SubjectResponse> subjects = subjectService.getAllSubjects(ownerId, null).getContent();
            
            if (subjects.isEmpty()) {
                return "📖 **Subject Information:**\nNo subjects are currently available in your school.\n\n";
            }
            
            StringBuilder subjectInfo = new StringBuilder();
            subjectInfo.append("📖 **Subject Information:**\n\n");
            
            // Count total subjects
            subjectInfo.append("Total Subjects: ").append(subjects.size()).append("\n\n");
            
            // List all subjects
            subjectInfo.append("**Available Subjects:**\n");
            for (SubjectResponse subject : subjects) {
                subjectInfo.append("• ").append(subject.getSubjectName())
                        .append(" (").append(subject.getSubjectCode()).append(")");
                if (subject.getDescription() != null && !subject.getDescription().isEmpty()) {
                    subjectInfo.append(" - ").append(subject.getDescription());
                }
                subjectInfo.append("\n");
            }
            
            return subjectInfo.toString();
            
        } catch (Exception e) {
            log.error("Error getting subject information: {}", e.getMessage());
            return "📖 **Subject Information:**\nUnable to retrieve subject information at this time.\n\n";
        }
    }

    /**
     * Get school policies and general information
     */
    private String getSchoolInformation(String query, Long ownerId) {
        StringBuilder schoolInfo = new StringBuilder();
        schoolInfo.append("🏫 **School Information:**\n\n");
        
        // Add common school policies
        schoolInfo.append("**School Policies:**\n");
        schoolInfo.append("• Attendance: Minimum 75% attendance required for examination eligibility\n");
        schoolInfo.append("• Uniform: Proper school uniform required during school hours\n");
        schoolInfo.append("• Mobile Phones: Not allowed in classrooms during teaching hours\n");
        schoolInfo.append("• Results: Examination results published within 15 days\n");
        schoolInfo.append("• Re-examination: Can be applied for within 7 days of result publication\n");
        schoolInfo.append("• Parent Meetings: Conducted quarterly for academic progress\n");
        schoolInfo.append("• Library: Books must be returned within 15 days\n");
        schoolInfo.append("• Discipline: Students must follow school rules and regulations\n\n");
        
        schoolInfo.append("**Academic Information:**\n");
        schoolInfo.append("• Curriculum: CBSE curriculum with regular assessments\n");
        schoolInfo.append("• Evaluation: Based on continuous assessment and final examinations\n");
        schoolInfo.append("• Grading: Grade point average calculated for all subjects\n");
        
        return schoolInfo.toString();
    }

    /**
     * Get document-related information from uploaded documents
     */
    private String getDocumentInformation(String query, Long ownerId) {
        try {
            // Search for relevant documents
            List<Document> relevantDocs = documentService.getDocumentsForRAGQuery(ownerId, query);
            
            if (relevantDocs.isEmpty()) {
                return "📄 **Document Information:**\nNo relevant documents found for your query.\n\n";
            }
            
            StringBuilder docInfo = new StringBuilder();
            docInfo.append("📄 **Document Information:**\n\n");
            docInfo.append("Found ").append(relevantDocs.size()).append(" relevant document(s):\n\n");
            
            for (Document doc : relevantDocs) {
                docInfo.append("**").append(doc.getOriginalFileName()).append("**\n");
                docInfo.append("• Type: ").append(doc.getFileType()).append("\n");
                docInfo.append("• Category: ").append(doc.getCategory()).append("\n");
                docInfo.append("• Uploaded: ").append(doc.getCreatedAt()).append("\n");
                
                if (doc.getDescription() != null && !doc.getDescription().isEmpty()) {
                    docInfo.append("• Description: ").append(doc.getDescription()).append("\n");
                }
                
                if (doc.getSummary() != null && !doc.getSummary().isEmpty()) {
                    docInfo.append("• Summary: ").append(doc.getSummary().substring(0, Math.min(200, doc.getSummary().length()))).append("...\n");
                }
                
                if (doc.getTags() != null && !doc.getTags().isEmpty()) {
                    docInfo.append("• Tags: ").append(doc.getTags()).append("\n");
                }
                
                docInfo.append("\n");
            }
            
            return docInfo.toString();
            
        } catch (Exception e) {
            log.error("Error getting document information: {}", e.getMessage());
            return "📄 **Document Information:**\nUnable to retrieve document information at this time.\n\n";
        }
    }

    /**
     * Get general assistance when query doesn't match specific categories
     */
    private String getGeneralAssistance(String query, Long ownerId) {
        StringBuilder assistance = new StringBuilder();
        assistance.append("🤖 **Intelligent Assistance:**\n\n");
        assistance.append("I can help you with information about:\n");
        assistance.append("• **Exams** - Upcoming exams, schedules, results\n");
        assistance.append("• **Students** - Student information, class details\n");
        assistance.append("• **Subjects** - Available subjects, curriculum\n");
        assistance.append("• **School Policies** - Rules, regulations, procedures\n");
        assistance.append("• **Documents** - Uploaded files, reports, manuals\n");
        assistance.append("• **Academic Information** - General school information\n\n");
        assistance.append("Please ask me about any of these topics for detailed information!\n\n");
        assistance.append("**Your Query:** ").append(query).append("\n");
        assistance.append("I understand you're asking about: ").append(query).append("\n");
        assistance.append("Feel free to be more specific about what you'd like to know!");
        
        return assistance.toString();
    }

    /**
     * Add document to knowledge base (simplified version)
     */
    public void addSchoolDocument(String content, String type, Long ownerId) {
        log.info("Adding school document: {} for owner: {}", type, ownerId);
        // In a real implementation, this would store the document
        // For now, we'll just log it
        log.info("Document added: Type={}, Content={}, OwnerId={}", type, content.substring(0, Math.min(100, content.length())), ownerId);
    }

    /**
     * Bulk add documents (simplified version)
     */
    public void addSchoolDocuments(List<String> contents, String type, Long ownerId) {
        log.info("Adding {} school documents of type: {} for owner: {}", contents.size(), type, ownerId);
        for (String content : contents) {
            addSchoolDocument(content, type, ownerId);
        }
    }
}
