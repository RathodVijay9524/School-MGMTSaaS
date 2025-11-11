package com.vijay.User_Master.config.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * The main chat controller that uses the "Smart Finder" (RAG) architecture.
 */
@RestController
public class ChatController {

//http://localhost:9092/chat?prompt=What%20is%20the%20weather%20in%20London%3F
    //http://localhost:9092/chat?prompt=Please%20send%20an%20email%20to%20vijay%40example.com%20saying%20'the%20RAG%20system%20is%20working'
    //http://localhost:9092/chat?prompt=what%20is%20123%20plus%20456%3F
    //http://localhost:9092/chat?prompt=what%20is%20the%20date%20and%20time%20right%20now%3F

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ToolFinderService toolFinder;

    // This is the single, reusable ChatClient instance.
    private final ChatClient chatClient;

    @Autowired
    public ChatController(ToolFinderService toolFinder,
                          List<AiToolProvider> allToolProviders,
                          ChatClient.Builder openAiChatClientBuilder) {

        this.toolFinder = toolFinder;

        // --- THIS IS THE EFFICIENT PATTERN ---
        // 1. Build the ChatClient ONCE in the constructor.
        // 2. Give it ALL your tools (e.g., all 200) just one time.
        //    The .toolNames() call later will filter them for each request.

        // --- REGISTER ALL TOOL PROVIDERS ---
        // Register all AiToolProvider implementations (AIAgentToolService + all managers)
        ChatClient.Builder builder = openAiChatClientBuilder;
        for (AiToolProvider provider : allToolProviders) {
            logger.info("Registering tool provider: {}", provider.getClass().getSimpleName());
            builder = builder.defaultTools(provider);
        }
        
        this.chatClient = builder.build();
        logger.info("ChatClient built with {} tool providers", allToolProviders.size());
    }

    /**
     * Handles a user chat request using the RAG Tool-Finding pattern.
     * @param prompt The user's natural language request.
     * @return The AI's final string response after any tool calls.
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        logger.info("Chat request received: {}", prompt);

        // 1. SMART FINDER (RAG) STEP:
        // Find the *names* of the tools needed (e.g., ["getWeather", "sendEmail"])
        List<String> requiredToolNames = toolFinder.findToolsFor(prompt);
        logger.info("SmartFinder: Activating tools for this request: {}", requiredToolNames);

        // Convert the List<String> to a String[] for the .toolNames() method
        String[] toolNamesArray = requiredToolNames.toArray(new String[0]);

        // 2. "AGENTIC" CALL:
        // Use the single, pre-built chatClient.
        String content = this.chatClient.prompt()
                .user(prompt)

                // --- THIS IS THE CORRECT API CALL ---
                // .toolNames() filters the *entire* tool list (from aiAgentToolService)
                // for this request only. This is the "on-demand" RAG pattern.
                .toolNames(toolNamesArray)

                // .call().content() handles the entire recursive loop
                // (e.g., call tool -> get result -> call another tool)
                // and returns the final text response.
                .call()
                .content();

        logger.info("Final AI response: {}", content);
        return content;
    }

    /**
     * POST endpoint with JWT authentication for chat with ToolFinder.
     * Tests if ToolFinderService correctly identifies tools from user queries.
     * @param requestBody JSON with "message" field
     * @return Response with AI answer and tools used
     */
    @PostMapping("/api/chat/with-tools")
    public Map<String, Object> chatWithTools(@RequestBody Map<String, String> requestBody) {
        String prompt = requestBody.get("message");
        logger.info("Chat POST request received: {}", prompt);

        try {
            // 1. SMART FINDER (RAG) STEP:
            // Find the *names* of the tools needed
            List<String> requiredToolNames = toolFinder.findToolsFor(prompt);
            logger.info("ToolFinder: Found {} tools for prompt: {}", requiredToolNames.size(), prompt);
            logger.info("ToolFinder: Tools identified: {}", requiredToolNames);

            // If no tools found, use all tools (fallback)
            String[] toolNamesArray;
            if (requiredToolNames == null || requiredToolNames.isEmpty()) {
                logger.warn("ToolFinder: No tools found, using all available tools");
                toolNamesArray = new String[0]; // Empty array means use all tools
            } else {
                toolNamesArray = requiredToolNames.toArray(new String[0]);
            }

            // 2. "AGENTIC" CALL:
            // Use the single, pre-built chatClient with filtered tools
            String content = this.chatClient.prompt()
                    .user(prompt)
                    .toolNames(toolNamesArray)
                    .call()
                    .content();

            logger.info("Final AI response: {}", content);

            // Return response with tools used
            return Map.of(
                    "status", "SUCCESS",
                    "message", prompt,
                    "toolsIdentified", requiredToolNames != null ? requiredToolNames : List.of(),
                    "toolCount", requiredToolNames != null ? requiredToolNames.size() : 0,
                    "response", content
            );
        } catch (Exception e) {
            logger.error("Error in chat endpoint: {}", e.getMessage(), e);
            return Map.of(
                    "status", "ERROR",
                    "message", prompt,
                    "error", e.getMessage()
            );
        }
    }
}