package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.LinkedList;

import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPSearchResultsMock extends LDAPSearchResults {

  private LinkedList<LDAPEntryMock> entryMocks = new LinkedList<LDAPEntryMock>();
  private LDAPException ldapException;

  public void addLDAPEntry(LDAPEntryMock ldapEntry) {
    entryMocks.add(ldapEntry);
  }

  @Override
  public LDAPEntry next() throws LDAPException {
    LDAPEntry entry = null;
    if (entryMocks != null) {
      entry = entryMocks.removeFirst();
      if (this.ldapException != null) {
        throw this.ldapException;
      }
    }
    return entry;
  }

  @Override
  public boolean hasMore() {
    if (entryMocks != null) {
      return entryMocks.size() > 0;
    } else {
      return false;
    }
  }

  public void setLdapException(LDAPException ldapException) {
    this.ldapException = ldapException;
  }
}
