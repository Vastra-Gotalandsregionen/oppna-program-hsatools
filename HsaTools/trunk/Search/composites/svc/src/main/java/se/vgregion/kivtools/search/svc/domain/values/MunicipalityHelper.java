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
package se.vgregion.kivtools.search.svc.domain.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;


public class MunicipalityHelper {

	public static List<Municipality> allMunicipalities;
	
	private static final String MUNICIPALITY_CODE_KEY = "kivtools.search.svc.impl.municipalitycode";
	private static final String MUNICIPALITY_NAME_KEY = "kivtools.search.svc.impl.municipalityname";

	private String implResourcePath;
	
	public List<Municipality> getAllMunicipalities() {
        return allMunicipalities;
    }
	
	private Enumeration<String> getAllConfigPars() {
		ResourceBundle bundle = ResourceBundle.getBundle(getImplResourcePath());
		return bundle.getKeys();
	}
	
	private String getConfigParByKey(String key) {
		String rv = "";
		ResourceBundle bundle = ResourceBundle.getBundle(getImplResourcePath());
		String value = bundle.getString(key);
		if (value != null)
			rv = value;
		return rv;
	}

	public String getImplResourcePath() {
		return implResourcePath;
	}

	public void setImplResourcePath(String implResourcePath)  {
		this.implResourcePath = implResourcePath;
		
        Enumeration<String> allKeys = getAllConfigPars();
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
        		currentName = getConfigParByKey(MUNICIPALITY_NAME_KEY + "_" + currentIndex);
        		currentMunicipality = new Municipality();
        		currentMunicipality.setMunicipalityKey(currentKey);
        		currentMunicipality.setMunicipalityName(currentName);
        		String code = getConfigParByKey(MUNICIPALITY_CODE_KEY + "_" + currentIndex);
        		currentMunicipality.setMunicipalityCode(code);
        		if (allMunicipalities == null) {
        			allMunicipalities = new ArrayList<Municipality>();
        		}
        		allMunicipalities.add(currentMunicipality);
        	}
        }
    	Collections.sort(allMunicipalities);
	}	
}
