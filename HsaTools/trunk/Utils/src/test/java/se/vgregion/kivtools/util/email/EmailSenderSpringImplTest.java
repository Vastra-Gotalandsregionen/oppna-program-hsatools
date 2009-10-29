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
