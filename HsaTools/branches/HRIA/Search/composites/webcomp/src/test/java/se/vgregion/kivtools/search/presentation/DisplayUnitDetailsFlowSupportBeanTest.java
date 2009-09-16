package se.vgregion.kivtools.search.presentation;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;

public class DisplayUnitDetailsFlowSupportBeanTest {

  private static final String HSA_ID = "hsaId";
  private static final String DN = "dn";
  DisplayUnitDetailsFlowSupportBean displayUnitDetailsFlowSupportBean = new DisplayUnitDetailsFlowSupportBean();
  Unit unitMock = new Unit();

  @Before
  public void setup() throws Exception {
    unitMock.setName("unitName");
    SearchService searchServiceMock = createMock(SearchService.class);
    expect(searchServiceMock.getUnitByDN(DN)).andReturn(unitMock);
    expect(searchServiceMock.getUnitByHsaId(HSA_ID)).andReturn(unitMock);
    replay(searchServiceMock);
    displayUnitDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }
  
  @Test
  public void testGetUnitDetails() {
    assertEquals(unitMock.getName(), displayUnitDetailsFlowSupportBean.getUnitDetails(HSA_ID).getName());
  }
  
  @Test
  public void testUnitByDn(){
   assertEquals(unitMock.getName(), displayUnitDetailsFlowSupportBean.getUnitByDn(DN).getName()); 
  }
  
  @Test
  public void testExceptionHandling(){
    displayUnitDetailsFlowSupportBean.setSearchService(null);
    assertNotNull(displayUnitDetailsFlowSupportBean.getUnitByDn(DN));
    assertNotNull(displayUnitDetailsFlowSupportBean.getUnitDetails(HSA_ID));
  }
}
