package se.vgregion.kivtools.search.domain;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.PojoTester;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

public class PersonTest {
  private Person person;

  @Before
  public void setUp() throws Exception {
    person = new Person();
  }

  @Test
  public void testInstantiation() {
    Person person = new Person();
    assertNotNull(person);
  }

  @Test
  public void testBasicProperties() {
    PojoTester.testProperty(person, "dn", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "cn", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "vgrId", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "hsaPersonIdentityNumber", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "givenName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "sn", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "hsaMiddleName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "initials", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "hsaNickName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "fullName", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "hsaIdentity", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "mail", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "hsaTitle", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "hsaPersonPrescriptionCode", String.class, null, "Test", "Test2");
    PojoTester.testProperty(person, "vgrStrukturPersonDN", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "vgrOrgRel", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "vgrAnstform", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "hsaSpecialityName", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "hsaSpecialityCode", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "vgrAO3kod", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "vgrAnsvarsnummer", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "hsaLanguageKnowledgeCode", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "hsaLanguageKnowledgeText", List.class, null, Arrays.asList("Test"), Arrays.asList("Test1", "Test2"));
    PojoTester.testProperty(person, "employmentPeriod", TimeInterval.class, null, TimeInterval.startingFrom(TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), Duration.days(30)), TimeInterval
        .preceding(TimePoint.atMidnight(2009, 11, 3, TimeZone.getDefault()), Duration.days(21)));
    PojoTester.testProperty(person, "employments", List.class, null, Arrays.asList(new Employment()), Arrays.asList(new Employment(), new Employment()));
  }

  @Test
  public void testEmploymentPeriod() {
    assertNull(person.getEmploymentPeriod());
    person.setEmploymentPeriod(TimePoint.atMidnightGMT(2009, 11, 3), TimePoint.atMidnightGMT(2009, 12, 24));
    assertEquals(TimeInterval.closed(TimePoint.atMidnightGMT(2009, 11, 3), TimePoint.atMidnightGMT(2009, 12, 24)), person.getEmploymentPeriod());
  }
}
