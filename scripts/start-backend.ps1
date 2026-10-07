$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$backendRoot = Join-Path $projectRoot 'backend'
$envFile = Join-Path $backendRoot '.env'
if (-not (Test-Path -LiteralPath $envFile)) { throw 'Copy backend/.env.example to backend/.env and set your local credentials first.' }
foreach ($line in Get-Content -LiteralPath $envFile) {
    if ($line.Trim() -eq '' -or $line.TrimStart().StartsWith('#')) { continue }
    $pair = $line -split '=', 2
    if ($pair.Length -eq 2 -and $pair[0] -match '^[A-Z][A-Z0-9_]*$') {
        [Environment]::SetEnvironmentVariable($pair[0], $pair[1].Trim().Trim('"').Trim("'"), 'Process')
    }
}
Push-Location $backendRoot
try { & .\mvnw.cmd spring-boot:run } finally { Pop-Location }
