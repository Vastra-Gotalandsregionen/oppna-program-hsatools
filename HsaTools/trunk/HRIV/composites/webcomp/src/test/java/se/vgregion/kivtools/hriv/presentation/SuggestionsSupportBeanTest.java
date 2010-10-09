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

package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.MunicipalityHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.UnitCache;
import se.vgregion.kivtools.search.svc.impl.cache.UnitCacheServiceImpl;

public class SuggestionsSupportBeanTest {

  private final UnitCacheLoaderMock unitCacheLoader = new UnitCacheLoaderMock();
  private final MockSearchUnitFlowSupportBean searchUnitFlowSupportBean = new MockSearchUnitFlowSupportBean();
  private final UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(unitCacheLoader);
  private final SuggestionsSupportBean bean = new SuggestionsSupportBean(searchUnitFlowSupportBean, unitCacheService);

  @Before
  public void setUp() throws Exception {
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
    unitCacheService.reloadCache();

    assertEquals(
        "Unexpected output for HTML",
        "<ul><li id=\"XYZ-987\">Angereds v&#229;rdcentral, Angered</li><li id=\"ABC-123\">M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;&#233;, M&#246;lndal</li><li id=\"JKL-654\">Slottsskogens v&#229;rdcentral</li></ul>",
        bean.getSuggestions("n", "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n<unit description=\"Angereds v&#229;rdcentral, Angered\" id=\"XYZ-987\" />\n"
        + "<unit description=\"M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;&#233;, M&#246;lndal\" id=\"ABC-123\" uri=\"http://www.test.com\" />\n"
        + "<unit description=\"Slottsskogens v&#229;rdcentral\" id=\"JKL-654\" uri=\"https://secure.test.com\" />\n</units>", bean.getSuggestions("n", "xml"));
    assertEquals("Unexpected output for plain text", "Angereds v&#229;rdcentral, Angered\tXYZ-987\n" + "M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;&#233;, M&#246;lndal\tABC-123\n"
        + "Slottsskogens v&#229;rdcentral\tJKL-654\n" + "", bean.getSuggestions("n", "text"));

    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions("Villa Villerkulla", "html"));
  }

  @Test
  public void testGetSuggestionsCacheNotComplete() {
    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions(null, "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n</units>", bean.getSuggestions(null, "xml"));
    assertEquals("Unexpected output for plain text", "", bean.getSuggestions(null, "text"));
    assertEquals("Unexpected output for HTML", "<ul></ul>", bean.getSuggestions("", "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n</units>", bean.getSuggestions("", "xml"));
    assertEquals("Unexpected output for plain text", "", bean.getSuggestions("", "text"));
  }

  @Test
  public void testGetSuggestionsSearched() {
    List<Unit> units = unitCacheLoader.loadCache().getUnits();
    SikSearchResultList<Unit> resultList = new SikSearchResultList<Unit>(units);
    searchUnitFlowSupportBean.setSearchResult(resultList);

    assertEquals("Unexpected output for HTML", "<ul><li id=\"XYZ-987\">Angereds v&#229;rdcentral, Angered</li>"
        + "<li id=\"ABC-123\">M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;&#233;, M&#246;lndal</li>" + "<li id=\"JKL-654\">Slottsskogens v&#229;rdcentral</li></ul>", bean
        .getSuggestions("n", "html"));
    assertEquals("Unexpected output for XML", "<?xml version='1.0' standalone='yes'?>\n<units>\n" + "<unit description=\"Angereds v&#229;rdcentral, Angered\" id=\"XYZ-987\" />\n"
        + "<unit description=\"M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;&#233;, M&#246;lndal\" id=\"ABC-123\" uri=\"http://www.test.com\" />\n"
        + "<unit description=\"Slottsskogens v&#229;rdcentral\" id=\"JKL-654\" uri=\"https://secure.test.com\" />\n" + "</units>", bean.getSuggestions("n", "xml"));
    assertEquals("Unexpected output for plain text", "Angereds v&#229;rdcentral, Angered\tXYZ-987\n" + "M&#246;lndals ABC &amp; &#229;&#228;&#246;&#197;&#196;&#214;&#233;, M&#246;lndal\tABC-123\n"
        + "Slottsskogens v&#229;rdcentral\tJKL-654\n" + "", bean.getSuggestions("n", "text"));
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

  private static class UnitCacheLoaderMock implements CacheLoader<UnitCache> {
    @Override
    public UnitCache createEmptyCache() {
      return new UnitCache();
    }

    @Override
    public UnitCache loadCache() {
      UnitCache unitCache = new UnitCache();

      unitCache.add(createUnit("ABC-123", "Mölndals ABC & åäöÅÄÖé", "Mölndal", "http://www.test.com"));
      unitCache.add(createUnit("XYZ-987", "Angereds vårdcentral", "Angered", ""));
      unitCache.add(createUnit("JKL-654", "Slottsskogens vårdcentral", null, "https://secure.test.com"));

      return unitCache;
    }

    private Unit createUnit(String hsaIdentity, String name, String locality, String labeledUri) {
      Unit unit = new Unit();
      unit.setHsaIdentity(hsaIdentity);
      unit.setName(name);
      unit.setLocality(locality);
      unit.setLabeledURI(labeledUri);
      return unit;
    }
  }
}
