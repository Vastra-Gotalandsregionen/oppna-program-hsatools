package se.vgregion.kivtools.search.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.PojoTester;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.domain.values.ZipCode;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

public class EmploymentTest {
  private Employment employment;

  @Before
  public void setUp() throws Exception {
    employment = new Employment();
  }

  @Test
  public void testInstantiation() {
    Employment employment = new Employment();
    assertNotNull(employment);
  }

  @Test
  public void testBasicProperties() {
    PojoTester.testProperty(employment, "cn", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "ou", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "hsaPersonIdentityNumber", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "vgrOrgRel", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "name", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "modifyersName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "vgrAnsvarsnummer", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "vgrAnstform", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "title", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "vgrFormansgrupp", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "vgrAO3kod", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "labeledUri", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "locality", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "position", String.class, null, "Test", "Test2");
    PojoTester.testProperty(employment, "dn", DN.class, null, DN.createDNFromString("cn=abc,ou=test"), DN.createDNFromString("cn=def,ou=test2"));
    PojoTester.testProperty(employment, "vgrStrukturPerson", DN.class, null, DN.createDNFromString("cn=abc,ou=test"), DN.createDNFromString("cn=def,ou=test2"));
    PojoTester.testProperty(employment, "hsaSedfInvoiceAddress", Address.class, null, createAddress("Storgatan 1", "41263", "Göteborg"), createAddress("Lilla stigen 4", "40210", "Uddevalla"));
    PojoTester.testProperty(employment, "hsaStreetAddress", Address.class, null, createAddress("Storgatan 1", "41263", "Göteborg"), createAddress("Lilla stigen 4", "40210", "Uddevalla"));
    PojoTester.testProperty(employment, "hsaInternalAddress", Address.class, null, createAddress("Storgatan 1", "41263", "Göteborg"), createAddress("Lilla stigen 4", "40210", "Uddevalla"));
    PojoTester.testProperty(employment, "hsaPostalAddress", Address.class, null, createAddress("Storgatan 1", "41263", "Göteborg"), createAddress("Lilla stigen 4", "40210", "Uddevalla"));
    PojoTester.testProperty(employment, "hsaSedfDeliveryAddress", Address.class, null, createAddress("Storgatan 1", "41263", "Göteborg"), createAddress("Lilla stigen 4", "40210", "Uddevalla"));
    PojoTester.testProperty(employment, "facsimileTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber("031-123456"), PhoneNumber.createPhoneNumber("0303-10000"));
    PojoTester.testProperty(employment, "hsaPublicTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber("031-123456"), PhoneNumber.createPhoneNumber("0303-10000"));
    PojoTester.testProperty(employment, "mobileTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber("031-123456"), PhoneNumber.createPhoneNumber("0303-10000"));
    PojoTester.testProperty(employment, "hsaInternalPagerNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber("031-123456"), PhoneNumber.createPhoneNumber("0303-10000"));
    PojoTester.testProperty(employment, "pagerTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber("031-123456"), PhoneNumber.createPhoneNumber("0303-10000"));
    PojoTester.testProperty(employment, "hsaTextPhoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber("031-123456"), PhoneNumber.createPhoneNumber("0303-10000"));
    PojoTester.testProperty(employment, "hsaSedfSwitchboardTelephoneNo", PhoneNumber.class, null, PhoneNumber.createPhoneNumber("031-123456"), PhoneNumber.createPhoneNumber("0303-10000"));
    PojoTester.testProperty(employment, "zipCode", ZipCode.class, null, new ZipCode("12345"), new ZipCode("54321"));
    PojoTester.testProperty(employment, "modifyTimestamp", TimePoint.class, null, TimePoint.at(2009, 11, 3, 12, 1, 2, TimeZone.getDefault()), TimePoint.at(2008, 7, 8, 23, 14, 27, TimeZone
        .getDefault()));
    PojoTester.testProperty(employment, "employmentPeriod", TimeInterval.class, null, TimeInterval.startingFrom(TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), Duration.days(30)),
        TimeInterval.preceding(TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), Duration.days(21)));
    PojoTester.testProperty(employment, "description", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(employment, "hsaTelephoneNumbers", List.class, new ArrayList<PhoneNumber>(), Arrays.asList(PhoneNumber.createPhoneNumber("031-123456")), Arrays.asList(PhoneNumber
        .createPhoneNumber("0303-10000"), PhoneNumber.createPhoneNumber("031-123456")));
    PojoTester.testProperty(employment, "hsaTelephoneTime", List.class, null, WeekdayTime.createWeekdayTimeList(Arrays.asList("1-5#8:30#10:00")), WeekdayTime.createWeekdayTimeList(Arrays
        .asList("1#18:00#18:30")));
  }

  @Test
  public void testHsaTelephoneNumber() {
    assertNull(employment.getHsaTelephoneNumber());
    employment.setHsaTelephoneNumbers(PhoneNumber.createPhoneNumberList(Arrays.asList("031-123456", "0303-10000")));
    assertEquals("031-123456", employment.getHsaTelephoneNumber().getPhoneNumber());
  }

  @Test
  public void testHsaTelephoneNumbersCSVString() {
    assertEquals("", employment.getHsaTelephoneNumbersCSVString());
    employment.setHsaTelephoneNumbers(PhoneNumber.createPhoneNumberList(Arrays.asList("031-123456", "0303-10000")));
    assertEquals("031 - 12 34 56 , 0303 - 100 00", employment.getHsaTelephoneNumbersCSVString());
  }

  @Test
  public void testEmploymentPeriod() {
    assertNull(employment.getEmploymentPeriod());
    employment.setEmploymentPeriod(TimePoint.atMidnightGMT(2009, 11, 3), TimePoint.atMidnightGMT(2009, 12, 24));
    assertEquals(TimeInterval.open(TimePoint.atMidnightGMT(2009, 11, 3), TimePoint.atMidnightGMT(2009, 12, 24)), employment.getEmploymentPeriod());
  }

  private Address createAddress(String street, String zipcode, String city) {
    Address address = new Address();
    address.setStreet(street);
    address.setZipCode(new ZipCode(zipcode));
    address.setCity(city);
    return address;
  }
}
