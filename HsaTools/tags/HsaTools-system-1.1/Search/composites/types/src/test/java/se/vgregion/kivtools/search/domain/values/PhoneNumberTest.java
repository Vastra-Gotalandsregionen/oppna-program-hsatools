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

package se.vgregion.kivtools.search.domain.values;

import static org.junit.Assert.*;

import org.junit.Test;

public class PhoneNumberTest {
  @Test
  public void swedishCountryCodeIsHandledByGetFormattedPhoneNumber() {
    PhoneNumber result = PhoneNumber.createPhoneNumber("+4630012345").getFormattedPhoneNumber();
    assertNotNull("formatted phone number", result);
    assertEquals("result phone number", "0300 - 123 45", result.toString());
  }

  @Test
  public void phoneNumbersShorterThanSevenCharactersAreReturnedAsIsFromGetFormattedPhoneNumber() {
    PhoneNumber result = PhoneNumber.createPhoneNumber("12345").getFormattedPhoneNumber();
    assertNotNull("formatted phone number", result);
    assertEquals("result phone number", "12345", result.toString());
  }

  @Test
  public void twoCharacterAreaCodesAreHandledByGetFormattedPhoneNumber() {
    PhoneNumber result = PhoneNumber.createPhoneNumber("+4681212345").getFormattedPhoneNumber();
    assertNotNull("formatted phone number", result);
    assertEquals("result phone number", "08 - 121 23 45", result.toString());
  }

  @Test
  public void threeCharacterAreaCodesAreHandledByGetFormattedPhoneNumber() {
    PhoneNumber result = PhoneNumber.createPhoneNumber("+4631212345").getFormattedPhoneNumber();
    assertNotNull("formatted phone number", result);
    assertEquals("result phone number", "031 - 21 23 45", result.toString());
  }

  @Test
  public void tooShortPhoneNumbersAfterStrippingSwedishCountryCodeIsReturnedAsIs() {
    PhoneNumber result = PhoneNumber.createPhoneNumber("+460049922").getFormattedPhoneNumber();
    assertNotNull("formatted phone number", result);
    assertEquals("result phone number", "+460049922", result.toString());
  }
}
