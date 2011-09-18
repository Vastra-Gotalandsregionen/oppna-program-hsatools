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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.BadLdapGrammarException;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.vgr.EniroUnitMapperVGR;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.UnitFetcher;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;

public class UnitFetcherVGR implements UnitFetcher {
  private final Log logger = LogFactory.getLog(this.getClass());
  private final SearchService searchService;
  private final String[] allowedUnitBusinessClassificationCodes;
  private final String[] otherCareTypeBusinessCodes;

  public UnitFetcherVGR(SearchService searchService, String[] allowedUnitBusinessClassificationCodes, String[] otherCareTypeBusinessCodes) {
    this.searchService = searchService;
    this.allowedUnitBusinessClassificationCodes = allowedUnitBusinessClassificationCodes;
    this.otherCareTypeBusinessCodes = otherCareTypeBusinessCodes;
  }

  @Override
  public List<UnitComposition> fetchUnits(List<String> municipalities, String locality) {
    try {
      List<Unit> allUnits = this.searchService.getAllUnits(true);
      List<Unit> filteredUnits = this.filterUnits(allUnits, municipalities, Arrays.asList(this.allowedUnitBusinessClassificationCodes));

      List<UnitComposition> unitsList = this.mapUnits(filteredUnits, locality);
      this.setParentIdsForUnits(unitsList);
      return unitsList;
    } catch (KivException e) {
      throw new RuntimeException("Unable to fetch units", e);
    }
  }

  private List<UnitComposition> mapUnits(List<Unit> filteredUnits, String locality) {
    EniroUnitMapperVGR eniroUnitMapper = new EniroUnitMapperVGR(locality, Arrays.asList(this.otherCareTypeBusinessCodes));
    List<UnitComposition> mappedUnits = new ArrayList<UnitComposition>();

    for (Unit unit : filteredUnits) {
      mappedUnits.add(eniroUnitMapper.map(unit));
    }
    return mappedUnits;
  }

  private List<Unit> filterUnits(List<Unit> allUnits, List<String> municipalities, List<String> businessClassificationCodes) {
    List<Unit> filteredUnits = new ArrayList<Unit>();
    for (Unit unit : allUnits) {
      if (municipalities.contains(unit.getHsaMunicipalityCode()) && this.hasAnyBusinessClassificationCode(businessClassificationCodes, unit.getHsaBusinessClassificationCode())) {
        filteredUnits.add(unit);
      }
    }
    return filteredUnits;
  }

  private boolean hasAnyBusinessClassificationCode(List<String> businessClassificationCodes, List<String> unitBusinessClassificationCode) {
    for (String code : unitBusinessClassificationCode) {
      if (businessClassificationCodes.contains(code)) {
        return true;
      }
    }
    return false;
  }

  private void setParentIdsForUnits(List<UnitComposition> compositions) {
    Map<String, UnitComposition> unitsMap = new HashMap<String, UnitComposition>();
    for (UnitComposition unitComposition : compositions) {
      unitsMap.put(unitComposition.getDn(), unitComposition);
    }
    for (UnitComposition unitComposition : compositions) {
      try {
        UnitComposition parentUnit = unitsMap.get(unitComposition.getParentDn());
        if (parentUnit != null) {
          unitComposition.getEniroUnit().setParentUnitId(parentUnit.getEniroUnit().getId());
        }
      } catch (BadLdapGrammarException e) {
        this.logger.debug(unitComposition.getDn());
        throw e;
      }
    }
  }
}
