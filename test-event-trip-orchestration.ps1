# Test EventTripOrchestrationManager Endpoints with JWT Authentication
$baseUrl = "http://localhost:9091"
$passed = 0
$failed = 0
$total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING EVENT/TRIP ORCHESTRATION MANAGER" -ForegroundColor Cyan
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

$headers = @{"Authorization" = "Bearer $token"; "Content-Type" = "application/json"}

Write-Host "Step 2: Testing Event/Trip Orchestration Endpoints..." -ForegroundColor Yellow
Write-Host ""

function Test-Endpoint {
    param([string]$Name, [string]$Method, [string]$Uri, [string]$Body = $null)
    $script:total++
    try {
        $response = if ($Body) { Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -Body $Body -ErrorAction Stop } else { Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -ErrorAction Stop }
        Write-Host "  ✅ $Name" -ForegroundColor Green
        Write-Host "     Response: $response" -ForegroundColor Gray
        $script:passed++
        return $response
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "  ⚠️  $Name ($statusCode)" -ForegroundColor Yellow
        $script:failed++
        return $null
    }
}

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 1: Start Event/Trip Orchestration" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
$startUrl = "$baseUrl/api/manager-agents/events/orch/start?eventId=1"
$runId = Test-Endpoint "POST /events/orch/start" "POST" $startUrl
if ($runId -and $runId -is [string] -and $runId -match "([a-f0-9]{32})") { $runId = $matches[1] }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Open Registration" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /events/orch/open-registration" "POST" "$baseUrl/api/manager-agents/events/orch/open-registration?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping - No runId" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Build Roster" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /events/orch/build-roster" "POST" "$baseUrl/api/manager-agents/events/orch/build-roster?runId=$runId&targetCount=50" | Out-Null } else { Write-Host "  ⚠️  Skipping - No runId" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: Dispatch" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /events/orch/dispatch" "POST" "$baseUrl/api/manager-agents/events/orch/dispatch?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping - No runId" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Post Report" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /events/orch/post-report" "POST" "$baseUrl/api/manager-agents/events/orch/post-report?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping - No runId" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 6: Get Run State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "GET /events/orch/state" "GET" "$baseUrl/api/manager-agents/events/orch/state?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping - No runId" -ForegroundColor Yellow }
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Total: $total | Passed: $passed | Failed: $failed | Success Rate: $(if($total -gt 0) { [math]::Round(($passed/$total)*100, 2) }%)%"
Write-Host ""
if ($passed -eq $total -and $total -gt 0) { Write-Host "🎉 EXCELLENT!" -ForegroundColor Green } elseif ($passed -ge ($total/2)) { Write-Host "⚠️  GOOD!" -ForegroundColor Yellow } else { Write-Host "❌ NEEDS ATTENTION!" -ForegroundColor Red }
Write-Host "========================================" -ForegroundColor Cyan
