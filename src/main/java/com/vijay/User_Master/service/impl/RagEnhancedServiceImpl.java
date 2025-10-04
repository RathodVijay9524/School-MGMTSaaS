package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.rag.SimpleRagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * RAG-Enhanced Service that combines traditional MCP tools with intelligent document retrieval
 * This service demonstrates how RAG can be integrated with existing MCP tools
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagEnhancedServiceImpl {

    private final SimpleRagService ragService;

    /**
     * RAG-Enhanced exam information retrieval
     * Combines traditional data access with intelligent document search
     */
    @Tool(name = "getIntelligentExamInfo", description = "Get comprehensive exam information using RAG for context-aware responses")
    public String getIntelligentExamInfo(String query, Long ownerId) {
        log.info("Getting intelligent exam info for query: {} by owner: {}", query, ownerId);
        
        try {
            // Use RAG to get contextually relevant information
            String ragResponse = ragService.generateIntelligentResponse(query, ownerId);
            
            // Format the response for better readability
            StringBuilder response = new StringBuilder();
            response.append("📚 Intelligent Exam Information:\n\n");
            response.append("Query: ").append(query).append("\n\n");
            response.append("Response: ").append(ragResponse).append("\n\n");
            response.append("This information is based on your school's actual data and policies.");
            
            return response.toString();
            
        } catch (Exception e) {
            log.error("Error getting intelligent exam info: {}", e.getMessage(), e);
            return "Sorry, I encountered an error while retrieving exam information. Please try again.";
        }
    }

    /**
     * RAG-Enhanced student assistance
     * Provides intelligent help based on student data and school policies
     */
    @Tool(name = "getStudentAssistance", description = "Get intelligent student assistance using RAG for personalized help")
    public String getStudentAssistance(String studentQuery, Long ownerId) {
        log.info("Getting student assistance for query: {} by owner: {}", studentQuery, ownerId);
        
        try {
            // Enhance the query with student context
            String enhancedQuery = "Student assistance: " + studentQuery;
            String ragResponse = ragService.generateIntelligentResponse(enhancedQuery, ownerId);
            
            StringBuilder response = new StringBuilder();
            response.append("🎓 Student Assistance:\n\n");
            response.append("Your Question: ").append(studentQuery).append("\n\n");
            response.append("Answer: ").append(ragResponse).append("\n\n");
            response.append("💡 Tip: This information is tailored to your school's specific policies and your academic data.");
            
            return response.toString();
            
        } catch (Exception e) {
            log.error("Error getting student assistance: {}", e.getMessage(), e);
            return "Sorry, I couldn't process your request right now. Please try again later.";
        }
    }

    /**
     * RAG-Enhanced administrative support
     * Provides intelligent administrative assistance based on school data
     */
    @Tool(name = "getAdministrativeSupport", description = "Get intelligent administrative support using RAG for school management tasks")
    public String getAdministrativeSupport(String adminQuery, Long ownerId) {
        log.info("Getting administrative support for query: {} by owner: {}", adminQuery, ownerId);
        
        try {
            // Enhance the query with administrative context
            String enhancedQuery = "Administrative support: " + adminQuery;
            String ragResponse = ragService.generateIntelligentResponse(enhancedQuery, ownerId);
            
            StringBuilder response = new StringBuilder();
            response.append("🏫 Administrative Support:\n\n");
            response.append("Query: ").append(adminQuery).append("\n\n");
            response.append("Response: ").append(ragResponse).append("\n\n");
            response.append("This response is based on your school's current data and established policies.");
            
            return response.toString();
            
        } catch (Exception e) {
            log.error("Error getting administrative support: {}", e.getMessage(), e);
            return "Sorry, I encountered an error while processing your administrative query. Please try again.";
        }
    }

    /**
     * RAG-Enhanced academic guidance
     * Provides intelligent academic guidance based on curriculum and student data
     */
    @Tool(name = "getAcademicGuidance", description = "Get intelligent academic guidance using RAG for personalized learning support")
    public String getAcademicGuidance(String academicQuery, Long ownerId) {
        log.info("Getting academic guidance for query: {} by owner: {}", academicQuery, ownerId);
        
        try {
            // Enhance the query with academic context
            String enhancedQuery = "Academic guidance: " + academicQuery;
            String ragResponse = ragService.generateIntelligentResponse(enhancedQuery, ownerId);
            
            StringBuilder response = new StringBuilder();
            response.append("📖 Academic Guidance:\n\n");
            response.append("Your Question: ").append(academicQuery).append("\n\n");
            response.append("Guidance: ").append(ragResponse).append("\n\n");
            response.append("This guidance is personalized based on your school's curriculum and your academic progress.");
            
            return response.toString();
            
        } catch (Exception e) {
            log.error("Error getting academic guidance: {}", e.getMessage(), e);
            return "Sorry, I couldn't provide academic guidance right now. Please try again later.";
        }
    }

    /**
     * RAG-Enhanced policy information
     * Provides intelligent policy information based on school documents
     */
    @Tool(name = "getPolicyInformation", description = "Get intelligent policy information using RAG for school rules and regulations")
    public String getPolicyInformation(String policyQuery, Long ownerId) {
        log.info("Getting policy information for query: {} by owner: {}", policyQuery, ownerId);
        
        try {
            // Enhance the query with policy context
            String enhancedQuery = "School policy: " + policyQuery;
            String ragResponse = ragService.generateIntelligentResponse(enhancedQuery, ownerId);
            
            StringBuilder response = new StringBuilder();
            response.append("📋 School Policy Information:\n\n");
            response.append("Query: ").append(policyQuery).append("\n\n");
            response.append("Policy Details: ").append(ragResponse).append("\n\n");
            response.append("This information is based on your school's official policies and procedures.");
            
            return response.toString();
            
        } catch (Exception e) {
            log.error("Error getting policy information: {}", e.getMessage(), e);
            return "Sorry, I couldn't retrieve policy information right now. Please contact the administration.";
        }
    }

    /**
     * RAG-Enhanced search across all school data
     * Universal search that can find information across all school documents
     */
    @Tool(name = "searchSchoolData", description = "Search across all school data using RAG for comprehensive information retrieval")
    public String searchSchoolData(String searchQuery, Long ownerId) {
        log.info("Searching school data for query: {} by owner: {}", searchQuery, ownerId);
        
        try {
            // Use RAG to search across all school data
            String ragResponse = ragService.generateIntelligentResponse(searchQuery, ownerId);
            
            StringBuilder response = new StringBuilder();
            response.append("🔍 School Data Search Results:\n\n");
            response.append("Search Query: ").append(searchQuery).append("\n\n");
            response.append("Results: ").append(ragResponse).append("\n\n");
            response.append("These results are based on a comprehensive search across your school's data.");
            
            return response.toString();
            
        } catch (Exception e) {
            log.error("Error searching school data: {}", e.getMessage(), e);
            return "Sorry, I couldn't search your school data right now. Please try again later.";
        }
    }

    /**
     * RAG-Enhanced document search
     * Search specifically within uploaded documents for relevant information
     */
    @Tool(name = "searchDocuments", description = "Search within uploaded documents using RAG for document-specific information")
    public String searchDocuments(String documentQuery, Long ownerId) {
        log.info("Searching documents for query: {} by owner: {}", documentQuery, ownerId);

        try {
            // Enhance the query with document context
            String enhancedQuery = "Document search: " + documentQuery;
            String ragResponse = ragService.generateIntelligentResponse(enhancedQuery, ownerId);

            StringBuilder response = new StringBuilder();
            response.append("📄 Document Search Results:\n\n");
            response.append("Query: ").append(documentQuery).append("\n\n");
            response.append("Results: ").append(ragResponse).append("\n\n");
            response.append("This search specifically looks within uploaded documents and files.");

            return response.toString();

        } catch (Exception e) {
            log.error("Error searching documents: {}", e.getMessage(), e);
            return "Sorry, I encountered an error while searching documents. Please try again.";
        }
    }

    /**
     * RAG-Enhanced file management assistance
     * Help with document organization and file management tasks
     */
    @Tool(name = "getFileManagementAssistance", description = "Get intelligent assistance with file management and document organization")
    public String getFileManagementAssistance(String fileQuery, Long ownerId) {
        log.info("Getting file management assistance for query: {} by owner: {}", fileQuery, ownerId);

        try {
            // Enhance the query with file management context
            String enhancedQuery = "File management: " + fileQuery;
            String ragResponse = ragService.generateIntelligentResponse(enhancedQuery, ownerId);

            StringBuilder response = new StringBuilder();
            response.append("📁 File Management Assistance:\n\n");
            response.append("Query: ").append(fileQuery).append("\n\n");
            response.append("Response: ").append(ragResponse).append("\n\n");
            response.append("This assistance helps with document organization and file management tasks.");

            return response.toString();

        } catch (Exception e) {
            log.error("Error getting file management assistance: {}", e.getMessage(), e);
            return "Sorry, I encountered an error while providing file management assistance. Please try again.";
        }
    }
}
