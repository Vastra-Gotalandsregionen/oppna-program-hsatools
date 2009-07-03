Sätta upp klient-cert-lösningen för WS som ansluter till vårdvalssystem

1. Få publik (cer-fil) nyckel och privat (p12) från vårdvalssystemteamet.
2. Ta bort eventuell överflödig (och felaktig enligt keytool) text i publika nyckeln fram till -----BEGIN CERTIFICATE-----
3. Bädda in den publika nyckeln i en keystore:
keytool -import -alias vardval -file HRIVclient.cer -storepass vardval -keystore vardval.jks
4. 
