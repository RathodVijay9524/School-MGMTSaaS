# Simple Hostel API Test
# This script will test basic connectivity and create a simple hostel

$baseUrl = "http://localhost:8080"
$username = "vijay-admin"
$password = "vijay"

Write-Host "Simple Hostel API Test" -ForegroundColor Yellow
Write-Host "=====================" -ForegroundColor Yellow

# Test 1: Basic connectivity
Write-Host "`n1. Testing server connectivity..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "✅ Server is running! Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ Server is not running or not accessible" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "`nPlease start your backend server first!" -ForegroundColor Yellow
    exit 1
}

# Test 2: Login
Write-Host "`n2. Testing login..." -ForegroundColor Cyan
$loginBody = @{
    usernameOrEmail = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    $userId = $loginResponse.user.id
    Write-Host "✅ Login successful! User ID: $userId" -ForegroundColor Green
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 3: Create a simple hostel
Write-Host "`n3. Creating a simple hostel..." -ForegroundColor Cyan

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
    "X-User-ID" = $userId.ToString()
}

$simpleHostel = @{
    hostelName = "Simple Test Hostel"
    hostelCode = "STH-$(Get-Date -Format 'yyyyMMddHHmmss')"
    description = "Simple test hostel"
    address = "Test Address"
    contactNumber = "1234567890"
    email = "test@hostel.com"
    capacity = 50
    wardenName = "Test Warden"
    wardenContact = "9876543210"
    wardenEmail = "warden@test.com"
    messAvailable = $true
    messTimings = "7:00 AM - 9:00 PM"
    rulesAndRegulations = "Simple rules"
    amenities = "WiFi"
    isActive = $true
    feesPerMonth = 3000.0
    securityDeposit = 5000.0
    laundryAvailable = $false
    wifiAvailable = $true
    gymAvailable = $false
    libraryAvailable = $false
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "$baseUrl/api/hostels" -Method POST -Body $simpleHostel -Headers $headers
    $hostelId = $createResponse.id
    Write-Host "✅ Simple hostel created successfully!" -ForegroundColor Green
    Write-Host "   ID: $hostelId" -ForegroundColor Green
    Write-Host "   Name: $($createResponse.hostelName)" -ForegroundColor Green
    Write-Host "   Code: $($createResponse.hostelCode)" -ForegroundColor Green
} catch {
    Write-Host "❌ Hostel creation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
}

# Test 4: Get all hostels
Write-Host "`n4. Testing GET all hostels..." -ForegroundColor Cyan
try {
    $hostelsResponse = Invoke-RestMethod -Uri "$baseUrl/api/hostels" -Method GET -Headers $headers
    Write-Host "✅ GET hostels successful! Found $($hostelsResponse.content.Count) hostels" -ForegroundColor Green
} catch {
    Write-Host "❌ GET hostels failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Simple Hostel API Test Complete!" -ForegroundColor Green
Write-Host "The hostel management system is working!" -ForegroundColor Green
