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

package se.vgregion.kivtools.util.email;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class EmailSenderSpringImplTest {

  private EmailSenderSpringImpl emailSenderSpringImpl;

  @Before
  public void setUp() throws Exception {
    emailSenderSpringImpl = new EmailSenderSpringImpl();
  }

  @Test
  public void testInstantiation() {
    EmailSenderSpringImpl emailSenderSpringImpl = new EmailSenderSpringImpl();
    assertNotNull(emailSenderSpringImpl);
  }

  @Test(expected = NullPointerException.class)
  public void testSendEmailNoMailSenderSet() {
    emailSenderSpringImpl.sendEmail("a@b.c", Arrays.asList("a@b.c"), "Test", "Test");
  }

  @Test
  public void testSendEmail() {
    MailSenderMock mailSender = new MailSenderMock();
    emailSenderSpringImpl.setMailSender(mailSender);
    emailSenderSpringImpl.sendEmail("a@b.c", Arrays.asList("a@b.c", "d@e.f"), "Test", "Test");
    mailSender.assertSentEmail("a@b.c", "Test", "Test", "a@b.c", "d@e.f");
  }

  @Test
  public void testAlwaysRecipients() {
    MailSenderMock mailSender = new MailSenderMock();
    emailSenderSpringImpl.setMailSender(mailSender);
    emailSenderSpringImpl.setAlwaysRecipients(Arrays.asList("test@test.com"));
    emailSenderSpringImpl.sendEmail("a@b.c", Arrays.asList("a@b.c", "d@e.f"), "Test", "Test");
    mailSender.assertSentEmail("a@b.c", "Test", "Test", "a@b.c", "d@e.f", "test@test.com");
  }

  private static class MailSenderMock implements MailSender {
    private SimpleMailMessage message;

    public void assertSentEmail(String fromAddress, String subject, String body, String... recipientAddresses) {
      assertEquals(fromAddress, message.getFrom());
      assertEquals(subject, message.getSubject());
      assertEquals(body, message.getText());
      assertEquals(recipientAddresses.length, message.getTo().length);
      List<String> actualRecipients = Arrays.asList(message.getTo());
      for (String recipientAddress : recipientAddresses) {
        assertTrue("Expected recipient was not found: " + recipientAddress, actualRecipients.contains(recipientAddress));
      }
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
      this.message = simpleMessage;
    }

    @Override
    public void send(SimpleMailMessage[] simpleMessages) throws MailException {
    }
  }
}
