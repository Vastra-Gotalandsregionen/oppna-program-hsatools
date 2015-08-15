# Ramverk i HRIV #

Förutom den ramverkskod som följer med referensarkitekturen används några andra Java- och webbtekniksrelaterade ramverk. Nedan följer en kortfattad genomgång av några av de viktigaste ramverken.

## Dependency Injection - Spring Core ##
Spring Core från [Spring Framework](http://www.springsource.org/about) används för [dependency injection](http://en.wikipedia.org/wiki/Dependency_injection) och konfigureras i [/HsaTools-HRIV-composite-webcomp/src/main/resources/services-config.xml](http://oppna-program.googlecode.com/svn/HsaTools/trunk/HRIV/composites/webcomp/src/main/resources/services-config.xml).

## Navigation - Spring WebFlow ##
För att hantera flöden och navigation används [Spring WebFlow](http://www.springsource.org/webflow). Detta är i enlighet med referensarkitekturen och konfigureras i [/HsaTools-HRIV-module-web/src/main/webapp/WEB-INF/webflow-config.xml](http://oppna-program-hsatools.googlecode.com/svn/HsaTools/trunk/HRIV/modules/web/src/main/webapp/WEB-INF/webflow-config.xml). Det flöde som används i HRIV finns i [/HsaTools-HRIV-composite-webcomp/src/main/resources/flows/HRIV.Search.searchunit-flow.xml](http://oppna-program-hsatools.googlecode.com/svn/HsaTools/trunk/HRIV/composites/webcomp/src/main/resources/flows/HRIV.Search.searchunit-flow.xml).

Användandet av Spring WebFlow gör att navigeringsregler inte hanteras via JavaServer Faces vilket annars är vanligt då JavaServer Faces används.

## Användargränssnitt - JavaServer Faces och JavaServer Facelets ##
HRIV använder de två webbapplikationsramverken [JavaServer Faces](http://java.sun.com/javaee/javaserverfaces/) och [JavaServer Facelets](https://facelets.dev.java.net) för att förenkla utvecklingen av användargränssnitt. De rekommenderas av referensarkitekturen och passar bra tillsammans.

Konfigurationsfilen för JavaServer Faces finns i [/HsaTools-HRIV-module-web/src/main/webapp/WEB-INF/faces-config.xml](http://oppna-program-hsatools.googlecode.com/svn/HsaTools/trunk/HRIV/modules/web/src/main/webapp/WEB-INF/faces-config.xml). Både JavaServer Faces och JavaServer Facelets konfigureras även till viss del via "context-parametrar" i [/HsaTools-HRIV-module-web/src/main/webapp/WEB-INF/web.xml](http://oppna-program-hsatools.googlecode.com/svn/HsaTools/trunk/HRIV/modules/web/src/main/webapp/WEB-INF/web.xml). I web.xml konfigureras även lyssnare, "servlet-mappningar" med mera för JavaServer Faces och JavaServer Facelets.

Det finns en mall, [/HsaTools-HRIV-module-web/src/main/resources/template.xhtml](http://oppna-program-hsatools.googlecode.com/svn/HsaTools/trunk/HRIV/modules/web/src/main/resources/template.xhtml), som beskriver det som är gemensamt för alla vyer. Vissa delar av vyn, som är olika för olika vyer, utgörs av utbytbara fragment. Exempelvis finns följande rad i ovan nämnda template.xhtml:<br>
<code>&lt;ui:insert name="MainContainer"&gt;</code><code>&lt;/ui:insert&gt;</code>

Respektive vy som visar upp något slags "huvudinnehåll" definierar denna MainContainer enligt följande:<br>
<br>
<code>&lt;ui:define name="MainContainer"&gt;</code>
<blockquote><code>&lt;div id="search-result-caption"&gt;</code> ...<br>
<blockquote><code>&lt;h:outputText ...</code>
<code>&lt;/ui:define&gt;</code></blockquote></blockquote>

Som synes i ovanstående kodblock går det bra att blanda vanlig HTML och JSP-komponenter i Facelets.<br>
<br>
<h2>JavaScript och layout – YUI</h2>
[<a href='http://developer.yahoo.com/yui/'>The Yahoo! User Interface Library</a> (YUI) används för layout och även för "type-ahead"-fältet där <a href='http://developer.yahoo.com/yui/autocomplete/'>Autocomplete-komponenten</a> används.<br>
<br>
<h2>Kartor – Google Maps</h2>
För att visa upp kartor och vägbeskrivningar används Google Maps. Detta är en tämligen lättanvänd tjänst och i HRIV fungerar det ungefär så här:<br>
<br>
<ol><li>Importera en extern JavaScript-fil från Google och skicka med den Google Maps-nyckel (är en <a href='http://code.google.com/p/oppna-program/wiki/Sokkomponenten_HsaTools_Search_composite_svc'>inställning</a>: kivtools.hriv.webcomp.googleMapsKey) som ska användas: <br><code>&lt;script type="text/javascript" src="http://maps.google.com/maps?file=api&amp;amp;v=2&amp;amp;key=XXXXXX" /&gt;</code>
</li><li>Tala om för Google Maps vilken div som ska användas för att visa upp kartan: var map = new GMap2(document.getElementById("map"));<br>
</li><li>Kör en JavaScript-funktion som använder de koordinater som finns knutna till mottagningen som ska visas upp. Om koordinaterna saknas kan adressen <a href='http://en.wikipedia.org/wiki/Geocoding'>geokodas</a> (är en <a href='http://code.google.com/p/oppna-program-hsatools/wiki/Sokkomponenten_HsaTools_Search_composite_svc'>inställning</a>: kivtools.hriv.webcomp.fallbackOnAddressForMap). Se JavaScript-funktionen showAddress() i <a href='http://oppna-program-hsatools.googlecode.com/svn/HsaTools/trunk/HRIV/composites/webcomp/src/main/resources/resources/scripts/widget.js'>widget.js</a>.