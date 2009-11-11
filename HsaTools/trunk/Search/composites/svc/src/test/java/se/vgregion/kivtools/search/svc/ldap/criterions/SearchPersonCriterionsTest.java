package se.vgregion.kivtools.search.svc.ldap.criterions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SearchPersonCriterionsTest {
  private SearchPersonCriterions searchPersonCriterions;

  @Before
  public void setUp() throws Exception {
    searchPersonCriterions = new SearchPersonCriterions();
  }

  @Test
  public void testIsEmpty() {
    assertTrue(searchPersonCriterions.isEmpty());

    searchPersonCriterions.setAdministration("test");
    assertFalse(searchPersonCriterions.isEmpty());
  }
}
