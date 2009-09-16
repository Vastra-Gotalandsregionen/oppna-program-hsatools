package se.vgregion.kivtools.search.svc.impl.mock;

public class SearchCondition {
	private String base;
	private String filter;
	private int scope;
	
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