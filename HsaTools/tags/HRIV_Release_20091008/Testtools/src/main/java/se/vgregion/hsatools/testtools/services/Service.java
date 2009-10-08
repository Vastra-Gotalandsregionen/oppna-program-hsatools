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
