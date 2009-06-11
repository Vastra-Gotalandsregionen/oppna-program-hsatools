/**
 * Copyright 2009 Västa Götalandsregionen
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
 */
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.UnitNameComparator;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.util.Evaluator;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class UnitRepository {
	Log logger = LogFactory.getLog(this.getClass());
	static final int POOL_WAIT_TIME_MILLISECONDS = 2000;
	public static final String KIV_SEARCH_BASE = "ou=Org,o=vgr";
	static final String CLASS_NAME = UnitRepository.class.getName();
	static final String LDAP_WILD_CARD = "*";
	static final String LDAP_EXACT_CARD = "\""; // an "
	private LdapConnectionPool theConnectionPool = null;
	private CodeTablesService codeTablesService;

	public CodeTablesService getCodeTablesService() {
		return codeTablesService;
	}

	public void setCodeTablesService(CodeTablesService codeTablesService) {
		this.codeTablesService = codeTablesService;
	}

	public void setLdapConnectionPool(LdapConnectionPool lp) {
		this.theConnectionPool = lp;
	}

	public SikSearchResultList<Unit> searchUnits(Unit unit) throws Exception {
		return searchUnits(unit, 0); // Zero means all units
	}

	public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, Comparator<Unit> sortOrder) throws Exception {
		return searchAdvancedUnits(unit, 0, sortOrder, new ArrayList<Integer>()); // Zero
		// means
		// all
		// units
	}

	/**
	 * Advanced means that it also handles healthcareTypeConditions in the
	 * search filter
	 * 
	 * @param showUnitsWithTheseHsaBussinessClassificationCodes
	 */
	public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
		String searchFilter = createAdvancedSearchFilter(unit, showUnitsWithTheseHsaBussinessClassificationCodes);
		SikSearchResultList<Unit> units = searchUnits(searchFilter, LDAPConnection.SCOPE_SUB, maxResult, sortOrder);

		removeUnallowedUnits(units, showUnitsWithTheseHsaBussinessClassificationCodes);

		return units;
	}

	/**
	 * Remove units that don't have at least one valid
	 * hsaBusinessClassificationCode
	 * 
	 * @param units
	 * @param showUnitsWithTheseHsaBussinessClassificationCodes
	 */
	private void removeUnallowedUnits(SikSearchResultList<Unit> units, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) {

		// Get all health care types that are unfiltered
		HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
		List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

		for (int j = units.size() - 1; j >= 0; j--) {
			boolean found = false;
			for (String s : units.get(j).getHsaBusinessClassificationCode()) {
				try {
					if (showUnitsWithTheseHsaBussinessClassificationCodes.contains(Integer.parseInt(s))) {
						found = true;
					}
				} catch (NumberFormatException e) {
					// We simply ignore this. hsaBusinessClassificationCodes
					// should be integers, otherwise something is seriously
					// wrong.
				}
			}

			// The unit might still be valid because of the unfiltered
			// healthcare types
			if (!found) {
				// If this unit does not match any unfiltered health care type,
				// don't include in search result
				unfilteredHealthCareTypesLoop: for (HealthcareType h : allUnfilteredHealthcareTypes) {
					for (Map.Entry<String, String> condition : h.getConditions().entrySet()) {
						String key = condition.getKey();
						key = key.substring(0, 1).toUpperCase() + key.substring(1);
						String[] conditionValues = condition.getValue().split(",");
						for (int i = 0; i < conditionValues.length; i++) {
							Method keyMethod;
							Object value = null;
							try {
								keyMethod = units.get(j).getClass().getMethod("get" + key, null);
								value = keyMethod.invoke(units.get(j), null);
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							boolean conditionFulfilled = false;
							if (value instanceof String) {
								if (value != null) {
									for (String v : conditionValues) {
										if (v.equals(value)) {
											conditionFulfilled = true;
										}
									}
								}
							} else if (value instanceof List) {
								for (String v : conditionValues) {
									if ((((List<String>) value).contains(v))) {
										conditionFulfilled = true;
									}
								}
							}
							if (conditionFulfilled) {
								found = true;
								break unfilteredHealthCareTypesLoop;
							}
						}
					}
				}
			}

			if (!found) {
				units.remove(units.get(j));
			}
		}
	}

	public SikSearchResultList<Unit> searchUnits(Unit unit, int maxResult) throws Exception {
		String searchFilter = createSearchFilter(unit);
		return searchUnits(searchFilter, LDAPConnection.SCOPE_SUB, maxResult, new UnitNameComparator());
	}

	public Unit getUnitByHsaId(String hsaId) throws Exception {
		String searchFilter = "(hsaIdentity=" + hsaId + ")";
		return searchUnit(KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, searchFilter);
	}

	public Unit getUnitByDN(DN dn) throws Exception {
		LDAPConnection lc = null;
		Unit u = null;

		try {
			lc = getLDAPConnection();
			u = UnitFactory.reconstitute(lc.read(dn.escape().toString()));
			assignCodeTableValuesToUnit(u);
		} finally {
			theConnectionPool.freeConnection(lc);
		}
		return u;
	}

	public List<String> getAllUnitsHsaIdentity() throws Exception {
		return getAllUnitsHsaIdentity(new ArrayList<Integer>());
	}

	public List<Unit> getAllUnits() throws Exception {
		List<Unit> units = new ArrayList<Unit>();
		for (String hsaId : getAllUnitsHsaIdentity()) {
			Unit unit = getUnitByHsaId(hsaId);
			if (unit != null) {
				units.add(unit);
			}
		}
		return units;
	}

	public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
		LDAPConnection lc = null;
		LDAPSearchConstraints constraints = new LDAPSearchConstraints();
		constraints.setMaxResults(0);
		String searchFilter = "(|(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")(objectclass=" + Constants.OBJECT_CLASS_FUNCTION_SPECIFIC + "))";

		List<String> filterList = new ArrayList<String>();
		String includedBCCSearchString = makeShowUnitsWithTheseHsaBussinessClassificationCodesString(showUnitsWithTheseHsaBussinessClassificationCodes);
		filterList.add(includedBCCSearchString);
		filterList.add(searchFilter);
		searchFilter = makeAnd(filterList); // (&(par3=value3)(par4=value4

		String[] attributes = new String[1];
		attributes[0] = "hsaIdentity";
		List<String> result = new ArrayList<String>();

		try {
			lc = getLDAPConnection();
			LDAPSearchResults searchResults = lc.search(KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, searchFilter, attributes, false, constraints);
			// fill the list from the search result
			while (searchResults.hasMore()) {
				try {
					LDAPEntry nextEntry = searchResults.next();
					LDAPAttribute attribute = nextEntry.getAttribute(attributes[0]);
					if (attribute != null) {
						result.add(attribute.getStringValue());
					}
				} catch (LDAPException e) {
					if (e.getResultCode() == LDAPException.SIZE_LIMIT_EXCEEDED || e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR) {
						// break;
						throw new NoConnectionToServerException();
					} else {
						continue; // take next Unit
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			theConnectionPool.freeConnection(lc);
		}

		return result;
	}

	private SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws Exception {
		LDAPSearchConstraints constraints = new LDAPSearchConstraints();
		LDAPConnection lc = null;
		constraints.setMaxResults(0);
		SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
		String[] attributes = { LDAPConnection.ALL_USER_ATTRS, "createTimeStamp" }; // Get
		// all
		// attributes,
		// including
		// operational attribute createTimeStamp

		logger.debug("LDAP search filter: " + searchFilter);

		try {
			lc = getLDAPConnection();
			result = extractResult(lc.search(KIV_SEARCH_BASE, searchScope, searchFilter, attributes, false, constraints), maxResult, sortOrder);
		} catch (SikInternalException e) {
			// We have no good connection to LDAP server and should be able to
			// tell the user we have no hope of success.
			throw new NoConnectionToServerException();
		} finally {
			theConnectionPool.freeConnection(lc);
		}
		return result;
	}

	private Unit searchUnit(String searchBase, int searchScope, String searchFilter) throws Exception {
		LDAPSearchConstraints constraints = new LDAPSearchConstraints();
		LDAPConnection lc = null;
		constraints.setMaxResults(0);
		Unit result = new Unit();
		String[] attributes = { LDAPConnection.ALL_USER_ATTRS, "createTimeStamp" }; // Get
		// all
		// attributes,
		// including
		// operational attribute createTimeStamp

		try {
			lc = getLDAPConnection();
			result = extractSingleResult(lc.search(searchBase, searchScope, searchFilter, attributes, false, constraints));
			assignCodeTableValuesToUnit(result);
		} catch (NoConnectionToServerException e) {
			// We have no good connection to LDAP server and should be able to
			// tell the user we have no hope of success.
			throw e;
		} finally {
			theConnectionPool.freeConnection(lc);
		}
		return result;
	}

	/**
	 * Get Ldap connection using a pool
	 * 
	 * @return
	 * @throws LDAPException
	 * @throws UnsupportedEncodingException
	 * @throws SikInternalException
	 */
	private LDAPConnection getLDAPConnection() throws LDAPException, UnsupportedEncodingException, NoConnectionToServerException, SikInternalException {
		LDAPConnection lc = theConnectionPool.getConnection(POOL_WAIT_TIME_MILLISECONDS);
		if (lc == null) {
			throw new SikInternalException(this, "getLDAPConnection()", "Could not get a connection after waiting " + POOL_WAIT_TIME_MILLISECONDS + " ms.");
		}
		return lc;
	}

	private Unit extractSingleResult(LDAPSearchResults searchResults) throws Exception {
		return UnitFactory.reconstitute(searchResults.next());
	}

	private SikSearchResultList<Unit> extractResult(LDAPSearchResults searchResults, int maxResult, Comparator<Unit> sortOrder) throws Exception {
		SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
		while (searchResults.hasMore()) {
			try {
				Unit u = UnitFactory.reconstitute(searchResults.next());
				assignCodeTableValuesToUnit(u);
				result.add(u);
			} catch (LDAPException e) {
				if (e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR) {
					// break;
					throw new NoConnectionToServerException();
				} else {
					continue;
				}
			}
		}

		// Make sure we don't return duplicates
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

		if (sortOrder == null) {
			// No sort order was supplied, default to sorting on unit name.
			sortOrder = new UnitNameComparator();
		}

		Collections.sort(resultNoDuplicates, sortOrder);
		int resultCount = resultNoDuplicates.size();
		if (resultNoDuplicates.size() > maxResult && maxResult != 0) {
			resultNoDuplicates = new SikSearchResultList<Unit>(resultNoDuplicates.subList(0, maxResult));
		}
		resultNoDuplicates.setTotalNumberOfFoundItems(resultCount);

		return resultNoDuplicates;
	}

	/**
	 * Uses code table service in order to lookup text value for "coded values".
	 * 
	 * @param u
	 */
	private void assignCodeTableValuesToUnit(Unit u) {
		String hsaManagementText = codeTablesService.getValueFromCode(CodeTableName.HSA_MANAGEMENT_CODE, u.getHsaManagementCode());
		List<String> businessText = new ArrayList<String>();
		for (String businessCode : u.getHsaBusinessClassificationCode()) {
			String hsaBusinessClassificationText = codeTablesService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessCode);
			businessText.add(hsaBusinessClassificationText);
		}
		u.setHsaBusinessClassificationText(businessText);
		u.setHsaManagementText(hsaManagementText);
		String hsaHsaAdministrationFormText = codeTablesService.getValueFromCode(CodeTableName.HSA_ADMINISTRATION_FORM, u.getHsaAdministrationForm());
		u.setHsaAdministrationFormText(hsaHsaAdministrationFormText);
		String vgrAO3Text = codeTablesService.getValueFromCode(CodeTableName.VGR_AO3_CODE, u.getVgrAO3kod());
		u.setVgrAO3kodText(vgrAO3Text);
		String vgrCareTypeText = codeTablesService.getValueFromCode(CodeTableName.VGR_CARE_TYPE, u.getVgrCareType());
		u.setVgrCareTypeText(vgrCareTypeText);
	}

	/**
	 * Creates a search string valid for functions from a search string valid
	 * for Units Input:
	 * 
	 * @param unitSearchString
	 *            a full search criteria for unitSearch
	 * @return A LDAP search string for search among functions
	 * @throws Exception
	 */
	String makeFunctionSearchStringFromUnitSearchString(String unitSearchString) throws Exception {
		String functionSearchString = Formatter.replaceStringInString(unitSearchString, Constants.OBJECT_CLASS_UNIT_SPECIFIC, Constants.OBJECT_CLASS_FUNCTION_SPECIFIC);
		functionSearchString = Formatter.replaceStringInString(functionSearchString, Constants.OBJECT_CLASS_UNIT_STANDARD, Constants.OBJECT_CLASS_FUNCTION_STANDARD);
		functionSearchString = Formatter.replaceStringInString(functionSearchString, Constants.LDAP_PROPERTY_UNIT_NAME + "=", Constants.LDAP_PROPERTY_FUNCTION_NAME + "=");
		return functionSearchString;
	}

	/**
	 * create search filter that search for both Units (and Functions)
	 * 
	 * @param unit
	 * @return
	 * @throws Exception
	 */
	String createSearchFilter(Unit unit) throws Exception {
		// create a plain unit search filter
		String unitSearchString = createUnitSearchFilter(unit);

		// create a plain function search filter
		String functionSearchString = makeFunctionSearchStringFromUnitSearchString(unitSearchString);

		// add both filters together
		List<String> filterList = new ArrayList<String>();
		filterList.add(unitSearchString);
		filterList.add(functionSearchString);
		String fullSearchString = makeOr(filterList); // (|(par1=value1)(par2=
		// value2))
		return fullSearchString;
	}

	/**
	 * create search filter that search for both Units (and Functions)
	 * 
	 * @param unit
	 * @param showUnitsWithTheseHsaBussinessClassificationCodes
	 * @return
	 * @throws Exception
	 */
	String createAdvancedSearchFilter(Unit unit, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
		// create a plain unit search filter
		String unitSearchString = createAdvancedUnitSearchFilter(unit);

		// create a plain function search filter
		String functionSearchString = makeFunctionSearchStringFromUnitSearchString(unitSearchString);

		// add filters together
		List<String> filterList = new ArrayList<String>();
		if (!"".equals(unitSearchString)) {
			filterList.add(unitSearchString);
		}
		if (!"".equals(functionSearchString)) {
			filterList.add(functionSearchString);
		}
		String orCriterias = makeOr(filterList); // (|(par1=value1)(par2=value2))
		return orCriterias;
	}

	/**
	 * Filter the units in a way that only units with a valid
	 * hsaBusinessClassificationCode or that should be matched by an unfiltered
	 * health care type is included.
	 * 
	 * @param showUnitsWithTheseHsaBussinessClassificationCodes
	 * @return
	 * @throws Exception
	 */
	private String makeShowUnitsWithTheseHsaBussinessClassificationCodesString(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
		long startTimeMillis = System.currentTimeMillis();
		List<String> filterList = new ArrayList<String>();

		for (Integer id : showUnitsWithTheseHsaBussinessClassificationCodes) {
			addSearchFilter(filterList, "hsaBusinessClassificationCode", "\"" + String.valueOf(id) + "\"");
		}

		// If there were no hsaBusinessClassificationCode filter, no further
		// conditions
		if (showUnitsWithTheseHsaBussinessClassificationCodes.size() == 0) {
			return "";
		}

		/*
		 * Include unfiltered health care conditions without taking
		 * showUnitsWithTheseHsaBussinessClassificationCodes into consideration.
		 */

		// Get all health care types that are unfiltered
		HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
		List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

		// For every unfiltered health care type, generate a sufficient
		// condition
		addHealthCareTypeConditions(filterList, allUnfilteredHealthcareTypes);

		String orCriterias = makeOr(filterList); // (|(par1=value1)(par2=value2))
		long endTimeMillis = System.currentTimeMillis();
		logger.debug("Creating filter for hsaBusinessClassificationCode took: " + (endTimeMillis - startTimeMillis) + " milliseconds.");
		return orCriterias;
	}

	String createUnitSearchFilter(Unit unit) throws Exception {
		List<String> filterList = new ArrayList<String>();

		String searchFilter = "&(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")";

		// or criterias
		addSearchFilter(filterList, "hsaMunicipalityName", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));

		// a bit special...
		addAddressSearchFilter(filterList, "hsaPostalAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));

		addAddressSearchFilter(filterList, "hsaStreetAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));
		String orCriterias = makeOr(filterList); // (|(par1=value1)(par2=value2))

		filterList = new ArrayList<String>();
		addSearchFilter(filterList, Constants.LDAP_PROPERTY_UNIT_NAME, unit.getName());
		if (!Evaluator.isEmpty(orCriterias)) {
			filterList.add(orCriterias);
		}

		addSearchFilter(filterList, "hsaIdentity", unit.getHsaIdentity());

		String andCriterias = makeAnd(filterList); // (&(par3=value3)(par4=value4
		// ))
		if (Evaluator.isEmpty(andCriterias)) {
			return searchFilter;
		}
		searchFilter = "(" + searchFilter + andCriterias + ")";
		return searchFilter;
	}

	String createAdvancedUnitSearchFilter(Unit unit) throws Exception {
		List<String> filterList = new ArrayList<String>();

		String searchFilter = "(&(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")";

		// or criterias
		addSearchFilter(filterList, "hsaMunicipalityName", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));
		if (unit.getHsaMunicipalityCode() != null) {
			addSearchFilter(filterList, "hsaMunicipalityCode", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityCode()));
		}

		// a bit special...
		addAddressSearchFilter(filterList, "hsaPostalAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));

		addAddressSearchFilter(filterList, "hsaStreetAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));
		String orCriterias = makeOr(filterList); // (|(par1=value1)(par2=value2))

		filterList = new ArrayList<String>();
		addSearchFilter(filterList, Constants.LDAP_PROPERTY_UNIT_NAME, unit.getName());
		if (!Evaluator.isEmpty(orCriterias)) {
			filterList.add(orCriterias);
		}

		addSearchFilter(filterList, "hsaIdentity", unit.getHsaIdentity());

		// Take all health care type conditions into consideration...
		if (unit.getHealthcareTypes() != null && unit.getHealthcareTypes().size() > 0) {
			addHealthCareTypeConditions(filterList, unit.getHealthcareTypes());
		}

		String andCriterias = makeAnd(filterList); // (&(par3=value3)(par4=value4
		// ))
		if (Evaluator.isEmpty(andCriterias)) {
			return "";
		}
		searchFilter = searchFilter + andCriterias + ")";
		return searchFilter;
	}

	/**
	 * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
	 * 
	 * e.g. searchField=givenName searchValue=hans-erik
	 * result=(givenName=*hans*erik*)
	 * 
	 * e.g. searchField=givenName searchValue="hans-erik"
	 * result=(givenName=hans-erik)
	 * 
	 * @throws Exception
	 */
	private void addSearchFilter(List<String> filterList, String searchField, String searchValue) throws Exception {
		if (!Evaluator.isEmpty(searchValue)) {
			searchValue = searchValue.trim();
			if (isExactMatchFilter(searchValue)) {
				searchValue = Formatter.replaceStringInString(searchValue, LDAP_EXACT_CARD, ""); // remove
				// "
				filterList.add("(" + searchField + "=" + searchValue.trim() + ")"); // exact
				// match
			} else {
				// change spaces to wildcards
				searchValue = Formatter.replaceStringInString(searchValue, " ", LDAP_WILD_CARD);
				searchValue = Formatter.replaceStringInString(searchValue, "-", LDAP_WILD_CARD);
				filterList.add("(" + searchField + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + ")");
			}
		}
	}

	/**
	 * e.g. searchField=hsaPostalAddress searchValue="uddevalla" result=
	 * (|(hsaPostalAddress
	 * =*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*)
	 * (hsaPostalAddress
	 * =*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*)
	 * (hsaPostalAddress
	 * =*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))
	 * 
	 * @throws Exception
	 */
	void addAddressSearchFilter(List<String> filterList, String searchField, String searchValue) throws Exception {
		if (!Evaluator.isEmpty(searchValue)) {
			searchValue = searchValue.trim();
			if (isExactMatchFilter(searchValue)) {
				searchValue = Formatter.replaceStringInString(searchValue, LDAP_EXACT_CARD, ""); // remove
				// "
				String temp = buildAddressSearch(searchField, searchValue);
				filterList.add(temp); // exact match
			} else {
				// change spaces to wildcards
				searchValue = Formatter.replaceStringInString(searchValue, " ", LDAP_WILD_CARD);
				searchValue = Formatter.replaceStringInString(searchValue, "-", LDAP_WILD_CARD);
				searchValue = LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD;
				String temp = buildAddressSearch(searchField, searchValue);
				filterList.add(temp); // wildcard match
			}
		}
	}

	/**
	 * e.g. searchField=hsaPostalAddress searchValue="*uddevalla*" result=
	 * (|(hsaPostalAddress
	 * =*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*)
	 * (hsaPostalAddress
	 * =*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*)
	 * (hsaPostalAddress
	 * =*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))
	 **/
	String buildAddressSearch(String searchField, String searchValue) {
		StringBuffer buf = new StringBuffer();
		buf.append("(|(");
		buf.append(searchField);
		buf.append("=");
		buf.append(searchValue);
		buf.append("$*$*$*$*$*)");

		buf.append("(");
		buf.append(searchField);
		buf.append("=*$");
		buf.append(searchValue);
		buf.append("$*$*$*$*)");

		buf.append("(");
		buf.append(searchField);
		buf.append("=*$*$");
		buf.append(searchValue);
		buf.append("$*$*$*)");

		buf.append("(");
		buf.append(searchField);
		buf.append("=*$*$*$");
		buf.append(searchValue);
		buf.append("$*$*)");

		buf.append("(");
		buf.append(searchField);
		buf.append("=*$*$*$*$");
		buf.append(searchValue);
		buf.append("$*)");

		buf.append("(");
		buf.append(searchField);
		buf.append("=*$*$*$*$*$");
		buf.append(searchValue);
		buf.append("))");

		return buf.toString();
	}

	private boolean isExactMatchFilter(String searchValue) {
		if (Evaluator.isEmpty(searchValue)) {
			return false;
		}
		// it has to be at least one character between the " e.g. "a" for an
		// exact match
		if (searchValue.length() <= 2) {
			return false;
		}
		if (searchValue.startsWith(LDAP_EXACT_CARD) && searchValue.endsWith(LDAP_EXACT_CARD)) {
			return true;
		}
		return false;
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
	 * Add conditions generated from supplied health care types to (also)
	 * supplied filterList.
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
				String conditionComponentsQuery = makeOr(conditionComponentFilter);
				conditionComponents.add(conditionComponentsQuery);
			}
			String healthCareTypeCondition = makeAnd(conditionComponents);

			// Add to top level, should be okay in same way as a valid
			// hsaBusinessClassificationCode
			filterList.add(healthCareTypeCondition);
		}
	}

	/**
	 * 
	 * @param parentUnit
	 * @param maxResult
	 * @return
	 * @throws Exception
	 */
	public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxResult) throws Exception {
		SikSearchResultList<Unit> subUnits = null;
		DN parentDn = parentUnit.getDn();
		LDAPConnection ldapConnection = getLDAPConnection();
		subUnits = extractResult(ldapConnection.search(parentDn.toString(), LDAPConnection.SCOPE_SUB, "(objectClass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")", null, false), maxResult, null);
		removeUnitParentFromList(parentUnit, subUnits);
		return subUnits;
	}

	// Remove parent unit from search result list
	private void removeUnitParentFromList(Unit parentUnit, SikSearchResultList<Unit> subUnits) {

		for (Iterator<Unit> iterator = subUnits.iterator(); iterator.hasNext();) {
			Unit unit = iterator.next();
			if (parentUnit.getHsaIdentity().equals(unit.getHsaIdentity())) {
				subUnits.remove(unit);
				break;
			}
		}
	}
}