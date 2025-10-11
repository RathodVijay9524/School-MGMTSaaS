# PowerShell script to run SQL update for student class ID
Write-Host "🔧 Running SQL update to fix student class ID..." -ForegroundColor Yellow

# Run the SQL script using MySQL Workbench or command line
# Since mysql command is not available in PATH, we'll provide instructions

Write-Host "📋 Please run the following SQL commands in MySQL Workbench:" -ForegroundColor Cyan
Write-Host ""
Write-Host "USE school_db;" -ForegroundColor Green
Write-Host ""
Write-Host "-- Show current student class before update" -ForegroundColor Gray
Write-Host "SELECT id, name, username, current_class_id FROM workers WHERE id = 53;" -ForegroundColor Green
Write-Host ""
Write-Host "-- Update student to correct class (54 where the data exists)" -ForegroundColor Gray
Write-Host "UPDATE workers SET current_class_id = 54 WHERE id = 53;" -ForegroundColor Green
Write-Host ""
Write-Host "-- Show updated student class" -ForegroundColor Gray
Write-Host "SELECT id, name, username, current_class_id FROM workers WHERE id = 53;" -ForegroundColor Green
Write-Host ""
Write-Host "-- Verify data exists for class 54" -ForegroundColor Gray
Write-Host "SELECT COUNT(*) as assignment_count FROM assignments WHERE class_id = 54 AND owner_id = 27 AND is_deleted = 0;" -ForegroundColor Green
Write-Host "SELECT COUNT(*) as timetable_count FROM timetables WHERE class_id = 54 AND owner_id = 27 AND is_deleted = 0;" -ForegroundColor Green
Write-Host "SELECT COUNT(*) as subject_count FROM subjects WHERE owner_id = 27 AND is_deleted = 0;" -ForegroundColor Green
Write-Host ""
Write-Host "✅ After running these SQL commands, restart the application and test the student portal." -ForegroundColor Yellow