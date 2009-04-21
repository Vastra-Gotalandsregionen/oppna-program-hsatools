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
package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.AddressHelper;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;

public class SearchServiceMockImpl implements SearchService {
    private Log logger = LogFactory.getLog(this.getClass());
    private static final String CLASS_NAME = SearchServiceMockImpl.class.getName();
    
    private SikSearchResultList<Person> personList = new SikSearchResultList<Person>();
    private SikSearchResultList<Unit>   unitList    = new SikSearchResultList<Unit>(); 
    
    public SearchServiceMockImpl() {
        init();
    }
    
    public SikSearchResultList<Employment> getEmployments(String personDn) throws Exception {
        SikSearchResultList<Employment> employments = new SikSearchResultList<Employment>();
        Employment e;
        
        e = new Employment();
        e.setHsaPublicTelephoneNumber(new PhoneNumber("031-123456"));
        e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
        employments.add(e);

        e = new Employment();
        e.setHsaPublicTelephoneNumber(new PhoneNumber("031-23 23 23"));
        e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
        employments.add(e);

        e = new Employment();
        e.setHsaPublicTelephoneNumber(new PhoneNumber("08-2283393"));
        e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
        employments.add(e);

        e = new Employment();
        e.setHsaPublicTelephoneNumber(new PhoneNumber("030012350"));
        e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
        employments.add(e);

        e = new Employment();
        e.setHsaPublicTelephoneNumber(new PhoneNumber("+46822412350"));
        e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
        employments.add(e);
        
        return employments;
    }

    public Person getPersonById(String vgrId) throws Exception {
        Person p = new Person();
        if(vgrId.equalsIgnoreCase("anders1")) {
            p.setGivenName("Anders");
            p.setSn("Asplund");
            p.setHsaMiddleName("Sandin");
            p.setFullName("Anders Sandin Asplund");
            p.setMail("anders.asplund@knowit.se");
            p.setVgrId("anders1");
        } else if(vgrId.equalsIgnoreCase("hangy2")) {
            p.setGivenName("Hans");
            p.setSn("Gyllensten");
            p.setFullName("Hans Gyllensten");
            p.setMail("hans.gyllensten@knowit.se");
            p.setVgrId("hangy2");
           
        } else if(vgrId.equalsIgnoreCase("pj3")) {
            p.setGivenName("Per-Johan");
            p.setSn("Andersson");
            p.setFullName("Per-Johan Andersson");
            p.setMail("per.johan@andersson@knowit.se");
            p.setVgrId("pj3");
        }
        return p;
    }

    public Unit getUnitByHsaId(String hsaId) throws Exception {
        Unit u = new Unit();
        List<String> a;
        List<PhoneNumber> p;
        List<String> d = new ArrayList<String>();
        d.add("Bla bla bla");
        d.add("Bla bla bla");
        if(hsaId.equalsIgnoreCase("ABC001")) {
            u.setName("VGR IT");
            u.setHsaIdentity("ABC001");
            p = new ArrayList<PhoneNumber>();
            p.add(new PhoneNumber("031-123456"));
            p.add(new PhoneNumber("031-654321"));
            u.setHsaTelephoneNumber(p);
            a = new ArrayList<String>();
            a.add("Storgatan 1");
            a.add("411 01 G�teborg");
            u.setDescription(d);
            u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
            u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Omr�de 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
        }
        
        else if(hsaId.equalsIgnoreCase("ABC002")) {
            u.setName("Sahlgrenska Sjukhuset");
            u.setHsaIdentity("ABC002");
            p = new ArrayList<PhoneNumber>();
            p.add(new PhoneNumber("031-123456"));
            p.add(new PhoneNumber("031-654321"));
            u.setHsaTelephoneNumber(p);
            a = new ArrayList<String>();
            a.add("Storgatan 1");
            a.add("411 01 G�teborg");
            u.setDescription(d);
            u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
            u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Omr�de 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
        }
        
        else if(hsaId.equalsIgnoreCase("ABC003")) {
            u.setName("Uddevalla v�rdcentral");
            u.setHsaIdentity("ABC003");
            p = new ArrayList<PhoneNumber>();
            p.add(new PhoneNumber("031-123456"));
            p.add(new PhoneNumber("031-654321"));
            u.setHsaTelephoneNumber(p);
            a = new ArrayList<String>();
            a.add("Storgatan 1");
            a.add("411 01 Uddevalla");
            u.setDescription(d);
            u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
            u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Omr�de 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
        }
        return u;
    }

    public SikSearchResultList<Person> searchPersons(String givenName, String familyName) throws Exception {
        return searchPersons(givenName, familyName, 0);
    }

    public SikSearchResultList<Person> searchPersons(String givenName,
            String familyName, int maxSearchResult) throws Exception {
        return personList;
    }

    /**
     * 
     * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list od Persons
     * @return
     * @throws Exception
     */    
    public SikSearchResultList<Person> searchPersons(String vgrId, int maxResult) throws Exception {
        SikSearchResultList<Person> tempList = new SikSearchResultList<Person>();
        for (Person p:personList) {
            if (p.getVgrId().indexOf(vgrId)>=0) {
                tempList.add(p);
            }
        }
        return tempList;
    }

    /**
     * 
     * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list od Persons
     * @return
     * @throws Exception
     */    
    public SikSearchResultList<Person> searchPersons(String vgrId) throws Exception {
        return searchPersons(vgrId, 0);
    }
    

    public SikSearchResultList<Unit> searchUnits(Unit unit) throws Exception {
        return searchUnits(unit, 0);
    }
    
    public SikSearchResultList<Unit> searchUnits(Unit unit, int maxSearchResult) throws Exception {
        return unitList;
    }

    /* (non-Javadoc)
     * @see se.vgregion.kivtools.search.svc.SearchService#getMaxNumberOfPersonHits()
     */
    public int getMaxNumberOfPersonHits() {
        return 200;
    }

    /* (non-Javadoc)
     * @see se.vgregion.kivtools.search.svc.SearchService#getMaxNumberOfUnitHits()
     */
    public int getMaxNumberOfUnitHits() {
        return 200;
    }

    public Person getPersonByHsaId(String hsaId) throws Exception {
        Person p = new Person();
        Employment e = new Employment();
        List<Employment> em = new ArrayList<Employment>();
        
        logger.info(CLASS_NAME + ".getPersonByHsaId(hsaId = " + hsaId + ")");

        p.setGivenName("Anders");
        p.setSn("Asplund");
        p.setHsaMiddleName("Sandin");
        p.setFullName("Anders Sandin Asplund");
        p.setMail("anders.asplund@knowit.se");

        e.setHsaPublicTelephoneNumber(new PhoneNumber("031-123456"));
        e.setOu("VGR IT");
        e.setTitle("Konsult");
        em.add(e);
        
        e = new Employment();
        e.setHsaPublicTelephoneNumber(new PhoneNumber("031-123456"));
        e.setOu("Akutmottagningen");
        e.setTitle("Sjuksk�terska");
        em.add(e);
        
        p.setEmployments(em);

        return p;
    }

    public List<String> getAllPersonsId() throws Exception {
        List<String> result = new ArrayList<String>();
        result.add("anders1");
        result.add("hangy2");
        result.add("pj3");
        return result;
    }

    public List<String> getAllUnitsHsaIdentity() throws Exception {
        List<String> result = new ArrayList<String>();
        result.add("ABC001");
        result.add("ABC002");
        result.add("ABC003");
        return result;
    }

    public Unit getUnitByDN(DN dn) throws Exception {
        
        Unit u = new Unit();
        ArrayList<PhoneNumber> p;
        ArrayList<String> a;
        
        u.setName("VGR IT");
        u.setHsaIdentity("ABC001");
        p = new ArrayList<PhoneNumber>();
        p.add(new PhoneNumber("031-123456"));
        p.add(new PhoneNumber("031-654321"));
        u.setHsaTelephoneNumber(p);
        a = new ArrayList<String>();
        a.add("Storgatan 1");
        a.add("411 01 G�teborg");
        u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
        u.setLdapDistinguishedName("VGR IT/Org C/Org B/Org A");
        return u;
    }

    private void init() {
        initPersons(personList);
        initUnits(unitList);
    }
    
    private void initPersons(SikSearchResultList<Person> list) {
        Person p = new Person();

        p.setGivenName("Anders");
        p.setSn("Asplund");
        p.setHsaMiddleName("Sandin");
        p.setFullName("Anders Sandin Asplund");
        p.setMail("anders.asplund@knowit.se");
        p.setDn("anders1");
        p.setVgrId("anders1");
        list.add(p);
        
        p = new Person();
        p.setGivenName("Hans");
        p.setSn("Gyllensten");
        p.setFullName("Hans Gyllensten");
        p.setMail("hans.gyllensten@knowit.se");
        p.setDn("hangy2");
        p.setVgrId("hangy2");
        list.add(p);
        
        p = new Person();
        p.setGivenName("Per-Johan");
        p.setSn("Andersson");
        p.setFullName("Per-Johan Andersson");
        p.setMail("per.johan@andersson@knowit.se");
        p.setDn("pj3");
        p.setVgrId("pj3");
        p.setDn("cn=ulfsa3,ou=Personal,o=vgr");
        list.add(p);
    }
    
    private void initUnits(SikSearchResultList<Unit> list) {
        Unit u;
        List<String> a;
        List<PhoneNumber> p;
        
        u = new Unit();
        u.setName("VGR IT");
        u.setHsaIdentity("ABC001");
        p = new ArrayList<PhoneNumber>();
        p.add(new PhoneNumber("031-123456"));
        p.add(new PhoneNumber("031-654321"));
        u.setHsaTelephoneNumber(p);
        a = new ArrayList<String>();
        a.add("Storgatan 1");
        a.add("411 01 G�teborg");
        u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
        u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Område 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
        list.add(u);
        
        u = new Unit();
        u.setHsaIdentity("ABC002");
        u.setName("Sahlgrenska Sjukhuset");
        p = new ArrayList<PhoneNumber>();
        p.add(new PhoneNumber("031-123456"));
        p.add(new PhoneNumber("031-654321"));
        u.setHsaTelephoneNumber(p);
        a = new ArrayList<String>();
        a.add("Storgatan 1");
        a.add("411 01 Göteborg");
        u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
        u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Område 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
        list.add(u);

        u = new Unit();
        u.setHsaIdentity("ABC003");
        u.setName("Uddevalla vårdcentral");
        p = new ArrayList<PhoneNumber>();
        p.add(new PhoneNumber("031-123456"));
        p.add(new PhoneNumber("031-654321"));
        u.setHsaTelephoneNumber(p);
        a = new ArrayList<String>();
        a.add("Storgatan 1");
        a.add("411 01 Uddevalla");
        u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
        u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Område 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
        list.add(u);
        
    }

    /* (non-Javadoc)
     * @see se.vgregion.kivtools.search.svc.SearchService#searchPersons(java.lang.String, java.lang.String, java.lang.String)
     */
    public SikSearchResultList<Person> searchPersons(String givenName,
            String familyName, String vgrId) throws Exception {
        // TODO Auto-generated method stub
        return searchPersons(givenName, familyName);
    }

    /* (non-Javadoc)
     * @see se.vgregion.kivtools.search.svc.SearchService#searchPersons(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public SikSearchResultList<Person> searchPersons(String givenName,
            String familyName, String vgrId, int maxResult) throws Exception {
        // TODO Auto-generated method stub
        return searchPersons(givenName, familyName);
    }

	public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, Comparator<Unit> sortOrder) throws Exception {
		// TODO Auto-generated method stub
		// JOLI please add code for this case
		return new SikSearchResultList<Unit>();
	}

	public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
		// TODO Auto-generated method stub
		// JOLI please add code for this case
		return new SikSearchResultList<Unit>();
	}

	public List<HealthcareType> getHealthcareTypesList()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getAllUnitsHsaIdentity(
			List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Person getPersonByDN(DN dn) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Employment> getEmploymentsForPerson(Person person) {
		// TODO Auto-generated method stub
		return null;
	}

	public SikSearchResultList<Person> getAllPersonsInUnitById(String hsaIdentity) throws Exception {
		 return searchPersons("", "");
	}

	public SikSearchResultList<Person> searchPersonsByDn(String dn) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}