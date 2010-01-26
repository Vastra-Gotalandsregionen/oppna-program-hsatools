/**
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
package se.vgregion.kivtools.search.presentation.hak;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.kivtools.search.svc.CacheService;
import se.vgregion.kivtools.util.presentation.PresentationHelper;

/**
 * Bean which provides suggestions for a type-ahead field.
 * 
 * @author Joakim Olsson
 */
@Controller("suggestionBeanLTH")
public class SuggestionBean {
  private CacheService cacheService;

  public void setCacheService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  /**
   * Lookup suggestions for given names.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param givenName The given name to lookup suggestions for.
   * @param surname The surname the user has entered. Used to reduce the number of given names returned.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions_givenname.servlet")
  public String getSuggestionsForGivenName(HttpServletResponse response, @RequestParam("givenName") String givenName, @RequestParam("surname") String surname) throws IOException {
    List<String> matchingGivenNames = cacheService.getPersonNameCache().getMatchingGivenNames(givenName, surname);

    return generateSuggestions(response, matchingGivenNames);
  }

  /**
   * Lookup suggestions for surnames.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param givenName The given name the users has entered. Used to reduce the number of surnames returned.
   * @param surname The surname to lookup suggestions for.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions_surname.servlet")
  public String getSuggestionsForSurname(HttpServletResponse response, @RequestParam("givenName") String givenName, @RequestParam("surname") String surname) throws IOException {
    List<String> matchingSurnames = cacheService.getPersonNameCache().getMatchingSurnames(givenName, surname);
    return generateSuggestions(response, matchingSurnames);
  }

  /**
   * Lookup suggestions for unit names.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param unitName The unit name to lookup suggestions for.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions_unitname.servlet")
  public String getSuggestionsForUnitName(HttpServletResponse response, @RequestParam("query") String unitName) throws IOException {
    List<String> matchingUnitNames = cacheService.getUnitNameCache().getMatchingUnitNames(unitName);
    return generateSuggestions(response, matchingUnitNames);
  }

  private String generateSuggestions(HttpServletResponse response, List<String> suggestions) throws IOException {
    String suggestionsXml = buildXml(suggestions);
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/xml");
    response.getWriter().write(suggestionsXml);

    // Will only write to the response object, return null because of view dispatcher in spring.
    return null;
  }

  /**
   * Builds an XML-string from the provided suggestions.
   * 
   * @param suggestions The suggestions to include in the XML-string.
   * @return The XML-string for the provided suggestions.
   */
  private String buildXml(List<String> suggestions) {
    StringBuilder suggestionsXml = new StringBuilder();
    suggestionsXml.append("<?xml version='1.0' standalone='yes'?>\n");
    suggestionsXml.append("<suggestions>\n");
    for (String suggestion : suggestions) {
      suggestionsXml.append("<suggestion description=\"" + PresentationHelper.escapeXhtml(suggestion) + "\" />\n");
    }
    suggestionsXml.append("</suggestions>");
    return suggestionsXml.toString();
  }
}
