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

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility class for encrypting and decrypting value with a provided key. The value is encrypted using the DESede algorithm and then Base64-encoded.
 * 
 * @author Joakim Olsson
 */
public class EncryptionUtil {

  public static final String KEY_PROPERTY = "ssnEncryptionKey";

  /**
   * Encrypts the provided value using the provided key.
   * 
   * @param value The value to encrypt.
   * @return The encrypted Base64-encoded string.
   */
  public static String encrypt(String value) {
    String encryptedString = null;
    if (value != null) {
      encryptedString = dercryptOrEncrypt(true, value.getBytes());
    }
    return encryptedString;
  }

  /**
   * Decrypts the provided value using the provided key.
   * 
   * @param value The Base64-encoded value to decrypt.
   * @return The decrypted string.
   */
  public static String decrypt(String value) {
    String decryptedString = null;
    if (value != null) {
      byte[] encryptedByteArray = Base64.decodeBase64(value.getBytes());
      decryptedString = dercryptOrEncrypt(false, encryptedByteArray);
    }
    return decryptedString;
  }
  
  /**
   * Method for encryption and decryption of data
   * @param encryptMode The mode to use if true encryption is used.
   * @param value The information to encrypt or decrypt.
   * @return
   */
  private static String dercryptOrEncrypt(boolean encryptMode, byte[] value) {
    String key = System.getProperty(KEY_PROPERTY);
    Cipher cipher = null;
    String result = null;
    // Set chosen cipher mode to use.
    int opmode = encryptMode ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
    if (key != null) {
      byte[] preSharedKey = key.getBytes();
      byte[] ssnBytes = null;
      SecretKey aesKey = new SecretKeySpec(preSharedKey, "DESede");
      try {
        cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        String initialVector = "vardval0";
        IvParameterSpec ivs = new IvParameterSpec(initialVector.getBytes());
        cipher.init(opmode, aesKey, ivs);
        ssnBytes = cipher.doFinal(value);
        // If data is encrypted encode the result to base 64. 
        if (opmode == Cipher.ENCRYPT_MODE) {
          ssnBytes = Base64.encodeBase64(ssnBytes);
        }
        
        result = new String(ssnBytes);
      } catch (GeneralSecurityException e) {
        throw new RuntimeException("Unable to decrypt the provided value", e);
      }
    }
    return result;
  }
}
