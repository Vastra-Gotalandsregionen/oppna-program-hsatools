package se.vgregion.kivtools.search.svc.impl.mock;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPConnectionMock extends LDAPConnection {

	@Override
	public LDAPSearchResults search(String base, int scope, String filter, String[] attrs, boolean typesOnly) throws LDAPException {
		LDAPSearchResults results = new TestLDAPSearchResults();
		return results;
	}

	class TestLDAPSearchResults extends LDAPSearchResults {

		@Override
		public LDAPEntry next() throws LDAPException {
			LDAPEntryMock entryMock = new LDAPEntryMock();
			entryMock.addAttribute("description", "1;Landsting/Region");
			return entryMock;

		}
	}
}
