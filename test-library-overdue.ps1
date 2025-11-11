# Test LibraryOverdueManager Endpoints
$baseUrl = "http://localhost:9091"
$passed = 0; $failed = 0; $total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING LIBRARY OVERDUE MANAGER" -ForegroundColor Cyan
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
Write-Host "TEST 1: Start Overdue Run" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
$runId = Test-Endpoint "POST /library/overdue/start" "POST" "$baseUrl/api/manager-agents/library/overdue/start?includeDueToday=true&autoExtendDays=7"
if ($runId -and $runId -is [string] -and $runId -match "([a-f0-9]{32})") { $runId = $matches[1] }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Auto-Extend Due Today" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /library/overdue/auto-extend" "POST" "$baseUrl/api/manager-agents/library/overdue/auto-extend?runId=$runId&days=7" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Apply Fines" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /library/overdue/apply-fines" "POST" "$baseUrl/api/manager-agents/library/overdue/apply-fines?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: Notify Borrowers" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /library/overdue/notify" "POST" "$baseUrl/api/manager-agents/library/overdue/notify?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Finish Run" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /library/overdue/finish" "POST" "$baseUrl/api/manager-agents/library/overdue/finish?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 6: Get Run State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "GET /library/overdue/state" "GET" "$baseUrl/api/manager-agents/library/overdue/state?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY: $passed/$total Passed ($(if($total -gt 0) { [math]::Round(($passed/$total)*100, 2) })%)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
