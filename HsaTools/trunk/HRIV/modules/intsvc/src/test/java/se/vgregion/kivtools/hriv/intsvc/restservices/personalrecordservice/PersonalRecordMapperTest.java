/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.hriv.intsvc.restservices.personalrecordservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class PersonalRecordMapperTest {

  private PersonalRecordMapper personalRecordMapper;
  private DirContextOperationsMock dirContextOperationsMock;

  @Before
  public void setup() {
    personalRecordMapper = new PersonalRecordMapper();
    dirContextOperationsMock = new DirContextOperationsMock();
    setAttributeMocks();
  }

  @Test
  public void testMapFromContext() {
    PersonalRecord personRecord = (PersonalRecord) personalRecordMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(personRecord);
    assertEquals("David", personRecord.getFirstName());
    assertEquals("Bennehult", personRecord.getLastName());
    assertEquals("David Bennehult", personRecord.getFullName());
  }

  private void setAttributeMocks() {
    dirContextOperationsMock.addAttributeValue("givenName", "David");
    dirContextOperationsMock.addAttributeValue("sn", "Bennehult");
    dirContextOperationsMock.addAttributeValue("fullName", "David Bennehult");
  }
}
