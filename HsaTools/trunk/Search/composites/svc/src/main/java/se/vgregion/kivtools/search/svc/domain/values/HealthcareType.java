/**
 * Copyright 2009 Västa Götalandsregionen
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
 */
package se.vgregion.kivtools.search.svc.domain.values;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.search.util.Evaluator;

/**
 * Represents a health care type (eg Jourcentral) that can be selected eg when
 * searching for units.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class HealthcareType implements IsEmptyMarker, Serializable,
		Comparable<HealthcareType> {

	public HealthcareType() {
	}

	public HealthcareType(int index) {
		this.index = index;
	}

	public HealthcareType(Map<String, String> conditions, String displayName,
			boolean filtered, Integer index) {
		super();
		this.conditions = conditions;
		this.displayName = displayName;
		this.filtered = filtered;
		this.index = index;
	}

	private static final long serialVersionUID = 1L;

	Map<String, String> conditions = new HashMap<String, String>();
	String displayName;
	Integer index;
	boolean filtered; // Should this care type care about
						// hsaBusinessClassificationCode filters?

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public Map<String, String> getConditions() {
		return conditions;
	}

	public void setConditions(Map<String, String> conditions) {
		this.conditions = conditions;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isEmpty() {
		return Evaluator.isEmpty(displayName);
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getIndex() {
		return index;
	}

	public int compareTo(HealthcareType o) {
		return this.getIndex().compareTo(o.getIndex());
	}

	public void addCondition(String conditionKey, String conditionValue) {
		conditions.put(conditionKey, conditionValue);
	}
}
