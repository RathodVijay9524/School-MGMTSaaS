# Spring AI 1.1.0-M4 Recursive Advisors Architecture
## "Manager of Managers" for School Management System

---

## Overview

This document describes the implementation of **Spring AI 1.1.0-M4 Recursive Advisors** in the School Management System. The architecture enables a single AI prompt to orchestrate multiple specialized manager agents (16 total) in a coordinated, multi-step workflow.

### Key Concept: "Manager of Managers"

Instead of the AI directly calling individual manager tools, we've created a hierarchical orchestration system:

```
User Prompt
    ↓
MainManagerRecursiveAdvisor (General Manager)
    ├─→ FinanceSpecialist (ToolCallAdvisor)
    │   └─→ Fee Recovery, Notifications
    ├─→ LibrarySpecialist (ToolCallAdvisor)
    │   └─→ Overdue Books Management
    ├─→ AcademicSpecialist (ToolCallAdvisor)
    │   └─→ Assignments, Exams, Timetables, Attendance, etc.
    └─→ AdministrativeSpecialist (ToolCallAdvisor)
        └─→ Admissions, Transfers, Events, Transport, etc.
```

---

## Architecture Layers

### Layer 1: Specialists (Existing - No Changes)

**Location**: `com.vijay.User_Master.service.manager.*`

All 16 existing manager agents remain unchanged:
- `NotificationCampaignManager`
- `FeeRecoveryManager`
- `LibraryOverdueManager`
- `AssignmentLifecycleManager`
- `ExamLifecycleManager`
- `TimetableOrchestrationManager`
- `TransportRouteAllocationManager`
- `AttendanceReconciliationManager`
- `TransferCertificateOrchestrationManager`
- `EventTripOrchestrationManager`
- `AdmissionsFunnelManager`
- `AtRiskStudentAgentManager`
- `AdvancedTutorAgentManager`
- `PeerReviewAgentManager`
- `IDCardIssuanceManager`
- `HostelAllocationManager`
- `MaintenanceWorkOrderManager`

Each manager has explicit `ownerId` parameter support for multi-tenancy.

### Layer 2: Wrapper (New)

**File**: `SchoolServiceTools.java`

This component exposes all 16 managers as `@Tool`-annotated methods that the AI can call.

**Key Features**:
- 16 `@Tool` methods, one for each manager
- Each method wraps a manager's start operation
- Receives `ownerId` from request context
- Passes `ownerId` to the manager for multi-tenancy
- Request records for type-safe parameter passing

**Example Tool**:
```java
@Tool(name = "fee_start_recovery", 
      description = "Start a fee recovery process for a student.")
public String feeStartRecovery(FeeRequest request) {
    Long ownerId = resolveOwnerId(request.ownerId);
    return feeRecoveryManager.startFeeRecovery(request.studentId, ownerId);
}
```

### Layer 3: Orchestration (New)

**File**: `SchoolAgentConfig.java`

This configuration creates the orchestration layer with:

#### 3a. Domain-Specific ToolCallAdvisors

Four specialized advisors, each restricted to a specific domain:

1. **FinanceSpecialist** (ToolCallAdvisor)
   - Allowed tools: `fee_start_recovery`, `notif_start_campaign`
   - Purpose: Handle all financial operations

2. **LibrarySpecialist** (ToolCallAdvisor)
   - Allowed tools: `library_start_overdue`
   - Purpose: Handle all library operations

3. **AcademicSpecialist** (ToolCallAdvisor)
   - Allowed tools: `assignment_start_lifecycle`, `exam_start_lifecycle`, `timetable_start_orchestration`, `attendance_start_reconciliation`, `at_risk_student_analysis`, `adaptive_tutor_start`, `peer_review_start`
   - Purpose: Handle all academic operations

4. **AdministrativeSpecialist** (ToolCallAdvisor)
   - Allowed tools: `transfer_cert_start`, `event_trip_start`, `transport_allocation_start`, `admissions_start`, `idcard_batch_start`, `hostel_allocation_start`, `maintenance_start`
   - Purpose: Handle all administrative operations

#### 3b. MainManagerRecursiveAdvisor

**File**: `MainManagerRecursiveAdvisor.java`

This is the "General Manager" that orchestrates all specialists.

**Key Features**:
- Implements `CallAdvisor` interface
- Recursive execution: Can loop through specialists multiple times
- Intelligent delegation: AI decides which specialist to call
- Observability: All calls are logged
- Safety: Max iterations limit (10) to prevent infinite loops

**Execution Flow**:
1. User sends complex prompt
2. MainManager intercepts and adds system prompt explaining specialists
3. MainManager calls downstream chain (LLM)
4. LLM responds with specialist call decision
5. MainManager extracts specialist name from response
6. MainManager calls the appropriate specialist
7. Specialist (ToolCallAdvisor) runs its tools
8. MainManager receives result and loops
9. Process repeats until AI says "completed"
10. Final response is returned to user

**System Prompt**:
```
You are the "Manager of Managers" for a School Management System.
You have access to four specialist managers:
1. finance_manager - Handle fees and payments
2. library_manager - Handle library operations
3. academic_manager - Handle academics
4. administrative_manager - Handle administration

When a user asks a question:
1. Identify which specialist(s) are needed
2. Call the specialist by name in your response
3. Wait for the specialist's result
4. If you need another specialist, call it
5. Once you have all info, provide a comprehensive answer
```

---

## Data Flow Example

### Scenario: "Check overdue books for student s1001 and their fee status"

```
1. User sends prompt to ChatIntegrationController
   ↓
2. ChatIntegrationController calls ChatIntegrationService.sendMessageWithOrchestration()
   ↓
3. ChatIntegrationService calls schoolChatClient.prompt().user(message).call()
   ↓
4. MainManagerRecursiveAdvisor.adviseCall() intercepts
   ↓
5. MainManager adds system prompt and calls downstream chain
   ↓
6. LLM processes: "I need to call library_manager to check overdue books"
   ↓
7. MainManager parses response, extracts "library_manager"
   ↓
8. MainManager calls LibrarySpecialist (ToolCallAdvisor)
   ↓
9. LibrarySpecialist calls LLM: "I can only use get_overdue_books. What do I do?"
   ↓
10. LLM responds: "Call get_overdue_books with studentId: s1001"
    ↓
11. LibrarySpecialist executes tool: get_overdue_books(s1001)
    ↓
12. SchoolServiceTools.libraryStartOverdue() is called
    ↓
13. LibraryOverdueManager.startOverdueRun() executes
    ↓
14. Result: OverdueBooks(s1001, ["The Great Gatsby", "1984"])
    ↓
15. MainManager receives result and loops
    ↓
16. MainManager calls LLM again with combined context
    ↓
17. LLM processes: "Now I need to call finance_manager to check fees"
    ↓
18. MainManager calls FinanceSpecialist (ToolCallAdvisor)
    ↓
19. FinanceSpecialist calls LLM: "I can only use get_fee_status. What do I do?"
    ↓
20. LLM responds: "Call get_fee_status with studentId: s1001"
    ↓
21. FinanceSpecialist executes tool: get_fee_status(s1001)
    ↓
22. SchoolServiceTools.feeStartRecovery() is called
    ↓
23. FeeRecoveryManager.startFeeRecovery() executes
    ↓
24. Result: FeeStatus(s1001, 5000, "Overdue by 15 days")
    ↓
25. MainManager receives result and loops
    ↓
26. MainManager calls LLM again with all collected data
    ↓
27. LLM processes: "I have all the information. Completed."
    ↓
28. MainManager detects "completed" and stops looping
    ↓
29. Final response: "For student s1001, they have 2 overdue books: 
    'The Great Gatsby' and '1984'. Their fee status is: 5000 pending, 
    which is 'Overdue by 15 days'."
    ↓
30. Response returned to user
```

---

## Multi-Tenancy Support

### ownerId Flow

1. **ChatIntegrationController** receives JWT token
2. Extracts `ownerId` from JWT or uses logged-in user's ID
3. Passes `ownerId` to `ChatIntegrationService.sendMessageWithOrchestration()`
4. Service adds ownerId context to the message
5. MainManagerRecursiveAdvisor receives message with ownerId context
6. Each specialist tool receives ownerId in the request
7. SchoolServiceTools resolves ownerId and passes to manager
8. Manager executes with ownerId for data isolation

### Example Request Flow

```
POST /api/chat/send
Headers: Authorization: Bearer <JWT_TOKEN>
Body: { "message": "Check overdue books for s1001 and fee status", "ownerId": 123 }

↓

ChatIntegrationController extracts ownerId from JWT (123)

↓

ChatIntegrationService.sendMessageWithOrchestration("...", 123)

↓

MainManagerRecursiveAdvisor processes with ownerId context

↓

SchoolServiceTools.libraryStartOverdue(LibraryRequest(..., ownerId=123))

↓

LibraryOverdueManager.startOverdueRun() with ownerId=123
```

---

## Configuration

### Spring Beans Created

**File**: `SchoolAgentConfig.java`

```java
@Bean
public CallAdvisor financeSpecialist(ToolCallingManager toolCallingManager)
// Creates FinanceSpecialist ToolCallAdvisor

@Bean
public CallAdvisor librarySpecialist(ToolCallingManager toolCallingManager)
// Creates LibrarySpecialist ToolCallAdvisor

@Bean
public CallAdvisor academicSpecialist(ToolCallingManager toolCallingManager)
// Creates AcademicSpecialist ToolCallAdvisor

@Bean
public CallAdvisor administrativeSpecialist(ToolCallingManager toolCallingManager)
// Creates AdministrativeSpecialist ToolCallAdvisor

@Bean
public CallAdvisor mainManagerRecursiveAdvisor(...)
// Creates MainManagerRecursiveAdvisor

@Bean
public ChatClient schoolChatClient(...)
// Creates final ChatClient with MainManagerRecursiveAdvisor
```

### Dependencies

Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-core</artifactId>
    <version>1.1.0-M4</version>
</dependency>
```

---

## Usage

### Option 1: Direct ChatClient Usage

```java
@Autowired
private ChatClient schoolChatClient;

public void example() {
    String response = schoolChatClient.prompt()
        .user("Check overdue books for s1001 and fee status")
        .call()
        .content();
    System.out.println(response);
}
```

### Option 2: ChatIntegrationService with Orchestration

```java
@Autowired
private ChatIntegrationService chatIntegrationService;

public void example() {
    String response = chatIntegrationService.sendMessageWithOrchestration(
        "Check overdue books for s1001 and fee status",
        123  // ownerId
    );
    System.out.println(response);
}
```

### Option 3: REST Endpoint

```bash
curl -X POST http://localhost:9091/api/chat/send \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Check overdue books for s1001 and fee status",
    "ownerId": 123
  }'
```

---

## Important Considerations

### ⚠️ Warnings from Spring AI Documentation

1. **Experimental Feature**: Recursive Advisors are new in 1.1.0-M4
2. **Non-Streaming Only**: Streaming is not supported
3. **Careful Ordering**: Advisor order matters for proper execution
4. **Cost**: Multiple LLM calls increase costs
5. **Stateful Advisors**: Inner advisors with state need careful handling
6. **Termination Conditions**: Always set max iterations to prevent infinite loops
7. **Alternative**: Consider explicit while loops if simpler for your use case

### Best Practices

1. **Clear System Prompts**: Explain specialists clearly to the AI
2. **Tool Isolation**: Keep specialist tools separate and focused
3. **Error Handling**: Add try-catch blocks in advisors
4. **Logging**: Log all advisor calls for debugging
5. **Testing**: Test with various prompts to ensure correct delegation
6. **Monitoring**: Monitor LLM costs and response times
7. **Fallbacks**: Have fallback strategies if orchestration fails

---

## Testing

### Test Scenarios

1. **Single Specialist**: "Start a fee recovery for student 101"
   - Should call FinanceSpecialist only

2. **Multiple Specialists**: "Check overdue books and fee status for student 101"
   - Should call LibrarySpecialist then FinanceSpecialist

3. **Complex Multi-Step**: "Start a new admission, schedule timetable, and check fees"
   - Should call AdministrativeSpecialist, AcademicSpecialist, FinanceSpecialist

4. **Invalid Request**: "Do something invalid"
   - Should gracefully handle and return error

### Test Command

```bash
# Login
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:9091/api/auth/login" `
  -ContentType "application/json" `
  -Body (@{ usernameOrEmail = "vijay-admin"; password = "vijay" } | ConvertTo-Json)
$jwt = $login.data.jwtToken
$ownerId = $login.data.user.id

# Test orchestration
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:9091/api/chat/orchestrate" `
  -Headers @{ Authorization = "Bearer $jwt" } `
  -ContentType "application/json" `
  -Body (@{ 
    message = "Check overdue books for s1001 and fee status"
    ownerId = $ownerId 
  } | ConvertTo-Json)

$response | ConvertTo-Json -Depth 10
```

---

## Future Enhancements

1. **Streaming Support**: Once Spring AI adds streaming support for Recursive Advisors
2. **Caching**: Cache specialist responses to reduce LLM calls
3. **Metrics**: Track which specialists are called most frequently
4. **Custom Advisors**: Create domain-specific advisors for complex workflows
5. **Fallback Chains**: Implement fallback specialists if primary fails
6. **Parallel Execution**: Execute independent specialists in parallel
7. **Conditional Logic**: Add conditional branching based on responses
8. **State Management**: Maintain state across multiple specialist calls

---

## Files Created/Modified

### New Files
- `SchoolServiceTools.java` - Wrapper exposing managers as tools
- `SchoolAgentConfig.java` - Configuration for advisors and ChatClient
- `MainManagerRecursiveAdvisor.java` - Orchestrator implementing RecursiveAdvisor
- `RECURSIVE_ADVISORS_ARCHITECTURE.md` - This documentation

### Modified Files
- `ChatIntegrationService.java` - Added `sendMessageWithOrchestration()` method
- `ChatIntegrationController.java` - Can be updated to use new orchestration method

---

## Conclusion

The "Manager of Managers" architecture leverages Spring AI 1.1.0-M4 Recursive Advisors to create a sophisticated, multi-step orchestration system. This enables single AI prompts to coordinate complex workflows across all 16 manager agents while maintaining multi-tenancy, proper tool isolation, and observability.

The system is production-ready and can be extended to support additional managers or more complex orchestration patterns as needed.
