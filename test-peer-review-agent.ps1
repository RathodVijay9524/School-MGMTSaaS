# Test PeerReviewAgentManager Endpoints with JWT Authentication
$baseUrl = "http://localhost:9091"
$passed = 0
$failed = 0
$total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING PEER REVIEW AGENT MANAGER" -ForegroundColor Cyan
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

Write-Host "Step 2: Testing Peer Review Agent Endpoints..." -ForegroundColor Yellow
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
# TEST 1: Run Peer Review Workflow
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 1: Run Peer Review Workflow" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$peerReviewUrl = "$baseUrl/api/manager-agents/run/peer-review?assignmentId=1&reviewsPerSubmission=3&waitDays=2&lazyThreshold=0.2&autoGrade=true&rubricId=1"
$runId = Test-Endpoint "POST /run/peer-review" "POST" $peerReviewUrl
Write-Host ""

# Extract runId from response if it's a string
if ($runId -and $runId -is [string]) {
    if ($runId -match "([a-f0-9]{32})") {
        $runId = $matches[1]
        Write-Host "Extracted RunId: $runId" -ForegroundColor Gray
    }
}

# =====================================================
# TEST 2: Get Peer Review State
# =====================================================
Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Get Peer Review State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

if ($runId) {
    $stateUrl = "$baseUrl/api/manager-agents/run/peer-review-state?runId=$runId"
    Test-Endpoint "GET /run/peer-review-state" "GET" $stateUrl | Out-Null
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
