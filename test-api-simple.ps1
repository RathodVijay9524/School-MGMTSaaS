Start-Sleep -Seconds 15

try {
    $response = Invoke-WebRequest -Uri 'http://localhost:9091/api/users/filter?pageNumber=0&pageSize=5' -Method GET
    Write-Host "Server is running! Status: $($response.StatusCode)"
} catch {
    Write-Host "Server error: $($_.Exception.Message)"
}
