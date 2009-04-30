package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPConnectionMock extends LDAPConnection {

	Map<SearchCondition, LinkedList<LDAPEntryMock>> availableSearchEntries = new HashMap<SearchCondition, LinkedList<LDAPEntryMock>>();
	
	public void addLdapEntries(SearchCondition searchCondition, LinkedList<LDAPEntryMock> ldapEntries) {	
		availableSearchEntries.put(searchCondition, ldapEntries);
	}

	@Override
	public LDAPSearchResults search(String base, int scope, String filter, String[] attrs, boolean typesOnly) throws LDAPException {
		SearchCondition searchCondition = new SearchCondition(base, scope, filter);
		
		LDAPSearchResults results = new TestLDAPSearchResults(searchCondition);
		return results;
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
	
	public class SearchCondition {
		String base;
		String filter;
		int scope;
		
		public SearchCondition(String base, int scope, String filter) {
			this.base = base;
			this.filter = filter;
			this.scope = scope;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((base == null) ? 0 : base.hashCode());
			result = prime * result + ((filter == null) ? 0 : filter.hashCode());
			result = prime * result + scope;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SearchCondition other = (SearchCondition) obj;
			if (base == null) {
				if (other.base != null)
					return false;
			} else if (!base.equals(other.base))
				return false;
			if (filter == null) {
				if (other.filter != null)
					return false;
			} else if (!filter.equals(other.filter))
				return false;
			if (scope != other.scope)
				return false;
			return true;
		}
	}
}
