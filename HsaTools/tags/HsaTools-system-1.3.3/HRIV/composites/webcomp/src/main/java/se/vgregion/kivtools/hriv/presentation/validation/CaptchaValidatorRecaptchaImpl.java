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

package se.vgregion.kivtools.hriv.presentation.validation;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;

/**
 * Implementation of the CaptchaValidator interface based on recaptcha.net's service.
 * 
 * @author Joakim Olsson
 */
public class CaptchaValidatorRecaptchaImpl implements CaptchaValidator {
  private ReCaptcha recaptcha;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(String captchaChallenge, String captchaResponse, String remoteAddress) {
    ReCaptchaResponse response = recaptcha.checkAnswer(remoteAddress, captchaChallenge, captchaResponse);

    return response.isValid();
  }

  public void setRecaptcha(ReCaptcha recaptcha) {
    this.recaptcha = recaptcha;
  }
}
