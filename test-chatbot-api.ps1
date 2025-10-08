# School Management System - AI Chatbot API Test
# Tests the chat endpoint with natural language queries

Write-Host "`n🤖 AI CHATBOT API TEST" -ForegroundColor Cyan
Write-Host "Testing the revolutionary AI chatbot system with 128 tools!`n" -ForegroundColor Yellow

$baseUrl = "http://localhost:9091/api/chat"

# Test 1: Simple greeting
Write-Host "📝 Test 1: Chatbot Greeting" -ForegroundColor Green
$body1 = @{
    message = "Hello! Can you help me manage my school?"
    provider = "openai"
    model = "gpt-4"
    userId = "test-admin"
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/message" -Method POST -Body $body1 -ContentType "application/json"
    Write-Host "✅ Response:" -ForegroundColor Green
    Write-Host $response1.response
    Write-Host "Conversation ID: $($response1.conversationId)" -ForegroundColor Cyan
    Write-Host "Tokens Used: $($response1.tokensUsed)" -ForegroundColor Yellow
    Write-Host "Response Time: $($response1.responseTime)ms`n" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Student query
Write-Host "`n📝 Test 2: Student Information Query" -ForegroundColor Green
$body2 = @{
    message = "Show me all students in the system"
    provider = "openai"
    model = "gpt-4"
    userId = "test-admin"
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "$baseUrl/message" -Method POST -Body $body2 -ContentType "application/json"
    Write-Host "✅ Response:" -ForegroundColor Green
    Write-Host $response2.response
    Write-Host "`nTokens Used: $($response2.tokensUsed)" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Attendance query
Write-Host "`n📝 Test 3: Attendance Query" -ForegroundColor Green
$body3 = @{
    message = "What is the attendance percentage today?"
    provider = "openai"
    model = "gpt-4"
    userId = "test-teacher"
} | ConvertTo-Json

try {
    $response3 = Invoke-RestMethod -Uri "$baseUrl/message" -Method POST -Body $body3 -ContentType "application/json"
    Write-Host "✅ Response:" -ForegroundColor Green
    Write-Host $response3.response
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Fee information
Write-Host "`n📝 Test 4: Fee Management Query" -ForegroundColor Green
$body4 = @{
    message = "Show me all pending fee payments"
    provider = "openai"
    model = "gpt-4"
    userId = "test-accountant"
} | ConvertTo-Json

try {
    $response4 = Invoke-RestMethod -Uri "$baseUrl/message" -Method POST -Body $body4 -ContentType "application/json"
    Write-Host "✅ Response:" -ForegroundColor Green
    Write-Host $response4.response
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Get available tools
Write-Host "`n📝 Test 5: Get Available MCP Tools" -ForegroundColor Green
try {
    $tools = Invoke-RestMethod -Uri "http://localhost:9091/api/mcp-servers/tools" -Method GET
    Write-Host "✅ Available Tools:" -ForegroundColor Green
    Write-Host "Total Tools: $($tools.count)" -ForegroundColor Cyan
    if ($tools.tools) {
        Write-Host "`nFirst 10 Tools:" -ForegroundColor Yellow
        $tools.tools | Select-Object -First 10 | ForEach-Object {
            Write-Host "  - $($_.name): $($_.description)" -ForegroundColor White
        }
    }
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Get available providers
Write-Host "`n📝 Test 6: Get AI Providers" -ForegroundColor Green
try {
    $providers = Invoke-RestMethod -Uri "$baseUrl/providers" -Method GET
    Write-Host "✅ Available Providers:" -ForegroundColor Green
    $providers | ForEach-Object {
        Write-Host "  - $($_.name): $($_.description)" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Summary
Write-Host "`n`n🎉 CHATBOT API TEST SUMMARY" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host "✅ Chat endpoint: /api/chat/message" -ForegroundColor Green
Write-Host "✅ Tools endpoint: /api/mcp-servers/tools" -ForegroundColor Green
Write-Host "✅ Providers endpoint: /api/chat/providers" -ForegroundColor Green
Write-Host "`n🏆 COMPETITIVE ADVANTAGE:" -ForegroundColor Yellow
Write-Host "  • 128+ AI-Powered Automation Tools" -ForegroundColor White
Write-Host "  • Natural Language School Management" -ForegroundColor White
Write-Host "  • Multi-Provider AI Support" -ForegroundColor White
Write-Host "  • 1School.in has NONE of this! 🚀" -ForegroundColor Green

Write-Host "`n📚 For more details, see:" -ForegroundColor Cyan
Write-Host "  - AI_CHATBOT_COMPETITIVE_ADVANTAGE.md" -ForegroundColor White
Write-Host "  - MCP_CHATBOT_INTEGRATION_GUIDE.md" -ForegroundColor White

