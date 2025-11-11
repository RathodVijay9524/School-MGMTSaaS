# Test ExamLifecycleManager Endpoints with JWT Authentication
$baseUrl = "http://localhost:9091"
$passed = 0
$failed = 0
$total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING EXAM LIFECYCLE MANAGER" -ForegroundColor Cyan
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

Write-Host "Step 2: Testing Exam Lifecycle Endpoints..." -ForegroundColor Yellow
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
# TEST 1: Start Exam Lifecycle
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 1: Start Exam Lifecycle" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$startUrl = "$baseUrl/api/manager-agents/exams/start?examId=1&classId=1&subjectId=1&rubricId=1"
$runId = Test-Endpoint "POST /exams/start" "POST" $startUrl
Write-Host ""

# Extract runId from response if it's a string
if ($runId -and $runId -is [string]) {
    if ($runId -match "([a-f0-9]{32})") {
        $runId = $matches[1]
        Write-Host "Extracted RunId: $runId" -ForegroundColor Gray
    }
}

# =====================================================
# TEST 2: Send Exam Reminder
# =====================================================
Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Send Exam Reminder" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

if ($runId) {
    $reminderUrl = "$baseUrl/api/manager-agents/exams/reminder?runId=$runId"
    Test-Endpoint "POST /exams/reminder" "POST" $reminderUrl | Out-Null
} else {
    Write-Host "  ⚠️  Skipping - No valid runId from previous test" -ForegroundColor Yellow
}

Write-Host ""

# =====================================================
# TEST 3: Collect Submissions
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Collect Submissions" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

if ($runId) {
    $collectUrl = "$baseUrl/api/manager-agents/exams/collect-submissions?runId=$runId&submissionIdsCsv=1,2,3,4,5"
    Test-Endpoint "POST /exams/collect-submissions" "POST" $collectUrl | Out-Null
} else {
    Write-Host "  ⚠️  Skipping - No valid runId from previous test" -ForegroundColor Yellow
}

Write-Host ""

# =====================================================
# TEST 4: AI Grade Batch
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: AI Grade Batch" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

if ($runId) {
    $gradeUrl = "$baseUrl/api/manager-agents/exams/grade-batch?runId=$runId"
    Test-Endpoint "POST /exams/grade-batch" "POST" $gradeUrl | Out-Null
} else {
    Write-Host "  ⚠️  Skipping - No valid runId from previous test" -ForegroundColor Yellow
}

Write-Host ""

# =====================================================
# TEST 5: Publish Results
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Publish Results" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

if ($runId) {
    $publishUrl = "$baseUrl/api/manager-agents/exams/publish?runId=$runId"
    Test-Endpoint "POST /exams/publish" "POST" $publishUrl | Out-Null
} else {
    Write-Host "  ⚠️  Skipping - No valid runId from previous test" -ForegroundColor Yellow
}

Write-Host ""

# =====================================================
# TEST 6: Notify Parents
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 6: Notify Parents" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

if ($runId) {
    $notifyUrl = "$baseUrl/api/manager-agents/exams/notify-parents?runId=$runId&studentIdsCsv=1,2,3"
    Test-Endpoint "POST /exams/notify-parents" "POST" $notifyUrl | Out-Null
} else {
    Write-Host "  ⚠️  Skipping - No valid runId from previous test" -ForegroundColor Yellow
}

Write-Host ""

# =====================================================
# TEST 7: Get Run State
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 7: Get Run State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

if ($runId) {
    $stateUrl = "$baseUrl/api/manager-agents/exams/state?runId=$runId"
    Test-Endpoint "GET /exams/state" "GET" $stateUrl | Out-Null
} else {
    Write-Host "  ⚠️  Skipping - No valid runId from previous test" -ForegroundColor Yellow
}

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
