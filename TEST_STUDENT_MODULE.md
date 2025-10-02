# 🧪 Student Module Testing Guide

## 📋 Test Credentials
- **Username:** karina
- **Password:** karina
- **Role:** ADMIN (full access)

---

## 🚀 Step-by-Step Testing

### **Step 1: Login and Get JWT Token**

```bash
curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "karina",
    "password": "karina"
  }'
```

**Expected Response:**
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "success",
  "data": {
    "jwtToken": "eyJhbGciOiJIUzM4NCJ9...",
    "refreshToken": {...},
    "user": {
      "username": "karina",
      "roles": [{"name": "ROLE_ADMIN"}]
    }
  }
}
```

**⚠️ IMPORTANT:** Copy the `jwtToken` value from the response. You'll need it for all subsequent requests!

---

### **Step 2: Create a Class First (Prerequisite)**

Before creating students, we need at least one class. If you don't have any classes yet, create one:

```bash
# Replace YOUR_JWT_TOKEN with the token from Step 1
curl -X POST http://localhost:9091/api/v1/classes \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "className": "Class 10",
    "section": "A",
    "classLevel": 10,
    "maxStudents": 40,
    "currentStudents": 0,
    "roomNumber": "101",
    "academicYear": "2024-2025",
    "status": "ACTIVE"
  }'
```

**Note:** If classes endpoint doesn't exist yet, you can manually insert a class in the database or we'll skip this and test other endpoints.

---

### **Step 3: Create Your First Student**

```bash
curl -X POST http://localhost:9091/api/v1/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Rahul",
    "middleName": "Kumar",
    "lastName": "Sharma",
    "admissionNumber": "STU20240001",
    "dateOfBirth": "2010-05-15",
    "gender": "MALE",
    "bloodGroup": "O+",
    "nationality": "Indian",
    "religion": "Hindu",
    "email": "rahul.sharma@student.school.com",
    "phoneNumber": "9876543210",
    "address": "123 MG Road, Apartment 4B",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400001",
    "country": "India",
    "fatherName": "Rajesh Sharma",
    "fatherPhone": "9876543211",
    "fatherOccupation": "Engineer",
    "motherName": "Priya Sharma",
    "motherPhone": "9876543212",
    "motherOccupation": "Doctor",
    "guardianName": "Rajesh Sharma",
    "guardianPhone": "9876543211",
    "guardianRelation": "Father",
    "parentEmail": "rajesh.sharma@email.com",
    "admissionDate": "2024-01-15",
    "classId": 1,
    "section": "A",
    "rollNumber": 1,
    "status": "ACTIVE",
    "medicalConditions": "None",
    "specialNeeds": "None",
    "notes": "Excellent student, good at mathematics",
    "previousSchool": "Delhi Public School",
    "totalFees": 50000.00,
    "feesPaid": 10000.00
  }'
```

**Expected Response:**
```json
{
  "responseStatus": "CREATED",
  "status": "success",
  "message": "success",
  "data": {
    "id": 1,
    "fullName": "Rahul Kumar Sharma",
    "admissionNumber": "STU20240001",
    "age": 14,
    "gender": "MALE",
    "email": "rahul.sharma@student.school.com",
    "className": "Class 10",
    "section": "A",
    "rollNumber": 1,
    "status": "ACTIVE",
    "totalFees": 50000.00,
    "feesPaid": 10000.00,
    "feesBalance": 40000.00,
    "feesPaidPercentage": 20.0,
    "isDeleted": false
  }
}
```

**✅ Test Passed if:** You get a 201 CREATED response with student details

---

### **Step 4: Create More Students for Testing**

```bash
# Student 2
curl -X POST http://localhost:9091/api/v1/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Priya",
    "lastName": "Patel",
    "admissionNumber": "STU20240002",
    "dateOfBirth": "2010-08-20",
    "gender": "FEMALE",
    "bloodGroup": "A+",
    "email": "priya.patel@student.school.com",
    "phoneNumber": "9876543220",
    "fatherName": "Amit Patel",
    "fatherPhone": "9876543221",
    "admissionDate": "2024-01-16",
    "classId": 1,
    "section": "A",
    "rollNumber": 2,
    "totalFees": 50000.00,
    "feesPaid": 50000.00
  }'

# Student 3 (with pending fees)
curl -X POST http://localhost:9091/api/v1/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Amit",
    "lastName": "Verma",
    "admissionNumber": "STU20240003",
    "dateOfBirth": "2010-03-10",
    "gender": "MALE",
    "email": "amit.verma@student.school.com",
    "phoneNumber": "9876543230",
    "fatherName": "Suresh Verma",
    "fatherPhone": "9876543231",
    "admissionDate": "2024-01-17",
    "classId": 1,
    "section": "B",
    "rollNumber": 1,
    "totalFees": 45000.00,
    "feesPaid": 5000.00
  }'
```

---

### **Step 5: Test Get All Students**

```bash
curl -X GET "http://localhost:9091/api/v1/students?page=0&size=10&sortBy=firstName&sortDir=asc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "fullName": "Amit Verma",
        "admissionNumber": "STU20240003"
      },
      {
        "id": 2,
        "fullName": "Priya Patel",
        "admissionNumber": "STU20240002"
      },
      {
        "id": 3,
        "fullName": "Rahul Kumar Sharma",
        "admissionNumber": "STU20240001"
      }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

**✅ Test Passed if:** You see all 3 students in the response

---

### **Step 6: Test Get Student by ID**

```bash
curl -X GET http://localhost:9091/api/v1/students/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** Complete student details for ID 1

---

### **Step 7: Test Get Student by Admission Number**

```bash
curl -X GET http://localhost:9091/api/v1/students/admission/STU20240001 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** Rahul Kumar Sharma's details

---

### **Step 8: Test Search Students**

```bash
# Search by name
curl -X GET "http://localhost:9091/api/v1/students/search?keyword=rahul&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** Should find Rahul Kumar Sharma

```bash
# Search by email
curl -X GET "http://localhost:9091/api/v1/students/search?keyword=priya.patel&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** Should find Priya Patel

---

### **Step 9: Test Get Students with Pending Fees**

```bash
curl -X GET "http://localhost:9091/api/v1/students/pending-fees?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "content": [
      {
        "fullName": "Rahul Kumar Sharma",
        "feesBalance": 40000.00
      },
      {
        "fullName": "Amit Verma",
        "feesBalance": 40000.00
      }
    ]
  }
}
```

**✅ Test Passed if:** Only students with balance > 0 are shown (Rahul and Amit, NOT Priya)

---

### **Step 10: Test Get Students by Class and Section**

```bash
curl -X GET http://localhost:9091/api/v1/students/class/1/section/A \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** Rahul and Priya (both in Class 1, Section A)

```bash
curl -X GET http://localhost:9091/api/v1/students/class/1/section/B \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** Only Amit (in Class 1, Section B)

---

### **Step 11: Test Get Students by Status**

```bash
curl -X GET "http://localhost:9091/api/v1/students/status/ACTIVE?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** All 3 students (all are ACTIVE)

---

### **Step 12: Test Update Student**

```bash
curl -X PUT http://localhost:9091/api/v1/students/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Rahul",
    "middleName": "Kumar",
    "lastName": "Sharma",
    "admissionNumber": "STU20240001",
    "dateOfBirth": "2010-05-15",
    "gender": "MALE",
    "bloodGroup": "O+",
    "nationality": "Indian",
    "email": "rahul.sharma.updated@student.school.com",
    "phoneNumber": "9876543210",
    "fatherName": "Rajesh Sharma",
    "fatherPhone": "9876543211",
    "admissionDate": "2024-01-15",
    "classId": 1,
    "section": "A",
    "rollNumber": 1,
    "totalFees": 50000.00,
    "feesPaid": 15000.00
  }'
```

**Expected:** Student updated with new email and increased fees paid

---

### **Step 13: Test Student Count by Status**

```bash
curl -X GET http://localhost:9091/api/v1/students/count/status/ACTIVE \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "status": "ACTIVE",
    "count": 3
  }
}
```

---

### **Step 14: Test Check Admission Number Exists**

```bash
# Check existing number
curl -X GET http://localhost:9091/api/v1/students/exists/admission/STU20240001 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** `exists: true`

```bash
# Check non-existing number
curl -X GET http://localhost:9091/api/v1/students/exists/admission/STU20249999 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** `exists: false`

---

### **Step 15: Test Soft Delete**

```bash
curl -X DELETE http://localhost:9091/api/v1/students/3 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "Student soft-deleted successfully"
}
```

**Verify:** Try to get student 3, should still exist but with `isDeleted: true`

---

### **Step 16: Test Restore**

```bash
curl -X PATCH http://localhost:9091/api/v1/students/3/restore \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "Student restored successfully"
}
```

---

### **Step 17: Test Get Students by Admission Date Range**

```bash
curl -X GET "http://localhost:9091/api/v1/students/admission-date-range?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** All students admitted in January 2024

---

### **Step 18: Test Validation (Should Fail)**

```bash
# Try to create student with invalid admission number
curl -X POST http://localhost:9091/api/v1/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Student",
    "admissionNumber": "INVALID123",
    "dateOfBirth": "2010-01-01",
    "gender": "MALE",
    "email": "test@student.com",
    "phoneNumber": "9876543210",
    "fatherName": "Father",
    "fatherPhone": "9876543211",
    "admissionDate": "2024-01-01",
    "classId": 1,
    "section": "A",
    "rollNumber": 99,
    "totalFees": 50000
  }'
```

**Expected Response:**
```json
{
  "responseStatus": "BAD_REQUEST",
  "status": "error",
  "message": "Validation failed",
  "errors": {
    "admissionNumber": "Admission number must start with STU followed by 4-10 digits"
  }
}
```

**✅ Test Passed if:** Validation error is returned

---

## 📊 Test Summary Checklist

Mark each test as you complete it:

- [ ] Step 1: Login successful ✅
- [ ] Step 2: Class created (or skip if exists)
- [ ] Step 3: First student created ✅
- [ ] Step 4: Multiple students created ✅
- [ ] Step 5: Get all students works ✅
- [ ] Step 6: Get student by ID works ✅
- [ ] Step 7: Get by admission number works ✅
- [ ] Step 8: Search functionality works ✅
- [ ] Step 9: Pending fees filter works ✅
- [ ] Step 10: Filter by class/section works ✅
- [ ] Step 11: Filter by status works ✅
- [ ] Step 12: Update student works ✅
- [ ] Step 13: Count by status works ✅
- [ ] Step 14: Existence check works ✅
- [ ] Step 15: Soft delete works ✅
- [ ] Step 16: Restore works ✅
- [ ] Step 17: Date range filter works ✅
- [ ] Step 18: Validation works ✅

---

## 🐛 Troubleshooting

### **Issue: 401 Unauthorized**
- **Cause:** JWT token expired or invalid
- **Solution:** Login again and get a new token

### **Issue: 403 Forbidden**
- **Cause:** User doesn't have required role
- **Solution:** Verify karina has ROLE_ADMIN

### **Issue: 404 Not Found (Class not found)**
- **Cause:** Class ID doesn't exist
- **Solution:** Create a class first or use existing class ID

### **Issue: 409 Conflict (Admission number exists)**
- **Cause:** Trying to use duplicate admission number
- **Solution:** Use unique admission numbers

### **Issue: 400 Bad Request (Validation)**
- **Cause:** Invalid data format
- **Solution:** Check validation rules (phone: 10 digits, email format, etc.)

---

## 📝 Expected Results Summary

After completing all tests, you should have:

✅ **3 Students Created:**
1. Rahul Kumar Sharma (STU20240001) - ₹40,000 pending
2. Priya Patel (STU20240002) - Fully paid
3. Amit Verma (STU20240003) - ₹40,000 pending

✅ **All CRUD Operations Working:**
- Create ✅
- Read (by ID, admission number, search) ✅
- Update ✅
- Delete (soft + restore) ✅

✅ **All Filters Working:**
- By class/section ✅
- By status ✅
- By pending fees ✅
- By date range ✅
- Search ✅

✅ **Validation Working:**
- Admission number format ✅
- Email format ✅
- Phone number ✅
- Required fields ✅

---

## 🎉 Success Criteria

**ALL TESTS PASSED if:**
1. You can login with karina/karina ✅
2. You can create students ✅
3. You can retrieve students (multiple ways) ✅
4. You can update students ✅
5. You can search and filter ✅
6. You can soft delete and restore ✅
7. Validation errors are caught ✅

---

**Happy Testing! 🚀**

If you encounter any issues, let me know the exact error message and I'll help you fix it!

