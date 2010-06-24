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

package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.KivwsCodeNameTableMapper;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebService;

/**
 * Class that handles code, text pairing of ldap values.
 * 
 * @author David & Nhi
 */
public class KivwsCodeTablesServiceImpl implements CodeTablesService {

  private Map<KivwsCodeTableName, Map<String, String>> codeTables = new ConcurrentHashMap<KivwsCodeTableName, Map<String, String>>();

  private final VGRegionWebService vgregionWebService;

  /**
   * Constructs a new KivwsCodeTablesServiceImpl.
   * 
   * @param vgregionWebService The VGR web service to use.
   */
  public KivwsCodeTablesServiceImpl(VGRegionWebService vgregionWebService) {
    this.vgregionWebService = vgregionWebService;
  }

  /**
   * Initializes the code table service.
   */
  public void init() {
    codeTables.clear();
    for (KivwsCodeTableName codeTableName : KivwsCodeTableName.values()) {
      try {
        populateCodeTablesMap(codeTableName);
      } catch (KivException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  private void populateCodeTablesMap(KivwsCodeTableName codeTableName) throws KivException {
    KivwsCodeNameTableMapper codeTableMapper = new KivwsCodeNameTableMapper();
    try {
      String codeNameString = codeTableName.toString();
      String2StringMap attributeCodesAndCleartexts = vgregionWebService.getAttributeCodesAndCleartexts(codeNameString);
      codeTableMapper.mapFromContext(attributeCodesAndCleartexts);
      codeTables.put(codeTableName, codeTableMapper.getCodeTableContent());
    } catch (VGRException_Exception e) {
      throw new KivException(e.getMessage());
    }
  }

  @Override
  public String getValueFromCode(CodeTableNameInterface codeTableName, String code) {
    KivwsCodeTableName kivwsCodeTableName = null;

    if (codeTableName instanceof KivwsCodeTableName) {
      kivwsCodeTableName = (KivwsCodeTableName) codeTableName;

    } else {
      throw new RuntimeException("Object codeTableName is not a type of KivwsCodeTableName");
    }

    Map<String, String> chosenCodeTable = codeTables.get(kivwsCodeTableName);
    String value = "";
    if (chosenCodeTable != null) {
      value = chosenCodeTable.get(code);
    }
    return value;
  }

  @Override
  public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
    KivwsCodeTableName kivwsCodeTableName = null;

    if (codeTableName instanceof KivwsCodeTableName) {
      kivwsCodeTableName = (KivwsCodeTableName) codeTableName;

    } else {
      throw new RuntimeException("Object codeTableName is not a type of KivwsCodeTableName");
    }

    String stringToMatch = textValue.toLowerCase();
    List<String> codes = new ArrayList<String>();
    Map<String, String> chosenCodeTable = codeTables.get(kivwsCodeTableName);
    for (Entry<String, String> entry : chosenCodeTable.entrySet()) {
      if (entry.getValue().toLowerCase().contains(stringToMatch)) {
        codes.add(entry.getKey());
      }
    }
    return codes;
  }

  @Override
  public List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
    KivwsCodeTableName kivwsCodeTableName = null;
    if (codeTableName instanceof KivwsCodeTableName) {
      kivwsCodeTableName = (KivwsCodeTableName) codeTableName;

    } else {
      throw new RuntimeException("Object codeTableName is not a type of KivwsCodeTableName");
    }

    String stringToMatch = textValue.toLowerCase();
    List<String> values = new ArrayList<String>();
    Map<String, String> chosenCodeTable = codeTables.get(kivwsCodeTableName);
    for (String value : chosenCodeTable.values()) {
      if (value.toLowerCase().contains(stringToMatch)) {
        values.add(value);
      }
    }
    return values;
  }

  @Override
  public List<String> getAllValuesItemsFromCodeTable(String codeTable) {
    return new ArrayList<String>(codeTables.get(KivwsCodeTableName.valueOf(codeTable)).values());
  }

}
