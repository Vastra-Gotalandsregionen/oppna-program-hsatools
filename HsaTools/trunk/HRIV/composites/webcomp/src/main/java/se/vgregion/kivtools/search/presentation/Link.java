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
package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;

/**
 * Hyperlink representation.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class Link implements Serializable {
	private static final long serialVersionUID = 1252630862103552549L;
	private String href;
	private String name;
	private String toParamName;

	public Link(String href, String name, String toParamName) {
		super();
		this.href = href;
		this.name = name;
		this.toParamName = toParamName;
	}

	public String getToParamName() {
		return toParamName;
	}

	public void setToParamName(String toParamName) {
		this.toParamName = toParamName;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
