package se.vgregion.kivtools.search.presentation;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;

public class DisplayPersonDetailsFlowSupportBeanTest {

  private static final String VGR_ID = "vgrId";
  private DisplayPersonDetailsFlowSupportBean displayPersonDetailsFlowSupportBean = new DisplayPersonDetailsFlowSupportBean();
  private Person personMock = new Person();

  @Before
  public void setup() throws Exception {
    SearchService searchServiceMock = createMock(SearchService.class);
    expect(searchServiceMock.getPersonById(VGR_ID)).andReturn(personMock);
    expect(searchServiceMock.getEmploymentsForPerson(personMock)).andReturn(new ArrayList<Employment>());
    replay(searchServiceMock);
    displayPersonDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }

  @Test
  public void testGetPersonDetails() throws Exception {
    assertEquals(personMock, displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID));
  }

  @Test
  public void testGetPersonDetailsPersonAlreadyGotEmployments() throws Exception {
    personMock.setEmployments(new ArrayList<Employment>());
    assertEquals(personMock, displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID));
  }

  @Test
  public void testExceptionHandling() throws Exception {
    SearchService searchServiceMock = createMock(SearchService.class);
    expect(searchServiceMock.getPersonById(VGR_ID)).andThrow(new KivException("Test"));
    replay(searchServiceMock);
    displayPersonDetailsFlowSupportBean.setSearchService(searchServiceMock);

    assertNotNull(displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID));
  }
}
