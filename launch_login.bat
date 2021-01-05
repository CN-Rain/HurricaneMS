@echo off
@title Login Server Console
set PATH=C:\Program Files\Java\jdk1.7.0_21\bin;
set CLASSPATH=.;dist\odinms.jar;dist\lib\mina-core.jar;dist\lib\slf4j-api.jar;dist\lib\slf4j-jdk14.jar;dist\lib\mysql-connector-java-bin.jar;dist\lib\pircbot.jar
java -Xmx150M -Dnet.sf.odinms.recvops=recvops.properties -Dnet.sf.odinms.sendops=sendops.properties -Dnet.sf.odinms.wzpath=wz\ -Dnet.sf.odinms.login.config=login.properties -Djavax.net.ssl.keyStore=filename.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=filename.keystore -Djavax.net.ssl.trustStorePassword=passwd net.sf.odinms.net.login.LoginServer
pause