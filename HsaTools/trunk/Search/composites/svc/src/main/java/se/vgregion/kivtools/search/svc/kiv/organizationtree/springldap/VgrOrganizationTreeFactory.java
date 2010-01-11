package se.vgregion.kivtools.search.svc.kiv.organizationtree.springldap;

import java.util.List;

import javax.naming.directory.SearchControls;

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
        @SuppressWarnings("unchecked")
        List<UnitComposition<Unit>> searchResult = ldapTemplate.search("", LDAP_QUERY,
                SearchControls.SUBTREE_SCOPE, new VgrUnitMapper());
        return searchResult;
    }

}
