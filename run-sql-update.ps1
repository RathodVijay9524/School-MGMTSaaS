# PowerShell script to update student class via direct SQL execution
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  UPDATING STUDENT CLASS FROM 1 TO 54" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

# MySQL connection details
$server = "localhost"
$port = "3306"
$database = "school_db"
$user = "root"
$password = "root"

# SQL Query
$sqlQuery = @"
USE school_db;
SELECT 'BEFORE UPDATE:' AS Status, id, name, username, current_class_id FROM workers WHERE id = 53;
UPDATE workers SET current_class_id = 54 WHERE id = 53;
SELECT 'AFTER UPDATE:' AS Status, id, name, username, current_class_id FROM workers WHERE id = 53;
SELECT CASE WHEN current_class_id = 54 THEN 'SUCCESS: Student class updated to 54' ELSE 'FAILED: Student class not updated' END AS Result FROM workers WHERE id = 53;
"@

Write-Host "`nAttempting to connect to MySQL database..." -ForegroundColor Yellow
Write-Host "Server: $server" -ForegroundColor Gray
Write-Host "Database: $database" -ForegroundColor Gray
Write-Host "User: $user" -ForegroundColor Gray

try {
    # Using MySQL .NET Connector (if available)
    Add-Type -Path "C:\Program Files (x86)\MySQL\MySQL Connector NET 8.0\Assemblies\v4.5.2\MySql.Data.dll" -ErrorAction SilentlyContinue
    
    $connectionString = "Server=$server;Port=$port;Database=$database;Uid=$user;Pwd=$password;SslMode=None;"
    $connection = New-Object MySql.Data.MySqlClient.MySqlConnection($connectionString)
    $connection.Open()
    
    Write-Host "`nConnection successful!" -ForegroundColor Green
    
    $command = $connection.CreateCommand()
    $command.CommandText = "SELECT id, name, username, current_class_id FROM workers WHERE id = 53;"
    $reader = $command.ExecuteReader()
    
    Write-Host "`n--- BEFORE UPDATE ---" -ForegroundColor Yellow
    while ($reader.Read()) {
        Write-Host ("ID: {0}, Name: {1}, Username: {2}, Current Class: {3}" -f $reader["id"], $reader["name"], $reader["username"], $reader["current_class_id"]) -ForegroundColor White
    }
    $reader.Close()
    
    # Execute UPDATE
    $command.CommandText = "UPDATE workers SET current_class_id = 54 WHERE id = 53;"
    $rowsAffected = $command.ExecuteNonQuery()
    Write-Host "`nRows affected: $rowsAffected" -ForegroundColor Cyan
    
    # Check AFTER
    $command.CommandText = "SELECT id, name, username, current_class_id FROM workers WHERE id = 53;"
    $reader = $command.ExecuteReader()
    
    Write-Host "`n--- AFTER UPDATE ---" -ForegroundColor Green
    while ($reader.Read()) {
        Write-Host ("ID: {0}, Name: {1}, Username: {2}, Current Class: {3}" -f $reader["id"], $reader["name"], $reader["username"], $reader["current_class_id"]) -ForegroundColor White
        if ($reader["current_class_id"] -eq 54) {
            Write-Host "`n SUCCESS: Student class updated to 54!" -ForegroundColor Green
        }
    }
    $reader.Close()
    
    $connection.Close()
    
} catch {
    Write-Host "`nMySQL .NET Connector not found. Using alternative method..." -ForegroundColor Yellow
    
    # Try to find MySQL executable
    $mysqlPaths = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
        "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\xampp\mysql\bin\mysql.exe",
        "C:\wamp64\bin\mysql\mysql8.0.27\bin\mysql.exe"
    )
    
    $mysqlPath = $null
    foreach ($path in $mysqlPaths) {
        if (Test-Path $path) {
            $mysqlPath = $path
            Write-Host "Found MySQL at: $path" -ForegroundColor Green
            break
        }
    }
    
    if ($mysqlPath) {
        # Create temp SQL file
        $tempSqlFile = Join-Path $env:TEMP "update_student.sql"
        $sqlQuery | Out-File -FilePath $tempSqlFile -Encoding ASCII
        
        # Execute SQL file
        Write-Host "`nExecuting SQL update..." -ForegroundColor Yellow
        & $mysqlPath -u $user -p$password -D $database -e $sqlQuery
        
        Remove-Item $tempSqlFile -ErrorAction SilentlyContinue
    } else {
        Write-Host "`nERROR: Could not find MySQL executable!" -ForegroundColor Red
        Write-Host "Please run the SQL script manually in MySQL Workbench:" -ForegroundColor Yellow
        Write-Host "UPDATE workers SET current_class_id = 54 WHERE id = 53;" -ForegroundColor White
    }
}

Write-Host "`n==================================================" -ForegroundColor Cyan
Write-Host "  UPDATE COMPLETE" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

