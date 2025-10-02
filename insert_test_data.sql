-- Insert a test class for testing Student Management
INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, room_number, academic_year, status, is_deleted) 
VALUES (1, 'Class 10', 'A', 10, 40, 0, '101', '2024-2025', 'ACTIVE', false)
ON CONFLICT (id) DO NOTHING;

-- Insert another class
INSERT INTO school_classes (id, class_name, section, class_level, max_students, current_students, room_number, academic_year, status, is_deleted) 
VALUES (2, 'Class 10', 'B', 10, 40, 0, '102', '2024-2025', 'ACTIVE', false)
ON CONFLICT (id) DO NOTHING;

-- Check if classes were inserted
SELECT * FROM school_classes;


