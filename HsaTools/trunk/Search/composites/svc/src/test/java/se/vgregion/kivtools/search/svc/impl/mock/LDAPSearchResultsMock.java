package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.LinkedList;

import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPSearchResultsMock extends LDAPSearchResults {
  
  private LinkedList<LDAPEntryMock> entryMocks = new LinkedList<LDAPEntryMock>();
  
  public void addLDAPEntry(LDAPEntryMock ldapEntry){
    entryMocks.add(ldapEntry);
  }
  
  @Override
  public LDAPEntry next() throws LDAPException {
     return entryMocks != null ? entryMocks.removeFirst() : null;
  }

  @Override
  public boolean hasMore() {
    if (entryMocks != null) {
      return entryMocks.size() > 0;
    } else {
      return false;
    }
  }

}
