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
/**
 * 
 */
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 * Test of the class UnitRepository
 *
 */
public class TestUnitRepository {
    UnitRepository ur = null;
    
    @BeforeClass  
    public static void runBeforeClass() {   
        // run for one time before all test cases           
    }   
      
    @AfterClass  
    public static void runAfterClass() {   
        // run for one time after all test cases   
    }   
    
    @Before
    public void runBeforeEveryTest() {
        // run for each time before every test cases   
        ur = new UnitRepository();
    }

    @After
    public void runAfterEveryTest() {
        // run for each time after every test cases   
        ur = null;
    }
    
    @Test
    public void testBuildAddressSearch(){
        String correctResult = "(|(hsaPostalAddress=*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*)" +
                                 "(hsaPostalAddress=*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*)" +
                                 "(hsaPostalAddress=*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))";
        String temp = ur.buildAddressSearch("hsaPostalAddress", "*uddevalla*"); 
        Assert.assertEquals(correctResult, temp);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testcreateSearchFilter() throws Exception{
        StringBuffer correctResult = new StringBuffer();
        correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*barn**och*ungdoms*)");
        correctResult.append("(|(hsaMunicipalityName=*Borås*)");
        correctResult.append("(|(hsaPostalAddress=*Borås*$*$*$*$*$*)");
        correctResult.append("(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)");
        correctResult.append("(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)");
        correctResult.append("(hsaPostalAddress=*$*$*$*$*$*Borås*))");
        correctResult.append("(|(hsaStreetAddress=*Borås*$*$*$*$*$*)");
        correctResult.append("(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)");
        correctResult.append("(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)");
        correctResult.append("(hsaStreetAddress=*$*$*$*$*$*Borås*)))))");
        correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(cn=*barn**och*ungdoms*)");
        correctResult.append("(|(hsaMunicipalityName=*Borås*)");
        correctResult.append("(|(hsaPostalAddress=*Borås*$*$*$*$*$*)");
        correctResult.append("(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)");
        correctResult.append("(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)");
        correctResult.append("(hsaPostalAddress=*$*$*$*$*$*Borås*))");
        correctResult.append("(|(hsaStreetAddress=*Borås*$*$*$*$*$*)");
        correctResult.append("(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)");
        correctResult.append("(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)");
        correctResult.append("(hsaStreetAddress=*$*$*$*$*$*Borås*))))))");
        
        Unit unit = new Unit();
        unit.setName("barn- och ungdoms");
        unit.setHsaMunicipalityName("Borås");
        String temp = ur.createSearchFilter(unit);
        Assert.assertEquals(correctResult.toString(), temp);
    }

    /**
     * Combined test
     * @throws Exception
     */
    @Test
    public void testcreateAdvancedSearchFilter1() throws Exception {
        StringBuffer correctResult = new StringBuffer();
        correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*))))");
        correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(cn=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*)))))");
        Unit unit = new Unit();
        unit.setName("barn- och ungdomsvård");
        unit.setHsaMunicipalityCode("1490");
        String temp = ur.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
        Assert.assertEquals(correctResult.toString(), temp);        
    }
    
    /**
     * Only hsamuncipalitycode
     * @throws Exception
     */
    @Test
    public void testcreateAdvancedSearchFilter2() throws Exception {
        StringBuffer correctResult = new StringBuffer();
        correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540)))))");
        correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540))))))");
        Unit unit = new Unit();
        unit.setHsaMunicipalityCode("1490");
        List<HealthcareType> healthcareTypeList = new ArrayList<HealthcareType>();
        HealthcareType ht = new HealthcareType();
        Map<String,String> conditions = new HashMap<String,String>();
        conditions.put("hsaBusinessClassificationCode", "1540");
        ht.setConditions(conditions);
        healthcareTypeList.add(ht);
        unit.setHealthcareTypes(healthcareTypeList);
        String temp = ur.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
        Assert.assertEquals(correctResult.toString(), temp);        
    }
    
    /**
     * Only hsamuncipalitycode
     * @throws Exception
     */
    @Test
    public void testcreateAdvancedSearchFilter3() throws Exception {
        StringBuffer correctResult = new StringBuffer();
        correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*ambulans*)))(&(objectclass=vgrOrganizationalRole)(&(cn=*ambulans*))))");
        Unit unit = new Unit();
        unit.setName("ambulans");
        String temp = ur.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
        Assert.assertEquals(correctResult.toString(), temp);        
    }
}
