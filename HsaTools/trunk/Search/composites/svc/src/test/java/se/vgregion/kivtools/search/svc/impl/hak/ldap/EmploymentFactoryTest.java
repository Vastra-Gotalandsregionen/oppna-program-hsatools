package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;

public class EmploymentFactoryTest {
  private static final String TEST = "Test";
  private static final String TEST_DN = "cn=abc,ou=def";
  private static final String TEST_TIME = "1-5#08:30#10:00";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";
  private LDAPEntryMock ldapEntry;

  @Before
  public void setUp() throws Exception {
    ldapEntry = new LDAPEntryMock();
    ldapEntry.addAttribute("cn", TEST);
    ldapEntry.addAttribute("ou", TEST);
    ldapEntry.addAttribute("hsaPersonIdentityNumber", TEST);
    ldapEntry.addAttribute("vgrOrgRel", TEST);
    ldapEntry.addAttribute("vgrStrukturPerson", TEST_DN);
    ldapEntry.addAttribute("vgrAnsvarsnummer", TEST);
    ldapEntry.addAttribute("hsaStartDate", "20090101");
    ldapEntry.addAttribute("hsaEndDate", "20091231");
    ldapEntry.addAttribute("hsaSedfInvoiceAddress", TEST);
    ldapEntry.addAttribute("hsaStreetAddress", TEST);
    ldapEntry.addAttribute("hsaInternalAddress", TEST);
    ldapEntry.addAttribute("hsaPostalAddress", TEST);
    ldapEntry.addAttribute("hsaSedfDeliveryAddress", TEST);
    ldapEntry.addAttribute("facsimileTelephoneNumber", TEST);
    ldapEntry.addAttribute("postalCode", TEST);
    ldapEntry.addAttribute("labeledUri", TEST);
    ldapEntry.addAttribute("vgrAnstform", TEST);
    ldapEntry.addAttribute("title", "Leg. psykolog");
    ldapEntry.addAttribute("vgrFormansgrupp", TEST);
    ldapEntry.addAttribute("hsaSedfSwitchboardTelephoneNo", TEST);
    ldapEntry.addAttribute("vgrAO3kod", TEST);
    ldapEntry.addAttribute("organizationalUnitName", TEST);
    ldapEntry.addAttribute("hsaTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaPublicTelephoneNumber", TEST);
    ldapEntry.addAttribute("mobileTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaInternalPagerNumber", TEST);
    ldapEntry.addAttribute("pagerTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaTextPhoneNumber", TEST);
    ldapEntry.addAttribute("modifyTimestamp", TEST);
    ldapEntry.addAttribute("modifyersName", TEST);
    ldapEntry.addAttribute("hsaTelephoneTime", TEST_TIME);
    ldapEntry.addAttribute("description", TEST);
  }

  @Test
  public void testInstantiation() {
    EmploymentFactory employmentFactory = new EmploymentFactory();
    assertNotNull(employmentFactory);
  }

  @Test
  public void testReconstituteNullLDAPEntry() {
    Employment employment = EmploymentFactory.reconstitute(null);
    assertNotNull(employment);
    assertNull(employment.getCn());
  }

  @Test
  public void testReconstitute() {
    Employment employment = EmploymentFactory.reconstitute(ldapEntry);
    assertEquals(TEST, employment.getCn());
    assertEquals(TEST, employment.getOu());
    assertEquals(TEST, employment.getHsaPersonIdentityNumber());
    assertEquals(TEST, employment.getVgrOrgRel());
    assertEquals(DN.createDNFromString(TEST_DN), employment.getVgrStrukturPerson());
    assertEquals(TEST, employment.getVgrAnsvarsnummer());
    assertNotNull(employment.getEmploymentPeriod());
    // assertEquals(TimePoint.atMidnightGMT(2009, 1, 1), employment.getEmploymentPeriod().start());
    // assertEquals(TimePoint.atMidnightGMT(2009, 12, 31), employment.getEmploymentPeriod().end());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaSedfInvoiceAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaStreetAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaInternalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaPostalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaSedfDeliveryAddress().getAdditionalInfo().toString());
    assertEquals(TEST, employment.getFacsimileTelephoneNumber().toString());
    assertEquals(TEST, employment.getZipCode().getZipCode());
    assertEquals(TEST, employment.getLabeledUri());
    assertEquals(TEST, employment.getVgrAnstform());
    assertEquals("Leg. psykolog", employment.getTitle());
    assertEquals(TEST, employment.getVgrFormansgrupp());
    assertEquals(TEST, employment.getHsaSedfSwitchboardTelephoneNo().toString());
    assertEquals(TEST, employment.getVgrAO3kod());
    assertEquals(TEST, employment.getName());
    assertEquals(TEST, employment.getHsaTelephoneNumber().toString());
    assertEquals(TEST, employment.getHsaPublicTelephoneNumber().toString());
    assertEquals(TEST, employment.getMobileTelephoneNumber().toString());
    assertEquals(TEST, employment.getHsaInternalPagerNumber().toString());
    assertEquals(TEST, employment.getPagerTelephoneNumber().toString());
    assertEquals(TEST, employment.getHsaTextPhoneNumber().toString());
    assertNotNull(employment.getModifyTimestamp());
    assertEquals(TEST, employment.getModifyersName());
    assertEquals("Måndag-Fredag 08:30-10:00", employment.getHsaTelephoneTime().get(0).getDisplayValue());
    assertEquals(EXPECTED_LIST_RESULT, employment.getDescription().toString());
  }
}