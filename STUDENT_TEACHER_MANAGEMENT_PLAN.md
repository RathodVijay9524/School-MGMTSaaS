# 📋 Student & Teacher Management Implementation Plan

## 🎯 Goal
Add Student and Teacher management sections to Owner Dashboard with same functionality as User Management in Super Admin dashboard.

## ✅ Features to Implement

### **1. Tab-based Filtering (Like User Management)**
- 👥 **All Students/Teachers** - Show all
- ✅ **Active** - isDeleted=false & isActive=true
- ❌ **Deleted** - isDeleted=true (Soft deleted - in recycle bin)
- ⏰ **Expired** - isDeleted=false & isActive=false (Inactive accounts)

### **2. Search Functionality**
- Search by: name, email, username, phone number
- Real-time search
- Works with tab filters

### **3. Data Table**
**Columns:**
- Photo/Avatar
- Name
- Email
- Phone
- Status (Active/Inactive badge)
- Roles
- Actions (View, Edit, Delete, Restore)

### **4. Actions**
- 👁️ **View** - Show details modal
- ✏️ **Edit** - Edit student/teacher info
- 🗑️ **Soft Delete** - Move to recycle bin
- ♻️ **Restore** - Restore from deleted
- ❌ **Hard Delete** - Permanent delete
- 🔄 **Toggle Status** - Activate/Deactivate

### **5. Pagination**
- Page size: 10, 25, 50
- Previous/Next buttons
- Page numbers
- Total count display

## 📡 API Endpoints to Use

### **Worker Filtering Endpoint:**
```
GET /api/v1/workers/superuser/{ownerId}/advanced-filter
```

**Parameters:**
- `isDeleted` (Boolean) - Filter deleted
- `isActive` (Boolean) - Filter active
- `keyword` (String) - Search term
- `page` (int) - Page number (default 0)
- `size` (int) - Page size (default 10)

### **Filter by Role:**
To show only students or teachers, we'll filter the results by:
- **Students**: Filter workers with `ROLE_STUDENT`
- **Teachers**: Filter workers with `ROLE_TEACHER`

## 🎨 UI Structure

### **Navigation:**
```
Sidebar → Students (click) → Show Student Management Section
Sidebar → Teachers (click) → Show Teacher Management Section
Sidebar → Dashboard (click) → Show Dashboard Section
```

### **Sections to Add:**
1. `dashboardSection` - Current dashboard (existing)
2. `studentSection` - New student management
3. `teacherSection` - New teacher management

### **Student/Teacher Section Layout:**
```
┌─────────────────────────────────────────────┐
│  Student Management            [+ Add]       │
├─────────────────────────────────────────────┤
│  [🔍 Search students...]                     │
├─────────────────────────────────────────────┤
│  👥 All │ ✅ Active │ ❌ Deleted │ ⏰ Expired │
├─────────────────────────────────────────────┤
│  ┌──────────────────────────────────────┐  │
│  │ Photo │ Name │ Email │ Status │ Actions│  │
│  ├──────────────────────────────────────┤  │
│  │ [img] │ John │ john@│ Active│ [⚙️]  │  │
│  │ [img] │ Jane │ jane@│ Active│ [⚙️]  │  │
│  └──────────────────────────────────────┘  │
├─────────────────────────────────────────────┤
│  ← Prev │ 1 2 3 4 5 │ Next →  │ Page 1 of 10│
└─────────────────────────────────────────────┘
```

## 🔧 Implementation Steps

### **Step 1: Add Section HTML**
- Create `studentSection` div (hidden by default)
- Create `teacherSection` div (hidden by default)
- Add tabs for filtering
- Add search input
- Add data table structure
- Add pagination controls

### **Step 2: Add Navigation Logic**
- Update sidebar clicks to show/hide sections
- Dashboard → show dashboard, hide others
- Students → show student section, hide others
- Teachers → show teacher section, hide others

### **Step 3: Add JavaScript Functions**
```javascript
// Load students
function loadStudents(filters) {
  // Call API with ROLE_STUDENT filter
}

// Load teachers  
function loadTeachers(filters) {
  // Call API with ROLE_TEACHER filter
}

// Handle tab change
function handleStudentTabChange(tabId) {
  // Update filters based on tab
}

// Handle search
function searchStudents(keyword) {
  // Search with keyword
}

// Pagination
function changeStudentPage(page) {
  // Load specific page
}

// Actions
function viewStudent(id) {}
function editStudent(id) {}
function deleteStudent(id) {}
function restoreStudent(id) {}
function toggleStudentStatus(id) {}
```

### **Step 4: Reuse User Management Code**
- Copy tab structure from super-admin.html
- Copy table structure
- Copy filtering logic
- Copy search functionality
- Adapt for workers API instead of users API

## 📊 Data Flow

```
User Clicks "Students" 
  → showSection('students')
  → loadStudents({ isDeleted: false, isActive: true })
  → API Call: /api/v1/workers/superuser/{ownerId}/advanced-filter?isActive=true
  → Filter results by ROLE_STUDENT
  → Display in table
  → Show pagination
```

## 🎯 Success Criteria

✅ Click "Students" in sidebar → Show student list
✅ Click "Teachers" in sidebar → Show teacher list
✅ Tabs work (All, Active, Deleted, Expired)
✅ Search works across all fields
✅ Pagination works
✅ Can view student/teacher details
✅ Can soft delete (move to recycle)
✅ Can restore from deleted
✅ Can hard delete permanently
✅ Status toggle works
✅ "Add Student" button opens form modal

## 🚀 Ready to Implement!

This will give you the same powerful management interface as the User Management section!

