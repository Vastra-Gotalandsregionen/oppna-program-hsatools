package se.vgregion.kivtools.hriv.presentation;

import java.util.Comparator;


import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.util.StringUtil;

public class UnitSearchStrategyVGRImpl extends AbstractUnitSearchStrategy implements UnitSearchStrategy {
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

    return list;
  }
}
