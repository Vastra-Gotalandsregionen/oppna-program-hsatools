package se.vgregion.kivtools.hriv.presentation;

import java.util.Comparator;


import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.util.StringUtil;

public class UnitSearchStrategyLTHImpl extends AbstractUnitSearchStrategy implements UnitSearchStrategy {
  @Override
  public SikSearchResultList<Unit> performSearch(UnitSearchSimpleForm theForm, Comparator<Unit> sortOrder, int effectiveMaxSearchResult, SearchService searchService, boolean onlyPublicUnits)
      throws KivException {
    SikSearchResultList<Unit> list;
    Unit u = this.mapSearchCriteriaToUnit(theForm);
    list = searchService.searchAdvancedUnits(u, effectiveMaxSearchResult, sortOrder, onlyPublicUnits);

    // No hits with complete criterions. Try again but with cleaned unit name this time
    if (list.size() == 0 && !StringUtil.isEmpty(theForm.getUnitName())) {
      u.setName(this.cleanUnitName(theForm.getUnitName()));
      list = searchService.searchAdvancedUnits(u, effectiveMaxSearchResult, sortOrder, onlyPublicUnits);
    }

    // Still no hits. Try again but with only the cleaned unit name this time if the user forgot to remove any care type or municipality selection.
    if (list.size() == 0 && this.lessSpecifiedSearchPossible(theForm)) {
      u = new Unit();
      u.setName(this.cleanUnitName(theForm.getUnitName()));
      list = searchService.searchAdvancedUnits(u, effectiveMaxSearchResult, sortOrder, onlyPublicUnits);
    }
    return list;
  }

  /**
   * Checks if it's possible to perform a less specified search by analysing the search criterions.
   * 
   * @param theForm The form-instance containing the search criterions.
   * @return True if a less specified search is possible, otherwise false.
   */
  private boolean lessSpecifiedSearchPossible(UnitSearchSimpleForm theForm) {
    boolean result = false;

    // A less specified search is possible

    // If any of healthcare type and municipality was filled
    result |= !StringUtil.isEmpty(theForm.getHealthcareType());
    result |= !StringUtil.isEmpty(theForm.getMunicipality());

    // And unit name was filled
    result &= !StringUtil.isEmpty(theForm.getUnitName());

    return result;
  }
}
