@echo off
setlocal enabledelayedexpansion

if not exist "%~dp0gradlew" (
    echo Error: gradlew not found in the current directory.
    exit /b 1
)

echo Starting all checks and tests...

call :run_gradle_task "spotlessApply --init-script gradle/init.gradle.kts --no-configuration-cache"
call :run_gradle_task "dependencyGuardBaseline"
call :run_gradle_task "updateProdReleaseBadging"
call :run_gradle_task ":app:lintProdRelease :lint:lint"

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