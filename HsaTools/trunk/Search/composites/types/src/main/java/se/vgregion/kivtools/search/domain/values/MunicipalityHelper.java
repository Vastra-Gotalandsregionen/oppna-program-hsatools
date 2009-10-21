/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.search.domain.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Helper class for municipality operations.
 */
public class MunicipalityHelper {
  private static final String MUNICIPALITY_CODE_KEY = "hsatools.search.svc.impl.municipalitycode";
  private static final String MUNICIPALITY_NAME_KEY = "hsatools.search.svc.impl.municipalityname";

  private static final List<Municipality> ALL_MUNICIPALITIES = new ArrayList<Municipality>();
  private static boolean initialized;

  public List<Municipality> getAllMunicipalities() {
    return ALL_MUNICIPALITIES;
  }

  private Enumeration<String> getAllConfigPars(String implResourcePath) {
    ResourceBundle bundle = ResourceBundle.getBundle(implResourcePath);
    return bundle.getKeys();
  }

  private String getConfigParByKey(String implResourcePath, String key) {
    String rv = "";
    ResourceBundle bundle = ResourceBundle.getBundle(implResourcePath);
    String value = bundle.getString(key);
    if (value != null) {
      rv = value;
    }
    return rv;
  }

  /**
   * Setter for the package-path to the properties-file to read municipalities from.
   * 
   * @param implResourcePath The package-path to the properties-file to read municipalities from.
   */
  public synchronized void setImplResourcePath(String implResourcePath) {
    if (!initialized) {
      Enumeration<String> allKeys = getAllConfigPars(implResourcePath);
      String currentKey;
      String currentName;
      Municipality currentMunicipality;
      int currentIndex;
      int beginPos;
      while (allKeys.hasMoreElements()) {
        currentKey = allKeys.nextElement();
        if (currentKey.startsWith(MUNICIPALITY_CODE_KEY)) {
          beginPos = currentKey.indexOf('_');
          currentIndex = Integer.parseInt(currentKey.substring(beginPos + 1));
          currentName = getConfigParByKey(implResourcePath, MUNICIPALITY_NAME_KEY + "_" + currentIndex);
          currentMunicipality = new Municipality();
          currentMunicipality.setMunicipalityKey(currentKey);
          currentMunicipality.setMunicipalityName(currentName);
          String code = getConfigParByKey(implResourcePath, MUNICIPALITY_CODE_KEY + "_" + currentIndex);
          currentMunicipality.setMunicipalityCode(code);
          ALL_MUNICIPALITIES.add(currentMunicipality);
        }
      }
      Collections.sort(ALL_MUNICIPALITIES);
      initialized = true;
    }
  }
}
