-- Quick Fix: Disable foreign key checks temporarily
-- Run this in MySQL to temporarily disable foreign key constraints

SET FOREIGN_KEY_CHECKS = 0;

-- Now you can start your application
-- The foreign key constraints will be ignored temporarily

-- After the application starts successfully, you can re-enable them:
-- SET FOREIGN_KEY_CHECKS = 1;
