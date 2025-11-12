# Test AdmissionsFunnelManager via Chat Endpoint
$baseUrl = "http://localhost:9091"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING AdmissionsFunnelManager" -ForegroundColor Cyan
Write-Host "Testing Admissions Funnel Workflow" -ForegroundColor Cyan
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
Write-Host "Step 2: Testing AdmissionsFunnelManager via Chat..." -ForegroundColor Yellow
Write-Host ""

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

function Test-Admissions {
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

# Test 1: Start Admissions
Test-Admissions "Start Admissions" "Start admissions for applicant John Doe with email john@example.com for grade 10"

# Test 2: Submit Documents
Test-Admissions "Submit Documents" "Submit documents for admissions process with document IDs 1,2,3"

# Test 3: Schedule Interview
Test-Admissions "Schedule Interview" "Schedule interview for admissions applicant on 2025-11-20 at 10:00 AM"

# Test 4: Interview Feedback
Test-Admissions "Interview Feedback" "Record interview feedback with score 85 for admissions process"

# Test 5: Final Decision
Test-Admissions "Final Decision" "Make final decision to approve admissions for the applicant"

# Test 6: Initiate Fee
Test-Admissions "Initiate Fee" "Initiate admission fee of 5000 rupees for the approved applicant"

# Test 7: Mark Payment
Test-Admissions "Mark Payment" "Mark payment captured for admission fee with transaction ID TXN123456"

# Test 8: Onboard Student
Test-Admissions "Onboard Student" "Onboard the admitted student to the system"

# Test 9: Onboard with Class
Test-Admissions "Onboard with Class" "Onboard student and assign to class 1"

# Test 10: Get Run State
Test-Admissions "Get Run State" "Get the current state of admissions process"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ AdmissionsFunnelManager Test Completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
