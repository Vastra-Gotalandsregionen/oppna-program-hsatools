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
function importScript(rootUrl, gmKey) {
	// Prototype
    var tag = document.createElement("script");
    tag.type = "text/javascript";
    tag.src = rootUrl + '/resources/scripts/prototype.js';
    document.getElementsByTagName('head')[0].appendChild(tag);
	
	// Google Maps
    tag = document.createElement("script");
    tag.type = "text/javascript";
    // Google Maps key should match the server displaying the widget.
    tag.src = 'http://maps.google.com/maps?file=api&amp;v=2&amp;key=' + gmKey;
    document.getElementsByTagName('head')[0].appendChild(tag);
}

function importStyles(rootUrl) {
    var tag = document.createElement("link");
    tag.type = "text/css";
    tag.rel = "stylesheet"
    tag.href = rootUrl + '/resources/styles/main.css';
    document.getElementsByTagName('head')[0].appendChild(tag);

    tag = document.createElement("link");
    tag.type = "text/css";
    tag.rel = "stylesheet"
    tag.href = rootUrl + '/resources/styles/style.css';
    document.getElementsByTagName('head')[0].appendChild(tag);
}

