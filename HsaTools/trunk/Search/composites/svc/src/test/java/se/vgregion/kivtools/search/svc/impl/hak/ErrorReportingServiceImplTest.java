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
package se.vgregion.kivtools.search.svc.impl.hak;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.ResponsibleEditorEmailFinder;
import se.vgregion.kivtools.util.email.EmailSender;

public class ErrorReportingServiceImplTest {

  private ErrorReportingServiceImpl errorReportingServiceImpl;
  private EmailSenderMock emailSender;

  @Before
  public void setUp() throws Exception {
    errorReportingServiceImpl = new ErrorReportingServiceImpl();
    ResponsibleEditorEmailFinderMock responsibleEditorEmailFinder = new ResponsibleEditorEmailFinderMock();
    errorReportingServiceImpl.setResponsibleEditorEmailFinder(responsibleEditorEmailFinder);
    emailSender = new EmailSenderMock();
    errorReportingServiceImpl.setEmailSender(emailSender);
  }

  @Test
  public void testInstantiation() {
    ErrorReportingServiceImpl errorReportingServiceImpl = new ErrorReportingServiceImpl();
    assertNotNull(errorReportingServiceImpl);
  }

  @Test
  public void testReportError() {
    errorReportingServiceImpl.reportError("cn=Nina Kanin,ou=abc,ou=def", "Saknar adress", "http://a.b.c");
    String expectedBody = "Detta e-postmeddelande har skickats till dig eftersom du är uppdateringsansvarig i Hallandskatalogen.\n" + "\n" + "Inrapporterat fel gäller: Nina Kanin\n" + "\n"
        + "Kommentar från användaren:\n" + "Saknar adress\n" + "\n"
        + "Klicka här för att justera informationen i Hallandskatalogen om Nina Kanin: <http://hak.lthalland.se/nordicedge/jsp/login.jsp?loginDN=Y249TmluYSBLYW5pbixvdT1hYmMsb3U9ZGVm>\n" + "\n"
        + "Klicka här för att se kontaktkortet för Nina Kanin: <http://a.b.c>";
    emailSender.assertEmailInformation("hallandskatalogen@lthalland.se", "Fel i Hallandskatalogen för Nina Kanin.", expectedBody, "test@test.com");
  }

  private static class EmailSenderMock implements EmailSender {
    private String fromAddress;
    private List<String> recipientAddresses;
    private String subject;
    private String body;

    public void assertEmailInformation(String expectedFromAddress, String expectedSubject, String expectedBody, String... expectedRecipients) {
      assertEquals("Unexpected fromAddress", expectedFromAddress, fromAddress);
      assertEquals("Unexpected subject", expectedSubject, subject);
      assertEquals("Unexpected body", expectedBody, body);
      assertEquals("Unexpected number of recipients", expectedRecipients.length, recipientAddresses.size());
      assertEquals("Unexpected recipients", Arrays.asList(expectedRecipients), recipientAddresses);
    }

    @Override
    public void sendEmail(String fromAddress, List<String> recipientAddresses, String subject, String body) {
      this.fromAddress = fromAddress;
      this.recipientAddresses = recipientAddresses;
      this.subject = subject;
      this.body = body;
    }
  }

  private static class ResponsibleEditorEmailFinderMock implements ResponsibleEditorEmailFinder {
    @Override
    public List<String> findResponsibleEditors(String dn) {
      return Arrays.asList("test@test.com");
    }
  }
}
