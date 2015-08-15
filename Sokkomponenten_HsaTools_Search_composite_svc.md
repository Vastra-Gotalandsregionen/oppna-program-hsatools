# Sökkomponenten HsaTools-Search-composite-svc #

Den komponent som används för att ställa frågor/hämta data från datakällan huserar i ett eget projekt, HsaTools-Search-composite-svc. I och med att datakällan är en LDAPv3-katalog finns stöd för detta protokoll.

I ovan nämnda projekt beskrivs de krav som ställs på sök-komponenten (via interfacet SearchService) samt två implementationer, en för LDAPv3 (SearchServiceLdapImpl) och en mock-implementation (SearchServiceMockImpl) för testning utan anslutning till en "riktig" datakälla.

# Att skapa en ny sökkomponentimplementation #

För att skapa en ny sökkomponentimplementation, till exempel för att ansluta till en datakälla som inte kan kommunicera via LDAPv3 eller på något sätt väsentligen skiljer sig från den befintliga, behöver följande göras:

  1. Skapa paketet: se.vgregion.kivtools.search.svc.impl.nykomponent
  1. Skapa en implementation av interfacet se.vgregion.kivtools.search.svc.SearchService i paketet du skapade.
  1. Konfigurera så att den nya sökkomponenten används genom att ändra i Spring-konfigurationen så att den nya sökkomponenten injectas överallt där sökkompontenten används. Konfigurationsfilen är /HsaTools-Search-composite-svc/src/main/resources/services-config.xml och sökkomponenten har id:t "Search.SearchService". Om den nya sökkomponenten avnänder LDAP är det lämpligt att även uppdatera bönan propertyPlaceholder så att denna använder en properties-fil med korrekta anslutningsuppgifter.
  1. Skapa property-filer för den nya implementationen. Property-filerna skall ligga i samma paket som implementationen men under /HsaTools-Search-composite-svc/src/main/resources/ till skillnad från javakoden som ligger under /HsaTools-Search-composite-svc/src/main/java/. De property-filer som skall skapas är:
    * search-composite-svc-healthcare-type-conditions.properties: Innehåller alla verksamhetskoder och namn för den aktuella katalogen. Det går att ange flera villkor för varje vårdtyp, exempel:
> > hsatools.search.svc.impl.condition.name\_5-1=hsaBusinessClassificationCode<br>
<blockquote>hsatools.search.svc.impl.condition.value_5-1=1000,1100,1600<br>
hsatools.search.svc.impl.condition.name_5-2=vgrCareType<br>
hsatools.search.svc.impl.condition.value_5-2=01<br>
hsatools.search.svc.impl.condition.filtered_5=true<p>
Ovanstående betyder att hsaBusinessClassificationCode ska vara antingen "1000", "1100" eller "1600" och vgrCareType ska vara "01". En enhet som uppfyller båda dessa villkor antas tillhöra den femte vårdtypen som benämns enligt hsatools.search.svc.impl.condition.displayname.5 som konfigureras i samma fil.<br>
<p>  Att "filtered" är satt till true betyder att denna vårdtyp ska begränsas av inställningen "hsatools.hriv.webcomp.showUnitsWithTheseHsaBussinessClassificationCodes" som anger att enbart mottagningar med vissa värden för "hsaBussinessClassificationCode" ska vara synliga i HRIV. Om en vårdtyp definieras som innehåller mottagningar som inte har "korrekta" värden på "hsaBussinessClassificationCode", exempelvis en vårdtyp såsom "Sjukhus" som skulle kunna vara lite speciell, är det lämpligt att sätta "filtered" till false.<p>
Observera att det inte är möjligt att använda samma fält två gånger i samma villkor, dvs både 5-1 och 5-2 kan ej ha villkor på exempelvis hsaBusinessClassificationCode.<br>
</blockquote><ul><li>search-composite-svc-connection.properties: Innehåller inloggningsuppgifter för den aktuella katalogen.<br>
</li><li>search-composite-svc-municipalities.properties: Innehåller alla sökbara kommuner för den aktuella katalogen. De existerande exempelfilerna bör vara självförklarande.<br>
</li></ul><ol><li>Om man skall skapa en implementation som läser från en LDAPv3-katalog är det lämpligt att utgå från implementationen se.vgregion.kivtools.search.svc.impl.hak.ldap då denna bäst följer HSA-standarden. Därefter bör mappningen mellan HSA-attribut och java-attributen i klassen se.vgregion.kivtools.search.svc.domain.Unit ändras. Mappningen sker i den implementationsspecifika klassen UnitFactory. I tabellen nedan listas samtliga hsa-attribut som mappas mot java-attrbut för HRIV.<br>
</li><li>Det finns ett antal inställningar som påverkar utseendet i HRIV. Dessa inställningar är till sin natur starkt förknippade med den implementation och katalog som används varför varje implementation har sin egen uppsättning: /HsaTools-HRIV-composite-webcomp/src/main/resources/se/vgregion/kivtools/hriv/presentation/[implementationsnamn, exempelvis kiv eller hak]/ hriv-composite-webcomp.properties.<br>
Dessa inställningar finns i ovan nämnda properties-fil:</li></ol>

<table><thead><th> <b>Inställningens namn</b> </th><th> <b>Möjliga värden</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> hsatools.hriv.webcomp.testingMode </td><td> true/false            </td><td> I testläge visas varningar när information saknas etc. </td></tr>
<tr><td> hsatools.hriv.webcomp.useAccessibilityDatabaseIntegration </td><td> true/false            </td><td> Anger om tillgänglighetsdatabasintegration ska vara aktiverad. </td></tr>
<tr><td> hsatools.hriv.webcomp.accessibilityDatabaseIntegrationGetIdUrl </td><td> Sträng (http...)      </td><td> Sökväg till webservice som levererar tillgänglighetsdatabasid utifrån ett hsaid. </td></tr>
<tr><td> hsatools.hriv.webcomp.accessibilityDatabaseIntegrationGetInfoUrl </td><td> Sträng (http...)      </td><td> Sökväg till webservice som levererar tillgänglighetsdata utifrån ett tillgänglighetsdatabasid. </td></tr>
<tr><td> hsatools.hriv.webcomp.fallbackOnAddressForMap </td><td> true/false            </td><td> Anger om adress ska användas för kartuppslagningar ifall koordinater saknas för enheten. </td></tr>
<tr><td> hsatools.hriv.webcomp.googleMapsKey </td><td> Sträng                </td><td> Google Maps-nyckeln som är giltig för den webbplats där HRIV körs. </td></tr>
<tr><td> hsatools.hriv.webcomp.metersToCloseUnits </td><td> Heltal                </td><td> Antal meter från angiven adress som mottagningen ska anses vara ”närbelägen”. </td></tr>
<tr><td> hsatools.hriv.webcomp.showUnitsWithTheseHsaBussinessClassificationCodes </td><td> Kommaseparerad lista med giltiga värden för HsaBussinessClassificationCodes. </td><td> Enbart mottagningar som har en av de angivna värdena som HsaBussinessClassificationCode visas. Om denna inställning lämnas tom görs ingen filtrering. </td></tr>
<tr><td> hsatools.hriv.webcomp.findRouteLinks </td><td> url1::länknamn1::tillParameterNamn;url2::länknamn2::tillParameterNamn </td><td> Lista med reselänkar som ska visas upp på detaljvyn under "Hitta resväg". Respektive url får följande tillägg så att mottagningens gatuadress skickas med som en get-parameter: ?tillParameterNamn=#{fn:trim(unit.hsaStreetAddress.street)} Om tillParameterNamn utelämnas kommmer denna även att utelämnas i de länkar som genereras. </td></tr>
<tr><td> hsatools.hriv.webcomp.styles </td><td> Import-satser för stilmallar. Exempel: @import 'resources/styles/grids-min.css'; @import 'resources/styles/main.css' </td><td> De stilmallsimporter som ska ske. </td></tr>
<tr><td> hsatools.hriv.webcomp.scripts </td><td> Sökvägar till script-filer </td><td> Kommaseparerad lista med sökvägar till script-filer som används förutom standard-filerna (script.js, widget.js etc). Är tänkt att göras om så att en fil pekas ut som hanterar scriptlänkningen. </td></tr>
<tr><td> hsatools.hriv.webcomp.header </td><td> Sökväg till sidhuvud som inkluderas. </td><td> Pekar ut en fil som inkluderas som sidhuvud. </td></tr>
<tr><td> hsatools.hriv.webcomp.footer </td><td> Sökväg till sidfot som inkluderas. </td><td> Pekar ut en fil som inkluderas som sidfot. </td></tr>
<tr><td> hsatools.hriv.webcomp.informationArea </td><td> Sökväg till sidinnehåll som inkluderas. </td><td> Pekar ut en fil som inkluderas i informationsrutan i vänsterkolumnen. </td></tr>
<tr><td> hsatools.hriv.webcomp.startPage </td><td> Sökväg till sidinnehåll som inkluderas. </td><td> Pekar ut en fil som inkluderas på startsidan. </td></tr>
<tr><td> hsatools.hriv.webcomp.careTypePage </td><td> Sökväg till sidinnehåll som inkluderas. </td><td> Pekar ut en fil som inkluderas på vårdtypssidan. </td></tr>
<tr><td> hsatools.hriv.webcomp.useShowUnitCode </td><td> true/false            </td><td> Anger om kodrutan som visar enhetsdetaljerwidgeten ska användas. </td></tr>
<tr><td> hsatools.hriv.webcomp.showUnitCodeOnServer </td><td> Serverns adress där kodrutan ska visas. </td><td> Gör det möjligt att enbart visa kodrutan på en specifik server. Kodrutan kommer inte att visas om HRIV körs på en annan server. </td></tr>
<tr><td> hsatools.hriv.webcomp.showUnitCode1 </td><td> HTML-kod som utgör första delen av kodrutan. </td><td> Koden för kodrutan har delats i två block för att kunna inkludera dynamisk information (mottagningens hsaId). </td></tr>
<tr><td> hsatools.hriv.webcomp.showUnitCode2 </td><td> HTML-kod som utgör andra delen av kodrutan. </td><td> Andra delen av kodrutans kod som fortsätter direkt efter att mottagningens hsaid. </td></tr>
<tr><td> hsatools.hriv.webcomp.useTrackingCode </td><td> true/false            </td><td> Anger om nedanstående "spårningskod" ska användas. </td></tr>
<tr><td> hsatools.hriv.webcomp.trackingCodeOnServer </td><td> Serverns adress där kodrutan ska visas. </td><td> Gör det möjligt att enbart inkludera ”spårningskoden” på en specifik server. Exempelvis troligt att spårningskoden inte ska köras på en testserver. </td></tr>
<tr><td> hsatools.hriv.webcomp.trackingCode </td><td> HTML-kod som utgör spårningskoden. </td><td> Exempelvis ett anrop till Google Analytics. </td></tr>
<tr><td> hsatools.hriv.webcomp.useListenLink </td><td> true/false            </td><td> Anger om lyssningslänk för uppläsning av sidan ska presenteras. </td></tr>
<tr><td> hsatools.hriv.webcomp.listenLinkCode1 </td><td> HTML-kod som utgör första delen av lyssningskoden. </td><td> Koden för lyssningskoden har delats i två block för att kunna inkludera dynamisk information. </td></tr>
<tr><td> hsatools.hriv.webcomp.listenLinkCode2 </td><td> HTML-kod som utgör andra delen av lyssningskoden. </td><td> Andra delen av kodrutans kod som fortsätter direkt efter att mottagningens hsaid. </td></tr>
<tr><td> hsatools.hriv.webcomp.usePrinting </td><td> true/false            </td><td> Anger om utskriftskod ska presenteras. </td></tr>
<tr><td> hsatools.hriv.webcomp.useMvk </td><td> true/false            </td><td> Aktiverar/inaktiverar integration med Mina Vårdkontakter. </td></tr>
<tr><td> hsatools.hriv.webcomp.mvkGuid </td><td> Sträng                </td><td> Identifieringskod som tillhandahålls av Mina Vårdkontakter. </td></tr>
<tr><td> hsatools.hriv.webcomp.mvkUrlBeforeHsaidAndGuidParams </td><td> Sträng/sökväg: http... </td><td> Sökvägen till den webbtjänst på MVK som returnerar de tjänster som finns tillgängliga för aktuell mottagning. GET-parametrar (&hsaid=...&guid=...) för guid och HSA-id läggs på. </td></tr>
<tr><td> hsatools.hriv.webcomp.mvkLoginUrl </td><td> Sträng/sökväg: http... </td><td> Sökvägen till den mvk-sida som det länkas till ifall enheten har tjänster som erbjuds via MVK. </td></tr>
<tr><td> hsatools.hriv.webcomp.metaAuthor </td><td> Sträng                </td><td> Utgivare/författare som anges i meta-informationen. </td></tr>
<tr><td> hsatools.hriv.webcomp.metaCopyright </td><td> Sträng                </td><td> Copyrighttext som anges i meta-informationen, exempelvis "Copyright (c) Västra Götalandsregionen". </td></tr>
<tr><td> hsatools.hriv.webcomp.startpage.title </td><td> Sträng                </td><td> Titeln på startsidan. </td></tr>
<tr><td> hsatools.hriv.webcomp.searchresult.title </td><td> Sträng                </td><td> Titeln på träffresultatsidan. </td></tr>
<tr><td> hsatools.hriv.webcomp.mainTop </td><td> Sträng                </td><td> Sträng som visas överst i ”huvudinnehållet” (div-id: yui-main) på alla sidor. </td></tr>
<tr><td> hsatools.hriv.webcomp.showLinkToIM </td><td> true/false            </td><td> Om länk för editering i IM ska visas. </td></tr>
<tr><td> hsatools.hriv.webcomp.linkToIMOnServer </td><td> Sträng                </td><td> Gör det möjligt att enbart visa länken till IM på en specifik server. Kodrutan kommer inte att visas om HRIV körs på en annan server. </td></tr>
<tr><td> hsatools.hriv.webcomp.linkToIMBase </td><td> Sträng                </td><td> Login-sidan till IM som vi länkar till och skickar med sökvägen till mottagningen Base64-kodad. Exempelvis: <a href='https://kivweb.vgregion.se/nordicedge/vgr/login.jsp?loginDN='>https://kivweb.vgregion.se/nordicedge/vgr/login.jsp?loginDN=</a> </td></tr>
<tr><td> hsatools.hriv.webcomp.verifyV1 </td><td> Sträng                </td><td> Innehåll till meta-taggen "verify-v1" som används av Google Web Toolkit (GWT) för att verifiera att man "äger" en viss webbplats. </td></tr>
<tr><td> hsatools.hriv.webcomp.geoRegion </td><td> Sträng/kod            </td><td> Region-kod som används för geometadatataggen "geo.region". Se <a href='http://en.wikipedia.org/wiki/ISO_3166-2:SE'>http://en.wikipedia.org/wiki/ISO_3166-2:SE</a> </td></tr>
<tr><td> hsatools.hriv.webcomp.mobileUrl </td><td> Sträng                </td><td> Hela servernamnet inkl domänadressen, exempelvis m.hittavard.vgregion.se, som används då gränssnittet anpassats för mobila enheter (handheld-CSS). </td></tr>
<tr><td> hsatools.hriv.webcomp.externalApplicationURL </td><td> Sträng/sökväg: http... </td><td> Applikationens externa URL. </td></tr>