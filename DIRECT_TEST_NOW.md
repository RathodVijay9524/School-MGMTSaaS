# 🧪 Direct Testing Guide - Use This Now!

## ✅ Application is Running - Let's Test!

Since your application is already running, **copy and paste these commands** in a **NEW PowerShell window**:

---

## 🔐 Step 1: Login as karina

```powershell
$response = Invoke-RestMethod -Uri "http://localhost:9091/api/auth/login" -Method Post -ContentType "application/json" -Body '{
  "usernameOrEmail": "karina",
  "password": "karina"
}'

$token = $response.data.jwtToken
Write-Host "✅ Login Successful!" -ForegroundColor Green
Write-Host "Username: $($response.data.user.username)" -ForegroundColor Cyan
Write-Host "Roles: $($response.data.user.roles.name)" -ForegroundColor Cyan
Write-Host "Token saved in variable: `$token" -ForegroundColor Yellow
```

**Expected:** You should see "Login Successful!" and your token

---

## 📝 Step 2: Create First Class (Required)

Before creating students, insert a class in your database:

```sql
-- Connect to your MySQL database and run:
USE user_master;

INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, room_number, academic_year, status, is_deleted) 
VALUES (1, 'Class 10', 'A', 10, 40, 0, '101', '2024-2025', 'ACTIVE', false);
```

**OR if table doesn't exist yet, it will be auto-created on next app restart.**

---

## 👨‍🎓 Step 3: Create Your First Student

```powershell
$studentBody = @{
    firstName = "Rahul"
    lastName = "Sharma"
    admissionNumber = "STU20240001"
    dateOfBirth = "2010-05-15"
    gender = "MALE"
    email = "rahul.sharma@student.com"
    phoneNumber = "9876543210"
    fatherName = "Rajesh Sharma"
    fatherPhone = "9876543211"
    admissionDate = "2024-01-15"
    classId = 1
    section = "A"
    rollNumber = 1
    totalFees = 50000
    feesPaid = 10000
} | ConvertTo-Json

$result = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students" `
    -Method Post `
    -ContentType "application/json" `
    -Headers @{Authorization = "Bearer $token"} `
    -Body $studentBody

Write-Host "✅ Student Created!" -ForegroundColor Green
Write-Host "ID: $($result.data.id)" -ForegroundColor Cyan
Write-Host "Name: $($result.data.fullName)" -ForegroundColor Cyan
Write-Host "Admission Number: $($result.data.admissionNumber)" -ForegroundColor Cyan
Write-Host "Fees Balance: ₹$($result.data.feesBalance)" -ForegroundColor Yellow
```

---

## 📋 Step 4: Get All Students

```powershell
$students = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students?page=0&size=10" `
    -Method Get `
    -Headers @{Authorization = "Bearer $token"}

Write-Host "✅ Total Students: $($students.data.totalElements)" -ForegroundColor Green
$students.data.content | ForEach-Object {
    Write-Host "  - $($_.fullName) ($($_.admissionNumber))" -ForegroundColor Cyan
}
```

---

## 🔍 Step 5: Search Students

```powershell
$searchResults = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students/search?keyword=rahul" `
    -Method Get `
    -Headers @{Authorization = "Bearer $token"}

Write-Host "✅ Found: $($searchResults.data.totalElements) student(s)" -ForegroundColor Green
$searchResults.data.content | ForEach-Object {
    Write-Host "  - $($_.fullName)" -ForegroundColor Cyan
}
```

---

## 💰 Step 6: Get Students with Pending Fees

```powershell
$pendingFees = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students/pending-fees" `
    -Method Get `
    -Headers @{Authorization = "Bearer $token"}

Write-Host "✅ Students with Pending Fees: $($pendingFees.data.totalElements)" -ForegroundColor Green
$pendingFees.data.content | ForEach-Object {
    Write-Host "  - $($_.fullName): ₹$($_.feesBalance) pending" -ForegroundColor Yellow
}
```

---

## 📊 Step 7: Get Count

```powershell
$count = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students/count/status/ACTIVE" `
    -Method Get `
    -Headers @{Authorization = "Bearer $token"}

Write-Host "✅ Total Active Students: $($count.data.count)" -ForegroundColor Green
```

---

## 🎯 Multi-Tenancy Test

### **Create Another User (Business Owner 2):**

```powershell
# 1. Logout / Clear token
# 2. Login as different user

$response2 = Invoke-RestMethod -Uri "http://localhost:9091/api/auth/login" -Method Post -ContentType "application/json" -Body '{
  "usernameOrEmail": "otheruser",
  "password": "password"
}'

$token2 = $response2.data.jwtToken

# 3. Create student with second user
$studentBody2 = @{
    firstName = "Amit"
    lastName = "Verma"
    admissionNumber = "STU20240099"
    dateOfBirth = "2011-03-10"
    gender = "MALE"
    email = "amit.verma@student.com"
    phoneNumber = "9876543299"
    fatherName = "Suresh Verma"
    fatherPhone = "9876543298"
    admissionDate = "2024-01-20"
    classId = 1
    section = "B"
    rollNumber = 1
    totalFees = 45000
    feesPaid = 5000
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students" `
    -Method Post `
    -ContentType "application/json" `
    -Headers @{Authorization = "Bearer $token2"} `
    -Body $studentBody2

# 4. Get students with second user (should see ONLY their students)
Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students" `
    -Method Get `
    -Headers @{Authorization = "Bearer $token2"}
```

**Result:** Second user sees ONLY their own students, NOT karina's! ✅

---

## 🐛 If You Get Errors:

### **Error: Class not found with ID: 1**
**Solution:** Create class in database first:
```sql
INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, status, is_deleted) 
VALUES (1, 'Class 10', 'A', 10, 40, 0, 'ACTIVE', false);
```

### **Error: 401 Unauthorized**
**Solution:** Login again to get fresh token

### **Error: 500 Internal Server Error**
**Solution:** Check application console logs for the exact error

---

## ✅ Success Criteria

**Multi-Tenant System Working if:**

1. ✅ karina can login
2. ✅ karina can create students
3. ✅ karina can see ONLY her own students
4. ✅ Another user can create students
5. ✅ Another user sees ONLY their own students
6. ✅ Complete data isolation between users

---

**Just copy-paste the commands and run them! 🚀**

All commands use `$token` variable, so make sure you run Step 1 first!

