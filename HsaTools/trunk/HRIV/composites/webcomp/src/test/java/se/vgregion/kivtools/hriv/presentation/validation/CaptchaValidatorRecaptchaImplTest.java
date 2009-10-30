package se.vgregion.kivtools.hriv.presentation.validation;

import static org.junit.Assert.*;

import java.util.Properties;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.junit.Before;
import org.junit.Test;

public class CaptchaValidatorRecaptchaImplTest {

  private CaptchaValidatorRecaptchaImpl captchaValidatorRecaptcha;
  private RecaptchaMock recaptcha;

  @Before
  public void setUp() throws Exception {
    captchaValidatorRecaptcha = new CaptchaValidatorRecaptchaImpl();
    recaptcha = new RecaptchaMock();
    captchaValidatorRecaptcha.setRecaptcha(recaptcha);
  }

  @Test
  public void testInstantiation() {
    CaptchaValidatorRecaptchaImpl captchaValidatorRecaptchaImpl = new CaptchaValidatorRecaptchaImpl();
    assertNotNull(captchaValidatorRecaptchaImpl);
  }

  @Test
  public void testValidate() {
    boolean result = captchaValidatorRecaptcha.validate("Challenge", "Response", "RemoteAddress");
    assertTrue(result);
    assertEquals("Challenge", recaptcha.captchaChallenge);
    assertEquals("Response", recaptcha.captchaResponse);
    assertEquals("RemoteAddress", recaptcha.remoteAddress);
  }

  private static class RecaptchaMock implements ReCaptcha {
    private String remoteAddress;
    private String captchaChallenge;
    private String captchaResponse;

    @Override
    public ReCaptchaResponse checkAnswer(String remoteAddress, String captchaChallenge, String captchaResponse) {
      this.remoteAddress = remoteAddress;
      this.captchaChallenge = captchaChallenge;
      this.captchaResponse = captchaResponse;

      return new ReCaptchaResponse(true, null) {
      };
    }

    @Override
    public String createRecaptchaHtml(String arg0, Properties arg1) {
      return null;
    }

    @Override
    public String createRecaptchaHtml(String arg0, String arg1, Integer arg2) {
      return null;
    }
  }
}
