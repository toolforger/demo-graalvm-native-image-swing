@echo off

set JAVA_HOME=C:\Users\jo\.jdks\bellsoft-liberica-vm-core-openjdk21-23.1.8

if not "%1"=="" goto ok
echo Usage: %0 [command]
echo Runs [command] with JAVA_HOME set to %JAVA_HOME%
exit /b 1

:ok
%*
