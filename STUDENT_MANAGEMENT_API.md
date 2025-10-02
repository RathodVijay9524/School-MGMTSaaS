# 🎓 Student Management API Documentation

## 📋 Overview

Complete REST API documentation for Student Management module with all endpoints, request/response examples, and usage guidelines.

**Base URL:** `https://api.codewithvijay.online/api/v1/students`

---

## 🔐 Authentication

All endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer {your_jwt_token}
```

### **Role-Based Access:**
- **ADMIN** / **SUPER_USER**: Full access to all operations
- **TEACHER**: Can view and update students
- **STUDENT**: Can view own profile
- **PARENT**: Can view child's profile

---

## 📚 API Endpoints

### **1. Create Student**

**Endpoint:** `POST /api/v1/students`  
**Authorization:** ADMIN, SUPER_USER  
**Description:** Register a new student in the system

**Request Body:**
```json
{
  "firstName": "John",
  "middleName": "Michael",
  "lastName": "Doe",
  "admissionNumber": "STU20240001",
  "dateOfBirth": "2010-05-15",
  "gender": "MALE",
  "bloodGroup": "O+",
  "nationality": "Indian",
  "religion": "Hindu",
  "email": "john.doe@student.school.com",
  "phoneNumber": "9876543210",
  "address": "123 Main Street, Apartment 4B",
  "city": "Mumbai",
  "state": "Maharashtra",
  "postalCode": "400001",
  "country": "India",
  "fatherName": "Robert Doe",
  "fatherPhone": "9876543211",
  "fatherOccupation": "Engineer",
  "motherName": "Mary Doe",
  "motherPhone": "9876543212",
  "motherOccupation": "Doctor",
  "guardianName": "Robert Doe",
  "guardianPhone": "9876543211",
  "guardianRelation": "Father",
  "parentEmail": "parent.doe@email.com",
  "admissionDate": "2024-01-15",
  "classId": 1,
  "section": "A",
  "rollNumber": 15,
  "status": "ACTIVE",
  "medicalConditions": "None",
  "specialNeeds": "None",
  "notes": "Excellent student",
  "previousSchool": "ABC School",
  "totalFees": 50000.00,
  "feesPaid": 10000.00,
  "userId": 123
}
```

**Response:** `201 Created`
```json
{
  "responseStatus": "CREATED",
  "status": "success",
  "message": "success",
  "data": {
    "id": 1,
    "firstName": "John",
    "middleName": "Michael",
    "lastName": "Doe",
    "fullName": "John Michael Doe",
    "admissionNumber": "STU20240001",
    "dateOfBirth": "2010-05-15",
    "age": 14,
    "gender": "MALE",
    "email": "john.doe@student.school.com",
    "classId": 1,
    "className": "Class 10",
    "section": "A",
    "rollNumber": 15,
    "status": "ACTIVE",
    "totalFees": 50000.00,
    "feesPaid": 10000.00,
    "feesBalance": 40000.00,
    "feesPaidPercentage": 20.0,
    "daysInSchool": 350,
    "isDeleted": false
  }
}
```

---

### **2. Update Student**

**Endpoint:** `PUT /api/v1/students/{id}`  
**Authorization:** ADMIN, SUPER_USER, TEACHER  
**Description:** Update student information

**Request Body:** Same as Create Student  
**Response:** `200 OK` with updated student data

---

### **3. Get Student by ID**

**Endpoint:** `GET /api/v1/students/{id}`  
**Authorization:** ADMIN, SUPER_USER, TEACHER, STUDENT, PARENT  
**Description:** Get detailed student information

**Example:** `GET /api/v1/students/1`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "success",
  "data": {
    "id": 1,
    "fullName": "John Michael Doe",
    "admissionNumber": "STU20240001",
    "email": "john.doe@student.school.com",
    "className": "Class 10",
    "section": "A",
    "rollNumber": 15,
    "status": "ACTIVE",
    "feesBalance": 40000.00
  }
}
```

---

### **4. Get Student by Admission Number**

**Endpoint:** `GET /api/v1/students/admission/{admissionNumber}`  
**Authorization:** ADMIN, SUPER_USER, TEACHER  
**Description:** Find student by admission number

**Example:** `GET /api/v1/students/admission/STU20240001`

**Response:** `200 OK` with student details

---

### **5. Get All Students**

**Endpoint:** `GET /api/v1/students`  
**Authorization:** ADMIN, SUPER_USER, TEACHER  
**Description:** Get paginated list of all students

**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Number of items per page
- `sortBy` (default: firstName) - Field to sort by
- `sortDir` (default: asc) - Sort direction (asc/desc)

**Example:** `GET /api/v1/students?page=0&size=20&sortBy=admissionNumber&sortDir=asc`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "fullName": "John Michael Doe",
        "admissionNumber": "STU20240001",
        "className": "Class 10",
        "section": "A"
      }
    ],
    "totalElements": 150,
    "totalPages": 8,
    "size": 20,
    "number": 0
  }
}
```

---

### **6. Get Students by Class**

**Endpoint:** `GET /api/v1/students/class/{classId}`  
**Authorization:** ADMIN, SUPER_USER, TEACHER  
**Description:** Get all students in a specific class

**Example:** `GET /api/v1/students/class/1?page=0&size=30`

**Response:** `200 OK` with paginated students

---

### **7. Get Students by Class and Section**

**Endpoint:** `GET /api/v1/students/class/{classId}/section/{section}`  
**Authorization:** ADMIN, SUPER_USER, TEACHER  
**Description:** Get students in specific class and section

**Example:** `GET /api/v1/students/class/1/section/A`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "success",
  "data": [
    {
      "id": 1,
      "fullName": "John Doe",
      "rollNumber": 1,
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "fullName": "Jane Smith",
      "rollNumber": 2,
      "status": "ACTIVE"
    }
  ]
}
```

---

### **8. Get Students by Status**

**Endpoint:** `GET /api/v1/students/status/{status}`  
**Authorization:** ADMIN, SUPER_USER, TEACHER  
**Description:** Filter students by status

**Status Values:** `ACTIVE`, `INACTIVE`, `GRADUATED`, `TRANSFERRED`, `SUSPENDED`, `EXPELLED`

**Example:** `GET /api/v1/students/status/ACTIVE?page=0&size=10`

**Response:** `200 OK` with filtered students

---

### **9. Search Students**

**Endpoint:** `GET /api/v1/students/search`  
**Authorization:** ADMIN, SUPER_USER, TEACHER  
**Description:** Search students by keyword (searches in name, email, admission number)

**Query Parameters:**
- `keyword` (required) - Search keyword
- `page` (default: 0)
- `size` (default: 10)

**Example:** `GET /api/v1/students/search?keyword=john&page=0&size=10`

**Response:** `200 OK` with matching students

---

### **10. Get Students with Pending Fees**

**Endpoint:** `GET /api/v1/students/pending-fees`  
**Authorization:** ADMIN, SUPER_USER  
**Description:** Get list of students with outstanding fee balances

**Example:** `GET /api/v1/students/pending-fees?page=0&size=20`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "fullName": "John Doe",
        "admissionNumber": "STU20240001",
        "totalFees": 50000.00,
        "feesPaid": 10000.00,
        "feesBalance": 40000.00,
        "feesPaidPercentage": 20.0
      }
    ],
    "totalElements": 45
  }
}
```

---

### **11. Get Students by Admission Date Range**

**Endpoint:** `GET /api/v1/students/admission-date-range`  
**Authorization:** ADMIN, SUPER_USER  
**Description:** Get students admitted within a date range

**Query Parameters:**
- `startDate` (required, format: yyyy-MM-dd)
- `endDate` (required, format: yyyy-MM-dd)
- `page` (default: 0)
- `size` (default: 10)

**Example:** `GET /api/v1/students/admission-date-range?startDate=2024-01-01&endDate=2024-12-31`

**Response:** `200 OK` with filtered students

---

### **12. Soft Delete Student**

**Endpoint:** `DELETE /api/v1/students/{id}`  
**Authorization:** ADMIN, SUPER_USER  
**Description:** Soft delete a student (move to recycle bin)

**Example:** `DELETE /api/v1/students/1`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "Student soft-deleted successfully"
}
```

---

### **13. Restore Student**

**Endpoint:** `PATCH /api/v1/students/{id}/restore`  
**Authorization:** ADMIN, SUPER_USER  
**Description:** Restore a soft-deleted student

**Example:** `PATCH /api/v1/students/1/restore`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "message": "Student restored successfully"
}
```

---

### **14. Permanently Delete Student**

**Endpoint:** `DELETE /api/v1/students/{id}/permanent`  
**Authorization:** ADMIN only  
**Description:** Permanently delete a student (cannot be undone)

**Example:** `DELETE /api/v1/students/1/permanent`

**Response:** `204 No Content`

---

### **15. Get Student Attendance Percentage**

**Endpoint:** `GET /api/v1/students/{id}/attendance-percentage`  
**Authorization:** ADMIN, SUPER_USER, TEACHER, STUDENT, PARENT  
**Description:** Get student's attendance percentage

**Example:** `GET /api/v1/students/1/attendance-percentage`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "studentId": 1,
    "percentage": 92.5
  }
}
```

---

### **16. Get Student Count by Status**

**Endpoint:** `GET /api/v1/students/count/status/{status}`  
**Authorization:** ADMIN, SUPER_USER  
**Description:** Get count of students by status

**Example:** `GET /api/v1/students/count/status/ACTIVE`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "status": "ACTIVE",
    "count": 1250
  }
}
```

---

### **17. Check Admission Number Existence**

**Endpoint:** `GET /api/v1/students/exists/admission/{admissionNumber}`  
**Authorization:** ADMIN, SUPER_USER  
**Description:** Check if an admission number already exists

**Example:** `GET /api/v1/students/exists/admission/STU20240001`

**Response:** `200 OK`
```json
{
  "responseStatus": "OK",
  "status": "success",
  "data": {
    "identifier": "STU20240001",
    "exists": true
  }
}
```

---

## 🚨 Error Responses

### **404 Not Found**
```json
{
  "responseStatus": "NOT_FOUND",
  "status": "error",
  "message": "Student not found with ID: 999"
}
```

### **400 Bad Request (Validation Error)**
```json
{
  "responseStatus": "BAD_REQUEST",
  "status": "error",
  "message": "Validation failed",
  "errors": {
    "firstName": "First name is required",
    "email": "Invalid email format",
    "admissionNumber": "Admission number must start with STU"
  }
}
```

### **409 Conflict**
```json
{
  "responseStatus": "CONFLICT",
  "status": "error",
  "message": "Admission number already exists: STU20240001"
}
```

### **403 Forbidden**
```json
{
  "responseStatus": "FORBIDDEN",
  "status": "error",
  "message": "Access denied. Required role: ADMIN"
}
```

---

## 💡 Usage Examples

### **Example 1: Register New Student**

```bash
curl -X POST https://api.codewithvijay.online/api/v1/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Rahul",
    "lastName": "Kumar",
    "admissionNumber": "STU20240025",
    "dateOfBirth": "2011-08-20",
    "gender": "MALE",
    "email": "rahul.kumar@student.school.com",
    "phoneNumber": "9988776655",
    "fatherName": "Rajesh Kumar",
    "fatherPhone": "9988776656",
    "admissionDate": "2024-01-20",
    "classId": 2,
    "section": "B",
    "rollNumber": 12,
    "totalFees": 45000.00
  }'
```

### **Example 2: Search for Students**

```bash
curl -X GET "https://api.codewithvijay.online/api/v1/students/search?keyword=rahul&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### **Example 3: Get Students with Pending Fees**

```bash
curl -X GET "https://api.codewithvijay.online/api/v1/students/pending-fees?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📊 Response Fields Description

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique student identifier |
| `fullName` | String | Complete name (first + middle + last) |
| `admissionNumber` | String | Unique admission number (STU + digits) |
| `age` | Integer | Calculated from date of birth |
| `className` | String | Name of the class (e.g., "Class 10") |
| `feesBalance` | Double | Remaining fee amount to be paid |
| `feesPaidPercentage` | Double | Percentage of fees paid |
| `daysInSchool` | Integer | Days since admission |
| `status` | Enum | Current student status |
| `isDeleted` | Boolean | Soft delete flag |

---

## 🎯 Best Practices

1. **Always validate admission numbers** before creating students
2. **Use pagination** for listing endpoints to avoid performance issues
3. **Soft delete** students instead of permanent deletion
4. **Search functionality** is case-insensitive
5. **Date formats** must be ISO 8601 (yyyy-MM-dd)
6. **Fee calculations** are automatic (balance = total - paid)

---

## 🔄 Integration with Other Modules

- **User Management**: Link students to User accounts for login
- **Attendance**: Track daily attendance with percentage calculation
- **Grades**: Record academic performance
- **Fees**: Manage fee payments and balances
- **Library**: Track book borrowing
- **Timetable**: Assign class schedules

---

**API Version:** 1.0  
**Last Updated:** January 2025  
**Contact:** support@schoolsystem.com

