Y@echo off
REM ======================================================================
REM Shree Ranga Traders - Convenience script to build, run, or deploy.
REM ----------------------------------------------------------------------
REM Usage:
REM   run-app.bat                -> Run with embedded container (dev)
REM   run-app.bat run            -> Same as above
REM   run-app.bat package        -> mvn clean package (creates WAR)
REM   run-app.bat deploy         -> package + copy WAR to %TOMCAT_HOME%\webapps
REM   run-app.bat clean          -> mvn clean
REM Environment (optional):
REM   SERVER_PORT (default 8080)
REM   JAVA_HOME (if not on PATH)
REM   TOMCAT_HOME (for deploy action)
REM   SKIP_TESTS=true (to skip tests)
REM ======================================================================

setlocal ENABLEDELAYEDEXPANSION

set ACTION=%1
if "%ACTION%"=="" set ACTION=run

REM Default port if not provided
if not defined SERVER_PORT set SERVER_PORT=8080

REM Decide whether to skip tests
set TEST_FLAG=
if /I "%SKIP_TESTS%"=="true" set TEST_FLAG=-DskipTests

REM Check Java
where java >NUL 2>&1
if errorlevel 1 (
  if defined JAVA_HOME (
    echo [INFO] java not on PATH, trying JAVA_HOME bin
    set PATH=%JAVA_HOME%\bin;%PATH%
    where java >NUL 2>&1 || (
      echo [ERROR] Java not found. Set JAVA_HOME or add java to PATH.
      exit /b 1
    )
  ) else (
    echo [ERROR] Java not found. Install JDK 17+ and/or set JAVA_HOME.
    exit /b 1
  )
)

REM Check Maven
where mvn >NUL 2>&1
if errorlevel 1 (
  echo [ERROR] Maven not found on PATH. Install Maven and retry.
  exit /b 1
)

if /I "%ACTION%"=="clean" goto :CLEAN
if /I "%ACTION%"=="package" goto :PACKAGE
if /I "%ACTION%"=="deploy" goto :DEPLOY
if /I "%ACTION%"=="run" goto :RUN

echo [ERROR] Unknown action: %ACTION%
echo Valid actions: run package deploy clean
exit /b 1

:CLEAN
echo [TASK] Cleaning project...
mvn --no-transfer-progress clean
exit /b %ERRORLEVEL%

:PACKAGE
echo [TASK] Packaging WAR...
mvn --no-transfer-progress clean package %TEST_FLAG%
if errorlevel 1 (
  echo [ERROR] Maven package failed.
  exit /b 1
)
echo [OK] WAR created at target\shree-ranga-traders-1.0.0.war
exit /b 0

:DEPLOY
echo [TASK] Building and deploying WAR to external Tomcat...
if not defined TOMCAT_HOME (
  echo [ERROR] TOMCAT_HOME not set. Set TOMCAT_HOME to your Tomcat root.
  exit /b 1
)
mvn --no-transfer-progress clean package %TEST_FLAG%
if errorlevel 1 (
  echo [ERROR] Build failed; aborting deploy.
  exit /b 1
)
if not exist "%TOMCAT_HOME%\webapps" (
  echo [ERROR] %TOMCAT_HOME%\webapps not found. Check TOMCAT_HOME.
  exit /b 1
)
copy /Y "target\shree-ranga-traders-1.0.0.war" "%TOMCAT_HOME%\webapps\shree-ranga-traders.war" >NUL
if errorlevel 1 (
  echo [ERROR] Copy failed.
  exit /b 1
)
echo [OK] Deployed WAR. Start Tomcat and access: http://localhost:8080/shree-ranga-traders/
exit /b 0

:RUN
echo [TASK] Running with embedded server on port %SERVER_PORT% ...
REM spring-boot:run uses the main class directly; skip explicit package step for speed.
REM Add jvm args for memory tuning if desired via JVM_ARGS env var.
set JVM_ARGS=-Xms256m -Xmx512m %JVM_ARGS%

mvn --no-transfer-progress spring-boot:run %TEST_FLAG% -Dspring-boot.run.jvmArguments="%JVM_ARGS%" -Dspring-boot.run.arguments="--server.port=%SERVER_PORT%"
exit /b %ERRORLEVEL%

endlocal
