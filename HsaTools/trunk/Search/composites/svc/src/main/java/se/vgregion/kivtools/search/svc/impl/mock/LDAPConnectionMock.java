package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPConnectionMock extends LDAPConnection {

	Map<String, LinkedList<LDAPEntryMock>> availableSearchEntries = new HashMap<String, LinkedList<LDAPEntryMock>>();
	LinkedList<LDAPEntryMock> ldapEntries;

	public LinkedList<LDAPEntryMock> getLdapEntries() {
		return ldapEntries;
	}

	public void setLdapEntries(LinkedList<LDAPEntryMock> ldapEntries) {
		this.ldapEntries = ldapEntries;
	}
	
	public void addLdapEntries(String searchFilter, LinkedList<LDAPEntryMock> ldapEntries) {
		availableSearchEntries.put(searchFilter, ldapEntries);
	}

	@Override
	public LDAPSearchResults search(String base, int scope, String filter, String[] attrs, boolean typesOnly) throws LDAPException {
		LDAPSearchResults results = new TestLDAPSearchResults(filter);
		return results;
	}

	class TestLDAPSearchResults extends LDAPSearchResults {
		
		private String searchFilterString;
		public TestLDAPSearchResults(String searchFilterString) {
			this.searchFilterString = searchFilterString;
		}
		@Override
		public LDAPEntry next() throws LDAPException {
			LinkedList<LDAPEntryMock> ldapEntries = availableSearchEntries.get(searchFilterString);
			return ldapEntries != null ? ldapEntries.getFirst() : null;
		}

		@Override
		public boolean hasMore() {
			LinkedList<LDAPEntryMock> ldapEntries = availableSearchEntries.get(searchFilterString);
			return ldapEntries != null ? true: false;
		}
	}
}
