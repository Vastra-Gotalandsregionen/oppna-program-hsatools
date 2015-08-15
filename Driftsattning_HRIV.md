# Driftsättning av HRIV #

Apache Tomcat är den rekommenderade applikationsservern och mer specifikt är det version 5.5.27 som har använts.

För att tolkandet av GET-parametrar ska fungera smärtfritt är det lämpligt att använda UTF-8 som teckenkodning. Ange därför attributet URIEncoding="UTF-8" på lämpligt Connector-element i server.xml.

Följande system properties ska göras tillgängliga för HRIV om anslutning till vårdvalssystemet ska fungera:<br />
javax.net.ssl.keyStoreType: Exempelvis pkcs12.<br />
javax.net.ssl.keyStore: Sökväg till keystoren som håller klientcertifikatet som används för att ansluta till vårdvalssystemet.<br />
javax.net.ssl.keyStorePassword=Lösenord för ovan nämnda keystore.<br />
ssnEncryptionKey: Nyckel som används för kryptering av personnummer vid vårdvalslistning. Ska vara 24 tecken.

Note: Efter uppgradering till JDK 1.6.0\_19 så verkar det som om Sun/Oracle har styrt upp säkerheten lite och då fungerar det inte att peka ut nyckeln till Vårdvalslistningen enbart pga någon re-negotiation. För att det skall fungera måste även
-Dsun.security.ssl.allowUnsafeRenegotiation=true läggas till.