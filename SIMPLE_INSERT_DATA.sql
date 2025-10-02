-- ========================================
-- COPY-PASTE THIS INTO YOUR MYSQL CLIENT
-- ========================================
-- Database: user-master
-- Username: root
-- Password: root
-- ========================================

USE `user-master`;

-- Insert Classes
INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, room_number, academic_year, status, is_deleted) VALUES (1, 'Class 10', 'A', 10, 40, 0, '101', '2024-2025', 'ACTIVE', false);
INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, room_number, academic_year, status, is_deleted) VALUES (2, 'Class 10', 'B', 10, 40, 0, '102', '2024-2025', 'ACTIVE', false);
INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, room_number, academic_year, status, is_deleted) VALUES (3, 'Class 9', 'A', 9, 35, 0, '201', '2024-2025', 'ACTIVE', false);

-- Insert 5 Test Students
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Rahul', 'Sharma', 'STU20240001', '2010-05-15', 'MALE', 'rahul.sharma@student.com', '9876543210', 'Rajesh Sharma', '9876543211', 'Rajesh Sharma', '9876543211', 'Father', 'rajesh@email.com', '2024-01-15', 1, 'A', 1, 'ACTIVE', 50000.00, 10000.00, 40000.00, false);
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Priya', 'Patel', 'STU20240002', '2010-08-20', 'FEMALE', 'priya.patel@student.com', '9876543220', 'Amit Patel', '9876543221', 'Amit Patel', '9876543221', 'Father', 'amit@email.com', '2024-01-16', 1, 'A', 2, 'ACTIVE', 50000.00, 50000.00, 0.00, false);
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Amit', 'Verma', 'STU20240003', '2010-03-10', 'MALE', 'amit.verma@student.com', '9876543230', 'Suresh Verma', '9876543231', 'Suresh Verma', '9876543231', 'Father', 'suresh@email.com', '2024-01-17', 2, 'B', 1, 'ACTIVE', 45000.00, 5000.00, 40000.00, false);
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Sneha', 'Gupta', 'STU20240004', '2010-07-25', 'FEMALE', 'sneha.gupta@student.com', '9876543240', 'Rakesh Gupta', '9876543241', 'Rakesh Gupta', '9876543241', 'Father', 'rakesh@email.com', '2024-01-20', 2, 'B', 2, 'ACTIVE', 50000.00, 25000.00, 25000.00, false);
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Arjun', 'Singh', 'STU20240005', '2011-02-14', 'MALE', 'arjun.singh@student.com', '9876543250', 'Vikram Singh', '9876543251', 'Vikram Singh', '9876543251', 'Father', 'vikram@email.com', '2024-02-01', 3, 'A', 1, 'ACTIVE', 40000.00, 0.00, 40000.00, false);

-- Verify insertion
SELECT 'CLASSES:' as type, COUNT(*) as count FROM school_classes WHERE is_deleted = false;
SELECT 'STUDENTS:' as type, COUNT(*) as count FROM students WHERE is_deleted = false;
SELECT CONCAT(first_name, ' ', last_name) as name, admission_number, fees_balance FROM students;
```

4. **Click Execute (⚡ icon or Ctrl+Shift+Enter)**

---

### **Method 2: Using Any MySQL GUI Tool**

- **HeidiSQL**
- **DBeaver**  
- **phpMyAdmin**
- **DataGrip**

Just connect to `localhost:3306`, database `user-master`, and run the SQL above.

---

### **Method 3: Quick Copy-Paste (Simplified)**

Just run these 8 commands one by one in your MySQL client:

```sql
USE `user-master`;
INSERT INTO school_classes VALUES (1, 'Class 10', 'A', 10, 40, 0, '101', '2024-2025', 'ACTIVE', false, NULL, NULL);
INSERT INTO school_classes VALUES (2, 'Class 10', 'B', 10, 40, 0, '102', '2024-2025', 'ACTIVE', false, NULL, NULL);
INSERT INTO school_classes VALUES (3, 'Class 9', 'A', 9, 35, 0, '201', '2024-2025', 'ACTIVE', false, NULL, NULL);

INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Rahul', 'Sharma', 'STU20240001', '2010-05-15', 'MALE', 'rahul@s.com', '9876543210', 'Rajesh', '9876543211', 'Rajesh', '9876543211', 'Father', 'r@e.com', '2024-01-15', 1, 'A', 1, 'ACTIVE', 50000, 10000, 40000, 0);
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Priya', 'Patel', 'STU20240002', '2010-08-20', 'FEMALE', 'priya@s.com', '9876543220', 'Amit', '9876543221', 'Amit', '9876543221', 'Father', 'a@e.com', '2024-01-16', 1, 'A', 2, 'ACTIVE', 50000, 50000, 0, 0);
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Amit', 'Verma', 'STU20240003', '2010-03-10', 'MALE', 'amit@s.com', '9876543230', 'Suresh', '9876543231', 'Suresh', '9876543231', 'Father', 's@e.com', '2024-01-17', 2, 'B', 1, 'ACTIVE', 45000, 5000, 40000, 0);
INSERT INTO students (first_name, last_name, admission_number, date_of_birth, gender, email, phone_number, father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email, admission_date, class_id, section, roll_number, status, total_fees, fees_paid, fees_balance, is_deleted) VALUES ('Sneha', 'Gupta', 'STU20240004', '2010-07-25', 'FEMALE', 'sneha@s.com', '9876543240', 'Rakesh', '9876543241', 'Rakesh', '9876543241', 'Father', 'rk@e.com', '2024-01-20', 2, 'B', 2, 'ACTIVE', 50000, 25000, 25000, 0);
```

---

## 🎯 **After Insertion:**

Once you've inserted the data, let me know and I'll run **ALL 17 ENDPOINT TESTS** to verify everything works perfectly!

---

**Please insert the data using one of these methods, then tell me "data inserted" and I'll test everything! 🚀**

