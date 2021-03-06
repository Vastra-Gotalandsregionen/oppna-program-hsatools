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
/**
 * 
 */
package se.vgregion.kivtools.search.presentation.forms;

import java.io.Serializable;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 */
@SuppressWarnings("serial")
public class UnitSearchSimpleForm implements Serializable {
    private String unitName="";
    private String searchParamValue="";
    
    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getSearchParamValue() {
        return searchParamValue;
    }
    public void setSearchParamValue(String searchParamValue) {
        this.searchParamValue = searchParamValue;
    }

    public boolean isEmpty() {
        if ((unitName==null) && (searchParamValue==null)) {
            return true;
        }
        if ( (unitName.trim().length() == 0) && (searchParamValue.trim().length()==0) ) {
            return true;
        }
        return false;
    }
}
