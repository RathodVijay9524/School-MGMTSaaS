USE school_db;

-- Show current student class before update
SELECT id, name, username, current_class_id FROM workers WHERE id = 53;

-- Update student to correct class (54 where the data exists)
UPDATE workers 
SET current_class_id = 54 
WHERE id = 53;

-- Show updated student class
SELECT id, name, username, current_class_id FROM workers WHERE id = 53;

-- Verify assignments exist for class 54
SELECT COUNT(*) as assignment_count FROM assignments WHERE class_id = 54 AND owner_id = 27 AND is_deleted = 0;

-- Verify timetable exists for class 54
SELECT COUNT(*) as timetable_count FROM timetables WHERE class_id = 54 AND owner_id = 27 AND is_deleted = 0;

-- Verify subjects exist for owner 27
SELECT COUNT(*) as subject_count FROM subjects WHERE owner_id = 27 AND is_deleted = 0;