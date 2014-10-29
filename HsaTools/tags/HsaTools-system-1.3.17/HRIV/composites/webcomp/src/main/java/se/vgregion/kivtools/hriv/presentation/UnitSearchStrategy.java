package se.vgregion.kivtools.hriv.presentation;

import java.util.Comparator;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;

/**
 * Strategy for how to perform the unit search.
 */
public interface UnitSearchStrategy {
  /**
   * Perform the search based on the provided paramters in the search form.
   * 
   * @param theForm The search form containing the search parameters.
   * @param sortOrder The comparator to use to sort the result.
   * @param effectiveMaxSearchResult The max number of search results to include.
   * @param searchService The SearchService to use to perform the actual search.
   * @param onlyPublicUnits True if only public units should be included in the search result, otherwise false.
   * @return a sorted list of units.
   * @throws KivException if there is a problem performing the search.
   */
  SikSearchResultList<Unit> performSearch(UnitSearchSimpleForm theForm, Comparator<Unit> sortOrder, int effectiveMaxSearchResult, SearchService searchService, boolean onlyPublicUnits)
      throws KivException;
}
