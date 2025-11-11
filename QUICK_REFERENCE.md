# Quick Reference Guide

## 🔐 Authentication

```powershell
# Login
$response = Invoke-WebRequest -Uri "http://localhost:9091/api/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"usernameOrEmail":"vijay-admin","password":"vijay"}' `
  -UseBasicParsing

$token = ($response.Content | ConvertFrom-Json).data.jwtToken
```

## 🚀 Quick Test Commands

### Chat Endpoint
```powershell
powershell -File "test-chat-endpoint.ps1"
```

### AssignmentLifecycleManager
```powershell
powershell -File "test-assignment-lifecycle.ps1"
```

### All LMS Endpoints
```powershell
powershell -File "test-all-lms-endpoints.ps1"
```

---

## 📋 AssignmentLifecycleManager Endpoints

### 1. Start
```
POST /api/manager-agents/assignments/start?assignmentId=1&reviewsPerSubmission=3
```
**Returns:** runId

### 2. Collect Submissions
```
POST /api/manager-agents/assignments/collect-submissions?runId={runId}&submissionIdsCsv=1,2,3
```
**Returns:** "Collected submissions: 3"

### 3. Grade Batch
```
POST /api/manager-agents/assignments/grade-batch?runId={runId}
```
**Returns:** "Graded submissions: X"

### 4. Teacher Gate
```
POST /api/manager-agents/assignments/teacher-gate?runId={runId}
```
**Returns:** "Teacher review gate set to WAITING"

### 5. Get State
```
GET /api/manager-agents/assignments/state?runId={runId}
```
**Returns:** State JSON

### 6. Publish
```
POST /api/manager-agents/assignments/publish?runId={runId}
```
**Returns:** "Assignment lifecycle completed"

---

## 🤖 Chat Endpoint

```
GET /chat?prompt={urlEncodedPrompt}
```

**Examples:**
```
/chat?prompt=What%20is%20123%20plus%20456%3F
/chat?prompt=What%20is%20the%20date%20and%20time%20right%20now%3F
/chat?prompt=What%20is%2010%20times%2020%3F
/chat?prompt=Please%20send%20an%20email%20to%20vijay%40example.com
```

---

## 📊 Test Results

| Component | Status | Success Rate |
|-----------|--------|--------------|
| Chat Endpoint | ✅ Working | 80% (4/5) |
| AssignmentLifecycleManager | ✅ Working | 100% (6/6) |
| **Overall** | ✅ **Working** | **91%** |

---

## 🔑 Credentials

```
Username: vijay-admin
Password: vijay
```

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `test-chat-endpoint.ps1` | Chat endpoint tests |
| `test-assignment-lifecycle.ps1` | Manager tests |
| `CHAT_ENDPOINT_TESTING_GUIDE.md` | Chat documentation |
| `ASSIGNMENT_LIFECYCLE_MANAGER_GUIDE.md` | Manager documentation |
| `TESTING_SUMMARY.md` | Complete test summary |

---

## 🛠️ Troubleshooting

### 401 Unauthorized
→ Get new JWT token from `/api/auth/login`

### 500 Server Error
→ Check parameter types and required fields

### "Invalid runId"
→ Use runId from `/assignments/start` response

### "No submissions to grade"
→ Call `/assignments/collect-submissions` first

---

## 📍 Base URLs

| Service | URL |
|---------|-----|
| Chat | `http://localhost:9091/chat` |
| Manager Agents | `http://localhost:9091/api/manager-agents` |
| Auth | `http://localhost:9091/api/auth` |

---

## ✅ Verified Features

- ✅ JWT Authentication
- ✅ Multi-step Workflows
- ✅ State Persistence
- ✅ AI Grading
- ✅ Peer Review Assignment
- ✅ Teacher Review Gate
- ✅ Grade Publication

---

**Last Updated:** November 11, 2025
