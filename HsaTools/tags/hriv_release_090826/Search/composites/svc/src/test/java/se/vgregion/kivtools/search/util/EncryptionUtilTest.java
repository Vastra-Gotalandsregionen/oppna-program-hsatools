/**
 * Copyright 2009 Västa Götalandsregionen
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
package se.vgregion.kivtools.search.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class EncryptionUtilTest {
  private static final String ENCRYPTED_VALUE = "GQ7WwYfjYMqyJqCRafOTPQ==";
  private static final String DECRYPTED_VALUE = "DecryptedValue";
  private static final String KEY = "ACME1234ACME1234QWERT123";

  @Before
  public void setup() {
    System.setProperty(EncryptionUtil.KEY_PROPERTY, KEY);
  }

  @Test
  public void testInstantiation() {
    EncryptionUtil encryptionUtil = new EncryptionUtil();
    assertNotNull(encryptionUtil);
  }

  @Test
  public void testEncryptNullValue() {
    assertNull("A null-value should be returned for a null input value", EncryptionUtil.encrypt(null));
  }

  @Test
  public void testEncryptNullKey() {
    System.getProperties().remove(EncryptionUtil.KEY_PROPERTY);
    assertNull("A null-value should be returned for a null input key", EncryptionUtil.encrypt(DECRYPTED_VALUE));
  }

  @Test
  public void testEncrypt() {
    assertEquals("Unexpected encrypted value", ENCRYPTED_VALUE, EncryptionUtil.encrypt(DECRYPTED_VALUE));
  }

  @Test
  public void testDecryptNullValue() {
    assertNull("A null-value should be returned for a null input value", EncryptionUtil.decrypt(null));
  }

  @Test
  public void testDecryptNullKey() {
    System.getProperties().remove(EncryptionUtil.KEY_PROPERTY);
    assertNull("A null-value should be returned for a null input key", EncryptionUtil.decrypt(ENCRYPTED_VALUE));
  }

  @Test
  public void testDecrypt() {
    assertEquals("Unexpected encrypted value", DECRYPTED_VALUE, EncryptionUtil.decrypt(ENCRYPTED_VALUE));
  }
}
