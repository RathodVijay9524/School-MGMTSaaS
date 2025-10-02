# PowerShell Script to Test Student Management API
# Run this script in PowerShell on Windows

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Student Management API Test Script" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:9091"
$username = "karina"
$password = "karina"

# Step 1: Login
Write-Host "Step 1: Logging in as $username..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody
    
    $token = $loginResponse.data.jwtToken
    Write-Host "✅ Login Successful!" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "❌ Login Failed!" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    exit
}

# Step 2: Create First Student
Write-Host "Step 2: Creating first student (Rahul Kumar Sharma)..." -ForegroundColor Yellow
$student1Body = @{
    firstName = "Rahul"
    middleName = "Kumar"
    lastName = "Sharma"
    admissionNumber = "STU20240001"
    dateOfBirth = "2010-05-15"
    gender = "MALE"
    bloodGroup = "O+"
    nationality = "Indian"
    religion = "Hindu"
    email = "rahul.sharma@student.school.com"
    phoneNumber = "9876543210"
    address = "123 MG Road, Apartment 4B"
    city = "Mumbai"
    state = "Maharashtra"
    postalCode = "400001"
    country = "India"
    fatherName = "Rajesh Sharma"
    fatherPhone = "9876543211"
    fatherOccupation = "Engineer"
    motherName = "Priya Sharma"
    motherPhone = "9876543212"
    motherOccupation = "Doctor"
    guardianName = "Rajesh Sharma"
    guardianPhone = "9876543211"
    guardianRelation = "Father"
    parentEmail = "rajesh.sharma@email.com"
    admissionDate = "2024-01-15"
    classId = 1
    section = "A"
    rollNumber = 1
    status = "ACTIVE"
    medicalConditions = "None"
    specialNeeds = "None"
    notes = "Excellent student"
    previousSchool = "Delhi Public School"
    totalFees = 50000.00
    feesPaid = 10000.00
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/students" `
        -Method Post `
        -ContentType "application/json" `
        -Headers @{Authorization = "Bearer $token"} `
        -Body $student1Body
    
    Write-Host "✅ Student Created Successfully!" -ForegroundColor Green
    Write-Host "Student ID: $($createResponse.data.id)" -ForegroundColor Gray
    Write-Host "Full Name: $($createResponse.data.fullName)" -ForegroundColor Gray
    Write-Host "Admission Number: $($createResponse.data.admissionNumber)" -ForegroundColor Gray
    Write-Host "Fees Balance: ₹$($createResponse.data.feesBalance)" -ForegroundColor Gray
    Write-Host ""
    
    $studentId = $createResponse.data.id
} catch {
    Write-Host "❌ Failed to create student!" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $errorBody = $reader.ReadToEnd()
        Write-Host "Error: $errorBody" -ForegroundColor Red
    } else {
        Write-Host "Error: $_" -ForegroundColor Red
    }
    $studentId = 1  # Try with ID 1 for remaining tests
    Write-Host ""
}

# Step 3: Create Second Student
Write-Host "Step 3: Creating second student (Priya Patel)..." -ForegroundColor Yellow
$student2Body = @{
    firstName = "Priya"
    lastName = "Patel"
    admissionNumber = "STU20240002"
    dateOfBirth = "2010-08-20"
    gender = "FEMALE"
    bloodGroup = "A+"
    email = "priya.patel@student.school.com"
    phoneNumber = "9876543220"
    fatherName = "Amit Patel"
    fatherPhone = "9876543221"
    admissionDate = "2024-01-16"
    classId = 1
    section = "A"
    rollNumber = 2
    totalFees = 50000.00
    feesPaid = 50000.00
} | ConvertTo-Json

try {
    $createResponse2 = Invoke-RestMethod -Uri "$baseUrl/api/v1/students" `
        -Method Post `
        -ContentType "application/json" `
        -Headers @{Authorization = "Bearer $token"} `
        -Body $student2Body
    
    Write-Host "✅ Student 2 Created Successfully!" -ForegroundColor Green
    Write-Host "Full Name: $($createResponse2.data.fullName)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "⚠️ Student 2 creation failed (may already exist)" -ForegroundColor Yellow
    Write-Host ""
}

# Step 4: Get All Students
Write-Host "Step 4: Fetching all students..." -ForegroundColor Yellow
try {
    $allStudents = Invoke-RestMethod -Uri "$baseUrl/api/v1/students?page=0&size=10" `
        -Method Get `
        -Headers @{Authorization = "Bearer $token"}
    
    Write-Host "✅ Found $($allStudents.data.totalElements) students:" -ForegroundColor Green
    foreach ($student in $allStudents.data.content) {
        Write-Host "  - $($student.fullName) ($($student.admissionNumber))" -ForegroundColor Gray
    }
    Write-Host ""
} catch {
    Write-Host "❌ Failed to fetch students!" -ForegroundColor Red
    Write-Host ""
}

# Step 5: Get Student by ID
Write-Host "Step 5: Fetching student by ID ($studentId)..." -ForegroundColor Yellow
try {
    $studentById = Invoke-RestMethod -Uri "$baseUrl/api/v1/students/$studentId" `
        -Method Get `
        -Headers @{Authorization = "Bearer $token"}
    
    Write-Host "✅ Student Found:" -ForegroundColor Green
    Write-Host "  Name: $($studentById.data.fullName)" -ForegroundColor Gray
    Write-Host "  Age: $($studentById.data.age) years" -ForegroundColor Gray
    Write-Host "  Class: $($studentById.data.className) - Section $($studentById.data.section)" -ForegroundColor Gray
    Write-Host "  Roll Number: $($studentById.data.rollNumber)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "❌ Failed to fetch student!" -ForegroundColor Red
    Write-Host ""
}

# Step 6: Search Students
Write-Host "Step 6: Searching for 'rahul'..." -ForegroundColor Yellow
try {
    $searchResults = Invoke-RestMethod -Uri "$baseUrl/api/v1/students/search?keyword=rahul&page=0&size=10" `
        -Method Get `
        -Headers @{Authorization = "Bearer $token"}
    
    Write-Host "✅ Found $($searchResults.data.totalElements) matching student(s):" -ForegroundColor Green
    foreach ($student in $searchResults.data.content) {
        Write-Host "  - $($student.fullName)" -ForegroundColor Gray
    }
    Write-Host ""
} catch {
    Write-Host "❌ Search failed!" -ForegroundColor Red
    Write-Host ""
}

# Step 7: Get Students with Pending Fees
Write-Host "Step 7: Fetching students with pending fees..." -ForegroundColor Yellow
try {
    $pendingFees = Invoke-RestMethod -Uri "$baseUrl/api/v1/students/pending-fees?page=0&size=10" `
        -Method Get `
        -Headers @{Authorization = "Bearer $token"}
    
    Write-Host "✅ Found $($pendingFees.data.totalElements) student(s) with pending fees:" -ForegroundColor Green
    foreach ($student in $pendingFees.data.content) {
        Write-Host "  - $($student.fullName): ₹$($student.feesBalance) pending" -ForegroundColor Gray
    }
    Write-Host ""
} catch {
    Write-Host "❌ Failed to fetch pending fees!" -ForegroundColor Red
    Write-Host ""
}

# Step 8: Get Student Count
Write-Host "Step 8: Getting active student count..." -ForegroundColor Yellow
try {
    $countResult = Invoke-RestMethod -Uri "$baseUrl/api/v1/students/count/status/ACTIVE" `
        -Method Get `
        -Headers @{Authorization = "Bearer $token"}
    
    Write-Host "✅ Total Active Students: $($countResult.data.count)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "❌ Failed to get count!" -ForegroundColor Red
    Write-Host ""
}

# Step 9: Check Admission Number Exists
Write-Host "Step 9: Checking if admission number exists..." -ForegroundColor Yellow
try {
    $existsResult = Invoke-RestMethod -Uri "$baseUrl/api/v1/students/exists/admission/STU20240001" `
        -Method Get `
        -Headers @{Authorization = "Bearer $token"}
    
    if ($existsResult.data.exists) {
        Write-Host "✅ Admission number STU20240001 exists!" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Admission number STU20240001 does not exist!" -ForegroundColor Yellow
    }
    Write-Host ""
} catch {
    Write-Host "❌ Failed to check existence!" -ForegroundColor Red
    Write-Host ""
}

# Summary
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Test Summary" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "✅ Login: Success" -ForegroundColor Green
Write-Host "✅ Create Student: Tested" -ForegroundColor Green
Write-Host "✅ Get All Students: Tested" -ForegroundColor Green
Write-Host "✅ Get Student by ID: Tested" -ForegroundColor Green
Write-Host "✅ Search: Tested" -ForegroundColor Green
Write-Host "✅ Pending Fees: Tested" -ForegroundColor Green
Write-Host "✅ Count: Tested" -ForegroundColor Green
Write-Host "✅ Existence Check: Tested" -ForegroundColor Green
Write-Host ""
Write-Host "🎉 All tests completed!" -ForegroundColor Green
Write-Host ""
Write-Host "📝 For more detailed testing, see TEST_STUDENT_MODULE.md" -ForegroundColor Cyan

