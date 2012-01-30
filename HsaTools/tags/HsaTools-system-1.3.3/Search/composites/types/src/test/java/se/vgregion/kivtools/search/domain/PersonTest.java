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

package se.vgregion.kivtools.search.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.PojoTester;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

public class PersonTest {
  private Person person;

  @Before
  public void setUp() throws Exception {
    this.person = new Person();
  }

  @Test
  public void testInstantiation() {
    Person person = new Person();
    assertNotNull(person);
  }

  @Test
  public void testBasicProperties() {
    PojoTester.testProperty(this.person, "dn", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "cn", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "vgrId", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "hsaPersonIdentityNumber", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "givenName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "sn", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "hsaMiddleName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "initials", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "hsaNickName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "fullName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "hsaIdentity", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "mail", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "hsaTitle", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "hsaPersonPrescriptionCode", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "vgrStrukturPersonDN", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "vgrOrgRel", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "vgrAnstform", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "hsaSpecialityName", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "hsaSpecialityCode", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "vgrAO3kod", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "vgrAnsvarsnummer", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "hsaLanguageKnowledgeCode", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "hsaLanguageKnowledgeText", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(this.person, "employmentPeriod", TimeInterval.class, null, TimeInterval.startingFrom(TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), Duration.days(30)),
        TimeInterval.preceding(TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), Duration.days(21)));
    PojoTester.testProperty(this.person, "employments", List.class, null, Arrays.asList(new Employment()), Arrays.asList(new Employment(), new Employment()));
    PojoTester.testProperty(this.person, "profileImagePresent", boolean.class, false, true, false);
    PojoTester.testProperty(this.person, "createTimestamp", TimePoint.class, null, TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), TimePoint.atMidnight(2010, 03, 18, TimeZone.getDefault()));
    PojoTester.testProperty(this.person, "modifyTimestamp", TimePoint.class, null, TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), TimePoint.atMidnight(2010, 03, 18, TimeZone.getDefault()));
    PojoTester.testProperty(this.person, "paTitleName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(this.person, "hsaAltText", String.class, null, "Test", "Test2");
  }

  @Test
  public void testEmploymentPeriod() {
    assertNull(this.person.getEmploymentPeriod());
    this.person.setEmploymentPeriod(TimePoint.atMidnightGMT(2009, 11, 3), TimePoint.atMidnightGMT(2009, 12, 24));
    assertEquals(TimeInterval.closed(TimePoint.atMidnightGMT(2009, 11, 3), TimePoint.atMidnightGMT(2009, 12, 24)), this.person.getEmploymentPeriod());
  }

  @Test
  public void testGetTelephoneNumberOfFirstEmployment() {
    assertEquals("", this.person.getTelephoneNumberOfFirstEmployment());

    Employment employment = new Employment();
    this.person.setEmployments(Arrays.asList(employment));
    assertEquals("", this.person.getTelephoneNumberOfFirstEmployment());

    PhoneNumber phoneNumber = PhoneNumber.createPhoneNumber("031-123456");
    employment.addHsaTelephoneNumbers(Arrays.asList(phoneNumber));
    assertEquals("031-123456", this.person.getTelephoneNumberOfFirstEmployment());
  }

  @Test
  public void testGetMobileNumberOfFirstEmployment() {
    assertEquals("", this.person.getMobileNumberOfFirstEmployment());

    Employment employment = new Employment();
    this.person.setEmployments(Arrays.asList(employment));
    assertEquals("", this.person.getMobileNumberOfFirstEmployment());

    PhoneNumber phoneNumber = PhoneNumber.createPhoneNumber("070-123456");
    employment.setMobileTelephoneNumber(phoneNumber);
    assertEquals("070-123456", this.person.getMobileNumberOfFirstEmployment());
  }
}
