package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;

public class DisplayPersonDetailsFlowSupportBeanTest {

  private static final String PERSON_DN = "cn=Nina Kanin,ou=abc,ou=def";
  private static final String VGR_ID = "vgrId";
  private DisplayPersonDetailsFlowSupportBean displayPersonDetailsFlowSupportBean = new DisplayPersonDetailsFlowSupportBean();
  private Person personMock = new Person();
  private SearchServiceMock searchServiceMock;

  @Before
  public void setup() throws Exception {
    searchServiceMock = new SearchServiceMock();
    searchServiceMock.setPerson(personMock);
    displayPersonDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }

  @Test
  public void testGetPersonDetails() throws Exception {
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID);
    assertEquals(personMock, person);
  }

  @Test
  public void testGetPersonDetailsPersonAlreadyGotEmployments() throws Exception {
    personMock.setEmployments(new ArrayList<Employment>());
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID);
    assertEquals(personMock, person);
  }

  @Test
  public void testGetPersonDetailsExceptionHandling() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivException("exception"));
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID);
    assertNotNull(person);
  }

  @Test
  public void testGetPersonDetailsByDn() {
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN);
    assertNotNull(person);
  }

  @Test
  public void testGetPersonDetailsByDnPersonAlreadyGotEmployments() throws Exception {
    personMock.setEmployments(new ArrayList<Employment>());
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN);
    assertEquals(personMock, person);
  }

  @Test
  public void testGetPersonDetailsByDnExceptionHandling() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivException("exception"));
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN);
    assertNotNull(person);
  }
}
