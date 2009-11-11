//yui autocompletion
function initAutocompleterUnitName() {
	var myDS = new YAHOO.util.XHRDataSource("suggestions_unitname.servlet");
	myDS.responseType = YAHOO.util.XHRDataSource.TYPE_XML;
	myDS.scriptQueryAppend = "output=xml";
	myDS.responseSchema = {
		fields : [ 'description' ],
		resultNode : 'suggestion'
	};
	var myAutoComp = new YAHOO.widget.AutoComplete("searchUnitForm:unitName", "autocomplete_unitName", myDS);
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
	var myAutoComp = new YAHOO.widget.AutoComplete("searchPersonForm:givenName", "autocomplete_givenName", myDS);
	myAutoComp.resultTypeList = false;
	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2;
	myAutoComp.maxResultsDisplayed = 50;
	myAutoComp.typeAhead = true;
	myAutoComp.useIFrame = true;
	myAutoComp.generateRequest = function(sQuery) {
		return "?givenName=" + sQuery + "&surname=" + document.getElementById('searchPersonForm:surname').value; 
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
	var myAutoComp = new YAHOO.widget.AutoComplete("searchPersonForm:surname", "autocomplete_surname", myDS);
	myAutoComp.resultTypeList = false;
	myAutoComp.autoHighlight = false;
	myAutoComp.minQueryLength = 2;
	myAutoComp.maxResultsDisplayed = 50;
	myAutoComp.typeAhead = true;
	myAutoComp.useIFrame = true;
	myAutoComp.generateRequest = function(sQuery) {
		return "?givenName=" + document.getElementById('searchPersonForm:givenName').value + "&surname=" + sQuery; 
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
