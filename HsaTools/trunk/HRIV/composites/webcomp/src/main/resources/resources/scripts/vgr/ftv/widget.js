/*
 * Copyright 2009 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
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
function showAddress(hsaid, address, tel, careTypeName, name, distance,
		printError, map) {
	if (map == null) {
		map = new GMap2(document.getElementById("map"));
	}
	var geocoder = new GClientGeocoder();
	geocoder
			.getLatLng(
					address + ", sweden",
					function(point) {
						if (point) {
							map.setCenter(point, 11);
							var marker = new GMarker(point);
							map.addOverlay(marker);
							GEvent
									.addListener(
											marker,
											"mouseover",
											function() {
												var label = '<span style="font-size: 9px"><b><a href="visaenhet?hsaidentity='
														+ hsaid
														+ '">'
														+ name
														+ '</a></b><br/>'
														+ careTypeName
														+ '<br/>Telefon: '
														+ tel + '<br/>';
												if (distance != '') {
													label += 'Avst&aring;nd till adress: ' + distance + ' km<br/>'
												}
												label += '<a href="visaenhet?hsaidentity=' + hsaid + '">Visa detaljer</a></span>'
												marker
														.openInfoWindowHtml(label);
											});
							map.addControl(new GLargeMapControl());
							map.addControl(new GMapTypeControl());
						} else {
							if (printError == 'true') {
								printMapError();
							}
						}
					});
}

/* Show map according to WGS84 coordinates in degrees (decimal format). */
function showAddressByCoordinates(hsaid, lat, lon, tel, careTypeName, name,
		distance, printError, map) {
	if (map == null) {
		map = new GMap2(document.getElementById("map"));
	}
	var point = new GLatLng(lat, lon);
	if (point) {
		map.setCenter(point, 11);
		var marker = new GMarker(point);
		map.addOverlay(marker);
		GEvent
				.addListener(
						marker,
						"mouseover",
						function() {
							var label = '<span style="font-size: 9px"><b><a href="visaenhet?hsaidentity='
									+ hsaid
									+ '">'
									+ name
									+ '</a></b><br/>'
									+ careTypeName
									+ '<br/>Telefon: '
									+ tel
									+ '<br/>';
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
	document.getElementById("map-div").style.visibility = 'visible';
	document.getElementById("dir-div").style.visibility = 'visible';

	directions = new GDirections(map, directionsPanel);
	var trip = "from: " + from + " to: " + to;
	directions.load(trip);
}

// yui autocompletion
function initAutocompleter() {
	var myDS = new YAHOO.util.XHRDataSource("suggestions");
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = {
		resultNode : 'unit',
		fields : [ 'description', 'id', 'uri' ]
	};

	var myAutoComp = new YAHOO.widget.AutoComplete("unitName",
			"autocomplete_choices", myDS);
	myAutoComp.resultTypeList = false;
	myAutoComp.formatResult = function(oResultData, sQuery, sResultMatch) {
		var query = sQuery.toLowerCase();
		var description = sResultMatch;
		var descriptionMatchIndex = description.toLowerCase().indexOf(query);
		var displayDescription;

		if (descriptionMatchIndex > -1) {
			displayDescription = highlightMatch(description, query,
					descriptionMatchIndex);
		}
		return displayDescription;
	}

	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2;
	myAutoComp.maxResultsDisplayed = 50;
	myAutoComp.typeAhead = true;
	myAutoComp.useIFrame = true;

	// Go directly to unit page when selecting
	var itemSelectHandler = function(sType, aArgs) {
		var oMyAcInstance = aArgs[0];
		var selectedItem = aArgs[1]; // the <li> element selected in the
										// suggestion container
		var oData = aArgs[2]; // object literal of data for the result
		if (oData.uri != '') {
			document.location = oData.uri;
		}
	};
	myAutoComp.itemSelectEvent.subscribe(itemSelectHandler);

	var workingHandler = function(oSelf, sQuery, aResults) {
		// document.getElementById('acIndicator').style.padding = '2.5em 0 0 0';
		document.getElementById('acIndicator').style.display = 'inline';
		document.getElementById('healthcare-selection').style.padding = "0";
	};

	var readyHandler = function(oSelf) {
		// document.getElementById('acIndicator').style.padding = '0';
		document.getElementById('acIndicator').style.display = 'none';
		document.getElementById('healthcare-selection').style.padding = "2em 0 0 0";
	};

	var returnHandler = function(sType, aArgs) {
		// Do not spin when we can not find a match.
		var oAutoComp2 = aArgs[0];
		var sQuery = aArgs[1];
		var aResults = aArgs[2];
		if (aResults.length == 0) {
			// document.getElementById('acIndicator').style.padding = '0';
			document.getElementById('acIndicator').style.display = 'none';
			document.getElementById('healthcare-selection').style.padding = "2em 0 0 0";
		}
	}

	myAutoComp.dataRequestEvent.subscribe(workingHandler);
	myAutoComp.containerExpandEvent.subscribe(readyHandler);
	myAutoComp.dataReturnEvent.subscribe(returnHandler);
}

// Helper function for the formatter
function highlightMatch(full, snippet, matchindex) {
	return full.substring(0, matchindex) + "<strong>"
			+ full.substr(matchindex, snippet.length) + "</strong>"
			+ full.substring(matchindex + snippet.length);
}

function goDirectlyToUnitFromAutocompleteList(inputField, selectedItem) {
	hsaId = selectedItem.id;
	if (hsaId != '') {
		document.location = "visaenhet?hsaidentity=" + hsaId;
	}
}

function drawToggleDescription() {
	if (document.getElementById("description-toggle-area") != null) {
		document.getElementById("description-toggle-area").innerHTML = '<a href="#" class="url" onclick="toggleExtendDescription();"><img id="description-toggle-image" alt="" src="resources/images/bullet_toggle_plus.png"/><span id="toggle-extend-description-link-span">Utöka beskrivning om mottagning</span></a>';
	}
}

function drawDirectionsTableRows(name, wgs84Lat, wgs84Long, street, city) {
	var fromCellHeader = '<span class="grey-header">Till:</span>';
	var fromCellBody = name;

	var toCellHeader = '<span class="grey-header">Fr&aring;n:</span>';
	var toCellBody = '';
	if (wgs84Lat != '0.0') {
		toCellBody += '<input type="hidden" name="route_to" id="route_to" value="'
				+ wgs84Lat + ', ' + wgs84Long + '" />';
	} else {
		toCellBody += '<input type="hidden" name="route_to" id="route_to" value="'
				+ street + ', ' + city + '" />';
	}

	toCellBody += "<input type=\"text\" size=\"20\" name=\"route_from\" id=\"route_from\" "
			+ "value=\"gata, ort\" "
			+ " onclick=\"if (this.value == 'gata, ort') { this.focus(); this.select();}\" "
			+ "onkeydown=\'if ((event.which &amp;&amp; event.which == 13) || (event.keyCode &amp;&amp; event.keyCode == 13)) "
			+ "{getDirections(document.getElementById(\"route_to\").value, this.value);}\'/><br/>\n		"
			+ "<input type=\"submit\" value=\"Hitta resv&auml;g\" onclick=\'getDirections(document.getElementById(\"route_to\").value, "
			+ "document.getElementById(\"route_from\").value);\'/>\n	</td>\n</tr>\n";

	directionsFromCellHeaderElem = document
			.getElementById('directionsFromCellHeader');
	directionsFromCellBodyElem = document
			.getElementById('directionsFromCellBody');
	directionsToCellHeaderElem = document
			.getElementById('directionsToCellHeader');
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
	var output = "<span id=\"labels\"><a href=\"#\" class=\"url\" onclick=\"printElement(\'"
			+ divToPrint
			+ "\');\"> Skriv ut <img src=\"resources/images/printer.png\" alt=\"\" /></a></span>";
	document.write(output);
}

function showUnitExternal(proxyUrl, urlParam, rootUrl, hsaId, showMap) {
	// For external use when we need AJAX proxy. Requires proxy at the host of
	// the widget:
	var url = proxyUrl + '?' + urlParam + '=' + rootUrl
			+ '/visaenhet?hsaidentity=' + hsaId; // Example: 'proxy?url=' +
													// rootUrl + ...
	showUnit(url, showMap);
}

function showUnitInternal(rootUrl, hsaId, showMap) {
	// For internal use without proxy:
	var url = rootUrl + '/visaenhet?hsaidentity=' + hsaId;
	showUnit(url, showMap);
}

function showUnit(url, showMap) {

	var callback = {
		success : function(transport) {
			var print_area_from_response = transport.responseText
					.split(/Display unit widget start[^>]*>/)[1];
			print_area_from_response = print_area_from_response
					.split(/Display unit widget end[^>]*>/)[0];

			if (showMap == '0') {
				print_area_from_response_temp1 = print_area_from_response
						.split(/Map cell start[^>]*>/)[0];
				print_area_from_response_temp2 = print_area_from_response
						.split(/Map cell end[^>]*/)[1];
				print_area_from_response = print_area_from_response_temp1
						+ print_area_from_response_temp2;
				document.getElementById('unit-detail').style.width = '600px';
			}
			var unit_detail = document.getElementById('unit-detail');
			unit_detail.innerHTML = print_area_from_response;
		}
	}

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback,
			null);
}

function searchUnitsExternal(proxyUrl, urlParam, rootUrl, webappName,
		municipalityId, resultOnly, googleMapsKey) {
	// For external use when we need AJAX proxy. Requires proxy at the host of
	// the widget:
	var url = proxyUrl + '?' + urlParam + '=' + rootUrl + '/' + webappName
			+ '/HRIV.Search.searchunit-flow.flow&startpage=1'; // Example:
																			// 'proxy?url='
																			// +
																			// rootUrl
																			// +
																			// ...
	searchUnits(url, rootUrl, webappName, municipalityId, resultOnly,
			googleMapsKey);
}

function searchUnitsInternal(rootUrl, webappName, municipalityId, resultOnly,
		googleMapsKey) {
	// For internal use without proxy:
	var url = rootUrl + '/' + webappName
			+ '/HRIV.Search.searchunit-flow.flow&startpage=1';
	searchUnits(url, rootUrl, webappName, municipalityId, resultOnly,
			googleMapsKey);
}

function searchUnits(url, rootUrl, webappName, municipalityId, resultOnly,
		googleMapsKey) {

	var callback = {
		success : function(transport) {
			var search_area_from_response = transport.responseText
					.split(/Search units widget start[^>]*>/i)[1];
			search_area_from_response = search_area_from_response
					.split(/Search units widget end[^>]*>/i)[0];

			// Update care types link
			var toBeReplacedExpression = 'a href="';
			var toBeReplaced = new RegExp(toBeReplacedExpression, "g");
			var toReplaceExpression = 'a href="' + rootUrl + '/' + webappName
					+ '/';
			search_area_from_response = search_area_from_response.replace(
					toBeReplaced, toReplaceExpression);

			// Update search form
			var toBeReplacedExpression = 'action="HRIV.Search.searchunit-flow.flow[;jsessionid=,0-9,A-Z]*';
			var toBeReplaced = new RegExp(toBeReplacedExpression, "g");
			var toReplaceExpression = 'action="' + rootUrl + '/' + webappName
					+ '/HRIV.Search.searchunit-flow.flow?googleMapsKey='
					+ googleMapsKey + '&resultOnly=' + resultOnly;
			search_area_from_response = search_area_from_response.replace(
					toBeReplaced, toReplaceExpression);

			var search_units = document.getElementById('search-units');

			// Set default municipality
			if (municipalityId != '') {
				search_area_from_response = search_area_from_response.replace(
						municipalityId + "\"", municipalityId
								+ "\" selected=\"selected\"");
			}
			
			search_units.innerHTML = search_area_from_response;
		}
	}

	var transaction = YAHOO.util.Connect.asyncRequest('GET', url, callback,
			null);
}

function closeUnitsFormValidate(address) {
	var geocoder = new GClientGeocoder();
	geocoder.getLatLng(address + ", sweden", function(point) {
		if (point) {
			// We found a matching location, submit the form!
			document.forms['closeUnitsForm'].submit();
       } else {
			// We did not find location
			alert("Angiven adress hittades inte, försök igen.")
		}
	});
	
	return false;
}
