# 🗄️ Manual Database Setup Guide

## 📋 Insert Test Data for Student Management Testing

### **Method 1: Using MySQL Workbench (Recommended)**

1. **Open MySQL Workbench**
2. **Connect to your database:**
   - Host: `localhost:3306`
   - Database: `user-master`
   - Username: `root`
   - Password: `root`

3. **Copy and paste this SQL:**

```sql
-- Insert 4 Test Classes
INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, room_number, academic_year, status, is_deleted) 
VALUES 
(1, 'Class 10', 'A', 10, 40, 0, '101', '2024-2025', 'ACTIVE', false),
(2, 'Class 10', 'B', 10, 40, 0, '102', '2024-2025', 'ACTIVE', false),
(3, 'Class 9', 'A', 9, 35, 0, '201', '2024-2025', 'ACTIVE', false),
(4, 'Class 11', 'A', 11, 30, 0, '301', '2024-2025', 'ACTIVE', false)
ON DUPLICATE KEY UPDATE class_name = VALUES(class_name);

-- Insert 5 Test Students
INSERT INTO students (
    first_name, middle_name, last_name, admission_number, date_of_birth, gender, blood_group,
    nationality, religion, email, phone_number, address, city, state, postal_code, country,
    father_name, father_phone, father_occupation, mother_name, mother_phone,
    guardian_name, guardian_phone, guardian_relation, parent_email,
    admission_date, class_id, section, roll_number, status,
    total_fees, fees_paid, fees_balance, is_deleted
) VALUES 
('Rahul', 'Kumar', 'Sharma', 'STU20240001', '2010-05-15', 'MALE', 'O+',
 'Indian', 'Hindu', 'rahul.sharma@student.com', '9876543210',
 '123 MG Road, Apartment 4B', 'Mumbai', 'Maharashtra', '400001', 'India',
 'Rajesh Sharma', '9876543211', 'Engineer', 'Priya Sharma', '9876543212',
 'Rajesh Sharma', '9876543211', 'Father', 'rajesh.sharma@email.com',
 '2024-01-15', 1, 'A', 1, 'ACTIVE',
 50000.00, 10000.00, 40000.00, false),

('Priya', NULL, 'Patel', 'STU20240002', '2010-08-20', 'FEMALE', 'A+',
 'Indian', NULL, 'priya.patel@student.com', '9876543220',
 NULL, 'Mumbai', 'Maharashtra', '400001', 'India',
 'Amit Patel', '9876543221', NULL, 'Meera Patel', '9876543222',
 'Amit Patel', '9876543221', 'Father', 'amit.patel@email.com',
 '2024-01-16', 1, 'A', 2, 'ACTIVE',
 50000.00, 50000.00, 0.00, false),

('Amit', NULL, 'Verma', 'STU20240003', '2010-03-10', 'MALE', 'B+',
 'Indian', NULL, 'amit.verma@student.com', '9876543230',
 NULL, 'Mumbai', 'Maharashtra', NULL, 'India',
 'Suresh Verma', '9876543231', NULL, NULL, NULL,
 'Suresh Verma', '9876543231', 'Father', 'suresh.verma@email.com',
 '2024-01-17', 2, 'B', 1, 'ACTIVE',
 45000.00, 5000.00, 40000.00, false),

('Sneha', NULL, 'Gupta', 'STU20240004', '2010-07-25', 'FEMALE', 'AB+',
 'Indian', NULL, 'sneha.gupta@student.com', '9876543240',
 NULL, 'Delhi', NULL, NULL, 'India',
 'Rakesh Gupta', '9876543241', NULL, NULL, NULL,
 'Rakesh Gupta', '9876543241', 'Father', 'rakesh.gupta@email.com',
 '2024-01-20', 2, 'B', 2, 'ACTIVE',
 50000.00, 25000.00, 25000.00, false),

('Arjun', NULL, 'Singh', 'STU20240005', '2011-02-14', 'MALE', 'O-',
 'Indian', NULL, 'arjun.singh@student.com', '9876543250',
 NULL, 'Bangalore', NULL, NULL, 'India',
 'Vikram Singh', '9876543251', NULL, NULL, NULL,
 'Vikram Singh', '9876543251', 'Father', 'vikram.singh@email.com',
 '2024-02-01', 3, 'A', 1, 'ACTIVE',
 40000.00, 0.00, 40000.00, false)
ON DUPLICATE KEY UPDATE admission_number = VALUES(admission_number);

-- Verify data
SELECT 'Classes:' as info, COUNT(*) as count FROM school_classes WHERE is_deleted = false;
SELECT 'Students:' as info, COUNT(*) as count FROM students WHERE is_deleted = false;
SELECT CONCAT(first_name, ' ', last_name) as student_name, admission_number, fees_balance FROM students WHERE is_deleted = false;
```

4. **Click Execute** (⚡ button or Ctrl+Enter)

---

### **Method 2: Using Command Line**

If you have MySQL command line installed:

```bash
# Navigate to project directory
cd "D:\Live Project -2025-Jul\Deployement\SchoolManagments\School-Managments"

# Run SQL file
mysql -hlocalhost -uroot -proot user-master < insert_complete_test_data.sql
```

---

### **Method 3: Using kubectl (If using Kubernetes)**

```bash
# Get MySQL pod name
kubectl get pods | findstr mysql

# Execute SQL
kubectl exec -it <mysql-pod-name> -- mysql -uroot -ppassword user_master < insert_complete_test_data.sql
```

---

## ✅ **After Inserting Data, You'll Have:**

### **4 Classes:**
1. Class 10 - Section A (Room 101)
2. Class 10 - Section B (Room 102)
3. Class 9 - Section A (Room 201)
4. Class 11 - Section A (Room 301)

### **5 Students:**
1. **Rahul Kumar Sharma** (STU20240001)
   - Class: 10-A, Roll: 1
   - Fees: ₹50,000 (Paid: ₹10,000, Balance: ₹40,000)
   
2. **Priya Patel** (STU20240002)
   - Class: 10-A, Roll: 2
   - Fees: ₹50,000 (Fully Paid ✅)
   
3. **Amit Verma** (STU20240003)
   - Class: 10-B, Roll: 1
   - Fees: ₹45,000 (Paid: ₹5,000, Balance: ₹40,000)
   
4. **Sneha Gupta** (STU20240004)
   - Class: 10-B, Roll: 2
   - Fees: ₹50,000 (Paid: ₹25,000, Balance: ₹25,000)
   
5. **Arjun Singh** (STU20240005)
   - Class: 9-A, Roll: 1
   - Fees: ₹40,000 (Paid: ₹0, Balance: ₹40,000)

---

## 🎯 **Then Run Full Tests!**

After inserting data, we can test all CREATE, UPDATE, DELETE operations!

---

**Which method do you prefer to insert the data?**
1. MySQL Workbench?
2. Command line?
3. Direct SQL copy-paste?

