@echo off
setlocal
chcp 65001 >nul

rem Chay menu tu ban sao tam de git pull co the cap nhat BAT goc an toan.
if /I "%~1"=="__NSOCRY_TEMP__" goto TEMP_START
set "NSOCRY_TEMP_BAT=%TEMP%\nsocry-work-%RANDOM%-%RANDOM%.bat"
copy /y "%~f0" "%NSOCRY_TEMP_BAT%" >nul
if errorlevel 1 (
  echo Khong the tao ban sao BAT tam.
  pause
  exit /b 1
)
call "%NSOCRY_TEMP_BAT%" __NSOCRY_TEMP__ "%~dp0" "%~1"
set "NSOCRY_EXIT=%ERRORLEVEL%"
del /q "%NSOCRY_TEMP_BAT%" >nul 2>&1
exit /b %NSOCRY_EXIT%

:TEMP_START
cd /d "%~2"
if not "%~3"=="" goto DIRECT

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
echo [7] Publish DATA snapshot CO LAP (database read-only)
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
if "%NSOCRY_ACTION%"=="7" goto RUN

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
powershell -NoProfile -ExecutionPolicy Bypass -File ".\tools\nsocry-work.ps1" -Action "%~3"
set "NSOCRY_EXIT=%ERRORLEVEL%"
echo.
pause
exit /b %NSOCRY_EXIT%
