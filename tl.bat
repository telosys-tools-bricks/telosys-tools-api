@echo off
echo bat file name : %0
echo bat file dir  : %~dp0
echo arg1 : %1
echo arg2 : %2
java -jar %~dp0/telosys-launcher.jar %1 %2