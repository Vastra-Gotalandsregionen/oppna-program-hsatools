package se.vgregion.kivtools.search.svc.ldap.criterions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterion.SearchCriterion;

public class SearchPersonCriterionTest {

  private SearchPersonCriterion searchPersonCriterion;

  @Before
  public void setUp() throws Exception {
    searchPersonCriterion = new SearchPersonCriterion();
    
  }

  @Test
  public void testAddSearchCriterionValue() {
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.GIVEN_NAME, "givenName");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.SURNAME, "");
    
    assertEquals(1, searchPersonCriterion.getAllSearchCriterions().size());
  }

}
