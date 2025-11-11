# Test AdvancedTutorAgentManager Endpoints with JWT Authentication
$baseUrl = "http://localhost:9091"
$passed = 0
$failed = 0
$total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING ADVANCED TUTOR AGENT MANAGER" -ForegroundColor Cyan
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

# Create headers with JWT token
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "Step 2: Testing Advanced Tutor Agent Endpoints..." -ForegroundColor Yellow
Write-Host ""

# Helper function to test endpoint
function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Uri,
        [string]$Body = $null
    )
    
    $script:total++
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -Body $Body -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -ErrorAction Stop
        }
        Write-Host "  ✅ $Name" -ForegroundColor Green
        Write-Host "     Response: $response" -ForegroundColor Gray
        $script:passed++
        return $response
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -eq 404) {
            Write-Host "  ⚠️  $Name (404 - Endpoint not found)" -ForegroundColor Yellow
        } elseif ($statusCode -eq 400) {
            Write-Host "  ⚠️  $Name (400 - Bad Request)" -ForegroundColor Yellow
        } elseif ($statusCode -eq 500) {
            Write-Host "  ❌ $Name (500 - Server Error)" -ForegroundColor Red
        } else {
            Write-Host "  ⚠️  $Name ($statusCode - $($_.Exception.Message))" -ForegroundColor Yellow
        }
        $script:failed++
        return $null
    }
}

# =====================================================
# TEST 1: Run Adaptive Tutor (Algebra)
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 1: Run Adaptive Tutor - Algebra" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$tutorUrl1 = "$baseUrl/api/manager-agents/run/adaptive-tutor?studentId=1&skillKey=Algebra&gradeLevel=Grade8&maxLoops=3"
$result1 = Test-Endpoint "POST /run/adaptive-tutor (Algebra)" "POST" $tutorUrl1
Write-Host ""

# =====================================================
# TEST 2: Run Adaptive Tutor (Geometry)
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Run Adaptive Tutor - Geometry" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$tutorUrl2 = "$baseUrl/api/manager-agents/run/adaptive-tutor?studentId=2&skillKey=Geometry&gradeLevel=Grade9&maxLoops=3"
$result2 = Test-Endpoint "POST /run/adaptive-tutor (Geometry)" "POST" $tutorUrl2
Write-Host ""

# =====================================================
# TEST 3: Run Adaptive Tutor (Physics)
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Run Adaptive Tutor - Physics" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$tutorUrl3 = "$baseUrl/api/manager-agents/run/adaptive-tutor?studentId=3&skillKey=Physics&gradeLevel=Grade10&maxLoops=3"
$result3 = Test-Endpoint "POST /run/adaptive-tutor (Physics)" "POST" $tutorUrl3
Write-Host ""

# =====================================================
# TEST 4: Run Adaptive Tutor (Chemistry)
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: Run Adaptive Tutor - Chemistry" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$tutorUrl4 = "$baseUrl/api/manager-agents/run/adaptive-tutor?studentId=4&skillKey=Chemistry&gradeLevel=Grade10&maxLoops=3"
$result4 = Test-Endpoint "POST /run/adaptive-tutor (Chemistry)" "POST" $tutorUrl4
Write-Host ""

# =====================================================
# TEST 5: Run Adaptive Tutor (English)
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Run Adaptive Tutor - English" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$tutorUrl5 = "$baseUrl/api/manager-agents/run/adaptive-tutor?studentId=5&skillKey=English&gradeLevel=Grade8&maxLoops=3"
$result5 = Test-Endpoint "POST /run/adaptive-tutor (English)" "POST" $tutorUrl5
Write-Host ""

# =====================================================
# SUMMARY
# =====================================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total Endpoints Tested: $total" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red

if ($total -gt 0) {
    $successRate = [math]::Round(($passed / $total) * 100, 2)
    Write-Host "Success Rate: $successRate%" -ForegroundColor $(if($successRate -ge 80) {"Green"} elseif($successRate -ge 60) {"Yellow"} else {"Red"})
}

Write-Host ""

if ($passed -eq $total -and $total -gt 0) {
    Write-Host "🎉 EXCELLENT! All endpoints are working!" -ForegroundColor Green
} elseif ($passed -ge ($total / 2) -and $total -gt 0) {
    Write-Host "⚠️  GOOD! Most endpoints are working." -ForegroundColor Yellow
} else {
    Write-Host "❌ ATTENTION NEEDED! Check the errors above." -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Test Completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
