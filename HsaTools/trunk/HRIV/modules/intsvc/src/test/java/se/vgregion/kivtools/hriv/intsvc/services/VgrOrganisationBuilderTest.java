package se.vgregion.kivtools.hriv.intsvc.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import se.vgregion.kivtools.hriv.intsvc.services.organization.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.services.organization.VgrOrganisationBuilder;
import se.vgregion.kivtools.hriv.intsvc.services.organization.VgrUnitComposition;
import se.vgregion.kivtools.hriv.intsvc.vgr.domain.Organization;
import se.vgregion.kivtools.hriv.intsvc.vgr.domain.Unit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:services-config.xml"})
public class VgrOrganisationBuilderTest {

    private VgrOrganisationBuilder vgrOrganisationBuilder;
    private List<UnitComposition<Unit>> vgrUnitCompositions;

    @Autowired
    private LdapTemplate ldapTemplate;
    
    @Before
    public void setUp() throws Exception {
        vgrOrganisationBuilder = new VgrOrganisationBuilder();
        
        
        Unit unit1 = new Unit();
        unit1.setUnitId("unit1");
        VgrUnitComposition vgrUnitComposition1 = new VgrUnitComposition("ou=node1", unit1);
        
        Unit unit2 = new Unit();
        unit2.setUnitId("unit2");
        VgrUnitComposition vgrUnitComposition2 = new VgrUnitComposition("ou=node2, ou=node1", unit2);
        

        Unit unit3 = new Unit();
        unit3.setUnitId("unit3");
        VgrUnitComposition vgrUnitComposition3 = new VgrUnitComposition("ou=node3", unit3);
        
        Unit unit4 = new Unit();
        unit4.setUnitId("unit4");
        VgrUnitComposition vgrUnitComposition4 = new VgrUnitComposition("ou=node4, ou=node3", unit4);
        
        Unit unit5 = new Unit();
        unit5.setUnitId("unit5");
        VgrUnitComposition vgrUnitComposition5 = new VgrUnitComposition("ou=node5, ou=node2, ou=node1", unit5);
        
        vgrUnitCompositions = new ArrayList<UnitComposition<Unit>>();
        vgrUnitCompositions.add(vgrUnitComposition2);
        vgrUnitCompositions.add(vgrUnitComposition5);
        vgrUnitCompositions.add(vgrUnitComposition3);
        vgrUnitCompositions.add(vgrUnitComposition1);
        vgrUnitCompositions.add(vgrUnitComposition4);
    }

    @Test
    public void testGenerateOrganisation() {
        AndFilter andFilter = new AndFilter();
        andFilter.and(new EqualsFilter("objectClass","vgrOrganizationalUnit"));
        //List<UnitComposition<Unit>> unitsList = ldapTemplate.search("", andFilter.encode(), SearchControls.SUBTREE_SCOPE, new VgrUnitMapper());
        Organization generateOrganisation = vgrOrganisationBuilder.generateOrganisation(vgrUnitCompositions);
        assertNotNull(generateOrganisation);
    }

}
