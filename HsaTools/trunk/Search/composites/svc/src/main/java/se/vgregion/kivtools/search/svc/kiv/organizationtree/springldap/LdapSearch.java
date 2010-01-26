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
package se.vgregion.kivtools.search.svc.kiv.organizationtree.springldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.interfaces.UnitComposition;
import se.vgregion.kivtools.search.svc.kiv.organizationtree.VgrUnitComposition;
import se.vgregion.kivtools.util.StringUtil;

/**
 * 
 * @author david
 * 
 */
public class LdapSearch {

    /**
     * @param dirContext
     *            DirContext to use in search
     * @param filter
     *            Ldap filter
     * @param searchScope
     *            SearchControls scope
     * @param treeLevel
     *            Limit deapth of ldapTree
     * @return {@link List}
     */
    public static List<UnitComposition<Unit>> search(DirContext dirContext, String filter, int treeLevel) {

        SearchControls controls = new SearchControls();
        if (treeLevel > 1) {
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        }
        List<UnitComposition<Unit>> unitCompositions = new ArrayList<UnitComposition<Unit>>();

        try {
            NamingEnumeration<SearchResult> results = dirContext.search("", "(objectclass=*)", controls);

            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();
                Unit unit = new Unit();
                unit.setHsaIdentity(getAttributeValue(attributes, "hsaIdentity"));
                unit.setName(getUnitName(attributes));
                // Set street address
                List<String> streetAddressAttr = getAddressAttributes(getAttributeValue(attributes,
                        "hsaStreetAddress"));
                unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(streetAddressAttr));
                // Set postal address
                List<String> postalAddressAttr = getAddressAttributes(getAttributeValue(attributes,
                        "hsaPostalAddress"));
                unit.setHsaPostalAddress(AddressHelper.convertToAddress(postalAddressAttr));

                UnitComposition<Unit> unitComposition = new VgrUnitComposition(searchResult.getName(), unit);

                if (!filterOutSearchResult(searchResult.getName(), treeLevel)) {
                    unitCompositions.add(unitComposition);
                }
            }
        } catch (NamingException e) {
            // do nothing
        }
        return unitCompositions;
    }

    private static String getAttributeValue(Attributes attributes, String attrId) {
        String value = "";

        if (attributes != null) {
            try {
                Attribute attribute = attributes.get(attrId);
                if (attribute != null) {
                    value = (String) attribute.get();
                }
            } catch (NamingException e) {
                // do nothing
            }
        }
        return value;
    }

    private static boolean filterOutSearchResult(String name, int treeLimitLevel) {
        DistinguishedName distinguishedName = new DistinguishedName(name);
        return distinguishedName.getNames().size() > treeLimitLevel;
    }

    private static String getUnitName(Attributes attributes) {
        String value = "";

        value = getAttributeValue(attributes, "ou");
        // Is a function, name is the cn attribute instead.
        if (StringUtil.isEmpty(value)) {
            value = getAttributeValue(attributes, "cn");
        }
        return value;
    }

    private static List<String> getAddressAttributes(String attributeValue) {
        List<String> result = null;
        if (!StringUtil.isEmpty(attributeValue)) {
            String[] addressFields = attributeValue.split("\\$");
            result = Arrays.asList(addressFields);
        } else {
            result = new ArrayList<String>();
        }
        return result;
    }
}
