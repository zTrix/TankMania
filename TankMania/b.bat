::@echo off
set main=src\TankManiaMain.java
set srcdir=src
set outputdir=bin
set jc=javac
set opt=-verbose
set lib=src\lib\PureMVC_Java_0_3.jar
set suc=false

%jc% -d %outputdir% -sourcepath %srcdir% -classpath %lib% %main% && set suc=true

if "%suc%"=="false" (
pause > nul
goto :eof
)

