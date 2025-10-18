# Comprehensive Test of All 61 LMS Endpoints
$baseUrl = "http://localhost:9091"
$passed = 0
$failed = 0
$total = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING ALL 61 LMS ENDPOINTS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Login
Write-Host "Authenticating..." -ForegroundColor Yellow
$authResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"usernameOrEmail":"vijay-admin","password":"vijay"}'
$token = $authResponse.data.jwtToken
Write-Host "✅ Authenticated!" -ForegroundColor Green
Write-Host ""

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Helper function to test endpoint
function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Uri,
        [string]$Body = $null
    )
    
    $script:total++
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -Body $Body -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -ErrorAction Stop
        }
        Write-Host "  ✅ $Name" -ForegroundColor Green
        $script:passed++
        return $response
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -eq 404) {
            Write-Host "  ⚠️  $Name (404 - Endpoint not found)" -ForegroundColor Yellow
        } elseif ($statusCode -eq 400) {
            Write-Host "  ⚠️  $Name (400 - Bad Request/Validation)" -ForegroundColor Yellow
        } elseif ($statusCode -eq 500) {
            Write-Host "  ❌ $Name (500 - Server Error)" -ForegroundColor Red
        } else {
            Write-Host "  ⚠️  $Name ($statusCode - $($_.Exception.Message))" -ForegroundColor Yellow
        }
        $script:failed++
        return $null
    }
}

# ==============================================
# QUESTION BANK MODULE (18 ENDPOINTS)
# ==============================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "QUESTION BANK MODULE (18 endpoints)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

# Create True/False Question
$tfBody = @{
    questionText = "The Earth is round."
    questionType = "TRUE_FALSE"
    difficulty = "EASY"
    points = 1.0
    subjectId = 1
    classId = 1
    correctAnswer = $true
    autoGradable = $true
} | ConvertTo-Json

$question = Test-Endpoint "POST /questions (Create Question)" "POST" "$baseUrl/api/v1/question-bank/questions" $tfBody
$questionId = if ($question) { $question.id } else { 1 }

Test-Endpoint "PUT /questions/{id} (Update Question)" "PUT" "$baseUrl/api/v1/question-bank/questions/$questionId" $tfBody
Test-Endpoint "GET /questions/{id} (Get Question)" "GET" "$baseUrl/api/v1/question-bank/questions/$questionId"
Test-Endpoint "GET /questions (Get All Questions)" "GET" "$baseUrl/api/v1/question-bank/questions"
Test-Endpoint "GET /questions/subject/{id} (By Subject)" "GET" "$baseUrl/api/v1/question-bank/questions/subject/1"
Test-Endpoint "GET /questions/class/{id} (By Class)" "GET" "$baseUrl/api/v1/question-bank/questions/class/1"
Test-Endpoint "GET /questions/difficulty/{level} (By Difficulty)" "GET" "$baseUrl/api/v1/question-bank/questions/difficulty/EASY"
Test-Endpoint "GET /questions/type/{type} (By Type)" "GET" "$baseUrl/api/v1/question-bank/questions/type/TRUE_FALSE"

$searchBody = '{"difficulty":"EASY"}' 
Test-Endpoint "POST /advanced-search (Search Questions)" "POST" "$baseUrl/api/v1/question-bank/questions/advanced-search" $searchBody

$duplicateBody = "{`"questionId`":$questionId,`"newOwnerId`":1}"
Test-Endpoint "POST /duplicate (Duplicate Question)" "POST" "$baseUrl/api/v1/question-bank/questions/duplicate" $duplicateBody

Test-Endpoint "GET /statistics (Question Statistics)" "GET" "$baseUrl/api/v1/question-bank/statistics"

# Auto-grading is in QuizService, not QuestionBank
# Skipping for now as it requires a quiz attempt

# Tags
$tagBody = '{"tagName":"Math","description":"Mathematics tag"}'
$tag = Test-Endpoint "POST /tags (Create Tag)" "POST" "$baseUrl/api/v1/question-bank/tags" $tagBody
$tagId = if ($tag) { $tag.id } else { 1 }

Test-Endpoint "GET /tags (Get All Tags)" "GET" "$baseUrl/api/v1/question-bank/tags"
Test-Endpoint "GET /tags/{id} (Get Tag)" "GET" "$baseUrl/api/v1/question-bank/tags/$tagId"
Test-Endpoint "POST /tags/{tagId}/add-questions (Add Questions to Tag)" "POST" "$baseUrl/api/v1/question-bank/tags/$tagId/add-questions" "[1,2]"
Test-Endpoint "DELETE /tags/{id} (Delete Tag)" "DELETE" "$baseUrl/api/v1/question-bank/tags/$tagId"
Test-Endpoint "DELETE /questions/{id} (Delete Question)" "DELETE" "$baseUrl/api/v1/question-bank/questions/$questionId"

Write-Host ""

# ==============================================
# QUIZ MODULE (23 ENDPOINTS)
# ==============================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "QUIZ MODULE (23 endpoints)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$quizBody = @{
    title = "Math Quiz 1"
    description = "Test quiz"
    quizType = "GRADED"
    subjectId = 1
    classId = 1
    totalPoints = 10.0
    passingScore = 6.0
    timeLimitMinutes = 30
    maxAttempts = 2
    randomizeQuestions = $true
    showCorrectAnswers = $true
    allowReview = $true
    autoGrade = $true
    isPublished = $false
    isActive = $true
} | ConvertTo-Json

$quiz = Test-Endpoint "POST /quiz (Create Quiz)" "POST" "$baseUrl/api/v1/quiz" $quizBody
$quizId = if ($quiz) { $quiz.id } else { 1 }

Test-Endpoint "PUT /quiz/{id} (Update Quiz)" "PUT" "$baseUrl/api/v1/quiz/$quizId" $quizBody
Test-Endpoint "GET /quiz/{id} (Get Quiz)" "GET" "$baseUrl/api/v1/quiz/$quizId"
Test-Endpoint "DELETE /quiz/{id} (Delete Quiz)" "DELETE" "$baseUrl/api/v1/quiz/$quizId"
Test-Endpoint "GET /quiz (Get All Quizzes)" "GET" "$baseUrl/api/v1/quiz"
Test-Endpoint "GET /quiz/subject/{id} (Quizzes by Subject)" "GET" "$baseUrl/api/v1/quiz/subject/1"
Test-Endpoint "GET /quiz/class/{id} (Quizzes by Class)" "GET" "$baseUrl/api/v1/quiz/class/1"
Test-Endpoint "GET /quiz/available/{studentId} (Available Quizzes)" "GET" "$baseUrl/api/v1/quiz/available/1"

$publishBody = "{`"quizId`":$quizId,`"published`":true}"
Test-Endpoint "POST /quiz/publish (Publish Quiz)" "POST" "$baseUrl/api/v1/quiz/publish" $publishBody

$cloneBody = "{`"quizId`":$quizId,`"newTitle`":`"Cloned Quiz`"}"
Test-Endpoint "POST /quiz/clone (Clone Quiz)" "POST" "$baseUrl/api/v1/quiz/clone" $cloneBody

# Quiz Attempts
Test-Endpoint "POST /quiz/{quizId}/attempt/student/{studentId} (Start Attempt)" "POST" "$baseUrl/api/v1/quiz/1/attempt/student/1"

$attemptBody = @{
    attemptId = 1
    responses = @(
        @{
            questionId = 1
            studentAnswer = "true"
        }
    )
} | ConvertTo-Json -Depth 10

Test-Endpoint "POST /quiz/attempt/student/{id} (Submit Attempt)" "POST" "$baseUrl/api/v1/quiz/attempt/student/1" $attemptBody
Test-Endpoint "GET /quiz/attempt/{id} (Get Attempt)" "GET" "$baseUrl/api/v1/quiz/attempt/1"
Test-Endpoint "GET /quiz/{quizId}/attempts (Get Quiz Attempts)" "GET" "$baseUrl/api/v1/quiz/1/attempts"
Test-Endpoint "GET /quiz/{quizId}/student/{studentId}/attempts (Student Attempts)" "GET" "$baseUrl/api/v1/quiz/1/student/1/attempts"
Test-Endpoint "GET /quiz/review/{attemptId}/student/{studentId} (Quiz Review)" "GET" "$baseUrl/api/v1/quiz/review/1/student/1"
Test-Endpoint "GET /quiz/{quizId}/statistics (Quiz Statistics)" "GET" "$baseUrl/api/v1/quiz/1/statistics"
Test-Endpoint "GET /quiz/student/{studentId}/summary (Student Summary)" "GET" "$baseUrl/api/v1/quiz/student/1/summary"
Test-Endpoint "GET /quiz/teacher/{teacherId}/dashboard (Teacher Dashboard)" "GET" "$baseUrl/api/v1/quiz/teacher/1/dashboard"

# Grading
$gradeReqBody = '{"responseId":1,"score":1.0,"feedback":"Good"}'
Test-Endpoint "POST /quiz/grade/teacher/{id} (Manual Grade)" "POST" "$baseUrl/api/v1/quiz/grade/teacher/1" $gradeReqBody

$batchGradeBody = '{"attemptIds":[1,2],"autoGrade":true}'
Test-Endpoint "POST /quiz/grade/batch/teacher/{id} (Batch Grade)" "POST" "$baseUrl/api/v1/quiz/grade/batch/teacher/1" $batchGradeBody

Write-Host ""

# ==============================================
# QUESTION POOL MODULE (9 ENDPOINTS)
# ==============================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "QUESTION POOL MODULE (9 endpoints)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$poolBody = @{
    poolName = "Math Pool 1"
    description = "Pool of math questions"
    subjectId = 1
    isActive = $true
} | ConvertTo-Json

$pool = Test-Endpoint "POST /question-pool (Create Pool)" "POST" "$baseUrl/api/v1/question-pool" $poolBody
$poolId = if ($pool) { $pool.id } else { 1 }

Test-Endpoint "PUT /question-pool/{id} (Update Pool)" "PUT" "$baseUrl/api/v1/question-pool/$poolId" $poolBody
Test-Endpoint "GET /question-pool/{id} (Get Pool)" "GET" "$baseUrl/api/v1/question-pool/$poolId"
Test-Endpoint "DELETE /question-pool/{id} (Delete Pool)" "DELETE" "$baseUrl/api/v1/question-pool/$poolId"
Test-Endpoint "GET /question-pool (Get All Pools)" "GET" "$baseUrl/api/v1/question-pool"
Test-Endpoint "GET /question-pool/subject/{id} (Pools by Subject)" "GET" "$baseUrl/api/v1/question-pool/subject/1"
Test-Endpoint "POST /question-pool/{poolId}/add-questions (Add Questions)" "POST" "$baseUrl/api/v1/question-pool/$poolId/add-questions" "[1,2]"

$generateBody = '{"poolId":1,"count":5,"difficulty":"EASY","shuffle":true}'
Test-Endpoint "POST /question-pool/generate (Generate Quiz)" "POST" "$baseUrl/api/v1/question-pool/generate" $generateBody

Test-Endpoint "DELETE /question-pool/{poolId}/remove-questions (Remove Questions)" "DELETE" "$baseUrl/api/v1/question-pool/$poolId/remove-questions" "[1]"

Write-Host ""

# ==============================================
# PEER REVIEW MODULE (11 ENDPOINTS)
# ==============================================
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host "PEER REVIEW MODULE (11 endpoints)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan

$assignBody = @{
    assignmentId = 1
    reviewsPerSubmission = 2
    assignmentMethod = "RANDOM"
    allowSelfReview = $false
} | ConvertTo-Json

Test-Endpoint "POST /peer-review/assign (Assign Reviews)" "POST" "$baseUrl/api/v1/peer-review/assign" $assignBody

$reviewBody = @{
    assignmentId = 1
    submissionId = 1
    reviewerId = 2
    rubricId = 1
    overallScore = 85.0
    reviewComments = "Good work"
} | ConvertTo-Json

$review = Test-Endpoint "POST /peer-review (Submit Review)" "POST" "$baseUrl/api/v1/peer-review" $reviewBody
$reviewId = if ($review) { $review.id } else { 1 }

Test-Endpoint "PUT /peer-review/{id}/reviewer/{reviewerId} (Update Review)" "PUT" "$baseUrl/api/v1/peer-review/$reviewId/reviewer/2" $reviewBody
Test-Endpoint "GET /peer-review/{id} (Get Review)" "GET" "$baseUrl/api/v1/peer-review/$reviewId"
Test-Endpoint "GET /peer-review/submission/{id} (Reviews by Submission)" "GET" "$baseUrl/api/v1/peer-review/submission/1"
Test-Endpoint "GET /peer-review/reviewer/{id} (Reviews by Reviewer)" "GET" "$baseUrl/api/v1/peer-review/reviewer/1"
Test-Endpoint "GET /peer-review/assignment/{id} (Reviews by Assignment)" "GET" "$baseUrl/api/v1/peer-review/assignment/1"

$approveBody = '{"reviewId":1,"approved":true,"teacherComments":"Approved"}'
Test-Endpoint "POST /peer-review/approve (Approve Review)" "POST" "$baseUrl/api/v1/peer-review/approve" $approveBody

Test-Endpoint "GET /peer-review/pending/reviewer/{id} (Pending Reviews)" "GET" "$baseUrl/api/v1/peer-review/pending/reviewer/1"
Test-Endpoint "GET /peer-review/statistics/assignment/{id} (Review Statistics)" "GET" "$baseUrl/api/v1/peer-review/statistics/assignment/1"
Test-Endpoint "DELETE /peer-review/{id} (Delete Review)" "DELETE" "$baseUrl/api/v1/peer-review/$reviewId"

Write-Host ""

# ==============================================
# SUMMARY
# ==============================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total Endpoints Tested: $total" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red
$successRate = [math]::Round(($passed / $total) * 100, 2)
Write-Host "Success Rate: $successRate%" -ForegroundColor $(if($successRate -ge 80) {"Green"} elseif($successRate -ge 60) {"Yellow"} else {"Red"})
Write-Host ""

if ($successRate -ge 80) {
    Write-Host "🎉 EXCELLENT! Most endpoints are working!" -ForegroundColor Green
} elseif ($successRate -ge 60) {
    Write-Host "⚠️  GOOD! Some endpoints need attention." -ForegroundColor Yellow
} else {
    Write-Host "❌ ATTENTION NEEDED! Many endpoints failing." -ForegroundColor Red
}

Write-Host ""
Write-Host "Note: Some 400/404 errors are expected if:" -ForegroundColor Yellow
Write-Host "  - No data exists yet (empty database)" -ForegroundColor Gray
Write-Host "  - DTO validation is strict" -ForegroundColor Gray
Write-Host "  - Related entities don't exist" -ForegroundColor Gray
