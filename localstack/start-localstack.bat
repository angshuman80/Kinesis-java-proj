@echo off
echo Starting LocalStack with Kinesis and DynamoDB...
echo.

cd /d "%~dp0"

docker-compose up -d

echo.
echo Waiting for LocalStack to initialize (30 seconds)...
timeout /t 30 /nobreak > nul

echo.
echo Checking LocalStack status...
docker-compose ps

echo.
echo LocalStack is running!
echo.
echo Endpoints:
echo   - LocalStack Gateway: http://localhost:4566
echo   - Kinesis Stream: my-stream
echo   - DynamoDB Table: my-table
echo.
echo To view logs: docker-compose logs -f
echo To stop: docker-compose down
echo.
