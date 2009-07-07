/**
 * Copyright 2009 Västa Götalandsregionen
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
package se.vgregion.kivtools.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.presentation.SearchUnitFlowSupportBean;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.UnitNameComparator;
import se.vgregion.kivtools.search.util.EnvAssistant;
import se.vgregion.kivtools.search.util.Formatter;

/**
 * Generates suggestions based on text entered in unitName text field.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class Suggestions extends HttpServlet implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		UnitSearchSimpleForm theForm = new UnitSearchSimpleForm();
		String userInputUnitName;
		if (EnvAssistant.isRunningOnIBM()) {
			// Running on WebSphere, UTF-8
			userInputUnitName = URLDecoder.decode(new String(request.getParameter("query").getBytes("UTF-8")), "UTF-8");
		} else {
			// We don't run on IBM (maybe Tomcat), 8859-1
			userInputUnitName = URLDecoder.decode(new String(request.getParameter("query").getBytes("ISO-8859-1")), "ISO-8859-1");
		}

		// param name is "query" (not unitName) as default when using YUI AC,
		// when using scriptaculous: String userInputUnitName =
		// request.getParameter("unitName");

		theForm.setUnitName(userInputUnitName);
		SikSearchResultList<Unit> resultList = null;

		// Spring bean name is hard coded!
		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		SearchUnitFlowSupportBean sb = (SearchUnitFlowSupportBean) springContext.getBean("Search.SearchUnitFlowSupportBean");
		sb.setSearchService((SearchService) springContext.getBean("Search.SearchService"));

		ArrayList<Unit> units = sb.getUnits();
		ArrayList<Unit> matchingUnits = new ArrayList<Unit>();

		// If we have not yet cached the units, search in catalog
		if (units == null || !sb.isUnitsCacheComplete()) {
			try {
				resultList = sb.doSearch(theForm);
			} catch (KivNoDataFoundException e) {
				// Not too much to do...
				e.printStackTrace();
			} catch (KivException e) {
				// Not too much to do...
				e.printStackTrace();
			}
			// Transfer matching units from resultList to matchingUnits
			if (resultList != null) {
				matchingUnits.addAll(resultList);
			}
		} else {
			// We have the units cached

			for (Unit u : units) {
				// System.out.println(u.getHsaIdentity() + ": " + u.getName());
				if (u.getName().toLowerCase().indexOf(userInputUnitName.toLowerCase()) >= 0) {
					matchingUnits.add(u);
				}
			}
		}

		Collections.sort(matchingUnits, new UnitNameComparator());

		/* Create output */
		String outputFormat = request.getParameter("output");
		String output = "";

		if ("xml".equals(outputFormat)) {
			// XML
			output += "<?xml version='1.0' standalone='yes'?>\n<units>\n";
			if (matchingUnits != null) {
				for (Unit u : matchingUnits) {
					String description = getUnitDescriptionEncoded(u);
					output += "<unit description=\"" + description + "\" id=\"" + u.getHsaIdentity() + "\" />\n";
				}
			}
			output += "</units>";
			response.setContentType("text/xml");
		} else if ("text".equals(outputFormat)) {
			// Flat text response, tab as field delimiter and newline as record
			// delimiter.
			if (matchingUnits != null) {
				for (Unit u : matchingUnits) {
					String description = getUnitDescriptionEncoded(u);
					output += description + "\t" + u.getHsaIdentity() + "\n";
				}
			}
			response.setContentType("text/plain");
		} else {
			// Default to HTML (unordered list, UL)
			output += "<ul>";
			if (matchingUnits != null) {
				for (Unit u : matchingUnits) {
					String description = getUnitDescriptionEncoded(u);
					output += "<li id=\"" + u.getHsaIdentity() + "\">" + description + "</li>";
				}
			}
			output += "</ul>";
			response.setContentType("text/html");
		}
		PrintWriter out = response.getWriter();
		out.print(output);
	}

	private String getUnitDescriptionEncoded(Unit u) {
		String name = u.getName();
		String locality = u.getLocality();
		String description = htmlEncodeSwedishCharacters(Formatter.concatenate(name, locality));
		return description;
	}

	private String htmlEncodeSwedishCharacters(String name) {
		name = name.replace("&", "&amp;"); // �

		name = name.replace("å", "&#229;"); // �
		name = name.replace("\u00E5", "&#229;"); // �

		name = name.replace("ä", "&#228;"); // �
		name = name.replace("\u00E4", "&#228;"); // �

		name = name.replace("ö", "&#246;"); // �
		name = name.replace("\u00F6", "&#246;"); // �

		name = name.replace("Å", "&#197;"); // �
		name = name.replace("\u00C5", "&&#197;"); // �

		name = name.replace("Ä", "&#196;"); // �
		name = name.replace("\u00E4", "&#196;"); // �

		name = name.replace("Ö", "&#214;"); // �
		name = name.replace("\u00D6", "&#214;"); // �
		return name;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}
