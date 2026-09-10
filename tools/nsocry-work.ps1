param(
    [ValidateSet("1", "2", "3", "4", "5", "6", "7", "8")]
    [string]$Action = "1"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$ExpectedBranch = "agent/document-nsokiss-runtime"
$RepositoryRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$WorkDirectory = Join-Path $RepositoryRoot ".nsocry-work"
$LogPath = Join-Path $WorkDirectory "maven-latest.log"
$LatestReportPath = Join-Path $RepositoryRoot "reports\windows\latest.md"
$HistoryDirectory = Join-Path $RepositoryRoot "reports\windows\history"

function Stop-Workflow([string]$Message, [int]$Code = 1) {
    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=STOPPED"
    Write-Host "REASON=$Message"
    Write-Host "DATABASE_CHANGED=false"
    exit $Code
}

function Invoke-Git([string[]]$Arguments) {
    # Không cho Git tự repack/gc trong workflow tương tác; Windows có thể đang khóa pack.idx.
    & git -c gc.auto=0 -c maintenance.auto=false @Arguments
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "Lenh git that bai: git $($Arguments -join ' ')" $LASTEXITCODE
    }
}

function Assert-Repository {
    Set-Location $RepositoryRoot
    & git rev-parse --is-inside-work-tree *> $null
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "Thu muc hien tai khong phai Git repository."
    }

    $branch = (& git branch --show-current).Trim()
    if ($LASTEXITCODE -ne 0 -or $branch -ne $ExpectedBranch) {
        Stop-Workflow "Sai nhanh. Can dung $ExpectedBranch; hien tai la '$branch'."
    }

    & git diff --quiet
    $worktreeChanged = $LASTEXITCODE -ne 0
    & git diff --cached --quiet
    $indexChanged = $LASTEXITCODE -ne 0
    if ($worktreeChanged -or $indexChanged) {
        Stop-Workflow "Co thay doi tracked chua commit. BAT khong tu y commit hoac ghi de source cua ban."
    }

    $unsafeUntracked = @(& git ls-files --others --exclude-standard |
        Where-Object { $_ -match '^(src/|tools/|pom\.xml$)' })
    if ($unsafeUntracked.Count -gt 0) {
        Stop-Workflow "Co file source/tool chua duoc Git theo doi: $($unsafeUntracked -join ', ')"
    }
}

function Pull-Branch {
    Write-Host "===== PULL FAST-FORWARD ====="
    Invoke-Git @("pull", "--ff-only", "origin", $ExpectedBranch)
    Assert-Repository
    Write-Host "PULL_STATUS=SUCCESS"
}

function Show-LatestReport {
    Assert-Repository
    if (-not (Test-Path $LatestReportPath)) {
        Stop-Workflow "Chua co reports/windows/latest.md. Hay chon 1 de tao bao cao dau tien."
    }
    Write-Host "===== BAO CAO GAN NHAT ====="
    Get-Content -Path $LatestReportPath
    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=REPORT_DISPLAYED"
    Write-Host "DATABASE_CHANGED=false"
}

function New-ReportContent(
    [string]$Status,
    [int]$MavenExitCode,
    [string]$TestSummary,
    [string]$TestedCommit,
    [string]$StartedAt,
    [string]$FinishedAt,
    [string]$JavaSummary,
    [string]$MavenSummary
) {
    $tail = (Get-Content -Path $LogPath -Tail 80 | ForEach-Object { "    " + $_ }) -join [Environment]::NewLine
    return @(
        "# Báo cáo build/test Windows NSOCry",
        "",
        "- Trạng thái: **$Status**",
        "- Nhánh: $ExpectedBranch",
        "- Commit được kiểm tra: $TestedCommit",
        "- Bắt đầu UTC: $StartedAt",
        "- Kết thúc UTC: $FinishedAt",
        "- Maven exit code: $MavenExitCode",
        "- Tổng hợp test: $TestSummary",
        "- Java: $JavaSummary",
        "- Maven: $MavenSummary",
        "- Lệnh: mvn clean package",
        "- Database changed: false",
        "- DATA imported: false",
        "- Runtime snapshot published: false",
        "- Server startup wired: false",
        "",
        "## Ý nghĩa",
        "",
        "Báo cáo này do NSOCRY_WORK.bat tạo trên máy Windows của chủ dự án. Báo cáo xác nhận khả năng compile/package và kết quả test của đúng commit nêu trên. Quy trình không chạy migration, không import DATA và không khởi động server.",
        "",
        "## Nhật ký đầy đủ",
        "",
        "Nhật ký Maven đầy đủ được giữ cục bộ tại .nsocry-work/maven-latest.log để tránh làm repository phình lớn. Khi build lỗi, phần cuối log được chép dưới đây.",
        "",
        "## Phần cuối Maven log",
        "",
        $tail
    ) -join [Environment]::NewLine
}

function Build-And-Publish {
    Assert-Repository
    New-Item -ItemType Directory -Force -Path $WorkDirectory, $HistoryDirectory | Out-Null

    $testedCommit = (& git rev-parse HEAD).Trim()
    $shortCommit = (& git rev-parse --short=8 HEAD).Trim()
    $stamp = (Get-Date).ToUniversalTime().ToString("yyyyMMdd-HHmmss")
    $startedAt = (Get-Date).ToUniversalTime().ToString("o")
    $historyPath = Join-Path $HistoryDirectory "$stamp-$shortCommit.md"

    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $javaOutput = @(& java -version 2>&1)
    $javaExitCode = $LASTEXITCODE
    $mavenOutput = @(& mvn -version 2>&1)
    $mavenVersionExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    if ($javaExitCode -ne 0) {
        Stop-Workflow "Khong tim thay Java 17 trong PATH."
    }
    if ($mavenVersionExitCode -ne 0) {
        Stop-Workflow "Khong tim thay Maven trong PATH."
    }
    $javaSummary = (($javaOutput | Select-Object -First 1) -join " ").Trim()
    $mavenSummary = (($mavenOutput | Select-Object -First 1) -join " ").Trim()

    Write-Host "===== MAVEN CLEAN PACKAGE ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & mvn clean package 2>&1 | Tee-Object -FilePath $LogPath
    $mavenExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference

    $finishedAt = (Get-Date).ToUniversalTime().ToString("o")
    $testLines = @(Select-String -Path $LogPath -Pattern 'Tests run:\s*\d+,\s*Failures:\s*\d+,\s*Errors:\s*\d+,\s*Skipped:\s*\d+' |
        ForEach-Object { $_.Matches.Value })
    $testSummary = if ($testLines.Count -gt 0) { $testLines[-1] } else { "Khong tim thay dong tong hop test trong Maven log" }
    $status = if ($mavenExitCode -eq 0) { "SUCCESS" } else { "FAILURE" }

    $report = New-ReportContent $status $mavenExitCode $testSummary $testedCommit $startedAt $finishedAt $javaSummary $mavenSummary
    Set-Content -Path $LatestReportPath -Value $report -Encoding UTF8
    Set-Content -Path $historyPath -Value $report -Encoding UTF8

    $name = [string](& git config user.name)
    $email = [string](& git config user.email)
    if ([string]::IsNullOrWhiteSpace($name) -or [string]::IsNullOrWhiteSpace($email)) {
        Stop-Workflow "Git user.name/user.email chua duoc cau hinh; bao cao da luu local nhung chua commit."
    }

    Invoke-Git @("add", "--", "reports/windows/latest.md", ($historyPath.Substring($RepositoryRoot.Length + 1).Replace("\", "/")))
    & git diff --cached --quiet
    if ($LASTEXITCODE -eq 0) {
        Stop-Workflow "Bao cao khong co thay doi de commit."
    }

    Invoke-Git @("commit", "-m", "ci: report Windows build $status $shortCommit")
    $reportCommit = (& git rev-parse HEAD).Trim()

    Write-Host "===== PUSH REPORT ====="
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "NSOCRY_WORKFLOW_RESULT=REPORT_COMMITTED_PUSH_FAILED"
        Write-Host "BUILD_STATUS=$status"
        Write-Host "TEST_SUMMARY=$testSummary"
        Write-Host "TESTED_COMMIT=$testedCommit"
        Write-Host "REPORT_COMMIT=$reportCommit"
        Write-Host "DATABASE_CHANGED=false"
        exit 2
    }

    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=REPORT_PUBLISHED"
    Write-Host "BUILD_STATUS=$status"
    Write-Host "TEST_SUMMARY=$testSummary"
    Write-Host "TESTED_COMMIT=$testedCommit"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "DATABASE_CHANGED=false"
    Write-Host "DATA_IMPORTED=false"
    Write-Host "RUNTIME_SNAPSHOT_PUBLISHED=false"
    Write-Host "SERVER_STARTUP_WIRED=false"
    exit 0
}


function Invoke-DataImportPlan {
    Assert-Repository
    $jar = Join-Path $RepositoryRoot "target\\nsocry-server-0.1.0-SNAPSHOT.jar"
    $plan = Join-Path $RepositoryRoot "config\\data-import-plan.properties.example"
    if (-not (Test-Path $jar)) {
        Stop-Workflow "Chua co JAR. Hay chon 1 de pull va build truoc."
    }
    if (-not (Test-Path $plan)) {
        Stop-Workflow "Thieu config/data-import-plan.properties.example."
    }

    Write-Host "===== DATA IMPORT PLAN OFFLINE ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $planOutput = @(& java -jar $jar data-seed-import-plan $plan 2>&1)
    $planExitCode = $LASTEXITCODE
    $planOutput | ForEach-Object { Write-Host $_ }
    $ErrorActionPreference = $previousPreference
    if ($planExitCode -ne 0) {
        Stop-Workflow "DATA import plan offline khong dat gate." $planExitCode
    }
    $planReportPath = Join-Path $RepositoryRoot "reports\\windows\\latest-data-import-plan.md"
    $testedCommit = (& git rev-parse HEAD).Trim()
    $reportLines = @(
        "# DATA import plan Windows",
        "",
        "- Tested commit: $testedCommit",
        "- Status: AUTHORIZED_OFFLINE",
        "- Database connection opened: false",
        "- Database changed: false",
        "- DATA imported: false",
        "",
        "## Command output",
        ""
    ) + ($planOutput | ForEach-Object { "    " + $_ })
    Set-Content -Path $planReportPath -Value ($reportLines -join [Environment]::NewLine) -Encoding UTF8
    Invoke-Git @("add", "--", "reports/windows/latest-data-import-plan.md")
    Invoke-Git @("commit", "-m", "ops: report DATA import plan offline")
    $reportCommit = (& git rev-parse HEAD).Trim()
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "DATA plan report da commit local nhung push that bai." $LASTEXITCODE
    }

    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=DATA_IMPORT_PLAN_REPORT_PUBLISHED"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "DATABASE_CONNECTION_OPENED=false"
    Write-Host "DATABASE_CHANGED=false"
    Write-Host "DATA_IMPORTED=false"
}


function Invoke-AuthorizedDataImport {
    Assert-Repository
    Pull-Branch

    $jar = Join-Path $RepositoryRoot "target\\nsocry-server-0.1.0-SNAPSHOT.jar"
    $plan = Join-Path $RepositoryRoot "config\\data-import-plan.properties.example"
    New-Item -ItemType Directory -Force -Path $WorkDirectory | Out-Null
    $preImportLog = Join-Path $WorkDirectory "data-import-prebuild.log"

    Write-Host "===== PRE-IMPORT FULL BUILD ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & mvn clean package 2>&1 | Tee-Object -FilePath $preImportLog
    $buildExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    if ($buildExitCode -ne 0) {
        Stop-Workflow "Pre-import build/test that bai; DATA import chua duoc chay." $buildExitCode
    }

    Write-Host "===== AUTHORIZED DATA V7 IMPORT: REJECT_EXISTING ====="
    $ErrorActionPreference = "Continue"
    $importOutput = @(& java -jar $jar data-seed-import $plan 2>&1)
    $importExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    $importOutput | ForEach-Object { Write-Host $_ }

    $joinedOutput = $importOutput -join [Environment]::NewLine
    $success = ($importExitCode -eq 0) -and
        $joinedOutput.Contains("DATA seed IMPORTED_AND_VERIFIED") -and
        $joinedOutput.Contains("overwritten=false")
    $testedCommit = (& git rev-parse HEAD).Trim()
    $reportPath = Join-Path $RepositoryRoot "reports\\windows\\latest-data-import.md"
    $status = if ($success) { "IMPORTED_AND_VERIFIED" } else { "FAILED_OR_UNCERTAIN" }
    $reportLines = @(
        "# DATA v7 import Windows",
        "",
        "- Tested commit: $testedCommit",
        "- Command exit code: $importExitCode",
        "- Status: $status",
        "- Authorized mode: REJECT_EXISTING",
        "- Runtime snapshot published: false",
        "- Server startup wired: false",
        "",
        "## Command output",
        ""
    ) + ($importOutput | ForEach-Object { "    " + $_ })
    Set-Content -Path $reportPath -Value ($reportLines -join [Environment]::NewLine) -Encoding UTF8
    Invoke-Git @("add", "--", "reports/windows/latest-data-import.md")
    $commitMessage = if ($success) {
        "ops: report DATA v7 import verified"
    } else {
        "ops: report DATA v7 import failed or uncertain"
    }
    Invoke-Git @("commit", "-m", $commitMessage)
    $reportCommit = (& git rev-parse HEAD).Trim()
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "DATA import report da commit local nhung push that bai." $LASTEXITCODE
    }
    if (-not $success) {
        Write-Host "NSOCRY_WORKFLOW_RESULT=DATA_IMPORT_FAILED_OR_UNCERTAIN"
        Write-Host "REPORT_COMMIT=$reportCommit"
        exit 3
    }

    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=DATA_V7_IMPORTED_AND_VERIFIED"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "DATABASE_CHANGED=true"
    Write-Host "DATA_IMPORTED=true"
    Write-Host "RUNTIME_SNAPSHOT_PUBLISHED=false"
    Write-Host "SERVER_STARTUP_WIRED=false"
}


function Invoke-IsolatedDataRuntimePublish {
    Assert-Repository
    Pull-Branch
    $jar = Join-Path $RepositoryRoot "target\\nsocry-server-0.1.0-SNAPSHOT.jar"
    $archive = Join-Path $RepositoryRoot "data-dry-run-data-seed-v7-candidate.zip"
    if (-not (Test-Path $jar)) {
        Stop-Workflow "Chua co JAR. Hay chon 1 de build truoc."
    }
    if (-not (Test-Path $archive)) {
        Stop-Workflow "Thieu DATA v7 candidate archive."
    }

    Write-Host "===== DATA RUNTIME PUBLISH ISOLATED ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $publishOutput = @(& java -jar $jar data-runtime-publish $archive 2>&1)
    $publishExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    $publishOutput | ForEach-Object { Write-Host $_ }

    $joinedOutput = $publishOutput -join [Environment]::NewLine
    $success = ($publishExitCode -eq 0) -and
        $joinedOutput.Contains("DATA runtime snapshot PUBLISHED_ISOLATED") -and
        $joinedOutput.Contains("serverStartupWired=false")
    $testedCommit = (& git rev-parse HEAD).Trim()
    $reportPath = Join-Path $RepositoryRoot "reports\\windows\\latest-data-runtime-publish.md"
    $status = if ($success) { "PUBLISHED_ISOLATED" } else { "FAILED" }
    $reportLines = @(
        "# DATA runtime publish Windows",
        "",
        "- Tested commit: $testedCommit",
        "- Command exit code: $publishExitCode",
        "- Status: $status",
        "- Database changed: false",
        "- Server startup wired: false",
        "",
        "## Command output",
        ""
    ) + ($publishOutput | ForEach-Object { "    " + $_ })
    Set-Content -Path $reportPath -Value ($reportLines -join [Environment]::NewLine) -Encoding UTF8
    Invoke-Git @("add", "--", "reports/windows/latest-data-runtime-publish.md")
    $commitMessage = if ($success) {
        "ops: report DATA runtime publish isolated"
    } else {
        "ops: report DATA runtime publish failure"
    }
    Invoke-Git @("commit", "-m", $commitMessage)
    $reportCommit = (& git rev-parse HEAD).Trim()
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "DATA runtime report da commit local nhung push that bai." $LASTEXITCODE
    }
    if (-not $success) {
        Write-Host "NSOCRY_WORKFLOW_RESULT=DATA_RUNTIME_PUBLISH_FAILED"
        Write-Host "REPORT_COMMIT=$reportCommit"
        exit 4
    }

    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=DATA_RUNTIME_PUBLISHED_ISOLATED"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "DATABASE_CHANGED=false"
    Write-Host "RUNTIME_SNAPSHOT_PUBLISHED=true"
    Write-Host "SERVER_STARTUP_WIRED=false"
}


function Invoke-DataStartupSmokeTest {
    Assert-Repository
    Pull-Branch
    $jar = Join-Path $RepositoryRoot "target\\nsocry-server-0.1.0-SNAPSHOT.jar"
    if (-not (Test-Path $jar)) {
        Stop-Workflow "Chua co JAR. Hay chon 1 de build truoc."
    }

    New-Item -ItemType Directory -Force -Path $WorkDirectory | Out-Null
    $smokeBuildLog = Join-Path $WorkDirectory "server-smoke-build.log"
    Write-Host "===== PRE-SMOKE FULL BUILD ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & mvn clean package 2>&1 | Tee-Object -FilePath $smokeBuildLog
    $buildExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    if ($buildExitCode -ne 0) {
        Stop-Workflow "Pre-smoke build/test that bai; server chua duoc khoi dong." $buildExitCode
    }

    $stdoutPath = Join-Path $WorkDirectory "server-smoke-stdout.log"
    $stderrPath = Join-Path $WorkDirectory "server-smoke-stderr.log"
    Remove-Item -Force -ErrorAction SilentlyContinue $stdoutPath, $stderrPath
    $process = $null
    $processWasRunning = $false
    try {
        Write-Host "===== SERVER DATA STARTUP SMOKE TEST ====="
        $arguments = "-jar `"$jar`" server"
        $process = Start-Process -FilePath "java" -ArgumentList $arguments -WorkingDirectory $RepositoryRoot -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath -PassThru
        for ($attempt = 0; $attempt -lt 30; $attempt++) {
            Start-Sleep -Milliseconds 500
            $process.Refresh()
            if ($process.HasExited) { break }
            $stdout = if (Test-Path $stdoutPath) { Get-Content $stdoutPath -Raw } else { "" }
            if ($stdout.Contains("NSOCry server started on") -and
                    $stdout.Contains("DATA runtime snapshot READY version=7")) {
                $processWasRunning = $true
                break
            }
        }
        $process.Refresh()
        if (-not $process.HasExited) {
            $processWasRunning = $true
        }
    } finally {
        if ($null -ne $process) {
            $process.Refresh()
            if (-not $process.HasExited) {
                Stop-Process -Id $process.Id -Force
                $process.WaitForExit()
            }
        }
    }

    $stdoutLines = if (Test-Path $stdoutPath) { @(Get-Content $stdoutPath) } else { @() }
    $stderrLines = if (Test-Path $stderrPath) { @(Get-Content $stderrPath) } else { @() }
    $joinedOutput = $stdoutLines -join [Environment]::NewLine
    $success = $processWasRunning -and
        $joinedOutput.Contains("NSOCry server started on") -and
        $joinedOutput.Contains("DATA runtime snapshot READY version=7")
    $testedCommit = (& git rev-parse HEAD).Trim()
    $reportPath = Join-Path $RepositoryRoot "reports\\windows\\latest-data-startup-smoke.md"
    $status = if ($success) { "STARTED_READY_AND_STOPPED" } else { "FAILED" }
    $reportLines = @(
        "# DATA production startup smoke Windows",
        "",
        "- Tested commit: $testedCommit",
        "- Status: $status",
        "- Process observed running: $processWasRunning",
        "- Process stopped by runner: true",
        "- Database changed: false",
        "- DATA imported: false",
        "",
        "## Standard output",
        ""
    ) + ($stdoutLines | ForEach-Object { "    " + $_ }) + @(
        "",
        "## Standard error",
        ""
    ) + ($stderrLines | ForEach-Object { "    " + $_ })
    Set-Content -Path $reportPath -Value ($reportLines -join [Environment]::NewLine) -Encoding UTF8
    Invoke-Git @("add", "--", "reports/windows/latest-data-startup-smoke.md")
    $commitMessage = if ($success) {
        "ops: report DATA production startup smoke verified"
    } else {
        "ops: report DATA production startup smoke failure"
    }
    Invoke-Git @("commit", "-m", $commitMessage)
    $reportCommit = (& git rev-parse HEAD).Trim()
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "Startup smoke report da commit local nhung push that bai." $LASTEXITCODE
    }
    if (-not $success) {
        Write-Host "NSOCRY_WORKFLOW_RESULT=DATA_STARTUP_SMOKE_FAILED"
        Write-Host "REPORT_COMMIT=$reportCommit"
        exit 5
    }

    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=DATA_STARTUP_SMOKE_VERIFIED"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "DATABASE_CHANGED=false"
    Write-Host "RUNTIME_SNAPSHOT_PUBLISHED=true"
    Write-Host "SERVER_STARTUP_WIRED=true"
    Write-Host "SERVER_PROCESS_STOPPED=true"
}

switch ($Action) {
    "1" {
        Assert-Repository
        Pull-Branch
        Build-And-Publish
    }
    "2" {
        Assert-Repository
        Pull-Branch
        Write-Host "NSOCRY_WORKFLOW_RESULT=PULL_COMPLETED"
        Write-Host "DATABASE_CHANGED=false"
    }
    "3" {
        Build-And-Publish
    }
    "4" {
        Show-LatestReport
    }
    "5" {
        Assert-Repository
        Pull-Branch
        Invoke-DataImportPlan
    }
    "6" {
        Invoke-AuthorizedDataImport
    }
    "7" {
        Invoke-IsolatedDataRuntimePublish
    }
    "8" {
        Invoke-DataStartupSmokeTest
    }
}
