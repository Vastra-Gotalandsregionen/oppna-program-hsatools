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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro.lth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.hriv.intsvc.ws.eniro.KivLdapFilterHelper;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.UnitFetcher;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;

public class UnitFetcherLTH implements UnitFetcher {
  private final LdapTemplate ldapTemplate;
  private final String[] allowedUnitBusinessClassificationCodes;
  private final Log logger = LogFactory.getLog(this.getClass());

  public UnitFetcherLTH(LdapTemplate ldapTemplate, String[] allowedUnitBusinessClassificationCodes) {
    this.ldapTemplate = ldapTemplate;
    this.allowedUnitBusinessClassificationCodes = allowedUnitBusinessClassificationCodes;
  }

  @Override
  public List<UnitComposition> fetchUnits(List<String> municipalities, String locality) {
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    Filter healthcareTypeFilter = KivLdapFilterHelper.createHealthcareTypeFilter(healthcareTypeConditionHelper.getAllHealthcareTypes());
    AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter("hsaDestinationIndicator", "03"));
    andFilter.and(this.createMunicipalityFilter(municipalities));
    OrFilter orBusinessCodes = new OrFilter();
    for (String businessCode : this.allowedUnitBusinessClassificationCodes) {
      orBusinessCodes.or(new EqualsFilter("businessClassificationCode", businessCode));
    }
    orBusinessCodes.or(healthcareTypeFilter);
    andFilter.and(orBusinessCodes);
    EniroUnitMapperLTH eniroUnitMapper = new EniroUnitMapperLTH(locality);
    this.logger.warn(andFilter.encode());
    @SuppressWarnings("unchecked")
    List<UnitComposition> unitsList = this.ldapTemplate.search("OU=Region Halland,DC=lthallandhsa,DC=se", andFilter.encode(), SearchControls.SUBTREE_SCOPE, eniroUnitMapper);
    this.setParentIdsForUnits(unitsList);
    return unitsList;
  }

  private Filter createMunicipalityFilter(List<String> municipalityCodes) {
    OrFilter filter = new OrFilter();

    for (String municipalityCode : municipalityCodes) {
      filter.or(new EqualsFilter("municipalityCode", municipalityCode));
    }
    return filter;
  }

  private void setParentIdsForUnits(List<UnitComposition> compositions) {
    Map<String, UnitComposition> unitsMap = new HashMap<String, UnitComposition>();
    for (UnitComposition unitComposition : compositions) {
      unitsMap.put(unitComposition.getDn(), unitComposition);
    }
    for (UnitComposition unitComposition : compositions) {
      UnitComposition parentUnit = unitsMap.get(unitComposition.getParentDn());
      if (parentUnit != null) {
        unitComposition.getEniroUnit().setParentUnitId(parentUnit.getEniroUnit().getId());
      }
    }
  }
}
