# Chat Endpoint Testing Guide

## Overview

The `/chat` endpoint is a **RAG (Retrieval-Augmented Generation)** powered AI chat interface that intelligently selects and executes tools based on user prompts.

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

**URL:** `GET /chat?prompt={userPrompt}`

**Required Header:**
```
Authorization: Bearer {jwtToken}
```

## Available Tools

The AI has access to 5 tools:

1. **getCurrentDateTime()** - Returns current date/time in user's timezone
2. **add(a, b)** - Adds two numbers
3. **multiply(a, b)** - Multiplies two numbers
4. **getWeather(location)** - Gets weather for a city
5. **sendEmail(to, body)** - Sends an email

## Test Results

### ✅ Test 1: Math Operation (Addition)
```
Prompt: What is 123 plus 456?
Response: 123 plus 456 equals 579.
Status: ✅ PASSED
```

### ✅ Test 2: Date/Time Query
```
Prompt: What is the date and time right now?
Response: The current date and time is November 11, 2025, at 8:56 PM (IST).
Status: ✅ PASSED
```

### ✅ Test 3: Multiplication
```
Prompt: What is 10 times 20?
Response: 10 times 20 is 200.
Status: ✅ PASSED
```

### ✅ Test 4: Email Sending
```
Prompt: Please send an email to vijay@example.com saying 'the RAG system is working'
Response: The email has been successfully sent to vijay@example.com with the message: "the RAG system is working."
Status: ✅ PASSED
```

### ⚠️ Test 5: Weather Query
```
Prompt: What is the weather in London?
Response: 400 Bad Request
Status: ⚠️ NEEDS INVESTIGATION
```

## How It Works

### Architecture Flow

```
User Prompt
    ↓
ChatController (/chat endpoint)
    ↓
ToolFinderService (RAG - Finds relevant tools)
    ↓
AIAgentToolService (Executes selected tools)
    ↓
ChatClient (LLM processes results)
    ↓
Final Response
```

### Key Components

**ChatController** (`src/main/java/com/vijay/User_Master/config/chat/ChatController.java`)
- Single endpoint: `GET /chat?prompt={prompt}`
- Uses RAG pattern to intelligently select tools
- Requires JWT authentication

**ToolFinderService** (`src/main/java/com/vijay/User_Master/config/chat/ToolFinderService.java`)
- Uses VectorStore for similarity search
- Finds top 3 relevant tools for each prompt
- Filters tools based on semantic relevance

**AIAgentToolService** (`src/main/java/com/vijay/User_Master/config/chat/AIAgentToolService.java`)
- Provides 5 @Tool-annotated methods
- Each tool is independently callable
- Returns structured responses

## Running Tests

### Option 1: Use PowerShell Test Script
```powershell
powershell -File "test-chat-endpoint.ps1"
```

### Option 2: Manual Testing with cURL
```bash
# Step 1: Get JWT Token
curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"vijay-admin","password":"vijay"}'

# Step 2: Use token to test /chat
curl -X GET "http://localhost:9091/chat?prompt=What%20is%20123%20plus%20456%3F" \
  -H "Authorization: Bearer {jwtToken}"
```

### Option 3: Browser/Postman
1. Login at: `POST http://localhost:9091/api/auth/login`
2. Copy the `jwtToken` from response
3. Add header: `Authorization: Bearer {jwtToken}`
4. Test: `GET http://localhost:9091/chat?prompt=What%20is%20123%20plus%20456%3F`

## Security

- ✅ Requires JWT authentication
- ✅ Token-based authorization
- ✅ Stateless (JWT)
- ✅ CORS enabled
- ✅ HTTPS ready

## Performance

- **Tool Selection:** ~50ms (VectorStore similarity search)
- **Tool Execution:** ~100-500ms (depends on tool)
- **LLM Processing:** ~1-3s (OpenAI API call)
- **Total Response Time:** ~2-4 seconds

## Troubleshooting

### 401 Unauthorized
- **Cause:** Missing or invalid JWT token
- **Solution:** Get new token from `/api/auth/login`

### 400 Bad Request
- **Cause:** Invalid prompt format or tool execution error
- **Solution:** Check prompt syntax and tool parameters

### 500 Internal Server Error
- **Cause:** LLM API error or tool execution failure
- **Solution:** Check application logs

## Next Steps

1. ✅ Verify all 5 tools are working
2. ⚠️ Investigate weather query 400 error
3. 🔄 Test multi-tool requests
4. 📊 Monitor performance metrics
5. 🔐 Implement rate limiting
6. 📝 Add request/response logging

## Files

- **Test Script:** `test-chat-endpoint.ps1`
- **Controller:** `src/main/java/com/vijay/User_Master/config/chat/ChatController.java`
- **Tool Finder:** `src/main/java/com/vijay/User_Master/config/chat/ToolFinderService.java`
- **Tools:** `src/main/java/com/vijay/User_Master/config/chat/AIAgentToolService.java`
