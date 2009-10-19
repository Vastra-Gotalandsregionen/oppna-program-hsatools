package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;

public class SuggestionBeanTest {

  private SuggestionBean suggestionBean;
  private CodeTableServiceMock codeTableServiceMock;
  private MockHttpServletResponse httpServletResponse;

  @Before
  public void setUp() throws Exception {
    suggestionBean = new SuggestionBean();
    codeTableServiceMock = new CodeTableServiceMock();
    suggestionBean.setCodeTablesService(codeTableServiceMock);
    httpServletResponse = new MockHttpServletResponse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetSuggestionsInvalidCodeTable() throws IOException {
    suggestionBean.getSuggestions(httpServletResponse, "", "");
  }

  @Test(expected = NullPointerException.class)
  public void testGetSuggestionsNullCodeTable() throws IOException {
    suggestionBean.getSuggestions(httpServletResponse, null, null);
  }

  @Test
  public void testGetSuggestionsEmptyValue() throws IOException {
    suggestionBean.getSuggestions(httpServletResponse, CodeTableName.VGR_AO3_CODE.name(), "");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsValidValue() throws IOException {
    codeTableServiceMock.descriptionValues.add("Test1");
    codeTableServiceMock.descriptionValues.add("Test2");

    suggestionBean.getSuggestions(httpServletResponse, CodeTableName.VGR_AO3_CODE.name(), "test");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n<suggestion description=\"Test1\" />\n<suggestion description=\"Test2\" />\n</suggestions>", suggestions);
  }

  class CodeTableServiceMock implements CodeTablesService {

    private List<String> descriptionValues = new ArrayList<String>();

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }

    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return null;
    }

    @Override
    public void init() {
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
      return descriptionValues;
    }
  }
}
