# 🤖 MCP Chatbot Integration Guide for School Management System

## 📋 Overview

This guide demonstrates how to integrate the School Management System with your chatbot using **Model Context Protocol (MCP)** tools. The system is designed to provide natural language access to all school management functions.

---

## 🎯 MCP Tool Architecture

### **How It Works:**

```
User Query (Natural Language)
    ↓
Chatbot interprets query
    ↓
MCP Tool Selection
    ↓
REST API Call to School Management System
    ↓
Database Query via Repository
    ↓
Response formatted for user
    ↓
Natural Language Response to User
```

---

## 🛠️ MCP Tools to Create

### **1. Student Management Tools**

#### **Tool: get_student_info**
```json
{
  "name": "get_student_info",
  "description": "Get student information by admission number, email, or ID",
  "parameters": {
    "student_identifier": {
      "type": "string",
      "description": "Student admission number, email, or ID"
    }
  }
}
```

**Example Usage:**
- User: "Show me details of student STU2024001"
- Chatbot: Calls `get_student_info("STU2024001")`
- API: `GET /api/students/admission/STU2024001`
- Response: Complete student profile with class, fees, attendance %

#### **Tool: get_student_attendance**
```json
{
  "name": "get_student_attendance",
  "description": "Get attendance records for a student",
  "parameters": {
    "student_id": {"type": "number"},
    "start_date": {"type": "string", "format": "date"},
    "end_date": {"type": "string", "format": "date"}
  }
}
```

**Example Queries:**
- "Show my attendance for this month"
- "What's my attendance percentage?"
- "How many days was I absent in January?"

---

### **2. Teacher Management Tools**

#### **Tool: get_teacher_schedule**
```json
{
  "name": "get_teacher_schedule",
  "description": "Get teacher's timetable/schedule",
  "parameters": {
    "teacher_id": {"type": "number"},
    "date": {"type": "string", "format": "date"}
  }
}
```

**Example Queries:**
- "What's my schedule for today?"
- "Show me tomorrow's classes"
- "Which periods do I have on Monday?"

#### **Tool: mark_attendance**
```json
{
  "name": "mark_attendance",
  "description": "Mark attendance for a class",
  "parameters": {
    "class_id": {"type": "number"},
    "date": {"type": "string"},
    "attendance_records": {
      "type": "array",
      "items": {
        "student_id": "number",
        "status": "string"
      }
    }
  }
}
```

**Example Usage:**
- "Mark attendance for Class 10-A"
- "Student Roll No 5 is absent today"
- "Mark all present for today"

---

### **3. Grade & Academic Tools**

#### **Tool: get_student_grades**
```json
{
  "name": "get_student_grades",
  "description": "Get grades for a student",
  "parameters": {
    "student_id": {"type": "number"},
    "subject_id": {"type": "number", "optional": true},
    "semester": {"type": "string", "optional": true}
  }
}
```

**Example Queries:**
- "Show my grades in Mathematics"
- "What's my overall GPA?"
- "Display my report card for last semester"

#### **Tool: submit_grades**
```json
{
  "name": "submit_grades",
  "description": "Submit grades for students",
  "parameters": {
    "exam_id": {"type": "number"},
    "grades": {
      "type": "array",
      "items": {
        "student_id": "number",
        "marks_obtained": "number"
      }
    }
  }
}
```

---

### **4. Fee Management Tools**

#### **Tool: get_fee_status**
```json
{
  "name": "get_fee_status",
  "description": "Get fee payment status for a student",
  "parameters": {
    "student_id": {"type": "number"},
    "academic_year": {"type": "string"}
  }
}
```

**Example Queries:**
- "What's my fee balance?"
- "Show pending fees"
- "When is my next fee due?"

#### **Tool: record_fee_payment**
```json
{
  "name": "record_fee_payment",
  "description": "Record a fee payment",
  "parameters": {
    "student_id": {"type": "number"},
    "amount": {"type": "number"},
    "payment_method": {"type": "string"},
    "transaction_id": {"type": "string"}
  }
}
```

---

### **5. Assignment & Homework Tools**

#### **Tool: get_pending_assignments**
```json
{
  "name": "get_pending_assignments",
  "description": "Get pending assignments for a student",
  "parameters": {
    "student_id": {"type": "number"}
  }
}
```

**Example Queries:**
- "What homework do I have?"
- "Show pending assignments"
- "What's due this week?"

#### **Tool: submit_homework**
```json
{
  "name": "submit_homework",
  "description": "Submit homework/assignment",
  "parameters": {
    "assignment_id": {"type": "number"},
    "student_id": {"type": "number"},
    "submission_text": {"type": "string"},
    "file_url": {"type": "string", "optional": true}
  }
}
```

---

### **6. Library Tools**

#### **Tool: search_library_books**
```json
{
  "name": "search_library_books",
  "description": "Search for books in the library",
  "parameters": {
    "keyword": {"type": "string"},
    "category": {"type": "string", "optional": true}
  }
}
```

**Example Queries:**
- "Search for books on Mathematics"
- "Find books by author J.K. Rowling"
- "Show available science textbooks"

#### **Tool: issue_book**
```json
{
  "name": "issue_book",
  "description": "Issue a library book",
  "parameters": {
    "book_id": {"type": "number"},
    "student_id": {"type": "number"},
    "due_date": {"type": "string", "format": "date"}
  }
}
```

---

### **7. Event & Announcement Tools**

#### **Tool: get_upcoming_events**
```json
{
  "name": "get_upcoming_events",
  "description": "Get upcoming school events",
  "parameters": {
    "days_ahead": {"type": "number", "default": 7}
  }
}
```

**Example Queries:**
- "What events are coming up?"
- "Show me this week's events"
- "Are there any sports events next month?"

#### **Tool: get_announcements**
```json
{
  "name": "get_announcements",
  "description": "Get recent announcements",
  "parameters": {
    "target_audience": {"type": "string"}
  }
}
```

---

### **8. Analytics & Reports Tools**

#### **Tool: get_class_analytics**
```json
{
  "name": "get_class_analytics",
  "description": "Get analytics for a class",
  "parameters": {
    "class_id": {"type": "number"},
    "metric": {
      "type": "string",
      "enum": ["attendance", "grades", "performance"]
    }
  }
}
```

**Example Queries:**
- "Show attendance statistics for Class 10-A"
- "What's the average grade in Mathematics?"
- "How many students are failing?"

---

## 🗣️ Natural Language Query Examples

### **Student Persona:**

1. **Attendance Queries:**
   ```
   User: "What's my attendance this month?"
   Bot: → get_student_attendance(student_id, this_month)
   Response: "Your attendance for January 2025 is 92% (23 present, 2 absent out of 25 days)"
   ```

2. **Grade Queries:**
   ```
   User: "How did I do in the math exam?"
   Bot: → get_student_grades(student_id, subject="Mathematics")
   Response: "You scored 85/100 (Grade: A) in the Mathematics midterm exam"
   ```

3. **Assignment Queries:**
   ```
   User: "What homework is due tomorrow?"
   Bot: → get_pending_assignments(student_id, due_date=tomorrow)
   Response: "You have 2 assignments due tomorrow:
              1. Mathematics - Chapter 5 Problems (Due: 10 AM)
              2. English - Essay Writing (Due: 2 PM)"
   ```

4. **Fee Queries:**
   ```
   User: "Do I have any pending fees?"
   Bot: → get_fee_status(student_id)
   Response: "Your current fee balance is ₹5,000. Due date: Jan 31, 2025.
              Breakdown: Tuition (₹4,000), Transport (₹1,000)"
   ```

### **Teacher Persona:**

1. **Schedule Queries:**
   ```
   User: "What classes do I have today?"
   Bot: → get_teacher_schedule(teacher_id, today)
   Response: "Today's schedule:
              1. 9:00 AM - Mathematics (Class 10-A, Room 201)
              2. 11:00 AM - Mathematics (Class 10-B, Room 202)
              3. 2:00 PM - Algebra (Class 9-A, Room 105)"
   ```

2. **Attendance Marking:**
   ```
   User: "Mark attendance for Class 10-A, all present except Roll No 5"
   Bot: → mark_attendance(class_id, date, attendance_data)
   Response: "Attendance marked successfully for Class 10-A. 
              Present: 29, Absent: 1 (Roll No 5: Rajesh Kumar)"
   ```

3. **Grading:**
   ```
   User: "Submit grades for midterm exam, Math, Class 10-A"
   Bot: Shows grading interface
   Response: "Please provide grades for 30 students. You can upload CSV or enter manually."
   ```

4. **Analytics:**
   ```
   User: "Show me attendance trends for my class this month"
   Bot: → get_class_analytics(class_id, "attendance")
   Response: "Class 10-A Attendance Trends (January):
              - Average: 94%
              - Students with <75%: 3
              - Perfect attendance: 12 students"
   ```

### **Parent Persona:**

1. **Child's Progress:**
   ```
   User: "How is my child doing in school?"
   Bot: → get_student_info + get_recent_grades + get_attendance
   Response: "Overview for Rajesh Kumar (Class 10-A):
              - Attendance: 88% (4 absences this month)
              - Recent Grades: Math (B+), Science (A), English (B)
              - Upcoming: Science project due on Jan 25"
   ```

2. **Fee Status:**
   ```
   User: "What fees are pending?"
   Bot: → get_fee_status(student_id)
   Response: "Pending fees for Rajesh Kumar:
              - Term 3 Tuition: ₹15,000 (Due: Feb 1)
              - Transportation: ₹2,000 (Due: Feb 1)
              Total: ₹17,000"
   ```

3. **Event Information:**
   ```
   User: "When is the next parent-teacher meeting?"
   Bot: → get_upcoming_events(type="PARENT_TEACHER_MEETING")
   Response: "Next Parent-Teacher Meeting:
              - Date: February 5, 2025
              - Time: 10:00 AM - 1:00 PM
              - Venue: School Auditorium
              - Registration: Click here to register"
   ```

### **Admin Persona:**

1. **System Analytics:**
   ```
   User: "How many students are enrolled this year?"
   Bot: → get_school_statistics()
   Response: "Academic Year 2024-25 Statistics:
              - Total Students: 1,250
              - Total Teachers: 85
              - Active Classes: 45
              - Enrollment Rate: 95%"
   ```

2. **Financial Reports:**
   ```
   User: "What's the fee collection status for this month?"
   Bot: → get_fee_collection_report(month)
   Response: "January 2025 Fee Collection:
              - Total Expected: ₹50,00,000
              - Collected: ₹45,00,000 (90%)
              - Pending: ₹5,00,000
              - Defaulters: 45 students"
   ```

3. **Staff Management:**
   ```
   User: "Which teachers are on leave today?"
   Bot: → get_teacher_leave_status(today)
   Response: "Teachers on leave today (Jan 20):
              1. Mrs. Sharma (Math) - Medical Leave
              2. Mr. Kumar (Physics) - Casual Leave
              Substitute arranged for both classes."
   ```

---

## 🔌 API Endpoints for MCP Tools

### **Base URL:** `https://api.codewithvijay.online/api/v1/school`

### **Student Endpoints:**
```
GET    /students/{id}                    - Get student by ID
GET    /students/admission/{number}      - Get student by admission number
GET    /students/search?keyword={k}      - Search students
POST   /students                         - Create new student
PUT    /students/{id}                    - Update student
DELETE /students/{id}                    - Soft delete student
```

### **Attendance Endpoints:**
```
GET    /attendance/student/{id}          - Get student attendance
POST   /attendance/mark                  - Mark attendance
GET    /attendance/class/{id}/date/{d}   - Get class attendance for date
GET    /attendance/percentage/{studentId} - Get attendance percentage
```

### **Grade Endpoints:**
```
GET    /grades/student/{id}              - Get student grades
GET    /grades/student/{id}/subject/{s}  - Get grades for subject
POST   /grades                           - Submit grades
GET    /grades/student/{id}/gpa          - Calculate GPA
```

### **Fee Endpoints:**
```
GET    /fees/student/{id}                - Get student fees
GET    /fees/pending/{id}                - Get pending fees
POST   /fees/payment                     - Record payment
GET    /fees/overdue                     - Get overdue fees
```

### **Assignment Endpoints:**
```
GET    /assignments/class/{id}           - Get class assignments
GET    /assignments/pending/{studentId}  - Get pending assignments
POST   /assignments                      - Create assignment
POST   /assignments/submit               - Submit homework
```

### **Library Endpoints:**
```
GET    /library/search?keyword={k}       - Search books
GET    /library/available                - Get available books
POST   /library/issue                    - Issue book
POST   /library/return/{issueId}         - Return book
```

### **Event & Announcement Endpoints:**
```
GET    /events/upcoming                  - Get upcoming events
GET    /events/type/{type}               - Get events by type
GET    /announcements/active             - Get active announcements
POST   /announcements                    - Create announcement
```

---

## 🤖 Chatbot Integration Example (Python)

```python
from typing import Dict, Any
import requests

class SchoolManagementMCP:
    def __init__(self, api_base_url: str, auth_token: str):
        self.base_url = api_base_url
        self.headers = {"Authorization": f"Bearer {auth_token}"}
    
    def get_student_attendance(self, student_id: int, 
                              start_date: str, end_date: str) -> Dict[str, Any]:
        """Get student attendance for date range"""
        endpoint = f"{self.base_url}/attendance/student/{student_id}"
        params = {"start_date": start_date, "end_date": end_date}
        response = requests.get(endpoint, params=params, headers=self.headers)
        return response.json()
    
    def get_pending_assignments(self, student_id: int) -> Dict[str, Any]:
        """Get pending assignments for student"""
        endpoint = f"{self.base_url}/assignments/pending/{student_id}"
        response = requests.get(endpoint, headers=self.headers)
        return response.json()
    
    def mark_attendance(self, class_id: int, date: str, 
                       attendance_data: list) -> Dict[str, Any]:
        """Mark attendance for a class"""
        endpoint = f"{self.base_url}/attendance/mark"
        payload = {
            "classId": class_id,
            "date": date,
            "attendanceRecords": attendance_data
        }
        response = requests.post(endpoint, json=payload, headers=self.headers)
        return response.json()
    
    def search_library_books(self, keyword: str) -> Dict[str, Any]:
        """Search library books"""
        endpoint = f"{self.base_url}/library/search"
        params = {"keyword": keyword}
        response = requests.get(endpoint, params=params, headers=self.headers)
        return response.json()

# Usage in chatbot
mcp = SchoolManagementMCP(
    "https://api.codewithvijay.online/api/v1/school",
    "jwt_token_here"
)

# Handle user query
user_query = "Show my attendance this month"
# Parse query and extract intent
intent = "get_attendance"
student_id = extract_student_id_from_context()

if intent == "get_attendance":
    result = mcp.get_student_attendance(
        student_id, 
        "2025-01-01", 
        "2025-01-31"
    )
    response = format_attendance_response(result)
    return response
```

---

## 🎯 Benefits of MCP Integration

1. **Natural Language Interface:**
   - Users interact in plain English
   - No need to learn complex UI
   - Conversational and intuitive

2. **24/7 Availability:**
   - Query information anytime
   - Instant responses
   - No waiting for staff

3. **Multi-Platform Access:**
   - Web chatbot
   - Mobile app chatbot
   - WhatsApp/Telegram integration
   - Voice assistants (Alexa, Google)

4. **Personalized Experience:**
   - Context-aware responses
   - Role-based information
   - Proactive notifications

5. **Reduced Admin Workload:**
   - Self-service for common queries
   - Automated responses
   - Smart routing to staff when needed

---

## 🚀 Implementation Steps

1. **Complete REST API Development**
   - Implement all controllers and services
   - Add authentication and authorization
   - Test all endpoints

2. **Create MCP Tool Definitions**
   - Define all tools in MCP format
   - Document parameters and responses
   - Create tool catalog

3. **Integrate with Chatbot**
   - Connect chatbot to MCP tools
   - Implement natural language understanding
   - Test conversation flows

4. **Deploy and Monitor**
   - Deploy to production
   - Monitor usage and errors
   - Collect user feedback

---

## 📚 Next Steps

To complete the MCP integration, you need to:

1. ✅ **Entities & Repositories** - DONE
2. ⏳ **Create DTOs** - Request/Response objects
3. ⏳ **Implement Services** - Business logic
4. ⏳ **Create Controllers** - REST API endpoints
5. ⏳ **Add Validation** - Input validation
6. ⏳ **Write Tests** - Unit and integration tests
7. ⏳ **API Documentation** - Swagger/OpenAPI docs
8. ⏳ **MCP Tool Definitions** - Tool catalog
9. ⏳ **Chatbot Integration** - Connect to existing chatbot

---

**This School Management System is fully designed for conversational AI integration and will work seamlessly with your chatbot! 🤖🎓**

---

**Author:** AI Assistant  
**Date:** January 2025  
**Version:** 1.0

