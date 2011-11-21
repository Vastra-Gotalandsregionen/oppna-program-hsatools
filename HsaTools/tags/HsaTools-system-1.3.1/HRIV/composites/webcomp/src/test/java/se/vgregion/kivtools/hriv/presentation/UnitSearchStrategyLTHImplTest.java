package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;

public class UnitSearchStrategyLTHImplTest {
  private final UnitSearchStrategyLTHImpl strategy = new UnitSearchStrategyLTHImpl();
  private final SearchServiceMock searchService = new SearchServiceMock();
  private final UnitSearchSimpleForm form = new UnitSearchSimpleForm();

  @Before
  public void setUp() {
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new ResettingHealthcareTypeConditionHelper();
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");
  }

  @After
  public void tearDown() {
    new ResettingHealthcareTypeConditionHelper();
  }

  @Test
  public void searchWithoutUnitNameCallsSearchServiceOnceEvenIfNoUnitsAreFound() throws Exception {
    this.form.setMunicipality("Kungälv");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    SikSearchResultList<Unit> result = this.strategy.performSearch(this.form, new UnitNameComparator(), 0, this.searchService, false);
    assertEquals("search service call count", 1, this.searchService.searchAdvancedUnitsCallCount);
    assertEquals("result count", 0, result.size());
  }

  @Test
  public void searchWithUnitNameCallsSearchServiceAnAdditionalTimeWithCleanedUnitNameIfNoUnitsAreFoundInFirstSearch() throws Exception {
    this.form.setUnitName("Vårdcentral, Kungälv");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.strategy.performSearch(this.form, new UnitNameComparator(), 0, this.searchService, false);
    assertEquals("search service call count", 2, this.searchService.searchAdvancedUnitsCallCount);
    assertEquals("search unit name", "Vårdcentral", this.searchService.unitCriterion.getName());
  }

  @Test
  public void searchWithUnitNameCallsSearchServiceAnAdditionalTimeWithOnlyCleanedUnitNameIfNoUnitsAreFoundInFirstOrSecondSearch() throws Exception {
    this.form.setUnitName("Vårdcentral, Kungälv");
    this.form.setMunicipality("Kungälv");
    this.form.setHealthcareType("18");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.strategy.performSearch(this.form, new UnitNameComparator(), 0, this.searchService, false);
    assertEquals("search service call count", 3, this.searchService.searchAdvancedUnitsCallCount);
    assertEquals("search unit name", "Vårdcentral", this.searchService.unitCriterion.getName());
    assertNull("search municipality", this.searchService.unitCriterion.getHsaMunicipalityName());
    assertEquals("search healthcare type count", 0, this.searchService.unitCriterion.getHealthcareTypes().size());
  }

  @Test
  public void selectedHealthcareTypeIsAddedToCriterionUnit() throws Exception {
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    SikSearchResultList<Unit> searchResult = new SikSearchResultList<Unit>();
    searchResult.add(unit);
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    // 18 == Vårdcentral
    this.form.setHealthcareType("18");
    this.strategy.performSearch(this.form, new UnitNameComparator(), 0, this.searchService, false);
    assertEquals("search service call count", 1, this.searchService.searchAdvancedUnitsCallCount);
    assertEquals("search unit healthcare type", "Vårdcentral", this.searchService.unitCriterion.getHealthcareTypes().get(0).getDisplayName());
  }

  private static final class ResettingHealthcareTypeConditionHelper extends HealthcareTypeConditionHelper {
    {
      resetInternalCache();
    }
  }
}
