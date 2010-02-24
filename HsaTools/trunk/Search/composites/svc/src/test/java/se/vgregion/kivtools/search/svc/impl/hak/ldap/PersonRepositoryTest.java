/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

public class PersonRepositoryTest {
  private PersonRepository personRepository;

  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() {
    personRepository = new PersonRepository();

    ldapTemplate = new LdapTemplateMock();
    personRepository.setLdapTemplate(ldapTemplate);
  }

  @Test
  public void testSearchPersons() throws KivException {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();

    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    assertNotNull(searchPersons);
    assertEquals(0, searchPersons.size());

    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Kalle");
    dirContext.addAttributeValue("cn", "cn=Kalle,ou=Org,o=VGR");
    this.ldapTemplate.addDirContextOperationForSearch(dirContext);
    dirContext = new DirContextOperationsMock();
    dn = DistinguishedName.immutableDistinguishedName("cn=Olle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Olle");
    dirContext.addAttributeValue("cn", "cn=Olle,ou=Org,o=VGR");
    this.ldapTemplate.addDirContextOperationForSearch(dirContext);

    searchPersonCriterion.setGivenName(" Kalle ");
    searchPersonCriterion.setSurname(" Svensson ");

    searchPersons = personRepository.searchPersons(searchPersonCriterion, 0);
    ldapTemplate.assertSearchFilter("(&(objectclass=hkatPerson)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");

    searchPersonCriterion.setUserId(" ksn829 ");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 1);
    ldapTemplate.assertSearchFilter("(&(objectclass=hkatPerson)(|(regionName=*ksn829*)(title=*ksn829*))(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");
    assertEquals(1, searchPersons.size());
  }

  @Test
  public void testSearchPersonsExactMatch() throws KivException {
    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Kalle");
    this.ldapTemplate.addDirContextOperationForSearch(dirContext);

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName(" \"Kalle\" ");
    searchPersonCriterion.setSurname(" \"Svensson\" ");
    searchPersonCriterion.setUserId(" \"ksn001\" ");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapTemplate.assertSearchFilter("(&(objectclass=hkatPerson)(|(regionName=ksn001)(title=ksn001))(|(givenName=Kalle)(rsvFirstNames=Kalle))(|(sn=Svensson)(middleName=Svensson)))");
    assertEquals(1, searchPersons.size());

    searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setUserId("\"ksn829\"");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapTemplate.assertSearchFilter("(&(objectclass=hkatPerson)(|(regionName=ksn829)(title=ksn829)))");
    assertEquals(1, searchPersons.size());
  }

  @Test
  public void testSearchPersonsTelephoneNumber() throws KivException {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setUserId(" 070-123 456 ");
    personRepository.searchPersons(searchPersonCriterion, 10);
    ldapTemplate.assertSearchFilter("(&(objectclass=hkatPerson)(|(regionName=*070*123*456*)(facsimileTelephoneNumber=*70123456*)(hsaSwitchboardNumber=*70123456*)"
        + "(telephoneNumber=*70123456*)(hsaTelephoneNumber=*70123456*)(mobile=*70123456*)(hsaInternalPagerNumber=*70123456*)(pager=*70123456*)(hsaTextPhoneNumber=*70123456*)(title=*070*123*456*)))");
  }

  @Test
  public void testSearchPersonsTitle() throws KivException {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setUserId(" ingenjör ");
    personRepository.searchPersons(searchPersonCriterion, 10);
    ldapTemplate.assertSearchFilter("(&(objectclass=hkatPerson)(|(regionName=*ingenjör*)(title=*ingenjör*)))");
  }

  @Test
  public void testSearchPersonsByDn() throws KivException {
    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Kalle");
    dirContext.addAttributeValue("sn", "Kula");
    dirContext.addAttributeValue("cn", "cn=Kalle Kula");
    dirContext.addAttributeValue("regionName", "kku814");
    this.ldapTemplate.addDirContextOperationForSearch(dirContext);
    dirContext = new DirContextOperationsMock();
    dn = DistinguishedName.immutableDistinguishedName("cn=Anders Ask,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Anders");
    dirContext.addAttributeValue("sn", "Ask");
    dirContext.addAttributeValue("cn", "Anders Ask");
    dirContext.addAttributeValue("regionName", "aas123");
    this.ldapTemplate.addDirContextOperationForSearch(dirContext);

    List<Person> searchPersons = personRepository.searchPersonsByDn("CN=Anders Ask,OU=Länssjukhuset i Halmstad,OU=Landstinget Halland,DC=lthallandhsa,DC=se", 10);
    ldapTemplate.assertSearchFilter("(objectClass=hkatPerson)");
    assertEquals(2, searchPersons.size());
    assertEquals("Anders", searchPersons.get(0).getGivenName());
    assertEquals("Kalle", searchPersons.get(1).getGivenName());
  }

  @Test
  public void testGetAllPersonsVgrId() throws KivException {
    DirContextOperationsMock regionName1 = new DirContextOperationsMock();
    regionName1.addAttributeValue("regionName", "kal456");
    DirContextOperationsMock regionName2 = new DirContextOperationsMock();
    regionName2.addAttributeValue("regionName", "abc123");
    this.ldapTemplate.addDirContextOperationForSearch(regionName1);
    this.ldapTemplate.addDirContextOperationForSearch(regionName2);
    List<String> allPersonsVgrId = personRepository.getAllPersonsVgrId();
    ldapTemplate.assertSearchFilter("(objectClass=hkatPerson)");
    assertNotNull(allPersonsVgrId);
    assertEquals(2, allPersonsVgrId.size());
    assertTrue(allPersonsVgrId.contains("abc123"));
    assertTrue(allPersonsVgrId.contains("kal456"));
  }

  @Test
  public void testGetPersonByDn() throws KivException {
    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Nina");
    dirContext.addAttributeValue("sn", "Kanin");
    dirContext.addAttributeValue("cn", "cn=Nina Kanin");
    dirContext.addAttributeValue("regionName", "nka435");
    this.ldapTemplate.addBoundDN(DistinguishedName.immutableDistinguishedName("cn=Nina Kanin, ou=abc, ou=def"), dirContext);

    Person person = personRepository.getPersonByDn("cn=Nina Kanin,ou=abc,ou=def");
    assertNotNull(person);
    assertEquals("Nina", person.getGivenName());
    assertEquals("Kanin", person.getSn());
    assertEquals("cn=Nina Kanin", person.getCn());
  }

  @Test
  public void testGetProfileImageByDn() throws KivException {
    DirContextOperationsMock personWithImage = new DirContextOperationsMock();
    personWithImage.addAttributeValue("jpegPhoto", StringUtil.getBytes("MockImageData", "UTF-8"));
    this.ldapTemplate.addBoundDN(new DistinguishedName("cn=Nina Kanin,ou=abc,ou=def"), personWithImage);

    byte[] profileImage = personRepository.getProfileImageByDn("cn=Nina Kanin,ou=abc,ou=def");
    assertEquals("MockImageData", StringUtil.getString(profileImage, "UTF-8"));
  }

  @Test
  public void testGetProfileImageByDnExceptionHandling() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new NameNotFoundException("No entry found"));

    byte[] profileImage = personRepository.getProfileImageByDn("cn=Nina Kanin,ou=abc,ou=def");
    assertNull(profileImage);
  }

  @Test
  public void testGetPersonByVgrId() throws KivException {
    Person person = personRepository.getPersonByVgrId("ksn829");
    assertNull(person);

    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Kalle");
    ldapTemplate.addDirContextOperationForSearch(dirContext);

    person = personRepository.getPersonByVgrId("ksn829");

    ldapTemplate.assertSearchFilter("(&(objectClass=hkatPerson)(regionName=ksn829))");
    assertNotNull(person);
  }

  @Test
  public void testGetAllPersonsInUnit() throws KivException {
    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Kalle");
    ldapTemplate.addDirContextOperationForSearch(dirContext);

    SikSearchResultList<Person> allPersonsInUnit = personRepository.getAllPersonsInUnit("abc-123", 10);

    ldapTemplate.assertSearchFilter("(hsaIdentity=abc-123)");
    assertNotNull(allPersonsInUnit);
    assertEquals(1, allPersonsInUnit.size());
  }

  @Test
  public void primaryEmploymentIsAlwaysReturnedFirst() throws KivException {
    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=kon829,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("regionName", "kon829");
    dirContext.addAttributeValue("title", "Läkare");
    ldapTemplate.addDirContextOperationForSearch(dirContext);
    dirContext = new DirContextOperationsMock();
    dirContext.setDn(dn);
    dirContext.addAttributeValue("regionName", "kon829");
    dirContext.addAttributeValue("title", "Assistent");
    dirContext.addAttributeValue("mainNode", "Ja");
    ldapTemplate.addDirContextOperationForSearch(dirContext);

    SikSearchResultList<Person> persons = personRepository.searchPersons("kon829", 1);
    assertNotNull(persons);
    assertEquals(1, persons.size());
    assertEquals(2, persons.get(0).getEmployments().size());
    Employment primaryEmployment = persons.get(0).getEmployments().get(0);
    Employment otherEmployment = persons.get(0).getEmployments().get(1);

    assertEquals("Assistent", primaryEmployment.getTitle());
    assertTrue(primaryEmployment.isPrimaryEmployment());
    assertEquals("Läkare", otherEmployment.getTitle());
    assertFalse(otherEmployment.isPrimaryEmployment());

    ldapTemplate.clearDirContexts();

    dirContext = new DirContextOperationsMock();
    dirContext.setDn(dn);
    dirContext.addAttributeValue("regionName", "kon829");
    dirContext.addAttributeValue("title", "Assistent");
    dirContext.addAttributeValue("mainNode", "Ja");
    ldapTemplate.addDirContextOperationForSearch(dirContext);

    dirContext = new DirContextOperationsMock();
    dirContext.setDn(dn);
    dirContext.addAttributeValue("regionName", "kon829");
    dirContext.addAttributeValue("title", "Projektledare");
    ldapTemplate.addDirContextOperationForSearch(dirContext);

    persons = personRepository.searchPersons("kon829", 1);

    primaryEmployment = persons.get(0).getEmployments().get(0);
    otherEmployment = persons.get(0).getEmployments().get(1);

    assertEquals("Assistent", primaryEmployment.getTitle());
    assertTrue(primaryEmployment.isPrimaryEmployment());
    assertEquals("Projektledare", otherEmployment.getTitle());
    assertFalse(otherEmployment.isPrimaryEmployment());
  }

  private static class LdapTemplateMock extends LdapTemplate {
    private String filter;
    private Map<Name, DirContextOperations> boundDNs = new HashMap<Name, DirContextOperations>();
    private List<DirContextOperations> dirContextOperations = new ArrayList<DirContextOperations>();
    private NameNotFoundException exceptionToThrow;

    public void addBoundDN(Name dn, DirContextOperations dirContextOperations) {
      this.boundDNs.put(dn, dirContextOperations);
    }

    public void clearDirContexts() {
      this.dirContextOperations.clear();
    }

    public void addDirContextOperationForSearch(DirContextOperations dirContextOperations) {
      this.dirContextOperations.add(dirContextOperations);
    }

    public void setExceptionToThrow(NameNotFoundException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    public void assertSearchFilter(String expectedFilter) {
      assertEquals(expectedFilter, this.filter);
    }

    @Override
    public Object lookup(Name dn, ContextMapper mapper) {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }

      DirContextOperations dirContextOperations = this.boundDNs.get(dn);
      if (dirContextOperations == null) {
        throw new NameNotFoundException("Name not found");
      }

      Object result = null;
      if (dirContextOperations != null) {
        result = mapper.mapFromContext(dirContextOperations);
      }
      return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List search(String base, String filter, SearchControls searchControls, ContextMapper mapper, DirContextProcessor dirContextProcessor) {
      this.filter = filter;
      List result = new ArrayList();
      for (DirContextOperations dirContextOperations : this.dirContextOperations) {
        result.add(mapper.mapFromContext(dirContextOperations));
      }
      // Use ReflectionUtil since there is no set-method for cookie.
      ReflectionUtil.setField(dirContextProcessor, "cookie", new PagedResultsCookie(null));
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List search(Name base, String filter, ContextMapper mapper) {
      this.filter = filter;
      List result = new ArrayList();
      for (DirContextOperations dirContextOperations : this.dirContextOperations) {
        result.add(mapper.mapFromContext(dirContextOperations));
      }
      return result;
    }
  }
}
