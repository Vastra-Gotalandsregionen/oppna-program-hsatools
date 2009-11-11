package se.vgregion.kivtools.search.svc.ldap.criterions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SearchUnitCriterionsTest {

  private SearchUnitCriterions searchUnitCriterions;

  @Before
  public void setUp() throws Exception {
    searchUnitCriterions = new SearchUnitCriterions();
  }

  @Test
  public void testIsEmpty() {
    assertTrue(searchUnitCriterions.isEmpty());

    searchUnitCriterions.setAdministrationName("test");
    assertFalse(searchUnitCriterions.isEmpty());
  }
}
