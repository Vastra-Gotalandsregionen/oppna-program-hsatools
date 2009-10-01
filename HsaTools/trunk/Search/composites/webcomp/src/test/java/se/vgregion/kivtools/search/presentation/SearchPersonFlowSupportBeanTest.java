package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.presentation.forms.PersonSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.Unit;

public class SearchPersonFlowSupportBeanTest {
  private static final String PERSON_SEARCH_TYPE_VGRID = "vgrid_selected";
  private static final String DN = "cn=abc";
  private static final String HSA_IDENTITY = "HSA-123";
  private SearchPersonFlowSupportBean bean;
  private PersonSearchSimpleForm form;
  private SearchServiceMock searchService;
  private static LogFactoryMock logFactoryMock;

  @BeforeClass
  public static void setLogger() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Before
  public void setUp() throws Exception {
    bean = new SearchPersonFlowSupportBean();
    form = new PersonSearchSimpleForm();
    searchService = new SearchServiceMock();

    bean.setSearchService(searchService);
  }

  @Test
  public void testIsVgrIdSearch() throws KivNoDataFoundException {
    try {
      bean.isVgrIdSearch(null);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
      assertEquals("ERROR: se.vgregion.kivtools.search.presentation.SearchPersonFlowSupportBean::isVgrIdSearch(...) detected that theForm is null\n", logFactoryMock.getError(true));
    }

    assertFalse(bean.isVgrIdSearch(form));

    form.setSearchType(PERSON_SEARCH_TYPE_VGRID);
    assertTrue(bean.isVgrIdSearch(form));
  }

  @Test
  public void testDoSearch() throws KivNoDataFoundException {
    try {
      bean.doSearch(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    form.setGivenName("a");
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    SikSearchResultList<Person> persons = new SikSearchResultList<Person>();
    Person person = new Person();
    persons.add(person);
    searchService.setPersons(persons);
    SikSearchResultList<Person> result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(1, result.size());

    person.setDn(DN);
    SikSearchResultList<Employment> employments = new SikSearchResultList<Employment>();
    Employment employment = new Employment();
    employments.add(employment);
    searchService.addEmployment(person.getDn(), employments);
    result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(1, result.size());

    searchService.addExceptionToThrow(new KivException("Test"));
    result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(0, result.size());
    assertEquals("Test\n", logFactoryMock.getError(true));
  }

  @Test
  public void testGetPersonsForUnitsRecursive() {
    try {
      SikSearchResultList<Person> result = bean.getPersonsForUnitsRecursive(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    Unit unit = new Unit();
    unit.setHsaIdentity(HSA_IDENTITY);
    searchService.addUnit(unit);
    SikSearchResultList<Person> result = bean.getPersonsForUnitsRecursive(HSA_IDENTITY);
    assertNotNull(result);
    assertEquals(0, result.size());

    searchService.addExceptionToThrow(new KivException("Test"));
    bean.getPersonsForUnitsRecursive(HSA_IDENTITY);
    assertEquals("Test\n", logFactoryMock.getError(true));
  }

  @Test
  public void testGetOrganisation() throws KivNoDataFoundException {
    try {
      bean.getOrganisation(null, null);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    try {
      bean.getOrganisation(null, DN);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    SikSearchResultList<Person> persons = new SikSearchResultList<Person>();
    Person person = new Person();
    persons.add(person);
    searchService.setPersons(persons);
    SikSearchResultList<Person> result = bean.getOrganisation(null, DN);
    assertNotNull(result);
    assertEquals(1, result.size());

    Unit unit = new Unit();
    unit.setHsaIdentity(HSA_IDENTITY);
    searchService.addUnit(unit);
    try {
      bean.getOrganisation(HSA_IDENTITY, null);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    searchService.addPersonsForUnit(unit, persons);
    result = bean.getOrganisation(HSA_IDENTITY, null);
    assertNotNull(result);
    assertEquals(1, result.size());

    searchService.addExceptionToThrow(null);
    searchService.addExceptionToThrow(null);
    searchService.addExceptionToThrow(new KivException("Test"));
    result = bean.getOrganisation(HSA_IDENTITY, null);
    assertNotNull(result);
    assertEquals(0, result.size());
    assertEquals("Test\n", logFactoryMock.getError(true));
  }

  @Test
  public void testGetAllPersonsVgrId() throws KivNoDataFoundException {
    List<String> result = bean.getAllPersonsVgrId();
    assertNotNull(result);
    assertEquals(0, result.size());

    searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      bean.getAllPersonsVgrId();
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    searchService.clearExceptionsToThrow();
    searchService.addExceptionToThrow(new KivException("Dummy exception"));
    result = bean.getAllPersonsVgrId();
    assertNotNull(result);
    assertEquals(0, result.size());
    assertEquals("Dummy exception\n", logFactoryMock.getError(true));
  }

  @Test
  public void testGetRangePersonsVgrIdPageList() throws KivNoDataFoundException {
    List<String> result = bean.getRangePersonsVgrIdPageList(-1, -1);
    assertNotNull(result);
    assertEquals(0, result.size());
    assertEquals("getRangeUnitsPageList(startIndex=-1, endIndex=-1), Error input parameters are wrong (result list size=0)\n", logFactoryMock.getError(true));

    result = bean.getRangePersonsVgrIdPageList(2, 1);
    assertNotNull(result);
    assertEquals(0, result.size());
    assertEquals("getRangeUnitsPageList(startIndex=2, endIndex=1), Error input parameters are wrong (result list size=0)\n", logFactoryMock.getError(true));

    result = bean.getRangePersonsVgrIdPageList(0, 0);
    assertNotNull(result);
    assertEquals(0, result.size());
    assertEquals("MethodName=se.vgregion.kivtools.search.presentation.SearchPersonFlowSupportBean::getRangeUnitsPageList(startIndex=0, endIndex=0) detected that endIndex > \n", logFactoryMock
        .getError(true));

    List<String> allPersonsId = new ArrayList<String>();
    allPersonsId.add("abc-123");
    searchService.setAllPersonsId(allPersonsId);
    result = bean.getRangePersonsVgrIdPageList(0, 0);
    assertNotNull(result);
    assertEquals(1, result.size());

    searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      bean.getRangePersonsVgrIdPageList(0, 0);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetAllPersonsVgrIdPageList() throws KivNoDataFoundException {
    try {
      bean.getAllPersonsVgrIdPageList(null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
      assertEquals("pageSize must be greater than zero", e.getMessage());
    }

    List<PagedSearchMetaData> result = bean.getAllPersonsVgrIdPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());

    List<String> allPersonsId = new ArrayList<String>();
    allPersonsId.add("abc-123");
    searchService.setAllPersonsId(allPersonsId);
    result = bean.getAllPersonsVgrIdPageList("1");
    assertNotNull(result);
    assertEquals(1, result.size());

    allPersonsId.add("def-456");
    result = bean.getAllPersonsVgrIdPageList("3");
    assertNotNull(result);
    assertEquals(1, result.size());

    searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      bean.getAllPersonsVgrIdPageList("1");
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    searchService.clearExceptionsToThrow();
    searchService.addExceptionToThrow(new KivException("Test"));
    result = bean.getAllPersonsVgrIdPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testSetMaxSearchResults() throws KivNoDataFoundException {
    bean.setMaxSearchResult(3);

    form.setGivenName("a");
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.assertMaxSearchResults(3);

    bean.setMaxSearchResult(5);
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.assertMaxSearchResults(5);
  }

  @Test
  public void testSetPageSize() throws KivNoDataFoundException {
    bean.setPageSize(3);

    List<String> allPersonsId = new ArrayList<String>();
    allPersonsId.add("abc-123");
    allPersonsId.add("def-456");
    allPersonsId.add("ghi-789");
    searchService.setAllPersonsId(allPersonsId);
    List<PagedSearchMetaData> result = bean.getAllPersonsVgrIdPageList("1");
    assertNotNull(result);
    // 3 results per page == 1 page metadata
    assertEquals(1, result.size());

    bean.setPageSize(2);
    result = bean.getAllPersonsVgrIdPageList("1");
    assertNotNull(result);
    // 2 results per page == 2 page metadata
    assertEquals(2, result.size());

    bean.setPageSize(0);
    result = bean.getAllPersonsVgrIdPageList("1");
    assertNotNull(result);
    // 1 result per page == 3 page metadata
    assertEquals(3, result.size());
  }
}
