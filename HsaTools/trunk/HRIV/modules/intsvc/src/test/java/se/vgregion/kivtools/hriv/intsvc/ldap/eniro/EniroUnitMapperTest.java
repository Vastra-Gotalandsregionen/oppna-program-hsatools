package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;

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
    dirContextOperationsMock.addAttributeValue("hsaPublicTelephoneNumber", "+46 31 3450700");
    dirContextOperationsMock.addAttributeValue("hsaTelephoneTime", "1-5#08:00#18:00");
    UnitComposition unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    assertEquals("id1", unitComposition.getEniroUnit().getId());
    assertEquals("name", unitComposition.getEniroUnit().getName());
    assertEquals("locality", unitComposition.getEniroUnit().getLocality());
    BusinessClassification businessClassification = null;
    for (Object info : unitComposition.getEniroUnit().getTextOrImageOrAddress()) {
      // Make sure that no address is created.
      assertFalse(info instanceof Address);

      if (info instanceof BusinessClassification) {
        businessClassification = (BusinessClassification) info;
      } else if (info instanceof TelephoneType) {
        TelephoneType telephoneType = (TelephoneType) info;
        assertEquals("+46 31 3450700", telephoneType.getTelephoneNumber().get(0));
      }
    }
    assertEquals("1", businessClassification.getBCCode());
    assertEquals("", businessClassification.getBCName());
  }
   
  @Test
  public void testMapCn() {
    // Test function unit with cn instead of ou.
    dirContextOperationsMock.setAttributeValue("ou", null);
    dirContextOperationsMock.addAttributeValue("cn", "name");
    dirContextOperationsMock.addAttributeValue("hsaSedfDeliveryAddress", ldapAddressValue);
    dirContextOperationsMock.addAttributeValue("hsaGeographicalCoordinates", "X: 6414080, Y: 1276736");
    UnitComposition unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    Address address = (Address) unitComposition.getEniroUnit().getTextOrImageOrAddress().get(0);
    assertEquals("Baker street", address.getStreetName());
    assertEquals("221", address.getStreetNumber());
    assertEquals("123 45", address.getPostCode().get(0));
    assertEquals("London", address.getCity());
    assertEquals("visit", address.getType());
  }

  @Test
  public void testMapNullValues() {
    // Test null values
    dirContextOperationsMock = new DirContextOperationsMock();
    dirContextOperationsMock.setDn(new NameMock("dn"));
    UnitComposition unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    assertEquals(1, unitComposition.getEniroUnit().getTextOrImageOrAddress().size());

  }

  private void setAttributeMocks() {
    dirContextOperationsMock.addAttributeValue("createTimeStamp", "20090118094127Z");
    dirContextOperationsMock.addAttributeValue("vgrModifyTimestamp", "20090318094127Z");
    dirContextOperationsMock.setDn(new NameMock("dn"));
    dirContextOperationsMock.addAttributeValue("hsaIdentity", "id1");
    dirContextOperationsMock.addAttributeValue("ou", "name");
    dirContextOperationsMock.addAttributeValue("hsaSurgeryHours", "1-5#08:00#17:00");
    dirContextOperationsMock.addAttributeValue("hsaBusinessClassificationCode", "1");
    dirContextOperationsMock.addAttributeValue("l", "locality");
  }
}
