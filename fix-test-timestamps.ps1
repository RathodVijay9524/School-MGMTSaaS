# Fix all timestamps references in test files
$testDir = "d:\Live Project -2025-Jul\Deployement\SchoolManagments\School-MGMTSaaS\src\test\java\com\vijay\User_Master\service\manager"

Write-Host "Fixing timestamps in test files..." -ForegroundColor Cyan

Get-ChildItem -Path $testDir -Filter "*Test.java" | ForEach-Object {
    $file = $_.FullName
    $content = Get-Content $file -Raw
    
    # Pattern 1: .timestamps(new java.util.HashMap<>())
    $pattern1 = '\.timestamps\(new java\.util\.HashMap<>\(\)\)'
    if ($content -match $pattern1) {
        Write-Host "Fixing $($_.Name) - Pattern 1" -ForegroundColor Yellow
        $content = $content -replace $pattern1, ''
    }
    
    # Pattern 2: Remove trailing comma before .build()
    $pattern2 = ',\s*\.build\(\)'
    if ($content -match $pattern2) {
        Write-Host "Fixing $($_.Name) - Pattern 2" -ForegroundColor Yellow
        $content = $content -replace $pattern2, '.build()'
    }
    
    # Write back
    Set-Content -Path $file -Value $content
    Write-Host "✅ Fixed: $($_.Name)" -ForegroundColor Green
}

Write-Host ""
Write-Host "✅ All test files fixed!" -ForegroundColor Green
