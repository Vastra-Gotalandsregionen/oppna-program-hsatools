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

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;

/**
 * Holds SIK settings for different organizations.
 * 
 * @author David Bennehult, Know IT
 * @author Jonas Liljenfeldt, Know IT
 */
public class SettingsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Properties settings = new Properties();

	public SettingsBean(Resource resource) {
		try {
			settings.load(resource.getInputStream());
		} catch (IOException e) {
			// Set default properties?
			e.printStackTrace();
		}
	}

	public Map<Object, Object> getSettings() {
		return (Map<Object,Object>) settings;
	}
}