param(
    [string]$ConfigurationPath = "config/nsocry.properties",
    [string]$MariaDbExecutable = "C:/laragon/bin/mysql/mariadb-10.11.10-winx64/bin/mariadb.exe",
    [string]$MigrationPath = "database/migrations/V005__client_data_assets.sql",
    [string]$BackupPath = "backups/nsocry-before-v005-20260901-174146.sql",
    [string]$ExpectedBackupSha256 = "9cea61d3482ec08a727b71f11c4400dd2c6144cc55b9450baf27bd6dd71983c6",
    [string]$JarPath = "target/nsocry-server-0.1.0-SNAPSHOT.jar"
)

$ErrorActionPreference = "Stop"

function Read-NsocryProperties([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        throw "Khong tim thay cau hinh NSOCry: $Path"
    }
    $values = @{}
    foreach ($line in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $trimmed = $line.Trim()
        if ($trimmed.Length -eq 0 -or $trimmed.StartsWith("#")) { continue }
        $separator = $trimmed.IndexOf("=")
        if ($separator -le 0) { throw "Dong cau hinh khong hop le" }
        $key = $trimmed.Substring(0, $separator).Trim()
        $value = $trimmed.Substring($separator + 1).Trim()
        if ($values.ContainsKey($key)) { throw "Khoa cau hinh bi trung: $key" }
        $values[$key] = $value
    }
    return $values
}

function Require-Property($Values, [string]$Key) {
    if (-not $Values.ContainsKey($Key) -or [string]::IsNullOrWhiteSpace($Values[$Key])) {
        throw "Thieu cau hinh bat buoc: $Key"
    }
    return $Values[$Key]
}

function Invoke-Preflight([string]$ExpectedState) {
    $previousErrorActionPreference = $ErrorActionPreference
    try {
        # NOT_READY ghi stack trace ra stderr theo contract CLI; thu cả hai stream để tự đánh giá.
        $ErrorActionPreference = "Continue"
        $output = & java -jar $JarPath data-schema-preflight $ConfigurationPath 2>&1
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    $text = ($output | Out-String)
    Write-Output $text.TrimEnd()
    if ($ExpectedState -eq "NOT_READY") {
        if ($exitCode -eq 0 -or -not $text.Contains("DATA schema preflight NOT_READY") -or
                -not $text.Contains("databaseChanged=false")) {
            throw "Preflight truoc V005 khong dung baseline NOT_READY"
        }
    } elseif ($ExpectedState -eq "READY") {
        if ($exitCode -ne 0 -or -not $text.Contains("DATA schema preflight READY") -or
                -not $text.Contains("databaseChanged=false")) {
            throw "Preflight sau V005 khong dat READY"
        }
    } else {
        throw "ExpectedState khong hop le"
    }
}

foreach ($requiredFile in @($MariaDbExecutable, $MigrationPath, $BackupPath, $JarPath)) {
    if (-not (Test-Path -LiteralPath $requiredFile -PathType Leaf)) {
        throw "Thieu file bat buoc: $requiredFile"
    }
}

$backup = Get-Item -LiteralPath $BackupPath
if ($backup.Length -ne 234839) {
    throw "Backup size khong khop checkpoint da khoa"
}
$backupSha256 = (Get-FileHash -LiteralPath $BackupPath -Algorithm SHA256).Hash.ToLowerInvariant()
if ($backupSha256 -ne $ExpectedBackupSha256.ToLowerInvariant()) {
    throw "Backup SHA-256 khong khop checkpoint da khoa"
}

$properties = Read-NsocryProperties $ConfigurationPath
$databaseUrl = Require-Property $properties "nsocry.database.url"
$databaseUser = Require-Property $properties "nsocry.database.user"
$databasePassword = Require-Property $properties "nsocry.database.password"
if ($databaseUrl -notmatch '^jdbc:mariadb://127\.0\.0\.1:3306/nsocry(?:\?.*)?$') {
    throw "Tu choi migration vi database URL khong dung dich NSOCry da khoa"
}

Write-Output "V005 PRECHECK VERIFIED"
Write-Output "backupSize=$($backup.Length)"
Write-Output "backupSha256=$backupSha256"
Write-Output "databaseTarget=127.0.0.1:3306/nsocry"
Invoke-Preflight "NOT_READY"

$migrationSql = Get-Content -LiteralPath $MigrationPath -Raw -Encoding UTF8
$env:MYSQL_PWD = $databasePassword
try {
    $migrationSql | & $MariaDbExecutable `
        "--host=127.0.0.1" `
        "--port=3306" `
        "--protocol=tcp" `
        "--user=$databaseUser" `
        "--database=nsocry" `
        "--default-character-set=utf8mb4"
    $migrationExitCode = $LASTEXITCODE
} finally {
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
    $databasePassword = $null
}
if ($migrationExitCode -ne 0) {
    throw "V005 migration that bai, exit code=$migrationExitCode; kiem tra DB truoc khi thu lai"
}

Invoke-Preflight "READY"
Write-Output "DATA V005 MIGRATION VERIFIED"
Write-Output "databaseChanged=true"
Write-Output "dataImported=false"
Write-Output "runtimeSnapshotPublished=false"
Write-Output "serverStartupWired=false"
