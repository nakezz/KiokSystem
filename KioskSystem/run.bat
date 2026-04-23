@echo off
cd /d "%~dp0"

echo Compiling Java files...
if not exist out\production\KioskSystem mkdir out\production\KioskSystem
"C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\bin\javac.exe" -encoding UTF-8 -d out/production/KioskSystem src\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Starting MUST Kiosk System...
"C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\bin\java.exe" -cp out/production/KioskSystem KioskMain
