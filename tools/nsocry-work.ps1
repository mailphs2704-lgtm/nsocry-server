param(
    [ValidateSet("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")]
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

function Resolve-MavenCommand {
    $command = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($null -eq $command) {
        $command = Get-Command mvn -ErrorAction SilentlyContinue
    }
    if ($null -ne $command) {
        return $command.Source
    }

    $candidates = @()
    if (-not [string]::IsNullOrWhiteSpace($env:MAVEN_HOME)) {
        $candidates += Join-Path $env:MAVEN_HOME "bin\\mvn.cmd"
    }
    $drive = Split-Path -Qualifier $RepositoryRoot
    $toolsRoot = Join-Path $drive "tools"
    if (Test-Path $toolsRoot) {
        $candidates += @(Get-ChildItem -Path $toolsRoot -Directory -Filter "apache-maven-*" -ErrorAction SilentlyContinue |
            Sort-Object Name -Descending |
            ForEach-Object { Join-Path $_.FullName "bin\\mvn.cmd" })
    }
    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) {
            $env:MAVEN_HOME = Split-Path (Split-Path $candidate -Parent) -Parent
            $env:PATH = (Split-Path $candidate -Parent) + ";" + $env:PATH
            Write-Host "MAVEN_AUTO_DISCOVERED=$candidate"
            return $candidate
        }
    }
    Stop-Workflow "Khong tim thay Maven trong PATH, MAVEN_HOME hoac thu muc tools tren o dia repository."
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
    Write-Host "===== FETCH + SAFE UPDATE ====="
    Invoke-Git @("fetch", "origin", $ExpectedBranch)
    $remoteRef = "origin/$ExpectedBranch"

    & git merge-base --is-ancestor HEAD $remoteRef
    if ($LASTEXITCODE -eq 0) {
        Invoke-Git @("merge", "--ff-only", $remoteRef)
        Assert-Repository
        Write-Host "PULL_STATUS=SUCCESS_FAST_FORWARD"
        return
    }

    & git merge-base --is-ancestor $remoteRef HEAD
    if ($LASTEXITCODE -eq 0) {
        Assert-Repository
        Write-Host "PULL_STATUS=LOCAL_AHEAD"
        return
    }

    $localCommits = @(& git rev-list "$remoteRef..HEAD")
    if ($LASTEXITCODE -ne 0 -or $localCommits.Count -eq 0) {
        Stop-Workflow "Nhanh local/remote diverged va khong xac dinh duoc commit local."
    }
    $unsafePaths = @()
    foreach ($commit in $localCommits) {
        $paths = @(& git diff-tree --no-commit-id --name-only -r $commit)
        if ($LASTEXITCODE -ne 0) {
            Stop-Workflow "Khong doc duoc noi dung commit local $commit."
        }
        $unsafePaths += @($paths | Where-Object { $_ -notmatch '^reports/windows/' })
    }
    if ($unsafePaths.Count -gt 0) {
        Stop-Workflow "Nhanh diverged co source local; khong tu rebase: $($unsafePaths -join ', ')"
    }

    Write-Host "SAFE_RECOVERY=REBASING_REPORT_ONLY_COMMITS"
    Invoke-Git @("rebase", $remoteRef)
    Assert-Repository
    Write-Host "PULL_STATUS=SUCCESS_REPORT_REBASE"
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
    if ($null -eq (Get-Command java -ErrorAction SilentlyContinue)) {
        Stop-Workflow "Khong tim thay Java trong PATH."
    }
    $mavenCommand = Resolve-MavenCommand
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
    $mavenOutput = @(& $mavenCommand -version 2>&1)
    $mavenVersionExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    if ($javaExitCode -ne 0) {
        Stop-Workflow "Java ton tai nhung java -version that bai."
    }
    if ($mavenVersionExitCode -ne 0) {
        Stop-Workflow "Maven ton tai nhung mvn -version that bai."
    }
    $javaSummary = (($javaOutput | Select-Object -First 1) -join " ").Trim()
    $mavenSummary = (($mavenOutput | Select-Object -First 1) -join " ").Trim()

    Write-Host "===== MAVEN CLEAN PACKAGE ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & $mavenCommand clean package 2>&1 | Tee-Object -FilePath $LogPath
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
    if ($null -eq (Get-Command java -ErrorAction SilentlyContinue)) {
        Stop-Workflow "Khong tim thay Java trong PATH; DATA import chua duoc chay."
    }
    $mavenCommand = Resolve-MavenCommand

    $jar = Join-Path $RepositoryRoot "target\\nsocry-server-0.1.0-SNAPSHOT.jar"
    $plan = Join-Path $RepositoryRoot "config\\data-import-plan.properties.example"
    New-Item -ItemType Directory -Force -Path $WorkDirectory | Out-Null
    $preImportLog = Join-Path $WorkDirectory "data-import-prebuild.log"

    Write-Host "===== PRE-IMPORT FULL BUILD ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & $mavenCommand clean package 2>&1 | Tee-Object -FilePath $preImportLog
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

    if ($null -eq (Get-Command java -ErrorAction SilentlyContinue)) {
        Stop-Workflow "Khong tim thay Java trong PATH; server chua duoc khoi dong."
    }
    $mavenCommand = Resolve-MavenCommand

    New-Item -ItemType Directory -Force -Path $WorkDirectory | Out-Null
    $smokeBuildLog = Join-Path $WorkDirectory "server-smoke-build.log"
    Write-Host "===== PRE-SMOKE FULL BUILD ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & $mavenCommand clean package 2>&1 | Tee-Object -FilePath $smokeBuildLog
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
            $stdout = if (Test-Path $stdoutPath) { [string](Get-Content $stdoutPath -Raw) } else { "" }
            if ($null -eq $stdout) { $stdout = "" }
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


function Invoke-V9HandshakeCapture {
    Assert-Repository
    Pull-Branch
    $jar = Join-Path $RepositoryRoot "target\\nsocry-server-0.1.0-SNAPSHOT.jar"
    if (-not (Test-Path $jar)) {
        Stop-Workflow "Chua co JAR. Hay chon 1 de pull va build truoc."
    }
    if ($null -eq (Get-Command java -ErrorAction SilentlyContinue)) {
        Stop-Workflow "Khong tim thay Java trong PATH."
    }
    $mavenCommand = Resolve-MavenCommand
    New-Item -ItemType Directory -Force -Path $WorkDirectory, $HistoryDirectory | Out-Null
    $captureBuildLog = Join-Path $WorkDirectory "v9-handshake-build.log"
    Write-Host "===== PRE-CAPTURE FULL BUILD ====="
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & $mavenCommand clean package 2>&1 | Tee-Object -FilePath $captureBuildLog
    $buildExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    if ($buildExitCode -ne 0) {
        Stop-Workflow "Pre-capture build/test that bai; server chua duoc khoi dong." $buildExitCode
    }

    $stdoutPath = Join-Path $WorkDirectory "v9-handshake-stdout.log"
    $stderrPath = Join-Path $WorkDirectory "v9-handshake-stderr.log"
    Remove-Item -Force -ErrorAction SilentlyContinue $stdoutPath, $stderrPath
    $process = $null
    $ready = $false
    try {
        Write-Host "===== V9 HANDSHAKE CAPTURE ====="
        $arguments = "-jar `"$jar`" server config\\nsocry.properties"
        $process = Start-Process -FilePath "java" -ArgumentList $arguments -WorkingDirectory $RepositoryRoot -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath -PassThru
        for ($attempt = 0; $attempt -lt 30; $attempt++) {
            Start-Sleep -Milliseconds 500
            $process.Refresh()
            if ($process.HasExited) { break }
            $stdout = if (Test-Path $stdoutPath) { [string](Get-Content $stdoutPath -Raw) } else { "" }
            if ($null -eq $stdout) { $stdout = "" }
            if ($stdout.Contains("NSOCry server started on") -and
                    $stdout.Contains("DATA runtime snapshot READY version=7")) {
                $ready = $true
                break
            }
        }
        if (-not $ready) {
            Write-Host "Server khong dat READY; runner se thu log va dung."
        } else {
            Write-Host ""
            Write-Host "Server READY. Hay mo client V9, thu dang nhap DUNG MOT LAN."
            [void](Read-Host "Sau khi client khong phan ung hoac ngat ket noi, nhan Enter")
            Start-Sleep -Seconds 2
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
    $testedCommit = (& git rev-parse HEAD).Trim()
    $stamp = (Get-Date).ToUniversalTime().ToString("yyyyMMdd-HHmmss")
    $shortCommit = (& git rev-parse --short=8 HEAD).Trim()
    $reportPath = Join-Path $RepositoryRoot "reports\\windows\\latest-v9-handshake.md"
    $historyPath = Join-Path $HistoryDirectory "$stamp-$shortCommit-v9-handshake.md"
    $status = if ($ready) { "CAPTURED" } else { "STARTUP_FAILED" }
    $reportLines = @(
        "# Client V9 handshake capture Windows",
        "",
        "- Tested commit: $testedCommit",
        "- Status: $status",
        "- Server READY before client attempt: $ready",
        "- Server process stopped by runner: true",
        "- Database migration/import performed: false",
        "- Authentication audit fields may change if login reached AuthenticationService: true",
        "",
        "## Standard output",
        ""
    ) + ($stdoutLines | ForEach-Object { "    " + $_ }) + @(
        "",
        "## Sanitized standard error",
        ""
    ) + ($stderrLines | ForEach-Object { "    " + $_ })
    $report = $reportLines -join [Environment]::NewLine
    Set-Content -Path $reportPath -Value $report -Encoding UTF8
    Set-Content -Path $historyPath -Value $report -Encoding UTF8

    Invoke-Git @("add", "--", "reports/windows/latest-v9-handshake.md", ($historyPath.Substring($RepositoryRoot.Length + 1).Replace("\\", "/")))
    Invoke-Git @("commit", "-m", "ops: report client v9 handshake capture")
    $reportCommit = (& git rev-parse HEAD).Trim()
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "V9 handshake report da commit local nhung push that bai." $LASTEXITCODE
    }

    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=V9_HANDSHAKE_REPORT_PUBLISHED"
    Write-Host "CAPTURE_STATUS=$status"
    Write-Host "TESTED_COMMIT=$testedCommit"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "SERVER_PROCESS_STOPPED=true"
    Write-Host "DATABASE_MIGRATION_OR_IMPORT=false"
}


function Invoke-V9ClientAnalysis {
    Assert-Repository
    Pull-Branch
    $clientJar = Join-Path $RepositoryRoot "source-reference\\V9_NsoCry_x1.jar"
    if (-not (Test-Path -LiteralPath $clientJar -PathType Leaf)) {
        Stop-Workflow "Thieu source-reference/V9_NsoCry_x1.jar."
    }
    foreach ($tool in @("java", "jar", "javap")) {
        if ($null -eq (Get-Command $tool -ErrorAction SilentlyContinue)) {
            Stop-Workflow "Khong tim thay JDK tool: $tool."
        }
    }

    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $archive = [System.IO.Compression.ZipFile]::OpenRead($clientJar)
    try {
        $entries = @($archive.Entries)
        $classes = @($entries | Where-Object { $_.FullName.EndsWith(".class") })
        $manifestEntry = $entries | Where-Object { $_.FullName -ieq "META-INF/MANIFEST.MF" } | Select-Object -First 1
        $manifest = "(khong co manifest)"
        if ($null -ne $manifestEntry) {
            $reader = New-Object System.IO.StreamReader($manifestEntry.Open())
            try { $manifest = $reader.ReadToEnd() } finally { $reader.Dispose() }
        }

        $latin1 = [Text.Encoding]::GetEncoding(28591)
        $candidateNames = New-Object System.Collections.Generic.List[string]
        foreach ($entry in $classes) {
            $stream = $entry.Open()
            $memory = New-Object System.IO.MemoryStream
            try {
                $stream.CopyTo($memory)
                $constantPoolText = $latin1.GetString($memory.ToArray())
            } finally {
                $memory.Dispose()
                $stream.Dispose()
            }
            if ($constantPoolText -match "java/net/Socket|writeUTF|readUTF|DataOutputStream|DataInputStream|14444|127\.0\.0\.1") {
                $candidateNames.Add(($entry.FullName.Substring(0, $entry.FullName.Length - 6) -replace "/", "."))
            }
        }
    } finally {
        $archive.Dispose()
    }

    $patterns = 'writeUTF|new\\s+#[0-9]+\\s+// class bR|Method bR\\..*<init>|Field bR\\.Y:B|lookupswitch|tableswitch|java/net/Socket|SocketConnection'
    $evidence = New-Object System.Collections.Generic.List[string]
    foreach ($className in ($candidateNames | Select-Object -First 80)) {
        $previousPreference = $ErrorActionPreference
        $ErrorActionPreference = "Continue"
        $disassembly = @(& javap -classpath $clientJar -c -p $className 2>&1)
        $ErrorActionPreference = $previousPreference
        $matches = @($disassembly | Select-String -Pattern $patterns -Context 8,16 | Select-Object -First 80)
        if ($matches.Count -gt 0) {
            $evidence.Add("### $className")
            foreach ($match in $matches) {
                $evidence.Add(($match.ToString()))
            }
        }
    }

    $dispatcherDetail = New-Object System.Collections.Generic.List[string]
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $alDisassembly = @(& javap -classpath $clientJar -c -p al 2>&1)
    $ErrorActionPreference = $previousPreference
    $switchIndex = -1
    for ($index = 0; $index -lt $alDisassembly.Count; $index++) {
        if (([string]$alDisassembly[$index]) -match 'tableswitch.*-30 to 126') {
            $switchIndex = $index
            break
        }
    }
    if ($switchIndex -ge 0) {
        $handlerOffset = $null
        for ($index = $switchIndex + 1; $index -lt $alDisassembly.Count; $index++) {
            $line = [string]$alDisassembly[$index]
            if ($line -match '^\s*-30:\s+(\d+)') {
                $handlerOffset = $Matches[1]
                break
            }
            if ($line -match '^\s*default:') {
                break
            }
        }

        $handlerIndex = -1
        if ($null -ne $handlerOffset) {
            $handlerPattern = '^\s*' + [Regex]::Escape($handlerOffset) + ':'
            for ($index = $switchIndex; $index -lt $alDisassembly.Count; $index++) {
                if (([string]$alDisassembly[$index]) -match $handlerPattern) {
                    $handlerIndex = $index
                    break
                }
            }
        }
        if ($handlerIndex -ge 0) {
            $detailStart = [Math]::Max(0, $handlerIndex - 8)
            $detailEnd = [Math]::Min($alDisassembly.Count - 1, $handlerIndex + 90)
            for ($index = $detailStart; $index -le $detailEnd; $index++) {
                $dispatcherDetail.Add(([string]$alDisassembly[$index]))
            }
        } else {
            $dispatcherDetail.Add("Khong tim thay bytecode handler cho command -30.")
        }
    } else {
        $dispatcherDetail.Add("Khong tim thay tableswitch dispatcher -30..126 trong class al.")
    }

    $loginHandlerDetail = New-Object System.Collections.Generic.List[string]
    $loginHandlerStart = -1
    for ($index = 0; $index -lt $alDisassembly.Count; $index++) {
        if (([string]$alDisassembly[$index]) -match '^\s*(private|public|protected).*\sh\(bR\);') {
            $loginHandlerStart = $index
            break
        }
    }
    if ($loginHandlerStart -ge 0) {
        $loginHandlerEnd = [Math]::Min($alDisassembly.Count - 1, $loginHandlerStart + 350)
        for ($index = $loginHandlerStart; $index -le $loginHandlerEnd; $index++) {
            $line = [string]$alDisassembly[$index]
            if ($index -gt $loginHandlerStart -and
                    $line -match '^\s{2}(private|public|protected).+\);') {
                break
            }
            $loginHandlerDetail.Add($line)
        }
    } else {
        $loginHandlerDetail.Add("Khong tim thay method h(bR) cua command -30.")
    }

    $nestedMinus123Detail = New-Object System.Collections.Generic.List[string]
    $nestedMinus123Offset = $null
    if ($loginHandlerStart -ge 0) {
        for ($index = $loginHandlerStart; $index -lt $alDisassembly.Count; $index++) {
            $line = [string]$alDisassembly[$index]
            if ($line -match '^\s*-123:\s+(\d+)') {
                $nestedMinus123Offset = $Matches[1]
                break
            }
            if ($line -match '^\s*default:') {
                break
            }
        }
    }
    if ($null -ne $nestedMinus123Offset) {
        $nestedMinus123Pattern = '^\s*' + [Regex]::Escape($nestedMinus123Offset) + ':'
        $nestedMinus123Index = -1
        for ($index = $loginHandlerStart; $index -lt $alDisassembly.Count; $index++) {
            if (([string]$alDisassembly[$index]) -match $nestedMinus123Pattern) {
                $nestedMinus123Index = $index
                break
            }
        }
        if ($nestedMinus123Index -ge 0) {
            $nestedMinus123End = [Math]::Min($alDisassembly.Count - 1, $nestedMinus123Index + 160)
            for ($index = $nestedMinus123Index; $index -le $nestedMinus123End; $index++) {
                $nestedMinus123Detail.Add(([string]$alDisassembly[$index]))
            }
        }
    }
    if ($nestedMinus123Detail.Count -eq 0) {
        $nestedMinus123Detail.Add("Khong tim thay handler nested command -123 trong al.h(bR).")
    }
    $versionHandlerDetail = New-Object System.Collections.Generic.List[string]
    $versionMethodStart = -1
    for ($index = 0; $index -lt $alDisassembly.Count; $index++) {
        if (([string]$alDisassembly[$index]) -match '^\s*(private|public|protected).*\se\(bR\);') {
            $versionMethodStart = $index
            break
        }
    }
    $versionNestedOffset = $null
    if ($versionMethodStart -ge 0) {
        for ($index = $versionMethodStart; $index -lt $alDisassembly.Count; $index++) {
            $line = [string]$alDisassembly[$index]
            if ($line -match '^\s*-123:\s+(\d+)') {
                $versionNestedOffset = $Matches[1]
                break
            }
            if ($line -match '^\s*default:') {
                break
            }
        }
    }
    if ($null -ne $versionNestedOffset) {
        $versionNestedPattern = '^\s*' + [Regex]::Escape($versionNestedOffset) + ':'
        $versionNestedIndex = -1
        for ($index = $versionMethodStart; $index -lt $alDisassembly.Count; $index++) {
            if (([string]$alDisassembly[$index]) -match $versionNestedPattern) {
                $versionNestedIndex = $index
                break
            }
        }
        if ($versionNestedIndex -ge 0) {
            $versionNestedEnd = [Math]::Min($alDisassembly.Count - 1, $versionNestedIndex + 220)
            for ($index = $versionNestedIndex; $index -le $versionNestedEnd; $index++) {
                $versionHandlerDetail.Add(([string]$alDisassembly[$index]))
            }
        }
    }
    if ($versionHandlerDetail.Count -eq 0) {
        $versionHandlerDetail.Add("Khong tim thay nested command -123 trong al.e(bR).")
    }
    $hash = (Get-FileHash -LiteralPath $clientJar -Algorithm SHA256).Hash.ToLowerInvariant()
    $size = (Get-Item -LiteralPath $clientJar).Length
    $testedCommit = (& git rev-parse HEAD).Trim()
    $reportPath = Join-Path $RepositoryRoot "reports\\windows\\latest-v9-client-analysis.md"
    $reportLines = @(
        "# Phân tích bytecode client V9 Windows",
        "",
        "- Tested commit: $testedCommit",
        "- Client file: V9_NsoCry_x1.jar",
        "- Client size: $size",
        "- Client SHA-256: $hash",
        "- Archive entries: $($entries.Count)",
        "- Class count: $($classes.Count)",
        "- Network candidates: $($candidateNames.Count)",
        "- Client JAR committed: false",
        "- Database changed: false",
        "",
        "## Manifest",
        "",
        '```text',
        $manifest.TrimEnd(),
        '```',
        "",
        "## Candidate classes",
        ""
    ) + ($candidateNames | Select-Object -First 80 | ForEach-Object { "- " + $_ }) + @(
        "",
        "## Bytecode evidence",
        "",
        '```text'
    ) + ($evidence | Select-Object -First 1200) + @(
        '```',
        "",
        "## Dispatcher command -30 detail",
        "",
        '```text'
    ) + $dispatcherDetail + @(
        '```',
        "",
        "## Command -30 handler al.h(bR)",
        "",
        '```text'
    ) + $loginHandlerDetail + @(
        '```',
        "",
        "## Nested command -123 handler in al.h(bR)",
        "",
        '```text'
    ) + $nestedMinus123Detail + @(
        '```',
        "",
        "## Version command -123 handler in al.e(bR)",
        "",
        '```text'
    ) + $versionHandlerDetail + @(
        '```'
    )
    Set-Content -Path $reportPath -Value ($reportLines -join [Environment]::NewLine) -Encoding UTF8

    Invoke-Git @("add", "--", "reports/windows/latest-v9-client-analysis.md")
    Invoke-Git @("commit", "-m", "ops: report client v9 bytecode analysis")
    $reportCommit = (& git rev-parse HEAD).Trim()
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "V9 client analysis report da commit local nhung push that bai." $LASTEXITCODE
    }

    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=V9_CLIENT_ANALYSIS_PUBLISHED"
    Write-Host "TESTED_COMMIT=$testedCommit"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "CLIENT_JAR_COMMITTED=false"
    Write-Host "DATABASE_CHANGED=false"
}

function Invoke-AppearanceSeedConvert {
    Assert-Repository
    Pull-Branch
    $mavenCommand = Resolve-MavenCommand
    New-Item -ItemType Directory -Force -Path $WorkDirectory | Out-Null
    $buildLog = Join-Path $WorkDirectory "appearance-seed-build.log"
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & $mavenCommand clean package 2>&1 | Tee-Object -FilePath $buildLog
    $buildExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    if ($buildExitCode -ne 0) {
        Stop-Workflow "Build/test truoc appearance convert that bai." $buildExitCode
    }

    $jar = Join-Path $RepositoryRoot "target\\nsocry-server-0.1.0-SNAPSHOT.jar"
    $configuration = Join-Path $RepositoryRoot "data-dry-run.properties"
    if (-not (Test-Path -LiteralPath $configuration -PathType Leaf)) {
        Stop-Workflow "Thieu data-dry-run.properties."
    }
    $archive = Join-Path $RepositoryRoot "data-dry-run-appearance-seed-candidate.zip"
    if (Test-Path -LiteralPath $archive) {
        Stop-Workflow "Appearance candidate da ton tai; khong ghi de: $archive"
    }

    $ErrorActionPreference = "Continue"
    $convertOutput = @(& java -jar $jar appearance-seed-convert $configuration 2>&1)
    $convertExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousPreference
    $convertOutput | ForEach-Object { Write-Host $_ }
    $joined = $convertOutput -join [Environment]::NewLine
    $success = ($convertExitCode -eq 0) -and
        $joined.Contains("APPEARANCE seed candidate VERIFIED") -and
        $joined.Contains("archiveRoundTripVerified=true") -and
        $joined.Contains("databaseChanged=false")

    $testedCommit = (& git rev-parse HEAD).Trim()
    $reportPath = Join-Path $RepositoryRoot "reports\\windows\\latest-appearance-seed-convert.md"
    $status = if ($success) { "VERIFIED" } else { "FAILED" }
    $reportLines = @(
        "# Appearance seed convert Windows",
        "",
        "- Tested commit: $testedCommit",
        "- Status: $status",
        "- Command exit code: $convertExitCode",
        "- Archive committed: false",
        "- Database changed: false",
        "- Runtime snapshot published: false",
        "- Server startup wired: false",
        "",
        "## Command output",
        ""
    ) + ($convertOutput | ForEach-Object { "    " + $_ })
    Set-Content -Path $reportPath -Value ($reportLines -join [Environment]::NewLine) -Encoding UTF8
    Invoke-Git @("add", "--", "reports/windows/latest-appearance-seed-convert.md")
    Invoke-Git @("commit", "-m", "ops: report appearance seed convert")
    $reportCommit = (& git rev-parse HEAD).Trim()
    & git -c gc.auto=0 -c maintenance.auto=false push origin $ExpectedBranch
    if ($LASTEXITCODE -ne 0) {
        Stop-Workflow "Appearance report da commit local nhung push that bai." $LASTEXITCODE
    }
    if (-not $success) {
        Write-Host "NSOCRY_WORKFLOW_RESULT=APPEARANCE_SEED_CONVERT_FAILED"
        Write-Host "REPORT_COMMIT=$reportCommit"
        exit 6
    }
    Write-Host ""
    Write-Host "NSOCRY_WORKFLOW_RESULT=APPEARANCE_SEED_CONVERT_VERIFIED"
    Write-Host "TESTED_COMMIT=$testedCommit"
    Write-Host "REPORT_COMMIT=$reportCommit"
    Write-Host "ARCHIVE_COMMITTED=false"
    Write-Host "DATABASE_CHANGED=false"
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
    "9" {
        Invoke-V9HandshakeCapture
    }
    "10" {
        Invoke-V9ClientAnalysis
    }
    "11" {
        Invoke-AppearanceSeedConvert
    }
}
