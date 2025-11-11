# Test TransferCertificateOrchestrationManager Endpoints
$baseUrl = "http://localhost:9091"
$passed = 0; $failed = 0; $total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING TRANSFER CERTIFICATE MANAGER" -ForegroundColor Cyan
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
Write-Host "TEST 1: Start Transfer Certificate" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
$runId = Test-Endpoint "POST /tc/start" "POST" "$baseUrl/api/manager-agents/tc/start?studentId=1&issuedByUserId=1&reasonForLeaving=TRANSFER&reasonDetails=Student%20transferred&lastAttendanceDate=2025-11-10&academicYearOfLeaving=2025-2026&conduct=GOOD&generalRemarks=Good%20student"
if ($runId -and $runId -is [string] -and $runId -match "([a-f0-9]{32})") { $runId = $matches[1] }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Approve Certificate" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /tc/approve" "POST" "$baseUrl/api/manager-agents/tc/approve?runId=$runId&approvedByUserId=2" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Issue Certificate" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /tc/issue" "POST" "$baseUrl/api/manager-agents/tc/issue?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: Generate PDF" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /tc/generate-pdf" "POST" "$baseUrl/api/manager-agents/tc/generate-pdf?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Finish Certificate" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /tc/finish" "POST" "$baseUrl/api/manager-agents/tc/finish?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 6: Get Run State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "GET /tc/state" "GET" "$baseUrl/api/manager-agents/tc/state?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY: $passed/$total Passed ($(if($total -gt 0) { [math]::Round(($passed/$total)*100, 2) })%)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
