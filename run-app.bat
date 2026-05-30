@echo off
setlocal enabledelayedexpansion
echo ================================================
echo   EMOTION FRIEND - Run Android App
echo ================================================

set ADB=C:\Users\NTNghia\AppData\Local\Android\Sdk\platform-tools\adb.exe
set EMULATOR=C:\Users\NTNghia\AppData\Local\Android\Sdk\emulator\emulator.exe
set ANDROID_APP=D:\_CODE_BANK\Project_\_Best Project_\emotion-friend\android-app
set ROOT=D:\_CODE_BANK\Project_\_Best Project_\emotion-friend
set PACKAGE=com.emotionfriend

REM ── Load BACKEND_URL from .env if present ──────────────────────────────────
set BACKEND_URL=http://10.0.2.2:80
if exist "%ROOT%\.env" (
    for /f "usebackq tokens=1,* delims==" %%a in ("%ROOT%\.env") do (
        if "%%a"=="BACKEND_URL" set BACKEND_URL=%%b
    )
)
echo.
echo [INFO] BACKEND_URL = %BACKEND_URL%
echo [INFO] Make sure backend is running: start-backend.bat
echo.

echo [1] Checking connected devices...
%ADB% devices

echo.
echo [2] Checking if emulator is running...
%ADB% devices | findstr "emulator"
if errorlevel 1 (
    echo No emulator found. Starting Medium_Phone_API_36.1...
    start "" "%EMULATOR%" -avd Medium_Phone_API_36.1
    echo Waiting 30 seconds for emulator to boot...
    timeout /t 30 /nobreak
    echo Waiting for boot_completed...
    :WAIT_BOOT
    %ADB% shell getprop sys.boot_completed 2>nul | findstr "1"
    if errorlevel 1 (
        timeout /t 5 /nobreak >nul
        goto WAIT_BOOT
    )
    echo Emulator is ready!
) else (
    echo Emulator already running.
)

echo.
echo [3] Building and installing APK (real DB, url=%BACKEND_URL%)...
cd /d "%ANDROID_APP%"
call gradlew.bat :app:installDebug
if errorlevel 1 (
    echo BUILD FAILED!
    pause
    exit /b 1
)

echo.
echo [4] Launching app...
%ADB% shell am start -n %PACKAGE%/.MainActivity
echo.
echo ================================================
echo   App launched! Connecting to: %BACKEND_URL%
echo ================================================
pause

