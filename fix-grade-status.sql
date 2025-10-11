-- Fix null grade status values
USE school_db;

-- Check for grades with null status
SELECT COUNT(*) as null_status_count FROM grades WHERE status IS NULL;

-- Update null status to PENDING
UPDATE grades 
SET status = 'PENDING' 
WHERE status IS NULL;

-- Verify the fix
SELECT COUNT(*) as remaining_null_count FROM grades WHERE status IS NULL;

-- Show sample of updated grades
SELECT id, student_id, subject_id, status, letter_grade FROM grades LIMIT 5;
