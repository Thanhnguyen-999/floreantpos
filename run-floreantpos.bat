@echo off
REM ============================================================
REM  Launch script for FloreantPOS (Java Swing + embedded Derby)
REM  Generated to run the app after `mvn compile`.
REM ============================================================

REM --- Use JDK 8 (project targets Java 1.8) ---
set "JAVA_HOME=C:\Program Files\Java\jdk-1.8"

REM --- Run from the project root so the embedded Derby DB
REM     (database\derby-single\posdb) resolves correctly. ---
cd /d "%~dp0"

REM --- Build classpath: compiled classes + resources + all jars from the local Maven repo ---
setlocal EnableDelayedExpansion
set "CP=target\classes;resources;i18n;config"
for /r "%USERPROFILE%\.m2\repository" %%J in (*.jar) do set "CP=!CP!;%%J"

"%JAVA_HOME%\bin\java.exe" -cp "!CP!" com.floreantpos.main.Main %*

endlocal
