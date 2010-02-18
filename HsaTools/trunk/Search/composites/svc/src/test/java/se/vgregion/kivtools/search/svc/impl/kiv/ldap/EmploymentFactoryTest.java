/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;

public class EmploymentFactoryTest {
  private static final String TEST = "Test";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";
  private static final String TEST_DN = "ou=Test,ou=Org,o=vgr";
  private static final String TEST_TIME = "1-5#08:30#10:00";
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
    ldapEntry.addAttribute("hsaStartDate", TEST);
    ldapEntry.addAttribute("hsaEndDate", TEST);
    ldapEntry.addAttribute("hsaSedfInvoiceAddress", TEST);
    ldapEntry.addAttribute("hsaStreetAddress", TEST);
    ldapEntry.addAttribute("hsaInternalAddress", TEST);
    ldapEntry.addAttribute("hsaPostalAddress", TEST);
    ldapEntry.addAttribute("hsaSedfDeliveryAddress", TEST);
    ldapEntry.addAttribute("facsimileTelephoneNumber", TEST);
    ldapEntry.addAttribute("postalCode", TEST);
    ldapEntry.addAttribute("labeledUri", TEST);
    ldapEntry.addAttribute("vgrAnstform", TEST);
    ldapEntry.addAttribute("title", TEST);
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
    ldapEntry.addAttribute("l", TEST);
    ldapEntry.addAttribute("paTitleCode", TEST);
  }

  @Test
  public void testInstantiation() {
    EmploymentFactory employmentFactory = new EmploymentFactory();
    assertNotNull(employmentFactory);
  }

  @Test
  public void testNullLDAPEntry() {
    Employment employment = EmploymentFactory.reconstitute(null, null);
    assertNotNull(employment);

  }

  @Test
  public void testReconstitute() {
    Employment employment = EmploymentFactory.reconstitute(ldapEntry, new CodeTablesServiceMock());
    assertEquals(TEST, employment.getCn());
    assertEquals(TEST, employment.getOu());
    assertEquals(TEST, employment.getHsaPersonIdentityNumber());
    assertEquals(TEST, employment.getVgrOrgRel());
    assertEquals(TEST_DN, employment.getVgrStrukturPerson().toString());
    assertEquals(TEST, employment.getVgrAnsvarsnummer());
    assertNotNull(employment.getEmploymentPeriod());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaSedfInvoiceAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaStreetAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaInternalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaPostalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaSedfDeliveryAddress().getAdditionalInfo().toString());
    assertEquals(TEST, employment.getFacsimileTelephoneNumber().toString());
    assertEquals(TEST, employment.getZipCode().getZipCode());
    assertEquals(TEST, employment.getLabeledUri());
    assertEquals(TEST, employment.getVgrAnstform());
    assertEquals(TEST, employment.getTitle());
    assertEquals(TEST, employment.getVgrFormansgrupp());
    assertEquals(TEST, employment.getHsaSedfSwitchboardTelephoneNo().toString());
    assertEquals(TEST, employment.getVgrAO3kod());
    assertEquals(TEST, employment.getOu());
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
    assertEquals(TEST, employment.getLocality());
    assertEquals("Translated " + TEST, employment.getPosition());
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
