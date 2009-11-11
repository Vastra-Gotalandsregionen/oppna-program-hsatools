package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.svc.UnitNameCache;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

public class UnitNameCacheLoaderImplTest {
  private UnitNameCacheLoaderImpl unitNameCacheLoaderImpl;
  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    ldapTemplate = new LdapTemplateMock();
    unitNameCacheLoaderImpl = new UnitNameCacheLoaderImpl();
    unitNameCacheLoaderImpl.setLdapTemplate(ldapTemplate);
  }

  @Test
  public void testInstantiation() {
    UnitNameCacheLoaderImpl unitNameCacheLoaderImpl = new UnitNameCacheLoaderImpl();
    assertNotNull(unitNameCacheLoaderImpl);
  }

  @Test
  public void testLoadCache() {
    DirContextOperationsMock unit1 = new DirContextOperationsMock();
    unit1.addAttributeValue("ou", "Vårdcentralen Hylte");
    DirContextOperationsMock unit2 = new DirContextOperationsMock();
    unit2.addAttributeValue("cn", "Tandregleringen\\, Varberg");
    this.ldapTemplate.addDirContextOperationForSearch(unit1);
    this.ldapTemplate.addDirContextOperationForSearch(unit2);

    UnitNameCache unitNameCache = unitNameCacheLoaderImpl.loadCache();

    this.ldapTemplate.assertSearchFilter("(|(objectclass=organizationalUnit)(objectclass=organizationalRole))");

    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("le");
    assertEquals(2, matchingUnitNames.size());
    assertTrue(matchingUnitNames.contains("Vårdcentralen Hylte"));
    assertTrue(matchingUnitNames.contains("Tandregleringen, Varberg"));
  }

  private static class LdapTemplateMock extends LdapTemplate {
    private String filter;
    private List<DirContextOperations> dirContextOperations = new ArrayList<DirContextOperations>();

    public void addDirContextOperationForSearch(DirContextOperations dirContextOperations) {
      this.dirContextOperations.add(dirContextOperations);
    }

    public void assertSearchFilter(String expectedFilter) {
      assertEquals(expectedFilter, this.filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List search(String base, String filter, SearchControls searchControls, ContextMapper mapper, DirContextProcessor dirContextProcessor) {
      this.filter = filter;
      List result = new ArrayList();
      for (DirContextOperations dirContextOperations : this.dirContextOperations) {
        result.add(mapper.mapFromContext(dirContextOperations));
      }
      // Use ReflectionUtil since there is no set-method for cookie.
      ReflectionUtil.setField(dirContextProcessor, "cookie", new PagedResultsCookie(null));
      return result;
    }
  }
}
