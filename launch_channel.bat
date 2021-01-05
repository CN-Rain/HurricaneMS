@echo off
@title Channel Server Console
set PATH=C:\Program Files\Java\jdk1.7.0_21\bin;
set CLASSPATH=.;dist\odinms.jar;dist\lib\mina-core.jar;dist\lib\slf4j-api.jar;dist\lib\slf4j-jdk14.jar;dist\lib\mysql-connector-java-bin.jar;dist\lib\pircbot.jar
java -Xmx1100m -Dnet.sf.odinms.recvops=recvops.properties -Dnet.sf.odinms.sendops=sendops.properties -Dnet.sf.odinms.wzpath=wz\ -Dnet.sf.odinms.channel.config=channel.properties -Djavax.net.ssl.keyStore=filename.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=filename.keystore -Djavax.net.ssl.trustStorePassword=passwd net.sf.odinms.net.channel.ChannelServer -Dcom.sun.management.jmxremote.port=13373 -Dcom.sun.management.jmxremote.password.file=jmxremote.password -Dcom.sun.management.jmxremote.access.file=jmxremote.access
pause