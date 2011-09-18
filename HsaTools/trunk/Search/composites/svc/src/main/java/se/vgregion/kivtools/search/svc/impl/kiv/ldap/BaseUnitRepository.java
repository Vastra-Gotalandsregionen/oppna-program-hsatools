/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.SingleAttributeMapper;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
 */
public abstract class BaseUnitRepository implements UnitRepository {
  private static final DistinguishedName KIV_SEARCH_BASE = new DistinguishedName("ou=Org,o=vgr");
  private static final String LDAP_WILD_CARD = "*";
  // an "
  private static final String LDAP_EXACT_CARD = "\"";
  private static final List<String> ATTRIBUTES = Arrays.asList("*", "objectClass", "createTimestamp");
  private CodeTablesService codeTablesService;
  private LdapTemplate ldapTemplate;
  private UnitMapper unitMapper;

  private static final String OPPENVARD = "Öppenvård";
  private static final String HEMSJUKVARD = "Hemsjukvård";

  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  /**
   * @inheritDoc
   */
  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
    String searchFilter = this.createAdvancedSearchFilter(unit, onlyPublicUnits);
    // Perform search without limit since the information will be filtered
    SikSearchResultList<Unit> units = this.searchUnits(searchFilter, SearchControls.SUBTREE_SCOPE, Integer.MAX_VALUE, sortOrder);

    this.removeUnallowedUnits(units);

    this.removeOutdatedUnits(units);

    // this.removeUnitsWithCareTypeInpatient(units);

    int numberOfHits = units.size();

    // Make sure that only a subset is returned
    if (numberOfHits > maxResult) {
      units = new SikSearchResultList<Unit>(units.subList(0, maxResult));
    }

    // Set the total number of found items on the returned result set
    units.setTotalNumberOfFoundItems(numberOfHits);

    return units;
  }

  /**
   * Removes units that have passed its end date (hsaEndDate).
   * 
   * @param units
   */
  private void removeOutdatedUnits(SikSearchResultList<Unit> units) {
    Date now = TimeUtil.asDate();
    for (Iterator<Unit> iterator = units.iterator(); iterator.hasNext();) {
      Unit unit = iterator.next();
      if (unit.getHsaEndDate() != null && now.after(unit.getHsaEndDate())) {
        iterator.remove();
      }
    }
  }

  /**
   * Remove units that don't have at least one valid hsaBusinessClassificationCode.
   * 
   * @param units
   */
  protected void removeUnallowedUnits(SikSearchResultList<Unit> units) {

    // Get all health care types that are unfiltered
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

    for (int j = units.size() - 1; j >= 0; j--) {
      if (!this.unitMatchesUnfilteredHealtcareType(units.get(j), allUnfilteredHealthcareTypes)) {
        units.remove(units.get(j));
      }
    }
  }

  private boolean unitMatchesUnfilteredHealtcareType(Unit unit, List<HealthcareType> allUnfilteredHealthcareTypes) {
    boolean found = false;
    for (HealthcareType h : allUnfilteredHealthcareTypes) {
      for (Map.Entry<String, String> condition : h.getConditions().entrySet()) {
        String key = condition.getKey();
        String[] conditionValues = condition.getValue().split(",");
        Object value = ReflectionUtil.getProperty(unit, key, true);

        boolean conditionFulfilled = false;
        if (value instanceof String) {
          for (String v : conditionValues) {
            if (v.equals(value)) {
              conditionFulfilled = true;
            }
          }
        } else if (value instanceof List<?>) {
          for (String v : conditionValues) {
            if (((List<?>) value).contains(v)) {
              conditionFulfilled = true;
            }
          }
        }
        if (conditionFulfilled) {
          found = true;
          break;
        }
      }
    }
    return found;
  }

  /**
   * @inheritDoc
   */
  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxResult) throws KivException {
    String searchFilter = this.createSearchFilter(searchUnitCriterions);
    return this.searchUnits(searchFilter, SearchControls.SUBTREE_SCOPE, maxResult, new UnitNameComparator());
  }

  /**
   * @inheritDoc
   */
  @Override
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    String searchFilter = "(hsaIdentity=" + hsaId + ")";
    return this.searchUnit(this.getSearchBase(), SearchControls.SUBTREE_SCOPE, searchFilter);
  }

  /**
   * @inheritDoc
   */
  @Override
  public Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId) throws KivException {
    String searchFilterString = null;
    List<String> andFilterList = new ArrayList<String>();
    List<String> careTypes = new ArrayList<String>();
    careTypes.add(OPPENVARD);
    careTypes.add(HEMSJUKVARD);
    Filter careTypesFilterList = this.generateCareTypeFilterFromList(CodeTableName.VGR_CARE_TYPE, UnitSearchAttributes.CARE_TYPE, careTypes);
    // Filter hsaIdentityFilter = createSearchFilter(LDAPUnitAttributes.UNIT_ID.toString(), hsaId);
    andFilterList.add("(hsaIdentity=" + hsaId + ")");
    andFilterList.add(careTypesFilterList.encode());
    searchFilterString = this.makeAnd(andFilterList);
    return this.searchUnit(this.getSearchBase(), SearchControls.SUBTREE_SCOPE, searchFilterString);
  }

  /**
   * @inheritDoc
   */
  @Override
  public Unit getUnitByDN(DN dn) throws KivException {
    Unit u = null;
    DistinguishedName distinguishedName = new DistinguishedName(dn.escape().toString());
    u = this.lookupUnit(distinguishedName, ATTRIBUTES);
    return u;
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<String> getAllUnitsHsaIdentity() throws KivException {
    return this.getAllUnitsHsaIdentity(false);
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException {
    String searchFilter = this.createAllUnitsFilter(onlyPublicUnits);

    List<String> result = this.searchSingleAttribute(this.getSearchBase(), searchFilter, SearchControls.SUBTREE_SCOPE, Arrays.asList("hsaIdentity"), UnitLdapAttributes.HSA_IDENTITY);
    return result;
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<Unit> getAllUnits(boolean onlyPublicUnits) {
    String searchFilter = this.createAllUnitsFilter(onlyPublicUnits);

    List<Unit> result = this.searchUnits(this.getSearchBase(), searchFilter, SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    return result;
  }

  private String createAllUnitsFilter(boolean onlyPublicUnits) {
    String searchFilter = "(|(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")(objectclass=" + Constants.OBJECT_CLASS_FUNCTION_SPECIFIC + "))";

    List<String> filterList = new ArrayList<String>();
    if (onlyPublicUnits) {
      filterList.add("(hsaDestinationIndicator=03)");
    }

    // List<String> careTypes = new ArrayList<String>();
    // careTypes.add(OPPENVARD);
    // careTypes.add(HEMSJUKVARD);
    // Filter careTypesFilterList = this.generateCareTypeFilterFromList(CodeTableName.VGR_CARE_TYPE, LDAPUnitAttributes.CARE_TYPE, careTypes);
    // filterList.add(careTypesFilterList.encode());

    // Gets unit which has careType Öppenvård and Hemsjukvård
    filterList.add("(|(vgrCareType=01)(vgrCareType=03))");

    filterList.add(searchFilter);
    // (&(par3=value3)(par4=value4
    searchFilter = this.makeAnd(filterList);
    return searchFilter;
  }

  protected SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws KivException {
    SikSearchResultList<Unit> result = null;
    List<Unit> search = this.searchUnits(this.getSearchBase(), searchFilter, SearchControls.SUBTREE_SCOPE, ATTRIBUTES);

    result = this.cleanAndSortResult(search, maxResult, sortOrder);

    return result;
  }

  private Unit searchUnit(DistinguishedName searchBase, int searchScope, String searchFilter) throws KivException {
    List<Unit> result = this.searchUnits(searchBase, searchFilter, searchScope, ATTRIBUTES);
    if (result.size() == 0) {
      throw new KivNoDataFoundException("Error getting unit from server");
    }
    return result.get(0);
  }

  private SikSearchResultList<Unit> cleanAndSortResult(List<Unit> units, int maxResult, Comparator<Unit> sortOrder) {
    // Make sure we don't return duplicates
    Comparator<Unit> sortOrderCurrent = sortOrder;
    SikSearchResultList<Unit> result = new SikSearchResultList<Unit>(units);
    SikSearchResultList<Unit> resultNoDuplicates = this.deduplicateResult(result);

    if (sortOrderCurrent == null) {
      // No sort order was supplied, default to sorting on unit name.
      sortOrderCurrent = new UnitNameComparator();
    }

    Collections.sort(resultNoDuplicates, sortOrderCurrent);
    int resultCount = resultNoDuplicates.size();
    if (resultNoDuplicates.size() > maxResult && maxResult != 0) {
      resultNoDuplicates = new SikSearchResultList<Unit>(resultNoDuplicates.subList(0, maxResult));
    }
    resultNoDuplicates.setTotalNumberOfFoundItems(resultCount);

    return resultNoDuplicates;
  }

  private SikSearchResultList<Unit> deduplicateResult(SikSearchResultList<Unit> result) {
    SikSearchResultList<Unit> resultNoDuplicates = new SikSearchResultList<Unit>();
    for (Unit u : result) {
      // Would like to use "contains" which uses equals (where you could
      // test for same hsa-id) but that would break the searching.
      boolean alreadyExists = false;
      for (Unit uND : resultNoDuplicates) {
        if (u.getHsaIdentity().equals(uND.getHsaIdentity())) {
          alreadyExists = true;
          break;
        }
      }
      if (!alreadyExists) {
        resultNoDuplicates.add(u);
      }
    }
    return resultNoDuplicates;
  }

  /**
   * Creates a search string valid for functions from a search string valid for Units Input.
   * 
   * @param unitSearchString a full search criteria for unitSearch
   * @return A LDAP search string for search among functions
   */
  private String makeFunctionSearchStringFromUnitSearchString(String unitSearchString) {
    String functionSearchString = Formatter.replaceStringInString(unitSearchString, Constants.OBJECT_CLASS_UNIT_SPECIFIC, Constants.OBJECT_CLASS_FUNCTION_SPECIFIC);
    functionSearchString = Formatter.replaceStringInString(functionSearchString, Constants.OBJECT_CLASS_UNIT_STANDARD, Constants.OBJECT_CLASS_FUNCTION_STANDARD);
    functionSearchString = Formatter.replaceStringInString(functionSearchString, Constants.LDAP_PROPERTY_UNIT_NAME + "=", Constants.LDAP_PROPERTY_FUNCTION_NAME + "=");
    return functionSearchString;
  }

  /**
   * create search filter that search for both Units (and Functions).
   * 
   * @param searchUnitCriterions
   * @return
   * @throws KivException
   */
  String createSearchFilter(SearchUnitCriterions searchUnitCriterions) throws KivException {
    // create a plain unit search filter
    String unitSearchString = this.createUnitSearchFilter(searchUnitCriterions);

    // create a plain function search filter
    String functionSearchString = this.makeFunctionSearchStringFromUnitSearchString(unitSearchString);

    // add both filters together
    List<String> filterList = new ArrayList<String>();
    filterList.add(unitSearchString);
    filterList.add(functionSearchString);
    // (|(par1=value1)(par2=value2))
    String fullSearchString = this.makeOr(filterList);
    return fullSearchString;
  }

  String createAdvancedSearchFilter(Unit unit, boolean onlyPublicUnits) {
    // create a plain unit search filter

    String unitSearchString = this.createAdvancedUnitSearchFilter(unit);

    // create a plain function search filter
    String functionSearchString = this.makeFunctionSearchStringFromUnitSearchString(unitSearchString);

    // add filters together
    List<String> filterList = new ArrayList<String>();
    if (!"".equals(unitSearchString)) {
      filterList.add(unitSearchString);
    }
    if (!"".equals(functionSearchString)) {
      filterList.add(functionSearchString);
    }

    // (|(par1=value1)(par2=value2))
    String orCriterias = this.makeOr(filterList);

    filterList.clear();
    filterList.add(orCriterias);

    if (onlyPublicUnits) {
      filterList.add("(hsaDestinationIndicator=03)");
    }

    String andCriterias = this.makeAnd(filterList);
    return andCriterias;
  }

  protected String createUnitSearchFilter(SearchUnitCriterions searchUnitCriterions) {
    List<String> filterList = new ArrayList<String>();

    AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_UNIT_SPECIFIC));
    AndFilter andFilter2 = new AndFilter();

    // Create or hsaIdentity
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitId())) {
      OrFilter orHsaIdentity = new OrFilter();
      orHsaIdentity.or(this.createSearchFilter("hsaIdentity", searchUnitCriterions.getUnitId()));
      andFilter2.and(orHsaIdentity);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitName())) {
      OrFilter orUnitName = new OrFilter();
      orUnitName.or(this.createSearchFilter(Constants.LDAP_PROPERTY_UNIT_NAME, searchUnitCriterions.getUnitName()));
      andFilter2.and(orUnitName);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getAdministrationName())) {
      Filter orFilter = this.generateOrFilterFromList(CodeTableName.VGR_AO3_CODE, UnitSearchAttributes.ADMINISTRATION, searchUnitCriterions.getAdministrationName());
      andFilter2.and(orFilter);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getLiableCode())) {
      andFilter2.and(this.createSearchFilter("vgrAnsvarsnummer", searchUnitCriterions.getLiableCode()));
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getBusinessClassificationName())) {
      Filter orFilter = this.generateOrFilterFromList(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, UnitSearchAttributes.BUSINESS_CLASSIFICATION_CODE,
          searchUnitCriterions.getBusinessClassificationName());
      andFilter2.and(orFilter);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getCareTypeName())) {
      Filter orFilter = this.generateOrFilterFromList(CodeTableName.VGR_CARE_TYPE, UnitSearchAttributes.CARE_TYPE, searchUnitCriterions.getCareTypeName());
      andFilter2.and(orFilter);
    }
    // create or criteria
    if (!StringUtil.isEmpty(searchUnitCriterions.getLocation())) {
      OrFilter orMunicipalityAndAddresses = new OrFilter();
      OrFilter orMunicipalityName = new OrFilter();
      orMunicipalityName.or(this.createSearchFilter("hsaMunicipalityName", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation())));
      // Create or criteria a bit special...
      OrFilter orPostalAddress = this.createAddressSearchFilter(filterList, "hsaPostalAddress", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation()));
      // Create or criteria
      OrFilter orStreetAddress = this.createAddressSearchFilter(filterList, "hsaStreetAddress", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation()));
      orMunicipalityAndAddresses.or(orMunicipalityName).or(orPostalAddress).or(orStreetAddress);
      andFilter2.and(orMunicipalityAndAddresses);
    }
    andFilter.and(andFilter2);
    return andFilter.encode();
  }

  private Filter generateOrFilterFromList(CodeTableName codeTableName, UnitSearchAttributes criterion, String criterionValue) {
    List<String> codeFromTextValues = this.codeTablesService.getCodeFromTextValue(codeTableName, criterionValue);
    OrFilter orFilter = new OrFilter();
    if (codeFromTextValues.size() > 0) {
      for (String value : codeFromTextValues) {
        orFilter.or(new LikeFilter(criterion.toString(), value));
      }
    } else {
      orFilter.or(new EqualsFilter(criterion.toString(), "NO_VALID_CODE_TABLE_CODE_FOUND"));
    }
    return orFilter;
  }

  private Filter generateCareTypeFilterFromList(CodeTableName codeTableName, UnitSearchAttributes criterion, List<String> careTypes) {
    List<String> codeFromTextValues = new ArrayList<String>();
    for (String careType : careTypes) {
      codeFromTextValues.addAll(this.codeTablesService.getCodeFromTextValue(codeTableName, careType));
    }

    OrFilter orFilter = new OrFilter();
    if (codeFromTextValues.size() > 0) {
      for (String value : codeFromTextValues) {
        orFilter.or(new LikeFilter(criterion.toString(), value));
      }
    } else {
      orFilter.or(new EqualsFilter(criterion.toString(), "NO_VALID_CODE_TABLE_CODE_FOUND"));
    }
    return orFilter;
  }

  protected String createAdvancedUnitSearchFilter(Unit unit) {
    List<String> filterList = new ArrayList<String>();

    String searchFilter = "(&(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")";

    // or criterias
    this.addSearchFilter(filterList, "hsaMunicipalityName", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));
    if (unit.getHsaMunicipalityCode() != null) {
      this.addSearchFilter(filterList, "hsaMunicipalityCode", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityCode()));
    }

    // a bit special...
    this.createAddressSearchFilter(filterList, "hsaPostalAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));

    this.createAddressSearchFilter(filterList, "hsaStreetAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));
    // (|(par1=value1)(par2=value2))
    String orCriterias = this.makeOr(filterList);

    filterList = new ArrayList<String>();
    this.addSearchFilter(filterList, Constants.LDAP_PROPERTY_UNIT_NAME, unit.getName());
    this.addSearchFilter(filterList, "hsaBusinessClassificationCode", unit.getName());
    String unitNameOrBusinessClassificationCode = this.makeOr(filterList);

    filterList = new ArrayList<String>();

    if (!StringUtil.isEmpty(unitNameOrBusinessClassificationCode)) {
      filterList.add(unitNameOrBusinessClassificationCode);
    }
    if (!StringUtil.isEmpty(orCriterias)) {
      filterList.add(orCriterias);
    }

    this.addSearchFilter(filterList, "hsaIdentity", unit.getHsaIdentity());

    // Take all health care type conditions into consideration...
    if (unit.getHealthcareTypes() != null && unit.getHealthcareTypes().size() > 0) {
      this.addHealthCareTypeConditions(filterList, unit.getHealthcareTypes());
    }

    // (&(par3=value3)(par4=value4))
    String andCriterias = this.makeAnd(filterList);
    if (StringUtil.isEmpty(andCriterias)) {
      return "";
    }
    searchFilter = searchFilter + andCriterias + ")";
    return searchFilter;
  }

  /**
   * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * e.g. searchField=givenName searchValue=hans-erik result=(givenName=*hans*erik*)
   * 
   * e.g. searchField=givenName searchValue="hans-erik" result=(givenName=hans-erik)
   */
  private void addSearchFilter(List<String> filterList, String searchField, String searchValue) {
    String searchValueCurrent = searchValue;
    if (!StringUtil.isEmpty(searchValueCurrent)) {
      searchValueCurrent = searchValueCurrent.trim();
      if (this.isExactMatchFilter(searchValueCurrent)) {
        // remove "
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, LDAP_EXACT_CARD, "");
        // exact match
        filterList.add("(" + searchField + "=" + searchValueCurrent.trim() + ")");
      } else {
        // change spaces to wildcards
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, " ", LDAP_WILD_CARD);
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, "-", LDAP_WILD_CARD);
        filterList.add("(" + searchField + "=" + LDAP_WILD_CARD + searchValueCurrent + LDAP_WILD_CARD + ")");
      }
    }
  }

  /**
   * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * e.g. searchField=givenName searchValue=hans-erik result=(givenName=*hans*erik*)
   * 
   * e.g. searchField=givenName searchValue="hans-erik" result=(givenName=hans-erik)
   */
  private Filter createSearchFilter(String searchField, String searchValue) {
    String currentSearchValue = searchValue;
    Filter filter = null;
    if (!StringUtil.isEmpty(currentSearchValue)) {
      currentSearchValue = currentSearchValue.trim();
      if (this.isExactMatchFilter(currentSearchValue)) {
        // remove "
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, LDAP_EXACT_CARD, "");
        filter = new EqualsFilter(searchField, currentSearchValue.trim());
      } else {
        // change spaces to wildcards
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, " ", LDAP_WILD_CARD);
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, "-", LDAP_WILD_CARD);
        filter = new LikeFilter(searchField, LDAP_WILD_CARD + currentSearchValue + LDAP_WILD_CARD);
      }
    }
    return filter;
  }

  /**
   * e.g. searchField=hsaPostalAddress searchValue="uddevalla" result= (|(hsaPostalAddress =*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*) (hsaPostalAddress
   * =*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*) (hsaPostalAddress =*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))
   */
  private OrFilter createAddressSearchFilter(List<String> filterList, String searchField, String searchValue) {
    String searchValueCurrent = searchValue;
    OrFilter temp = null;
    if (!StringUtil.isEmpty(searchValueCurrent)) {
      searchValueCurrent = searchValueCurrent.trim();
      if (this.isExactMatchFilter(searchValueCurrent)) {
        // remove "
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, LDAP_EXACT_CARD, "");
        temp = this.buildAddressSearch(searchField, searchValueCurrent);
        // exact match
        filterList.add(temp.encode());
      } else {
        // change spaces to wildcards
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, " ", LDAP_WILD_CARD);
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, "-", LDAP_WILD_CARD);
        searchValueCurrent = LDAP_WILD_CARD + searchValueCurrent + LDAP_WILD_CARD;
        temp = this.buildAddressSearch(searchField, searchValueCurrent);
        // wildcard match
        filterList.add(temp.encode());
      }
    }
    return temp;
  }

  protected OrFilter buildAddressSearch(String searchField, String searchValue) {
    OrFilter orFilter = new OrFilter();
    for (int i = 0; i < 12; i += 2) {
      StringBuffer buf2 = new StringBuffer("*$*$*$*$*$*");
      buf2.replace(i, i + 1, searchValue);
      orFilter.or(new LikeFilter(searchField, buf2.toString()));
    }

    return orFilter;
  }

  private boolean isExactMatchFilter(String searchValue) {
    boolean exactMatch = false;

    // it has to be at least one character between the " e.g. "a" for an exact match
    if (searchValue.length() > 2 && searchValue.startsWith(LDAP_EXACT_CARD) && searchValue.endsWith(LDAP_EXACT_CARD)) {
      exactMatch = true;
    }

    return exactMatch;
  }

  private String makeOr(List<String> orFilterList) {
    String orCriterias = "";
    if (!orFilterList.isEmpty()) {
      orCriterias += "(|";
      for (String s : orFilterList) {
        orCriterias += s;
      }
      orCriterias += ")";
    }
    return orCriterias;
  }

  private String makeAnd(List<String> orFilterList) {
    String orCriterias = "";
    if (!orFilterList.isEmpty()) {
      orCriterias += "(&";
      for (String s : orFilterList) {
        orCriterias += s;
      }
      orCriterias += ")";
    }
    return orCriterias;
  }

  /**
   * Add conditions generated from supplied health care types to (also) supplied filterList.
   * 
   * @param filterList
   * @param allUnfilteredHealthcareTypes
   */
  private void addHealthCareTypeConditions(List<String> filterList, List<HealthcareType> allUnfilteredHealthcareTypes) {
    for (HealthcareType h : allUnfilteredHealthcareTypes) {

      // All conditions for the unfiltered health care type must be taken
      // into consideration
      List<String> conditionComponents = new ArrayList<String>();
      for (Map.Entry<String, String> condition : h.getConditions().entrySet()) {
        List<String> conditionComponentFilter = new ArrayList<String>();
        String[] conditionValues = condition.getValue().split(",");
        for (int i = 0; i < conditionValues.length; i++) {
          conditionComponentFilter.add("(" + condition.getKey() + "=" + conditionValues[i] + ")");
        }
        String conditionComponentsQuery = this.makeOr(conditionComponentFilter);
        conditionComponents.add(conditionComponentsQuery);
      }
      String healthCareTypeCondition = this.makeAnd(conditionComponents);

      // Add to top level, should be okay in same way as a valid
      // hsaBusinessClassificationCode
      filterList.add(healthCareTypeCondition);
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxResult) throws KivException {
    SikSearchResultList<Unit> subUnits = null;

    DistinguishedName parentDn = new DistinguishedName(parentUnit.getDn().toString());

    List<Unit> search = this.searchUnits(parentDn, "(objectClass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")", SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    subUnits = this.cleanAndSortResult(search, maxResult, null);
    this.removeUnitParentFromList(parentUnit, subUnits);
    return subUnits;
  }

  /**
   * @inheritDoc
   */
  @Override
  public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException {
    SikSearchResultList<Unit> subUnits = null;

    DistinguishedName parentDn = new DistinguishedName(parentUnit.getDn().toString());

    List<Unit> search = this.searchUnits(parentDn, "(objectClass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")", SearchControls.ONELEVEL_SCOPE, ATTRIBUTES);
    subUnits = this.cleanAndSortResult(search, 0, null);
    this.removeUnitParentFromList(parentUnit, subUnits);
    return subUnits;
  }

  // Remove parent unit from search result list
  private void removeUnitParentFromList(Unit parentUnit, SikSearchResultList<Unit> subUnits) {
    for (Unit unit : subUnits) {
      if (parentUnit.getHsaIdentity().equals(unit.getHsaIdentity())) {
        subUnits.remove(unit);
        break;
      }
    }
  }

  protected DistinguishedName getSearchBase() {
    return KIV_SEARCH_BASE;
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<String> getUnitAdministratorVgrIds(String hsaId) throws KivException {
    Unit unit = this.getUnitByHsaId(hsaId);
    List<String> administratorVgrIds = new ArrayList<String>();
    for (String dn : unit.getVgrObjectManagers()) {
      dn.trim();
      String vgrId = this.extractVgrId(dn);
      if (vgrId != null && vgrId.length() > 0) {
        administratorVgrIds.add(vgrId);
      }
    }
    return administratorVgrIds;
  }

  private String extractVgrId(String personDN) {
    String vgrId = null;
    String[] split = personDN.split(",");
    if (split[0].startsWith("cn")) {
      vgrId = split[0].replace("cn=", "");
    }
    return vgrId;
  }

  @SuppressWarnings("unchecked")
  private List<String> searchSingleAttribute(Name base, String filter, int searchScope, List<String> attrs, String mappingAttribute) {
    return this.ldapTemplate.search(base, filter, searchScope, attrs.toArray(new String[0]), new SingleAttributeMapper(mappingAttribute));
  }

  @SuppressWarnings("unchecked")
  private List<Unit> searchUnits(Name base, String filter, int searchScope, List<String> attrs) {
    return this.ldapTemplate.search(base, filter, searchScope, attrs.toArray(new String[0]), this.unitMapper);
  }

  private Unit lookupUnit(Name name, List<String> attrs) {
    return (Unit) this.ldapTemplate.lookup(name, attrs.toArray(new String[0]), this.unitMapper);
  }
}
