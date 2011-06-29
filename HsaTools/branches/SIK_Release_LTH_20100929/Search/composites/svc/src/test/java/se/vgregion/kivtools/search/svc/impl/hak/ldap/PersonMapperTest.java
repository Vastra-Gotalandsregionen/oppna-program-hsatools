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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
  private final DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();

  @Test
  public void mapPopulatedContextReturnPopulatedPerson() {
    this.addPersonAttributes();

    this.mapper.mapFromContext(this.dirContextOperations);

    Person person = this.mapper.getFirstPerson();
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
    assertEquals("paTitleName", "IT-Ansvarig", person.getPaTitleName());
    assertEquals("altText", "alternativ text", person.getHsaAltText());
    assertEquals("prescription code", TEST, person.getHsaPersonPrescriptionCode());
    assertNotNull("employment period", person.getEmploymentPeriod());
    assertFalse("profile image present", person.isProfileImagePresent());
  }

  @Test
  public void testReconsituteProfileImagePresent() {
    this.addPersonAttributes();
    this.dirContextOperations.addAttributeValue("jpegPhoto", TEST);

    this.mapper.mapFromContext(this.dirContextOperations);

    Person person = this.mapper.getFirstPerson();
    assertTrue(person.isProfileImagePresent());
  }

  @Test
  public void mapPopulatedEmploymentAttributesReturnPersonWithEmployment() {
    this.addPersonAttributes();
    this.addEmploymentAttributes();

    this.mapper.mapFromContext(this.dirContextOperations);

    this.addOtherEmploymentAttributes();
    this.mapper.mapFromContext(this.dirContextOperations);

    Person person = this.mapper.getFirstPerson();
    assertNotNull(person.getEmployments());
    assertEquals(2, person.getEmployments().size());

    assertTrue(person.getEmployments().get(0).isPrimaryEmployment());
  }

  private void addEmploymentAttributes() {
    this.dirContextOperations.addAttributeValue("cn", TEST);
    this.dirContextOperations.addAttributeValue("ou", TEST);
    this.dirContextOperations.addAttributeValue("hsaIdentity", TEST);
    this.dirContextOperations.addAttributeValue("street", TEST);
    this.dirContextOperations.addAttributeValue("hsaInternalAddress", TEST);
    this.dirContextOperations.addAttributeValue("postalAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaDeliveryAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaInvoiceAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaConsigneeAddress", TEST);
    this.dirContextOperations.addAttributeValue("facsimileTelephoneNumber", TEST);
    this.dirContextOperations.addAttributeValue("labeledUri", TEST);
    this.dirContextOperations.addAttributeValue("title", TEST);
    this.dirContextOperations.addAttributeValue("description", TEST);
    this.dirContextOperations.addAttributeValue("hsaSwitchboardNumber", TEST);
    this.dirContextOperations.addAttributeValue("company", TEST);
    this.dirContextOperations.addAttributeValue("telephoneNumber", TEST);
    this.dirContextOperations.addAttributeValue("hsaTelephoneNumber", TEST);
    this.dirContextOperations.addAttributeValue("mobile", TEST);
    this.dirContextOperations.addAttributeValue("hsaInternalPagerNumber", TEST);
    this.dirContextOperations.addAttributeValue("pager", TEST);
    this.dirContextOperations.addAttributeValue("hsaTextPhoneNumber", TEST);
    this.dirContextOperations.addAttributeValue("whenCreated", "20091109104650.0Z");
    this.dirContextOperations.addAttributeValue("telephoneHours", TEST_TIME);
    this.dirContextOperations.addAttributeValue("distinguishedName", TEST_DN);
    this.dirContextOperations.addAttributeValue("postalCode", TEST);
  }

  private void addOtherEmploymentAttributes() {
    this.dirContextOperations.addAttributeValue("cn", TEST2);
    this.dirContextOperations.addAttributeValue("ou", TEST2);
    this.dirContextOperations.addAttributeValue("hsaIdentity", TEST2);
    this.dirContextOperations.addAttributeValue("street", TEST2);
    this.dirContextOperations.addAttributeValue("hsaInternalAddress", TEST2);
    this.dirContextOperations.addAttributeValue("postalAddress", TEST2);
    this.dirContextOperations.addAttributeValue("hsaDeliveryAddress", TEST2);
    this.dirContextOperations.addAttributeValue("hsaInvoiceAddress", TEST2);
    this.dirContextOperations.addAttributeValue("hsaConsigneeAddress", TEST2);
    this.dirContextOperations.addAttributeValue("facsimileTelephoneNumber", TEST2);
    this.dirContextOperations.addAttributeValue("labeledUri", TEST2);
    this.dirContextOperations.addAttributeValue("title", TEST2);
    this.dirContextOperations.addAttributeValue("description", TEST2);
    this.dirContextOperations.addAttributeValue("hsaSwitchboardNumber", TEST2);
    this.dirContextOperations.addAttributeValue("company", TEST2);
    this.dirContextOperations.addAttributeValue("telephoneNumber", TEST2);
    this.dirContextOperations.addAttributeValue("hsaTelephoneNumber", TEST2);
    this.dirContextOperations.addAttributeValue("mobile", TEST2);
    this.dirContextOperations.addAttributeValue("hsaInternalPagerNumber", TEST2);
    this.dirContextOperations.addAttributeValue("pager", TEST2);
    this.dirContextOperations.addAttributeValue("hsaTextPhoneNumber", TEST2);
    this.dirContextOperations.addAttributeValue("whenCreated", "20091109104650.0Z");
    this.dirContextOperations.addAttributeValue("whenChanged", "20100224082302.0Z");
    this.dirContextOperations.addAttributeValue("telephoneHours", TEST_TIME);
    this.dirContextOperations.addAttributeValue("distinguishedName", TEST_DN);
    this.dirContextOperations.addAttributeValue("postalCode", TEST2);
    this.dirContextOperations.addAttributeValue("mainNode", "Ja");
  }

  private void addPersonAttributes() {
    this.dirContextOperations.setDn(DistinguishedName.immutableDistinguishedName(TEST_DN));
    this.dirContextOperations.addAttributeValue("distinguishedName", TEST_DN);
    this.dirContextOperations.addAttributeValue("cn", TEST);
    this.dirContextOperations.addAttributeValue("regionName", TEST);
    this.dirContextOperations.addAttributeValue("personalIdentityNumber", TEST);
    this.dirContextOperations.addAttributeValue("givenName", TEST);
    this.dirContextOperations.addAttributeValue("sn", TEST);
    this.dirContextOperations.addAttributeValue("middleName", TEST);
    this.dirContextOperations.addAttributeValue("nickname", TEST);
    this.dirContextOperations.addAttributeValue("fullName", TEST);
    this.dirContextOperations.addAttributeValue("hsaIdentity", TEST);
    this.dirContextOperations.addAttributeValue("mail", TEST);
    this.dirContextOperations.addAttributeValue("specialityName", TEST);
    this.dirContextOperations.addAttributeValue("hsaSpecialityCode", TEST);
    this.dirContextOperations.addAttributeValue("hsaLanguageKnowledgeCode", TEST);
    this.dirContextOperations.addAttributeValue("hsaLanguageKnowledgeText", TEST);
    this.dirContextOperations.addAttributeValue("hsaTitle", "Leg. psykolog$IT-Arkitekt");
    this.dirContextOperations.addAttributeValue("hsaPersonPrescriptionCode", TEST);
    this.dirContextOperations.addAttributeValue("hsaStartDate", TEST);
    this.dirContextOperations.addAttributeValue("hsaEndDate", TEST);
    this.dirContextOperations.addAttributeValue("paTitleName", "IT-Ansvarig");
    this.dirContextOperations.addAttributeValue("hsaAltText", "alternativ text");
  }
}
