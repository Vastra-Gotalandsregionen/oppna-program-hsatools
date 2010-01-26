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

import java.util.List;

import javax.naming.directory.DirContext;

import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.interfaces.UnitComposition;
import se.vgregion.kivtools.search.svc.kiv.organizationtree.VgrOrganisationBuilder;

/**
 * 
 * @author David Bennehult & Ulf Carlsson
 * 
 */
public class VgrOrganizationTreeFactory {

    private static final String LDAP_QUERY = "(objectClass=*)";
    private LdapTemplate ldapTemplate;
    private VgrOrganisationBuilder vgrOrganisationBuilder = new VgrOrganisationBuilder();
    private int treeLevelLimit;

    public void setTreeLevelLimit(int treeLevelLimit) {
        this.treeLevelLimit = treeLevelLimit;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * Get organization tree for vgr organization.
     * 
     * @param flatOrganizationList
     *            UnitComposition List to generate organization tree from.
     * @return {@link UnitComposition}
     */
    public UnitComposition<Unit> createVgrOrganizationTree(List<UnitComposition<Unit>> flatOrganizationList) {
        UnitComposition<Unit> vgrOrganization = vgrOrganisationBuilder.generateOrganisation(flatOrganizationList);
        return vgrOrganization;
    }

    /**
     * 
     * @return UnitComposition list of all unit in current organization.
     */
    public List<UnitComposition<Unit>> getFlatOrganizationList() {
        DirContext ctx = ldapTemplate.getContextSource().getReadOnlyContext();
        List<UnitComposition<Unit>> searchResult = LdapSearch.search(ctx, LDAP_QUERY, treeLevelLimit);
        return searchResult;
    }

}
