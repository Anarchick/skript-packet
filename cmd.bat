@echo off
cmd.exe
gradle build && xcopy C:\Users\aeim\git\skript-packet\build\libs\Skript-Packet-2.1.0.jar C:\Users\aeim\Documents\minecraft\eclipse1.17\plugins\skript-packet.jar
pause