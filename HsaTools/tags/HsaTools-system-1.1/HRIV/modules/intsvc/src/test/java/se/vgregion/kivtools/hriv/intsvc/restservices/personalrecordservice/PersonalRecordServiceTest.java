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

package se.vgregion.kivtools.hriv.intsvc.restservices.personalrecordservice;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class PersonalRecordServiceTest {

  private static final String USER_ID = "userId1";
  private static final String EXPECTED_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><personRecord><fullName>David Bennehult</fullName><firstName>Davidåäö</firstName><lastName>BennhultÅÄÖ</lastName></personRecord>";
  private static final String EXPECTED_EMPTY_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><personRecord/>";
  private PersonalRecordService personalRecordService;
  private LdapTemplateMock ldapTemplateMock;
  private MockHttpServletRequest mockHttpServletRequest;
  private MockHttpServletResponse mockHttpServletResponse;

  @Before
  public void setup() {
    personalRecordService = new PersonalRecordService();
    ldapTemplateMock = new LdapTemplateMock();
    ldapTemplateMock.setPersonRecord(new PersonalRecord("Davidåäö", "BennhultÅÄÖ", "David Bennehult"));
    personalRecordService.setLdapTemplate(ldapTemplateMock);
    mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletResponse = new MockHttpServletResponse();
    mockHttpServletRequest.setPathInfo("/personalrecord/" + USER_ID);
  }

  @Test
  public void testGetPersonalRecord() throws IOException {
    personalRecordService.getPersonalRecord(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(EXPECTED_RESULT, mockHttpServletResponse.getContentAsString());
  }

  @Test
  public void testEmptyUserId() throws IOException {
    // if user id is empty, then an empty xml personalRecord thould be return.
    mockHttpServletRequest.setPathInfo("/personalrecord/");
    personalRecordService.getPersonalRecord(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(EXPECTED_EMPTY_RESULT, mockHttpServletResponse.getContentAsString());
    assertEquals("UTF-8", mockHttpServletResponse.getCharacterEncoding());
  }

  @Test
  public void testNoPersonFound() throws IOException {
    // if person not found, then an empty xml personalRecord thould be return.
    ldapTemplateMock.setPersonRecord(null);
    personalRecordService.getPersonalRecord(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(EXPECTED_EMPTY_RESULT, mockHttpServletResponse.getContentAsString());
  }

  @Test
  public void testNameNotFoundException() throws IOException {
    // if exception is thrown then an empty xml personalRecord thould be return.
    ldapTemplateMock.throwException = true;
    personalRecordService.getPersonalRecord(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(EXPECTED_EMPTY_RESULT, mockHttpServletResponse.getContentAsString());
  }

  private static class LdapTemplateMock extends se.vgregion.kivtools.mocks.ldap.LdapTemplateMock {
    private boolean throwException;
    private PersonalRecord personRecord;

    public void setPersonRecord(PersonalRecord personRecord) {
      this.personRecord = personRecord;
    }

    @Override
    public Object lookup(String dn, ContextMapper mapper) {
      if (throwException) {
        throw new NameNotFoundException("");
      } else {
        assertEquals("cn=" + USER_ID, dn.toString());
        return personRecord;
      }
    }
  }
}
