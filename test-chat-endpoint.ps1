# Test Chat Endpoint with JWT Authentication
$baseUrl = "http://localhost:9091"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING /CHAT ENDPOINT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Login
Write-Host "Step 1: Authenticating..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = "vijay-admin"
    password = "vijay"
} | ConvertTo-Json

try {
    $webResponse = Invoke-WebRequest -Uri ($baseUrl + "/api/auth/login") -Method POST -Headers @{"Content-Type"="application/json"} -Body $loginBody -UseBasicParsing
    $authResponse = $webResponse.Content | ConvertFrom-Json
    $token = $authResponse.data.jwtToken
    Write-Host "✅ Authenticated!" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
} catch {
    Write-Host "❌ Login failed!" -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}

Write-Host ""

# Step 2: Create headers with JWT token
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "Step 2: Testing /chat endpoint..." -ForegroundColor Yellow
Write-Host ""

# Test 1: Weather Query
Write-Host "Test 1: Weather Query" -ForegroundColor Cyan
Write-Host "URL: $baseUrl/chat?prompt=What%20is%20the%20weather%20in%20London%3F" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/chat?prompt=What%20is%20the%20weather%20in%20London%3F" -Method GET -Headers $headers
    Write-Host "✅ Response: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 2: Math Operation (Addition)
Write-Host "Test 2: Math Operation (123 + 456)" -ForegroundColor Cyan
Write-Host "URL: $baseUrl/chat?prompt=What%20is%20123%20plus%20456%3F" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/chat?prompt=What%20is%20123%20plus%20456%3F" -Method GET -Headers $headers
    Write-Host "✅ Response: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 3: Date/Time Query
Write-Host "Test 3: Date/Time Query" -ForegroundColor Cyan
Write-Host "URL: $baseUrl/chat?prompt=What%20is%20the%20date%20and%20time%20right%20now%3F" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/chat?prompt=What%20is%20the%20date%20and%20time%20right%20now%3F" -Method GET -Headers $headers
    Write-Host "✅ Response: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 4: Multiplication
Write-Host "Test 4: Multiplication (10 * 20)" -ForegroundColor Cyan
Write-Host "URL: $baseUrl/chat?prompt=What%20is%2010%20times%2020%3F" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/chat?prompt=What%20is%2010%20times%2020%3F" -Method GET -Headers $headers
    Write-Host "✅ Response: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 5: Email Sending
Write-Host "Test 5: Email Sending" -ForegroundColor Cyan
Write-Host "URL: $baseUrl/chat?prompt=Please%20send%20an%20email%20to%20vijay%40example.com%20saying%20'the%20RAG%20system%20is%20working'" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/chat?prompt=Please%20send%20an%20email%20to%20vijay%40example.com%20saying%20'the%20RAG%20system%20is%20working'" -Method GET -Headers $headers
    Write-Host "✅ Response: $response" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Chat Endpoint Tests Completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
