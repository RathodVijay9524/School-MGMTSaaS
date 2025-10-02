# 🏢 Multi-Tenant Migration Plan for School Management System

## 📋 Overview

Apply business context (owner_id) to ALL entities for complete multi-tenant isolation.

---

## 🎯 Migration Checklist

### **Phase 1: Update Entities (Add owner_id field)**

| Entity | Priority | Status | Changes |
|--------|----------|--------|---------|
| Student | 🔴 Critical | ✅ DONE | Added owner_id field |
| Teacher | 🔴 Critical | ⏳ TODO | Add owner_id field |
| SchoolClass | 🔴 Critical | ⏳ TODO | Add owner_id field |
| Subject | 🔴 Critical | ⏳ TODO | Add owner_id field |
| Course | 🟡 High | ⏳ TODO | Add owner_id field |
| Attendance | 🟡 High | ⏳ TODO | Add owner_id field |
| Grade | 🟡 High | ⏳ TODO | Add owner_id field |
| Exam | 🟡 High | ⏳ TODO | Add owner_id field |
| Assignment | 🟡 High | ⏳ TODO | Add owner_id field |
| Timetable | 🟢 Medium | ⏳ TODO | Add owner_id field |
| Fee | 🟢 Medium | ⏳ TODO | Add owner_id field |
| Library | 🟢 Medium | ⏳ TODO | Add owner_id field |
| BookIssue | 🟢 Low | ⏳ TODO | Add owner_id field |
| Event | 🟢 Low | ⏳ TODO | Add owner_id field |
| Announcement | 🟢 Low | ⏳ TODO | Add owner_id field |
| HomeworkSubmission | 🟢 Low | ⏳ TODO | Add owner_id field |

---

## 🔧 Standard Changes for Each Entity

### **1. Add Owner Field:**
```java
// Business Owner (Multi-tenancy)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "owner_id", nullable = false)
private User owner;
```

### **2. Update Repository Methods:**
```java
// Find by owner
Page<Entity> findByOwner_IdAndIsDeletedFalse(Long ownerId, Pageable pageable);

// Search by owner
@Query("SELECT e FROM Entity e WHERE e.owner.id = :ownerId AND ...")
Page<Entity> searchByOwner(@Param("ownerId") Long ownerId, ...);

// Count by owner
long countByOwner_IdAndIsDeletedFalse(Long ownerId);
```

### **3. Update Service Implementation:**
```java
@Override
public EntityResponse createEntity(EntityRequest request) {
    // Get logged-in user (Business Owner)
    CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
    User owner = userRepository.findById(loggedInUser.getId())
        .orElseThrow(...);
    
    // Create entity
    Entity entity = Entity.builder()
        .field1(request.getField1())
        .owner(owner) // ← Add this
        .build();
    
    return repository.save(entity);
}

@Override
public Page<EntityResponse> getAllEntities(Pageable pageable) {
    // Get business context
    CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
    Long ownerId = loggedInUser.getId();
    
    // Filter by owner
    return repository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
}
```

---

## ⏱️ Time Estimates

| Task | Time | Priority |
|------|------|----------|
| Update 16 Entities | 30 min | 🔴 Critical |
| Update 16 Repositories | 45 min | 🔴 Critical |
| Create Indexes (SQL) | 15 min | 🟡 High |
| Update Services | 2 hours | 🔴 Critical |
| Test All Modules | 1 hour | 🔴 Critical |

**Total Estimated Time:** 4.5 hours

---

## 🚀 Implementation Order

### **Priority 1: Core Academic (1 hour)**
1. Teacher
2. SchoolClass  
3. Subject

### **Priority 2: Daily Operations (1 hour)**
4. Attendance
5. Grade
6. Course

### **Priority 3: Administrative (1 hour)**
7. Fee
8. Exam
9. Assignment

### **Priority 4: Supporting (1.5 hours)**
10. Timetable
11. Library
12. BookIssue
13. Event
14. Announcement
15. HomeworkSubmission

---

## 📊 Database Migration

### **Add Indexes for Performance:**
```sql
-- Add indexes on all owner_id columns
CREATE INDEX idx_students_owner ON students(owner_id);
CREATE INDEX idx_teachers_owner ON teachers(owner_id);
CREATE INDEX idx_classes_owner ON school_classes(owner_id);
CREATE INDEX idx_subjects_owner ON subjects(owner_id);
CREATE INDEX idx_courses_owner ON courses(owner_id);
CREATE INDEX idx_attendance_owner ON attendance(owner_id);
CREATE INDEX idx_grades_owner ON grades(owner_id);
CREATE INDEX idx_exams_owner ON exams(owner_id);
CREATE INDEX idx_assignments_owner ON assignments(owner_id);
CREATE INDEX idx_timetables_owner ON timetables(owner_id);
CREATE INDEX idx_fees_owner ON fees(owner_id);
CREATE INDEX idx_library_owner ON library_books(owner_id);
CREATE INDEX idx_book_issues_owner ON book_issues(owner_id);
CREATE INDEX idx_events_owner ON events(owner_id);
CREATE INDEX idx_announcements_owner ON announcements(owner_id);
CREATE INDEX idx_homework_owner ON homework_submissions(owner_id);
```

---

## 🎯 Expected Outcome

After complete implementation:

✅ **Complete Multi-Tenancy**
- Every entity filtered by owner_id
- Complete data isolation
- No cross-school data access

✅ **Scalability**
- Support unlimited schools
- Each school independent
- Shared infrastructure

✅ **Performance**
- Indexed queries
- Fast filtering
- Efficient pagination

---

**Ready to implement? I'll update all entities, repositories, and services now!**

