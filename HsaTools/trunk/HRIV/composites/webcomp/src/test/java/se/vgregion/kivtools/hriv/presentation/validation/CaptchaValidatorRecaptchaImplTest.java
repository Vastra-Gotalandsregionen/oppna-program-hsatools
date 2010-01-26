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
