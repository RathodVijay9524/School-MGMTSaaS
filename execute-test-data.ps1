# PowerShell Script to Insert Test Data into MySQL
# Run this script to populate your database with test data

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MYSQL TEST DATA INSERTION SCRIPT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# MySQL Connection Details (Update these with your actual details)
$mysqlHost = "localhost"
$mysqlPort = "3306"
$mysqlDatabase = "user_master"
$mysqlUser = "root"
$mysqlPassword = "password"  # UPDATE THIS WITH YOUR ACTUAL PASSWORD

# Path to SQL file
$sqlFile = "insert_complete_test_data.sql"

Write-Host "📋 Configuration:" -ForegroundColor Yellow
Write-Host "   Host: $mysqlHost" -ForegroundColor Gray
Write-Host "   Port: $mysqlPort" -ForegroundColor Gray
Write-Host "   Database: $mysqlDatabase" -ForegroundColor Gray
Write-Host "   User: $mysqlUser" -ForegroundColor Gray
Write-Host "   SQL File: $sqlFile" -ForegroundColor Gray
Write-Host ""

# Check if MySQL is accessible
Write-Host "🔍 Checking MySQL connection..." -ForegroundColor Yellow

try {
    # Test MySQL connection
    $testQuery = "SELECT 1"
    $mysqlCmd = "mysql -h$mysqlHost -P$mysqlPort -u$mysqlUser -p$mysqlPassword -D$mysqlDatabase -e `"$testQuery`" 2>&1"
    
    $result = Invoke-Expression $mysqlCmd
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ MySQL connection successful!" -ForegroundColor Green
        Write-Host ""
        
        # Execute SQL file
        Write-Host "📝 Executing SQL file..." -ForegroundColor Yellow
        $executeCmd = "mysql -h$mysqlHost -P$mysqlPort -u$mysqlUser -p$mysqlPassword -D$mysqlDatabase < $sqlFile"
        
        Invoke-Expression $executeCmd
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "✅ TEST DATA INSERTED SUCCESSFULLY!" -ForegroundColor Green
            Write-Host ""
            Write-Host "📊 Data Created:" -ForegroundColor Cyan
            Write-Host "   • 4 School Classes (Class 9-A, 10-A, 10-B, 11-A)" -ForegroundColor Gray
            Write-Host "   • 5 Test Students" -ForegroundColor Gray
            Write-Host "     - Rahul Kumar Sharma (STU20240001) - ₹40,000 pending" -ForegroundColor Gray
            Write-Host "     - Priya Patel (STU20240002) - Fully paid" -ForegroundColor Gray
            Write-Host "     - Amit Verma (STU20240003) - ₹40,000 pending" -ForegroundColor Gray
            Write-Host "     - Sneha Gupta (STU20240004) - ₹25,000 pending" -ForegroundColor Gray
            Write-Host "     - Arjun Singh (STU20240005) - ₹40,000 pending" -ForegroundColor Gray
            Write-Host ""
            Write-Host "🎉 You can now test all Student Management endpoints!" -ForegroundColor Green
            Write-Host ""
        } else {
            Write-Host "❌ Failed to execute SQL file!" -ForegroundColor Red
            Write-Host "Error code: $LASTEXITCODE" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ MySQL connection failed!" -ForegroundColor Red
        Write-Host ""
        Write-Host "Please check:" -ForegroundColor Yellow
        Write-Host "1. MySQL is running" -ForegroundColor Gray
        Write-Host "2. Connection details are correct" -ForegroundColor Gray
        Write-Host "3. User has permissions on database" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Alternative: Run SQL manually" -ForegroundColor Yellow
    Write-Host "1. Open MySQL Workbench or command line" -ForegroundColor Gray
    Write-Host "2. Connect to your database" -ForegroundColor Gray
    Write-Host "3. Run: SOURCE insert_complete_test_data.sql" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

