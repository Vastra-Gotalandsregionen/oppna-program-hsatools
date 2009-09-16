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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.impl.kiv.ldap.PersonRepository;

public class TestAddressHelper {
	
	private String street=null;
	private String zipCode1=null;
	private String zipCode2=null;
	private String zipCode3=null;
	private String city=null;
	private String additionalInfo1=null;
	private String additionalInfo2=null;
    
    @BeforeClass  
    public static void runBeforeClass() {   
        // run for one time before all test cases           
    }   
      
    @AfterClass  
    public static void runAfterClass() {   
        // run for one time after all test cases   
    }   
    
    
    @After
    public void runAfterEveryTest() {
        // run for each time after every test cases   
    }

    @Before
    public void runBeforeEveryTest() {
        // run for each time before every test cases   
    	street="Lilla Hasselbacken 4";
    	zipCode1="422 04";
    	zipCode2="42204";
    	zipCode3="42 204";
    	city="Hisingsbacka Station";
    	additionalInfo1="Till vänster via Huvudentré";
    	additionalInfo2="Hiss A Plan 10";
    }

    @Test
    public void testConvertToStreetAddress1() throws Exception {
    	Address address=null;
    	List<String> addressList = new ArrayList<String>();
    	addressList.add(street);
    	addressList.add(zipCode1 + city);
    	address = AddressHelper.convertToStreetAddress(addressList);
    	Assert.assertEquals(address.getStreet(), street);
    	String s=address.getZipCode().getFormattedZipCode().toString();
    	Assert.assertEquals(s, zipCode1);
    	Assert.assertEquals(address.getCity(), city);
    	Assert.assertEquals(address.getAdditionalInfo().size(), 0);    	    
    }
    
    @Test
    public void testConvertToStreetAddress2() throws Exception {
    	Address address=null;
    	List<String> addressList = new ArrayList<String>();
    	addressList.add(street);
    	addressList.add(zipCode2 + "  " + city);
    	address = AddressHelper.convertToStreetAddress(addressList);
    	Assert.assertEquals(address.getStreet(), street);
    	String s=address.getZipCode().getFormattedZipCode().toString();
    	Assert.assertEquals(s, zipCode1);
    	Assert.assertEquals(address.getCity(), city);
    	Assert.assertEquals(address.getAdditionalInfo().size(), 0);    	    
    }

    @Test
    public void testConvertToStreetAddress3() throws Exception {
    	Address address=null;
    	List<String> addressList = new ArrayList<String>();
    	addressList.add(additionalInfo1);
    	addressList.add(street);
    	addressList.add(zipCode3 + "  " + city);
    	address = AddressHelper.convertToStreetAddress(addressList);
    	Assert.assertEquals(address.getStreet(), street);
    	String s=address.getZipCode().getFormattedZipCode().toString();
    	Assert.assertEquals(s, zipCode1);
    	Assert.assertEquals(address.getCity(), city);
    	Assert.assertEquals(address.getAdditionalInfo().size(), 1);    	    
    }
    @Test
    public void testConvertToStreetAddress4() throws Exception {
    	Address address=null;
    	List<String> addressList = new ArrayList<String>();
    	addressList.add(street);
    	addressList.add(additionalInfo2);
    	addressList.add(zipCode3 + city);
    	address = AddressHelper.convertToStreetAddress(addressList);
    	Assert.assertEquals(address.getStreet(), street);
    	String s=address.getZipCode().getFormattedZipCode().toString();
    	Assert.assertEquals(s, zipCode1);
    	Assert.assertEquals(address.getCity(), city);
    	Assert.assertEquals(address.getAdditionalInfo().size(), 1);    	    
    }

}
