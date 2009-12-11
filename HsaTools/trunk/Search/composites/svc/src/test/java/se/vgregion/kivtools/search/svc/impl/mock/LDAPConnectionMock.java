package se.vgregion.kivtools.search.svc.impl.mock;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchQueue;
import com.novell.ldap.LDAPSearchResults;

public class LDAPConnectionMock extends LDAPConnection {
  private String filter;
  private String baseDn;
  private Map<String, LDAPSearchResults> searchResults = new HashMap<String, LDAPSearchResults>();
  private Map<String, LDAPEntry> dnEntryMap = new HashMap<String, LDAPEntry>();
  private final List<String> readEntries = new ArrayList<String>();
  private LDAPException ldapException;
  private boolean returnNullResult;

  public void setLdapException(LDAPException ldapException) {
    this.ldapException = ldapException;
  }

  public void addLDAPSearchResults(String filter, LDAPSearchResultsMock ldapSearchResultsMock) {
    searchResults.put(filter, ldapSearchResultsMock);
  }

  public void addLDAPEntry(String dn, LDAPEntry entry) {
    this.dnEntryMap.put(dn, entry);
  }

  public void setReturnNullResult(boolean returnNullResult) {
    this.returnNullResult = returnNullResult;
  }

  public void assertFilter(String expectedFilter) {
    assertEquals(expectedFilter, filter);
  }

  public void assertBaseDn(String expectedBaseDn) {
    assertEquals(expectedBaseDn, baseDn);
  }

  public void assertEntryRead(String dn) {
    assertTrue("DN " + dn + " was not read", readEntries.contains(dn));
  }

  public void resetReadEntries() {
    readEntries.clear();
  }

  @Override
  public LDAPSearchResults search(String base, int scope, String filter, String[] attrs, boolean typesOnly) throws LDAPException {
    return search(base, scope, filter, attrs, typesOnly, new LDAPSearchConstraints());
  }

  @Override
  public LDAPSearchResults search(String base, int scope, String filter, String[] attrs, boolean typesOnly, LDAPSearchConstraints constraints) throws LDAPException {
    if (ldapException != null) {
      throw ldapException;
    }
    this.filter = filter;
    this.baseDn = base;
    LDAPSearchResults ldapSearchResults = searchResults.get(filter);
    if (ldapSearchResults == null && !returnNullResult) {
      ldapSearchResults = new LDAPSearchResultsMock();
    }
    return ldapSearchResults;
  }

  @Override
  public LDAPSearchQueue search(String arg0, int arg1, String arg2, String[] arg3, boolean arg4, LDAPSearchQueue arg5, LDAPSearchConstraints arg6) throws LDAPException {
    return super.search(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
  }

  @Override
  public LDAPSearchQueue search(String base, int scope, String filter, String[] attrs, boolean typesOnly, LDAPSearchQueue queue) throws LDAPException {
    return super.search(base, scope, filter, attrs, typesOnly, queue);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LDAPEntry read(String dn) throws LDAPException {
    if (ldapException != null) {
      throw ldapException;
    }
    readEntries.add(dn);
    return dnEntryMap.get(dn);
  }
}
