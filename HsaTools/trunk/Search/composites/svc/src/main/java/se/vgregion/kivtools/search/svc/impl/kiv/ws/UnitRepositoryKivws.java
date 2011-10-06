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

package se.vgregion.kivtools.search.svc.impl.kiv.ws;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitSearchAttributes;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitLdapAttributes;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfFunction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfString;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionDirectory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebServiceImplPortType;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;
import se.vgregion.kivtools.util.Arguments;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

/**
 * This is a copy of UnitRepository.
 * 
 * @author davidbennehult
 * 
 */
public class UnitRepositoryKivws implements UnitRepository {
  private static final DistinguishedName KIV_SEARCH_BASE = new DistinguishedName("ou=Org,o=vgr");
  private static final String LDAP_WILD_CARD = "*";
  private static final String LDAP_EXACT_CARD = "\"";
  private static final List<String> ATTRIBUTES = new ArrayList<String>();
  private final Log logger = LogFactory.getLog(UnitRepositoryKivws.class);
  private final CodeTablesService codeTablesService;
  private final VGRegionWebServiceImplPortType vgregionWebService;
  private final KivwsUnitMapper kivwsUnitMapper;

  private static final String OPPENVARD = "Öppenvård";
  private static final String SLUTENVARD = "Slutenvård";
  private static final String HEMSJUKVARD = "Hemsjukvård";

  public UnitRepositoryKivws(VGRegionWebServiceImplPortType vgregionWebService, KivwsUnitMapper kivwsUnitMapper, CodeTablesService codeTablesService) {
    this.vgregionWebService = vgregionWebService;
    this.kivwsUnitMapper = kivwsUnitMapper;
    this.codeTablesService = codeTablesService;
  }

  /**
   * Advanced means that it also handles healthcareTypeConditions in the search filter.
   * 
   * @param unit - unit to search for.
   * @param maxResult - max result of found units to return.
   * @param sortOrder - sort order for the result list.
   * @param onlyPublicUnits - show units that should be displayed to the public.
   * 
   * @return - a list of found units.
   * @throws KivException If an error occur.
   */
  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
    String searchFilterOU = this.createAdvancedSearchFilter(unit, onlyPublicUnits, false);
    String searchFilterCN = this.createAdvancedSearchFilter(unit, onlyPublicUnits, true);

    List<Unit> searchOUresult = this.searchUnits(this.getSearchBase(), searchFilterOU, SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    List<Unit> searchCNResult = this.searchFunctionUnits(this.getSearchBase(), searchFilterCN, SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    searchOUresult.addAll(searchCNResult);
    SikSearchResultList<Unit> units = this.cleanAndSortResult(searchOUresult, sortOrder);

    this.removeUnallowedUnits(units);

    this.removeOutdatedUnits(units);

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
   * Search for selected unit.
   * 
   * @param searchUnitCriterions The unit to search for.
   * @param maxResult Max number of units to contain in the result.
   * @return List of found units.
   * @throws KivException .
   */
  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxResult) throws KivException {
    String searchFilterOU = this.createSearchFilter(searchUnitCriterions, false);
    String searchFilterCN = this.createSearchFilter(searchUnitCriterions, true);

    List<Unit> searchResultForOuUnits = this.searchUnits(this.getSearchBase(), searchFilterOU, SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    List<Unit> searchResultForCnUnits = this.searchFunctionUnits(this.getSearchBase(), searchFilterCN, SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    searchResultForOuUnits.addAll(searchResultForCnUnits);

    SikSearchResultList<Unit> result = this.cleanAndSortResult(searchResultForOuUnits, new UnitNameComparator());
    // Make sure that only a subset is returned
    return this.getMaxResultList(maxResult, result);
  }

  private SikSearchResultList<Unit> getMaxResultList(int maxResult, SikSearchResultList<Unit> result) {
    if (result.size() > maxResult) {
      result = new SikSearchResultList<Unit>(result.subList(0, maxResult));
    }
    return result;
  }

  /**
   * Fetch unit by the unit hsa id.
   * 
   * @param hsaId The hsa id of the unit.
   * @return The unit with the given hsa id.
   * @throws KivException .
   */
  @Override
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    String searchFilter = "(hsaIdentity=" + hsaId + ")";
    return this.searchUnit(this.getSearchBase(), SearchControls.SUBTREE_SCOPE, searchFilter);
  }

  /**
   * Fetch unit by the unit hsa id and does not have careType inpatient.
   * 
   * @param hsaId The hsa id of the unit.
   * @return The unit with the given hsa id.
   * @throws KivException .
   */
  @Override
  public Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId) throws KivException {
    String searchFilterString = null;
    List<String> andFilterList = new ArrayList<String>();
    List<String> careTypes = new ArrayList<String>();
    careTypes.add(OPPENVARD);
    careTypes.add(SLUTENVARD);
    careTypes.add(HEMSJUKVARD);
    Filter careTypesFilterList = this.generateCareTypeFilterFromList(KivwsCodeTableName.CARE_TYPE, UnitSearchAttributes.CARE_TYPE, careTypes);

    andFilterList.add("(hsaIdentity=" + hsaId + ")");
    andFilterList.add(careTypesFilterList.encode());
    searchFilterString = this.makeAnd(andFilterList);
    return this.searchUnit(this.getSearchBase(), SearchControls.SUBTREE_SCOPE, searchFilterString);
  }

  /**
   * Fetch unit by the unit dn.
   * 
   * @param dn The dn for the unit.
   * @return The unit with the given dn.
   * @throws KivException .
   */
  @Override
  public Unit getUnitByDN(DN dn) throws KivException {
    Unit u = null;
    DistinguishedName distinguishedName = new DistinguishedName(dn.escape().toString());
    u = this.lookupUnit(distinguishedName, ATTRIBUTES);
    return u;
  }

  /**
   * Get all hsa ids to all units.
   * 
   * @return List of hsa ids.
   * @throws KivException .
   */
  @Override
  public List<String> getAllUnitsHsaIdentity() throws KivException {
    return this.getAllUnitsHsaIdentity(false);
  }

  /**
   * Get all hsa ids for units with chosen businessClassification codes.
   * 
   * @param onlyPublicUnits List only units that should be displayed to the public.
   * @return List of found units.
   * @throws KivException .
   */
  @Override
  public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException {
    String searchFilter = this.createAllUnitsFilter(onlyPublicUnits);

    // Since SingleAttributeMapper return a String we are certain that the cast to List<String> is ok
    List<String> result = this.searchSingleAttribute(this.getSearchBase(), searchFilter, SearchControls.SUBTREE_SCOPE, Arrays.asList("hsaIdentity"), UnitLdapAttributes.HSA_IDENTITY);
    return result;
  }

  /**
   * Retrieves a list of all Units and functions filtered based on if only units for public display should be retrieved.
   * 
   * @param onlyPublicUnits Only select units from search that should be displayed to the public.
   * @return A list of units.
   */
  @Override
  public List<Unit> getAllUnits(boolean onlyPublicUnits) {
    String searchFilter = this.createAllUnitsFilter(onlyPublicUnits);

    List<Unit> result = new ArrayList<Unit>();
    result.addAll(this.searchUnits(this.getSearchBase(), searchFilter, SearchControls.SUBTREE_SCOPE, ATTRIBUTES));
    result.addAll(this.searchFunctionUnits(this.getSearchBase(), searchFilter, SearchControls.SUBTREE_SCOPE, ATTRIBUTES));
    return result;
  }

  private String createAllUnitsFilter(boolean onlyPublicUnits) {
    List<String> filterList = new ArrayList<String>();
    if (onlyPublicUnits) {
      filterList.add("(hsaDestinationIndicator=03)");
    }

    return this.makeAnd(filterList);
  }

  private Unit searchUnit(DistinguishedName searchBase, int searchScope, String searchFilter) throws KivException {
    // Since UnitMapper return Units we are certain that the suppression is ok
    List<Unit> result = this.searchUnits(searchBase, searchFilter, searchScope, ATTRIBUTES);
    List<Unit> resultFunctions = this.searchFunctionUnits(searchBase, searchFilter, searchScope, ATTRIBUTES);
    result.addAll(resultFunctions);
    if (result.size() == 0) {
      throw new KivNoDataFoundException("Error getting unit from server");
    }
    return result.get(0);
  }

  private SikSearchResultList<Unit> cleanAndSortResult(List<Unit> units, Comparator<Unit> sortOrder) {
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
  String createSearchFilter(SearchUnitCriterions searchUnitCriterions, boolean createCNSearchString) throws KivException {

    String result = this.createUnitSearchFilter(searchUnitCriterions);
    // create a plain unit search filter
    if (createCNSearchString) {
      // create a plain function search filter
      String functionSearchString = this.makeFunctionSearchStringFromUnitSearchString(result);
      result = functionSearchString;
    }
    return result;
  }

  String createAdvancedSearchFilter(Unit unit, boolean onlyPublicUnits, boolean createFunctionSearchFilter) {
    // create a plain unit search filter

    String result = this.createAdvancedUnitSearchFilter(unit);

    if (createFunctionSearchFilter) {
      // create a plain function search filter
      result = this.makeFunctionSearchStringFromUnitSearchString(result);
    }

    List<String> filterList = new ArrayList<String>();
    filterList.add(result);

    if (onlyPublicUnits) {
      filterList.add("(hsaDestinationIndicator=03)");
      result = this.makeAnd(filterList);
    }

    return result;
  }

  protected String createUnitSearchFilter(SearchUnitCriterions searchUnitCriterions) {
    List<String> filterList = new ArrayList<String>();

    AndFilter andFilter = new AndFilter();

    // Create or hsaIdentity
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitId())) {
      OrFilter orHsaIdentity = new OrFilter();
      orHsaIdentity.or(this.createSearchFilter("hsaIdentity", searchUnitCriterions.getUnitId()));
      andFilter.and(orHsaIdentity);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitName())) {
      OrFilter orUnitName = new OrFilter();
      orUnitName.or(this.createSearchFilter(Constants.LDAP_PROPERTY_UNIT_NAME, searchUnitCriterions.getUnitName()));
      andFilter.and(orUnitName);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getAdministrationName())) {
      Filter orFilter = this.generateOrFilterFromList(KivwsCodeTableName.VGR_AO3_CODE, UnitSearchAttributes.ADMINISTRATION, searchUnitCriterions.getAdministrationName());
      andFilter.and(orFilter);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getLiableCode())) {
      andFilter.and(this.createSearchFilter("vgrAnsvarsnummer", searchUnitCriterions.getLiableCode()));
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getBusinessClassificationName())) {
      Filter orFilter = this.generateOrFilterFromList(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, UnitSearchAttributes.BUSINESS_CLASSIFICATION_CODE,
          searchUnitCriterions.getBusinessClassificationName());
      andFilter.and(orFilter);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getCareTypeName())) {
      Filter orFilter = this.generateOrFilterFromList(KivwsCodeTableName.CARE_TYPE, UnitSearchAttributes.CARE_TYPE, searchUnitCriterions.getCareTypeName());
      andFilter.and(orFilter);
    }
    // create or criteria
    if (!StringUtil.isEmpty(searchUnitCriterions.getLocation())) {
      OrFilter orMunicipalityAndAddresses = new OrFilter();
      //OrFilter orMunicipalityName = new OrFilter();
      //orMunicipalityName.or(this.createSearchFilter("hsaMunicipalityName", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation())));
      // Create or criteria a bit special...
      OrFilter orPostalAddress = this.createAddressSearchFilter(filterList, "hsaPostalAddress", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation()));
      // Create or criteria
      OrFilter orStreetAddress = this.createAddressSearchFilter(filterList, "hsaStreetAddress", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation()));
      //orMunicipalityAndAddresses.or(orMunicipalityName).or(orPostalAddress).or(orStreetAddress);
      orMunicipalityAndAddresses.or(orPostalAddress).or(orStreetAddress);
      andFilter.and(orMunicipalityAndAddresses);
    }

    return andFilter.encode();
  }

  private Filter generateOrFilterFromList(KivwsCodeTableName codeTableName, UnitSearchAttributes criterion, String criterionValue) {
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

  private Filter generateCareTypeFilterFromList(KivwsCodeTableName codeTableName, UnitSearchAttributes criterion, List<String> careTypes) {
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
    if (!StringUtil.isEmpty(unit.getName())) {
      List<String> matchingHsaBusinessClassificationCodes = this.codeTablesService.getCodeFromTextValue(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, unit.getName());
      for (String matchingHsaBusinessClassificationCode : matchingHsaBusinessClassificationCodes) {
        this.addSearchFilter(filterList, "hsaBusinessClassificationCode", matchingHsaBusinessClassificationCode);
      }
    }
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

    String andCriterias = this.makeAnd(filterList);
    if (StringUtil.isEmpty(andCriterias)) {
      return "";
    }
    return andCriterias;
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
   * 
   * @param parentUnit - unit to get subunits for
   * @param maxResult - maximum of unit to be return in the result
   * @return A list of subunits for current unit.
   * @throws KivException If something goes wrong doing search.
   */
  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxResult) throws KivException {
    SikSearchResultList<Unit> subUnits = null;

    DistinguishedName parentDn = new DistinguishedName(parentUnit.getDn().toString());

    // Since UnitMapper return a Unit we are certain that the cast to List<Unit> is ok
    List<Unit> searchOU = this.searchUnits(parentDn, "(ou=*)", SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    List<Unit> searchCN = this.searchFunctionUnits(parentDn, "(cn=*)", SearchControls.SUBTREE_SCOPE, ATTRIBUTES);
    searchOU.addAll(searchCN);
    subUnits = this.cleanAndSortResult(searchOU, null);
    this.removeUnitParentFromList(parentUnit, subUnits);
    this.getMaxResultList(maxResult, subUnits);
    return subUnits;
  }

  /**
   * 
   * @param parentUnit - unit to get subunits for
   * @param maxResult - maximum of unit to be return in the result
   * @return A list the first level of subunits for current unit.
   * @throws KivException If something goes wrong doing search.
   */
  @Override
  public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException {
    SikSearchResultList<Unit> subUnits = null;

    DistinguishedName parentDn = new DistinguishedName(parentUnit.getDn().toString());

    // Since UnitMapper return a Unit we are certain that the cast to List<Unit> is ok
    List<Unit> search = this.searchUnits(parentDn, "(ou=*)", SearchControls.ONELEVEL_SCOPE, ATTRIBUTES);
    subUnits = this.cleanAndSortResult(search, null);
    this.removeUnitParentFromList(parentUnit, subUnits);
    this.getMaxResultList(subUnits.size(), subUnits);
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

  private Unit lookupUnit(Name name, List<String> attrs) {
    Arguments.notNull("name", name);
    Arguments.notNull("attrs", attrs);

    Unit unit = null;
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);

    try {
      String filter = "(" + name.get(name.size() - 1) + ")";
      Name base = name.getPrefix(name.size()-1);
      ArrayOfUnit searchUnit = this.vgregionWebService.searchUnit(filter, arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(SearchControls.ONELEVEL_SCOPE));
      unit = this.mapKivwsUnitToUnit(searchUnit).get(0);
    } catch (VGRException_Exception e) {
      this.logger.error(e.getMessage(), e);
      unit = new Unit();
    }

    return unit;
  }

  private List<Unit> searchFunctionUnits(Name base, String filter, int searchScope, List<String> attrs) {
    List<Unit> resultUnits = new ArrayList<Unit>();
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);
    try {
      ArrayOfFunction searchFunction = this.vgregionWebService.searchFunction(StringUtils.defaultIfEmpty(filter, "(cn=*)"), arrayOfString, VGRegionDirectory.KIV, base.toString(),
          Integer.toString(searchScope));
      resultUnits.addAll(this.mapKivwsUnits(searchFunction));
    } catch (VGRException_Exception e) {
      this.logger.error(e.getMessage(), e);
    }
    return resultUnits;
  }

  private List<String> searchSingleAttribute(Name base, String filter, int searchScope, List<String> attrs, String mappingAttribute) {
    List<String> result = new ArrayList<String>();
    SingleAttributeMapper singleAttributeMaper = new SingleAttributeMapper(mappingAttribute);
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);
    try {
      // Get all functions and map.
      ArrayOfFunction searchFunction = this.vgregionWebService.searchFunction(filter, arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(searchScope));
      ArrayOfUnit searchUnits = this.vgregionWebService.searchUnit(filter, arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(searchScope));
      List<Function> functions = searchFunction.getFunction();
      List<se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit> units = searchUnits.getUnit();

      for (Function function : functions) {
        result.add(singleAttributeMaper.map(function));
      }

      for (se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit unit : units) {
        result.add(singleAttributeMaper.map(unit));
      }
    } catch (VGRException_Exception e) {
      this.logger.error(e.getMessage(), e);
    }

    return result;
  }

  private List<Unit> searchUnits(Name base, String filter, int searchScope, List<String> attrs) {
    List<Unit> result = new ArrayList<Unit>();
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);

    try {
      ArrayOfUnit searchUnit = this.vgregionWebService.searchUnit(StringUtils.defaultIfEmpty(filter, "(ou=*)"), arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(searchScope));
      result = this.mapKivwsUnits(searchUnit);
    } catch (VGRException_Exception e) {
      this.logger.error(e.getMessage(), e);
    }
    return result;
  }

  private List<Unit> mapKivwsUnits(Object object) {
    List<Unit> result = null;
    if (object instanceof ArrayOfFunction) {
      result = this.mapKivwsUnitFunctionToUnit((ArrayOfFunction) object);
    } else {
      result = this.mapKivwsUnitToUnit((ArrayOfUnit) object);
    }
    return result;
  }

  private List<Unit> mapKivwsUnitToUnit(ArrayOfUnit arrayOfUnit) {
    List<se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit> unit = arrayOfUnit.getUnit();
    List<Unit> result = new ArrayList<Unit>();
    for (se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit kivwsUnit : unit) {
      result.add(this.kivwsUnitMapper.mapFromContext(kivwsUnit));
    }
    return result;
  }

  private List<Unit> mapKivwsUnitFunctionToUnit(ArrayOfFunction arrayOfFunction) {
    List<Unit> result = new ArrayList<Unit>();
    List<Function> function = arrayOfFunction.getFunction();
    for (Function function2 : function) {
      result.add(this.kivwsUnitMapper.mapFromContext(function2));
    }
    return result;
  }
}
