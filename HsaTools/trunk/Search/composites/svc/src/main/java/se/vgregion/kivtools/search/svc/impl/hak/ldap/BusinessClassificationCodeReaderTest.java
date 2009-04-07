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

import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;

public class BusinessClassificationCodeReaderTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		HealthcareTypeConditionHelper ctr = new HealthcareTypeConditionHelper();
		ctr.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.hak.ldap.search-composite-svc-impl");
		
		try {
			/* TODO Write new test for new class HealthcareTypeConditionHelper
			 * 
			List<HealthcareType> careTypes = ctr.getAllBusinessClassifications();
			for (Iterator i = careTypes.iterator(); i.hasNext(); ) {
				System.out.println("-- New caretype --");
				HealthcareType careType = (HealthcareType) i.next();
				System.out.println(careType.getBusinessClassificationKey());
				System.out.println(careType.getBusinessClassificationName());
				for (Iterator j = careType.getBusinessClassificationCodes().iterator(); j.hasNext(); ) {
					System.out.println( j.next());
				}
			}
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
