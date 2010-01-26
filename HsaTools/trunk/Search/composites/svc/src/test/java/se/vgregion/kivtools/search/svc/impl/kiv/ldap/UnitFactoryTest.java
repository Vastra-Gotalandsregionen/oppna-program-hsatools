package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;

public class UnitFactoryTest {
  private static final String TEST = "Test";
  private static final String VGR_OU = "vgrOrganizationalUnit";
  private static final String OU = "organizationalUnit";
  private static final String VGR_ORG_ROLE = "vgrOrganizationalRole";
  private static final String ORG_ROLE = "organizationalRole";
  private static final String CN = "ou=Unit,ou=Org,o=vgr";

  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";
  private static final String EXPECTED_HOURS = "Måndag-Fredag 08:30-10:00";
  private static final String EXPECTED_DATE_TIME = "2009-01-01 12:01:02";
  private static final String TEST_TIMESTAMP = "20090101120102";
  private static final String TEST_TIME = "1-5#08:30#10:00";
  private static final String TEST_ZULUTIME = "20090101120102Z";
  private LDAPEntryMock ldapEntry;
  private UnitFactory unitFactory;
  private SimpleDateFormat dateFormat;

  @Before
  public void setUp() throws Exception {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    unitFactory = new UnitFactory();
    unitFactory.setCodeTablesService(new CodeTablesServiceMock());
    DisplayValueTranslator displayValueTranslator = new DisplayValueTranslator();
    displayValueTranslator.setTranslationMap(new HashMap<String, String>());
    unitFactory.setDisplayValueTranslator(displayValueTranslator);

    ldapEntry = new LDAPEntryMock();
    ldapEntry.addAttribute("cn", CN);
    ldapEntry.addAttribute("objectClass", TEST);
    ldapEntry.addAttribute("ou", TEST + "\\, " + TEST);
    ldapEntry.addAttribute("hsaIdentity", TEST);
    ldapEntry.addAttribute("organizationalUnitNameShort", TEST);
    ldapEntry.addAttribute("description", TEST);
    ldapEntry.addAttribute("vgrInternalDescription", TEST);
    ldapEntry.addAttribute("mail", TEST);
    ldapEntry.addAttribute("l", TEST);
    ldapEntry.addAttribute("labeledUri", TEST);
    ldapEntry.addAttribute("vgrInternalSedfInvoiceAddress", TEST);
    ldapEntry.addAttribute("vgrCareType", TEST);
    ldapEntry.addAttribute("vgrAO3kod", TEST);
    ldapEntry.addAttribute("hsaBusinessClassificationCode", TEST);
    ldapEntry.addAttribute("hsaTextPhoneNumber", TEST);
    ldapEntry.addAttribute("hsaTextPhoneNumber", TEST);
    ldapEntry.addAttribute("mobileTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaSedfSwitchboardTelephoneNo", TEST);
    ldapEntry.addAttribute("hsaTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaSmsTelephoneNumber", TEST);
    ldapEntry.addAttribute("facsimileTelephoneNumber", TEST);
    ldapEntry.addAttribute("pagerTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaPublicTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaTelephoneTime", TEST_TIME);
    ldapEntry.addAttribute("hsaEndDate", TEST_ZULUTIME);
    ldapEntry.addAttribute("hsaSurgeryHours", TEST_TIME);
    ldapEntry.addAttribute("hsaDropInHours", TEST_TIME);
    ldapEntry.addAttribute("hsaInternalAddress", TEST);
    ldapEntry.addAttribute("hsaStreetAddress", TEST);
    ldapEntry.addAttribute("hsaPostalAddress", TEST);
    ldapEntry.addAttribute("hsaSedfDeliveryAddress", TEST);
    ldapEntry.addAttribute("hsaSedfInvoiceAddress", TEST);
    ldapEntry.addAttribute("hsaUnitPrescriptionCode", TEST);
    ldapEntry.addAttribute("vgrAnsvarsnummer", TEST);
    ldapEntry.addAttribute("hsaMunicipalityCode", TEST);
    ldapEntry.addAttribute("vgrEdiCode", TEST);
    ldapEntry.addAttribute("vgrEanCode", TEST);
    ldapEntry.addAttribute("hsaMunicipalitySectionName", TEST);
    ldapEntry.addAttribute("hsaMunicipalitySectionCode", TEST);
    ldapEntry.addAttribute("hsaCountyCode", TEST);
    ldapEntry.addAttribute("hsaCountyName", TEST);
    ldapEntry.addAttribute("hsaManagementCode", TEST);
    ldapEntry.addAttribute("hsaVisitingRules", TEST);
    ldapEntry.addAttribute("hsaVisitingRuleAge", TEST);
    ldapEntry.addAttribute("vgrTempInfo", TEST);
    ldapEntry.addAttribute("vgrRefInfo", TEST);
    ldapEntry.addAttribute("hsaAdministrationForm", TEST);
    ldapEntry.addAttribute("vgrModifyTimestamp", TEST_TIMESTAMP);
    ldapEntry.addAttribute("createTimeStamp", TEST_TIMESTAMP);
    ldapEntry.addAttribute("hsaGeographicalCoordinates", TEST);
    ldapEntry.addAttribute("hsaRoute", TEST);
    ldapEntry.addAttribute("vgrVardVal", TEST);
    ldapEntry.addAttribute("vgrAvtalskod", TEST);
    ldapEntry.addAttribute("vgrLabeledURI", "http://" + TEST);
    ldapEntry.addAttribute("hsaVisitingHours", TEST_TIME);
    ldapEntry.addAttribute("hsaVisitingRuleReferral", TEST);
  }

  @Test
  public void testInstantiation() {
    UnitFactory unitFactory = new UnitFactory();
    assertNotNull(unitFactory);
  }

  @Test
  public void testNullLDAPEntry() throws KivException {
    Unit unit = unitFactory.reconstitute(null);
    assertNotNull(unit);
  }

  @Test(expected = KivException.class)
  public void testReconstituteInvalidObjectClass() throws KivException {
    unitFactory.reconstitute(ldapEntry);
  }

  @Test
  public void testReconstituteVgrOrgUnit() throws KivException {
    ldapEntry.addAttribute("objectClass", VGR_OU);
    ldapEntry.addAttribute("hsaBusinessClassificationCode", "1504");
    Unit unit = unitFactory.reconstitute(ldapEntry);
    assertEquals(VGR_OU, unit.getObjectClass());
    assertEquals(TEST + "\\, " + TEST, unit.getOu());
    assertEquals(TEST + ", " + TEST, unit.getName());
    assertEquals(TEST, unit.getHsaIdentity());
    assertEquals(TEST, unit.getOrganizationalUnitNameShort());
    assertEquals(EXPECTED_LIST_RESULT, unit.getDescription().toString());
    assertEquals(TEST, unit.getInternalConcatenatedDescription());
    assertEquals(TEST, unit.getMail());
    assertEquals(TEST, unit.getLocality());
    assertEquals("http://" + TEST, unit.getLabeledURI());
    assertEquals(TEST, unit.getVgrInternalSedfInvoiceAddress());
    assertEquals(TEST, unit.getVgrCareType());
    assertEquals(TEST, unit.getVgrAO3kod());
    assertEquals("[1504]", unit.getHsaBusinessClassificationCode().toString());
    assertEquals(TEST, unit.getHsaTextPhoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaTextPhoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getMobileTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaSedfSwitchboardTelephoneNo().getPhoneNumber());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaTelephoneNumber().toString());
    assertEquals(TEST, unit.getHsaSmsTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getFacsimileTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getPagerTelephoneNumber().getPhoneNumber());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaTelephoneNumber().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaPublicTelephoneNumber().toString());
    assertEquals(EXPECTED_HOURS, unit.getHsaTelephoneTime().get(0).getDisplayValue());
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getHsaEndDate()));
    assertEquals(EXPECTED_HOURS, unit.getHsaSurgeryHours().get(0).getDisplayValue());
    assertEquals(EXPECTED_HOURS, unit.getHsaDropInHours().get(0).getDisplayValue());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaInternalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaStreetAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaPostalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaSedfDeliveryAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaSedfInvoiceAddress().getAdditionalInfo().toString());
    assertEquals(TEST, unit.getHsaUnitPrescriptionCode());
    assertEquals(EXPECTED_LIST_RESULT, unit.getVgrAnsvarsnummer().toString());
    assertEquals(TEST, unit.getHsaMunicipalityCode());
    assertEquals(TEST, unit.getVgrEDICode());
    assertEquals(TEST, unit.getVgrEANCode());
    assertEquals(TEST, unit.getHsaMunicipalitySectionName());
    assertEquals(TEST, unit.getHsaMunicipalitySectionCode());
    assertEquals(TEST, unit.getHsaCountyCode());
    assertEquals(TEST, unit.getHsaCountyName());
    assertEquals(TEST, unit.getHsaManagementCode());
    assertEquals(TEST, unit.getHsaVisitingRules());
    assertEquals(TEST + " år", unit.getHsaVisitingRuleAge());
    assertEquals(TEST, unit.getVgrTempInfo());
    assertEquals(TEST, unit.getVgrRefInfo());
    assertEquals(TEST, unit.getHsaAdministrationForm());
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getModifyTimestamp().asJavaUtilDate()));
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getCreateTimestamp().asJavaUtilDate()));
    assertEquals(TEST, unit.getHsaGeographicalCoordinates());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaRoute().toString());
    assertFalse(unit.isVgrVardVal());
    assertTrue(unit.getIsUnit());
    assertTrue(unit.isShowVisitingRules());
    assertTrue(unit.isShowAgeInterval());
    assertEquals(TEST, unit.getContractCode());
    assertEquals("http://" + TEST, unit.getInternalWebsite());
    assertEquals(EXPECTED_HOURS, unit.getVisitingHours().get(0).getDisplayValue());
    assertEquals(TEST, unit.getVisitingRuleReferral());
  }

  @Test
  public void testReconstituteOrgUnit() throws KivException {
    ldapEntry.addAttribute("objectClass", OU);
    Unit unit = unitFactory.reconstitute(ldapEntry);
    assertTrue(unit.getIsUnit());
    assertFalse(unit.isShowVisitingRules());
    assertFalse(unit.isShowAgeInterval());
  }

  @Test
  public void testReconstituteVgrOrgRole() throws KivException {
    ldapEntry.addAttribute("objectClass", VGR_ORG_ROLE);
    Unit unit = unitFactory.reconstitute(ldapEntry);
    assertFalse(unit.getIsUnit());
    assertEquals(TEST + "\\, " + TEST, unit.getOu());
    assertEquals(CN, unit.getName());
  }

  @Test
  public void testReconstituteOrgRole() throws KivException {
    ldapEntry.addAttribute("objectClass", ORG_ROLE);
    ldapEntry.addAttribute("hsaGeographicalCoordinates", "X: 1234567, Y: 1234567");
    Unit unit = unitFactory.reconstitute(ldapEntry);
    assertFalse(unit.getIsUnit());
    assertEquals(1234567, unit.getRt90X());
    assertEquals(1234567, unit.getRt90Y());
    assertEquals(11.159754999084681, unit.getWgs84Lat(), 0.0);
    assertEquals(13.376313261575913, unit.getWgs84Long(), 0.0);
  }

  @Test
  public void testHttpsURI() throws KivException {
    ldapEntry.addAttribute("objectClass", OU);
    ldapEntry.addAttribute("vgrLabeledURI", "https://" + TEST);
    Unit unit = unitFactory.reconstitute(ldapEntry);
    assertEquals("https://" + TEST, unit.getInternalWebsite());
  }

  class CodeTablesServiceMock implements CodeTablesService {
    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return "Translated " + string;
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
      return null;
    }
  }
}
