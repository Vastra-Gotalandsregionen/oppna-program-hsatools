package se.vgregion.kivtools.search.presentation.kiv;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.util.presentation.PresentationHelper;

/**
 * Bean which provides suggestions for a type-ahead field.
 * 
 * @author David Bennehult & Joakim Olsson
 */
@Controller
public class SuggestionBean {

  private CodeTablesService codeTablesService;

  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  private String generateSuggestionForCodeTable(HttpServletResponse response, CodeTableName codeTableName, String value) throws IOException {
    String decodedValue = decodeUserInput(value);
    List<String> codeValues = codeTablesService.getValuesFromTextValue(codeTableName, decodedValue);
    String suggestionsXml = buildXml(codeValues);
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/xml");
    response.getWriter().write(suggestionsXml);
    
    // Will only write to the response object, return null because of view dispatcher in spring.
    return null;
  }

  /**
   * Lookup suggestions for a given value.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param query String value to lookup suggestions for.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions_HSA_SPECIALITY_CODE.servlet")
  public String getSuggestionsForSpeciality(HttpServletResponse response, @RequestParam("query") String query) throws IOException {
    return generateSuggestionForCodeTable(response, CodeTableName.HSA_SPECIALITY_CODE, query);
  }

  /**
   * Lookup suggestions for a given value.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param query String value to lookup suggestions for.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions_VGR_AO3_CODE.servlet")
  public String getSuggestionsForAdministration(HttpServletResponse response, @RequestParam("query") String query) throws IOException {
    return generateSuggestionForCodeTable(response, CodeTableName.VGR_AO3_CODE, query);
  }

  /**
   * Lookup suggestions for a given value.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param query String value to lookup suggestions for.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions_HSA_TITLE.servlet")
  public String getSuggestionsForProfession(HttpServletResponse response, @RequestParam("query") String query) throws IOException {
    return generateSuggestionForCodeTable(response, CodeTableName.HSA_TITLE, query);
  }

  /**
   * Lookup suggestions for a given value.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param query String value to lookup suggestions for.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions_HSA_LANGUAGE_KNOWLEDGE_CODE.servlet")
  public String getSuggestionsForLanguageKnowledge(HttpServletResponse response, @RequestParam("query") String query) throws IOException {
    return generateSuggestionForCodeTable(response, CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, query);
  }

  private String decodeUserInput(String input) {
    try {
      return URLDecoder.decode(input, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // Should not happen. Re-throwing as RuntimeException.
      throw new RuntimeException(e);
    }
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
