package com.vijay.User_Master.config.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore; // <-- This import will now work
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolFinderService {

    private static final Logger logger = LoggerFactory.getLogger(ToolFinderService.class);

    // We can now use the VectorStore INTERFACE
    private final VectorStore vectorStore;

    public ToolFinderService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<String> findToolsFor(String prompt) {
        logger.info("ToolFinder: Searching for tools matching prompt: {}", prompt);
        
        SearchRequest request = SearchRequest.builder()
                .query(prompt)
                .topK(3)
                //.similarityThreshold(0.7)
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(request);
        logger.info("ToolFinder: Found {} similar documents", similarDocuments.size());

        List<String> toolNames = similarDocuments.stream()
                .map(doc -> {
                    Object toolNameObj = doc.getMetadata().get("toolName");
                    String toolName = toolNameObj != null ? toolNameObj.toString() : null;
                    logger.info("ToolFinder: Metadata: {}, Extracted tool: {}", 
                            doc.getMetadata(), toolName);
                    return toolName;
                })
                .filter(toolName -> toolName != null && !toolName.isEmpty())
                .collect(Collectors.toList());

        logger.info("ToolFinder: Final tools for prompt '{}': {}", prompt, toolNames);
        return toolNames;
    }
}