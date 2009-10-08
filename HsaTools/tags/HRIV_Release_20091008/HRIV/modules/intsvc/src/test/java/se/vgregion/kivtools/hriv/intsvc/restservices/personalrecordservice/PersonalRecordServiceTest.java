package se.vgregion.kivtools.hriv.intsvc.restservices.personalrecordservice;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class PersonalRecordServiceTest {

  private static final String USER_ID = "userId1";
  private static final String EXPECTED_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><personRecord><fullName>David Bennehult</fullName><firstName>David</firstName><lastName>Bennhult</lastName></personRecord>";
  private static final String EXPECTED_EMPTY_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><personRecord/>";
  private PersonalRecordService personalRecordService;
  private LdapTemplateMock ldapTemplateMock;
  private MockHttpServletRequest mockHttpServletRequest;
  private MockHttpServletResponse mockHttpServletResponse;

  @Before
  public void setup(){
    personalRecordService = new PersonalRecordService();
    ldapTemplateMock = new LdapTemplateMock();
    ldapTemplateMock.setPersonRecord(new PersonalRecord("David", "Bennhult", "David Bennehult"));
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
  public void testEmptyUserId() throws IOException{
    // if user id is empty, then an empty xml personalRecord thould be return.
    mockHttpServletRequest.setPathInfo("/personalrecord/");
    personalRecordService.getPersonalRecord(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(EXPECTED_EMPTY_RESULT, mockHttpServletResponse.getContentAsString());
  }
  
  @Test
  public void testNoPersonFound() throws IOException{
    // if person not found, then an empty xml personalRecord thould be return.
    ldapTemplateMock.setPersonRecord(null);
    personalRecordService.getPersonalRecord(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(EXPECTED_EMPTY_RESULT, mockHttpServletResponse.getContentAsString());
  }
  
  @Test
  public void testNameNotFoundException() throws IOException{
    // if exception is thrown then an empty xml personalRecord thould be return.
    ldapTemplateMock.throwException = true;
    personalRecordService.getPersonalRecord(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(EXPECTED_EMPTY_RESULT, mockHttpServletResponse.getContentAsString());
  }

  class LdapTemplateMock extends LdapTemplate {

    private boolean throwException;
    private PersonalRecord personRecord;

    public void setPersonRecord(PersonalRecord personRecord) {
      this.personRecord = personRecord;
    }

    @Override
    public Object lookup(String dn, ContextMapper mapper)   {
      if (throwException) {
       throw new NameNotFoundException(""); 
      }else {
      assertEquals("cn=" + USER_ID, dn.toString());
      return personRecord;
      }
    }
  }

}
