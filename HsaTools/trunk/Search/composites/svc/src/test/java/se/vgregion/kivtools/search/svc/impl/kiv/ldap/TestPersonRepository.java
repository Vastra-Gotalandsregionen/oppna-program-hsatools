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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.impl.kiv.ldap.PersonRepository;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 * Test of the class PersonRepository
 *
 */
public class TestPersonRepository {
    PersonRepository repo = null;
    
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
        repo = new PersonRepository();
    }

    @After
    public void runAfterEveryTest() {
        // run for each time after every test cases   
        repo = null;
    }

    /**
     * Combined test
     * @throws Exception
     */
    @Test
    public void testcreateSearchPersonsFilter1() throws Exception {
        StringBuffer correctResult = new StringBuffer();
        correctResult.append("(&(objectclass=vgrUser)(vgr-id=*hanac*)(|(givenName=*hans*)(hsaNickName=*hans*))(|(sn=*ackerot*)(hsaMiddleName=*ackerot*)))");
        String temp = repo.createSearchPersonsFilter("hans","ackerot","hanac");
        Assert.assertEquals(correctResult.toString(), temp);        
    }
    
    /**
     * Combined test
     * @throws Exception
     */
    @Test
    public void testcreateSearchPersonsFilter2() throws Exception {
        StringBuffer correctResult = new StringBuffer();
        correctResult.append("(&(objectclass=vgrUser)(|(givenName=*hans*)(hsaNickName=*hans*)))");
        String temp = repo.createSearchPersonsFilter("hans","","");
        Assert.assertEquals(correctResult.toString(), temp);        
    }
    
    /**
     * Combined test
     * @throws Exception
     */
    @Test
    public void testcreateSearchPersonsFilter3() throws Exception {
        StringBuffer correctResult = new StringBuffer();
        correctResult.append("(&(objectclass=vgrUser)(vgr-id=*ana*))");
        String temp = repo.createSearchPersonsFilter("","","ana");
        Assert.assertEquals(correctResult.toString(), temp);        
    }
}
