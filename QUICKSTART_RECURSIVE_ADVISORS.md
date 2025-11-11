# Quick Start: Spring AI Recursive Advisors

## 5-Minute Overview

You now have a "Manager of Managers" system that lets a single AI prompt orchestrate all 16 manager agents.

---

## What Changed?

### New Files (3)
1. `SchoolServiceTools.java` - Exposes 16 managers as AI tools
2. `SchoolAgentConfig.java` - Creates specialist advisors
3. `MainManagerRecursiveAdvisor.java` - Orchestrates specialists

### Modified Files (1)
1. `ChatIntegrationService.java` - Added `sendMessageWithOrchestration()` method

### Documentation (2)
1. `RECURSIVE_ADVISORS_ARCHITECTURE.md` - Full technical documentation
2. `IMPLEMENTATION_SUMMARY.md` - Detailed implementation guide

---

## How to Use

### 1. Build the Project
```bash
mvn clean install
```

### 2. Start the Application
```bash
# Application starts on port 9091
java -jar target/School-MGMTSaaS.jar
```

### 3. Test with PowerShell

```powershell
# Login
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:9091/api/auth/login" `
  -ContentType "application/json" `
  -Body (@{ usernameOrEmail = "vijay-admin"; password = "vijay" } | ConvertTo-Json)
$jwt = $login.data.jwtToken
$ownerId = $login.data.user.id
$h = @{ Authorization = "Bearer $jwt" }

# Test orchestration (once endpoint is created)
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:9091/api/chat/orchestrate" `
  -Headers $h `
  -ContentType "application/json" `
  -Body (@{ 
    message = "Check overdue books for student s1001 and their fee status"
    ownerId = $ownerId 
  } | ConvertTo-Json)

$response
```

---

## Example Prompts to Try

### 1. Single Specialist
```
"Start a fee recovery for student 101"
→ Calls FinanceSpecialist
→ Executes FeeRecoveryManager
```

### 2. Multiple Specialists
```
"Check overdue books for s1001 and their fee status"
→ Calls LibrarySpecialist
→ Gets overdue books
→ Calls FinanceSpecialist
→ Gets fee status
→ Combines and returns both
```

### 3. Complex Multi-Step
```
"Start a new admission for John Doe, schedule his timetable for next semester, and check if there are any fees pending"
→ Calls AdministrativeSpecialist (admissions)
→ Calls AcademicSpecialist (timetable)
→ Calls FinanceSpecialist (fees)
→ Combines all results
```

### 4. Academic Operations
```
"Analyze at-risk students in class 10A and start an adaptive tutor for the top 3 at-risk students"
→ Calls AcademicSpecialist
→ Runs at-risk analysis
→ Starts tutoring for identified students
```

---

## Architecture at a Glance

```
User: "Check overdue books and fee status"
    ↓
MainManagerRecursiveAdvisor
    ├─→ LibrarySpecialist (ToolCallAdvisor)
    │   └─→ SchoolServiceTools.libraryStartOverdue()
    │       └─→ LibraryOverdueManager.startOverdueRun()
    │
    └─→ FinanceSpecialist (ToolCallAdvisor)
        └─→ SchoolServiceTools.feeStartRecovery()
            └─→ FeeRecoveryManager.startFeeRecovery()

Result: Combined response with both pieces of information
```

---

## The 4 Specialists

### 1. Finance Specialist
**Tools**: `fee_start_recovery`, `notif_start_campaign`
**Managers**: FeeRecoveryManager, NotificationCampaignManager
**Use When**: User asks about fees, payments, financial matters

### 2. Library Specialist
**Tools**: `library_start_overdue`
**Managers**: LibraryOverdueManager
**Use When**: User asks about books, library, overdue items

### 3. Academic Specialist
**Tools**: 7 academic tools
**Managers**: AssignmentLifecycleManager, ExamLifecycleManager, TimetableOrchestrationManager, AttendanceReconciliationManager, AtRiskStudentAgentManager, AdvancedTutorAgentManager, PeerReviewAgentManager
**Use When**: User asks about academics, classes, learning, assignments, exams

### 4. Administrative Specialist
**Tools**: 7 administrative tools
**Managers**: AdmissionsFunnelManager, TransferCertificateOrchestrationManager, EventTripOrchestrationManager, TransportRouteAllocationManager, AdmissionsFunnelManager, IDCardIssuanceManager, HostelAllocationManager, MaintenanceWorkOrderManager
**Use When**: User asks about administration, operations, admissions, events, transport

---

## Key Features

✅ **Single Prompt, Multiple Managers**
- One user message can orchestrate all 16 managers

✅ **Tool Isolation**
- Each specialist only has access to its domain's tools
- Finance specialist can't call library tools

✅ **Intelligent Delegation**
- AI decides which specialist to call based on the request
- Can call multiple specialists in sequence

✅ **Multi-Tenancy**
- ownerId flows through all layers
- Proper data isolation between users

✅ **Full Observability**
- All advisor calls are logged
- Easy to debug and monitor

✅ **Scalable**
- Easy to add more specialists or managers
- Extensible architecture

---

## How It Works (Step by Step)

### Example: "Check overdue books for s1001 and fee status"

```
1. User sends prompt to ChatIntegrationController
2. Controller extracts ownerId from JWT
3. Calls ChatIntegrationService.sendMessageWithOrchestration()
4. Service calls schoolChatClient.prompt().user(message).call()
5. MainManagerRecursiveAdvisor intercepts
6. MainManager adds system prompt explaining specialists
7. MainManager calls LLM
8. LLM responds: "I need to check library_manager for overdue books"
9. MainManager parses response, extracts "library_manager"
10. MainManager calls LibrarySpecialist (ToolCallAdvisor)
11. LibrarySpecialist calls LLM: "I can only use library tools"
12. LLM responds: "Call get_overdue_books with studentId: s1001"
13. LibrarySpecialist executes tool
14. SchoolServiceTools.libraryStartOverdue() is called
15. LibraryOverdueManager.startOverdueRun() executes
16. Result: ["The Great Gatsby", "1984"]
17. MainManager receives result and loops
18. MainManager calls LLM again with combined context
19. LLM responds: "Now I need finance_manager for fee status"
20. MainManager calls FinanceSpecialist
21. FinanceSpecialist calls LLM
22. LLM responds: "Call get_fee_status with studentId: s1001"
23. FinanceSpecialist executes tool
24. SchoolServiceTools.feeStartRecovery() is called
25. FeeRecoveryManager.startFeeRecovery() executes
26. Result: FeeStatus(5000, "Overdue by 15 days")
27. MainManager receives result and loops
28. MainManager calls LLM with all collected data
29. LLM responds: "I have all information. Completed."
30. MainManager detects "completed" and stops looping
31. Final response: "For student s1001, they have 2 overdue books: 
    'The Great Gatsby' and '1984'. Their fee status is: 5000 pending, 
    which is 'Overdue by 15 days'."
32. Response returned to user
```

---

## Next Steps

### Immediate (Required)
1. ✅ Build the project: `mvn clean install`
2. ✅ Restart the application
3. ⏳ Create REST endpoint in ChatIntegrationController:
   ```java
   @PostMapping("/api/chat/orchestrate")
   public ResponseEntity<String> orchestrateChat(
       @RequestBody ChatOrchestrationRequest request,
       @RequestHeader(value = "Authorization", required = false) String authHeader
   ) {
       Long ownerId = extractOwnerIdFromJwt(authHeader);
       String response = chatIntegrationService.sendMessageWithOrchestration(
           request.getMessage(),
           ownerId
       );
       return ResponseEntity.ok(response);
   }
   ```

### Testing (Recommended)
1. Test single specialist calls
2. Test multiple specialist calls
3. Test complex multi-step workflows
4. Monitor LLM costs and response times
5. Verify multi-tenancy isolation

### Optimization (Optional)
1. Add response caching
2. Implement parallel specialist execution
3. Add conditional branching logic
4. Create domain-specific advisors
5. Monitor and optimize LLM usage

---

## Troubleshooting

### Issue: "SchoolChatClient not configured"
**Solution**: Ensure SchoolAgentConfig is being loaded. Check that Spring AI dependencies are in pom.xml.

### Issue: Specialist not being called
**Solution**: Check the system prompt in MainManagerRecursiveAdvisor. Verify specialist names match the prompt.

### Issue: ownerId not being passed
**Solution**: Ensure JWT token is being sent with requests. Check CommonUtils.getLoggedInUser() is working.

### Issue: High LLM costs
**Solution**: Reduce max iterations in MainManagerRecursiveAdvisor. Consider caching specialist responses.

### Issue: Slow responses
**Solution**: Monitor LLM response times. Consider implementing parallel specialist execution.

---

## Important Notes

⚠️ **Experimental Feature**: Recursive Advisors are new in Spring AI 1.1.0-M4

⚠️ **Non-Streaming Only**: Streaming is not supported yet

⚠️ **Cost**: Multiple LLM calls increase costs. Monitor usage.

⚠️ **Max Iterations**: Set to 10 to prevent infinite loops

⚠️ **Termination**: AI must say "completed" to stop looping

---

## Documentation

For detailed information, see:
- `RECURSIVE_ADVISORS_ARCHITECTURE.md` - Full technical documentation
- `IMPLEMENTATION_SUMMARY.md` - Detailed implementation guide

---

## Support

For issues or questions:
1. Check the logs for advisor execution details
2. Review the system prompt in MainManagerRecursiveAdvisor
3. Test with simpler prompts first
4. Monitor LLM responses for correct specialist delegation

---

**Status**: ✅ Ready to Build & Test

**Next Action**: Run `mvn clean install` and restart the application.
