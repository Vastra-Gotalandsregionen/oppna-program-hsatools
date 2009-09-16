package se.vgregion.hsatools.testtools.services;

import java.util.HashMap;
import java.util.Map;

public class Service {

  private static Map<String, String> signatures = new HashMap<String, String>();
  private static Map<String, String> units = new HashMap<String, String>();
  private static final String DEFAULT_UNIT = "SE2321000131-E000000006740";

  public static void addSignature(String artifact, String ssn) {
    signatures.put(artifact, ssn);
  }

  public static String getSignature(String artifact) {
    return signatures.remove(artifact);
  }

  public static String getUnit(String ssn) {
    String unitId = units.get(ssn);
    if (unitId == null) {
      unitId = DEFAULT_UNIT;
    }
    return unitId;
  }

  public static void setUnit(String ssn, String unitId) {
    units.put(ssn, unitId);
  }

}
