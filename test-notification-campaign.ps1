# Test NotificationCampaignManager Endpoints
$baseUrl = "http://localhost:9091"
$passed = 0; $failed = 0; $total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING NOTIFICATION CAMPAIGN MANAGER" -ForegroundColor Cyan
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
Write-Host "TEST 1: Start Campaign" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
$runId = Test-Endpoint "POST /notifications/campaign/start" "POST" "$baseUrl/api/manager-agents/notifications/campaign/start?campaignName=Exam%20Alert&channel=EMAIL&audienceType=STUDENTS"
if ($runId -and $runId -is [string] -and $runId -match "([a-f0-9]{32})") { $runId = $matches[1] }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Select Audience" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /notifications/campaign/select-audience" "POST" "$baseUrl/api/manager-agents/notifications/campaign/select-audience?runId=$runId&classIdsCsv=1,2,3&studentIdsCsv=1,2,3,4,5" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Schedule Campaign" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /notifications/campaign/schedule" "POST" "$baseUrl/api/manager-agents/notifications/campaign/schedule?runId=$runId&scheduledAt=2025-11-12T10:00:00" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: Send Campaign" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /notifications/campaign/send" "POST" "$baseUrl/api/manager-agents/notifications/campaign/send?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Get Campaign Stats" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "GET /notifications/campaign/stats" "GET" "$baseUrl/api/manager-agents/notifications/campaign/stats?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 6: Get Run State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "GET /notifications/campaign/state" "GET" "$baseUrl/api/manager-agents/notifications/campaign/state?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY: $passed/$total Passed ($(if($total -gt 0) { [math]::Round(($passed/$total)*100, 2) })%)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
