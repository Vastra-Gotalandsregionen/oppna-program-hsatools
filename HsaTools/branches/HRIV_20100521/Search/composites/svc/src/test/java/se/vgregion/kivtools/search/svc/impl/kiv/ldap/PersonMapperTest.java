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

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.NameMock;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;

import com.domainlanguage.time.TimePoint;

public class PersonMapperTest {
  private static final String DN = "cn=abcd12,ou=Personal,o=vgr";
  private static final String TEST = "Test";
  private CodeTablesServiceMock codeTablesServiceMock = new CodeTablesServiceMock();
  private DirContextOperationsMock dirContextOperationsMock = new DirContextOperationsMock();
  private PersonMapper personMapper = new PersonMapper(codeTablesServiceMock);

  @Before
  public void setUp() throws Exception {
    dirContextOperationsMock.setDn(new NameMock(DN));
    dirContextOperationsMock.addAttributeValue("cn", TEST);
    dirContextOperationsMock.addAttributeValue("vgr-id", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPersonIdentityNumber", TEST);
    dirContextOperationsMock.addAttributeValue("givenName", TEST);
    dirContextOperationsMock.addAttributeValue("sn", TEST);
    dirContextOperationsMock.addAttributeValue("hsaMiddleName", TEST);
    dirContextOperationsMock.addAttributeValue("initials", TEST);
    dirContextOperationsMock.addAttributeValue("hsaNickName", TEST);
    dirContextOperationsMock.addAttributeValue("fullName", TEST);
    dirContextOperationsMock.addAttributeValue("vgrStrukturPersonDN", TEST);
    dirContextOperationsMock.addAttributeValue("vgrOrgRel", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAnstform", TEST);
    dirContextOperationsMock.addAttributeValue("hsaIdentity", TEST);
    dirContextOperationsMock.addAttributeValue("mail", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSpecialityName", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSpecialityCode", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAO3kod", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAnsvarsnummer", TEST);
    dirContextOperationsMock.addAttributeValue("hsaLanguageKnowledgeCode", TEST);
    dirContextOperationsMock.addAttributeValue("hsaLanguageKnowledgeText", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTitle", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPersonPrescriptionCode", TEST);
    dirContextOperationsMock.addAttributeValue("hsaStartDate", "20100101070300Z");
    dirContextOperationsMock.addAttributeValue("hsaEndDate", "20101231224401Z");
    dirContextOperationsMock.addAttributeValue("hsaLanguageKnowledgeCode", TEST);
  }

  @Test
  public void testPersonMapper() {
    Person person = personMapper.mapFromContext(dirContextOperationsMock);
    assertPersonResult(person);
  }

  @Test
  public void createAndModifyTimestampsAreMappedCorrectly() {
    dirContextOperationsMock.addAttributeValue("createTimestamp", "20100101064500Z");
    dirContextOperationsMock.addAttributeValue("modifyTimestamp", "20100427152833Z");

    Person person = personMapper.mapFromContext(dirContextOperationsMock);
    assertEquals("create timestamp", TimePoint.atGMT(2010, 1, 1, 5, 45, 0), person.getCreateTimestamp());
    assertEquals("modify timestamp", TimePoint.atGMT(2010, 4, 27, 13, 28, 33), person.getModifyTimestamp());
  }

  private void assertPersonResult(Person person) {
    assertNotNull(person);
    assertEquals(DN, person.getDn());
    assertEquals(TEST, person.getCn());
    assertEquals(TEST, person.getVgrId());
    assertEquals(TEST, person.getHsaPersonIdentityNumber());
    assertEquals(TEST, person.getGivenName());
    assertEquals(TEST, person.getSn());
    assertEquals(TEST, person.getHsaMiddleName());
    assertEquals(TEST, person.getInitials());
    assertEquals(TEST, person.getHsaNickName());
    assertEquals(TEST, person.getFullName());
    assertEquals("[" + TEST + "]", person.getVgrStrukturPersonDN().toString());
    assertEquals("[" + TEST + "]", person.getVgrOrgRel().toString());
    assertEquals("[" + TEST + "]", person.getVgrAnstform().toString());
    assertEquals(TEST, person.getHsaIdentity());
    assertEquals(TEST, person.getMail());
    assertEquals("[Translated " + TEST + "]", person.getHsaSpecialityName().toString());
    assertEquals("[" + TEST + "]", person.getHsaSpecialityCode().toString());
    assertEquals("[" + TEST + "]", person.getVgrAO3kod().toString());
    assertEquals("[" + TEST + "]", person.getVgrAnsvarsnummer().toString());
    assertEquals("[" + TEST + "]", person.getHsaLanguageKnowledgeCode().toString());
    assertEquals("[Translated " + TEST + "]", person.getHsaLanguageKnowledgeText().toString());
    assertEquals(TEST, person.getHsaTitle());
    assertEquals(TEST, person.getHsaPersonPrescriptionCode());
    assertNotNull(person.getEmploymentPeriod());
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
