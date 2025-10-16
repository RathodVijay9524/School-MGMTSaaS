# Test Bus Management API Endpoints
# Login credentials: vijay-admin/vijay
# Server: http://localhost:9091

Write-Host "🚌 TESTING BUS MANAGEMENT API ENDPOINTS" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green

# Variables
$baseUrl = "http://localhost:9091"
$username = "vijay-admin"
$password = "vijay"
$token = ""

# Function to make API calls
function Invoke-ApiCall {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers = @{},
        [string]$Body = $null
    )
    
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $Headers -Body $Body -ContentType "application/json"
        } else {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $Headers
        }
        return $response
    } catch {
        Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Step 1: Login
Write-Host "`n🔐 STEP 1: LOGIN" -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = $username
    password = $password
} | ConvertTo-Json

$loginResponse = Invoke-ApiCall -Method "POST" -Uri "$baseUrl/api/auth/login" -Body $loginBody

if ($loginResponse -and $loginResponse.data -and $loginResponse.data.jwtToken) {
    $token = $loginResponse.data.jwtToken
    Write-Host "✅ Login successful! Token obtained." -ForegroundColor Green
} else {
    Write-Host "❌ Login failed!" -ForegroundColor Red
    exit 1
}

# Headers for authenticated requests
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Step 2: Test Bus Endpoints
Write-Host "`n🚌 STEP 2: TESTING BUS ENDPOINTS" -ForegroundColor Yellow

# 2.1 Get all buses
Write-Host "`n📋 2.1 GET ALL BUSES" -ForegroundColor Cyan
$allBuses = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses" -Headers $headers
if ($allBuses) {
    Write-Host "✅ Get all buses: SUCCESS" -ForegroundColor Green
    Write-Host "   Found $($allBuses.content.Count) buses" -ForegroundColor White
} else {
    Write-Host "❌ Get all buses: FAILED" -ForegroundColor Red
}

# 2.2 Get active buses
Write-Host "`n📋 2.2 GET ACTIVE BUSES" -ForegroundColor Cyan
$activeBuses = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/active" -Headers $headers
if ($activeBuses) {
    Write-Host "✅ Get active buses: SUCCESS" -ForegroundColor Green
    Write-Host "   Found $($activeBuses.Count) active buses" -ForegroundColor White
} else {
    Write-Host "❌ Get active buses: FAILED" -ForegroundColor Red
}

# 2.3 Get available buses
Write-Host "`n📋 2.3 GET AVAILABLE BUSES" -ForegroundColor Cyan
$availableBuses = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/available" -Headers $headers
if ($availableBuses) {
    Write-Host "✅ Get available buses: SUCCESS" -ForegroundColor Green
    Write-Host "   Found $($availableBuses.Count) available buses" -ForegroundColor White
} else {
    Write-Host "❌ Get available buses: FAILED" -ForegroundColor Red
}

# 2.4 Get bus statistics
Write-Host "`n📋 2.4 GET BUS STATISTICS" -ForegroundColor Cyan
$busStats = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/statistics" -Headers $headers
if ($busStats) {
    Write-Host "✅ Get bus statistics: SUCCESS" -ForegroundColor Green
    Write-Host "   Statistics: $($busStats | ConvertTo-Json -Compress)" -ForegroundColor White
} else {
    Write-Host "❌ Get bus statistics: FAILED" -ForegroundColor Red
}

# 2.5 Create a new bus
Write-Host "`n📋 2.5 CREATE NEW BUS" -ForegroundColor Cyan
$newBus = @{
    busNumber = "BUS-001"
    busName = "School Bus 1"
    registrationNumber = "REG-001"
    make = "Tata"
    model = "Winger"
    yearOfManufacture = 2020
    capacity = 25
    fuelType = "Diesel"
    fuelCapacity = 100.0
    mileage = 12.5
    insuranceNumber = "INS-001"
    insuranceExpiryDate = "2025-12-31"
    fitnessCertificateNumber = "FIT-001"
    fitnessExpiryDate = "2025-12-31"
    lastServiceDate = "2024-01-01"
    nextServiceDate = "2024-07-01"
    purchaseDate = "2020-01-01"
    purchasePrice = 1500000.0
    currentValue = 1200000.0
    depreciationRate = 10.0
    maintenanceCostPerMonth = 5000.0
    fuelCostPerKm = 8.0
    driverSalaryPerMonth = 25000.0
    conductorSalaryPerMonth = 15000.0
    gpsTrackingEnabled = $true
    cctvEnabled = $true
    acAvailable = $true
    wifiAvailable = $false
    emergencyExits = 2
    fireExtinguisher = $true
    firstAidKit = $true
    speedGovernor = $true
    serviceIntervalKm = 10000
    currentMileage = 50000
    isActive = $true
    isAvailable = $true
    isUnderMaintenance = $false
    notes = "New school bus for student transport"
} | ConvertTo-Json

$createdBus = Invoke-ApiCall -Method "POST" -Uri "$baseUrl/api/v1/buses" -Headers $headers -Body $newBus
if ($createdBus) {
    Write-Host "✅ Create new bus: SUCCESS" -ForegroundColor Green
    Write-Host "   Created bus ID: $($createdBus.id)" -ForegroundColor White
    $busId = $createdBus.id
} else {
    Write-Host "❌ Create new bus: FAILED" -ForegroundColor Red
    $busId = 1  # Use a default ID for testing
}

# 2.6 Get bus by ID
Write-Host "`n📋 2.6 GET BUS BY ID" -ForegroundColor Cyan
$busById = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/$busId" -Headers $headers
if ($busById) {
    Write-Host "✅ Get bus by ID: SUCCESS" -ForegroundColor Green
    Write-Host "   Bus: $($busById.busNumber) - $($busById.busName)" -ForegroundColor White
} else {
    Write-Host "❌ Get bus by ID: FAILED" -ForegroundColor Red
}

# 2.7 Search buses
Write-Host "`n📋 2.7 SEARCH BUSES" -ForegroundColor Cyan
$searchResults = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/search?keyword=Tata" -Headers $headers
if ($searchResults) {
    Write-Host "✅ Search buses: SUCCESS" -ForegroundColor Green
    Write-Host "   Found $($searchResults.content.Count) buses matching 'Tata'" -ForegroundColor White
} else {
    Write-Host "❌ Search buses: FAILED" -ForegroundColor Red
}

# 2.8 Get buses by make
Write-Host "`n📋 2.8 GET BUSES BY MAKE" -ForegroundColor Cyan
$busesByMake = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/by-make/Tata" -Headers $headers
if ($busesByMake) {
    Write-Host "✅ Get buses by make: SUCCESS" -ForegroundColor Green
    Write-Host "   Found $($busesByMake.Count) Tata buses" -ForegroundColor White
} else {
    Write-Host "❌ Get buses by make: FAILED" -ForegroundColor Red
}

# 2.9 Get buses by capacity range
Write-Host "`n📋 2.9 GET BUSES BY CAPACITY RANGE" -ForegroundColor Cyan
$busesByCapacity = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/by-capacity?minCapacity=20&maxCapacity=30" -Headers $headers
if ($busesByCapacity) {
    Write-Host "✅ Get buses by capacity range: SUCCESS" -ForegroundColor Green
    Write-Host "   Found $($busesByCapacity.Count) buses with capacity 20-30" -ForegroundColor White
} else {
    Write-Host "❌ Get buses by capacity range: FAILED" -ForegroundColor Red
}

# 2.10 Check if bus number exists
Write-Host "`n📋 2.10 CHECK BUS NUMBER EXISTS" -ForegroundColor Cyan
$busExists = Invoke-ApiCall -Method "GET" -Uri "$baseUrl/api/v1/buses/exists/bus-number/BUS-001" -Headers $headers
if ($busExists -ne $null) {
    Write-Host "✅ Check bus number exists: SUCCESS" -ForegroundColor Green
    Write-Host "   Bus number BUS-001 exists: $busExists" -ForegroundColor White
} else {
    Write-Host "❌ Check bus number exists: FAILED" -ForegroundColor Red
}

Write-Host "`n🎉 BUS API TESTING COMPLETED!" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green
