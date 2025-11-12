# Test AtRiskStudentAgentManager via Chat Endpoint
$baseUrl = "http://localhost:9091"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING AtRiskStudentAgentManager" -ForegroundColor Cyan
Write-Host "Testing At-Risk Student Analysis" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Login
Write-Host "Step 1: Authenticating..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = "vijay-admin"
    password = "vijay"
} | ConvertTo-Json

$webResponse = Invoke-WebRequest -Uri ($baseUrl + "/api/auth/login") -Method POST -Headers @{"Content-Type"="application/json"} -Body $loginBody -UseBasicParsing
$authResponse = $webResponse.Content | ConvertFrom-Json
$token = $authResponse.data.jwtToken
Write-Host "✅ Authenticated!" -ForegroundColor Green

Write-Host ""
Write-Host "Step 2: Testing AtRiskStudentAgentManager via Chat..." -ForegroundColor Yellow
Write-Host ""

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

function Test-AtRiskStudent {
    param(
        [string]$Name,
        [string]$Query
    )
    
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
    Write-Host "TEST: $Name" -ForegroundColor Cyan
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
    
    $body = @{
        message = $Query
    } | ConvertTo-Json

    Write-Host "Query: $Query" -ForegroundColor Gray
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/chat/with-tools" -Method POST -Headers $headers -Body $body -ErrorAction Stop
        
        if ($response.status -eq "SUCCESS") {
            Write-Host "✅ SUCCESS" -ForegroundColor Green
            Write-Host "Tools Found: $($response.toolCount)" -ForegroundColor Yellow
            Write-Host "Tools: $($response.toolsIdentified -join ', ')" -ForegroundColor Yellow
            Write-Host "Response: $($response.response.Substring(0, [Math]::Min(150, $response.response.Length)))..." -ForegroundColor White
        } else {
            Write-Host "❌ ERROR" -ForegroundColor Red
            Write-Host "Error: $($response.error)" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Exception!" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
}

# Test 1: At-Risk Student Analysis
Test-AtRiskStudent "At-Risk Student Analysis" "Analyze at-risk students in class 1 with attendance below 80 percent for subject 1"

# Test 2: At-Risk with Different Threshold
Test-AtRiskStudent "At-Risk with Custom Threshold" "Find students with low attendance below 75 percent and failing grades"

# Test 3: At-Risk General Query
Test-AtRiskStudent "At-Risk General Query" "Check which students are at risk of failing"

# Test 4: At-Risk with Class Focus
Test-AtRiskStudent "At-Risk Class Focus" "Run at-risk analysis for class 1"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ AtRiskStudentAgentManager Test Completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
