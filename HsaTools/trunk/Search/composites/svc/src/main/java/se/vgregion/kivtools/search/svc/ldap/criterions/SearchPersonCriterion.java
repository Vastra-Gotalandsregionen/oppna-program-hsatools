package se.vgregion.kivtools.search.svc.ldap.criterions;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Implements search criterion for searches of persons in ldap.
 * 
 * @author David Bennehult
 * 
 */
public class SearchPersonCriterion {

  /**
   * Enumeration of searchable criterions.
   * 
   * @author David Bennehult
   */
  public enum SearchCriterion {
    /** given name. */
    GIVEN_NAME("givenName"),
    /** surname. */
    SURNAME("sn"),
    /** Employment title. */
    EMPLOYMENT_TITEL("paTitleCode"),
    /** user id. */
    USER_ID("vgr-id"),
    /** Unit name. */
    EMPLOYMENT_AT_UNIT("vgrStrukturPerson"),
    /** hsaSpecialityCode. */
    SPECIALITY_AREA_CODE("hsaSpecialityCode"),
    /** user profession. */
    PROFESSION("hsaTitle"),
    /** mail. */
    E_MAIL("mail"),
    /** hsaLanguageKnowledgeCode. */
    LANGUAGE_KNOWLEDGE_CODE("hsaLanguageKnowledgeCode"),
    /** administration. */
    ADMINISTRATION("vgrAO3kod");

    private String value;

    private SearchCriterion(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private SortedMap<SearchCriterion, String> searchCriterions = new TreeMap<SearchCriterion, String>();

  /**
   * Adds criterion values to the search criterion if provided value is not empty.
   * @param criterion The criterion to add value for.
   * @param value The value to add for the provided criterion.
   */
  public void addSearchCriterionValue(SearchCriterion criterion, String value) {
    if (!StringUtil.isEmpty(value)) {
      searchCriterions.put(criterion, value);
    }
  }

  /**
   * Get all search criterion.
   * 
   * @return A unmodifiable map of all search criterion.
   */
  public Map<SearchCriterion, String> getAllSearchCriterions() {
    return Collections.unmodifiableSortedMap(searchCriterions);
  }

  public boolean isEmpty() {
    return searchCriterions.size() <= 0;
  }
}
