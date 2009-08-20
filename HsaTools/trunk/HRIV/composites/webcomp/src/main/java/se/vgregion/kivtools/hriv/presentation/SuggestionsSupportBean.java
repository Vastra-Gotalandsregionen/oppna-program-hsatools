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
package se.vgregion.kivtools.hriv.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.UnitNameComparator;
import se.vgregion.kivtools.search.util.Formatter;

/**
 * Support-bean for the Suggestions-servlet.
 * 
 * @author Joakim Olsson
 */
public class SuggestionsSupportBean {
  private SearchUnitFlowSupportBean searchUnitFlowSupportBean;

  /**
   * Setter for the SearchUnitFlowSupportBean to use for looking up units.
   * 
   * @param searchUnitFlowSupportBean The SearchUnitFlowSupportBean to use.
   */
  public void setSearchUnitFlowSupportBean(SearchUnitFlowSupportBean searchUnitFlowSupportBean) {
    this.searchUnitFlowSupportBean = searchUnitFlowSupportBean;
  }

  /**
   * Gets suggestions based on the query in the specified output format.
   * 
   * @param unitName The unitname to find suggestions for.
   * @param outputFormat The format to output the result in.
   * @return A string containing the matching records in the specified format.
   */
  public String getSuggestions(String unitName, String outputFormat) {
    String suggestions = null;

    ArrayList<Unit> matchingUnits = new ArrayList<Unit>();

    // Get cached units
    ArrayList<Unit> units = searchUnitFlowSupportBean.getUnits();

    // If we have not yet cached the units, search in catalog
    if (units == null || !searchUnitFlowSupportBean.isUnitsCacheComplete()) {
      SikSearchResultList<Unit> resultList = performSearch(unitName);

      // Transfer matching units from resultList to matchingUnits
      if (resultList != null) {
        matchingUnits.addAll(resultList);
      }
    } else {
      // We have the units cached
      matchingUnits.addAll(getMatchingUnits(unitName, units));
    }

    // Sort units on name
    Collections.sort(matchingUnits, new UnitNameComparator());

    if ("xml".equals(outputFormat)) {
      // XML
      suggestions = buildXml(matchingUnits);
    } else if ("text".equals(outputFormat)) {
      // Flat text response, tab as field delimiter and newline as record
      // delimiter.
      suggestions = buildPlainText(matchingUnits);
    } else {
      // Default to HTML (unordered list, UL)
      suggestions = buildHtml(matchingUnits);
    }

    return suggestions.toString();
  }

  /**
   * Builds an HTML-string from the provided units. Each unit is an &lt;li&gt;-element and all units are contained in an &lt;ul&gt;-element.
   * 
   * @param units The units to include in the HTML-string.
   * @return The HTML-string from the provided units.
   */
  private String buildHtml(List<Unit> units) {
    StringBuilder suggestions = new StringBuilder();

    suggestions.append("<ul>");
    if (units != null) {
      for (Unit u : units) {
        String description = getUnitDescriptionEncoded(u);
        suggestions.append("<li id=\"").append(u.getHsaIdentity()).append("\">").append(description).append("</li>");
      }
    }
    suggestions.append("</ul>");

    return suggestions.toString();
  }

  /**
   * Builds a tab-separated string from the provided units.
   * 
   * @param units The units to include in the tab-separated string.
   * @return The tab-separated string from the provided units.
   */
  private String buildPlainText(List<Unit> units) {
    StringBuilder suggestions = new StringBuilder();

    if (units != null) {
      for (Unit u : units) {
        String description = getUnitDescriptionEncoded(u);
        suggestions.append(description).append("\t").append(u.getHsaIdentity()).append("\n");
      }
    }

    return suggestions.toString();
  }

  /**
   * Builds an XML-string from the provided units.
   * 
   * @param units The units to include in the XML-string.
   * @return The XML-string from the provided units.
   */
  private String buildXml(List<Unit> units) {
    StringBuilder suggestions = new StringBuilder();

    suggestions.append("<?xml version='1.0' standalone='yes'?>\n<units>\n");
    if (units != null) {
      for (Unit u : units) {
        String description = getUnitDescriptionEncoded(u);
        suggestions.append("<unit description=\"" + description + "\" id=\"" + u.getHsaIdentity() + "\" />\n");
      }
    }
    suggestions.append("</units>");

    return suggestions.toString();
  }

  /**
   * Gets a subset of the provided list of units where the name of the unit matches the provided unitName.
   * 
   * @param units The list of units to find matching units in.
   */
  private List<Unit> getMatchingUnits(String unitName, ArrayList<Unit> units) {
    List<Unit> matchingUnits = new ArrayList<Unit>();
    for (Unit u : units) {
      if (u.getName().toLowerCase().indexOf(unitName.toLowerCase()) >= 0) {
        matchingUnits.add(u);
      }
    }

    return matchingUnits;
  }

  /**
   * Search for units matching the provided unitname.
   * 
   * @param unitName The unitname to search for.
   * @return A list of matching units.
   */
  private SikSearchResultList<Unit> performSearch(String unitName) {
    SikSearchResultList<Unit> resultList = null;

    UnitSearchSimpleForm theForm = new UnitSearchSimpleForm();
    theForm.setUnitName(unitName);

    try {
      resultList = searchUnitFlowSupportBean.doSearch(theForm);
    } catch (KivException e) {
      // Not too much to do...
      e.printStackTrace();
    }
    return resultList;
  }

  /**
   * Helper-method to get the description of a unit (name and locality concatenated) in an encoded format.
   * 
   * @param unit The unit to get the description for.
   * @return The units description with swedish characters encoded to their HTML entities.
   */
  private String getUnitDescriptionEncoded(Unit unit) {
    String name = unit.getName();
    String locality = unit.getLocality();
    String description = htmlEncodeSwedishCharacters(Formatter.concatenate(name, locality));
    return description;
  }

  /**
   * Encodes swedish characters etc. in the provided string to their HTML entities.
   * 
   * @param string The string to encode.
   * @return The provided string in an encoded form.
   */
  private String htmlEncodeSwedishCharacters(String string) {
    String result = string;
    // &
    result = result.replace("&", "&amp;");

    // å
    result = result.replace("\u00E5", "&#229;");

    // ä
    result = result.replace("\u00E4", "&#228;");

    // ö
    result = result.replace("\u00F6", "&#246;");

    // Å
    result = result.replace("\u00C5", "&#197;");

    // Ä
    result = result.replace("\u00C4", "&#196;");

    // Ö
    result = result.replace("\u00D6", "&#214;");

    return result;
  }
}
