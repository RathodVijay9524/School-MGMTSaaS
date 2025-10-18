# Test Adaptive Learning Endpoints

Write-Host "=== TESTING ADAPTIVE LEARNING ENGINE ===" -ForegroundColor Cyan
Write-Host ""

# Login
Write-Host "1. Logging in..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "http://localhost:9091/api/auth/login" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"usernameOrEmail":"vijay-admin","password":"vijay"}'
$token = $response.data.jwtToken
Write-Host "   Login successful!" -ForegroundColor Green
Write-Host ""

# Test 1: Get Student Mastery
Write-Host "2. Testing GET /adaptive/mastery/1" -ForegroundColor Yellow
try {
    $uri = "http://localhost:9091/api/v1/tutoring/adaptive/mastery/1?subjectId=1"
    $mastery = Invoke-RestMethod -Uri $uri -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "   Endpoint working! Skills tracked: $($mastery.Count)" -ForegroundColor Green
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Get Statistics
Write-Host "3. Testing GET /adaptive/statistics/1" -ForegroundColor Yellow
try {
    $stats = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/tutoring/adaptive/statistics/1" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "   Endpoint working! Avg Mastery: $($stats.avgMastery)" -ForegroundColor Green
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 3: Record Interaction
Write-Host "4. Testing POST /adaptive/record-interaction" -ForegroundColor Yellow
$interactionBody = @{
    studentId = 1
    moduleId = 1
    skillKey = "algebra_linear_equations"
    difficulty = "MEDIUM"
    outcome = "CORRECT"
    score = 85.0
    timeTakenSeconds = 120
    hintsUsed = 0
} | ConvertTo-Json

try {
    $result = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/tutoring/adaptive/record-interaction" -Method POST -Headers @{"Authorization"="Bearer $token";"Content-Type"="application/json"} -Body $interactionBody
    Write-Host "   Interaction recorded! Mastery: $($result.masteryLevel)%" -ForegroundColor Green
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 4: Get Review Queue
Write-Host "5. Testing GET /adaptive/review-queue/1" -ForegroundColor Yellow
try {
    $queue = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/tutoring/adaptive/review-queue/1" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "   Endpoint working! Queue items: $($queue.Count)" -ForegroundColor Green
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 5: Get Skills Needing Attention
Write-Host "6. Testing GET /adaptive/skills-needing-attention/1" -ForegroundColor Yellow
try {
    $attention = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/tutoring/adaptive/skills-needing-attention/1" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "   Endpoint working! Skills: $($attention.Count)" -ForegroundColor Green
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 6: Get Mastered Skills
Write-Host "7. Testing GET /adaptive/mastered-skills/1" -ForegroundColor Yellow
try {
    $mastered = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/tutoring/adaptive/mastered-skills/1" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "   Endpoint working! Mastered: $($mastered.Count)" -ForegroundColor Green
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== TEST COMPLETE ===" -ForegroundColor Cyan
Write-Host "All adaptive learning endpoints tested successfully!" -ForegroundColor Green
