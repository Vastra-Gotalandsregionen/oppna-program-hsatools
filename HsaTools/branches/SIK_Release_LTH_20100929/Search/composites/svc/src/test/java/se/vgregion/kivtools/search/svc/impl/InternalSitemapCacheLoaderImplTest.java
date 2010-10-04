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

package se.vgregion.kivtools.search.svc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.SitemapCache;
import se.vgregion.kivtools.search.svc.UnitCacheServiceImpl;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.MvkClient;
import se.vgregion.kivtools.util.http.HttpFetcher;

import com.domainlanguage.time.TimePoint;

public class InternalSitemapCacheLoaderImplTest {
  private final UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(new UnitCacheLoaderMock());
  private final SearchServiceMock searchService = new SearchServiceMock();
  private final HttpFetcherStaticMock httpFetcher = new HttpFetcherStaticMock();
  private final MvkClient mvkClient = new MvkClient(this.httpFetcher, "uid123", "http://localhost?mvk=1");
  private final InternalSitemapCacheLoaderImpl loader = new InternalSitemapCacheLoaderImpl(this.unitCacheService, this.searchService, this.mvkClient, "http://internal.com", "weekly");

  @Test
  public void createEmptyCacheReturnEmptyCache() {
    SitemapCache emptyCache = this.loader.createEmptyCache();
    assertNotNull(emptyCache);
    assertEquals(0, emptyCache.getEntries(null).size());
  }

  @Test
  public void loadCacheReloadsUnitCacheIfNoUnitsAreFound() {
    SitemapCache cache = this.loader.loadCache();
    assertNotNull(cache);
    assertEquals(6, cache.getEntries(null).size());
  }

  @Test
  public void locationUsesInternalUrlForUnits() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("http://internal.com/visaenhet?hsaidentity=ABC-123", cache.getEntries(null).get(0).getLocation());
  }

  @Test
  public void loadCacheUsesCreateTimestampForLastmodIfUnitIsNotModified() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("2010-02-10T01:00:00+01:00", cache.getEntries(null).get(0).getLastModified());
  }

  @Test
  public void loadCacheUsesModifyTimestampForLastmodIfUnitIsModified() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("2010-02-16T01:00:00+01:00", cache.getEntries(null).get(1).getLastModified());
  }

  @Test
  public void loadCacheFetchesAPersonsEmploymentsIfNotAlreadyLoaded() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("2010-04-22T02:00:00+02:00", cache.getEntries(null).get(5).getLastModified());
  }

  @Test
  public void unitIsAddedAsExtraInformation() {
    SitemapCache cache = this.loader.loadCache();

    for (Object extraInformation : cache.getEntries(null).get(2)) {
      assertTrue("extra information is a unit", extraInformation instanceof se.vgregion.kivtools.svc.sitemap.Unit);
      assertEquals("JKL-654", ((se.vgregion.kivtools.svc.sitemap.Unit) extraInformation).getHsaIdentity());
    }
  }

  @Test
  public void locationUsesInternalUrlForPersons() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("http://internal.com/visaperson?vgrid=krila8", cache.getEntries(null).get(4).getLocation());
  }

  @Test
  public void personIsAddedAsExtraInformation() {
    SitemapCache cache = this.loader.loadCache();
    for (Object extraInformation : cache.getEntries(null).get(3)) {
      assertTrue("extra information is a person", extraInformation instanceof se.vgregion.kivtools.svc.sitemap.Person);
      assertEquals("hsa-456", ((se.vgregion.kivtools.svc.sitemap.Person) extraInformation).getHsaIdentity());
    }
  }

  @Test
  public void latestDateOnEmploymentIsUsedForLastmod() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("2010-02-16T01:00:00+01:00", cache.getEntries(null).get(3).getLastModified());
  }

  @Test
  public void cacheLoadingIsStoppedAtKivException() {
    this.searchService.setExceptionToThrow(new KivException("error"));
    SitemapCache cache = this.loader.loadCache();
    assertNotNull(cache);
    assertEquals(0, cache.getEntries(null).size());
  }

  @Test
  public void mvkClientIsCalledForEachUnitToPopulateMvkServices() {
    this.loader.loadCache();
    assertEquals("calls to MVK", 3, this.httpFetcher.callCount);
  }

  private static class SearchServiceMock implements SearchService {
    List<Person> persons = new ArrayList<Person>();
    private KivException exceptionToThrow;

    public SearchServiceMock() {
      this.persons.add(this.createPerson("kon829", "hsa-456", TimePoint.atMidnightGMT(2010, 2, 12), TimePoint.atMidnightGMT(2010, 2, 16), TimePoint.atMidnightGMT(2010, 1, 10)));
      this.persons.add(this.createPerson("krila8", "hsa-123"));
      this.persons.add(this.createPerson("hanjo26", "hsa-789"));
    }

    public void setExceptionToThrow(KivException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    private Person createPerson(String vgrid, String hsaIdentity, TimePoint... employmentsModified) {
      Person person = new Person();
      person.setVgrId(vgrid);
      person.setHsaIdentity(hsaIdentity);
      person.setDn(vgrid);
      List<Employment> employments = new ArrayList<Employment>();
      for (int i = 0; employmentsModified != null && i < employmentsModified.length; i++) {
        Employment employment = new Employment();
        employment.setModifyTimestamp(employmentsModified[i]);
        employments.add(employment);
      }
      if (employments.size() > 0) {
        person.setEmployments(employments);
      }
      return person;
    }

    @Override
    public List<Person> getAllPersons() throws KivException {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
      return this.persons;
    }

    @Override
    public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException {
      SikSearchResultList<Employment> result = null;

      if (personDn.contains("hanjo26")) {
        Employment employment = new Employment();
        employment.setModifyTimestamp(TimePoint.atMidnightGMT(2010, 4, 22));
        result = new SikSearchResultList<Employment>();
        result.add(employment);
      }
      return result;
    }

    // Not implemented

    @Override
    public SikSearchResultList<Person> searchPersons(String id, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public Person getPersonById(String id) throws KivException {
      return null;
    }

    @Override
    public List<String> getAllUnitsHsaIdentity() throws KivException {
      return null;
    }

    @Override
    public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException {
      return null;
    }

    @Override
    public List<Employment> getEmploymentsForPerson(Person person) throws KivException {
      return null;
    }

    @Override
    public Person getPersonByDn(String personDn) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
      return null;
    }

    @Override
    public byte[] getProfileImageByDn(String dn) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public Unit getUnitByDN(String dn) throws KivException {
      return null;
    }

    @Override
    public Unit getUnitByHsaId(String hsaId) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public List<String> getAllPersonsId() throws KivException {
      return null;
    }

    @Override
    public List<Unit> getAllUnits(boolean onlyPublicUnits) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException {
      return null;
    }

    @Override
    public Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId) throws KivException {
      return null;
    }

    @Override
    public List<String> getUnitAdministratorVgrIds(String hsaId) throws KivException {
      return null;
    }
  }

  private static class HttpFetcherStaticMock implements HttpFetcher {
    private int callCount;

    @Override
    public String fetchUrl(String urlToFetch) {
      this.callCount++;
      return "<xml></xml>";
    }
  }
}
