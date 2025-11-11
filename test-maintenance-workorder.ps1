# Test MaintenanceWorkOrderManager Endpoints
$baseUrl = "http://localhost:9091"
$passed = 0; $failed = 0; $total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING MAINTENANCE WORK ORDER MANAGER" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$loginBody = @{usernameOrEmail = "vijay-admin"; password = "vijay"} | ConvertTo-Json
$webResponse = Invoke-WebRequest -Uri ($baseUrl + "/api/auth/login") -Method POST -Headers @{"Content-Type"="application/json"} -Body $loginBody -UseBasicParsing
$token = ($webResponse.Content | ConvertFrom-Json).data.jwtToken
$headers = @{"Authorization" = "Bearer $token"; "Content-Type" = "application/json"}

function Test-Endpoint {
    param([string]$Name, [string]$Method, [string]$Uri)
    $script:total++
    try {
        $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -ErrorAction Stop
        Write-Host "  ✅ $Name" -ForegroundColor Green
        Write-Host "     Response: $response" -ForegroundColor Gray
        $script:passed++
        return $response
    } catch {
        Write-Host "  ⚠️  $Name ($($_.Exception.Response.StatusCode.value__))" -ForegroundColor Yellow
        $script:failed++
        return $null
    }
}

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 1: Start Maintenance Work Order" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
$runId = Test-Endpoint "POST /maintenance/start" "POST" "$baseUrl/api/manager-agents/maintenance/start?title=Roof%20Repair&description=Fix%20leaking%20roof&costEstimate=5000"
if ($runId -and $runId -is [string] -and $runId -match "([a-f0-9]{32})") { $runId = $matches[1] }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Approve Work Order" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /maintenance/approve" "POST" "$baseUrl/api/manager-agents/maintenance/approve?runId=$runId&approverUserId=1" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Assign Work Order" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /maintenance/assign" "POST" "$baseUrl/api/manager-agents/maintenance/assign?runId=$runId&assigneeUserId=2" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: Complete Work Order" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /maintenance/complete" "POST" "$baseUrl/api/manager-agents/maintenance/complete?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Get Run State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "GET /maintenance/state" "GET" "$baseUrl/api/manager-agents/maintenance/state?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY: $passed/$total Passed ($(if($total -gt 0) { [math]::Round(($passed/$total)*100, 2) })%)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
