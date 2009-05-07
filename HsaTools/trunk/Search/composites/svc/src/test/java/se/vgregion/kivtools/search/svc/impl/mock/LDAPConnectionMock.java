package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchQueue;
import com.novell.ldap.LDAPSearchResults;

public class LDAPConnectionMock extends LDAPConnection {

	Map<SearchCondition, LinkedList<LDAPEntryMock>> availableSearchEntries = new HashMap<SearchCondition, LinkedList<LDAPEntryMock>>();

	public Map<SearchCondition, LinkedList<LDAPEntryMock>> getAvailableSearchEntries() {
		return availableSearchEntries;
	}

	public void addLdapEntries(SearchCondition searchCondition, LinkedList<LDAPEntryMock> ldapEntries) {
		availableSearchEntries.put(searchCondition, ldapEntries);
	}

	@Override
	public LDAPSearchResults search(String base, int scope, String filter, String[] attrs, boolean typesOnly) throws LDAPException {
		return search(base, scope, filter, attrs, typesOnly, new LDAPSearchConstraints());
	}

	@Override
	public LDAPSearchResults search(String base, int scope, String filter, String[] attrs, boolean typesOnly, LDAPSearchConstraints constraints) throws LDAPException {
		SearchCondition searchCondition = new SearchCondition(base, scope, filter);
		LDAPSearchResults results = new TestLDAPSearchResults(searchCondition);
		return results;
	}

	@Override
	public LDAPSearchQueue search(String arg0, int arg1, String arg2, String[] arg3, boolean arg4, LDAPSearchQueue arg5, LDAPSearchConstraints arg6) throws LDAPException {
		// TODO Auto-generated method stub
		return super.search(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public LDAPSearchQueue search(String base, int scope, String filter, String[] attrs, boolean typesOnly, LDAPSearchQueue queue) throws LDAPException {
		// TODO Auto-generated method stub
		return super.search(base, scope, filter, attrs, typesOnly, queue);
	}

	class TestLDAPSearchResults extends LDAPSearchResults {

		private SearchCondition searchCondition;

		public TestLDAPSearchResults(SearchCondition searchCondition) {
			this.searchCondition = searchCondition;
		}

		@Override
		public LDAPEntry next() throws LDAPException {
			LinkedList<LDAPEntryMock> ldapEntries = availableSearchEntries.get(searchCondition);
			return ldapEntries != null ? ldapEntries.removeFirst() : null;
		}

		@Override
		public boolean hasMore() {
			LinkedList<LDAPEntryMock> ldapEntries = availableSearchEntries.get(searchCondition);
			if (ldapEntries != null) {
				return ldapEntries.size() > 0;
			} else {
				return false;
			}
		}
	}
}
