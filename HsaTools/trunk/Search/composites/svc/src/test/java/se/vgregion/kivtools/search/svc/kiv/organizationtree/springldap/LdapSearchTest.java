package se.vgregion.kivtools.search.svc.kiv.organizationtree.springldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.interfaces.UnitComposition;

public class LdapSearchTest {

    private LdapSearch ldapSearch;
    private MockDirContext mockDirContext;
    private static String HSA_POSTAL_ADDRESS = "Vårdcentral Majorna$ $Skärgårdsgatan 4$ $414 58$Göteborg";
    private static String HSA_STREET_ADDRESS = "Skärgårdsgatan 4$414 58  Göteborg";

    @Before
    public void setUp() throws Exception {
        List<SearchResult> searchResultList = new ArrayList<SearchResult>();

        // Create attributes
        BasicAttributes basicAttributes1 = new BasicAttributes();
        basicAttributes1.put(new BasicAttribute("hsaIdentity", "id1"));
        basicAttributes1.put(new BasicAttribute("cn", "Name from cn"));
        basicAttributes1.put(new BasicAttribute("hsaStreetAddress", HSA_STREET_ADDRESS));
        basicAttributes1.put(new BasicAttribute("hsaPostalAddress", HSA_POSTAL_ADDRESS));

        // Create attributes
        BasicAttributes basicAttributes2 = new BasicAttributes();
        basicAttributes2.put(new BasicAttribute("hsaIdentity", "id2"));
        basicAttributes2.put(new BasicAttribute("ou", "Name from ou"));

        SearchResult searchResult1 = new SearchResult("ou=root,ou=child1", "", basicAttributes1);
        SearchResult searchResult2 = new SearchResult("ou=root,ou=child2", "", basicAttributes2);
        SearchResult searchResult3 = new SearchResult("ou=root,ou=child1,ou=leaf", "", basicAttributes1);
        // Add attributes to list
        searchResultList.add(searchResult1);
        searchResultList.add(searchResult2);
        searchResultList.add(searchResult3);
        mockDirContext = new MockDirContext(searchResultList);
        ldapSearch = new LdapSearch();
    }

    @Test
    public void testSearch() {
        List<UnitComposition<Unit>> search = ldapSearch.search(mockDirContext, "(objectClass=*)", 2);
        assertNotNull(search);
        UnitComposition<Unit> unitComposition = search.get(0);
        assertEquals("ou=root,ou=child1", unitComposition.getDn());
        assertEquals("Name from cn", unitComposition.getUnit().getName());
        assertEquals("Skärgårdsgatan 4", unitComposition.getUnit().getHsaStreetAddress().getStreet());
        assertEquals(AddressHelper.convertToStreetAddress(Arrays.asList(HSA_STREET_ADDRESS.split("\\$")))
                .getStreet(), unitComposition.getUnit().getHsaStreetAddress().getStreet());
        assertEquals(AddressHelper.convertToAddress(Arrays.asList(HSA_POSTAL_ADDRESS.split("\\$")))
                .getAdditionalInfo(), unitComposition.getUnit().getHsaPostalAddress().getAdditionalInfo());
    }

    @Test
    public void testNamingException() {
        // Check that namingException from serch is handled
        mockDirContext = new MockDirContext(new ArrayList<SearchResult>());
        mockDirContext.setThrowNamingException(true);
        List<UnitComposition<Unit>> search = ldapSearch.search(mockDirContext, "(objectClass=*)", 2);
        assertNotNull(search);

        // Check that namingException from attributes is handled.
        BasicAttributes basicAttributes = new BasicAttributes();
        basicAttributes.put(new MockAttriuteException("hsaIdentity"));
        SearchResult searchResult = new SearchResult("ou=root,ou=child1", "", basicAttributes);

        mockDirContext = new MockDirContext(Arrays.asList(searchResult));

        search = ldapSearch.search(mockDirContext, "(objectClass=*)", 1);
        assertNotNull(search);

    }

    class MockAttriuteException extends BasicAttribute {
        public MockAttriuteException(String attrId) {
            super(attrId, "");
        }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Object get() throws NamingException {
            throw new NamingException();
        }

    }
}
