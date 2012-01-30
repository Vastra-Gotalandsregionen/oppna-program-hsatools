/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

package se.vgregion.kivtools.search.presentation.kiv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;

public class SuggestionBeanTest {

  private static final String RESULT = "<?xml version='1.0' standalone='yes'?>\n<suggestions>\n<suggestion description=\"Test1\" />\n<suggestion description=\"Test2\" />\n</suggestions>";
  private SuggestionBean suggestionBean;
  private CodeTableServiceMock codeTableServiceMock;
  private MockHttpServletResponse httpServletResponse;

  @Before
  public void setUp() throws Exception {
    suggestionBean = new SuggestionBean();
    codeTableServiceMock = new CodeTableServiceMock();
    suggestionBean.setCodeTablesService(codeTableServiceMock);
    httpServletResponse = new MockHttpServletResponse();
    codeTableServiceMock.descriptionValues.add("Test1");
    codeTableServiceMock.descriptionValues.add("Test2");

  }

  @Test(expected = NullPointerException.class)
  public void testGetSuggestionsNullCodeTable() throws IOException {
    suggestionBean.getSuggestionsForSpeciality(httpServletResponse, null);
  }

  @Test
  public void testGetSuggestionsEmptyValue() throws IOException {
    suggestionBean.setCodeTablesService(new CodeTableServiceMock());
    suggestionBean.getSuggestionsForAdministration(httpServletResponse, "");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n</suggestions>", suggestions);
  }

  @Test
  public void testSuggestionsForProfession() throws IOException {
    suggestionBean.getSuggestionsForProfession(httpServletResponse, "test");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals(RESULT, suggestions);
  }

  @Test
  public void testSuggestionsForLanguageKnowledge() throws IOException {
    suggestionBean.getSuggestionsForLanguageKnowledge(httpServletResponse, "test");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals(RESULT, suggestions);
  }

  @Test
  public void testSuggestionsForSpeciality() throws IOException {
    suggestionBean.getSuggestionsForSpeciality(httpServletResponse, "test");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals(RESULT, suggestions);
  }

  @Test
  public void testGetSuggestionsAdministration() throws IOException {
    suggestionBean.getSuggestionsForAdministration(httpServletResponse, "test");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals(RESULT, suggestions);
  }

  @Test
  public void testGetSuggestionsBusinessClassification() throws IOException {
    suggestionBean.getSuggestionsForBusinessClassification(httpServletResponse, "test");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals(RESULT, suggestions);
  }

  class CodeTableServiceMock implements CodeTablesService {

    private List<String> descriptionValues = new ArrayList<String>();

    @Override
    public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return null;
    }

    @Override
    public String getValueFromCode(CodeTableNameInterface codeTableName, String string) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return descriptionValues;
    }

    @Override
    public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
      return null;
    }
  }
}
