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
