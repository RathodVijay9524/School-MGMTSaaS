# Test Student & Teacher Management Operations
Write-Host "🧪 Testing Student & Teacher Management Operations" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

# Configuration
$baseUrl = "http://localhost:9091"
$username = "ui"  # Owner account
$password = "ui"

# Step 1: Login and get JWT token
Write-Host "`n1️⃣ Logging in as owner..." -ForegroundColor Yellow
$loginBody = @{
    username = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    
    if ($loginResponse.jwtToken) {
        $token = $loginResponse.jwtToken
        $ownerId = $loginResponse.user.id
        Write-Host "✅ Login successful! Owner ID: $ownerId" -ForegroundColor Green
        
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
    } else {
        Write-Host "❌ Login failed - no token received" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Test Worker API Endpoints
Write-Host "`n2️⃣ Testing Worker API Endpoints..." -ForegroundColor Yellow

$testResults = @()

# Test 1: Get all workers
Write-Host "`n📋 Test 1: Get all workers" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter" -Method Get -Headers $headers
    $workerCount = $response.data?.content?.Count ?? 0
    Write-Host "✅ Found $workerCount workers" -ForegroundColor Green
    $testResults += @{Test = "Get All Workers"; Status = "✅ Success"; Count = $workerCount}
    
    # Get first worker for testing operations
    $firstWorker = $response.data?.content?[0]
    if ($firstWorker) {
        Write-Host "📝 First worker: $($firstWorker.name) (ID: $($firstWorker.id))" -ForegroundColor Gray
        
        # Test 2: Get worker by ID
        Write-Host "`n📋 Test 2: Get worker by ID" -ForegroundColor Cyan
        try {
            $workerResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/$($firstWorker.id)" -Method Get -Headers $headers
            Write-Host "✅ Worker details retrieved" -ForegroundColor Green
            $testResults += @{Test = "Get Worker by ID"; Status = "✅ Success"; Details = "Worker: $($workerResponse.data.name)"}
        } catch {
            Write-Host "❌ Failed to get worker by ID: $($_.Exception.Message)" -ForegroundColor Red
            $testResults += @{Test = "Get Worker by ID"; Status = "❌ Failed"; Error = $_.Exception.Message}
        }
        
        # Test 3: Update worker status (toggle)
        Write-Host "`n📋 Test 3: Update worker status" -ForegroundColor Cyan
        $currentStatus = $firstWorker.accountStatus?.isActive
        $newStatus = -not $currentStatus
        
        try {
            $statusResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/$($firstWorker.id)/status?isActive=$newStatus" -Method PATCH -Headers $headers
            Write-Host "✅ Status updated from $currentStatus to $newStatus" -ForegroundColor Green
            
            # Revert the status back
            $revertResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/$($firstWorker.id)/status?isActive=$currentStatus" -Method PATCH -Headers $headers
            Write-Host "✅ Status reverted back to $currentStatus" -ForegroundColor Green
            $testResults += @{Test = "Update Worker Status"; Status = "✅ Success"; Details = "Toggled and reverted"}
        } catch {
            Write-Host "❌ Failed to update worker status: $($_.Exception.Message)" -ForegroundColor Red
            $testResults += @{Test = "Update Worker Status"; Status = "❌ Failed"; Error = $_.Exception.Message}
        }
        
        # Test 4: Soft delete worker
        Write-Host "`n📋 Test 4: Soft delete worker" -ForegroundColor Cyan
        try {
            $deleteResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/$($firstWorker.id)" -Method DELETE -Headers $headers
            Write-Host "✅ Worker soft deleted" -ForegroundColor Green
            
            # Test 5: Restore worker
            Write-Host "`n📋 Test 5: Restore worker" -ForegroundColor Cyan
            $restoreResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/workers/$($firstWorker.id)/restore" -Method PATCH -Headers $headers
            Write-Host "✅ Worker restored" -ForegroundColor Green
            $testResults += @{Test = "Soft Delete & Restore"; Status = "✅ Success"; Details = "Deleted and restored"}
        } catch {
            Write-Host "❌ Failed soft delete/restore: $($_.Exception.Message)" -ForegroundColor Red
            $testResults += @{Test = "Soft Delete & Restore"; Status = "❌ Failed"; Error = $_.Exception.Message}
        }
        
    } else {
        Write-Host "⚠️ No workers found to test operations" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "❌ Failed to get workers: $($_.Exception.Message)" -ForegroundColor Red
    $testResults += @{Test = "Get All Workers"; Status = "❌ Failed"; Error = $_.Exception.Message}
}

# Step 3: Test filtering endpoints
Write-Host "`n3️⃣ Testing Filtering Endpoints..." -ForegroundColor Yellow

$filterTests = @(
    @{Name = "Active Workers"; Url = "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isActive=true"},
    @{Name = "Deleted Workers"; Url = "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isDeleted=true"},
    @{Name = "Expired Workers"; Url = "$baseUrl/api/v1/workers/superuser/$ownerId/advanced-filter?isDeleted=false&isActive=false"}
)

foreach ($filterTest in $filterTests) {
    Write-Host "`n🔍 Testing: $($filterTest.Name)" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri $filterTest.Url -Method Get -Headers $headers
        $count = $response.data?.content?.Count ?? 0
        Write-Host "✅ Found $count workers" -ForegroundColor Green
        $testResults += @{Test = $filterTest.Name; Status = "✅ Success"; Count = $count}
    } catch {
        Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
        $testResults += @{Test = $filterTest.Name; Status = "❌ Failed"; Error = $_.Exception.Message}
    }
}

# Step 4: Summary Report
Write-Host "`n📊 TEST SUMMARY REPORT" -ForegroundColor Magenta
Write-Host "=====================" -ForegroundColor Magenta

$successCount = ($testResults | Where-Object { $_.Status -like "*Success*" }).Count
$totalCount = $testResults.Count

Write-Host "`nOverall Results:" -ForegroundColor White
Write-Host "✅ Successful: $successCount/$totalCount" -ForegroundColor Green
Write-Host "❌ Failed: $($totalCount - $successCount)/$totalCount" -ForegroundColor Red

Write-Host "`nDetailed Results:" -ForegroundColor White
foreach ($result in $testResults) {
    $status = $result.Status
    $test = $result.Test
    $details = $result.Details ?? $result.Count ?? ""
    
    if ($status -like "*Success*") {
        Write-Host "✅ $test`: $details" -ForegroundColor Green
    } else {
        Write-Host "❌ $test`: $($result.Error)" -ForegroundColor Red
    }
}

Write-Host "`n🎉 Testing Complete!" -ForegroundColor Green
Write-Host "All worker management operations have been tested." -ForegroundColor White
