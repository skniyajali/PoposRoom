@echo off
setlocal enabledelayedexpansion

if not exist "%~dp0gradlew" (
    echo Error: gradlew not found in the current directory.
    exit /b 1
)

echo Starting all checks and tests...

call :run_gradle_task "testDemoDebug -P roborazzi.test.verify=false :lint:test"
call :run_gradle_task "connectedDemoDebugAndroidTest --daemon"
call :run_gradle_task "testDemoDebugUnitTest -P roborazzi.test.verify=false"
call :run_gradle_task "createDemoDebugCombinedCoverageReport"

echo All checks and tests completed successfully.
exit /b 0

:run_gradle_task
echo Running: %~1
call "%~dp0gradlew" %~1
if %ERRORLEVEL% neq 0 (
    echo Error: Task %~1 failed
    exit /b 1
)
exit /b 0