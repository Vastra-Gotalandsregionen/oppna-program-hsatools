package se.vgregion.kivtools.search.svc.impl.hak;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class PersonEmailMapperTest {
  private PersonEmailMapper personEmailMapper;
  private DirContextOperationsMock dirContextOperations;

  @Before
  public void setUp() throws Exception {
    personEmailMapper = new PersonEmailMapper();
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    PersonEmailMapper personEmailMapper = new PersonEmailMapper();
    assertNotNull(personEmailMapper);
  }

  @Test
  public void testNoEmailFound() {
    String email = (String) personEmailMapper.mapFromContext(dirContextOperations);
    assertNull(email);
  }

  @Test
  public void testEmailFound() {
    dirContextOperations.addAttributeValue("mail", "anders.ask@lthalland.se");
    String email = (String) personEmailMapper.mapFromContext(dirContextOperations);
    assertEquals("anders.ask@lthalland.se", email);
  }
}
