package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.domain.values.ZipCode;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;

public class UnitDetailsServiceImplTest {

  private static final String UNIT_NAME = "UnitName";
  private static final String UNIT_HSA_IDENTITY = "UnitHsaIdentity";
  private UnitDetailsService<Organization> unitDetailsService;

  @Before
  public void setup() throws Exception {
    unitDetailsService = new UnitDetailsServiceImpl();
    ((UnitDetailsServiceImpl) unitDetailsService).setUnitRepository(generateUnitRepositoryMock());
  }

  @Test
  public void testGetUnitDetails() {

    Organization organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 1);
    assertEquals(UNIT_HSA_IDENTITY + 1, organization.getUnit().get(0).getId());
  }

  @Test
  public void testGetUnitDetailsKivException() throws Exception {
    UnitRepository unitRepositoryMock = new UnitRepository() {
      @Override
      public Unit getUnitByHsaId(String hsaId) throws KivException {
        throw new KivException("Test");
      }
    };
    ((UnitDetailsServiceImpl) unitDetailsService).setUnitRepository(unitRepositoryMock);

    try {
      unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetUnitDetailsWithNullAndEmptyString() {
    assertNotNull(unitDetailsService.getUnitDetails(null));
    assertNotNull(unitDetailsService.getUnitDetails(""));
  }

  @Test
  public void testUnitAddressInfo() {
    // Check Unit 0

    Organization organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 0);
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Address addressWs = organization.getUnit().get(0).getAddress().get(0);
    assertEquals("En trevlig mottagning", organization.getUnit().get(0).getDescription().get(0).getValue());
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals(null, addressWs.getStreetNumber());
    assertEquals("1111", organization.getUnit().get(0).getTelephone().get(0).getTelephoneNumber().get(0));
    assertEquals("http://unit", organization.getUnit().get(0).getEAlias().get(0).getAlias());
    assertEquals("Ingen parfym tack", organization.getUnit().get(0).getVisitingConditions().get(0).getVisitingRules());
    assertEquals("Måndag-Fredag 08:00-17:00, Lördag 10:00-14:00", organization.getUnit().get(0).getVisitingConditions().get(0).getVisitingHours());
    assertEquals("Måndag-Fredag 08:00-17:00, Lördag 10:00-14:00, Söndag 10:00-12:00", organization.getUnit().get(0).getVisitingConditions().get(0).getDropInHours());
    assertEquals("Måndag-Fredag 08:00-17:00", organization.getUnit().get(0).getVisitingConditions().get(0).getTelephoneHours());
    assertEquals("Landsting/region", organization.getUnit().get(0).getManagement().getValue());
    assertEquals("Vårdcentral", organization.getUnit().get(0).getBusinessClassification().get(0).getValue());
    assertEquals(2, organization.getUnit().get(0).getBusinessClassification().size());
    assertEquals("Götlaborg", organization.getUnit().get(0).getLocality().getValue());

    // Check Unit 1
    organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 1);
    addressWs = organization.getUnit().get(0).getAddress().get(0);
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals("12", addressWs.getStreetNumber());

    // Check Unit 2
    organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 2);
    addressWs = organization.getUnit().get(0).getAddress().get(0);
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals("1B", addressWs.getStreetNumber());

    // Check Unit 3
    organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 3);
    addressWs = organization.getUnit().get(0).getAddress().get(0);
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals("12b", addressWs.getStreetNumber());

  }

  private List<Address> generateUnitAddress() {
    Address addressWithoutNb = new Address("Teststreet", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
    Address addressWithNb = new Address("Teststreet 12", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
    Address addressWithNbAndChar = new Address("Teststreet 1B", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
    Address addressWithNbAndCharNoSpace = new Address("Teststreet12b", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
    return Arrays.asList(addressWithoutNb, addressWithNb, addressWithNbAndChar, addressWithNbAndCharNoSpace);
  }

  private UnitRepository generateUnitRepositoryMock() throws Exception {
    UnitRepository unitRepositoryMock = EasyMock.createMock(UnitRepository.class);
    CodeTablesService codeTablesServiceMock = EasyMock.createMock(CodeTablesService.class);
    // Fill unitRepositoryMock with unitMocks
    for (Entry<String, Unit> unitEntry : createUnitMocks(generateUnitAddress()).entrySet()) {
      org.easymock.EasyMock.expect(unitRepositoryMock.getUnitByHsaId(unitEntry.getKey())).andReturn(unitEntry.getValue());
    }

    org.easymock.EasyMock.expect(unitRepositoryMock.getCodeTablesService()).andReturn(codeTablesServiceMock);
    org.easymock.EasyMock.expect(codeTablesServiceMock.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "1500")).andReturn("Vårdcentral");
    org.easymock.EasyMock.expect(codeTablesServiceMock.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "1504")).andReturn("Akutmottagning");
    EasyMock.replay(unitRepositoryMock);
    return unitRepositoryMock;
  }

  // Create 4 units with hsaIdentity UnitHsaIdentity<0 to 3>
  private Map<String, Unit> createUnitMocks(List<Address> addressList) throws Exception {
    Map<String, Unit> units = new LinkedHashMap<String, Unit>();
    for (int i = 0; i < addressList.size(); i++) {
      Unit unit = new Unit();
      unit.setName(UNIT_NAME + i);
      unit.setHsaIdentity(UNIT_HSA_IDENTITY + i);
      unit.setDescription(Arrays.asList("En trevlig mottagning"));
      unit.setHsaStreetAddress(addressList.get(i));
      unit.setHsaPostalAddress(addressList.get(i));
      unit.setHsaPublicTelephoneNumber(Arrays.asList(PhoneNumber.createPhoneNumber("1111")));
      unit.setLabeledURI("http://unit");
      unit.setHsaVisitingRules("Ingen parfym tack");
      unit.setHsaDropInHours(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0), new WeekdayTime(6, 6, 10, 0, 14, 0), new WeekdayTime(7, 7, 10, 0, 12, 0)));
      unit.setHsaTelephoneTime(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0)));
      unit.setHsaSurgeryHours(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0), new WeekdayTime(6, 6, 10, 0, 14, 0)));
      unit.setHsaManagementText("Landsting/region");
      unit.setHealthcareTypes(Arrays.asList(new HealthcareType(null, "Vårdcentral", false, 0), new HealthcareType(null, "Akutmottagning", false, 1)));
      unit.setHsaMunicipalityName("Götlaborg");
      units.put(unit.getHsaIdentity(), unit);
    }
    return units;
  }
}
