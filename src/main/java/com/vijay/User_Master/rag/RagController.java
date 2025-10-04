package com.vijay.User_Master.rag;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// Removed Spring AI dependency
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RAG Controller for intelligent document retrieval and AI-powered responses
 * Provides context-aware assistance for school management tasks
 */
@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "RAG System", description = "Retrieval-Augmented Generation for intelligent school management assistance")
public class RagController {

    private final SimpleRagService ragService;

    /**
     * Get intelligent response using RAG
     */
    @PostMapping("/query")
    @Operation(summary = "Get intelligent response", description = "Get context-aware AI response using RAG")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getIntelligentResponse(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ExceptionUtil.createBuildResponseMessage("Query cannot be empty", HttpStatus.BAD_REQUEST);
        }

        log.info("Processing RAG query: {}", query);
        
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            String response = ragService.generateIntelligentResponse(query, ownerId);
            
            return ExceptionUtil.createBuildResponse(Map.of(
                "query", query,
                "response", response,
                "timestamp", System.currentTimeMillis()
            ), HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error processing RAG query: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error processing query: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve relevant documents for a query
     */
    @PostMapping("/documents")
    @Operation(summary = "Retrieve relevant documents", description = "Get documents relevant to the query")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> getRelevantDocuments(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ExceptionUtil.createBuildResponseMessage("Query cannot be empty", HttpStatus.BAD_REQUEST);
        }

        log.info("Retrieving documents for query: {}", query);
        
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            // Simplified document retrieval - return query info instead
            List<Map<String, Object>> documents = List.of(Map.of(
                "content", "Simplified RAG implementation - query: " + query,
                "metadata", Map.of("type", "simple", "ownerId", ownerId)
            ));
            
            List<Map<String, Object>> documentData = documents;
            
            return ExceptionUtil.createBuildResponse(Map.of(
                "query", query,
                "documents", documentData,
                "count", documents.size()
            ), HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error retrieving documents: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error retrieving documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add document to knowledge base
     */
    @PostMapping("/documents/add")
    @Operation(summary = "Add document to knowledge base", description = "Add school document for RAG retrieval")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> addDocument(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String type = (String) request.get("type");
        
        if (content == null || content.trim().isEmpty()) {
            return ExceptionUtil.createBuildResponseMessage("Content cannot be empty", HttpStatus.BAD_REQUEST);
        }
        
        if (type == null || type.trim().isEmpty()) {
            type = "general";
        }

        log.info("Adding document of type: {}", type);
        
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            ragService.addSchoolDocument(content, type, ownerId);
            
            return ExceptionUtil.createBuildResponseMessage("Document added successfully", HttpStatus.CREATED);
            
        } catch (Exception e) {
            log.error("Error adding document: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error adding document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Bulk add documents to knowledge base
     */
    @PostMapping("/documents/bulk")
    @Operation(summary = "Bulk add documents", description = "Add multiple school documents for RAG retrieval")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> addDocumentsBulk(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> contents = (List<String>) request.get("contents");
        String type = (String) request.get("type");
        
        if (contents == null || contents.isEmpty()) {
            return ExceptionUtil.createBuildResponseMessage("Contents cannot be empty", HttpStatus.BAD_REQUEST);
        }
        
        if (type == null || type.trim().isEmpty()) {
            type = "general";
        }

        log.info("Bulk adding {} documents of type: {}", contents.size(), type);
        
        try {
            Long ownerId = CommonUtils.getLoggedInUser().getId();
            ragService.addSchoolDocuments(contents, type, ownerId);
            
            return ExceptionUtil.createBuildResponseMessage(
                String.format("Successfully added %d documents", contents.size()), 
                HttpStatus.CREATED
            );
            
        } catch (Exception e) {
            log.error("Error bulk adding documents: {}", e.getMessage(), e);
            return ExceptionUtil.createBuildResponseMessage("Error bulk adding documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
