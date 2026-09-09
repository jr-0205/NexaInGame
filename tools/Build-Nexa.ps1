param(
    [ValidateSet('all', '1.21.1', '1.21.8', '1.21.10')]
    [string]$Target = 'all',
    [switch]$Clean
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$wrapper = Join-Path $repoRoot 'gradlew.bat'

$candidates = @(
    'C:\Program Files\Java\jdk-21.0.10',
    'C:\Program Files\Java\jdk-21',
    'C:\Program Files\Eclipse Adoptium\jdk-21*',
    'C:\Program Files\Microsoft\jdk-21*'
)

$jdk = $null
foreach ($candidate in $candidates) {
    $match = Get-Item -Path $candidate -ErrorAction SilentlyContinue |
        Where-Object { Test-Path (Join-Path $_.FullName 'bin\java.exe') } |
        Select-Object -First 1
    if ($match) { $jdk = $match.FullName; break }
}

if (-not $jdk) {
    throw 'NEXA In-Game requiere un JDK 21. Instala Java 21 o agrega su ruta a tools/Build-Nexa.ps1.'
}

# These changes affect only this build process and its child Gradle process.
$env:JAVA_HOME = $jdk
$env:Path = (Join-Path $jdk 'bin') + [IO.Path]::PathSeparator + $env:Path

# java -version writes its version banner to STDERR by design. Invoke it through
# cmd.exe so PowerShell 5.1 does not turn that normal STDERR output into a
# NativeCommandError while $ErrorActionPreference is set to Stop.
$javaExe = Join-Path $jdk 'bin\java.exe'
$major = (& cmd.exe /d /c "`"$javaExe`" -version 2^>^&1" | Select-Object -First 1)
if (-not $major) { $major = 'Java 21' }
Write-Host "NEXA build runtime: $major" -ForegroundColor Cyan

$tasks = [System.Collections.Generic.List[string]]::new()
if ($Clean) { $tasks.Add('clean') }
if ($Target -eq 'all') {
    $tasks.Add('releaseCandidate')
} else {
    $tasks.Add(":fabric-$Target`:verifiedSource")
}

Push-Location $repoRoot
try {
    & $wrapper -g (Join-Path $repoRoot '.gradle-user') @tasks --no-daemon --stacktrace
    if ($LASTEXITCODE -ne 0) { throw "Gradle terminó con código $LASTEXITCODE" }
} finally {
    Pop-Location
}
