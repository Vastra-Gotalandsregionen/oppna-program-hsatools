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

package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.NamingException;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.SingleAttributeMapper;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
 */
public class UnitRepository {
	private static final DistinguishedName UNIT_SEARCH_BASE = DistinguishedName
			.immutableDistinguishedName(Constants.SEARCH_BASE);

	private static final String LDAP_WILD_CARD = "*";
	private static final String LDAP_EXACT_CARD = "\"";
	private LdapTemplate ldapTemplate;

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	/**
	 * Advanced means that it also handles healthcareTypeConditions in the
	 * search filter.
	 * 
	 * @param unit
	 *            Unit to base search filter on.
	 * @param maxResult
	 *            Maximum number of units to return.
	 * @param sortOrder
	 *            Sort order of the result list.
	 * @param onlyPublicUnits
	 *            List only units that should be displayed to the public.
	 * @return List of matching units.
	 * @throws KivException
	 *             If something goes wrong.
	 */
	public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit,
			int maxResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits)
			throws KivException {
		Filter searchFilter = this.createAdvancedSearchFilter(unit,
				onlyPublicUnits);

		return this.searchUnits(searchFilter, SearchControls.SUBTREE_SCOPE,
				maxResult, sortOrder);
	}

	/**
	 * Search for selected unit.
	 * 
	 * @param searchUnitCriterions
	 *            The unit to search for.
	 * @param maxResult
	 *            Max number of units to contain in the result.
	 * @return List of found units.
	 * @throws KivException .
	 */
	public SikSearchResultList<Unit> searchUnits(
			SearchUnitCriterions searchUnitCriterions, int maxResult)
			throws KivException {
		Filter searchFilter = this.createSearchFilter(searchUnitCriterions);
		return this.searchUnits(searchFilter, SearchControls.SUBTREE_SCOPE,
				maxResult, new UnitNameComparator());
	}

	/**
	 * 
	 * @param hsaId
	 *            Id to search for.
	 * @return Found unit object
	 * @throws KivException
	 *             If something goes wrong.
	 */
	public Unit getUnitByHsaId(String hsaId) throws KivException {
		Filter searchFilter = new EqualsFilter("hsaIdentity", hsaId);
		return this.searchUnit(UNIT_SEARCH_BASE, SearchControls.SUBTREE_SCOPE,
				searchFilter);
	}

	/**
	 * 
	 * @param dn
	 *            Dn to search for.
	 * @return Found unit.
	 * @throws KivException
	 *             If something goes wrong.
	 */
	public Unit getUnitByDN(DN dn) throws KivException {
		DistinguishedName distinguishedName = new DistinguishedName(
				dn.toString());
		UnitMapper unitMapper = new UnitMapper();

		Unit unit;
		try {
			// UnitMapper return a unit so we are certain that the cast is ok
			unit = (Unit) this.ldapTemplate.lookup(distinguishedName,
					unitMapper);
		} catch (NamingException e) {
			throw new KivException("Error getting unit from server: "
					+ e.getMessage());
		}

		return unit;
	}

	/**
	 * 
	 * @return List of all unit identity strings.
	 * @throws KivException
	 *             If something goes wrong.
	 */
	public List<String> getAllUnitsHsaIdentity() throws KivException {
		return this.getAllUnitsHsaIdentity(false);
	}

	/**
	 * 
	 * @param onlyPublicUnits
	 *            Units to include to the result list.
	 * @return List of all unit identity strings.
	 * @throws KivException
	 *             If something goes wrong.
	 */
	public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits)
			throws KivException {
		Filter filter = this.createAllUnitsFilter(onlyPublicUnits);

		PagedResultsCookie cookie = null;
		PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(
				100, cookie);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		List<String> result = new ArrayList<String>();

		try {
			do {
				// SingleAttributeMapper return a String so we are pretty
				// certain that List<String> is ok.
				@SuppressWarnings("unchecked")
				List<String> resultList = this.ldapTemplate.search(
						Constants.SEARCH_BASE, filter.encode(), searchControls,
						new SingleAttributeMapper("hsaIdentity"), control);
				// Put everything in a map to remove duplicates.
				for (String hsaIdentity : resultList) {
					result.add(hsaIdentity);
				}
			} while (control.getCookie().getCookie() != null);
		} catch (NamingException e) {
			throw new KivException("Error getting units id's from server: "
					+ e.getMessage());
		}

		return result;
	}

	/**
	 * Retrieves a list of all Units and functions filtered based on if only
	 * units for public display should be retrieved.
	 * 
	 * @param onlyPublicUnits
	 *            Only select units from search that should be displayed to the
	 *            public.
	 * @return A list of units.
	 * @throws KivException
	 *             If something goes wrong.
	 */
	public List<Unit> getAllUnits(boolean onlyPublicUnits) throws KivException {
		Filter filter = this.createAllUnitsFilter(onlyPublicUnits);

		PagedResultsCookie cookie = null;
		PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(
				100, cookie);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		UnitMapper unitMapper = new UnitMapper();
		List<Unit> result = new ArrayList<Unit>();

		try {
			do {
				// UnitMapper return a Unit so we are pretty certain that
				// List<Unit> is ok.
				@SuppressWarnings("unchecked")
				List<Unit> resultList = this.ldapTemplate.search(
						Constants.SEARCH_BASE, filter.encode(), searchControls,
						unitMapper, control);
				// Put everything in a map to remove duplicates.
				for (Unit unit : resultList) {
					result.add(unit);
				}
			} while (control.getCookie().getCookie() != null);
		} catch (NamingException e) {
			throw new KivException("Error getting units from server: "
					+ e.getMessage());
		}

		return result;
	}

	private Filter createAllUnitsFilter(boolean onlyPublicUnits) {
		OrFilter objectClassFilter = new OrFilter();
		objectClassFilter.or(new EqualsFilter("objectclass",
				Constants.OBJECT_CLASS_UNIT_SPECIFIC));
		objectClassFilter.or(new EqualsFilter("objectclass",
				Constants.OBJECT_CLASS_FUNCTION_SPECIFIC));

		AndFilter filter = new AndFilter();
		filter.and(objectClassFilter);

		if (onlyPublicUnits) {
			filter.and(new EqualsFilter("hsaDestinationIndicator", "03"));
		}

		return filter;
	}

	private SikSearchResultList<Unit> searchUnits(Filter searchFilter,
			int searchScope, int maxResult, Comparator<Unit> sortOrder)
			throws KivException {
		SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
		UnitMapper unitMapper = new UnitMapper();
		try {
			// Since UnitMapper returns unit the assignment to List<Unit> is
			// safe
			@SuppressWarnings("unchecked")
			List<Unit> searchResult = this.ldapTemplate.search(
					UNIT_SEARCH_BASE, searchFilter.encode(), searchScope,
					unitMapper);
			result.addAll(searchResult);
		} catch (NamingException e) {
			throw new KivException("Error searching units from server: "
					+ e.getMessage());
		}

		// Make sure we don't return duplicates
		SikSearchResultList<Unit> resultNoDuplicates = this
				.deduplicateUnits(result);

		Comparator<Unit> effectiveSortOrder = sortOrder;
		if (effectiveSortOrder == null) {
			// No sort order was supplied, default to sorting on unit name.
			effectiveSortOrder = new UnitNameComparator();
		}

		Collections.sort(resultNoDuplicates, effectiveSortOrder);

		int resultCount = resultNoDuplicates.size();
		if (resultNoDuplicates.size() > maxResult && maxResult != 0) {
			resultNoDuplicates = new SikSearchResultList<Unit>(
					resultNoDuplicates.subList(0, maxResult));
		}
		resultNoDuplicates.setTotalNumberOfFoundItems(resultCount);

		return resultNoDuplicates;
	}

	private Unit searchUnit(Name searchBase, int searchScope,
			Filter searchFilter) throws KivException {
		Unit result = new Unit();
		try {
			// UnitMapper return a Unit so we are pretty certain that List<Unit>
			// is ok.
			@SuppressWarnings("unchecked")
			List<Unit> searchResult = this.ldapTemplate.search(searchBase,
					searchFilter.encode(), searchScope, new UnitMapper());

			if (searchResult.size() > 0) {
				result = searchResult.get(0);
			}
		} catch (NamingException e) {
			throw new KivException("Error searching unit from server: "
					+ e.getMessage());
		}
		return result;
	}

	private SikSearchResultList<Unit> deduplicateUnits(
			SikSearchResultList<Unit> result) {
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
	 * create search filter that search for both Units (and Functions).
	 * 
	 * @param unit
	 * @return
	 * @throws Exception
	 */
	private Filter createSearchFilter(SearchUnitCriterions unit)
			throws KivException {
		// create a plain unit search filter
		Filter unitSearchFilter = this.createUnitSearchFilter(unit,
				Constants.OBJECT_CLASS_UNIT_SPECIFIC,
				Constants.LDAP_PROPERTY_UNIT_NAME);

		// create a plain function search filter
		Filter functionSearchFilter = this.createUnitSearchFilter(unit,
				Constants.OBJECT_CLASS_FUNCTION_SPECIFIC,
				Constants.LDAP_PROPERTY_FUNCTION_NAME);

		OrFilter searchFilter = new OrFilter();
		searchFilter.or(unitSearchFilter);
		searchFilter.or(functionSearchFilter);
		return searchFilter;
	}

	private Filter createAdvancedSearchFilter(Unit unit, boolean onlyPublicUnits)
			throws KivException {
		Filter unitSearch = this.createAdvancedUnitSearchFilter(unit,
				Constants.OBJECT_CLASS_UNIT_SPECIFIC,
				Constants.LDAP_PROPERTY_UNIT_NAME);
		Filter functionSearch = this.createAdvancedUnitSearchFilter(unit,
				Constants.OBJECT_CLASS_FUNCTION_SPECIFIC,
				Constants.LDAP_PROPERTY_FUNCTION_NAME);

		OrFilter searchFilter = new OrFilter();
		searchFilter.or(unitSearch);
		searchFilter.or(functionSearch);

		AndFilter filter = new AndFilter();
		filter.and(searchFilter);

		if (onlyPublicUnits) {
			filter.and(new EqualsFilter("hsaDestinationIndicator", "03"));
		}

		return filter;
	}

	private Filter createUnitSearchFilter(
			SearchUnitCriterions searchUnitCriterions, String objectClass,
			String unitNameProperty) throws KivException {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", objectClass));
		AndFilter andFilter2 = new AndFilter();

		// Create or hsaIdentity
		if (!StringUtil.isEmpty(searchUnitCriterions.getUnitId())) {
			OrFilter orHsaIdentity = new OrFilter();
			orHsaIdentity.or(this.createSearchFilter("hsaIdentity",
					searchUnitCriterions.getUnitId()));
			andFilter2.and(orHsaIdentity);
		}
		if (!StringUtil.isEmpty(searchUnitCriterions.getUnitName())) {
			OrFilter orUnitName = new OrFilter();
			orUnitName.or(this.createSearchFilter(unitNameProperty,
					searchUnitCriterions.getUnitName()));
			orUnitName.or(this.createSearchFilter(
					Constants.LDAP_PROPERTY_DESCRIPTION,
					searchUnitCriterions.getUnitName()));
			andFilter2.and(orUnitName);
		}

		// create or criteria
		if (!StringUtil.isEmpty(searchUnitCriterions.getLocation())) {
			OrFilter orMunicipalityAndAddresses = new OrFilter();
			OrFilter orMunicipalityName = new OrFilter();
			orMunicipalityName.or(this.createSearchFilter("municipalityName",
					LdapParse.escapeLDAPSearchFilter(searchUnitCriterions
							.getLocation())));
			Filter orPostalAddress = this.createSearchFilter("postalAddress",
					LdapParse.escapeLDAPSearchFilter(searchUnitCriterions
							.getLocation()));
			Filter orStreetAddress = this.createSearchFilter("streetAddress",
					LdapParse.escapeLDAPSearchFilter(searchUnitCriterions
							.getLocation()));
			orMunicipalityAndAddresses.or(orMunicipalityName)
					.or(orPostalAddress).or(orStreetAddress);
			andFilter2.and(orMunicipalityAndAddresses);
		}
		andFilter.and(andFilter2);
		return andFilter;
	}

	private Filter createAdvancedUnitSearchFilter(Unit unit,
			String objectClass, String unitNameProperty) throws KivException {
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter("objectClass", objectClass));

		OrFilter localityCriterias = new OrFilter();
		if (!StringUtil.isEmpty(unit.getHsaMunicipalityName())) {
			localityCriterias.or(this.createSearchFilter("municipalityName",
					LdapParse.escapeLDAPSearchFilter(unit
							.getHsaMunicipalityName())));
		}
		if (!StringUtil.isEmpty(unit.getHsaMunicipalityCode())) {
			localityCriterias.or(this.createSearchFilter("municipalityCode",
					LdapParse.escapeLDAPSearchFilter(unit
							.getHsaMunicipalityCode())));
		}
		if (!StringUtil.isEmpty(unit.getHsaMunicipalityName())) {
			localityCriterias.or(this.addAddressSearchFilter("postalAddress",
					LdapParse.escapeLDAPSearchFilter(unit
							.getHsaMunicipalityName())));
			localityCriterias.or(this.addAddressSearchFilter("streetAddress",
					LdapParse.escapeLDAPSearchFilter(unit
							.getHsaMunicipalityName())));
		}

		OrFilter unitNameCriterias = new OrFilter();
		// Add like search for unit name in description field
		if (!StringUtil.isEmpty(unit.getName())) {
			unitNameCriterias.or(this.createSearchFilter(
					Constants.LDAP_PROPERTY_DESCRIPTION, unit.getName()));
			unitNameCriterias.or(this.createSearchFilter(unitNameProperty,
					unit.getName()));
		}

		searchFilter.and(localityCriterias);
		searchFilter.and(unitNameCriterias);

		if (!StringUtil.isEmpty(unit.getHsaIdentity())) {
			searchFilter.and(this.createSearchFilter("hsaIdentity",
					unit.getHsaIdentity()));
		}

		// Take all health care type conditions into consideration...
		if (unit.getHealthcareTypes() != null
				&& unit.getHealthcareTypes().size() > 0) {
			searchFilter.and(this.createHealtCareTypeConditions(unit
					.getHealthcareTypes()));
		}

		return searchFilter;
	}

	private Filter createHealtCareTypeConditions(
			List<HealthcareType> healthcareTypes) {
		OrFilter healthCareTypeFilter = new OrFilter();

		for (HealthcareType healthcareType : healthcareTypes) {
			AndFilter conditionFilter = new AndFilter();
			// All conditions for the unfiltered health care type must be taken
			// into consideration
			for (Map.Entry<String, String> condition : healthcareType
					.getConditions().entrySet()) {
				OrFilter valueFilter = new OrFilter();
				String[] conditionValues = condition.getValue().split(",");
				for (int i = 0; i < conditionValues.length; i++) {
					valueFilter.or(new EqualsFilter(condition.getKey(),
							conditionValues[i]));
				}
				conditionFilter.and(valueFilter);
			}
			healthCareTypeFilter.or(conditionFilter);
		}

		return healthCareTypeFilter;
	}

	/**
	 * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
	 * 
	 * e.g. searchField=givenName searchValue=hans-erik
	 * result=(givenName=*hans*erik*)
	 * 
	 * e.g. searchField=givenName searchValue="hans-erik"
	 * result=(givenName=hans-erik)
	 */
	private Filter createSearchFilter(String searchField, String searchValue) {
		String currentSearchValue = searchValue;
		Filter filter = null;
		if (!StringUtil.isEmpty(currentSearchValue)) {
			currentSearchValue = currentSearchValue.trim();
			if (this.isExactMatchFilter(currentSearchValue)) {
				// remove "
				currentSearchValue = Formatter.replaceStringInString(
						currentSearchValue, LDAP_EXACT_CARD, "");
				filter = new EqualsFilter(searchField,
						currentSearchValue.trim());
			} else {
				// change spaces to wildcards
				currentSearchValue = Formatter.replaceStringInString(
						currentSearchValue, " ", LDAP_WILD_CARD);
				currentSearchValue = Formatter.replaceStringInString(
						currentSearchValue, "-", LDAP_WILD_CARD);
				currentSearchValue = LDAP_WILD_CARD + currentSearchValue
						+ LDAP_WILD_CARD;
				currentSearchValue = currentSearchValue.replaceAll("\\*\\*",
						"*");
				filter = new LikeFilter(searchField, currentSearchValue);
			}
		}
		return filter;
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
	 * @throws KivException
	 */
	private Filter addAddressSearchFilter(String searchField, String searchValue) {
		String searchValueCurrent = searchValue;
		Filter temp = null;
		if (!StringUtil.isEmpty(searchValueCurrent)) {
			searchValueCurrent = searchValueCurrent.trim();
			if (this.isExactMatchFilter(searchValueCurrent)) {
				// remove "
				searchValueCurrent = Formatter.replaceStringInString(
						searchValueCurrent, LDAP_EXACT_CARD, "");
				temp = this.buildAddressSearch(searchField, searchValueCurrent);
				// exact match
			} else {
				// change spaces to wildcards
				searchValueCurrent = Formatter.replaceStringInString(
						searchValueCurrent, " ", LDAP_WILD_CARD);
				searchValueCurrent = Formatter.replaceStringInString(
						searchValueCurrent, "-", LDAP_WILD_CARD);
				searchValueCurrent = LDAP_WILD_CARD + searchValueCurrent
						+ LDAP_WILD_CARD;
				temp = this.buildAddressSearch(searchField, searchValueCurrent);
			}
		}
		return temp;
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
	private Filter buildAddressSearch(String searchField, String searchValue) {
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

		// it has to be at least one character between the " e.g. "a" for an
		// exact match
		if (searchValue.length() > 2 && searchValue.startsWith(LDAP_EXACT_CARD)
				&& searchValue.endsWith(LDAP_EXACT_CARD)) {
			exactMatch = true;
		}

		return exactMatch;
	}
}
