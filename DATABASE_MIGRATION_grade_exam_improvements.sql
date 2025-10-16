-- ============================================================================
-- DATABASE MIGRATION: Grade & Exam Entity Improvements
-- ============================================================================
-- This script adds:
-- 1. GPA calculation fields to grades table
-- 2. Class rank fields to grades table  
-- 3. Creates examiners table for multiple examiners per exam
-- ============================================================================

USE school_db;

-- ============================================================================
-- 1. ADD GPA CALCULATION FIELDS TO GRADES TABLE
-- ============================================================================

ALTER TABLE grades 
  ADD COLUMN grade_point DOUBLE COMMENT 'Numeric grade point (e.g., 4.0, 3.7, 3.3)';

ALTER TABLE grades 
  ADD COLUMN gpa_value DOUBLE COMMENT 'GPA for this specific subject/grade';

ALTER TABLE grades 
  ADD COLUMN cumulative_gpa DOUBLE COMMENT 'Cumulative GPA including all previous semesters';

ALTER TABLE grades 
  ADD COLUMN gpa_scale VARCHAR(50) COMMENT 'GPA scale (e.g., "4.0", "5.0", "10.0")';

-- ============================================================================
-- 2. ADD CLASS RANK FIELDS TO GRADES TABLE
-- ============================================================================

ALTER TABLE grades 
  ADD COLUMN class_rank INT COMMENT 'Student rank in class (1 = highest)';

ALTER TABLE grades 
  ADD COLUMN total_students INT COMMENT 'Total number of students in class/cohort';

ALTER TABLE grades 
  ADD COLUMN percentile DOUBLE COMMENT 'Percentile rank (0-100, where 100 is best)';

ALTER TABLE grades 
  ADD COLUMN section_rank INT COMMENT 'Rank within section';

ALTER TABLE grades 
  ADD COLUMN grade_rank INT COMMENT 'Rank within entire grade level';

-- ============================================================================
-- 3. CREATE EXAMINERS TABLE (Multiple Examiners per Exam)
-- ============================================================================

CREATE TABLE examiners (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exam_id BIGINT NOT NULL COMMENT 'Reference to exam',
  teacher_id BIGINT COMMENT 'Internal teacher examiner (can be null for external)',
  
  -- External Examiner Fields
  external_examiner_name VARCHAR(200) COMMENT 'Name of external examiner',
  external_examiner_email VARCHAR(200) COMMENT 'Email of external examiner',
  external_examiner_phone VARCHAR(20) COMMENT 'Phone of external examiner',
  institution VARCHAR(200) COMMENT 'Institution/organization of external examiner',
  
  -- Examiner Role & Status
  role VARCHAR(50) NOT NULL COMMENT 'PRIMARY, SECONDARY, EXTERNAL, MODERATOR, CO_EXAMINER',
  status VARCHAR(50) NOT NULL DEFAULT 'ASSIGNED' COMMENT 'ASSIGNED, IN_PROGRESS, COMPLETED, PENDING',
  
  -- Tracking
  assigned_date DATE COMMENT 'Date examiner was assigned',
  completion_date DATE COMMENT 'Date examiner completed grading',
  specialization VARCHAR(1000) COMMENT 'Subject specialization/expertise',
  is_blind_grading BOOLEAN DEFAULT FALSE COMMENT 'Whether this examiner grades anonymously',
  remarks VARCHAR(500) COMMENT 'Additional remarks',
  
  -- Multi-tenancy & Soft Delete
  owner_id BIGINT NOT NULL COMMENT 'Owner for multi-tenancy',
  is_deleted BOOLEAN DEFAULT FALSE,
  
  -- Audit fields (inherited from BaseModel)
  created_on DATETIME,
  created_by VARCHAR(255),
  updated_on DATETIME,
  
  -- Foreign Keys
  CONSTRAINT fk_examiner_exam FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
  CONSTRAINT fk_examiner_teacher FOREIGN KEY (teacher_id) REFERENCES workers(id) ON DELETE SET NULL,
  CONSTRAINT fk_examiner_owner FOREIGN KEY (owner_id) REFERENCES users(id),
  
  -- Indexes
  INDEX idx_examiner_exam_id (exam_id),
  INDEX idx_examiner_teacher_id (teacher_id),
  INDEX idx_examiner_owner_id (owner_id),
  INDEX idx_examiner_role (role),
  INDEX idx_examiner_status (status),
  INDEX idx_examiner_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Examiners table - supports multiple examiners per exam';

-- ============================================================================
-- 4. VERIFICATION QUERIES
-- ============================================================================

-- Check grades table new columns
SELECT 'Grades Table - New Columns:' AS info;
SHOW COLUMNS FROM grades WHERE Field IN ('grade_point', 'gpa_value', 'cumulative_gpa', 'gpa_scale', 'class_rank', 'total_students', 'percentile', 'section_rank', 'grade_rank');

-- Check examiners table
SELECT 'Examiners Table Created:' AS info;
SHOW TABLES LIKE 'examiners';

SELECT 'Examiners Table Structure:' AS info;
DESCRIBE examiners;

-- ============================================================================
-- MIGRATION COMPLETE
-- ============================================================================

SELECT '✅ Migration completed successfully!' AS status;
