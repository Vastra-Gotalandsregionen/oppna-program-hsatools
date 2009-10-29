package se.vgregion.kivtools.util.email;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class EmailLoggerImplTest {

  private EmailLoggerImpl emailLoggerImpl;

  @Before
  public void setUp() throws Exception {
    emailLoggerImpl = new EmailLoggerImpl();
  }

  @Test
  public void testInstantiation() {
    EmailLoggerImpl emailLoggerImpl = new EmailLoggerImpl();
    assertNotNull(emailLoggerImpl);
  }

  @Test
  public void testSendEmail() throws Exception {
    emailLoggerImpl.sendEmail("test@test.com", Arrays.asList("a@b.c", "d@e.f"), "Testsubject", "Testbody");
  }
}
