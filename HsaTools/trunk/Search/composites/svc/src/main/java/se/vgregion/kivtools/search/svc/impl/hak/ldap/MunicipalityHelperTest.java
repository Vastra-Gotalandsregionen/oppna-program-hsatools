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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.Iterator;
import java.util.List;

import se.vgregion.kivtools.search.svc.domain.values.Municipality;
import se.vgregion.kivtools.search.svc.domain.values.MunicipalityHelper;

public class MunicipalityHelperTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MunicipalityHelper mh = new MunicipalityHelper();
		mh.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.hak.ldap.search-composite-svc-municipalities");
		
		try {
			List<Municipality> municipalities = mh.getAllMunicipalities();
			for (Iterator<Municipality> i = municipalities.iterator(); i.hasNext(); ) {
				System.out.println("-- New municipality --");
				Municipality m = i.next();
				System.out.println(m.getMunicipalityKey());
				System.out.println(m.getMunicipalityName());
				System.out.println(m.getMunicipalityCode());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
