/*
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

//yui autocompletion
function initAutocompleter(fieldName, codeTable) {
	var myDS = new YAHOO.util.XHRDataSource("suggestions_" + codeTable
			+ ".servlet");
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = {
		fields : [ 'description' ],
		resultNode : 'suggestion'
	};
	var myAutoComp = new YAHOO.widget.AutoComplete(fieldName, "autocomplete_"
			+ fieldName, myDS);
	myAutoComp.resultTypeList = false;
	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2;
	myAutoComp.maxResultsDisplayed = 50;
	myAutoComp.typeAhead = true;
	myAutoComp.useIFrame = true;
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

}

function initAutocompleterUnitName() {
	var myDS = new YAHOO.util.XHRDataSource("suggestions_unitname_vgr.servlet");
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = {
		fields : [ 'description' ],
		resultNode : 'suggestion'
	};
	var myAutoComp = new YAHOO.widget.AutoComplete("unitName", "autocomplete_unitName", myDS);
	myAutoComp.resultTypeList = false;
	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2;
	myAutoComp.maxResultsDisplayed = 50;
	myAutoComp.typeAhead = true;
	myAutoComp.useIFrame = true;
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
}

function initAutocompleterGivenName() {
	var myDS = new YAHOO.util.XHRDataSource("suggestions_givenname_vgr.servlet");
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = {
		fields : [ 'description' ],
		resultNode : 'suggestion'
	};
	var myAutoComp = new YAHOO.widget.AutoComplete("givenName", "autocomplete_givenName", myDS);
	myAutoComp.resultTypeList = false;
	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2;
	myAutoComp.maxResultsDisplayed = 50;
	myAutoComp.typeAhead = true;
	myAutoComp.useIFrame = true;
	myAutoComp.generateRequest = function(sQuery) {
		return "?givenName=" + sQuery + "&surname=" + document.getElementById('surname').value; 
	}; 
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
}

function initAutocompleterSurname() {
	var myDS = new YAHOO.util.XHRDataSource("suggestions_surname_vgr.servlet");
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = {
		fields : [ 'description' ],
		resultNode : 'suggestion'
	};
	var myAutoComp = new YAHOO.widget.AutoComplete("surname", "autocomplete_surname", myDS);
	myAutoComp.resultTypeList = false;
	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2;
	myAutoComp.maxResultsDisplayed = 50;
	myAutoComp.typeAhead = true;
	myAutoComp.useIFrame = true;
	myAutoComp.generateRequest = function(sQuery) {
		return "?givenName=" + document.getElementById('givenName').value + "&surname=" + sQuery; 
	}; 
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
}

// Helper function for the formatter
function highlightMatch(full, snippet, matchindex) {
	return full.substring(0, matchindex) + "<strong>"
			+ full.substr(matchindex, snippet.length) + "</strong>"
			+ full.substring(matchindex + snippet.length);
}

function drawToggleDescription() {
	var e = document.getElementsByName("description-toggle-area");
	for ( var i = 0; i < e.length; i++) {
		if (e[i].innerHTML != null)
			e[i].innerHTML = '<a href="#" class="url" onclick="toggleExtendDescription();"><img name="description-toggle-image" alt="" src="resources/images/bullet_toggle_plus.png"/><span name="toggle-extend-description-link-span">Utöka beskrivning om mottagning</span></a>';
	}
}

function drawPrintLabel(divToPrint) {
	var output = "<span id=\"labels\"><a href=\"#\" class=\"url\" onclick=\"printElement(\'"
			+ divToPrint
			+ "\');\"> Skriv ut <img src=\"resources/images/printer.png\" alt=\"\" /></a></span>";
	document.write(output);
}
