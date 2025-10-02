-- Database Cleanup Script for Foreign Key Constraint Issues
-- This script will clean up invalid owner_id references

-- Step 1: Check what users exist
SELECT id, username, email FROM users;

-- Step 2: Check what records have invalid owner_id
SELECT 'school_classes' as table_name, COUNT(*) as invalid_records 
FROM school_classes 
WHERE owner_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'subjects' as table_name, COUNT(*) as invalid_records 
FROM subjects 
WHERE owner_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'attendance' as table_name, COUNT(*) as invalid_records 
FROM attendance 
WHERE owner_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'grades' as table_name, COUNT(*) as invalid_records 
FROM grades 
WHERE owner_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'fees' as table_name, COUNT(*) as invalid_records 
FROM fees 
WHERE owner_id NOT IN (SELECT id FROM users);

-- Step 3: Delete records with invalid owner_id (CAREFUL!)
-- Uncomment these lines only after reviewing the counts above

-- DELETE FROM school_classes WHERE owner_id NOT IN (SELECT id FROM users);
-- DELETE FROM subjects WHERE owner_id NOT IN (SELECT id FROM users);
-- DELETE FROM attendance WHERE owner_id NOT IN (SELECT id FROM users);
-- DELETE FROM grades WHERE owner_id NOT IN (SELECT id FROM users);
-- DELETE FROM fees WHERE owner_id NOT IN (SELECT id FROM users);

-- Step 4: Alternative - Set invalid owner_id to NULL (if column allows NULL)
-- UPDATE school_classes SET owner_id = NULL WHERE owner_id NOT IN (SELECT id FROM users);
-- UPDATE subjects SET owner_id = NULL WHERE owner_id NOT IN (SELECT id FROM users);
-- UPDATE attendance SET owner_id = NULL WHERE owner_id NOT IN (SELECT id FROM users);
-- UPDATE grades SET owner_id = NULL WHERE owner_id NOT IN (SELECT id FROM users);
-- UPDATE fees SET owner_id = NULL WHERE owner_id NOT IN (SELECT id FROM users);

-- Step 5: Create a default user if none exists
-- INSERT INTO users (id, username, email, password, name, pho_no, is_deleted, created_by, created_on, updated_by, updated_on) 
-- VALUES (1, 'karina', 'karina@example.com', '$2a$10$encrypted_password', 'Karina Admin', '1234567890', false, 1, NOW(), 1, NOW())
-- ON DUPLICATE KEY UPDATE username = username;

-- Step 6: Update all records to use the default user
-- UPDATE school_classes SET owner_id = 1 WHERE owner_id IS NULL OR owner_id NOT IN (SELECT id FROM users);
-- UPDATE subjects SET owner_id = 1 WHERE owner_id IS NULL OR owner_id NOT IN (SELECT id FROM users);
-- UPDATE attendance SET owner_id = 1 WHERE owner_id IS NULL OR owner_id NOT IN (SELECT id FROM users);
-- UPDATE grades SET owner_id = 1 WHERE owner_id IS NULL OR owner_id NOT IN (SELECT id FROM users);
-- UPDATE fees SET owner_id = 1 WHERE owner_id IS NULL OR owner_id NOT IN (SELECT id FROM users);
