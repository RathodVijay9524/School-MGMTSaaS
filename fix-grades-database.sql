-- Fix grades with null status values in the database
USE school_db;

-- Check for grades with null status
SELECT 'BEFORE FIX:' AS Status, COUNT(*) as null_status_count FROM grades WHERE status IS NULL;

-- Update null status to PENDING for existing grades
UPDATE grades 
SET status = 'PENDING' 
WHERE status IS NULL;

-- Verify the fix
SELECT 'AFTER FIX:' AS Status, COUNT(*) as remaining_null_count FROM grades WHERE status IS NULL;

-- Show sample of grades
SELECT id, student_id, subject_id, status, letter_grade, marks_obtained, total_marks 
FROM grades 
WHERE student_id = 53 
ORDER BY grade_date DESC 
LIMIT 5;
