# Detailed Test of Adaptive Learning

Write-Host "=== ADAPTIVE LEARNING DETAILED TEST ===" -ForegroundColor Cyan
Write-Host ""

# Login
$response = Invoke-RestMethod -Uri "http://localhost:9091/api/auth/login" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"usernameOrEmail":"vijay-admin","password":"vijay"}'
$token = $response.data.jwtToken
Write-Host "Login successful!" -ForegroundColor Green
Write-Host ""

# Check if we need to create prerequisite data
Write-Host "Checking prerequisites..." -ForegroundColor Yellow

# Test recording interaction with detailed output
Write-Host "Recording learning interaction..." -ForegroundColor Yellow
$interactionBody = @{
    studentId = 1
    moduleId = 1
    skillKey = "algebra_linear_equations"
    difficulty = "MEDIUM"
    outcome = "CORRECT"
    score = 85.0
    timeTakenSeconds = 120
    hintsUsed = 0
    questionType = "MCQ"
    confidenceLevel = 4
} | ConvertTo-Json

Write-Host "Request body:" -ForegroundColor Gray
Write-Host $interactionBody -ForegroundColor Gray
Write-Host ""

try {
    $result = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/tutoring/adaptive/record-interaction" -Method POST -Headers @{"Authorization"="Bearer $token";"Content-Type"="application/json"} -Body $interactionBody
    Write-Host "SUCCESS! Interaction recorded:" -ForegroundColor Green
    Write-Host "  Skill: $($result.skillName)" -ForegroundColor White
    Write-Host "  Mastery Level: $($result.masteryLevel)%" -ForegroundColor White
    Write-Host "  Accuracy: $($result.avgAccuracy)%" -ForegroundColor White
    Write-Host "  Total Attempts: $($result.totalAttempts)" -ForegroundColor White
    Write-Host "  Correct Attempts: $($result.correctAttempts)" -ForegroundColor White
    Write-Host "  Mastery Category: $($result.masteryCategory)" -ForegroundColor White
    Write-Host "  Recommended Difficulty: $($result.recommendedDifficulty)" -ForegroundColor White
    Write-Host ""
} catch {
    Write-Host "ERROR recording interaction:" -ForegroundColor Red
    Write-Host "  Message: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    Write-Host ""
}

# Now check mastery again
Write-Host "Checking mastery after interaction..." -ForegroundColor Yellow
try {
    $uri = "http://localhost:9091/api/v1/tutoring/adaptive/mastery/1"
    $mastery = Invoke-RestMethod -Uri $uri -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "Total skills tracked: $($mastery.Count)" -ForegroundColor Green
    
    if ($mastery.Count -gt 0) {
        Write-Host ""
        Write-Host "Skill Details:" -ForegroundColor Cyan
        foreach ($skill in $mastery) {
            Write-Host "  - $($skill.skillName): $($skill.masteryLevel)% ($($skill.masteryCategory))" -ForegroundColor White
        }
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test getting next module recommendation
Write-Host "Getting next module recommendation..." -ForegroundColor Yellow
try {
    $uri = "http://localhost:9091/api/v1/tutoring/adaptive/next-module?studentId=1&subjectId=1"
    $next = Invoke-RestMethod -Uri $uri -Method GET -Headers @{"Authorization"="Bearer $token"}
    
    if ($next) {
        Write-Host "Recommendation received:" -ForegroundColor Green
        Write-Host "  Skill: $($next.skillName)" -ForegroundColor White
        Write-Host "  Type: $($next.recommendationType)" -ForegroundColor White
        Write-Host "  Difficulty: $($next.difficulty)" -ForegroundColor White
        Write-Host "  Rationale: $($next.rationale)" -ForegroundColor White
    } else {
        Write-Host "No recommendation (null response)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Get statistics
Write-Host "Getting adaptive learning statistics..." -ForegroundColor Yellow
try {
    $stats = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/tutoring/adaptive/statistics/1" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "Statistics:" -ForegroundColor Green
    Write-Host "  Average Mastery: $($stats.avgMastery)%" -ForegroundColor White
    Write-Host "  High Mastery Skills: $($stats.highMasterySkills)" -ForegroundColor White
    Write-Host "  Medium Mastery Skills: $($stats.mediumMasterySkills)" -ForegroundColor White
    Write-Host "  Low Mastery Skills: $($stats.lowMasterySkills)" -ForegroundColor White
    Write-Host "  Total Interactions: $($stats.totalInteractions)" -ForegroundColor White
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== TEST COMPLETE ===" -ForegroundColor Cyan
