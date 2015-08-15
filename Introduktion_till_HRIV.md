

# Introduktion #
Hitta-rätt-i-vården är en Java EE-webbapplikation vars syfte är att göra det möjligt att presentera vårdrelaterad information från en [HSA](http://www.carelink.se/tjanster/hsa/)-kompatibel datakälla (läs: LDAPv3-katalog). HRIV delar viss funktionalitet med applikationen Sök-i-Katalog som har samma gränssnitt mot datakällan.

Dokumentationen kring HRIV är tänkt att beskriva dess uppbyggnad och bör göra det enklare att underhålla och vidareutveckla applikationen.

# Starthjälp för HRIV-utvecklare #
För att enklare komma igång med HRIV projektet bör man läsa wiki sidan [HRIV\_committers](HRIV_committers.md)

# Ramverk och Arkitekturbeskrivning #
För att bättre förstå ramverk och arkitektur som HRIV använder sig av kan man läsa [Ramverk\_i\_HRIV](Ramverk_i_HRIV.md) och  [Arkitekturen\_i\_HRIV](Arkitekturen_i_HRIV.md)

# Webbservice i HRIV #
I HRIV finns två webservice tjänster som möjliggör hämtning av Personinformation och Enhetsinformation. Båda webbservicarna ligger under paketet HsaTools-HRIV-module-intsvc.
Personinformation webbservicen är av typen REST och möjlig gör sökning av personinformation i KIV se [PersonalRecordService](PersonalRecordService.md) för mer information.

Enhetsinformation webservicen är av typen SOAP och möjliggör hämtning av enhetsinformation med hjälp av hsa identity. se [Unit webservice](http://oppna-program-hsatools.googlecode.com/files/UnitDetails%20webservice.pdf) för mer information.

Båda tjänsterna är implementerade med hjälp av Spring webservice [Spring ws](http://static.springsource.org/spring-ws/sites/1.5/)

# Externa tjänster och funktioner i HRIV #

## Katalog i väst ##
Katalog i väst, förkortad till KIV, är en LDAP katalog som innehåller bland annat alla vårdenheter som finns i västra götalands regionen. HRIV använder sig av KIV för att hämta ut information  om en vårdenhet som presenteras på sidan.

## Vårdval tjänsten ##
Med hjälp av integrationen med externa tjänsten vårdval kan medborgare i västra götaland själv lista sig till en vald vårdcentral på nätet. Tjänsten kan bara användas om man har e-legitimation, eftersom det krävs att man kan logga in på en säker sida med hjälp av sitt e-legitimation. Där kan användaren sedan lista sig till den vårdcentral som man har sökt upp på HRIV sidan. För mer information se [Arkitekturen\_i\_HRIV](Arkitekturen_i_HRIV.md)

## Mina vårdkontakter ##
Med tjänsten Mina vårdkontakter har en användare möjligheten att på nätet att utföra vissa tjänster som till exempel beställa tid, av- och omboka tid, förnya recept eller be mottagningen kontakta dig. Även denna tjänst kräver att man har ett e-legitimation. För mer information se [Arkitekturen\_i\_HRIV](Arkitekturen_i_HRIV.md)

## Tillgänglighetsdatabasen ##
Med hjälp av integration av den externa tjänsten Tillgänglighetsdatabasen i HRIV kan funktionshindrade person få information som kan hjälpa en vid ett besök till vald vårdcentral. För mer information se [Arkitekturen\_i\_HRIV](Arkitekturen_i_HRIV.md)

## Google Maps ##
För att kunna visa vald vårdenhet på karta för användaren används integration av tjänsten Google map. För vidare läsning se [Google Maps](http://code.google.com/intl/sv-SE/apis/maps/index.html)