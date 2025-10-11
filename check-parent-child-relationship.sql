-- Check parent-child relationship
USE school_db;

-- Check parent details
SELECT 'PARENT DETAILS:' AS Info, id, name, username, email FROM workers WHERE username = 'vj.parent29';

-- Check student details  
SELECT 'STUDENT DETAILS:' AS Info, id, name, username, email, parent_email FROM workers WHERE username = 'vj.student29.cls9';

-- Check if student's parent_email matches parent's email
SELECT 
    'RELATIONSHIP CHECK:' AS Info,
    s.name AS student_name,
    s.email AS student_email,
    s.parent_email AS student_parent_email,
    p.name AS parent_name,
    p.email AS parent_email,
    CASE 
        WHEN s.parent_email = p.email THEN 'CORRECT' 
        ELSE 'MISMATCH' 
    END AS relationship_status
FROM workers s
JOIN workers p ON s.parent_email = p.email
WHERE s.username = 'vj.student29.cls9' AND p.username = 'vj.parent29';
