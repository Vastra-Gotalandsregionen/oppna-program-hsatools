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
