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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.NameMock;
import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;

public class EmploymentMapperTest {
  private static final String TEST = "Test";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";
  private static final String TEST_DN = "ou=Test,ou=Org,o=vgr";
  private static final String TEST_TIME = "1-5#08:30#10:00";
  private DirContextOperationsMock dirContextOperationsMock;
  private CodeTablesServiceMock codeTablesServiceMock;

  @Before
  public void setUp() throws Exception {
    codeTablesServiceMock = new CodeTablesServiceMock();
    dirContextOperationsMock = new DirContextOperationsMock();
    dirContextOperationsMock.setDn(new NameMock("ou=Folktandvården Fyrbodal,ou=Folktandvården Västra Götaland,ou=Org,o=vgr"));

    dirContextOperationsMock.addAttributeValue("cn", TEST);
    dirContextOperationsMock.addAttributeValue("ou", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPersonIdentityNumber", TEST);
    dirContextOperationsMock.addAttributeValue("vgrOrgRel", TEST);
    dirContextOperationsMock.addAttributeValue("vgrStrukturPerson", TEST_DN);
    dirContextOperationsMock.addAttributeValue("vgrAnsvarsnummer", TEST);
    dirContextOperationsMock.addAttributeValue("hsaStartDate", "20100101070300Z");
    dirContextOperationsMock.addAttributeValue("hsaEndDate", "20101231224401Z");
    dirContextOperationsMock.addAttributeValue("hsaSedfInvoiceAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaStreetAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaInternalAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPostalAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSedfDeliveryAddress", TEST);
    dirContextOperationsMock.addAttributeValue("facsimileTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("postalCode", TEST);
    dirContextOperationsMock.addAttributeValue("labeledUri", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAnstform", TEST);
    dirContextOperationsMock.addAttributeValue("title", TEST);
    dirContextOperationsMock.addAttributeValue("vgrFormansgrupp", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSedfSwitchboardTelephoneNo", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAO3kod", TEST);
    dirContextOperationsMock.addAttributeValue("organizationalUnitName", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPublicTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("mobileTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaInternalPagerNumber", TEST);
    dirContextOperationsMock.addAttributeValue("pagerTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTextPhoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("modifyTimestamp", "20100405123456Z");
    dirContextOperationsMock.addAttributeValue("modifyersName", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTelephoneTime", TEST_TIME);
    dirContextOperationsMock.addAttributeValue("description", TEST);
    dirContextOperationsMock.addAttributeValue("l", TEST);
    dirContextOperationsMock.addAttributeValue("paTitleCode", TEST);
  }

  @Test
  public void testMapFromContext() {
    EmploymentMapper employmentMapper = new EmploymentMapper(codeTablesServiceMock);
    Employment mapFromContext = employmentMapper.mapFromContext(dirContextOperationsMock);
    assertEmploymentResult(mapFromContext);
  }

  private void assertEmploymentResult(Employment employment) {
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
    public String getValueFromCode(CodeTableNameInterface codeTableName, String string) {
      return "Translated " + string;
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
      return null;
    }
  }
}
