@echo off
setlocal EnableExtensions

set "NEXA_JDK=C:\Program Files\Java\jdk-21.0.10"
if not exist "%NEXA_JDK%\bin\java.exe" set "NEXA_JDK=C:\Program Files\Java\jdk-21"

if not exist "%NEXA_JDK%\bin\java.exe" (
  echo [NEXA] No se encontro un JDK 21 compatible.
  echo [NEXA] Instala Java 21 o actualiza NEXA_JDK en tools\build-nexa.bat.
  exit /b 1
)

set "JAVA_HOME=%NEXA_JDK%"
set "PATH=%NEXA_JDK%\bin;%PATH%"

set "NEXA_TARGET=%~1"
if "%NEXA_TARGET%"=="" set "NEXA_TARGET=1.21.8"

if /I "%NEXA_TARGET%"=="all" (
  set "NEXA_TASK=releaseCandidate"
) else if /I "%NEXA_TARGET%"=="1.21.1" (
  set "NEXA_TASK=:fabric-1.21.1:verifiedSource"
) else if /I "%NEXA_TARGET%"=="1.21.8" (
  set "NEXA_TASK=:fabric-1.21.8:verifiedSource"
) else if /I "%NEXA_TARGET%"=="1.21.10" (
  set "NEXA_TASK=:fabric-1.21.10:verifiedSource"
) else (
  echo [NEXA] Target invalido: %NEXA_TARGET%
  echo Uso: tools\build-nexa.bat [1.21.1^|1.21.8^|1.21.10^|all]
  exit /b 2
)

echo [NEXA] Runtime:
"%JAVA_HOME%\bin\java.exe" -version
echo [NEXA] Ejecutando %NEXA_TASK%

call "%~dp0..\gradlew.bat" -g "%~dp0..\.gradle-user" %NEXA_TASK% --no-daemon --stacktrace
exit /b %ERRORLEVEL%
