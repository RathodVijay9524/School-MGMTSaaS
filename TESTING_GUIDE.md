# 🧪 School Management System Testing Guide

## 📋 Overview

This guide provides comprehensive testing instructions for the School Management System with dual authentication support.

**Base URL:** `http://localhost:9091`

---

## 🚀 Quick Start Testing

### Prerequisites
1. School Management System running on port 9091
2. Database properly configured
3. All dependencies installed

### Test Users Available
- **Admin**: `admin/admin`
- **School Owner**: `vijay/vijay`
- **Regular User**: `rana/rana`
- **Teacher**: `teacher_sapna/teacher_sapna`
- **Student**: `student_sapna/student_sapna`

---

## 🔐 Authentication Testing

### 1. Register New Admin User
```bash
curl -X POST http://localhost:9091/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_admin",
    "password": "password123",
    "email": "admin@test.com",
    "name": "Test Admin",
    "phoNo": "9876543210"
  }'
```

### 2. Login as Admin
```bash
curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin"
  }'
```

### 3. Login as Teacher (Worker User)
```bash
curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "teacher_sapna",
    "password": "teacher_sapna"
  }'
```

### 4. Login as Student (Worker User)
```bash
curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "student_sapna",
    "password": "student_sapna"
  }'
```

**Expected Results:**
- All login attempts should return 200 OK
- JWT token should be generated for each user type
- User details should be returned correctly

---

## 👥 User Management Testing

### 1. Create New User
```bash
curl -X POST http://localhost:9091/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "new_user",
    "password": "password123",
    "email": "user@test.com",
    "name": "Test User",
    "phoNo": "9876543211"
  }'
```

### 2. Get All Users
```bash
curl -X GET "http://localhost:9091/api/users/filter?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Get Active Users
```bash
curl -X GET http://localhost:9091/api/users/active \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 👨‍🏫 Worker Management Testing

### 1. Create Teacher
```bash
curl -X POST http://localhost:9091/api/v1/workers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_teacher",
    "password": "password123",
    "email": "teacher@test.com",
    "name": "Test Teacher",
    "phoNo": "9876543212",
    "roles": ["ROLE_TEACHER"]
  }'
```

### 2. Create Student
```bash
curl -X POST http://localhost:9091/api/v1/workers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_student",
    "password": "password123",
    "email": "student@test.com",
    "name": "Test Student",
    "phoNo": "9876543213",
    "roles": ["ROLE_STUDENT"]
  }'
```

### 3. Get All Workers
```bash
curl -X GET "http://localhost:9091/api/v1/workers?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Search Workers
```bash
curl -X GET "http://localhost:9091/api/v1/workers/search?keyword=test&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🏫 School Class Testing

### 1. Create Class
```bash
curl -X POST http://localhost:9091/api/v1/school-classes \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "className": "Test Class 10A",
    "description": "Test class for testing",
    "capacity": 40,
    "academicYear": "2024-2025",
    "status": "ACTIVE"
  }'
```

### 2. Get All Classes
```bash
curl -X GET "http://localhost:9091/api/v1/school-classes?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📚 Subject Testing

### 1. Create Subject
```bash
curl -X POST http://localhost:9091/api/v1/subjects \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subjectCode": "TEST101",
    "subjectName": "Test Subject",
    "description": "Test subject for testing",
    "type": "CORE",
    "credits": 4,
    "totalMarks": 100,
    "passingMarks": 33,
    "department": "Test Department",
    "status": "ACTIVE"
  }'
```

### 2. Get All Subjects
```bash
curl -X GET "http://localhost:9091/api/v1/subjects?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📊 Dashboard Testing

### 1. Get Dashboard Stats
```bash
curl -X GET http://localhost:9091/api/dashboard/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Get User Dashboard
```bash
curl -X GET http://localhost:9091/api/dashboard/user \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📧 Communication Testing

### 1. Send SMS
```bash
curl -X POST http://localhost:9091/api/sms/send \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "9876543210",
    "message": "Test SMS from School Management System"
  }'
```

### 2. Send Email
```bash
curl -X POST http://localhost:9091/api/email/send \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "test@example.com",
    "subject": "Test Email",
    "body": "This is a test email from School Management System"
  }'
```

---

## 🔧 System Health Testing

### 1. Health Check
```bash
curl -X GET http://localhost:9091/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

## 🧪 Automated Testing Scripts

### PowerShell Testing Script
```powershell
# Test Authentication
$loginBody = @{
    usernameOrEmail = "admin"
    password = "admin"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$jwtToken = $loginResponse.data.jwtToken

Write-Host "✅ Login successful! JWT Token: $($jwtToken.Substring(0,50))..."

# Test Worker Creation
$headers = @{
    "Authorization" = "Bearer $jwtToken"
    "Content-Type" = "application/json"
}

$workerData = @{
    username = "test_worker"
    password = "password123"
    email = "worker@test.com"
    name = "Test Worker"
    phoNo = "9876543214"
    roles = @("ROLE_TEACHER")
} | ConvertTo-Json

$workerResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/workers" -Method POST -Headers $headers -Body $workerData
Write-Host "✅ Worker created! ID: $($workerResponse.data.id)"
```

---

## 📋 Test Checklist

### Authentication Tests
- [ ] Admin user registration
- [ ] Regular user login
- [ ] Teacher login (worker user)
- [ ] Student login (worker user)
- [ ] JWT token generation
- [ ] Token validation

### User Management Tests
- [ ] Create new user
- [ ] Get all users
- [ ] Get active users
- [ ] Update user status
- [ ] User filtering and pagination

### Worker Management Tests
- [ ] Create teacher
- [ ] Create student
- [ ] Get all workers
- [ ] Get worker by ID
- [ ] Update worker
- [ ] Delete worker (soft delete)
- [ ] Restore worker
- [ ] Search workers

### Class Management Tests
- [ ] Create class
- [ ] Get all classes
- [ ] Get class by ID
- [ ] Update class
- [ ] Delete class

### Subject Management Tests
- [ ] Create subject
- [ ] Get all subjects
- [ ] Get subject by ID
- [ ] Update subject
- [ ] Delete subject

### Communication Tests
- [ ] Send SMS
- [ ] Send email
- [ ] Send WhatsApp (if configured)

### System Tests
- [ ] Health check
- [ ] Database connectivity
- [ ] JWT token expiration
- [ ] Error handling

---

## 🐛 Common Issues & Solutions

### Issue 1: 401 Unauthorized
**Cause:** Invalid or expired JWT token
**Solution:** Re-login to get new token

### Issue 2: 500 Internal Server Error
**Cause:** Database constraint violations or missing data
**Solution:** Check database logs and ensure all required fields are provided

### Issue 3: 404 Not Found
**Cause:** Incorrect endpoint URL
**Solution:** Verify endpoint URL matches the documentation

### Issue 4: Foreign Key Constraint Error
**Cause:** Referenced entity doesn't exist
**Solution:** Ensure parent entities exist before creating child entities

---

## 📊 Performance Testing

### Load Testing with Apache Bench
```bash
# Test login endpoint with 100 requests
ab -n 100 -c 10 -p login_data.json -T application/json http://localhost:9091/api/auth/login

# Test worker creation with 50 requests
ab -n 50 -c 5 -p worker_data.json -T application/json -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:9091/api/v1/workers
```

### Response Time Benchmarks
- **Login**: < 500ms
- **User Creation**: < 300ms
- **Worker Creation**: < 400ms
- **Data Retrieval**: < 200ms

---

## 🔒 Security Testing

### 1. JWT Token Security
- Test with expired token
- Test with invalid token
- Test with malformed token

### 2. Authorization Testing
- Test endpoints with different user roles
- Test unauthorized access attempts
- Test privilege escalation

### 3. Input Validation
- Test with malicious input
- Test SQL injection attempts
- Test XSS attempts

---

## 📈 Monitoring & Logging

### Application Logs
Check application logs for:
- Authentication attempts
- Database queries
- Error messages
- Performance metrics

### Database Monitoring
Monitor:
- Connection pool usage
- Query performance
- Lock contention
- Storage usage

---

*Last Updated: October 3, 2024*
*Version: 2.0.0*
