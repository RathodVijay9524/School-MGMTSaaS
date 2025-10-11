-- Fix student class assignment to match where data exists
USE school_db;

-- Check current student class
SELECT 'BEFORE UPDATE:' AS Status, id, name, username, current_class_id FROM workers WHERE id = 53;

-- Update student to class 54 (where assignments and timetable data exists)
UPDATE workers SET current_class_id = 54 WHERE id = 53;

-- Verify the update
SELECT 'AFTER UPDATE:' AS Status, id, name, username, current_class_id FROM workers WHERE id = 53;

-- Check if assignments exist for class 54
SELECT 'ASSIGNMENTS FOR CLASS 54:' AS Info, COUNT(*) as assignment_count FROM assignments WHERE class_id = 54 AND is_deleted = 0;

-- Check if timetable exists for class 54  
SELECT 'TIMETABLE FOR CLASS 54:' AS Info, COUNT(*) as timetable_count FROM timetables WHERE class_id = 54 AND is_deleted = 0;
