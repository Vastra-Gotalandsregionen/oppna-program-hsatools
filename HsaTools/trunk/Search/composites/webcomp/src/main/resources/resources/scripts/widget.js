function initGMap(hitsSize) {
	if (hitsSize < 1) {
		printMapError();
		return;
	}
	
	if (document.getElementById("map-div") != null) {
		document.getElementById("map-div").style.visibility = "hidden";
	}
	if (document.getElementById("map-div") != null) {
		map = new GMap2(document.getElementById("map-div"));
		return map;
	}
}

/* Show map according to address (which we geocode with help from Google Maps). */
function showAddress(hsaid, address, tel, careTypeName, name, distance, printError, map) {
	if (map == null) {
		map = new GMap2(document.getElementById("map"));
	}
	var geocoder = new GClientGeocoder();
	geocoder.getLatLng(address + ", sweden",    
		function(point) {      
		  if (point) {        
		    map.setCenter(point, 11);        
		    var marker = new GMarker(point);        
		    map.addOverlay(marker);
		    GEvent.addListener(marker,"mouseover", function() { 
	    		var label = '<span style="font-size: 9px"><b><a href="visaenhet?hsaidentity=' + hsaid + '">' +name + '</a></b><br/>' + careTypeName + '<br/>Telefon: ' + tel + '<br/>';
	    		if (distance != '') {
	    			label += 'Avstånd till adress: ' + distance + ' km<br/>'	
	    		}
		    	label += '<a href="visaenhet?hsaidentity=' + hsaid + '">Visa detaljer</a></span>'
		    marker.openInfoWindowHtml(label);
		    });
			map.addControl(new GLargeMapControl());
			map.addControl(new GMapTypeControl());
		  } else {
				if (printError == 'true') {
					printMapError();
				}
		  }
	    }  
    );
}

/* Show map according to WGS84 coordinates in degrees (decimal format). */
function showAddressByCoordinates(hsaid, lat, lon, tel, careTypeName, name, distance, printError, map) {
	if (map == null) {
		map = new GMap2(document.getElementById("map"));
	}
	var point = new GLatLng(lat,lon);
	if (point) {
	    map.setCenter(point, 11);        
	    var marker = new GMarker(point);        
	    map.addOverlay(marker);
	    GEvent.addListener(marker,"mouseover", function() { 
    		var label = '<span style="font-size: 9px"><b><a href="visaenhet?hsaidentity=' + hsaid + '">' +name + '</a></b><br/>' + careTypeName + '<br/>Telefon: ' + tel + '<br/>';
    		if (distance != '') {
    			label += 'Avstånd till adress: ' + distance + ' km<br/>'	
    		}
	    	label += '<a href="visaenhet?hsaidentity=' + hsaid + '">Visa detaljer</a></span>'
	    marker.openInfoWindowHtml(label);
	    });
		map.addControl(new GLargeMapControl());
		map.addControl(new GMapTypeControl());
	} else {
		if (printError == 'true') {
			printMapError();
		}
   }
}
	
function printMapError() {
	var mapDiv = null;
	// Try to display error message
	if (document.getElementById("map") != null) {
		mapDiv = document.getElementById("map");
	} else {
		mapDiv = document.getElementById("map-div");
	}
	if (mapDiv != null) {
		mapDiv.innerHTML = "Hittade inte adress p&aring; karta.";
		mapDiv.style.height = "50px";
		mapDiv.style.width = "200px";
		mapDiv.style.backgroundColor = "#FFFFFF";
	}
}

function getDirections(to, from) {
	// Show directions table
	document.getElementById("directions-container").style.display = "block";
	
	var map = new GMap2(document.getElementById("map-div"));
	var directionsPanel = document.getElementById("dir-div");
	directionsPanel.innerHTML = '';
	document.getElementById("map-div").style.visibility='visible';
	document.getElementById("dir-div").style.visibility='visible';
	
	directions = new GDirections(map, directionsPanel);
	var trip = "from: " + from + " to: " + to;
	directions.load(trip);
}

function drawAutoCompleteDiv() {
	document.write('<div id="autocomplete_choices" class="autocomplete"></div>');
}

function initAutocompleter() {
	/* Not yet... */
	var myDS = new YAHOO.util.XHRDataSource("suggestions"); 
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = { 
       resultNode: 'unit', 
       fields: ['name','id']             
    };
    /**/
	
	/* For output=text: 	myDS.responseSchema = {		recordDelim :"\n", 		fieldDelim :"\t"	}; */
	
	/* Not yet... */
	var myAutoComp = new YAHOO.widget.AutoComplete("unitName","autocomplete_choices", myDS);
	myAutoComp.resultTypeList = false; 
	myAutoComp.formatResult = function(oResultData, sQuery, sResultMatch) { 
		return (sResultMatch + oResultData.name); 
	}

	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2; 
	myAutoComp.maxResultsDisplayed = 50; 

	// define your itemSelect handler function:
	var itemSelectHandler = function(sType, aArgs) { 
		var oMyAcInstance = aArgs[0]; 
		var selectedItem = aArgs[1]; // the <li> element selected in the suggestion container 
		var oData = aArgs[2]; // object literal of data for the result
		var hsaId = oData.id;
		if (hsaId != '') {
			document.location = "visaenhet?hsaidentity=" + hsaId;
		}
	};  
	myAutoComp.itemSelectEvent.subscribe(itemSelectHandler);
	
	/* */
	
	/* Standard formatting
	myAutoComp.formatResult = function(oResultData, sQuery, sResultMatch) {
		var sMarkup = (sResultMatch) ? sResultMatch : "";
		return sMarkup;
	};*/
		
	/*
	new Ajax.Autocompleter("unitName", "autocomplete_choices", "suggestions", {
		minChars: 2, 
		indicator: 'indicator1',
		afterUpdateElement: goDirectlyToUnitFromAutocompleteList
	});
	*/
}

function goDirectlyToUnitFromAutocompleteList(inputField, selectedItem) {
	hsaId = selectedItem.id;
	if (hsaId != '') {
		document.location = "visaenhet?hsaidentity=" + hsaId;
	}
}

function drawToggleDescription() {
	var e = document.getElementsByName("description-toggle-area");
	for(var i=0; i<e.length;i++) {
		if (e[i].innerHTML != null)
			e[i].innerHTML = '<a href="#" class="url" onclick="toggleExtendDescription();"><img name="description-toggle-image" alt="" src="resources/images/bullet_toggle_plus.png"/><span name="toggle-extend-description-link-span">Utöka beskrivning om mottagning</span></a>';
	}
}

function drawDirectionsTableRows(name, wgs84Lat, wgs84Long, street, city) {
	var fromCellHeader = '<span class="grey-header">Till</span>';
	var fromCellBody = name;
	
	var toCellHeader = '<span class="grey-header">Fr&aring;n</span>';
	var toCellBody = '';
	if (wgs84Lat != '0.0') {
		toCellBody += '<input type="hidden" name="route_to" id="route_to" value="' + 
		wgs84Lat + ', ' + wgs84Long + '" />';
	} else {
		toCellBody += '<input type="hidden" name="route_to" id="route_to" value="' + 
		street + ', ' + city + '" />';
	}
	
	toCellBody += "<input type=\"text\" size=\"20\" name=\"route_from\" id=\"route_from\" " +
	"value=\"gata, ort\" " + " onclick=\"if (this.value == 'gata, ort') { this.focus(); this.select();}\" " + 
	"onkeydown=\'if ((event.which &amp;&amp; event.which == 13) || (event.keyCode &amp;&amp; event.keyCode == 13)) " + 
	"{getDirections(document.getElementById(\"route_to\").value, this.value);}\'/><br/>\n		" +
	"<input type=\"submit\" value=\"Hitta resv&auml;g\" onclick=\'getDirections(document.getElementById(\"route_to\").value, " + 
	"document.getElementById(\"route_from\").value);\'/>\n	</td>\n</tr>\n";
	
	directionsFromCellHeaderElem = document.getElementById('directionsFromCellHeader');
	directionsFromCellBodyElem = document.getElementById('directionsFromCellBody');
	directionsToCellHeaderElem = document.getElementById('directionsToCellHeader');
	directionsToCellBodyElem = document.getElementById('directionsToCellBody');
	
	if (directionsFromCellHeaderElem != null) {
		directionsFromCellHeaderElem.innerHTML = fromCellHeader;
	}
	
	if (directionsFromCellBodyElem != null) {
		directionsFromCellBodyElem.innerHTML = fromCellBody;
	}

	if (directionsToCellHeaderElem != null) {
		directionsToCellHeaderElem.innerHTML = toCellHeader;
	}

	if (directionsToCellBodyElem != null) {
		directionsToCellBodyElem.innerHTML = toCellBody;
	}
}

function drawPrintLabel(divToPrint) {
	var output = "<span id=\"labels\"><a href=\"#\" class=\"url\" onclick=\"printElement(\'" + divToPrint + "\');\"> Skriv ut <img src=\"resources/images/printer.png\" alt=\"\" /></a></span>";
	document.write(output);
}

function showUnit(rootUrl, hsaId, showMap) {
	// Create request
	// For external: var url = 'proxy?url=' + rootUrl + '/visaenhet?hsaidentity=' + hsaId;
	var url = rootUrl + '/visaenhet?hsaidentity=' + hsaId;
	new Ajax.Request(url, {
	  method: 'get',
	  onSuccess: function(transport) {
		var print_area_from_response = transport.responseText.split(/Display unit widget start[^>]*>/)[1];
		print_area_from_response = print_area_from_response.split(/Display unit widget end[^>]*>/)[0];
		
		if (showMap == '0') {
			print_area_from_response_temp1 = print_area_from_response.split(/Map cell start[^>]*>/)[0];
			print_area_from_response_temp2 = print_area_from_response.split(/Map cell end[^>]*/)[1];
			print_area_from_response = print_area_from_response_temp1 + print_area_from_response_temp2;
			$('unit-detail').style.width='600px';
		}
		var unit_detail = $('unit-detail');
        unit_detail.update(print_area_from_response);
	  }
	});
}

function searchUnits(rootUrl, webappName, municipalityId, resultOnly, googleMapsKey) {
	//For external: var url = 'proxy?url=' + rootUrl + '/' + webappName + '/startpage.jsp?startpage=1';
	var url = rootUrl + '/' + webappName + '/HRIV.Search.searchunit-flow.flow&startpage=1';
	new Ajax.Request(url, {
	  method: 'get',
	  onSuccess: function(transport) {
		var search_area_from_response = transport.responseText.split(/Search units widget start[^>]*>/i)[1];
		search_area_from_response = search_area_from_response.split(/Search units widget end[^>]*>/i)[0];
		
		//Update care types link
		var toBeReplacedExpression = 'a href="getlink';
		var toBeReplaced = new RegExp(toBeReplacedExpression, "g");
		var toReplaceExpression = 'a href=' + rootUrl + '/' + webappName + '/getlink';
		search_area_from_response = search_area_from_response.replace(toBeReplaced, toReplaceExpression);
		
		//Update search form
		var toBeReplacedExpression = 'action="Search.searchunit-flow.flow[;jsessionid=,0-9,A-Z]*';
		var toBeReplaced = new RegExp(toBeReplacedExpression, "g");
		var toReplaceExpression = 'action="' + rootUrl + '/' + webappName + '/Search.searchunit-flow.flow?googleMapsKey=' + googleMapsKey + '&resultOnly=' + resultOnly;
		search_area_from_response = search_area_from_response.replace(toBeReplaced, toReplaceExpression);

		var search_units = $('search-units');
		
		//Set default municipality
		if (municipalityId != '') {
			search_area_from_response = search_area_from_response.replace(municipalityId + "\"", municipalityId + "\" selected=\"selected\"");
		}
        search_units.update(search_area_from_response);
	  }
	});
}

function closeUnitsFormValidate(address) {
	var geocoder = new GClientGeocoder();
	geocoder.getLatLng(address + ", sweden",    
		function(point) {      
		  if (point) {        
			  // We found a matching location
			  return true;
		  } else {
			  // We did not find location
			  alert("Angiven adress hittades inte, försök igen.")
			  return false;
		  }
		}
    );
}