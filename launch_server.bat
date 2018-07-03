@echo off
color 4c
title HurricaneMS: activating 0/3
start /b launch_world.bat
title HurricaneMS: activating 1/3
ping localhost -w 10000 >nul
start /b launch_login.bat >nul
title HurricaneMS: activating 2/3
ping localhost -w 10000 >nul
start launch_channel.bat >nul
title HurricaneMS: activating 3/3
ping localhost -w 10000 >nul
COLOR 3
title HurricaneMS ::: fully active