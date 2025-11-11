# Test IDCardIssuanceManager Endpoints
$baseUrl = "http://localhost:9091"
$passed = 0; $failed = 0; $total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING ID CARD ISSUANCE MANAGER" -ForegroundColor Cyan
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
Write-Host "TEST 1: Start ID Card Batch" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
$runId = Test-Endpoint "POST /idcards/start" "POST" "$baseUrl/api/manager-agents/idcards/start?batchName=Batch-2025-Nov"
if ($runId -and $runId -is [string] -and $runId -match "([a-f0-9]{32})") { $runId = $matches[1] }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 2: Ingest Students" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /idcards/ingest-students" "POST" "$baseUrl/api/manager-agents/idcards/ingest-students?runId=$runId&studentIdsCsv=1,2,3,4,5" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 3: Render Cards" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /idcards/render" "POST" "$baseUrl/api/manager-agents/idcards/render?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 4: Print Cards" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /idcards/print" "POST" "$baseUrl/api/manager-agents/idcards/print?runId=$runId&batchSize=10" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 5: Distribute Cards" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "POST /idcards/distribute" "POST" "$baseUrl/api/manager-agents/idcards/distribute?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "TEST 6: Get Run State" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
if ($runId) { Test-Endpoint "GET /idcards/state" "GET" "$baseUrl/api/manager-agents/idcards/state?runId=$runId" | Out-Null } else { Write-Host "  ⚠️  Skipping" -ForegroundColor Yellow }
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY: $passed/$total Passed ($(if($total -gt 0) { [math]::Round(($passed/$total)*100, 2) })%)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
