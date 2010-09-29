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
 */
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.domain.Person;

public class PersonMapperTest {
  private static final String TEST = "Test";
  private static final String TEST2 = "Test2";
  private static final String TEST_DN = "cn=abc,ou=def";
  private static final String TEST_TIME = "1-4#08:00#17:00";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";

  private final PersonMapper mapper = new PersonMapper();
  private DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();

  @Test
  public void mapPopulatedContextReturnPopulatedPerson() {
    addPersonAttributes();

    mapper.mapFromContext(dirContextOperations);

    Person person = mapper.getFirstPerson();
    assertEquals("cn", TEST, person.getCn());
    assertEquals("region name", TEST, person.getVgrId());
    assertEquals("person identity number", TEST, person.getHsaPersonIdentityNumber());
    assertEquals("givenName", TEST, person.getGivenName());
    assertEquals("sn", TEST, person.getSn());
    assertEquals("middleName", TEST, person.getHsaMiddleName());
    assertEquals("nickname", TEST, person.getHsaNickName());
    assertEquals("full name", TEST, person.getFullName());
    assertEquals("dn", TEST_DN, person.getDn());
    assertEquals("org rel", EXPECTED_LIST_RESULT, person.getVgrOrgRel().toString());
    assertEquals("hsaIdentity", TEST, person.getHsaIdentity());
    assertEquals("mail", TEST, person.getMail());
    assertEquals("speciality name", EXPECTED_LIST_RESULT, person.getHsaSpecialityName().toString());
    assertEquals("speciality code", EXPECTED_LIST_RESULT, person.getHsaSpecialityCode().toString());
    assertEquals("knowledge code", EXPECTED_LIST_RESULT, person.getHsaLanguageKnowledgeCode().toString());
    assertEquals("knowledge test", EXPECTED_LIST_RESULT, person.getHsaLanguageKnowledgeText().toString());
    assertEquals("title", "Leg. psykolog, IT-Arkitekt", person.getHsaTitle());
    assertEquals("prescription code", TEST, person.getHsaPersonPrescriptionCode());
    assertNotNull("employment period", person.getEmploymentPeriod());
    assertFalse("profile image present", person.isProfileImagePresent());
  }

  @Test
  public void testReconsituteProfileImagePresent() {
    addPersonAttributes();
    dirContextOperations.addAttributeValue("jpegPhoto", TEST);

    mapper.mapFromContext(dirContextOperations);

    Person person = mapper.getFirstPerson();
    assertTrue(person.isProfileImagePresent());
  }

  @Test
  public void mapPopulatedEmploymentAttributesReturnPersonWithEmployment() {
    addPersonAttributes();
    addEmploymentAttributes();

    mapper.mapFromContext(dirContextOperations);

    addOtherEmploymentAttributes();
    mapper.mapFromContext(dirContextOperations);

    Person person = mapper.getFirstPerson();
    assertNotNull(person.getEmployments());
    assertEquals(2, person.getEmployments().size());

    assertTrue(person.getEmployments().get(0).isPrimaryEmployment());
  }

  private void addEmploymentAttributes() {
    dirContextOperations.addAttributeValue("cn", TEST);
    dirContextOperations.addAttributeValue("ou", TEST);
    dirContextOperations.addAttributeValue("hsaIdentity", TEST);
    dirContextOperations.addAttributeValue("street", TEST);
    dirContextOperations.addAttributeValue("hsaInternalAddress", TEST);
    dirContextOperations.addAttributeValue("postalAddress", TEST);
    dirContextOperations.addAttributeValue("hsaDeliveryAddress", TEST);
    dirContextOperations.addAttributeValue("hsaInvoiceAddress", TEST);
    dirContextOperations.addAttributeValue("hsaConsigneeAddress", TEST);
    dirContextOperations.addAttributeValue("facsimileTelephoneNumber", TEST);
    dirContextOperations.addAttributeValue("labeledUri", TEST);
    dirContextOperations.addAttributeValue("title", TEST);
    dirContextOperations.addAttributeValue("description", TEST);
    dirContextOperations.addAttributeValue("hsaSwitchboardNumber", TEST);
    dirContextOperations.addAttributeValue("company", TEST);
    dirContextOperations.addAttributeValue("telephoneNumber", TEST);
    dirContextOperations.addAttributeValue("hsaTelephoneNumber", TEST);
    dirContextOperations.addAttributeValue("mobile", TEST);
    dirContextOperations.addAttributeValue("hsaInternalPagerNumber", TEST);
    dirContextOperations.addAttributeValue("pager", TEST);
    dirContextOperations.addAttributeValue("hsaTextPhoneNumber", TEST);
    dirContextOperations.addAttributeValue("whenCreated", "20091109104650.0Z");
    dirContextOperations.addAttributeValue("telephoneHours", TEST_TIME);
    dirContextOperations.addAttributeValue("distinguishedName", TEST_DN);
    dirContextOperations.addAttributeValue("postalCode", TEST);
  }

  private void addOtherEmploymentAttributes() {
    dirContextOperations.addAttributeValue("cn", TEST2);
    dirContextOperations.addAttributeValue("ou", TEST2);
    dirContextOperations.addAttributeValue("hsaIdentity", TEST2);
    dirContextOperations.addAttributeValue("street", TEST2);
    dirContextOperations.addAttributeValue("hsaInternalAddress", TEST2);
    dirContextOperations.addAttributeValue("postalAddress", TEST2);
    dirContextOperations.addAttributeValue("hsaDeliveryAddress", TEST2);
    dirContextOperations.addAttributeValue("hsaInvoiceAddress", TEST2);
    dirContextOperations.addAttributeValue("hsaConsigneeAddress", TEST2);
    dirContextOperations.addAttributeValue("facsimileTelephoneNumber", TEST2);
    dirContextOperations.addAttributeValue("labeledUri", TEST2);
    dirContextOperations.addAttributeValue("title", TEST2);
    dirContextOperations.addAttributeValue("description", TEST2);
    dirContextOperations.addAttributeValue("hsaSwitchboardNumber", TEST2);
    dirContextOperations.addAttributeValue("company", TEST2);
    dirContextOperations.addAttributeValue("telephoneNumber", TEST2);
    dirContextOperations.addAttributeValue("hsaTelephoneNumber", TEST2);
    dirContextOperations.addAttributeValue("mobile", TEST2);
    dirContextOperations.addAttributeValue("hsaInternalPagerNumber", TEST2);
    dirContextOperations.addAttributeValue("pager", TEST2);
    dirContextOperations.addAttributeValue("hsaTextPhoneNumber", TEST2);
    dirContextOperations.addAttributeValue("whenCreated", "20091109104650.0Z");
    dirContextOperations.addAttributeValue("whenChanged", "20100224082302.0Z");
    dirContextOperations.addAttributeValue("telephoneHours", TEST_TIME);
    dirContextOperations.addAttributeValue("distinguishedName", TEST_DN);
    dirContextOperations.addAttributeValue("postalCode", TEST2);
    dirContextOperations.addAttributeValue("mainNode", "Ja");
  }

  private void addPersonAttributes() {
    dirContextOperations.setDn(DistinguishedName.immutableDistinguishedName(TEST_DN));
    dirContextOperations.addAttributeValue("distinguishedName", TEST_DN);
    dirContextOperations.addAttributeValue("cn", TEST);
    dirContextOperations.addAttributeValue("regionName", TEST);
    dirContextOperations.addAttributeValue("personalIdentityNumber", TEST);
    dirContextOperations.addAttributeValue("givenName", TEST);
    dirContextOperations.addAttributeValue("sn", TEST);
    dirContextOperations.addAttributeValue("middleName", TEST);
    dirContextOperations.addAttributeValue("nickname", TEST);
    dirContextOperations.addAttributeValue("fullName", TEST);
    dirContextOperations.addAttributeValue("hsaIdentity", TEST);
    dirContextOperations.addAttributeValue("mail", TEST);
    dirContextOperations.addAttributeValue("specialityName", TEST);
    dirContextOperations.addAttributeValue("hsaSpecialityCode", TEST);
    dirContextOperations.addAttributeValue("hsaLanguageKnowledgeCode", TEST);
    dirContextOperations.addAttributeValue("hsaLanguageKnowledgeText", TEST);
    dirContextOperations.addAttributeValue("hsaTitle", "Leg. psykolog$IT-Arkitekt");
    dirContextOperations.addAttributeValue("hsaPersonPrescriptionCode", TEST);
    dirContextOperations.addAttributeValue("hsaStartDate", TEST);
    dirContextOperations.addAttributeValue("hsaEndDate", TEST);
  }
}
