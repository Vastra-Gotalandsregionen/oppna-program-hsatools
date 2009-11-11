package se.vgregion.kivtools.search.presentation.hak;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import se.vgregion.kivtools.search.svc.CacheService;
import se.vgregion.kivtools.search.svc.PersonNameCache;
import se.vgregion.kivtools.search.svc.PersonNameCacheLoader;
import se.vgregion.kivtools.search.svc.UnitNameCache;
import se.vgregion.kivtools.search.svc.UnitNameCacheLoader;

public class SuggestionBeanTest {
  private SuggestionBean suggestionBean;
  private MockHttpServletResponse httpServletResponse;

  @Before
  public void setUp() throws Exception {
    suggestionBean = new SuggestionBean();
    CacheService cacheService = new CacheService();
    cacheService.setPersonNameCacheLoader(new PersonNameCacheLoaderMock());
    cacheService.setUnitNameCacheLoader(new UnitNameCacheLoaderMock());
    cacheService.reloadCaches();
    suggestionBean.setCacheService(cacheService);
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

  private static class PersonNameCacheLoaderMock implements PersonNameCacheLoader {
    @Override
    public PersonNameCache loadCache() {
      PersonNameCache personNameCache = new PersonNameCache();
      personNameCache.add("Nina", "Kanin");
      return personNameCache;
    }
  }

  private static class UnitNameCacheLoaderMock implements UnitNameCacheLoader {
    @Override
    public UnitNameCache loadCache() {
      UnitNameCache unitNameCache = new UnitNameCache();
      unitNameCache.add("Tandregleringen Varberg");
      return unitNameCache;
    }
  }
}
