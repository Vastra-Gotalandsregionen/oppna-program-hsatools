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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import se.vgregion.kivtools.search.util.Evaluator;
import se.vgregion.kivtools.search.util.Formatter;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.util.Base64;

/**
 * @author Anders Asplund - KnowIT
 *
 */
public class LdapORMHelper {
    private static String KIV_NEW_LINE_DELIMITER = "$";

    static String getSingleValue(LDAPAttribute attribute) {
        List<String> values = getValues(attribute);
        if (values==null) {
            return "";
        }
        String s = values.get(0);
        if (Evaluator.isEmpty(s)) {
            return "";
        }
        return s;
    }

    static List<String> getMultipleValues(LDAPAttribute attribute) {
        List<String> values = getValues(attribute);
        if (values==null) {
            return new ArrayList<String>();
        }
        return values;
    }
    
    /**
     * 
     * @param allAttributes             An Iterator with all LDAP attributes
     * @param attributeAndValuesMap     Key=attribute name, value=ArrayList with corresponding values
     */
    @SuppressWarnings("unchecked")
    private static List<String> getValues(LDAPAttribute attribute) {
        if(attribute == null) {
            return null;
        }
        
        List<String> values = new ArrayList<String>();

        Enumeration<String> allValues = attribute.getStringValues();
    
        if (allValues != null) {
    
            // while loop goes through all the attribute values
            while (allValues.hasMoreElements()) {
                String value = allValues.nextElement();
                value=value.trim();
                if (Base64.isLDIFSafe(value) && (value.indexOf(KIV_NEW_LINE_DELIMITER)<0)) {
                    values.add(value);
                }
                else {
                    // The parser th-inks that Strings containing a '$' character is 64 encoded
                    // KIV Strings having a $ character ends up here                            
                    values = Formatter.chopUpStringToList(values, value, KIV_NEW_LINE_DELIMITER);
                }
            }
        }
        return values;
    }

}
