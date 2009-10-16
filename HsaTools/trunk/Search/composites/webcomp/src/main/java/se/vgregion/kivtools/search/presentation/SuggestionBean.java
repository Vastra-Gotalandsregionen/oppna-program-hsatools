package se.vgregion.kivtools.search.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;

/**
 * 
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class SuggestionBean {

  private CodeTablesService codeTablesService;

  @Autowired
  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  /**
   * Lookup suggestion for a given value.
   * 
   * @param fieldKey Name of field to provide suggestions for.
   * @param value String value to lookup suggestions for.
   * @return A xml presentation of suggestions.
   */
  public String getSuggestions(String fieldKey, String value) {
    CodeTableName codeTable = CodeTableName.valueOf(fieldKey);
    List<String> codeValues = codeTablesService.getCodeFromTextValue(codeTable, value);

    return buildXml(codeValues);
  }

  /**
   * Builds an XML-string from the provided suggestions.
   * 
   * @param suggestions The units to include in the XML-string.
   * @return The XML-string from the provided units.
   */
  private String buildXml(List<String> suggestions) {
    StringBuilder suggestionsXml = new StringBuilder();
    suggestionsXml.append("<?xml version='1.0' standalone='yes'?>\n");
    suggestionsXml.append("<suggestions>\n");
    for (String suggestion : suggestions) {
      suggestionsXml.append("<suggestion>" + suggestion + "</suggestion>\n");
    }
    suggestionsXml.append("</suggestions>");
    return suggestionsXml.toString();
  }
}
