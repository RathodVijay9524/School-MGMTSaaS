-- Insert ROLE_PARENT directly into database
-- Run this SQL in your database management tool (MySQL Workbench, phpMyAdmin, etc.)

INSERT INTO roles (name, is_active, is_deleted, created_on, updated_on) 
VALUES ('ROLE_PARENT', true, false, NOW(), NOW());

-- Verify the role was created
SELECT * FROM roles WHERE name = 'ROLE_PARENT';
