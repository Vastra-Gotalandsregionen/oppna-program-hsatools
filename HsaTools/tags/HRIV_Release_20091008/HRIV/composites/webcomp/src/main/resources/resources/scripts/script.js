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
function popup(url) {
	newwindow=window.open(url,'name','scrollbars=1,height=800,width=600');
	if (window.focus) {
		newwindow.focus();
	}
	return false;
}

function changeStyleClass(container, classname) {
    container.className = classname;
}

function attachEventYUI() {
    function fnCallbackOver(e, obj) {
        changeStyleClass(obj, 'selected-item');
    }
    function fnCallbackOut(e, obj) {
        changeStyleClass(obj, 'normal');
    }

    // array can contain object references, element ids, or both
    var oMainElement = document.getElementById("search-result-container");
    if (oMainElement != null) {
	    var arrChildren = oMainElement.getElementsByTagName('li');
	    var oCurrent;
	
	    for (var i = 0, l = arrChildren.length; i < l; i++) {
	        oCurrent  = arrChildren[i];
	        YAHOO.util.Event.addListener(oCurrent, "mouseover", fnCallbackOver, oCurrent);
	        YAHOO.util.Event.addListener(oCurrent, "mouseout", fnCallbackOut, oCurrent);
	    }
    }
}

/* Create VGR namespace if it doesn't already exist */
if (typeof VGR === "undefined") { var VGR = {}; }

/* Check for browser support. */
VGR.browserOK = document.getElementById && document.createTextNode;

/**
 * Turn a specific set of elements into a tab widget.
 * @requires YUI
 */
VGR.KIVSearch = function() {
	var YUD = YAHOO.util.Dom;
	var config = {
		sContId:'kiv-results',
		sHiddenClass:'hidden',
		sTabClass:'tab',
		sTabElement:'div',
		sTabHeaderElement:'h3',
		sTabContentClass:'tab-bd',
		sTabContentElement:'div'
	};
	function init() {
		if (!VGR.browserOK) { return; }
		var oCont = YUD.get(config.sContId);
		if (!oCont) { return; }
		// Add tabs
		var tabView = new YAHOO.widget.TabView();
		var modules = YUD.getElementsByClassName(config.sTabClass, config.sTabElement, oCont);
		YUD.batch(modules, function(module) {
			tabView.addTab(new YAHOO.widget.Tab({
				label: module.getElementsByTagName(config.sTabHeaderElement)[0].innerHTML,
				contentEl: YUD.getElementsByClassName(config.sTabContentClass, config.sTabContentElement, module)[0]
			}));
			// Hide modules
			YUD.addClass(module, config.sHiddenClass);
		});
		// Make the first tab active
		tabView.set('activeIndex', 0);
		tabView.appendTo(oCont);
	}
	return {
		init:init
	};
}();

YAHOO.util.Event.onDOMReady(function () {
	VGR.KIVSearch.init();
    attachEventYUI();
});

function adjustSortOrder() {
	if (document.getElementById('sort') != null) {
	  if (document.getElementById('sortOrder') != null && document.getElementById('sortOrder').value == "UNIT_NAME") {
			document.getElementById('sort').selectedIndex='0';
	  } else if (document.getElementById('sortOrder') != null && document.getElementById('sortOrder').value == "CARE_TYPE_NAME") {
			document.getElementById('sort').selectedIndex='1';
	  }
	}
}

function adjustMapVisibility() {
	if (document.getElementById("spinner-div") != null) {
		document.getElementById("spinner-div").style.display = 'none';
	}
	if (document.getElementById("map-div") != null) {
		document.getElementById("map-div").style.visibility = "visible";
	}
}

function toggleExtendDescription() {
	if (document.getElementById("description-body-long").style.display == 'none') {
		document.getElementById("toggle-extend-description-link-span").innerHTML = "Minimera beskrivning av mottagning";
		document.getElementById("description-toggle-image").src = "resources/images/bullet_toggle_minus.png";
		document.getElementById("description-body-long").style.display = "block";
		document.getElementById("description-body-short").style.display = "none";
	} else {
		document.getElementById("description-toggle-image").src = "resources/images/bullet_toggle_plus.png";
		document.getElementById("toggle-extend-description-link-span").innerHTML = "Utöka beskrivning av mottagning";
		document.getElementById("description-body-short").style.display = "block";
		document.getElementById("description-body-long").style.display = "none";
	}
}

function printElement(element) {
	if (element != 'map-div') {
		window.print();
	} else {
		var a = window.open('','','width=800,height=600');
		a.document.open("text/html");
		a.document.write('<script type="text/javascript" src="resources/scripts/widget.js" ></script>');
		a.document.write($(element).innerHTML);
		a.document.close();
		a.focus();
		a.print();
	}
}

function toggleAccessibilityItem(id) {
	var accessibilityItemElem = document.getElementById(id);
	var iconElem = document.getElementById("icon_" + id);
	if (accessibilityItemElem.style.display == 'none' || accessibilityItemElem.style.display == '') {
		iconElem.src = "resources/images/icoMinus.gif";
		accessibilityItemElem.style.display = "block";
	} else {
		iconElem.src = "resources/images/icoPlus.gif";
		accessibilityItemElem.style.display = "none";
	}
}

function setJsEnabledAccInfo() {
	//Old way with prototype: $$("div.accessibilityItemShowable").each(function(elmt) { elmt.style.display = 'none' });
	var accessibilityItemShowableElems = YAHOO.util.Dom.getElementsByClassName('accessibilityItemShowable');
	YAHOO.util.Dom.batch(accessibilityItemShowableElems, function(elmt) { elmt.style.display = 'none' }); 
}

function writeImage(blockId, blockName) {
	document.write('<img src="resources/images/icoPlus.gif" id="icon_' + blockId + '" onclick="toggleAccessibilityItem(\'' + blockId + '\');" align="left"/><li onclick="toggleAccessibilityItem(\'' + blockId + '\');">' + blockName + '</li>');
}

function drawAccInfoToggleButton() {
	var accInfoToggleElem = document.getElementById("acc-info-toggle");
	accInfoToggleElem.innerHTML = '<input type="button" name="toggleAll" value="Växla visa/dölj" onclick="toggleAllAccInfo();" />';
}

function toggleAllAccInfo() {
	//Old way with prototype: $$("div.accessibilityItemShowable").each(function(elmt) { toggleAccessibilityItem(elmt.id) } );
	var accessibilityItemShowableElems = YAHOO.util.Dom.getElementsByClassName('accessibilityItemShowable');
	YAHOO.util.Dom.batch(accessibilityItemShowableElems, function(elmt) { toggleAccessibilityItem(elmt.id) } ); 
}