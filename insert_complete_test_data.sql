-- ========================================
-- SCHOOL MANAGEMENT SYSTEM - TEST DATA
-- ========================================
-- Created: January 2025
-- Purpose: Insert test data for Student Management testing
-- ========================================

-- Clean up existing test data (optional - comment out if you want to keep existing data)
-- DELETE FROM students WHERE admission_number LIKE 'STU2024%';
-- DELETE FROM school_classes WHERE id IN (1, 2, 3, 4);

-- ========================================
-- 1. INSERT SCHOOL CLASSES
-- ========================================

INSERT INTO school_classes (
    id, class_name, section, class_level, max_students, current_students, 
    room_number, description, academic_year, status, is_deleted,
    created_on, last_modified_on
) VALUES 
(1, 'Class 10', 'A', 10, 40, 0, '101', 'Science Section', '2024-2025', 'ACTIVE', false, NOW(), NOW()),
(2, 'Class 10', 'B', 10, 40, 0, '102', 'Commerce Section', '2024-2025', 'ACTIVE', false, NOW(), NOW()),
(3, 'Class 9', 'A', 9, 35, 0, '201', 'General Section', '2024-2025', 'ACTIVE', false, NOW(), NOW()),
(4, 'Class 11', 'A', 11, 30, 0, '301', 'Science Section', '2024-2025', 'ACTIVE', false, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    class_name = VALUES(class_name),
    section = VALUES(section),
    last_modified_on = NOW();

-- ========================================
-- 2. INSERT TEST STUDENTS
-- ========================================

-- Student 1: Rahul Kumar Sharma (Class 10-A) - WITH PENDING FEES
INSERT INTO students (
    first_name, middle_name, last_name, admission_number, date_of_birth, gender, blood_group,
    nationality, religion, email, phone_number, address, city, state, postal_code, country,
    father_name, father_phone, father_occupation, mother_name, mother_phone, mother_occupation,
    guardian_name, guardian_phone, guardian_relation, parent_email,
    admission_date, class_id, section, roll_number, status,
    medical_conditions, special_needs, notes, previous_school,
    total_fees, fees_paid, fees_balance, is_deleted,
    created_on, last_modified_on
) VALUES (
    'Rahul', 'Kumar', 'Sharma', 'STU20240001', '2010-05-15', 'MALE', 'O+',
    'Indian', 'Hindu', 'rahul.sharma@student.school.com', '9876543210',
    '123 MG Road, Apartment 4B', 'Mumbai', 'Maharashtra', '400001', 'India',
    'Rajesh Sharma', '9876543211', 'Engineer', 'Priya Sharma', '9876543212', 'Doctor',
    'Rajesh Sharma', '9876543211', 'Father', 'rajesh.sharma@email.com',
    '2024-01-15', 1, 'A', 1, 'ACTIVE',
    'None', 'None', 'Excellent student, good at mathematics', 'Delhi Public School',
    50000.00, 10000.00, 40000.00, false,
    NOW(), NOW()
) ON DUPLICATE KEY UPDATE last_modified_on = NOW();

-- Student 2: Priya Patel (Class 10-A) - FULLY PAID
INSERT INTO students (
    first_name, last_name, admission_number, date_of_birth, gender, blood_group,
    nationality, email, phone_number, city, state, postal_code, country,
    father_name, father_phone, mother_name, mother_phone,
    guardian_name, guardian_phone, guardian_relation, parent_email,
    admission_date, class_id, section, roll_number, status,
    total_fees, fees_paid, fees_balance, is_deleted,
    created_on, last_modified_on
) VALUES (
    'Priya', 'Patel', 'STU20240002', '2010-08-20', 'FEMALE', 'A+',
    'Indian', 'priya.patel@student.school.com', '9876543220',
    'Mumbai', 'Maharashtra', '400001', 'India',
    'Amit Patel', '9876543221', 'Meera Patel', '9876543222',
    'Amit Patel', '9876543221', 'Father', 'amit.patel@email.com',
    '2024-01-16', 1, 'A', 2, 'ACTIVE',
    50000.00, 50000.00, 0.00, false,
    NOW(), NOW()
) ON DUPLICATE KEY UPDATE last_modified_on = NOW();

-- Student 3: Amit Verma (Class 10-B) - WITH PENDING FEES
INSERT INTO students (
    first_name, last_name, admission_number, date_of_birth, gender, blood_group,
    nationality, email, phone_number, city, state, country,
    father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email,
    admission_date, class_id, section, roll_number, status,
    total_fees, fees_paid, fees_balance, is_deleted,
    created_on, last_modified_on
) VALUES (
    'Amit', 'Verma', 'STU20240003', '2010-03-10', 'MALE', 'B+',
    'Indian', 'amit.verma@student.school.com', '9876543230',
    'Mumbai', 'Maharashtra', 'India',
    'Suresh Verma', '9876543231', 'Suresh Verma', '9876543231', 'Father', 'suresh.verma@email.com',
    '2024-01-17', 2, 'B', 1, 'ACTIVE',
    45000.00, 5000.00, 40000.00, false,
    NOW(), NOW()
) ON DUPLICATE KEY UPDATE last_modified_on = NOW();

-- Student 4: Sneha Gupta (Class 10-B) - PARTIAL PAYMENT
INSERT INTO students (
    first_name, last_name, admission_number, date_of_birth, gender, blood_group,
    email, phone_number, city, country,
    father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email,
    admission_date, class_id, section, roll_number, status,
    total_fees, fees_paid, fees_balance, is_deleted,
    created_on, last_modified_on
) VALUES (
    'Sneha', 'Gupta', 'STU20240004', '2010-07-25', 'FEMALE', 'AB+',
    'sneha.gupta@student.school.com', '9876543240',
    'Delhi', 'India',
    'Rakesh Gupta', '9876543241', 'Rakesh Gupta', '9876543241', 'Father', 'rakesh.gupta@email.com',
    '2024-01-20', 2, 'B', 2, 'ACTIVE',
    50000.00, 25000.00, 25000.00, false,
    NOW(), NOW()
) ON DUPLICATE KEY UPDATE last_modified_on = NOW();

-- Student 5: Arjun Singh (Class 9-A) - NEW ADMISSION, PENDING FEES
INSERT INTO students (
    first_name, last_name, admission_number, date_of_birth, gender, blood_group,
    email, phone_number, city, country,
    father_name, father_phone, guardian_name, guardian_phone, guardian_relation, parent_email,
    admission_date, class_id, section, roll_number, status,
    total_fees, fees_paid, fees_balance, is_deleted,
    created_on, last_modified_on
) VALUES (
    'Arjun', 'Singh', 'STU20240005', '2011-02-14', 'MALE', 'O-',
    'arjun.singh@student.school.com', '9876543250',
    'Bangalore', 'India',
    'Vikram Singh', '9876543251', 'Vikram Singh', '9876543251', 'Father', 'vikram.singh@email.com',
    '2024-02-01', 3, 'A', 1, 'ACTIVE',
    40000.00, 0.00, 40000.00, false,
    NOW(), NOW()
) ON DUPLICATE KEY UPDATE last_modified_on = NOW();

-- ========================================
-- 3. VERIFY DATA INSERTION
-- ========================================

-- Check classes
SELECT 'CLASSES INSERTED:' as info;
SELECT id, class_name, section, room_number, status FROM school_classes WHERE is_deleted = false;

-- Check students
SELECT 'STUDENTS INSERTED:' as info;
SELECT 
    id, 
    CONCAT(first_name, ' ', last_name) as full_name, 
    admission_number, 
    class_id,
    section,
    total_fees, 
    fees_paid, 
    fees_balance,
    status 
FROM students 
WHERE is_deleted = false 
ORDER BY admission_number;

-- Check pending fees summary
SELECT 'PENDING FEES SUMMARY:' as info;
SELECT 
    COUNT(*) as total_students,
    SUM(fees_balance) as total_pending_fees,
    AVG(fees_balance) as avg_pending_fees
FROM students 
WHERE fees_balance > 0 AND is_deleted = false;

-- ========================================
-- SUCCESS MESSAGE
-- ========================================
SELECT '✅ TEST DATA INSERTED SUCCESSFULLY!' as message;
SELECT '📊 Summary: 4 Classes + 5 Students created' as summary;
SELECT '💰 Students with pending fees: 4 students' as fees_info;
SELECT '✅ Fully paid students: 1 student (Priya Patel)' as paid_info;

