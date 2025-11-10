@echo off
echo Stopping LocalStack...
echo.

cd /d "%~dp0"

docker-compose down

echo.
echo LocalStack stopped successfully!
echo.
