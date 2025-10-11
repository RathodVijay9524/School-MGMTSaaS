-- =====================================================
-- FIX STUDENT CLASS ASSIGNMENT
-- Database: school_db
-- User: root / Password: root
-- =====================================================

USE school_db;

-- Show current student data (BEFORE)
SELECT 
    id, 
    name, 
    username, 
    current_class_id AS 'Current Class',
    parent_email,
    roll_number
FROM workers 
WHERE id = 53;

-- Check what classes exist for this owner
SELECT 
    id AS 'Class ID',
    class_name AS 'Class Name',
    grade_level AS 'Grade',
    owner_id AS 'Owner'
FROM school_classes 
WHERE owner_id = 27 
ORDER BY id;

-- Check what class the assignment belongs to
SELECT 
    id AS 'Assignment ID',
    title,
    class_id AS 'Class ID',
    owner_id AS 'Owner'
FROM assignments 
WHERE owner_id = 27 
  AND is_deleted = FALSE;

-- Check what class the timetable belongs to
SELECT 
    id AS 'Timetable ID',
    day_of_week,
    class_id AS 'Class ID',
    owner_id AS 'Owner'
FROM timetables 
WHERE owner_id = 27 
  AND is_deleted = FALSE 
LIMIT 5;

-- =====================================================
-- UPDATE STUDENT TO CORRECT CLASS (54)
-- =====================================================

UPDATE workers 
SET current_class_id = 54 
WHERE id = 53;

-- Show updated student data (AFTER)
SELECT 
    id, 
    name, 
    username, 
    current_class_id AS 'Current Class (UPDATED)',
    parent_email,
    roll_number
FROM workers 
WHERE id = 53;

-- Verify the change
SELECT 
    CASE 
        WHEN current_class_id = 54 THEN '✓ SUCCESS: Student class updated to 54'
        ELSE '✗ FAILED: Student class not updated'
    END AS 'Status'
FROM workers 
WHERE id = 53;

