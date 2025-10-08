# Parent Role Setup Guide

## 🎯 Overview
This guide explains how to set up the `ROLE_PARENT` role in the system and configure parent management.

---

## ✅ Changes Made

### 1. **Role Assignment Modal (Parents)**
**Removed powerful roles** that owners shouldn't be able to assign:
- ❌ **Removed**: `ROLE_ADMIN`
- ❌ **Removed**: `ROLE_SUPER_ADMIN`
- ✅ **Available**: `ROLE_STUDENT`, `ROLE_TEACHER`, `ROLE_PARENT`

**Location**: `src/main/resources/templates/dashboard/parents.html`

### 2. **Parent Creation Form (Owner Dashboard)**
**Updated** to use `ROLE_PARENT` instead of `ROLE_NORMAL`:
```javascript
roles: ['ROLE_PARENT']  // Changed from ROLE_NORMAL
```

**Location**: `src/main/resources/templates/dashboard/owner.html`

### 3. **Parent Filtering Logic**
**Updated** to filter by `ROLE_PARENT`:
```javascript
parents = allWorkers.filter(worker => 
    worker.roles && worker.roles.some(role => 
        role.name === 'ROLE_PARENT' || role.name.includes('ROLE_PARENT')
    )
);
```

**Location**: `src/main/resources/templates/dashboard/parents.html`

### 4. **Role Badge Colors**
**Added** purple color for `ROLE_PARENT`:
```javascript
'ROLE_PARENT': 'bg-purple-100 text-purple-800'
```

---

## 🚀 Setup Instructions

### **Step 1: Create ROLE_PARENT Role**

You need to create the `ROLE_PARENT` role using the admin account:

#### **Option A: Using PowerShell Script**
```powershell
# Run this command (make sure server is running first!)
powershell -ExecutionPolicy Bypass -File create-role-parent-simple.ps1
```

#### **Option B: Using API Directly**
1. **Login as admin**:
   ```bash
   POST http://localhost:9091/api/auth/login
   Body: {"username":"admin","password":"admin"}
   ```

2. **Create ROLE_PARENT**:
   ```bash
   POST http://localhost:9091/api/roles
   Headers: 
     Authorization: Bearer YOUR_TOKEN
     Content-Type: application/json
   Body: {"name":"ROLE_PARENT"}
   ```

#### **Option C: Using Postman/Thunder Client**
1. **Login**:
   - Method: `POST`
   - URL: `http://localhost:9091/api/auth/login`
   - Body (JSON):
     ```json
     {
       "username": "admin",
       "password": "admin"
     }
     ```
   - Copy the `jwtToken` from response

2. **Create Role**:
   - Method: `POST`
   - URL: `http://localhost:9091/api/roles`
   - Headers:
     - `Authorization`: `Bearer YOUR_JWT_TOKEN`
     - `Content-Type`: `application/json`
   - Body (JSON):
     ```json
     {
       "name": "ROLE_PARENT"
     }
     ```

---

### **Step 2: Restart the Application**
After creating the role, restart the Spring Boot application to ensure all changes are loaded.

---

### **Step 3: Test Parent Management**

1. **Login as Owner** (e.g., username: `rana`, password: `rana`)

2. **Go to Owner Dashboard**: `http://localhost:9091/dashboard/owner`

3. **Click "Add New Parent"** in Quick Actions

4. **Fill the form**:
   - Name: `Test Parent`
   - Username: `parent1`
   - Email: `parent1@example.com`
   - Phone: `1234567890`
   - Password: `password123`
   - About: `Test parent account`

5. **Submit** - Parent will be created with `ROLE_PARENT`

6. **Go to Parent Management**: `http://localhost:9091/dashboard/parents`

7. **Verify**:
   - ✅ Parent appears in the list
   - ✅ Role badge shows "PARENT" in purple
   - ✅ Can activate/deactivate
   - ✅ Can soft delete
   - ✅ Can restore
   - ✅ Can view details
   - ✅ Can assign roles (only STUDENT, TEACHER, PARENT)

---

## 🔒 Security Features

### **Owner Role Restrictions**
Owners can now only assign these roles:
- ✅ `ROLE_STUDENT` - For students
- ✅ `ROLE_TEACHER` - For teachers  
- ✅ `ROLE_PARENT` - For parents

Owners **CANNOT** assign:
- ❌ `ROLE_ADMIN` - Prevents privilege escalation
- ❌ `ROLE_SUPER_ADMIN` - Prevents privilege escalation

This prevents owners from creating admin accounts and compromising system security.

---

## 📋 API Endpoints Reference

### **Role Management**
```
POST   /api/roles                 - Create new role (Admin only)
GET    /api/roles                 - List all roles
GET    /api/roles/{id}            - Get role by ID
PUT    /api/roles/{id}            - Update role
DELETE /api/roles/{id}            - Delete role
```

### **Worker Management (Parents)**
```
POST   /api/v1/workers                                    - Create parent
GET    /api/v1/workers/{id}                               - Get parent details
PUT    /api/v1/workers/{id}                               - Update parent
DELETE /api/v1/workers/{id}                               - Soft delete parent
PATCH  /api/v1/workers/{id}/restore                       - Restore parent
DELETE /api/v1/workers/{id}/permanent                     - Permanently delete parent
PATCH  /api/v1/workers/{id}/status?isActive=true/false   - Toggle status
GET    /api/v1/workers/superuser/{ownerId}/advanced-filter - Filter parents
```

---

## 🐛 Troubleshooting

### **Issue: "Role not found with name: 'ROLE_PARENT'"**
**Solution**: You need to create the `ROLE_PARENT` role first using Step 1 above.

### **Issue: "View modal not working"**
**Solution**: Check browser console (F12) for detailed error logs. The system now provides:
- 📝 Parent ID being fetched
- 🔑 Token status
- 📡 Response status code
- 📦 Full API response

### **Issue: "No parents showing in list"**
**Solution**: 
1. Make sure you created parents with `ROLE_PARENT`
2. Check if old parents have `ROLE_NORMAL` - they won't show up anymore
3. You may need to reassign old parents to `ROLE_PARENT`

### **Issue: "401 Unauthorized when creating role"**
**Solution**: Make sure the server is running on `http://localhost:9091`

---

## 📝 Notes

1. **Backward Compatibility**: The system still recognizes `ROLE_NORMAL` for parents (both show purple badges), but new parents will use `ROLE_PARENT`.

2. **Role Colors**:
   - 🔵 Blue: `ROLE_STUDENT`
   - 🟢 Green: `ROLE_TEACHER`
   - 🟣 Purple: `ROLE_PARENT` / `ROLE_NORMAL`
   - 🔵 Indigo: `ROLE_ADMIN`
   - 🔴 Red: `ROLE_SUPER_ADMIN`

3. **View Modal**: Now fetches fresh data from API instead of using cached data.

4. **Permanent Delete**: Fixed to use correct endpoint `/api/v1/workers/{id}/permanent`.

---

## ✅ Summary

**What's Working Now**:
1. ✅ Owner can only assign STUDENT, TEACHER, PARENT roles
2. ✅ No ADMIN or SUPER_ADMIN in owner's role assignment
3. ✅ Parent creation uses `ROLE_PARENT`
4. ✅ Parent filtering looks for `ROLE_PARENT`
5. ✅ Permanent delete works correctly
6. ✅ View modal has detailed error logging
7. ✅ Role badge colors updated

**What You Need To Do**:
1. ⚠️ Create `ROLE_PARENT` role using admin account
2. ⚠️ Test the parent management functionality
3. ⚠️ Check view modal and share console logs if it fails

---

## 🔗 Related Files

- `src/main/resources/templates/dashboard/parents.html` - Parent management page
- `src/main/resources/templates/dashboard/owner.html` - Owner dashboard with parent creation
- `src/main/java/com/vijay/User_Master/controller/RoleController.java` - Role API endpoints
- `src/main/java/com/vijay/User_Master/controller/WorkerUserController.java` - Worker API endpoints
- `create-role-parent-simple.ps1` - Script to create ROLE_PARENT

---

**Created**: 2025-10-08  
**Status**: ✅ Ready for Testing (after creating ROLE_PARENT)

