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

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.MunicipalityHelper;

public class SuggestionsSupportBeanTest {

  private SuggestionsSupportBean bean;
  private MockSearchUnitFlowSupportBean searchUnitFlowSupportBean;

  @Before
  public void setUp() throws Exception {
    bean = new SuggestionsSupportBean();
    searchUnitFlowSupportBean = new MockSearchUnitFlowSupportBean();
    bean.setSearchUnitFlowSupportBean(searchUnitFlowSupportBean);
    new MunicipalityHelper().setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-municipalities");
  }

  @Test
  public void testGetSuggestionsNoFoundMatches() {
    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions(null, "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n</units>", bean.getSuggestions(null, "xml"));
    assertEquals("Unexpected output for plain text", "", bean.getSuggestions(null, "text"));
    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions("", "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n</units>", bean.getSuggestions("", "xml"));
    assertEquals("Unexpected output for plain text", "", bean.getSuggestions("", "text"));
  }

  @Test
  public void testGetSuggestionsExceptionHandling() {
    this.searchUnitFlowSupportBean.setExceptionToThrow(new KivException("Exception"));
    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions(null, "html"));
  }

  @Test
  public void testGetSuggestionsCachedMatches() {
    ArrayList<Unit> units = createUnits();
    searchUnitFlowSupportBean.setUnits(units);
    searchUnitFlowSupportBean.setUnitsCacheComplete(true);

    assertEquals("Unexpected output for HTML",
        "<ul><li id=\"XYZ-987\">Angereds v&#229;rdcentral, Angered</li><li id=\"ABC-123\">M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;, M&#246;lndal</li></ul>", bean.getSuggestions(
            "n", "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n<unit description=\"Angereds v&#229;rdcentral, Angered\" id=\"XYZ-987\" />\n"
        + "<unit description=\"M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;, M&#246;lndal\" id=\"ABC-123\" />\n" + "</units>", bean.getSuggestions("n", "xml"));
    assertEquals("Unexpected output for plain text", "Angereds v&#229;rdcentral, Angered\tXYZ-987\n" + "M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;, M&#246;lndal\tABC-123\n" + "",
        bean.getSuggestions("n", "text"));

    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions("Villa Villerkulla", "html"));
  }

  @Test
  public void testGetSuggestionsCacheNotComplete() {
    ArrayList<Unit> units = createUnits();
    searchUnitFlowSupportBean.setUnits(units);

    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions(null, "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n</units>", bean.getSuggestions(null, "xml"));
    assertEquals("Unexpected output for plain text", "", bean.getSuggestions(null, "text"));
    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions("", "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n</units>", bean.getSuggestions("", "xml"));
    assertEquals("Unexpected output for plain text", "", bean.getSuggestions("", "text"));
  }

  @Test
  public void testGetSuggestionsSearched() {
    ArrayList<Unit> units = createUnits();
    SikSearchResultList<Unit> resultList = new SikSearchResultList<Unit>(units);
    searchUnitFlowSupportBean.setSearchResult(resultList);

    assertEquals("Unexpected output for HTML",
        "<ul><li id=\"XYZ-987\">Angereds v&#229;rdcentral, Angered</li><li id=\"ABC-123\">M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;, M&#246;lndal</li></ul>", bean.getSuggestions(
            "n", "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n<unit description=\"Angereds v&#229;rdcentral, Angered\" id=\"XYZ-987\" />\n"
        + "<unit description=\"M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;, M&#246;lndal\" id=\"ABC-123\" />\n" + "</units>", bean.getSuggestions("n", "xml"));
    assertEquals("Unexpected output for plain text", "Angereds v&#229;rdcentral, Angered\tXYZ-987\n" + "M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;, M&#246;lndal\tABC-123\n" + "",
        bean.getSuggestions("n", "text"));
  }

  private ArrayList<Unit> createUnits() {
    ArrayList<Unit> units = new ArrayList<Unit>();
    Unit unit1 = new Unit();
    unit1.setHsaIdentity("ABC-123");
    unit1.setName("Mölndals ABC & åäöÅÄÖ");
    unit1.setLocality("Mölndal");
    Unit unit2 = new Unit();
    unit2.setHsaIdentity("XYZ-987");
    unit2.setName("Angereds vårdcentral");
    unit2.setLocality("Angered");
    Unit unit3 = new Unit();
    unit3.setHsaIdentity("JKL-654");
    unit3.setName("Slottsskogens vårdcentral");
    unit3.setLocality(null);
    units.add(unit1);
    units.add(unit2);
    units.add(unit3);

    return units;
  }

  @SuppressWarnings("serial")
  class MockSearchUnitFlowSupportBean extends SearchUnitFlowSupportBean {
    private SikSearchResultList<Unit> searchResult;
    private KivException exceptionToThrow;

    @Override
    public SikSearchResultList<Unit> doSearch(UnitSearchSimpleForm theForm) throws KivException {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
      return this.searchResult;
    }

    public void setSearchResult(SikSearchResultList<Unit> searchResult) {
      this.searchResult = searchResult;
    }

    public void setExceptionToThrow(KivException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }
  }
}
