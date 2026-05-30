@echo off
setlocal enabledelayedexpansion

echo ================================================
echo   EMOTION FRIEND - Start Backend (Local Network)
echo ================================================

set ROOT=%~dp0

REM ── Check .env exists ───────────────────────────────────────────────────────
if not exist "%ROOT%.env" (
    echo.
    echo [ERROR] .env file not found!
    echo.
    echo Please create it first:
    echo   copy .env.example .env
    echo.
    echo Then edit .env and set:
    echo   MYSQL_ROOT_PASSWORD=your_password
    echo   ADMIN_TOKEN=your_admin_token
    echo   BACKEND_URL=http://^<YOUR_LAN_IP^>:80  ^(for physical device^)
    echo.
    pause
    exit /b 1
)

REM ── Detect LAN IP (first non-loopback IPv4) ─────────────────────────────────
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /i "IPv4" ^| findstr /v "127.0"') do (
    set LAN_IP=%%a
    goto :GOT_IP
)
:GOT_IP
set LAN_IP=%LAN_IP: =%

echo.
echo [INFO] Detected LAN IP: %LAN_IP%
echo [INFO] Android physical device should use: http://%LAN_IP%:80
echo [INFO] Android emulator should use: http://10.0.2.2:80
echo.

REM ── Start Docker Compose ────────────────────────────────────────────────────
echo [1] Starting MySQL + Backend + Nginx via Docker Compose...
cd /d "%ROOT%"
docker compose --env-file .env up -d --build

if errorlevel 1 (
    echo.
    echo [ERROR] Docker Compose failed to start!
    echo Make sure Docker Desktop is running.
    pause
    exit /b 1
)

echo.
echo [2] Waiting for backend to be healthy...
set /a COUNT=0
:WAIT_HEALTH
set /a COUNT+=1
if %COUNT% GTR 30 (
    echo [WARN] Backend health check timed out after 60s. Check logs:
    echo   docker compose logs backend
    goto :DONE
)
docker compose exec -T backend wget -qO- http://localhost:8080/health >nul 2>&1
if errorlevel 1 (
    timeout /t 2 /nobreak >nul
    goto :WAIT_HEALTH
)

:DONE
echo.
echo ================================================
echo   Backend is running!
echo ================================================
echo.
echo   API (emulator)      : http://10.0.2.2:80/api
echo   API (physical device): http://%LAN_IP%:80/api
echo   Admin panel (browser): http://localhost:3000
echo   Health check        : http://localhost:80/health
echo.
echo   View logs : docker compose logs -f
echo   Stop      : docker compose down
echo ================================================
pause
