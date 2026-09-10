@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"

if not "%~1"=="" goto DIRECT

:MENU
cls
echo ============================================================
echo             NSOCRY WINDOWS WORK MENU
echo ============================================================
echo Repo: %CD%
echo Branch bat buoc: agent/document-nsokiss-runtime
echo.
echo [1] Pull + build/test + gui bao cao len GitHub  (khuyen dung)
echo [2] Chi pull code moi
echo [3] Build/test + gui bao cao, khong pull
echo [4] Xem bao cao Windows gan nhat tren may
echo [5] Chay DATA import plan OFFLINE (khong mo database)
echo [6] IMPORT DATA V7 vao NSOCry (REJECT_EXISTING)
echo [0] Thoat
echo.
set /p "NSOCRY_ACTION=Chon mot so: "

if "%NSOCRY_ACTION%"=="0" exit /b 0
if "%NSOCRY_ACTION%"=="1" goto RUN
if "%NSOCRY_ACTION%"=="2" goto RUN
if "%NSOCRY_ACTION%"=="3" goto RUN
if "%NSOCRY_ACTION%"=="4" goto RUN
if "%NSOCRY_ACTION%"=="5" goto RUN
if "%NSOCRY_ACTION%"=="6" goto RUN

echo Lua chon khong hop le.
pause
goto MENU

:RUN
powershell -NoProfile -ExecutionPolicy Bypass -File ".\tools\nsocry-work.ps1" -Action "%NSOCRY_ACTION%"
set "NSOCRY_EXIT=%ERRORLEVEL%"
echo.
if not "%NSOCRY_EXIT%"=="0" echo Quy trinh dung voi ma loi %NSOCRY_EXIT%.
pause
goto MENU

:DIRECT
powershell -NoProfile -ExecutionPolicy Bypass -File ".\tools\nsocry-work.ps1" -Action "%~1"
set "NSOCRY_EXIT=%ERRORLEVEL%"
echo.
pause
exit /b %NSOCRY_EXIT%
