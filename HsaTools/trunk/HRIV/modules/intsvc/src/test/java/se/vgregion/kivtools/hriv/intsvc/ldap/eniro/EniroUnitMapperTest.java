package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;

public class EniroUnitMapperTest {

  private EniroUnitMapper eniroUnitMapper;
  private DirContextOperationsMock dirContextOperationsMock;
  private String ldapAddressValue = "$$Baker street 221$$123 45$London";
  
  @Before
  public void setup() {
    eniroUnitMapper = new EniroUnitMapper(Arrays.asList("1"));
    dirContextOperationsMock = new DirContextOperationsMock();
    setAttributeMocks();
  }

  @Test
  public void testMapFromContext() {
    UnitComposition unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    
    //Test function unit with cn instead of ou.
    dirContextOperationsMock.setAttributeValue("ou", null);
    dirContextOperationsMock.addAttributeValue("cn", "name");
    unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    Address address = (Address) unitComposition.getEniroUnit().getTextOrImageOrAddress().get(0);
    assertEquals("Baker street", address.getStreetName());
    assertEquals("221", address.getStreetNumber());
    assertEquals("123 45", address.getPostCode().get(0));
    assertEquals("London", address.getCity());
    // Test null values
    dirContextOperationsMock = new DirContextOperationsMock();
    dirContextOperationsMock.setDn(new NameMock("dn"));
    unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    
  }

  private void setAttributeMocks() {
    dirContextOperationsMock.addAttributeValue("createTimeStamp", "20090118094127Z");
    dirContextOperationsMock.addAttributeValue("vgrModifyTimestamp", "20090318094127Z");
    dirContextOperationsMock.setDn(new NameMock("dn"));
    dirContextOperationsMock.addAttributeValue("hsaIdentity", "id1");
    dirContextOperationsMock.addAttributeValue("ou", "name");
    dirContextOperationsMock.addAttributeValue("hsaSurgeryHours", "1-5#08:00#17:00");
    dirContextOperationsMock.addAttributeValue("hsaPublicTelephoneNumber", "+46 31 3450700");
    dirContextOperationsMock.addAttributeValue("hsaTelephoneTime", "1-5#08:00#18:00");
    dirContextOperationsMock.addAttributeValue("hsaGeographicalCoordinates", "X: 6414080, Y: 1276736");
    dirContextOperationsMock.addAttributeValue("hsaBusinessClassificationCode", "1");
    dirContextOperationsMock.addAttributeValue("hsaSedfDeliveryAddress", ldapAddressValue);
  }
}
