

# Starthjälp för HRIV-utvecklare #

HRIV är uppbyggd efter den referensarkitekturen som är framtagen för västra götalands regionen, se [Referensarkitekturen](http://code.google.com/p/oppna-program/wiki/Anvisningar_Utvecklingsmiljo) för vidare läsning. HRIV använder sig av Spring framework för implementation av olika funktioner i HRIV.

## Spring Web Flow och Spring MVC ##
Eftersom HRIV följer RA så används Spring Web Flow tillsammans med facelets. Vid dem fall som vanliga Servlet hantering används, implementeras dessa med hjälp av Spring MVC och annotaions.
För mera information se [Referensarkitekturen](http://code.google.com/p/oppna-program/wiki/Anvisningar_Utvecklingsmiljo) och [Spring Framework](http://www.springsource.org/documentation)

## Spring LDAP ##
För integration med LDAP katalogen KIV används Spring LDAP som är ett Java bibliotek som förenklar LDAP operationer, baserat på Springs JdbcTemplate. För vidare läsning se [Spring LDAP](http://www.springsource.org/ldap)

## Spring Web Services ##
De webbservice implementationer som finns i HRIV är implementerade med hjälp av Spring Web Service ([Spring ws](http://static.springsource.org/spring-ws/sites/1.5/)) och Spring MVC ([Spring Framework](http://www.springsource.org/about)). För SOAP service används Spring Web Service som följer ”contract-first service” implementation. När det gäller REST service implementation används vanliga Spring MVC annotation implementation. För vidare läsning om hur de olika Spring lösningarna fungerar kan man läsa referensdokumentationen [Spring framework](http://www.springsource.org/documentation) och  [Spring ws](http://static.springsource.org/spring-ws/sites/1.5/reference.html)

# Lathund #

[Referensarkitekturen](http://code.google.com/p/oppna-program/wiki/Anvisningar_Utvecklingsmiljo) tillhandahåller gedigna anvisningar för hur utvecklingsmiljön sätts upp.

Kortvarianten är som följer:

  1. Skicka e-post till någon av projektägarna om att du behöver skrivrättigheter. Om dina skäl är legitima är chanserna goda att du lyckas med det.
  1. Du behöver lite mjukvara. Se till att du har en [Subversion-klient](http://subversion.tigris.org/links.html#clients), Java 5 eller senare, [Maven](http://maven.apache.org) samt en hygglig utvecklingsmiljö, exempelvis [Eclipse](http://www.eclipse.org). Din utvecklingsmiljö får gärna ha integrerat stöd för Subversion men det är även käckt att komplettera med en fristående subversion-klient.
  1. Checka ut källkoden:<br>cd sökväg_till_källkoden && svn checkout <a href='https://oppna-program-hsatools.googlecode.com/svn/'>https://oppna-program-hsatools.googlecode.com/svn/</a> oppna-program-hsatools --username ditt.användarnamn<br>
<ol><li>Importera projekten i Eclipse med hjälp av m2eclipse-pluginet. I Eclipse: File/Import.../Maven projects, Välj "sökväg_till_källkoden/oppna-program-hsatools/HsaTools/trunk" och importera alla projekten. Voila. Utveckla, checka in dina ändringar och se till att hålla den här wikin uppdaterad.<br>
</li><li>När Eclipse frågar efter anslutningsuppgifter till subversion ska användarnamn och lösenord som framgår via <a href='http://code.google.com/hosting/settings'>http://code.google.com/hosting/settings</a> anges.<br>
</li><li>Importera inställningar:<br>
<ul><li>Spara ner inställningsfilerna från projektet på Google Code:<br>
<ul><li><a href='http://oppna-program.googlecode.com/files/VGR_CodeFormat.xml'>http://oppna-program.googlecode.com/files/VGR_CodeFormat.xml</a>
</li><li><a href='http://oppna-program.googlecode.com/files/VGR_CodeTemplates.xml'>http://oppna-program.googlecode.com/files/VGR_CodeTemplates.xml</a>
</li><li><a href='http://oppna-program.googlecode.com/files/VGR_CleanUp.xml'>http://oppna-program.googlecode.com/files/VGR_CleanUp.xml</a>
</li></ul></li><li>I Eclipse välj Window/Preferences<br>
</li><li>Välj Java/Code Style/Formatter och tryck på Import och importera VGR_CodeFormat.xml<br>
</li><li>Välj Java/Code Style/Code Templates och tryck på Import och importera VGR_CodeTemplates.xml<br>
</li><li>Välj Java/Code Style/Clean Up och tryck på Import och importera VGR_CleanUp.xml<br>
</li></ul></li><li>Om man valt att installera Checkstyle-pluginen i Eclipse (<a href='http://eclipse-cs.sf.net/update'>http://eclipse-cs.sf.net/update</a>) så måste man sätta upp sina projekt så att de kontrolleras av pluginen.<br>
<ul><li>Gå in i Properties för projektet<br>
</li><li>Välj Checkstyle<br>
</li><li>Gå till fliken Local Check Configurations<br>
</li><li>Klicka på New, välj Project Relative Configuration under Type, skriv local i Name och browse:a fram till src/test/resources/checkstyle/checkstyle.xml för det projekt du konfigurerar<br>
</li><li>Tryck Ok och gå tillbaka till Main-fliken och välj local i drop-down:en, kryssa i Checkstyle active for this project och tryck Ok.<br>
</li><li>Eclipse kommer nu att vilja bygga om projektet.<br>
</li><li>För att se eventuella checkstyle varningar, gå till Window - Show view - Other - Checkstyle - Checkstyle violations<br>
</li><li>Välj local</li></ul></li></ol>

Vid incheckningar, skriv lämpliga incheckningskommentarer och hänvisa om möjligt till det ärende som det gäller genom att skriva "issue X". På detta sätt får Google Code en möjlighet att länka ihop en revision av källkoden med ett ärende. Mer info i GoogleCode-hjälpen: <a href='http://code.google.com/p/support/wiki/IssueTracker#Integration_with_version_control'>http://code.google.com/p/support/wiki/IssueTracker#Integration_with_version_control</a>