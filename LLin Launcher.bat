@echo off 
set a=0
title Server: inactive 
color 1b
if "%1"=="start" goto :start 
echo L.Lin Launcher V13.0
echo By L.Lin
echo. 
echo Commands: 
echo --------- 
echo start - Start Server
echo shutdown - Shutdown Server. Will not cause roll back
echo restart - Restarts your server without roll back
echo auto-restart - Automatically restarts the server after a specified amount of time
echo.
echo Java commands:
echo --------------
echo compile - compile you source
echo clean and build - Deletes your old odinms.jar and builds a new one
echo.
pause
echo.
echo MySQL commands:
echo ---------------
echo backup - creates a backup of your server
echo password - changes the password of some account. Remember to encrypt it with sha1 first!
echo shop - add a shop with shop id and npc id
echo shopitem - add items to a shop with shop id, price id and item id
echo monsterdrops - add monster drops
echo clearcheatlog - clears your cheatlog
echo loginfix - fixs your login
echo nx - add NX to all accounts
echo delsi - Delete an item from all shops
echo negexp - Negative experience fix
echo.
echo MySQL tables:
echo -------------
echo accounts - opens the table accounts
echo characters - opens the table characters
echo gmlog - opens the table gmlog
echo inventoryequipment - opens the table inventoryequipment
echo inventoryitems - opens the table inventoryitems
echo shops - opens the table shops
echo shopitems - opens the table shopitems
echo storages - opens the table storages
echo.
echo MySQL misc:
echo -----------
echo mysql - enter querys that are not included
echo.
echo Misc:
echo -----
echo diff - apply .diff patch
echo reset - reset path and database
echo save (MySQL table name) - Saves the table in the database
echo.
if EXIST %windir%\replace.vbs (
goto :existdatabase
) else (
goto :makereplace
)

:makereplace
ECHO Const ForReading = 1 > replace.vbs
ECHO Const ForWriting = 2 >> replace.vbs
ECHO. >> replace.vbs
ECHO strFileName = Wscript.Arguments(0) >> replace.vbs
ECHO strOldText = Wscript.Arguments(1) >> replace.vbs
ECHO strNewText = Wscript.Arguments(2) >> replace.vbs
ECHO. >> replace.vbs
ECHO Set objFSO = CreateObject("Scripting.FileSystemObject") >> replace.vbs
ECHO Set objFile = objFSO.OpenTextFile(strFileName, ForReading) >> replace.vbs
ECHO. >> replace.vbs
ECHO strText = objFile.ReadAll >> replace.vbs
ECHO objFile.Close >> replace.vbs
ECHO strNewText = Replace(strText, strOldText, strNewText) >> replace.vbs
ECHO. >> replace.vbs
ECHO Set objFile = objFSO.OpenTextFile(strFileName, ForWriting) >> replace.vbs
ECHO objFile.WriteLine strNewText >> replace.vbs
ECHO objFile.Close >> replace.vbs
move replace.vbs %windir%
goto :existdatabase

:existdatabase
if EXIST database.ini (
set /p database=<database.ini
goto :setpath
) else (
goto :setdatabase
)

:setdatabase
find "3306" "db.properties">mysql.tmp
ping localhost -n 5 >nul
echo @echo off>lol.bat
echo for /f "skip=2" lalala%%A in (>>lol.bat
echo 2B>>lol.bat
echo 3C>>lol.bat
echo 4D>>lol.bat
echo )>>lol.bat
echo exit>>lol.bat
replace.vbs lol.bat "2B" "mysql.tmp"
replace.vbs lol.bat "3C" ") do ("
replace.vbs lol.bat "4D" "echo lalala%%A>>database.ini"
replace.vbs lol.bat "lalala" "%%"
ping localhost -n 5 >nul
start lol.bat
replace.vbs database.ini "url=jdbc:mysql://localhost:3306/" ""
del mysql.tmp
del lol.bat
goto :existdatabase

:setpath
if EXIST settings.ini (
set /p PATH=<settings.ini
echo.
goto :command
) else (
goto :choose
)

:choose
echo Do you want to set the path manually or automatically (not recommended for people with lots of files in their root drive)?
echo Manual - M
echo Automatic - A
set /p choose=
if %choose%==A goto :setsql
if %choose%==a goto :setsql
goto :mpath

:mpath
set /p j="Enter path of JDK folder(JDK FOLDER ONLY. NOT BIN FILE. E.G C:\Program Files\Java\JDK1.6.0_10): "
echo.
set /p w="Enter path of WinRAR folder(E.G C:\Program Files\WinRAR): "
echo.
set /p s="Enter path of MySQL Server(E.G C:\Program Files\MySQL\MySQL Server 5.0): "
echo %path%>%j%\bin;%w%;%s%\bin;> settings.ini
goto :setpath

:setsql
if exist mysql.ini (
goto :setwinrar
) else (
goto :makesql
)

:makesql
set here=%cd%
cd %windir%
cd ..
ATTRIB /s "mysql.exe">mysql.ini
move mysql.ini \WINDOWS
cd %here%
move %windir%\mysql.ini 
goto :checksql

:checksql
replace.vbs mysql.ini "A          " ""
set /p sqlp=<mysql.ini
goto :checksql2

:checksql2
if "%sqlp%"=="File not found - mysql.exe" (
goto :dsql
) else (
goto :setwinrar
)

:dsql
ECHO You have not downloaded MySQL yet. The file is being downloaded.
start http://dev.mysql.com/get/Downloads/MySQL-5.0/mysql-essential-5.0.67-win32.msi/from/http://mysql.easynet.be/
del mysql.ini
exit

:setwinrar
if exist winrar.ini (
goto :setjava
) else (
goto :makewinrar
)

:makewinrar
set here=%cd%
cd %windir%
cd ..
ATTRIB /s "winrar.exe">winrar.ini
move winrar.ini %windir%
cd %here%
move %windir%\winrar.ini
goto :checkwinrar

:checkwinrar
replace.vbs winrar.ini "A          " ""
set /p wrp=<winrar.ini
goto :checkwinrar2

:checkwinrar2
if "%wrp%"=="File not found - winrar.exe" (
goto :dwinrar
) else (
goto :setjava
)

:dwinrar
ECHO You have not downloaded WinRar yet. The file is being downloaded.
start http://www.rarlab.com/rar/wrar380.exe
del winrar.ini
exit

:setjava
if exist javac.ini (
goto :makepath
) else (
goto :makejava
)

:makejava
set here=%cd%
cd %windir%
cd ..
ATTRIB /s "javac.exe">javac.ini
move javac.ini %windir%
cd %here%
move %windir%\javac.ini
goto :checkjavac

:checkjavac
replace.vbs javac.ini "A          " ""
set /p jcp=<javac.ini
goto :checkjavac2

:checkjavac2
if "%jcp%"=="File not found - javac.exe" (
goto :djava
) else (
goto :makepath
)

:djava
ECHO You have not downloaded JDK yet. The file is being downloaded.
start http://cds.sun.com/is-bin/INTERSHOP.enfinity/WFS/CDS-CDS_Developer-Site/en_US/-/USD/VerifyItem-Start/jre-6u10-windows-i586-p.exe?BundledLineItemUUID=opxIBe.n01cAAAEdryE9SUQE&OrderID=CxhIBe.n6eAAAAEdlyE9SUQE&ProductID=PLNIBe.npD0AAAEbpuoKz7Lc&FileName=/jre-6u10-windows-i586-p.exe
del javac.ini
exit

:makepath
set /p sqlp=<mysql.ini
set /p wrp=<winrar.ini
set /p jcp=<javac.ini
echo %path%;%sqlp%;%wrp%;%jcp%;>settings.ini
del mysql.ini
del winrar.ini
del javac.ini
replace.vbs settings.ini "javac.exe" ""
replace.vbs settings.ini "WinRAR.exe" ""
replace.vbs settings.ini "mysql.exe" ""
goto :command

:command
set /p s="Enter command: " 
if "%s%"=="start" goto :start 
if "%s%"=="shutdown" goto :shutdown
if "%s%"=="restart" goto :restart 
if "%s%"=="auto-restart" goto :arestart 
if "%s%"=="compile" goto :compile
if "%s%"=="clean and build" goto :c&b
if "%s%"=="backup" goto :backup
if "%s%"=="password" goto :password
if "%s%"=="shop" goto :shop
if "%s%"=="shopitem" goto :shopitem
if "%s%"=="monsterdrops" goto :monsterdrops
if "%s%"=="clearcheatlog" goto :clearcheatlog
if "%s%"=="loginfix" goto :loginfix
if "%s%"=="accounts" goto :accounts
if "%s%"=="characters" goto :characters
if "%s%"=="gmlog" goto :gmlog
if "%s%"=="inventoryequipment" goto :inventoryequipment
if "%s%"=="inventoryitems" goto :inventoryitems
if "%s%"=="mysql" goto :mysql
if "%s%"=="nx" goto :nx
if "%s%"=="delsi" goto :delsi
if "%s%"=="ne" goto :ne
if "%s%"=="diff" goto :diff
if "%s%"=="reset" goto :reset
if "%s%"=="shops" goto :shops
if "%s%"=="shopitems" goto :shopitems
if "%s%"=="storages" goto :storages
if "%s%"=="save" goto :save
if "%s%"=="save accounts" goto :saveacc
if "%s%"=="save characters" goto :savechar
if "%s%"=="save shops" goto :saveshops
if "%s%"=="save shopitems" goto :saveshopitems
if "%s%"=="save storages" goto :savestorages

echo Incorrect command! 
echo. 
goto :command

:start
if "%a%"=="1" ( 
echo Server is already active! 
echo. 
goto :command 
) 
color 4c 
title Server: activating 0/3 
start /b launch_world.bat 
title Server: activating 1/3 
ping localhost -w 10000 >nul 
start /b launch_login.bat 
title Server: activating 2/3 
ping localhost -w 10000 >nul 
start /b launch_channel.bat 
title Server: activating 3/3 
ping localhost -w 10000 >nul 
color 2a 
title Server: fully active 
set a=1
replace.vbs launch_channel.bat "pause" "exit"
replace.vbs launch_login.bat "pause" "exit"
replace.vbs launch_world.bat "pause" "exit"
goto :command 

:shutdown 
color 4c 
title Server: shutting down 
echo DO NOT PRESS ANYTHING! Please wait
ipconfig /release >nul
echo 10
ping localhost -n 2 >nul
echo 9
ping localhost -n 2 >nul
echo 8
ping localhost -n 2 >nul
echo 7
ping localhost -n 2 >nul
echo 6
ping localhost -n 2 >nul
echo 5
ping localhost -n 2 >nul
echo 4
ping localhost -n 2 >nul
echo 3
ping localhost -n 2 >nul
echo 2
ping localhost -n 2 >nul
echo 1
ipconfig /renew >nul 
taskkill /im cmd.exe 

:compile
color 2
cd src
echo @echo off>java.bat
attrib /s /d>1.dat
find 1.dat "           ">2.dat
find /v ".svn" 2.dat>3.dat
replace.vbs 3.dat "           %cd%\" ""
goto :mkjava

:mkjava
for /f "skip=4" %%A in (
3.dat
) do (
echo %%A\*.java>>java.bat
)
pause
del 1.dat
del 2.dat
del 3.dat
goto :mkjava2

:mkjava2
set here=%cd%
echo winrar a odinms.jarLinRocks>win.bat
goto :mkjava3

:mkjava3
FOR /f "skip=1" %%A in (
java.bat
) do (
echo %%A\*.classLinRocks>>win.bat
)
replace.vbs java.bat "net\sf\odinms" "javac net\sf\odinms"
replace.vbs win.bat "LinRocks" " ^"
replace.vbs win.bat "\*.java" ""
echo echo Done>>win.bat
type java.bat>delete.bat
replace.vbs delete.bat "javac" "del"
replace.vbs delete.bat "*.java" "*.class"
echo start win.bat>>java.bat
echo set here="%cd%">>win.bat
echo move odinms.jar ..>>win.bat
echo cd ..>>win.bat
echo move odinms.jar dist>>win.bat
echo cd src>>win.bat
echo start delete.bat>>win.bat
start java.bat
cd ..
echo XCOPY odinms.jar "%programfiles%" /y>dist\copy.bat
echo cd %programfiles%>>dist\copy.bat
echo move odinms.jar \java>>dist\copy.bat
echo cd java>>dist\copy.bat
echo attrib /s /d mina-core.jarLLin>>dist\copy.bat
echo replace.vbs ext.bat "A          " "copy odinms.jar ">>dist\copy.bat
echo replace.vbs ext.bat "\mina-core.jar" " /y">>dist\copy.bat
echo replace.vbs ext.bat "%programfiles%\Java\" "">>dist\copy.bat
echo start ext.bat>>dist\copy.bat
echo echo exitLLin>>dist\copy.bat
echo exit>>dist\copy.bat
cd dist
replace.vbs copy.bat "LLin" ">ext.bat"
cd ..
cd src
echo cd ..>>delete.bat
echo cd dist>>delete.bat
echo start copy.bat>>delete.bat
echo cd ..>>delete.bat
echo cd src>>delete.bat
echo del java.bat>>delete.bat
echo del win.bat>>delete.bat
echo del delete.bat>>delete.bat
echo exit>>java.bat
echo exit>>delete.bat
echo exit>>win.bat
echo Another window should pop out. Those windows are compiling, so please wait for a WinRar error to pop out, then when only 1 pop-up box remains, you may close all the windows. In the mean time, you may continue doing other things.
cd ..
goto :command 

:c&b
color 2
cd dist
del odinms.jar
cd ..
cd src
javac -nowarn net\sf\odinms\tools\*.java >nul
javac -nowarn net\sf\odinms\tools\performance\*.java >nul
javac -nowarn net\sf\odinms\tools\data\input\*.java >nul
javac -nowarn net\sf\odinms\tools\data\output\*.java >nul
javac -nowarn net\sf\odinms\server\*.java >nul
javac -nowarn net\sf\odinms\server\life\*.java >nul
javac -nowarn net\sf\odinms\server\maps\*.java >nul
javac -nowarn net\sf\odinms\server\movement\*.java >nul
javac -nowarn net\sf\odinms\server\quest\*.java >nul
javac -nowarn net\sf\odinms\scripting\*.java >nul
javac -nowarn net\sf\odinms\scripting\event\*.java >nul
javac -nowarn net\sf\odinms\scripting\npc\*.java >nul
javac -nowarn net\sf\odinms\scripting\portal\*.java >nul
javac -nowarn net\sf\odinms\scripting\reactor\*.java >nul
javac -nowarn net\sf\odinms\provider\*.java >nul
javac -nowarn net\sf\odinms\provider\wz\*.java >nul
javac -nowarn net\sf\odinms\provider\xmlwz\*.java >nul
javac -nowarn net\sf\odinms\net\*.java >nul
javac -nowarn net\sf\odinms\net\channel\*.java >nul
javac -nowarn net\sf\odinms\net\channel\pvp\*.java >nul
javac -nowarn net\sf\odinms\net\channel\handler\*.java >nul
javac -nowarn net\sf\odinms\net\handler\*.java >nul
javac -nowarn net\sf\odinms\net\login\*.java >nul
javac -nowarn net\sf\odinms\net\login\handler\*.java >nul
javac -nowarn net\sf\odinms\net\mina\*.java >nul
javac -nowarn net\sf\odinms\net\world\*.java >nul
javac -nowarn net\sf\odinms\net\world\guild\*.java >nul
javac -nowarn net\sf\odinms\net\world\remote\*.java >nul
javac -nowarn net\sf\odinms\database\*.java >nul
javac -nowarn net\sf\odinms\client\*.java >nul
javac -nowarn net\sf\odinms\client\anticheat\*.java >nul
javac -nowarn net\sf\odinms\client\messages\*.java >nul
javac -nowarn net\sf\odinms\client\messages\commands\*.java >nul
javac -nowarn net\sf\odinms\client\status\*.java >nul
winrar a odinms.jar net\sf\odinms\tools\*.class net\sf\odinms\tools\data\input\*.class net\sf\odinms\tools\data\output\*.class net\sf\odinms\server\*.class net\sf\odinms\server\life\*.class net\sf\odinms\server\maps\*.class net\sf\odinms\server\movement\*.class net\sf\odinms\server\quest\*.class net\sf\odinms\scripting\*.class net\sf\odinms\provider\*.class net\sf\odinms\provider\wz\*.class net\sf\odinms\net\*.class net\sf\odinms\net\channel\*.class net\sf\odinms\net\channel\remote\*.class net\sf\odinms\net\channel\handler\*.class net\sf\odinms\net\handler\*.class net\sf\odinms\net\login\*.class net\sf\odinms\net\login\remote\*.class net\sf\odinms\net\login\handler\*.class net\sf\odinms\net\mina\*.class net\sf\odinms\net\world\*.class net\sf\odinms\net\world\remote\*.class net\sf\odinms\database\*.class net\sf\odinms\client\*.class net\sf\odinms\client\anticheat\*.class net\sf\odinms\client\messages\*.class net\sf\odinms\client\status\*.class net\sf\odinms\client\messages\commands\*.class net\sf\odinms\provider\xmlwz\*.class net\sf\odinms\scripting\*.class net\sf\odinms\scripting\event\*.class net\sf\odinms\scripting\npc\*.class net\sf\odinms\scripting\portal\*.class net\sf\odinms\scripting\reactor\*.class net\sf\odinms\tools\performance\*.class net\sf\odinms\net\world\guild\*.class net\sf\odinms\net\channel\pvp\*.class META-INF
move odinms.jar ..
cd ..
move odinms.jar dist
cd src
del net\sf\odinms\tools\*.class >nul
del net\sf\odinms\tools\performance\*.class >nul
del net\sf\odinms\tools\data\input\*.class >nul
del net\sf\odinms\tools\data\output\*.class >nul
del net\sf\odinms\server\*.class >nul
del net\sf\odinms\server\life\*.class >nul
del net\sf\odinms\server\maps\*.class >nul
del net\sf\odinms\server\movement\*.class >nul
del net\sf\odinms\server\quest\*.class >nul
del net\sf\odinms\scripting\*.class >nul
del net\sf\odinms\scripting\event\*.class >nul
del net\sf\odinms\scripting\npc\*.class >nul
del net\sf\odinms\scripting\portal\*.class >nul
del net\sf\odinms\scripting\reactor\*.class >nul
del net\sf\odinms\provider\*.class >nul
del net\sf\odinms\provider\wz\*.class >nul
del net\sf\odinms\provider\xmlwz\*.class >nul
del net\sf\odinms\net\*.class >nul
del net\sf\odinms\net\channel\*.class >nul
del net\sf\odinms\net\channel\pvp\*.class >nul
del net\sf\odinms\net\channel\handler\*.class >nul
del net\sf\odinms\net\handler\*.class >nul
del net\sf\odinms\net\login\*.class >nul
del net\sf\odinms\net\login\handler\*.class >nul
del net\sf\odinms\net\mina\*.class >nul
del net\sf\odinms\net\world\*.class >nul
del net\sf\odinms\net\world\guild\*.class >nul
del net\sf\odinms\net\world\remote\*.class >nul
del net\sf\odinms\database\*.class >nul
del net\sf\odinms\client\*.class >nul
del net\sf\odinms\client\anticheat\*.class >nul
del net\sf\odinms\client\messages\*.class >nul
del net\sf\odinms\client\messages\commands\*.class >nul
del net\sf\odinms\client\status\*.class >nul
cd ..
echo Done
goto :command 

:restart
color 4c 
title Server: restarting
echo DO NOT PRESS ANYTHING! Please wait
ipconfig /release >nul
echo 10
ping localhost -n 2 >nul
echo 9
ping localhost -n 2 >nul
echo 8
ping localhost -n 2 >nul
echo 7
ping localhost -n 2 >nul
echo 6
ping localhost -n 2 >nul
echo 5
ping localhost -n 2 >nul
echo 4
ping localhost -n 2 >nul
echo 3
ping localhost -n 2 >nul
echo 2
ping localhost -n 2 >nul
echo 1
taskkill /f /im java.exe
ipconfig /renew >nul 
set a=0
goto :start

:arestart
set /p t="Enter time(In minutes): "
set /a t=%t%*59
echo @echo off>AutoRestarter.bat
echo TITLE Auto Restarter>>AutoRestarter.bat
echo ping -n %t% 127.0.0.1 LinRocks>>AutoRestarter.bat
echo ipconfig /release LinRocks>>AutoRestarter.bat
echo ipconfig /renew LinRocks>>AutoRestarter.bat
echo taskkill /f /im java.exe LinRocks>>AutoRestarter.bat
echo cls>>AutoRestarter.bat
echo "LLin Launcher.bat" start>>AutoRestarter.bat
echo exit>>AutoRestarter.bat
replace.vbs AutoRestarter.bat "LinRocks" ">nul"
start AutoRestarter.bat
goto :command

:backup
if EXIST backup ( 
cd backup
) else (
mkdir backup
cd backup
) 
echo %date%>1.dat
replace.vbs 1.dat "Mon " ""
replace.vbs 1.dat "Tue " ""
replace.vbs 1.dat "Wed " ""
replace.vbs 1.dat "Thur " ""
replace.vbs 1.dat "Fri " ""
replace.vbs 1.dat "Sat " ""
replace.vbs 1.dat "Sun " ""
replace.vbs 1.dat "/" "."
set /p d=<1.dat
mysqldump "%database%" > "%d%.sql" -u root -p
cd ..
echo Done
goto :command

:password

set /p p="Enter encrypted password: "
set /p n="Enter account username: "
echo update accounts set password = "%p%" where name = "%n%"; > password.dat
echo update accounts set salt = null where name = "%n%"; > salt.dat
mysql -u root -p "%database%" < password.dat
mysql -u root -p "%database%" < salt.dat
del password.dat
del salt.dat
echo Done
goto :command

:shop
set /p nid="Enter npc id: "
echo INSERT INTO `shops` (`npcid`) VALUES ('%nid%'); > shop.dat
mysql -u root -p "%database%" < shop.dat
del shop.dat
echo SELECT shopid FROM shops where npcid='%nid%'; > shops.dat
mysql -u root -p "%database%" < shops.dat > shop.dat
del shops.dat
flr.bat shop.dat shop2.dat
set /p sid=<shop2.dat
if exist shops (
goto :existshops
) else (
goto :noshops
)

:existshops
echo shop id of %nid% is %sid% > shops\%nid%.dat
echo Done
goto :command

:noshops
mkdir shops
echo shop id of %nid% is %sid% > shops\%nid%.dat
echo Done
goto :command

:shopitem
set /p sid="Enter shop id: "
set /p i="Enter item id: "
set /p pr="Enter price: "
echo INSERT INTO `shopitems` (`shopid`,`itemid`,`price`,`position`) VALUES ('%sid%', '%i%', '%pr%', '0'); > shopitems.dat
mysql -u root -p "%database%" < shopitems.dat
del shopitems.dat
echo Done
goto :command

:monsterdrops
set /p mid="Enter monster id: "
set /p i="Enter item id: "
set /p c="Enter Chance: "
echo INSERT INTO `monsterdrops` (`monsterid`,`itemid`,`chance`) VALUES ('%mid%','%i%','%c%'); > monsterdrops.dat
mysql -u root -p "%database%" < monsterdrops.dat
del monsterdrops.dat
echo Done
goto :command

:clearcheatlog
echo DROP TABLE IF EXISTS `cheatlog`; CREATE TABLE `cheatlog` ( `id` int(11) NOT NULL auto_increment, `cid` int(11) NOT NULL default '0', `offense` tinytext NOT NULL, `count` int(11) NOT NULL default '0', `lastoffensetime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP, `param` tinytext NOT NULL, PRIMARY KEY  (`id`), KEY `cid` (`cid`) ) ENGINE=InnoDB AUTO_INCREMENT=33004 DEFAULT CHARSET=latin1;> clearcheatlog.dat
mysql -u root -p "%database%" < clearcheatlog.dat
del clearcheatlog.dat
echo Done
goto :command

:loginfix
echo Update accounts SET loggedin = 0 > alfix.dat
mysql -u root -p "%database%" < alfix.dat
del alfix.dat
echo Done
goto :command

:accounts
if EXIST tables (
echo SELECT * FROM accounts; > accounts.dat
mysql -u root -p "%database%" < accounts.dat > tables/accounts.xls
del accounts.dat
echo Done
start tables/accounts.xls
goto :command
) else (
mkdir tables
goto :accounts
)

:characters
if EXIST tables (
echo SELECT * FROM characters; > characters.dat
mysql -u root -p "%database%" < characters.dat > tables/characters.xls
del characters.dat
echo Done
start tables/characters.xls
goto :command
) else (
mkdir tables
goto :characters
)

:gmlog
if EXIST tables (
echo SELECT * FROM gmlog; > gmlog.dat
mysql -u root -p "%database%" < gmlog.dat > tables/gmlog.xls
del gmlog.dat
echo Done
start tables/gmlog.xls
goto :command
) else (
mkdir tables
goto :gmlog
)

:inventoryequipment
if EXIST tables (
echo SELECT * FROM inventoryequipment; > inventoryequipment.dat
mysql -u root -p "%database%" < inventoryequipment.dat > tables/inventoryequipment.xls
del inventoryequipment.dat
echo Done
start tables/inventoryequipment.xls
goto :command
) else (
mkdir tables
goto :inventoryequipment
)

:inventoryitems
if EXIST tables (
echo SELECT * FROM inventoryitems; > inventoryitems.dat
mysql -u root -p "%database%" < inventoryitems.dat > tables/inventoryitems.xls
del inventoryitems.dat
echo Done
start tables/inventoryitems.xls
goto :command
) else (
mkdir tables
goto :inventoryitems
)

:shops
if EXIST tables (
echo SELECT * FROM shops; > shops.dat
mysql -u root -p "%database%" < shops.dat > tables/shops.xls
del shops.dat
echo Done
start tables/shops.xls
goto :command
) else (
mkdir tables
goto :shops
)

:shopitems
if EXIST tables (
echo SELECT * FROM shopitems; > shopitems.dat
mysql -u root -p "%database%" < shopitems.dat > tables/shopitems.xls
del shopitems.dat
echo Done
start tables/shopitems.xls
goto :command
) else (
mkdir tables
goto :shopitems
)

:storages
if EXIST tables (
echo SELECT * FROM storages; > storages.dat
mysql -u root -p "%database%" < storages.dat > tables/storages.xls
del storages.dat
echo Done
start tables/storages.xls
goto :command
) else (
mkdir tables
goto :storages
)

:loginfix
set /p mysql="Enter MySQL query: "
echo "%mysql%" > mysql.dat
mysql -u root -p "%database%" < mysql.dat
del mysql.dat
echo Done
goto :command

:nx
set /p nx="Enter NX cash you would like to add to everyone's account: "
echo update accounts set nxcash = nxcash + "%nx%" > nx.dat
mysql -u root -p "%database%" < nx.dat
del nx.dat
echo Done
goto :command

:delsi
set /p delsi="Enter item you want to delete from all shops: "
echo DELETE FROM shopitems WHERE itemid = "%delsi%" > delsi.dat
mysql -u root -p "%database%" < delsi.dat
del delsi.dat
echo Done
goto :command

:ne
echo "UPDATE characters SET exp = 0 WHERE exp < 0" > negexp.dat
mysql -u root -p "%database%" < negexp.dat
del negexp.dat
echo Done
goto :command

:mysql
set /p mysql="Enter MySQL Query: "
echo "%mysql%" > mysql.dat
mysql -u root -p "%database%" < mysql.dat
del mysql.dat
echo Done
goto :command

:diff
if EXIST %systemroot%\Installed.dat (
goto :patchdiff
) else (
goto :haven
)

:haven
ECHO Hi, you have not downloaded and pathed patch.exe yet.
set /p wan="Do you want to download it now? (y/n)"
if "%wan%"==y goto :download

:download
start http://forum.ragezone.com/attachment.php?attachmentid=60866&d=1223701396
echo Please download it and set the path.
if exist settings.ini (
del settings.ini
ECHO Download > %systemroot%\Installed.dat
goto :setpath
) else (
ECHO Download > %systemroot%\Installed.dat
goto :setpath
)

:patchdiff
set /p p="Enter patch name including the ".diff": "
patch -i %p% --backup-if-mismatch
echo Done
goto :command

:reset
del database.ini
del settings.ini
goto :existdatabase

:save
echo Syntax save (table name)
goto :command

:saveacc
if exist tables cd tables
replace.vbs accounts.xls "	" "','"
echo delete accounts from accounts;>accounts.sql
for /f "skip=1" %%a in (
accounts.xls
) do (
echo INSERT INTO accounts values ('%%aLLinrocks>>accounts.sql
)
replace.vbs account.sql "LLinrocks" "');"
replace.vbs account.xls "','" "	"
mysql odinms -u root -p<accounts.sql
del accounts.sql
cd ..
goto :command

:savechar
if exist tables cd tables
replace.vbs characters.xls "	" "','"
echo delete characters from characters;>characters.sql
for /f "skip=1" %%a in (
characters.xls
) do (
echo INSERT INTO characters values ('%%aLLinrocks>>characters.sql
)
replace.vbs characters.sql "LLinrocks" "');"
replace.vbs characters.xls "','" "	"
mysql odinms -u root -p<characters.sql
del characters.sql
cd ..
goto :command

:saveshops
if exist tables cd tables
replace.vbs shops.xls "	" "','"
echo delete shops from shops;>shops.sql
for /f "skip=1" %%a in (
shops.xls
) do (
echo INSERT INTO shops values ('%%aLLinrocks>>shops.sql
)
replace.vbs shops.sql "LLinrocks" "');"
replace.vbs shops.xls "','" "	"
mysql odinms -u root -p<shops.sql
del shops.sql
cd ..
goto :command

:saveshopitems
if exist tables cd tables
replace.vbs shopitems.xls "	" "','"
echo delete shopitems from shopitems;>shopitems.sql
for /f "skip=1" %%a in (
shopitems.xls
) do (
echo INSERT INTO shopitems values ('%%aLLinrocks>>shopitems.sql
)
replace.vbs shopitems.sql "LLinrocks" "');"
replace.vbs shopitems.xls "','" "	"
mysql odinms -u root -p<shopitems.sql
del shopitems.sql
cd ..
goto :command

:savestorages
if exist tables cd tables
replace.vbs storages.xls "	" "','"
echo delete storages from storages;>storages.sql
for /f "skip=1" %%a in (
storages.xls
) do (
echo INSERT INTO storages values ('%%aLLinrocks>>storages.sql
)
replace.vbs storages.sql "LLinrocks" "');"
replace.vbs storages.xls "','" "	"
mysql odinms -u root -p<storages.sql
del storages.sql
cd ..
goto :command
