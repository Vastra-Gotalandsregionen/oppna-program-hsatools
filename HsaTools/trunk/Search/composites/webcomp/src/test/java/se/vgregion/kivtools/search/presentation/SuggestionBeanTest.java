package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;

public class SuggestionBeanTest {

  private SuggestionBean suggestionBean;
  private CodeTableServiceMock codeTableServiceMock;

  @Before
  public void setUp() throws Exception {
    suggestionBean = new SuggestionBean();
    codeTableServiceMock = new CodeTableServiceMock();
    suggestionBean.setCodeTablesService(codeTableServiceMock);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetSuggestionsInvalidCodeTable() {
    suggestionBean.getSuggestions("", "");
  }

  @Test(expected = NullPointerException.class)
  public void testGetSuggestionsNullCodeTable() {
    suggestionBean.getSuggestions(null, null);
  }

  @Test
  public void testGetSuggestionWithValidValues() {
    String suggestions = suggestionBean.getSuggestions(CodeTableName.VGR_AO3_CODE.name(), "");
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n</suggestions>", suggestions);
  }

  class CodeTableServiceMock implements CodeTablesService {

    private List<String> codeValues = new ArrayList<String>();

    public void setCodeValues(List<String> codeValues) {
      this.codeValues = codeValues;
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return codeValues;
    }

    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return null;
    }

    @Override
    public void init() {
    }

  }
}
