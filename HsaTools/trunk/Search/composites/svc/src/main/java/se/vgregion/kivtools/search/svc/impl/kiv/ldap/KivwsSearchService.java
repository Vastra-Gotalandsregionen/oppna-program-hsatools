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
import java.util.List;

import javax.naming.Name;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfFunction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfString;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionDirectory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebService;
import se.vgregion.kivtools.util.Arguments;

public class KivwsSearchService implements SearchService {

  private VGRegionWebService vgregionWebService;
  private KivwsUnitMapper kivwsUnitMapper;
  private final Log logger = LogFactory.getLog(KivwsSearchService.class);

  public void setKivwsUnitMapper(KivwsUnitMapper kivwsUnitMapper) {
    this.kivwsUnitMapper = kivwsUnitMapper;
  }

  public void setVgregionWebService(VGRegionWebService vgregionWebService) {
    this.vgregionWebService = vgregionWebService;
  }

  @Override
  public Unit lookupUnit(Name name, List<String> attrs) {
    Arguments.notNull("name", name);
    Arguments.notNull("attrs", attrs);

    Unit unit = null;
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);

    try {
      ArrayOfUnit searchUnit = vgregionWebService.searchUnit(name.get(name.size() - 1), arrayOfString, VGRegionDirectory.KIV, null, null);
      unit = mapKivwsUnitToUnit(searchUnit).get(0);
    } catch (VGRException_Exception e) {
      logger.error(e.getMessage(), e);
      unit = new Unit();
    }

    return unit;
  }

  @Override
  public List<Unit> searchFunctionUnits(Name base, String filter, int searchScope, List<String> attrs) {
    List<Unit> resultUnits = new ArrayList<Unit>();
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);
    try {
      ArrayOfFunction searchFunction = vgregionWebService.searchFunction(filter, arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(searchScope));
      resultUnits.addAll(this.mapKivwsUnits(searchFunction));
    } catch (VGRException_Exception e) {
      logger.error(e.getMessage(), e);
    }
    return resultUnits;
  }

  @Override
  public List<String> searchSingleAttribute(Name base, String filter, int searchScope, List<String> attrs, String mappingAttribute) {
    List<String> result = new ArrayList<String>();
    SingleAttributeMapper singleAttributeMaper = new SingleAttributeMapper(mappingAttribute);
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);
    try {
      // Get all functions and map.
      ArrayOfFunction searchFunction = vgregionWebService.searchFunction(filter, arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(searchScope));
      ArrayOfUnit searchUnits = vgregionWebService.searchUnit(filter, arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(searchScope));
      List<Function> functions = searchFunction.getFunction();
      List<se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit> units = searchUnits.getUnit();

      for (Function function : functions) {
        result.add(singleAttributeMaper.mapFromContext(function));
      }

      for (se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit unit : units) {
        result.add(singleAttributeMaper.mapFromContext(unit));
      }
    } catch (VGRException_Exception e) {
      logger.error(e.getMessage(), e);
    }

    return result;
  }

  @Override
  public List<Unit> searchUnits(Name base, String filter, int searchScope, List<String> attrs) {
    List<Unit> result = null;
    ArrayOfString arrayOfString = new ArrayOfString();
    arrayOfString.getString().addAll(attrs);
    try {
      ArrayOfUnit searchUnit = vgregionWebService.searchUnit(filter, arrayOfString, VGRegionDirectory.KIV, base.toString(), Integer.toString(searchScope));
      result = mapKivwsUnits(searchUnit);
    } catch (VGRException_Exception e) {
      logger.error(e.getMessage(), e);
      result = new ArrayList<Unit>();
    }
    return result;
  }

  private List<Unit> mapKivwsUnits(Object object) {
    List<Unit> result = null;
    if (object instanceof ArrayOfFunction) {
      result = mapKivwsUnitFunctionToUnit((ArrayOfFunction) object);
    } else {
      result = mapKivwsUnitToUnit((ArrayOfUnit) object);
    }
    return result;
  }

  private List<Unit> mapKivwsUnitToUnit(ArrayOfUnit arrayOfUnit) {
    List<se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit> unit = arrayOfUnit.getUnit();
    List<Unit> result = new ArrayList<Unit>();
    for (se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit kivwsUnit : unit) {
      result.add(kivwsUnitMapper.mapFromContext(kivwsUnit));
    }
    return result;
  }

  private List<Unit> mapKivwsUnitFunctionToUnit(ArrayOfFunction arrayOfFunction) {
    List<Unit> result = new ArrayList<Unit>();
    List<Function> function = arrayOfFunction.getFunction();
    for (Function function2 : function) {
      result.add(kivwsUnitMapper.mapFromContext(function2));
    }
    return result;
  }
}
