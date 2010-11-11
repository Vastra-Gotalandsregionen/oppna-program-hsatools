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

import static org.junit.Assert.assertEquals;

import java.io.ObjectInputStream;
import java.util.List;

import org.junit.Before;
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

  @Test
  public void testMapFromContext() {
    List<Person> person = arrayOfPerson.getPerson();
    // Person 1
    se.vgregion.kivtools.search.domain.Person personsResult = kivwsPersonMapper.mapFromContext(person.get(1));
    
    assertEquals("SE2321000131-P0000111111111", personsResult.getHsaIdentity());
    assertEquals("Sjuksköterska", personsResult.getHsaTitle());
    assertEquals("Jane", personsResult.getGivenName());
    assertEquals("jane.doe@vgregion.se", personsResult.getMail());
    assertEquals("testsn", personsResult.getSn());
    assertEquals("jane17", personsResult.getVgrId());

    assertEquals("JaneD", personsResult.getHsaNickName());
    assertEquals("30100", personsResult.getHsaSpecialityCode().get(0));
    assertEquals("30200", personsResult.getHsaSpecialityCode().get(1));
    assertEquals("[ENG, SWE]", personsResult.getHsaLanguageKnowledgeCode().toString());
    assertEquals("x", personsResult.getHsaMiddleName());
  }

}
