package se.vgregion.kivtools.search.presentation;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;

public class DisplayPersonDetailsTest {
  
  private static final String VGR_ID = "vgrId";
  private DisplayPersonDetailsFlowSupportBean displayUnitDetailsFlowSupportBean = new DisplayPersonDetailsFlowSupportBean();
  private Person personMock = new Person();
  
  @Before
  public void setup() throws Exception{
    SearchService searchServiceMock = createMock(SearchService.class);
    expect(searchServiceMock.getPersonById(VGR_ID)).andReturn(personMock);
    expect(searchServiceMock.getEmploymentsForPerson(personMock)).andReturn(new ArrayList<Employment>());
    replay(searchServiceMock);
    displayUnitDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }
  
  @Test
  public void testGetPersonDetails() throws Exception{
    assertEquals(personMock,displayUnitDetailsFlowSupportBean.getPersonDetails(VGR_ID));
  }
  
  @Test
  public void testExceptionHandling(){
    displayUnitDetailsFlowSupportBean.setSearchService(null);
    assertNotNull(displayUnitDetailsFlowSupportBean.getPersonDetails(VGR_ID));
  }
}
