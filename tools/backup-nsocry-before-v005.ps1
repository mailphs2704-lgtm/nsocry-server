param(
    [string]$ConfigurationPath = "config/nsocry.properties",
    [string]$DumpExecutable = "C:/laragon/bin/mysql/mariadb-10.11.10-winx64/bin/mariadb-dump.exe",
    [string]$BackupDirectory = "backups"
)

$ErrorActionPreference = "Stop"

function Read-NsocryProperties([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        throw "Khong tim thay cau hinh NSOCry: $Path"
    }
    $values = @{}
    foreach ($line in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $trimmed = $line.Trim()
        if ($trimmed.Length -eq 0 -or $trimmed.StartsWith("#")) {
            continue
        }
        $separator = $trimmed.IndexOf("=")
        if ($separator -le 0) {
            throw "Dong cau hinh khong hop le"
        }
        $key = $trimmed.Substring(0, $separator).Trim()
        $value = $trimmed.Substring($separator + 1).Trim()
        if ($values.ContainsKey($key)) {
            throw "Khoa cau hinh bi trung: $key"
        }
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

$properties = Read-NsocryProperties $ConfigurationPath
$databaseUrl = Require-Property $properties "nsocry.database.url"
$databaseUser = Require-Property $properties "nsocry.database.user"
$databasePassword = Require-Property $properties "nsocry.database.password"

if ($databaseUrl -notmatch '^jdbc:mariadb://127\.0\.0\.1:3306/nsocry(?:\?.*)?$') {
    throw "Tu choi backup vi database URL khong dung dich NSOCry da khoa"
}
if (-not (Test-Path -LiteralPath $DumpExecutable -PathType Leaf)) {
    throw "Khong tim thay mariadb-dump: $DumpExecutable"
}

New-Item -ItemType Directory -Path $BackupDirectory -Force | Out-Null
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$target = Join-Path $BackupDirectory "nsocry-before-v005-$timestamp.sql"
$partial = "$target.partial"
if ((Test-Path -LiteralPath $target) -or (Test-Path -LiteralPath $partial)) {
    throw "Backup target da ton tai; khong ghi de"
}

$env:MYSQL_PWD = $databasePassword
try {
    & $DumpExecutable `
        "--host=127.0.0.1" `
        "--port=3306" `
        "--protocol=tcp" `
        "--user=$databaseUser" `
        "--single-transaction" `
        "--routines" `
        "--triggers" `
        "--events" `
        "--hex-blob" `
        "--databases" `
        "nsocry" `
        "--result-file=$partial"
    $dumpExitCode = $LASTEXITCODE
} finally {
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
    $databasePassword = $null
}

if ($dumpExitCode -ne 0) {
    throw "mariadb-dump that bai, exit code=$dumpExitCode; V005 chua duoc chay"
}
if (-not (Test-Path -LiteralPath $partial -PathType Leaf)) {
    throw "mariadb-dump khong tao file partial"
}
$size = (Get-Item -LiteralPath $partial).Length
if ($size -le 0) {
    throw "Backup rong; V005 chua duoc chay"
}

Move-Item -LiteralPath $partial -Destination $target
$resolved = (Resolve-Path -LiteralPath $target).Path
$sha256 = (Get-FileHash -LiteralPath $target -Algorithm SHA256).Hash.ToLowerInvariant()

Write-Output "NSOCRY BACKUP VERIFIED"
Write-Output "backupPath=$resolved"
Write-Output "backupSize=$size"
Write-Output "sha256=$sha256"
Write-Output "databaseChanged=false"
Write-Output "v005Executed=false"
