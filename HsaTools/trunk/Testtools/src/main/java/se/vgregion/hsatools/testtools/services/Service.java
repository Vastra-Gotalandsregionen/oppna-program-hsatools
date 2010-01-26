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
package se.vgregion.hsatools.testtools.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Test-version of the signature service.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public class Service {

  private static Map<String, String> signatures = new HashMap<String, String>();
  private static Map<String, String> units = new HashMap<String, String>();
  private static final String DEFAULT_UNIT = "SE2321000131-E000000006740";

  /**
   * Adds a signature to the map of signatures.
   * 
   * @param artifact The artifact to store the signature for.
   * @param ssn The signature to store (a simple SSN for the test-version).
   */
  public static void addSignature(String artifact, String ssn) {
    signatures.put(artifact, ssn);
  }

  /**
   * Gets the signature for the provided artifact.
   * 
   * @param artifact The artifact to find the signature for.
   * @return The signature stored for the provided artifact.
   */
  public static String getSignature(String artifact) {
    return signatures.remove(artifact);
  }

  /**
   * Gets the currently registered unit for the provided SSN.
   * 
   * @param ssn The SSN to find the currently registered unit for.
   * @return The unit currently registered for the provided SSN or a default unit in case none is registered.
   */
  public static String getUnit(String ssn) {
    String unitId = units.get(ssn);
    if (unitId == null) {
      unitId = DEFAULT_UNIT;
    }
    return unitId;
  }

  /**
   * Sets the registered unit for the provided SSN.
   * 
   * @param ssn The SSN to register a unit for.
   * @param unitId The hsaIdentity of the unit to register.
   */
  public static void setUnit(String ssn, String unitId) {
    units.put(ssn, unitId);
  }
}
