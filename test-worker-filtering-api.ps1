# Test Worker Filtering API Endpoints
Write-Host "Testing Worker Filtering API Endpoints" -ForegroundColor Cyan

# Configuration
$baseUrl = "http://localhost:9091"
$username = "ui"
$password = "ui"

# Step 1: Login
Write-Host "Logging in as owner..." -ForegroundColor Yellow
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
        Write-Host "Login failed - no token received" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Test endpoints
Write-Host "Testing Worker Filtering Endpoints..." -ForegroundColor Yellow

$endpoints = @(
    @{
        Name = "All Workers"
        Url = "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter"
    },
    @{
        Name = "Active Workers"
        Url = "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isActive=true"
    },
    @{
        Name = "Deleted Workers"
        Url = "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isDeleted=true"
    },
    @{
        Name = "Expired Workers"
        Url = "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isDeleted=false&isActive=false"
    }
)

foreach ($endpoint in $endpoints) {
    Write-Host "Testing: $($endpoint.Name)" -ForegroundColor Cyan
    Write-Host "URL: $($endpoint.Url)" -ForegroundColor Gray
    
    try {
        $response = Invoke-RestMethod -Uri $endpoint.Url -Method Get -Headers $headers
        
        $count = 0
        if ($response.content) {
            $count = $response.content.Count
        } elseif ($response.data) {
            $count = $response.data.Count
        } elseif ($response -is [array]) {
            $count = $response.Count
        }
        
        Write-Host "Success! Found $count workers" -ForegroundColor Green
    } catch {
        Write-Host "Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "Worker API Testing Complete!" -ForegroundColor Green
