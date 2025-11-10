@echo off
echo ========================================
echo   Kinesis DynamoDB Consumer - Test Runner
echo ========================================
echo.

:menu
echo Select test option:
echo.
echo 1. Run all tests
echo 2. Run tests with coverage report
echo 3. Run specific test class
echo 4. Run tests in debug mode
echo 5. Clean and test
echo 6. Skip tests (build only)
echo 7. Exit
echo.

set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto run_all
if "%choice%"=="2" goto run_coverage
if "%choice%"=="3" goto run_specific
if "%choice%"=="4" goto run_debug
if "%choice%"=="5" goto clean_test
if "%choice%"=="6" goto skip_tests
if "%choice%"=="7" goto end

echo Invalid choice. Please try again.
echo.
goto menu

:run_all
echo.
echo Running all tests...
echo.
mvn test
goto end_test

:run_coverage
echo.
echo Running tests with coverage report...
echo.
mvn test jacoco:report
echo.
echo Coverage report generated at: target\site\jacoco\index.html
echo Opening coverage report...
start target\site\jacoco\index.html
goto end_test

:run_specific
echo.
echo Available test classes:
echo   - EventRecordTest
echo   - DynamoDbServiceTest
echo   - RecordProcessorTest
echo   - RecordProcessorFactoryTest
echo   - AwsConfigTest
echo   - KinesisDynamoDbApplicationTest
echo.
set /p testclass="Enter test class name: "
echo.
echo Running %testclass%...
echo.
mvn test -Dtest=%testclass%
goto end_test

:run_debug
echo.
echo Running tests in debug mode...
echo.
mvn test -X
goto end_test

:clean_test
echo.
echo Cleaning and running tests...
echo.
mvn clean test
goto end_test

:skip_tests
echo.
echo Building without tests...
echo.
mvn clean install -DskipTests
goto end_test

:end_test
echo.
echo ========================================
echo   Test execution completed!
echo ========================================
echo.
pause
goto menu

:end
echo.
echo Exiting...
echo.
