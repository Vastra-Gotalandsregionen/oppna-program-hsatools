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

package se.vgregion.kivtools.search.presentation.hak;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.PersonNameCache;
import se.vgregion.kivtools.search.svc.cache.TitleCache;
import se.vgregion.kivtools.search.svc.cache.UnitNameCache;
import se.vgregion.kivtools.search.svc.impl.cache.PersonNameCacheServiceImpl;
import se.vgregion.kivtools.search.svc.impl.cache.TitleCacheServiceImpl;
import se.vgregion.kivtools.search.svc.impl.cache.UnitNameCacheServiceImpl;

public class SuggestionBeanTest {
  private SuggestionBean suggestionBean;
  private MockHttpServletResponse httpServletResponse;

  @Before
  public void setUp() throws Exception {
    suggestionBean = new SuggestionBean();
    PersonNameCacheServiceImpl personNameCacheService = new PersonNameCacheServiceImpl(new PersonNameCacheLoaderMock());
    UnitNameCacheServiceImpl unitNameCacheService = new UnitNameCacheServiceImpl(new UnitNameCacheLoaderMock());
    TitleCacheServiceImpl titleCacheService = new TitleCacheServiceImpl(new TitleCacheLoaderMock());
    personNameCacheService.reloadCache();
    unitNameCacheService.reloadCache();
    titleCacheService.reloadCache();
    suggestionBean.setPersonNameCacheService(personNameCacheService);
    suggestionBean.setUnitNameCacheService(unitNameCacheService);
    suggestionBean.setTitleCacheService(titleCacheService);
    httpServletResponse = new MockHttpServletResponse();
  }

  @Test
  public void testInstantiation() {
    SuggestionBean suggestionBean = new SuggestionBean();
    assertNotNull(suggestionBean);
  }

  @Test
  public void testGetSuggestionsForGivenNameNoMatches() throws IOException {
    suggestionBean.getSuggestionsForGivenName(httpServletResponse, "Anna", "");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsForGivenName() throws IOException {
    suggestionBean.getSuggestionsForGivenName(httpServletResponse, "nina", "");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n<suggestion description=\"Nina\" />\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsForSurnameNoMatches() throws IOException {
    suggestionBean.getSuggestionsForSurname(httpServletResponse, "", "Anka");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsForSurname() throws IOException {
    suggestionBean.getSuggestionsForSurname(httpServletResponse, "", "kanin");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n<suggestion description=\"Kanin\" />\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsForUnitNameNoMatches() throws IOException {
    suggestionBean.getSuggestionsForUnitName(httpServletResponse, "Hylte");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsForUnitName() throws IOException {
    suggestionBean.getSuggestionsForUnitName(httpServletResponse, "tand");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n<suggestion description=\"Tandregleringen Varberg\" />\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsForTitleNoMatches() throws IOException {
    suggestionBean.getSuggestionsForTitle(httpServletResponse, "assistent");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n</suggestions>", suggestions);
  }

  @Test
  public void testGetSuggestionsForTitle() throws IOException {
    suggestionBean.getSuggestionsForTitle(httpServletResponse, "ingen");
    String suggestions = httpServletResponse.getContentAsString();
    assertEquals("<?xml version='1.0' standalone='yes'?>\n<suggestions>\n<suggestion description=\"Systemingenjör\" />\n</suggestions>", suggestions);
  }

  private static class PersonNameCacheLoaderMock implements CacheLoader<PersonNameCache> {
    @Override
    public PersonNameCache loadCache() {
      PersonNameCache personNameCache = new PersonNameCache();
      personNameCache.add("Nina", "Kanin");
      return personNameCache;
    }

    @Override
    public PersonNameCache createEmptyCache() {
      return new PersonNameCache();
    }
  }

  private static class UnitNameCacheLoaderMock implements CacheLoader<UnitNameCache> {
    @Override
    public UnitNameCache loadCache() {
      UnitNameCache unitNameCache = new UnitNameCache();
      unitNameCache.add("Tandregleringen Varberg");
      return unitNameCache;
    }

    @Override
    public UnitNameCache createEmptyCache() {
      return new UnitNameCache();
    }
  }

  private static class TitleCacheLoaderMock implements CacheLoader<TitleCache> {
    @Override
    public TitleCache loadCache() {
      TitleCache titleCache = new TitleCache();
      titleCache.add("Systemingenjör");
      return titleCache;
    }

    @Override
    public TitleCache createEmptyCache() {
      return new TitleCache();
    }
  }
}
