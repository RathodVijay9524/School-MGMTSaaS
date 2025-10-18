-- Create Sample Data for LMS Testing
-- This script creates subjects and classes needed for testing LMS features

USE school_db;

-- Check if sample data already exists
SET @subject_count = (SELECT COUNT(*) FROM subjects WHERE id = 1);
SET @class_count = (SELECT COUNT(*) FROM school_classes WHERE id = 1);

-- Insert sample subject if not exists
INSERT INTO subjects (id, subject_name, description, created_at, updated_at, is_deleted)
SELECT 1, 'Mathematics', 'Mathematics subject for testing', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE id = 1);

INSERT INTO subjects (id, subject_name, description, created_at, updated_at, is_deleted)
SELECT 2, 'Science', 'Science subject for testing', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE id = 2);

INSERT INTO subjects (id, subject_name, description, created_at, updated_at, is_deleted)
SELECT 3, 'English', 'English subject for testing', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE id = 3);

-- Insert sample class if not exists  
INSERT INTO school_classes (id, class_name, grade_level, section, created_at, updated_at, is_deleted)
SELECT 1, 'Grade 10 A', 10, 'A', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM school_classes WHERE id = 1);

INSERT INTO school_classes (id, class_name, grade_level, section, created_at, updated_at, is_deleted)
SELECT 2, 'Grade 9 B', 9, 'B', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM school_classes WHERE id = 2);

-- Verify data
SELECT 'Subjects Created:' AS Info;
SELECT id, subject_name, description FROM subjects WHERE is_deleted = 0 LIMIT 5;

SELECT 'Classes Created:' AS Info;
SELECT id, class_name, grade_level, section FROM school_classes WHERE is_deleted = 0 LIMIT 5;

SELECT '✅ Sample data created successfully!' AS Status;
