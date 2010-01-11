package se.vgregion.kivtools.search.svc.kiv.organizationtree.springldap;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.interfaces.UnitComposition;

public class VgrOrganizationFactoryTest {

    private VgrOrganizationTreeFactory vgrOrganizationFactory;

    @Before
    public void setUp() throws Exception {
        vgrOrganizationFactory = new VgrOrganizationTreeFactory();
        vgrOrganizationFactory.setLdapTemplate(new MockLdapTemplet());
    }

    @Test
    public void testCreateVgrOrganizationTree() {
        assertNotNull("Was null", vgrOrganizationFactory.createVgrOrganizationTree(new ArrayList<UnitComposition<Unit>>()));
    }
    
    class MockLdapTemplet extends LdapTemplate {
        @Override
        public List search(String base, String filter, int searchScope, ContextMapper mapper) {
            return new ArrayList<UnitComposition<Unit>>();
        }
    }

}
