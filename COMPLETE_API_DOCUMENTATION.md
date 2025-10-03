# 🚀 Complete School Management System API Documentation

## 📋 Overview

This document provides comprehensive API documentation for the School Management System with dual authentication support for both regular users and worker users.

**Base URL:** `http://localhost:9091`

**Authentication:** JWT Bearer Token

---

## 🔐 Authentication System

### Dual Authentication Support
The system supports two types of users:
- **Regular Users** (School Owners, Admins) - stored in `users` table
- **Worker Users** (Teachers, Students, Staff) - stored in `workers` table

Both use the same login endpoint with automatic user type detection.

---

## 📚 API Endpoints

### 🔑 Authentication Endpoints

#### 1. User Registration (Admin)
```http
POST /api/auth/register/admin
Content-Type: application/json

{
  "username": "school_owner",
  "password": "password123",
  "email": "owner@school.com",
  "name": "School Owner Name",
  "phoNo": "9876543210"
}
```

**Response:**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "username": "school_owner",
    "email": "owner@school.com",
    "name": "School Owner Name",
    "roles": ["ROLE_ADMIN"]
  }
}
```

#### 2. Universal Login (User & Worker)
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "school_owner",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "success",
  "data": {
    "jwtToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "school_owner",
      "email": "owner@school.com",
      "name": "School Owner Name",
      "roles": ["ROLE_ADMIN"]
    },
    "refreshTokenDto": {
      "refreshToken": "uuid-token",
      "expiryDate": "2024-10-10T10:00:00Z"
    }
  }
}
```

#### 3. Password Change
```http
PUT /api/auth/change-password
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "currentPassword": "old_password",
  "newPassword": "new_password"
}
```

#### 4. Get Current User
```http
GET /api/auth/current-user
Authorization: Bearer {jwt_token}
```

---

### 👥 User Management Endpoints

#### 1. Create User
```http
POST /api/users
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "username": "new_user",
  "password": "password123",
  "email": "user@school.com",
  "name": "User Name",
  "phoNo": "9876543210"
}
```

#### 2. Get All Users (with filtering)
```http
GET /api/users/filter?page=0&size=10&sortBy=name&sortDir=asc
Authorization: Bearer {jwt_token}
```

#### 3. Get Active Users
```http
GET /api/users/active
Authorization: Bearer {jwt_token}
```

#### 4. Update User Status
```http
PATCH /api/users/{userId}/status
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "status": "ACTIVE"
}
```

---

### 👨‍🏫 Worker Management Endpoints

#### 1. Create Worker (Teacher/Student/Staff)
```http
POST /api/v1/workers
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "username": "teacher_john",
  "password": "password123",
  "email": "john@school.com",
  "name": "John Teacher",
  "phoNo": "9876543210",
  "roles": ["ROLE_TEACHER"]
}
```

#### 2. Get All Workers
```http
GET /api/v1/workers?page=0&size=10&sortBy=name&sortDir=asc
Authorization: Bearer {jwt_token}
```

#### 3. Get Worker by ID
```http
GET /api/v1/workers/{workerId}
Authorization: Bearer {jwt_token}
```

#### 4. Update Worker
```http
PUT /api/v1/workers/{workerId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "Updated Name",
  "email": "updated@school.com",
  "phoNo": "9876543211"
}
```

#### 5. Delete Worker (Soft Delete)
```http
DELETE /api/v1/workers/{workerId}
Authorization: Bearer {jwt_token}
```

#### 6. Restore Worker
```http
PATCH /api/v1/workers/{workerId}/restore
Authorization: Bearer {jwt_token}
```

#### 7. Get Active Workers
```http
GET /api/v1/workers/active
Authorization: Bearer {jwt_token}
```

#### 8. Search Workers
```http
GET /api/v1/workers/search?keyword=john&page=0&size=10
Authorization: Bearer {jwt_token}
```

---

### 🏫 School Class Management

#### 1. Create Class
```http
POST /api/v1/school-classes
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "className": "Class 10A",
  "description": "Class 10 Section A",
  "capacity": 40,
  "academicYear": "2024-2025",
  "status": "ACTIVE"
}
```

#### 2. Get All Classes
```http
GET /api/v1/school-classes?page=0&size=10&sortBy=className&sortDir=asc
Authorization: Bearer {jwt_token}
```

#### 3. Get Class by ID
```http
GET /api/v1/school-classes/{classId}
Authorization: Bearer {jwt_token}
```

#### 4. Update Class
```http
PUT /api/v1/school-classes/{classId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "className": "Updated Class Name",
  "description": "Updated Description",
  "capacity": 45
}
```

#### 5. Delete Class
```http
DELETE /api/v1/school-classes/{classId}
Authorization: Bearer {jwt_token}
```

---

### 📚 Subject Management

#### 1. Create Subject
```http
POST /api/v1/subjects
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "subjectCode": "MATH101",
  "subjectName": "Mathematics",
  "description": "Basic Mathematics",
  "type": "CORE",
  "credits": 4,
  "totalMarks": 100,
  "passingMarks": 33,
  "department": "Mathematics",
  "status": "ACTIVE"
}
```

#### 2. Get All Subjects
```http
GET /api/v1/subjects?page=0&size=10&sortBy=subjectName&sortDir=asc
Authorization: Bearer {jwt_token}
```

#### 3. Get Subject by ID
```http
GET /api/v1/subjects/{subjectId}
Authorization: Bearer {jwt_token}
```

#### 4. Update Subject
```http
PUT /api/v1/subjects/{subjectId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "subjectName": "Advanced Mathematics",
  "credits": 5,
  "totalMarks": 120
}
```

#### 5. Delete Subject
```http
DELETE /api/v1/subjects/{subjectId}
Authorization: Bearer {jwt_token}
```

---

### 📊 Dashboard & Analytics

#### 1. Get Dashboard Data
```http
GET /api/dashboard/stats
Authorization: Bearer {jwt_token}
```

#### 2. Get User Dashboard
```http
GET /api/dashboard/user
Authorization: Bearer {jwt_token}
```

---

### 📧 Communication Endpoints

#### 1. Send SMS
```http
POST /api/sms/send
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "to": "9876543210",
  "message": "Hello from school"
}
```

#### 2. Send WhatsApp Message
```http
POST /api/whatsapp/send
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "to": "9876543210",
  "message": "Hello from school"
}
```

#### 3. Send Email
```http
POST /api/email/send
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "to": "student@school.com",
  "subject": "Important Notice",
  "body": "Please check the school notice board"
}
```

---

### 🎓 Student Management (Advanced)

#### 1. Create Student
```http
POST /api/v1/students
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "admissionNumber": "STU20240001",
  "dateOfBirth": "2010-05-15",
  "gender": "MALE",
  "email": "john.doe@student.com",
  "phoneNumber": "9876543210",
  "fatherName": "Robert Doe",
  "fatherPhone": "9876543211",
  "motherName": "Mary Doe",
  "motherPhone": "9876543212",
  "admissionDate": "2024-01-15",
  "classId": 1,
  "section": "A",
  "rollNumber": 15,
  "totalFees": 50000,
  "feesPaid": 10000
}
```

---

### 📝 Attendance Management

#### 1. Mark Attendance
```http
POST /api/attendance/mark
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "studentId": 1,
  "classId": 1,
  "date": "2024-10-03",
  "status": "PRESENT",
  "remarks": "On time"
}
```

#### 2. Get Attendance Report
```http
GET /api/attendance/report?studentId=1&startDate=2024-10-01&endDate=2024-10-31
Authorization: Bearer {jwt_token}
```

---

### 💰 Fee Management

#### 1. Create Fee Record
```http
POST /api/fees/create
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "studentId": 1,
  "amount": 5000,
  "dueDate": "2024-10-15",
  "description": "Monthly Fee",
  "feeType": "TUITION"
}
```

#### 2. Record Fee Payment
```http
POST /api/fees/payment
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "studentId": 1,
  "amount": 5000,
  "paymentDate": "2024-10-03",
  "paymentMethod": "CASH",
  "receiptNumber": "RCP001"
}
```

---

### 📋 Exam Management

#### 1. Create Exam
```http
POST /api/exams/create
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "examName": "Mid Term Exam",
  "classId": 1,
  "subjectId": 1,
  "examDate": "2024-10-15",
  "startTime": "09:00",
  "endTime": "12:00",
  "totalMarks": 100
}
```

#### 2. Record Exam Results
```http
POST /api/exams/results
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "examId": 1,
  "studentId": 1,
  "obtainedMarks": 85,
  "grade": "A",
  "remarks": "Excellent performance"
}
```

---

## 🔒 Security & Authorization

### Role-Based Access Control
- **ROLE_ADMIN**: Full system access
- **ROLE_SUPER_USER**: School management access
- **ROLE_TEACHER**: Student and class management
- **ROLE_STUDENT**: Own profile access
- **ROLE_PARENT**: Child's profile access

### JWT Token Usage
Include the JWT token in the Authorization header:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📊 Response Format

### Success Response
```json
{
  "status": "success",
  "data": { ... },
  "message": "Operation completed successfully"
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "details": "Detailed error information"
}
```

---

## 🧪 Testing

### Test Users
- **Admin**: admin/admin
- **School Owner**: vijay/vijay
- **Regular User**: rana/rana
- **Teacher**: teacher_sapna/teacher_sapna
- **Student**: student_sapna/student_sapna

### Health Check
```http
GET /actuator/health
```

---

## 📱 MCP Integration

The system exposes **109 MCP tools** for AI chatbot integration:

- User Management Tools (15+)
- Worker Management Tools (20+)
- Class Management Tools (10+)
- Subject Management Tools (10+)
- Attendance Tools (15+)
- Fee Management Tools (15+)
- Communication Tools (10+)
- Dashboard Tools (5+)
- File Management Tools (5+)
- Role Management Tools (5+)

---

## 🚀 Quick Start

1. **Register Admin User**
2. **Login to get JWT token**
3. **Create workers (teachers/students)**
4. **Create classes and subjects**
5. **Start managing your school!**

---

*Last Updated: October 3, 2024*
*Version: 2.0.0*
