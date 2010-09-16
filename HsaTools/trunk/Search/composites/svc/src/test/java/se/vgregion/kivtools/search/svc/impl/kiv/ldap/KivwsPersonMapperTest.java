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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfPerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Person;

import com.thoughtworks.xstream.XStream;

public class KivwsPersonMapperTest {

  private ArrayOfPerson arrayOfPerson;
  private KivwsPersonMapper kivwsPersonMapper;

  @Before
  public void setUp() throws Exception {
    DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
    Resource resource = defaultResourceLoader.getResource("kivwsPersons.xml");
    XStream xStream = new XStream();
    ObjectInputStream objectInputStream = xStream.createObjectInputStream(resource.getInputStream());
    arrayOfPerson = (ArrayOfPerson) objectInputStream.readObject();
    kivwsPersonMapper = new KivwsPersonMapper();
  }
@Ignore
  @Test
  public void testMapFromContext() {
    List<Person> person = arrayOfPerson.getPerson();
    // Person 1
    se.vgregion.kivtools.search.domain.Person personsResult = kivwsPersonMapper.mapFromContext(person.get(0));
    assertEquals("", personsResult.getCn());
    assertEquals("", personsResult.getCreateTimestamp());
    assertEquals("", personsResult.getDn());
    assertEquals("", personsResult.getEmploymentPeriod());
    assertEquals("", personsResult.getEmployments());
    assertEquals("", personsResult.getFullName());
    assertEquals("Anders", personsResult.getGivenName());
    assertEquals("SE2321000131-P000000098362", personsResult.getHsaIdentity());
    assertEquals("", personsResult.getHsaLanguageKnowledgeCode());
    assertEquals("", personsResult.getHsaLanguageKnowledgeText());
    assertEquals("", personsResult.getHsaMiddleName());
    assertEquals("", personsResult.getHsaNickName());
    assertEquals("", personsResult.getHsaPersonIdentityNumber());
    assertEquals("3352564", personsResult.getHsaPersonPrescriptionCode());
    assertEquals("", personsResult.getHsaSpecialityCode());
    assertEquals("", personsResult.getHsaSpecialityName());
    assertEquals("Läkare", personsResult.getHsaTitle());
    assertEquals("", personsResult.getInitials());
    assertEquals("", personsResult.getMail());
    assertEquals("", personsResult.getMobileNumberOfFirstEmployment());
    assertEquals("", personsResult.getMobileNumberOfFirstEmployment());
    assertEquals("", personsResult.getModifyTimestamp());
    assertEquals("", personsResult.getSn());
    assertEquals("", personsResult.getTelephoneNumberOfFirstEmployment());
    assertEquals("", personsResult.getVgrAdminTypes());
    assertEquals("", personsResult.getVgrAnsvarsnummer());
    assertEquals("", personsResult.getVgrAO3kod());
    assertEquals("", personsResult.getVgrId());
    assertEquals("", personsResult.getVgrManagedObjects());
    assertEquals("", personsResult.getVgrOrgRel());
    assertEquals("", personsResult.getVgrStrukturPersonDN());


  }

}
