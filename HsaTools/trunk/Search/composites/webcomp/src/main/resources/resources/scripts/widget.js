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
