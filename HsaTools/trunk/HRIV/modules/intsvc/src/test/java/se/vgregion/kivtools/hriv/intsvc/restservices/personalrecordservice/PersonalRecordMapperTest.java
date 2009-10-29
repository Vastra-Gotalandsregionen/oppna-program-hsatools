package se.vgregion.kivtools.hriv.intsvc.restservices.personalrecordservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class PersonalRecordMapperTest {

  private PersonalRecordMapper personalRecordMapper;
  private DirContextOperationsMock dirContextOperationsMock;

  @Before
  public void setup() {
    personalRecordMapper = new PersonalRecordMapper();
    dirContextOperationsMock = new DirContextOperationsMock();
    setAttributeMocks();
  }

  @Test
  public void testMapFromContext() {
    PersonalRecord personRecord = (PersonalRecord) personalRecordMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(personRecord);
    assertEquals("David", personRecord.getFirstName());
    assertEquals("Bennehult", personRecord.getLastName());
    assertEquals("David Bennehult", personRecord.getFullName());
  }

  private void setAttributeMocks() {
    dirContextOperationsMock.addAttributeValue("givenName", "David");
    dirContextOperationsMock.addAttributeValue("sn", "Bennehult");
    dirContextOperationsMock.addAttributeValue("fullName", "David Bennehult");
  }
}
