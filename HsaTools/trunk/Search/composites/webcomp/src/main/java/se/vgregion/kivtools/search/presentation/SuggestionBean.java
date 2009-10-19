package se.vgregion.kivtools.search.presentation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;

/**
 * Bean which provides suggestions for a type-ahead field.
 * 
 * @author David Bennehult & Joakim Olsson
 */
@Controller
public class SuggestionBean {

  private CodeTablesService codeTablesService;

  @Autowired
  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  /**
   * Lookup suggestions for a given value.
   * 
   * @param response The HttpServletResponse to stream the generated suggestions-XML to.
   * @param fieldKey Name of field to provide suggestions for.
   * @param value String value to lookup suggestions for.
   * @return Always returns null since the result is streamed back to the client.
   * @throws IOException if there is a problem writing the generated XML to the client.
   */
  @RequestMapping("/suggestions.servlet")
  public String getSuggestions(HttpServletResponse response, @RequestParam("fieldKey") String fieldKey, @RequestParam("value") String value) throws IOException {
    String decodedValue = decodeUserInput(value);
    CodeTableName codeTable = CodeTableName.valueOf(fieldKey);
    List<String> codeValues = codeTablesService.getValuesFromTextValue(codeTable, decodedValue);

    String suggestionsXml = buildXml(codeValues);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(suggestionsXml);

    return null;
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
      suggestionsXml.append("<suggestion description=\"" + suggestion + "\" />\n");
    }
    suggestionsXml.append("</suggestions>");
    return suggestionsXml.toString();
  }
}
