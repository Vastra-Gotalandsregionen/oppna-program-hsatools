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
/**
 * 
 */
package se.vgregion.kivtools.search.util;

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 * 
 *         Ldap specific parsing. Received from the Kiv Admin project
 */
public class LdapParse {
  private static Map<String, String> objectStatusText = new HashMap<String, String>();
  private static Map<String, String> dayNames = new HashMap<String, String>();

  static {
    objectStatusText.put("0", "[Klarmarkerad]");
    objectStatusText.put("10", "[Palett] Ny anställning");
    objectStatusText.put("11", "[Palett] Ansvarsnummer");
    objectStatusText.put("12", "[Palett] Titel");
    objectStatusText.put("20", "Ny person");
    objectStatusText.put("21", "[Västfolket] Namn");
    objectStatusText.put("22", "[Västfolket] Sekretessmark.");
    objectStatusText.put("30", "[Notes] E-postadress");

    dayNames.put("1", "Måndag");
    dayNames.put("2", "Tisdag");
    dayNames.put("3", "Onsdag");
    dayNames.put("4", "Torsdag");
    dayNames.put("5", "Fredag");
    dayNames.put("6", "Lördag");
    dayNames.put("0", "Söndag");
  }

  /**
   * Metod som tar en String som har $ som skiljetecken och ersätter detta med radmatningstecken.
   * 
   * @param in String som innehåller $-tecken
   * @return String där $ är utbytt mot \n
   */
  public static String convertMultilineBeforeView(String in) {
    if (in != null && in.length() > 0) {
      in = in.replace('$', '\n');
    }
    return in;
  }

  /**
   * Metod som kontrollerar om en internetadress börjar med <code>http://</code> eller <code>https://</code>. Om inte sätter den dit det prefixet.
   * 
   * @param in String-värde från inmatningsfält
   * @return String med värde som har pålagt prefix om värdet inte var tomt
   */
  public static String setInternetFormat(String in) {
    if (in != null && in.length() > 5) {
      if ((in.indexOf("http://") == -1 || in.indexOf("https://") == -1) && in.indexOf(".") != -1) {
        return "http://" + in;
      }
    }
    return "";
  }

  /**
   * Hämtar värde från en specifik plats från en Sträng med separator av typen $.
   * 
   * @param in - String som pekar på det dummy fält man populerar med värde
   * @param vec - Vector pos 0 String Håller en pekare på det dolda värdet i gui som egentligen är kopplat till ett ldap-attribut. pos 1 String Håller position på den substräng som ska hämtas från
   *          attributet som är utpekat i vector(0).
   * @return -String Det som strängen hade på angiven position. ex: hej$på$dig pos1 = på
   */
  public static String getAddressPart(String in, Vector<String> vec) {

    if (vec != null || vec.size() == 2) {
      String valueFromGUI = vec.get(0).trim();
      int pos = 0;
      try {
        pos = Integer.parseInt(vec.get(1));
      } catch (NumberFormatException e) {
        return "";
      }
      String[] tmp = valueFromGUI.split("\\$");
      if (tmp != null && tmp.length > 4) {
        if (pos == 4) {
          return convertPostNumberBeforeView(tmp[pos]);
        }
      }
      return tmp[pos].trim();
    }
    return "";
  }

  /**
   * Översätter ett string nummer till dag i klartext.
   * 
   * @param number - String nummer mellan 0-6 representerar varsin dag.
   * @return - String returnerar dag ex. "6" blir "Lördag".
   */
  public static String getDayName(String number) {
    String dayName = dayNames.get(number);

    if (dayName == null) {
      dayName = "";
    }

    return dayName;
  }

  /**
   * Kontroll som ser till att 'i' skrivs med liten bokstav. för iNummer.
   * 
   * @param in - String värde som är sparat i attributet <code>vgrInternalSedfInvoiceAddress</code>
   * @return - String formaterat värde.
   */
  public static String iNumberCheck(String in) {
    if (in != null && in.trim().length() > 0) {
      return in.toLowerCase();
    }
    return "";
  }

  /**
   * Kontrollerar om angivet datum är ett riktigt datum.
   * 
   * @param in String
   * @return om angivet datum är ett riktigt datum
   */
  public static boolean isValidDate(String in) {
    if (in != null && in.trim().length() > 0) {
      String rx = "\\d\\d";
      String timeStr = "";
      Pattern pattern = Pattern.compile(rx);
      Matcher match = pattern.matcher(in);
      while (match.find()) {
        timeStr += match.group();
      }
      if (timeStr.length() == 8) {
        return isValidDateFormat(in, "yyyyMMdd");
      }
    }
    return false;
  }

  /**
   * Kontrollerar att ett datum med specificerat format är riktigt.
   * 
   * @param value Datumet som skall kontrolleras.
   * @param pattern Datumformat på värdet som ska kontrolleras.
   * @return True om datumet är riktigt, annars false.
   */
  public static boolean isValidDateFormat(String value, String pattern) {
    boolean valid = true;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
      Date sd = dateFormat.parse(value);
      valid = value.equals(dateFormat.format(sd));
    } catch (ParseException e) {
      valid = false;
    }

    return valid;
  }

  /**
   * Konverterar datum/tid till zuluformat.
   * 
   * @param in - String datumangivelse i olika format
   * @return - Konverterat datum till zulutime format
   */
  public static String convertTimeToZulu(String in) {
    if (in != null && in.trim().length() > 0) {
      String rx = "\\d\\d";
      String timeStr = "";
      Pattern pattern = Pattern.compile(rx);
      Matcher match = pattern.matcher(in);
      while (match.find()) {
        timeStr += match.group();
      }
      if (timeStr.length() == 14) {
        if (isValidDateFormat(timeStr, "yyyyMMddHHmmss")) {
          return timeStr + "Z";
        }
      } else if (timeStr.length() == 12) {
        if (isValidDateFormat(timeStr, "yyyyMMddHHmm")) {
          return timeStr + "00Z";
        }
      } else if (timeStr.length() == 8) {
        if (isValidDateFormat(timeStr, "yyyyMMdd")) {
          return timeStr + "000000Z";
        }
      }
    }
    return "";
  }

  /**
   * Konverterar zulutime till läsbart datum ex.<code>20070101121500Z</code> blir <code>2007-01-01 12:15</code>
   * 
   * @param in - String zulutime
   * @return - String konverterat datum och tid
   */

  public static String convertZuluToTime(String in) {
    if (in != null && in.trim().length() == 15) {
      if (isValidDateFormat(in.replace("Z", ""), "yyyyMMddHHmmss")) {
        return in.substring(0, 4) + "-" + in.substring(4, 6) + "-" + in.substring(6, 8) + " " + in.substring(8, 10) + ":" + in.substring(10, 12);
      }
    }
    return "";
  }

  /**
   * Sorterar värden i array.
   * 
   * @param in -String[] med värden
   * @return -String[] med värden sorterade
   */
  public static String[] sortMultiValues(String[] in) {
    if (in != null && in.length > 1) {
      Arrays.sort(in);
    }
    return in;
  }

  /**
   * Konverterar postnummer så de stämmer överens med hur det ska sparas i katalogen.
   * 
   * @param in -String postnummer ex <code>123 45</code>
   * @return -String postnummer ex <code>12345</code>
   */
  public static String convertPostNumberBeforeSave(String in) {
    if (in != null && in.length() > 0) {
      in = in.replace(" ", "");
      if (in.length() == 5) {
        return in;
      }
    }
    return "";
  }

  /**
   * Konverterar postnummer så de stämmer överens med hur det ska visas för användaren.
   * 
   * @param in -String postnummer ex <code>12345</code>
   * @return -String postnummer ex <code>123 45</code>
   */
  public static String convertPostNumberBeforeView(String in) {
    if (in != null && in.trim().length() > 3) {
      return in.substring(0, 3) + " " + in.substring(3);
    }
    return in;
  }

  /**
   * Visar objektstatus i textform.
   * 
   * @param in Objektstatus som nummerisk sträng
   * @return Objektstatus som text
   */
  public static String vgrObjectStatusToText(String in) {
    String objectStatus = objectStatusText.get(in);

    if (objectStatus == null) {
      objectStatus = "";
    }

    return objectStatus;
  }

  /**
   * Konverterar peronnummer till hur det ska lagras i katalogen.
   * 
   * @param in -String personnummer inskrivet från gui
   * @return -String personnummer som det ska lagras i katalogen
   */
  public static String convertPersonNumberBeforeSave(String in) {
    if (in != null) {
      in = in.replace("-", "").replace(" ", "").trim();

    }
    return "";
  }

  /**
   * Konverterar personnummer så det är indelat med födelsedatum-nummer.
   * 
   * @param in - String värde från katalog
   * @return - String formaterat värde.
   */
  public static String convertPersonNumberBeforeView(String in) {
    if (in != null && in.trim().length() == 12) {
      return in.substring(0, 8) + " - " + in.substring(8);
    }
    return "";
  }

  /**
   * Konverterar en eller flera rader med telefontider så de blir mer lättlästa.
   * 
   * @param in -String[] med telefontider i ldap-format
   * @return -String[] med läsbara telefontider
   */
  public static String[] convertTelephoneTime(String[] in) {
    if (in != null && in.length > 0) {
      for (int i = 0; i < in.length; i++) {
        in[i] = convertTelephoneTime(in[i]);
      }
    }
    return in;
  }

  /**
   * Escape av LDAP-specifika tecken som kommatecken och backslash.
   * 
   * @param in - String värde som ska kontrolleras innan det sparas
   * @return - String med bortescapade tecken.
   */
  public static String escapeDN(String in) {
    in = in.trim();
    final StringBuffer result = new StringBuffer();

    final StringCharacterIterator iterator = new StringCharacterIterator(in);
    char character = iterator.current();
    while (character != CharacterIterator.DONE) {
      if (character == ',') {
        result.append("\\,");
      } else if (character == '\\') {
        result.append("\\\\");
      } else {
        result.append(character);
      }
      character = iterator.next();
    }
    return result.toString();
  }

  /**
   * Konverterar en String med telefontid i ldap-format.
   * 
   * @param in - String en telefontid i ldap-format
   * @return - String med telefontid som är mer lättläst.
   */
  public static String convertTelephoneTime(String in) {
    if (in != null && in.trim().length() > 0 && in.indexOf("#") != -1) {
      try {
        String[] tmp = in.trim().split("#");
        String tmpStr = tmp[0].replace("-", "").trim();
        String startDay = getDayName(tmpStr.substring(0, 1));
        String endDay = getDayName(tmpStr.substring(1));
        if (startDay.equals(endDay)) {
          return startDay + " " + tmp[1] + " " + tmp[2];
        } else {
          return startDay + "-" + endDay + " " + tmp[1] + " " + tmp[2];
        }
      } catch (Exception e) {
        return "";
      }
    }
    return "";
  }

  public static String showLoginStatus(String in) {
    if (in != null && in.equalsIgnoreCase("false")) {
      return "[Login PÅ]";
    } else {
      return "[Login AV]";
    }
  }

  /**
   * Filters the query, prevents ldap injection.
   * 
   * @param filter
   * @see http://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java
   * @return escaped ldap search filter
   */
  public static final String escapeLDAPSearchFilter(String filter) {
    if (filter == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < filter.length(); i++) {
      char curChar = filter.charAt(i);
      switch (curChar) {
        case '\\':
          sb.append("\\5c");
          break;
        case '*':
          sb.append("\\2a");
          break;
        case '(':
          sb.append("\\28");
          break;
        case ')':
          sb.append("\\29");
          break;
        case '\u0000':
          sb.append("\\00");
          break;
        default:
          sb.append(curChar);
      }
    }
    return sb.toString();
  }
}
