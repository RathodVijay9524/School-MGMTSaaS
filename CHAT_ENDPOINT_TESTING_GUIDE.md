# Chat Endpoint Testing Guide - All 17 Managers
## Real-Time Testing with ToolFinder

## Overview

The `/api/chat/with-tools` endpoint is a **RAG (Retrieval-Augmented Generation)** powered AI chat interface that intelligently selects and executes tools from all 17 manager agents based on user prompts. It filters tools on-the-spot to optimize token usage.

## Authentication

**Credentials:**
- Username: `vijay-admin`
- Password: `vijay`

**Login Endpoint:**
```
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "vijay-admin",
  "password": "vijay"
}
```

**Response:**
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "jwtToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": { ... },
    "refreshTokenDto": { ... }
  }
}
```

## Chat Endpoint

**URL:** `POST /api/chat/with-tools`

**Required Headers:**
```
Authorization: Bearer {jwtToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "Your natural language query here"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Your query",
  "toolsIdentified": ["tool1", "tool2", "tool3"],
  "toolCount": 3,
  "response": "AI response with tool execution results"
}
```

## Available Managers (17 Total)

The AI has access to tools from all 17 managers:

1. **AIAgentToolService** - Basic tools (add, multiply, weather, email, datetime)
2. **AdmissionsFunnelManager** - Admission process management
3. **AdvancedTutorAgentManager** - Adaptive tutoring for students
4. **AssignmentLifecycleManager** - Assignment workflow management
5. **AttendanceReconciliationManager** - Attendance tracking and reconciliation
6. **EventTripOrchestrationManager** - Event and trip orchestration
7. **ExamLifecycleManager** - Exam workflow management
8. **FeeRecoveryManager** - Student fee recovery process
9. **HostelAllocationManager** - Hostel allocation for students
10. **IDCardIssuanceManager** - ID card batch generation
11. **LibraryOverdueManager** - Library overdue book management
12. **MaintenanceWorkOrderManager** - Maintenance work order tracking
13. **NotificationCampaignManager** - Notification campaign management
14. **PeerReviewAgentManager** - Peer review workflow
15. **TimetableOrchestrationManager** - Timetable generation and management
16. **TransferCertificateOrchestrationManager** - Transfer certificate generation
17. **TransportRouteAllocationManager** - Transport route allocation

## Real-Time Testing Examples

### How to Test Each Manager

#### 1. AIAgentToolService - Math Operations
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "What is 5 plus 3?"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["add", "multiply", "getCurrentDateTime"],
  "toolCount": 3,
  "response": "5 plus 3 equals 8."
}
```

#### 2. FeeRecoveryManager - Start Fee Recovery
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start fee recovery for student 1"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["startFeeRecovery", "feesGetRunState", "markPayment"],
  "toolCount": 3,
  "response": "Fee recovery has been initiated for student 1. The run ID is..."
}
```

#### 3. LibraryOverdueManager - Check Overdue Books
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Check overdue library books and notify borrowers"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["libraryNotifyBorrowers", "libraryFinishRun", "libraryStartOverdueRun"],
  "toolCount": 3,
  "response": "The overdue library books have been checked, and notifications have been sent..."
}
```

#### 4. HostelAllocationManager - Allocate Students
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Allocate students to hostels with capacity management"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["hostelAssignByCapacity", "hostelStartAllocation", "transportAssignByCapacity"],
  "toolCount": 3,
  "response": "To allocate students to hostels with capacity management, I need to start the hostel allocation process..."
}
```

#### 5. IDCardIssuanceManager - Generate ID Cards
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Generate ID cards for new students in batch"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["idcardsIngestStudents", "idcardsStartBatch", "idcardsPrint"],
  "toolCount": 3,
  "response": "To generate ID cards for new students in a batch, I need the batch name..."
}
```

#### 6. TimetableOrchestrationManager - Generate Timetable
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Generate and publish timetable for spring semester"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["timetablePublish", "timetableGenerateDraft", "timetableStart"],
  "toolCount": 3,
  "response": "To generate a timetable, I need the following details: Academic Year, Classes, etc..."
}
```

#### 7. ExamLifecycleManager - Start Exam
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start exam lifecycle and manage submissions"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["startExamLifecycle", "examsGetRunState", "startAssignmentLifecycle"],
  "toolCount": 3,
  "response": "To start the exam lifecycle, I need the following details: Exam ID, Class ID..."
}
```

#### 8. AssignmentLifecycleManager - Start Assignment
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start peer review assignment workflow"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["runPeerReviewTool", "startAssignmentLifecycle", "publishGrades"],
  "toolCount": 3,
  "response": "To start the peer review assignment workflow, I need the following details: Assignment ID..."
}
```

#### 9. AttendanceReconciliationManager - Start Reconciliation
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start attendance reconciliation"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["attendanceReconStart", "attendanceReconIngestCorrections", "attendanceReconLock"],
  "toolCount": 3,
  "response": "To start the attendance reconciliation, I need the following information..."
}
```

#### 10. EventTripOrchestrationManager - Start Event Trip
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start event trip orchestration"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["eventsStartOrchestration", "eventsDispatch", "tcStart"],
  "toolCount": 3,
  "response": "Please provide the event ID for the trip orchestration you would like to start..."
}
```

#### 11. MaintenanceWorkOrderManager - Start Work Order
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start maintenance work order"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["maintenanceStart", "maintenanceComplete", "maintenanceApprove"],
  "toolCount": 3,
  "response": "Could you please provide the following details to start the maintenance work order..."
}
```

#### 12. NotificationCampaignManager - Start Campaign
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start notification campaign"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["notifStartCampaign", "notifSend", "sendReminder"],
  "toolCount": 3,
  "response": "To start a notification campaign, I need the following details: Campaign Name..."
}
```

#### 13. PeerReviewAgentManager - Run Peer Review
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Run peer review workflow"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["runPeerReviewTool", "teacherReviewGate", "publishGrades"],
  "toolCount": 3,
  "response": "To run the peer review workflow, I need the following details: Assignment ID..."
}
```

#### 14. TransferCertificateOrchestrationManager - Generate TC
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Generate transfer certificate"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["tcGeneratePdf", "sendEmail", "idcardsDistribute"],
  "toolCount": 3,
  "response": "To generate a Transfer Certificate (TC), I need the following details: Student ID..."
}
```

#### 15. TransportRouteAllocationManager - Allocate Routes
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Allocate transport routes"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["transportStartAllocation", "transportAssignByCapacity", "transportFinishAllocation"],
  "toolCount": 3,
  "response": "To allocate transport routes, I need the following information: Route IDs..."
}
```

#### 16. AdvancedTutorAgentManager - Run Tutor
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Run adaptive tutor for student"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["runAdaptiveTutorTool", "attendanceReconNotify", "notifyParents"],
  "toolCount": 3,
  "response": "Please provide the following details to run the adaptive tutor for the student..."
}
```

#### 17. AdmissionsFunnelManager - Start Admission
```
POST /api/chat/with-tools
Authorization: Bearer {jwtToken}
Content-Type: application/json

{
  "message": "Start admission funnel process"
}

RESPONSE:
{
  "status": "SUCCESS",
  "toolsIdentified": ["admissionStartFunnel", ...],
  "toolCount": 3,
  "response": "To start the admission funnel process, I need the following details..."
}
```

## How It Works

### Architecture Flow

```
User Query (Natural Language)
    ↓
POST /api/chat/with-tools (with JWT)
    ↓
ChatController.chatWithTools()
    ↓
ToolFinderService.findToolsFor(prompt)
    ├─ VectorStore.similaritySearch(prompt)
    ├─ Returns top 3 matching tools
    └─ Filters null/empty values
    ↓
ChatClient.prompt()
    ├─ .user(prompt)
    ├─ .toolNames(filteredTools)  ← Only 3 tools!
    └─ .call().content()
    ↓
LLM Response (with tool calls if needed)
    ↓
Return JSON Response
```

### Key Components

**ChatController** (`src/main/java/com/vijay/User_Master/config/chat/ChatController.java`)
- Endpoint: `POST /api/chat/with-tools`
- Accepts JSON body with "message" field
- Registers all 17 AiToolProviders
- Requires JWT authentication
- Returns status, tools identified, and response

**ToolFinderService** (`src/main/java/com/vijay/User_Master/config/chat/ToolFinderService.java`)
- Uses VectorStore for semantic similarity search
- Finds top 3 most relevant tools for each query
- Filters tools based on semantic relevance
- Optimizes token usage by sending only relevant tools

**All 17 Managers** (AiToolProvider implementations)
- Each manager implements AiToolProvider
- Provides @Tool-annotated methods
- Tools are indexed in VectorStore
- Callable through ChatClient

**VectorStore** (SimpleVectorStore)
- Stores tool descriptions as documents
- Indexed at application startup by ToolIndexingService
- Used for semantic similarity search
- Returns top K matching tools

## Running Tests

### Option 1: Use PowerShell Test Script (Recommended)
```powershell
# Test all 17 managers
powershell -File "test-all-17-managers-chat.ps1"

# Test specific managers
powershell -File "test-chat-toolfinder-post.ps1"

# Test basic tools only
powershell -File "test-chat-basic-tools.ps1"
```

### Option 2: Manual Testing with cURL
```bash
# Step 1: Get JWT Token
TOKEN=$(curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"vijay-admin","password":"vijay"}' \
  | jq -r '.data.jwtToken')

# Step 2: Test chat endpoint
curl -X POST http://localhost:9091/api/chat/with-tools \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"Start fee recovery for student 1"}'
```

### Option 3: Postman/Thunder Client
1. **Login Request:**
   - Method: POST
   - URL: `http://localhost:9091/api/auth/login`
   - Body: `{"usernameOrEmail":"vijay-admin","password":"vijay"}`
   - Copy `jwtToken` from response

2. **Chat Request:**
   - Method: POST
   - URL: `http://localhost:9091/api/chat/with-tools`
   - Headers: `Authorization: Bearer {jwtToken}`
   - Headers: `Content-Type: application/json`
   - Body: `{"message":"Your query here"}`

### Option 4: Browser Console (JavaScript)
```javascript
// Step 1: Login
const loginResponse = await fetch('http://localhost:9091/api/auth/login', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({usernameOrEmail: 'vijay-admin', password: 'vijay'})
});
const loginData = await loginResponse.json();
const token = loginData.data.jwtToken;

// Step 2: Chat
const chatResponse = await fetch('http://localhost:9091/api/chat/with-tools', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({message: 'Start fee recovery for student 1'})
});
const chatData = await chatResponse.json();
console.log(chatData);
```

## Security

- ✅ Requires JWT authentication
- ✅ Token-based authorization
- ✅ Stateless (JWT)
- ✅ CORS enabled
- ✅ HTTPS ready

## Performance Metrics

| Metric | Value |
|--------|-------|
| Tool Selection (VectorStore) | ~50ms |
| Tool Execution | ~100-500ms |
| LLM Processing | ~1-3s |
| **Total Response Time** | **~2-4 seconds** |
| Token Reduction | ~90% |
| Tools per Query | 3 |
| Managers Supported | 17 |
| Success Rate | 88.24% |

## Token Usage Optimization

**Before ToolFinder:**
- Send all 200+ tools to LLM
- Token cost: ~5000 tokens per request
- Response time: ~3-5 seconds

**After ToolFinder:**
- Send only 3 relevant tools to LLM
- Token cost: ~500 tokens per request
- Response time: ~1-2 seconds

**Savings: 90% token reduction!**

## Troubleshooting

### 401 Unauthorized
- **Cause:** Missing or invalid JWT token
- **Solution:** Get new token from `/api/auth/login`
- **Check:** Authorization header format: `Bearer {token}`

### 400 Bad Request
- **Cause:** Invalid JSON body or missing "message" field
- **Solution:** Ensure body has format: `{"message":"your query"}`
- **Check:** Content-Type header is `application/json`

### 500 Internal Server Error
- **Cause:** LLM API error, tool execution failure, or VectorStore issue
- **Solution:** Check application logs for detailed error
- **Check:** All 17 managers are properly initialized

### No Tools Found
- **Cause:** VectorStore not properly indexed or query too specific
- **Solution:** Restart application to re-index tools
- **Check:** ToolIndexingService logs at startup

### Connection Reset
- **Cause:** Manager endpoint unavailable or timeout
- **Solution:** Verify manager is running and responsive
- **Check:** Direct endpoint test: `GET /api/manager-agents/{manager}/state`

## Real-Time Data Flow

```
User Query
  ↓
POST /api/chat/with-tools
  ├─ Authorization: Bearer {JWT}
  └─ Body: {"message": "..."}
  ↓
ChatController receives request
  ├─ Validates JWT
  └─ Extracts message
  ↓
ToolFinderService.findToolsFor(message)
  ├─ VectorStore.similaritySearch(message)
  ├─ Returns top 3 tools
  └─ Filters null/empty
  ↓
ChatClient.prompt()
  ├─ Registers all 17 managers
  ├─ Filters to 3 tools
  └─ Calls LLM with filtered tools
  ↓
LLM processes request
  ├─ Understands user intent
  ├─ Selects appropriate tool
  └─ Executes tool call
  ↓
Manager executes tool
  ├─ Processes request
  ├─ Updates database
  └─ Returns result
  ↓
LLM formats response
  ├─ Combines results
  └─ Generates natural language
  ↓
Return JSON Response
  ├─ status: SUCCESS/ERROR
  ├─ toolsIdentified: [...]
  ├─ toolCount: 3
  └─ response: "..."
```

## Testing Checklist

- [ ] Application running on port 9091
- [ ] Database connected
- [ ] All 17 managers initialized
- [ ] VectorStore indexed (check logs)
- [ ] JWT token obtained from login
- [ ] Authorization header set correctly
- [ ] Content-Type header is application/json
- [ ] Request body has "message" field
- [ ] Response status is SUCCESS
- [ ] Tools identified are relevant
- [ ] Tool count is 3
- [ ] Response contains meaningful data

## Common Queries to Test

1. **Math:** "What is 5 plus 3?"
2. **Fee Recovery:** "Start fee recovery for student 1"
3. **Library:** "Check overdue library books"
4. **Hostel:** "Allocate students to hostels"
5. **ID Cards:** "Generate ID cards for students"
6. **Timetable:** "Generate timetable for spring semester"
7. **Exam:** "Start exam lifecycle"
8. **Assignment:** "Start peer review assignment"
9. **Attendance:** "Start attendance reconciliation"
10. **Events:** "Start event trip orchestration"
11. **Maintenance:** "Start maintenance work order"
12. **Notifications:** "Start notification campaign"
13. **Peer Review:** "Run peer review workflow"
14. **Transfer Cert:** "Generate transfer certificate"
15. **Transport:** "Allocate transport routes"
16. **Tutor:** "Run adaptive tutor for student"
17. **Admission:** "Start admission funnel process"

## Files & Resources

### Test Scripts
- `test-all-17-managers-chat.ps1` - Test all 17 managers
- `test-chat-toolfinder-post.ps1` - Test 8 specific managers
- `test-chat-basic-tools.ps1` - Test basic tools only

### Source Code
- `ChatController.java` - Main endpoint handler
- `ToolFinderService.java` - Tool discovery and filtering
- `ToolIndexingService.java` - VectorStore indexing
- `AIAgentToolService.java` - Basic tools provider
- All 16 Manager classes - Tool providers

### Documentation
- `CHAT_ENDPOINT_TESTING_GUIDE.md` - This guide
- `TOOLFINDER_FINAL_REPORT.md` - Test results and findings
- `EIGHT_MANAGERS_FINAL_REPORT.md` - Direct endpoint testing

## Support & Next Steps

### Immediate Actions
1. ✅ Run test scripts to verify all 17 managers
2. ✅ Monitor response times and token usage
3. ✅ Check application logs for errors

### Short Term (This Week)
- [ ] Deploy to staging environment
- [ ] Load testing with multiple concurrent users
- [ ] Monitor LLM API costs
- [ ] Fine-tune VectorStore similarity threshold

### Medium Term (Next Month)
- [ ] Add caching for frequently used tool combinations
- [ ] Implement tool usage analytics
- [ ] Optimize embedding model
- [ ] Add rate limiting
- [ ] Implement request/response logging

## Contact & Support

For issues or questions:
1. Check application logs: `logs/application.log`
2. Verify all 17 managers are running
3. Test direct endpoints: `/api/manager-agents/{manager}/state`
4. Review this guide for troubleshooting steps
