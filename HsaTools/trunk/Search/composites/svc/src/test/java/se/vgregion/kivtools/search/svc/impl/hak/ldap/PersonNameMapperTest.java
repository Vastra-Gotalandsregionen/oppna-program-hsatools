package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.svc.PersonNameCache;

public class PersonNameMapperTest {
  private PersonNameCache personNameCache;
  private PersonNameMapper personNameMapper;
  private DirContextOperationsMock dirContextOperations;

  @Before
  public void setUp() throws Exception {
    personNameCache = new PersonNameCache();
    personNameMapper = new PersonNameMapper(personNameCache);
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    PersonNameMapper personNameMapper = new PersonNameMapper(personNameCache);
    assertNotNull(personNameMapper);
  }

  @Test
  public void testMapFromContext() {
    dirContextOperations.addAttributeValue("givenName", "Nina");
    dirContextOperations.addAttributeValue("sn", "Kanin");
    personNameMapper.mapFromContext(dirContextOperations);
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("nina", "");
    assertEquals(1, matchingGivenNames.size());
    assertEquals("Nina", matchingGivenNames.get(0));
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("", "kanin");
    assertEquals(1, matchingSurnames.size());
    assertEquals("Kanin", matchingSurnames.get(0));
  }
}
