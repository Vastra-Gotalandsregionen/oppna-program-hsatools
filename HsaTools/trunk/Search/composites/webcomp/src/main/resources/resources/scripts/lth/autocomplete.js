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
//yui autocompletion
function initAutocompleterUnitName() {
	var myDS = new YAHOO.util.XHRDataSource("suggestions_unitname.servlet");
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
	var myDS = new YAHOO.util.XHRDataSource("suggestions_givenname.servlet");
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
	var myDS = new YAHOO.util.XHRDataSource("suggestions_surname.servlet");
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

function initAutocompleterTitle() {
	var myDS = new YAHOO.util.XHRDataSource("suggestions_title.servlet");
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = {
		fields : [ 'description' ],
		resultNode : 'suggestion'
	};
	var myAutoComp = new YAHOO.widget.AutoComplete("vgrId", "autocomplete_title", myDS);
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
