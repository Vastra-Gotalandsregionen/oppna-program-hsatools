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
import java.util.Arrays;
import java.util.List;

import se.vgregion.kivtools.util.StringUtil;

public class LdapUtils {

	public static List<String> getListFromArrayAttributes(String[] arrayAttributes) {
		List<String> stringList = null;
		if (arrayAttributes == null) {
			stringList = new ArrayList<String>();
		} else {
			stringList = Arrays.asList(arrayAttributes);
		}
		return stringList;
	}

	/**
	 * 
	 * @param attributeValue
	 *            - A address attribute value with "$" as field separators.
	 * @return List with each field
	 */
	public static List<String> getFormattedAddressList(String attributeValue) {
		List<String> result = new ArrayList<String>();
		if (!StringUtil.isEmpty(attributeValue)) {
			String[] values = attributeValue.split("\\$");
			for (String string : values) {
				result.add(string);
			}
		}
		return result;
	}

}
