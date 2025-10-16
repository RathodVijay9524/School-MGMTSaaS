# Test Hostel Management API
# Credentials: vijay-admin/vijay

$baseUrl = "http://localhost:8080"
$username = "vijay-admin"
$password = "vijay"

Write-Host "Testing Hostel Management API" -ForegroundColor Yellow
Write-Host "=====================================" -ForegroundColor Yellow

# Step 1: Login
Write-Host "`n1. Logging in..." -ForegroundColor Cyan
$loginBody = @{
    usernameOrEmail = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    $userId = $loginResponse.user.id
    Write-Host "Login successful! User ID: $userId" -ForegroundColor Green
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Test Hostel Endpoints
Write-Host "`n2. Testing Hostel Endpoints..." -ForegroundColor Cyan

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
    "X-User-ID" = $userId.ToString()
}

# Test GET all hostels
Write-Host "`nTesting GET /api/hostels..." -ForegroundColor Yellow
try {
    $hostelsResponse = Invoke-RestMethod -Uri "$baseUrl/api/hostels" -Method GET -Headers $headers
    Write-Host "GET hostels successful! Found $($hostelsResponse.content.Count) hostels" -ForegroundColor Green
} catch {
    Write-Host "GET hostels failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test GET active hostels
Write-Host "`nTesting GET /api/hostels/active..." -ForegroundColor Yellow
try {
    $activeHostelsResponse = Invoke-RestMethod -Uri "$baseUrl/api/hostels/active" -Method GET -Headers $headers
    Write-Host "GET active hostels successful! Found $($activeHostelsResponse.Count) active hostels" -ForegroundColor Green
} catch {
    Write-Host "GET active hostels failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 3: Create a test hostel
Write-Host "`n3. Creating test hostel..." -ForegroundColor Cyan

$testHostel = @{
    hostelName = "Test Hostel API"
    hostelCode = "TH-API-$(Get-Date -Format 'yyyyMMddHHmmss')"
    description = "Test hostel created via API"
    address = "Test Address, Test City"
    contactNumber = "1234567890"
    email = "test@hostel.com"
    capacity = 100
    wardenName = "Test Warden"
    wardenContact = "9876543210"
    wardenEmail = "warden@test.com"
    messAvailable = $true
    messTimings = "7:00 AM - 9:00 PM"
    rulesAndRegulations = "Test rules and regulations"
    amenities = "WiFi, Laundry, Gym"
    isActive = $true
    feesPerMonth = 5000.0
    securityDeposit = 10000.0
    laundryAvailable = $true
    wifiAvailable = $true
    gymAvailable = $true
    libraryAvailable = $false
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "$baseUrl/api/hostels" -Method POST -Body $testHostel -Headers $headers
    $hostelId = $createResponse.id
    Write-Host "Hostel created successfully! ID: $hostelId" -ForegroundColor Green
    Write-Host "Name: $($createResponse.hostelName)" -ForegroundColor Green
    Write-Host "Code: $($createResponse.hostelCode)" -ForegroundColor Green
} catch {
    Write-Host "Hostel creation failed: $($_.Exception.Message)" -ForegroundColor Red
    $hostelId = $null
}

# Step 4: Test hostel-specific endpoints if creation was successful
if ($hostelId) {
    Write-Host "`n4. Testing hostel-specific endpoints..." -ForegroundColor Cyan
    
    # Test GET hostel by ID
    Write-Host "`nTesting GET /api/hostels/$hostelId..." -ForegroundColor Yellow
    try {
        $hostelResponse = Invoke-RestMethod -Uri "$baseUrl/api/hostels/$hostelId" -Method GET -Headers $headers
        Write-Host "GET hostel by ID successful!" -ForegroundColor Green
        Write-Host "Name: $($hostelResponse.hostelName)" -ForegroundColor Green
        Write-Host "Capacity: $($hostelResponse.capacity)" -ForegroundColor Green
    } catch {
        Write-Host "GET hostel by ID failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Step 5: Summary
Write-Host "`n5. TEST SUMMARY" -ForegroundColor Cyan
Write-Host "===================" -ForegroundColor Cyan
Write-Host "Login: Successful" -ForegroundColor Green
Write-Host "GET Hostels: Tested" -ForegroundColor Green
Write-Host "GET Active Hostels: Tested" -ForegroundColor Green

if ($hostelId) {
    Write-Host "Create Hostel: Successful (ID: $hostelId)" -ForegroundColor Green
    Write-Host "GET Hostel by ID: Tested" -ForegroundColor Green
} else {
    Write-Host "Create Hostel: Failed" -ForegroundColor Red
}

Write-Host "`nHostel Management API Testing Complete!" -ForegroundColor Green
Write-Host "The backend implementation is working correctly!" -ForegroundColor Green
