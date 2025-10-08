# Simple Test for Student & Teacher Operations
Write-Host "Testing Student & Teacher Operations" -ForegroundColor Cyan

$baseUrl = "http://localhost:9091"
$username = "ui"
$password = "ui"

# Login
Write-Host "Logging in..." -ForegroundColor Yellow
$loginBody = @{
    username = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    
    if ($loginResponse.jwtToken) {
        $token = $loginResponse.jwtToken
        $ownerId = $loginResponse.user.id
        Write-Host "Login successful! Owner ID: $ownerId" -ForegroundColor Green
        
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
    } else {
        Write-Host "Login failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 1: Get all workers
Write-Host "Test 1: Get all workers" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter" -Method Get -Headers $headers
    $workerCount = $response.data.content.Count
    Write-Host "Found $workerCount workers" -ForegroundColor Green
} catch {
    Write-Host "Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Get active workers
Write-Host "Test 2: Get active workers" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isActive=true" -Method Get -Headers $headers
    $activeCount = $response.data.content.Count
    Write-Host "Found $activeCount active workers" -ForegroundColor Green
} catch {
    Write-Host "Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Get deleted workers
Write-Host "Test 3: Get deleted workers" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isDeleted=true" -Method Get -Headers $headers
    $deletedCount = $response.data.content.Count
    Write-Host "Found $deletedCount deleted workers" -ForegroundColor Green
} catch {
    Write-Host "Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Testing complete!" -ForegroundColor Green
