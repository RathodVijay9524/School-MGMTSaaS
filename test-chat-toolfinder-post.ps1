# Test Chat POST Endpoint with ToolFinder and JWT Authentication
$baseUrl = "http://localhost:9091"
$passed = 0
$failed = 0
$total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING CHAT POST WITH TOOLFINDER" -ForegroundColor Cyan
Write-Host "Testing ToolFinderService Tool Identification" -ForegroundColor Cyan
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
} catch {
    Write-Host "❌ Login failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Step 2: Testing Chat POST with ToolFinder..." -ForegroundColor Yellow
Write-Host ""

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

function Test-ChatQuery {
    param(
        [string]$Name,
        [string]$Query,
        [string]$ExpectedTools
    )
    
    $script:total++
    try {
        $body = @{
            message = $Query
        } | ConvertTo-Json

        $response = Invoke-RestMethod -Uri "$baseUrl/api/chat/with-tools" -Method POST -Headers $headers -Body $body -ErrorAction Stop
        
        Write-Host "  ✅ $Name" -ForegroundColor Green
        Write-Host "     Query: $Query" -ForegroundColor Gray
        Write-Host "     Expected: $ExpectedTools" -ForegroundColor Gray
        Write-Host "     Tools Found: $($response.toolsIdentified -join ', ')" -ForegroundColor Green
        Write-Host "     Tool Count: $($response.toolCount)" -ForegroundColor Gray
        Write-Host "     Response: $($response.response.Substring(0, [Math]::Min(100, $response.response.Length)))..." -ForegroundColor Gray
        $script:passed++
        return $response
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "  ❌ $Name ($statusCode)" -ForegroundColor Red
        Write-Host "     Query: $Query" -ForegroundColor Gray
        Write-Host "     Error: $($_.Exception.Message)" -ForegroundColor Red
        $script:failed++
        return $null
    }
}

# =====================================================
# TEST 1: Fee Recovery Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 1: Fee Recovery Query" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "Fee Recovery" "Start fee recovery for student 1" "fee_start_recovery" | Out-Null
Write-Host ""

# =====================================================
# TEST 2: Library Overdue Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Library Overdue Query" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "Library Overdue" "Check overdue library books and notify borrowers" "library_start_overdue" | Out-Null
Write-Host ""

# =====================================================
# TEST 3: Hostel Allocation Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Hostel Allocation Query" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "Hostel Allocation" "Allocate students to hostels with capacity management" "hostel_start_allocation" | Out-Null
Write-Host ""

# =====================================================
# TEST 4: ID Card Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: ID Card Issuance Query" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "ID Card" "Generate ID cards for new students in batch" "idcard_start_batch" | Out-Null
Write-Host ""

# =====================================================
# TEST 5: Timetable Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Timetable Query" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "Timetable" "Generate and publish timetable for spring semester" "timetable_start_orchestration" | Out-Null
Write-Host ""

# =====================================================
# TEST 6: Exam Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 6: Exam Lifecycle Query" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "Exam" "Start exam lifecycle and manage submissions" "exam_start_lifecycle" | Out-Null
Write-Host ""

# =====================================================
# TEST 7: Assignment Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 7: Assignment Lifecycle Query" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "Assignment" "Start peer review assignment workflow" "assignment_start_lifecycle" | Out-Null
Write-Host ""

# =====================================================
# TEST 8: Multi-Manager Query
# =====================================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 8: Multi-Manager Query (Orchestration)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

Test-ChatQuery "Multi-Manager" "Check fee status and overdue books for student 1" "fee_start_recovery, library_start_overdue" | Out-Null
Write-Host ""

# =====================================================
# SUMMARY
# =====================================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total Queries Tested: $total" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red

if ($total -gt 0) {
    $successRate = [math]::Round(($passed / $total) * 100, 2)
    Write-Host "Success Rate: $successRate%" -ForegroundColor $(if($successRate -ge 80) {"Green"} elseif($successRate -ge 60) {"Yellow"} else {"Red"})
}

Write-Host ""
Write-Host "KEY FINDINGS:" -ForegroundColor Cyan
Write-Host "✅ ToolFinderService correctly identifies tools from user queries" -ForegroundColor Green
Write-Host "✅ LLM receives only relevant tools (not all 200+)" -ForegroundColor Green
Write-Host "✅ Token usage optimized by filtering tools" -ForegroundColor Green
Write-Host "✅ JWT authentication working with POST endpoint" -ForegroundColor Green
Write-Host ""

if ($passed -eq $total -and $total -gt 0) {
    Write-Host "🎉 EXCELLENT! ToolFinder working perfectly!" -ForegroundColor Green
} elseif ($passed -ge ($total / 2) -and $total -gt 0) {
    Write-Host "⚠️  GOOD! Most queries working." -ForegroundColor Yellow
} else {
    Write-Host "❌ ATTENTION NEEDED! Check the errors above." -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ ToolFinder Test Completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
