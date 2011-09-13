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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.KivLdapFilterHelper;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.vgr.EniroUnitMapperVGR;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.UnitFetcher;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;

public class UnitFetcherVGR implements UnitFetcher {
  private final LdapTemplate ldapTemplate;
  private final String[] allowedUnitBusinessClassificationCodes;
  private final String[] otherCareTypeBusinessCodes;

  public UnitFetcherVGR(LdapTemplate ldapTemplate, String[] allowedUnitBusinessClassificationCodes, String[] otherCareTypeBusinessCodes) {
    this.ldapTemplate = ldapTemplate;
    this.allowedUnitBusinessClassificationCodes = allowedUnitBusinessClassificationCodes;
    this.otherCareTypeBusinessCodes = otherCareTypeBusinessCodes;
  }

  @Override
  public List<UnitComposition> fetchUnits(List<String> municipalities, String locality) {
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    Filter healthcareTypeFilter = KivLdapFilterHelper.createHealthcareTypeFilter(healthcareTypeConditionHelper.getAllHealthcareTypes());
    AndFilter andFilter = new AndFilter();
    andFilter.and(this.createMunicipalityFilter(municipalities));
    OrFilter orBusinessCodes = new OrFilter();
    for (String businessCode : this.allowedUnitBusinessClassificationCodes) {
      orBusinessCodes.or(new EqualsFilter("hsaBusinessClassificationCode", businessCode));
    }
    orBusinessCodes.or(healthcareTypeFilter);
    andFilter.and(orBusinessCodes);
    EniroUnitMapperVGR eniroUnitMapper = new EniroUnitMapperVGR(locality, Arrays.asList(this.otherCareTypeBusinessCodes));
    @SuppressWarnings("unchecked")
    List<UnitComposition> unitsList = this.ldapTemplate.search("", andFilter.encode(), SearchControls.SUBTREE_SCOPE, eniroUnitMapper);
    this.setParentIdsForUnits(unitsList);
    return unitsList;
  }

  private Filter createMunicipalityFilter(List<String> municipalityCodes) {
    OrFilter filter = new OrFilter();

    for (String municipalityCode : municipalityCodes) {
      filter.or(new EqualsFilter("hsaMunicipalityCode", municipalityCode));
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
