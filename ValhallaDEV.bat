@echo off
title ValhallaMS: inactive
color 1b
echo ValhallaMS Launcher
echo    
echo.
echo Ready to execute, press any key
pause >nul
cls
color 4c
title ValhallaDEV: activating 0/3
start /b launch_world.bat
title ValhallaDEV: activating 1/3
ping localhost -w 10000 >nul
start /b launch_login.bat >nul
title ValhallaDEV: activating 2/3
ping localhost -w 10000 >nul
start /b launch_channel.bat >nul
title MsSoul: activating 3/3
ping localhost -w 10000 >nul
color 2a
title ValhallaMS: Fully Active
