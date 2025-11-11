# Test Chat with Basic Tools (add, multiply, weather, email, datetime)
$baseUrl = "http://localhost:9091"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING CHAT WITH BASIC TOOLS" -ForegroundColor Cyan
Write-Host "Testing ToolFinderService with simple math tools" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Login
Write-Host "Step 1: Authenticating..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = "vijay-admin"
    password = "vijay"
} | ConvertTo-Json

$webResponse = Invoke-WebRequest -Uri ($baseUrl + "/api/auth/login") -Method POST -Headers @{"Content-Type"="application/json"} -Body $loginBody -UseBasicParsing
$authResponse = $webResponse.Content | ConvertFrom-Json
$token = $authResponse.data.jwtToken
Write-Host "✅ Authenticated!" -ForegroundColor Green

Write-Host ""
Write-Host "Step 2: Testing Chat with Basic Tools..." -ForegroundColor Yellow
Write-Host ""

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

function Test-BasicTool {
    param(
        [string]$Name,
        [string]$Query
    )
    
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
    Write-Host "TEST: $Name" -ForegroundColor Cyan
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
    
    $body = @{
        message = $Query
    } | ConvertTo-Json

    Write-Host "Query: $Query" -ForegroundColor Gray
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/chat/with-tools" -Method POST -Headers $headers -Body $body -ErrorAction Stop
        
        if ($response.status -eq "SUCCESS") {
            Write-Host "✅ SUCCESS" -ForegroundColor Green
            Write-Host "Tools Found: $($response.toolCount)" -ForegroundColor Yellow
            Write-Host "Response: $($response.response)" -ForegroundColor White
        } else {
            Write-Host "❌ ERROR" -ForegroundColor Red
            Write-Host "Error: $($response.error)" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Exception!" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
}

# Test 1: Math
Test-BasicTool "Math Addition" "What is 5 plus 3?"

# Test 2: Math Multiplication
Test-BasicTool "Math Multiplication" "Multiply 7 by 8"

# Test 3: Weather
Test-BasicTool "Weather" "What is the weather in London?"

# Test 4: Email
Test-BasicTool "Email" "Send an email to test@example.com saying hello"

# Test 5: DateTime
Test-BasicTool "DateTime" "What is the current date and time?"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Basic Tools Test Completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
