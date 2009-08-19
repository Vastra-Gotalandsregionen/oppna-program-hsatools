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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

    String key = System.getProperty(KEY_PROPERTY);

    if (value != null && key != null) {
      byte[] preSharedKey = key.getBytes();
      SecretKey aesKey = new SecretKeySpec(preSharedKey, "DESede");
      Cipher cipher;
      byte[] cipherText;
      try {
        cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        String initialVector = "vardval0";
        IvParameterSpec ivs = new IvParameterSpec(initialVector.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivs);
        cipherText = cipher.doFinal(value.getBytes());
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Unable to encrypt the provided value", e);
      } catch (NoSuchPaddingException e) {
        throw new RuntimeException("Unable to encrypt the provided value", e);
      } catch (InvalidKeyException e) {
        throw new RuntimeException("Unable to encrypt the provided value", e);
      } catch (InvalidAlgorithmParameterException e) {
        throw new RuntimeException("Unable to encrypt the provided value", e);
      } catch (IllegalBlockSizeException e) {
        throw new RuntimeException("Unable to encrypt the provided value", e);
      } catch (BadPaddingException e) {
        throw new RuntimeException("Unable to encrypt the provided value", e);
      }
      encryptedString = new String(Base64.encodeBase64(cipherText));
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
    String key = System.getProperty(KEY_PROPERTY);

    if (value != null && key != null) {
      byte[] encryptedByteArray = Base64.decodeBase64(value.getBytes());
      byte[] preSharedKey = key.getBytes();
      SecretKey aesKey = new SecretKeySpec(preSharedKey, "DESede");
      byte[] ssnBytes = null;
      try {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        String initialVector = "vardval0";
        IvParameterSpec ivs = new IvParameterSpec(initialVector.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivs);
        ssnBytes = cipher.doFinal(encryptedByteArray);
      } catch (InvalidKeyException e) {
        throw new RuntimeException("Unable to decrypt the provided value", e);
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Unable to decrypt the provided value", e);
      } catch (NoSuchPaddingException e) {
        throw new RuntimeException("Unable to decrypt the provided value", e);
      } catch (IllegalBlockSizeException e) {
        throw new RuntimeException("Unable to decrypt the provided value", e);
      } catch (BadPaddingException e) {
        throw new RuntimeException("Unable to decrypt the provided value", e);
      } catch (InvalidAlgorithmParameterException e) {
        throw new RuntimeException("Unable to decrypt the provided value", e);
      }
      decryptedString = new String(ssnBytes);
    }
    return decryptedString;
  }
}
