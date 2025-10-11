-- Fix student class assignment to match the actual data
-- Student: Advait Desai (ID: 53) 
-- Update from class 1 to class 54 where the actual data exists

-- Check current student data
SELECT id, name, username, current_class_id FROM workers WHERE id = 53;

-- Check what classes exist for this school owner
SELECT id, class_name, grade_level, owner_id FROM school_classes WHERE owner_id = 27;

-- Check assignments - which class do they belong to?
SELECT id, title, class_id, owner_id FROM assignments WHERE owner_id = 27;

-- Check timetable - which class does it belong to?
SELECT id, day_of_week, class_id, owner_id FROM timetables WHERE owner_id = 27 AND is_deleted = FALSE LIMIT 5;

-- Update student to correct class (54)
UPDATE workers 
SET current_class_id = 54 
WHERE id = 53;

-- Verify the update
SELECT id, name, username, current_class_id FROM workers WHERE id = 53;

