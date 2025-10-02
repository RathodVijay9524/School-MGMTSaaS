# ⚡ Quick Test Guide - Student Management API

## 🎯 Quick Start (3 Steps)

### **Step 1: Start Your Application**
```bash
cd D:\Live Project -2025-Jul\Deployement\SchoolManagments\School-Managments
./gradlew bootRun
```

Wait for the message: **"Started UserMasterApplication"**

---

### **Step 2: Run PowerShell Test Script**

Open PowerShell in your project directory and run:

```powershell
.\test-student-api.ps1
```

This will automatically:
- ✅ Login with karina/karina
- ✅ Create 2 test students
- ✅ Test all major endpoints
- ✅ Show results

**Expected Output:**
```
=====================================
Student Management API Test Script
=====================================

Step 1: Logging in as karina...
✅ Login Successful!

Step 2: Creating first student (Rahul Kumar Sharma)...
✅ Student Created Successfully!
Student ID: 1
Full Name: Rahul Kumar Sharma
Fees Balance: ₹40000

...

🎉 All tests completed!
```

---

### **Step 3: Manual Testing (Optional)**

If the PowerShell script works, you can also test manually:

#### **1. Login:**
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:9091/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body '{"usernameOrEmail": "karina", "password": "karina"}'

$token = $response.data.jwtToken
Write-Host "Token: $token"
```

#### **2. Create Student:**
```powershell
$body = @{
    firstName = "Test"
    lastName = "Student"
    admissionNumber = "STU20240999"
    dateOfBirth = "2010-01-01"
    gender = "MALE"
    email = "test.student@school.com"
    phoneNumber = "9999999999"
    fatherName = "Test Father"
    fatherPhone = "9999999998"
    admissionDate = "2024-01-01"
    classId = 1
    section = "A"
    rollNumber = 99
    totalFees = 50000
    feesPaid = 10000
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students" `
    -Method Post `
    -ContentType "application/json" `
    -Headers @{Authorization = "Bearer $token"} `
    -Body $body
```

#### **3. Get All Students:**
```powershell
Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students?page=0&size=10" `
    -Method Get `
    -Headers @{Authorization = "Bearer $token"}
```

#### **4. Search Students:**
```powershell
Invoke-RestMethod -Uri "http://localhost:9091/api/v1/students/search?keyword=test" `
    -Method Get `
    -Headers @{Authorization = "Bearer $token"}
```

---

## 🔥 Quick API Reference

After logging in and getting your token, you can test these endpoints:

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/students` | Create student |
| `GET` | `/api/v1/students` | Get all students |
| `GET` | `/api/v1/students/{id}` | Get student by ID |
| `GET` | `/api/v1/students/search?keyword={q}` | Search students |
| `GET` | `/api/v1/students/pending-fees` | Students with fees due |
| `PUT` | `/api/v1/students/{id}` | Update student |
| `DELETE` | `/api/v1/students/{id}` | Soft delete |

---

## 🐛 Common Issues

### **Issue: Application won't start**
```
Error: Port 9091 already in use
```
**Solution:** Kill the existing process:
```powershell
Get-Process -Name java | Stop-Process -Force
```

### **Issue: 401 Unauthorized**
```json
{"status": "error", "message": "Unauthorized"}
```
**Solution:** 
1. Check if you're logged in
2. Get a fresh token
3. Make sure token is in Authorization header

### **Issue: 404 Class not found**
```json
{"message": "Class not found with ID: 1"}
```
**Solution:** First create a class or use an existing class ID from database

### **Issue: 409 Admission number exists**
```json
{"message": "Admission number already exists: STU20240001"}
```
**Solution:** Use a different admission number (e.g., STU20240999)

---

## 📊 What to Check

After running tests, verify these:

✅ **Login Works:**
- Can login with karina/karina
- Receives JWT token
- Token has ROLE_ADMIN

✅ **Create Works:**
- Can create students with valid data
- Validation catches invalid data
- Duplicate admission numbers rejected

✅ **Read Works:**
- Can get all students (paginated)
- Can get student by ID
- Can get by admission number
- Can search by keyword

✅ **Filter Works:**
- Can filter by class/section
- Can filter by status
- Can filter by pending fees
- Can filter by date range

✅ **Update Works:**
- Can update student details
- Validation works on update

✅ **Delete Works:**
- Can soft delete students
- Can restore deleted students

✅ **Computed Fields:**
- Age calculated correctly from DOB
- Fees balance = total - paid
- Days in school calculated
- Full name constructed properly

---

## 🎯 Success Criteria

**ALL WORKING if you see:**

1. ✅ Login returns JWT token
2. ✅ Can create students without errors
3. ✅ Can retrieve created students
4. ✅ Search returns correct results
5. ✅ Filters work as expected
6. ✅ Validation catches bad data
7. ✅ Computed fields are correct

---

## 📝 Test Data Created

After running the PowerShell script, you'll have:

**Student 1: Rahul Kumar Sharma**
- Admission: STU20240001
- Class: 1, Section: A
- Fees: ₹50,000 (Paid: ₹10,000, Balance: ₹40,000)

**Student 2: Priya Patel**
- Admission: STU20240002
- Class: 1, Section: A
- Fees: ₹50,000 (Fully paid)

---

## 🚀 Next Steps

Once Student module is tested and working:

1. **Test with Postman/Thunder Client** for better visualization
2. **Create more test students** with different scenarios
3. **Test edge cases** (invalid data, missing fields, etc.)
4. **Test pagination** with large datasets
5. **Move to Teacher module** implementation

---

## 📞 Need Help?

If something doesn't work:

1. **Check application logs** in the console
2. **Check the error message** in the response
3. **Verify database connection**
4. **Ensure all entities are properly saved**
5. **Share the error message** for debugging

---

**Happy Testing! 🎉**

The Student Management module is ready to test. Run the PowerShell script and see the magic happen! ✨

