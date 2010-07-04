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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.vgregion.kivtools.search.domain.util.Evaluator;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Helper utilities
 */

public class AddressHelper implements Serializable {
  private static final long serialVersionUID = 1L;

  // to any special address info
  private static final List<String> VALID_STREET_SUFFIX = new LinkedList<String>();
  private static final List<String> EXCEPTIONED_STREET_SUFFIX = new LinkedList<String>();
  private static final int ZIPCODE_LENGTH = 5;

  // Define all valid street suffix as well as all exceptioned street suffix
  static {
    VALID_STREET_SUFFIX.add("gata");
    VALID_STREET_SUFFIX.add("v\u00E4g");
    VALID_STREET_SUFFIX.add("plats");
    VALID_STREET_SUFFIX.add("torg");
    VALID_STREET_SUFFIX.add("park");
    VALID_STREET_SUFFIX.add("leden");
    VALID_STREET_SUFFIX.add("str\u00E5ket");
    VALID_STREET_SUFFIX.add("backe");
    VALID_STREET_SUFFIX.add("g\u00E5ngen");
    VALID_STREET_SUFFIX.add("\u00F6sterled");
    VALID_STREET_SUFFIX.add("k\u00E4rnsjukhuset");
    VALID_STREET_SUFFIX.add("dalslands sjukhus");
    VALID_STREET_SUFFIX.add("stig");
    VALID_STREET_SUFFIX.add("centrum");
    VALID_STREET_SUFFIX.add("aveny");

    EXCEPTIONED_STREET_SUFFIX.add("bruna str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("vita str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("r\u00F6da str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("gr\u00F6na str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("bl\u00E5 str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("gula str\u00E5ket");
  }

  /**
   * Helper-method to check if a string contains street information.
   * 
   * @param text The string to check.
   * @return True if the provided string contains street information, otherwise false.
   */
  private static boolean isStreet(String text) {
    boolean result = false;

    result |= containsListWord(text, VALID_STREET_SUFFIX);
    result &= !containsListWord(text, EXCEPTIONED_STREET_SUFFIX);

    return result;
  }

  /**
   * Helper-method to check if a string contains any of the words in the provided list of words.
   * 
   * @param text The string to check.
   * @param words The list of words to check against.
   * @return True if the string contains any of the words in the provided list, otherwise false.
   */
  private static boolean containsListWord(String text, List<String> words) {
    boolean result = false;

    if (!StringUtil.isEmpty(text)) {
      for (String word : words) {
        result |= text.toLowerCase().contains(word);
      }
    }

    return result;
  }

  /**
   * Convert a List of Strings (containing an address to an Address object).
   * 
   * @param addressList The list of strings to populate the Address object with.
   * @return A populated Address object.
   */
  public static Address convertToAddress(List<String> addressList) {
    Address address = new Address();
    boolean foundCity = false;
    boolean foundPostalCode = false;

    if (!Evaluator.isEmpty(addressList)) {
      int size = addressList.size();

      if (size >= 3) {
        // let´s make a copy to handle this case
        List<String> additionalInfo = new ArrayList<String>(addressList);

        // The list has at least 3 items let´s see if we can find out some things

        // last item should be city
        String temp = additionalInfo.get(size - 1);
        if (StringUtil.containsNoNumbers(temp)) {
          address.setCity(temp);
          foundCity = true;
          // remove city from list
          additionalInfo.remove(size - 1);
        }

        // the last - 1 item should be postal code.
        String zCode = additionalInfo.get(additionalInfo.size() - 1);
        if (ZipCode.isValid(zCode)) {
          address.setZipCode(new ZipCode(zCode));
          foundPostalCode = true;
          // remove postal code from list
          additionalInfo.remove(additionalInfo.size() - 1);
        }

        // let´s find the street
        int streetRow = findStreet(additionalInfo);

        if (streetRow != -1) {
          temp = additionalInfo.get(streetRow);
          address.setStreet(temp);
          additionalInfo.remove(streetRow);
        }

        if (foundCity && foundPostalCode) {
          // found city and zipCode that´s good enough
          address.setAdditionalInfo(additionalInfo);
        } else {
          // not an ok mapping we can´t rip out enough information
          address = new Address();
          address.setAdditionalInfo(addressList);
        }
      } else {
        address.setAdditionalInfo(addressList);
      }
    }
    return address;
  }

  /**
   * Prints an address to standard output.
   * 
   * @param address The address to print.
   */
  public static void print(Address address) {
    System.out.println("**** print adress ****");
    System.out.println("Street:" + address.getStreet() + "*");
    System.out.println("zipCode:" + address.getZipCode().getFormattedZipCode());
    System.out.println("City:" + address.getCity());
    System.out.println("Additional Info:" + address.getAdditionalInfoToString());
  }

  private static Address createNewUnparsedAddress(List<String> origAddressList) {
    Address address = new Address();
    address.setAdditionalInfo(origAddressList);
    return address;
  }

  private static boolean isStreetMoreThanOnce(List<String> adressList) {
    String temp = "";
    int size = adressList.size();
    int foundStreet = 0;
    for (int row = size - 1; row >= 0; row--) {
      temp = adressList.get(row);
      if (AddressHelper.isStreet(temp)) {
        foundStreet++;
      }
    }
    return foundStreet > 1;
  }

  /**
   * Convert a List of Strings (containing an address to an Address object). The entry can look like:
   * 
   * <pre>
   * ex1.
   * Dan Anderssonsgatan 4
   * 422 04 Hisingsbacka
   * 
   * ex2.
   * Hus 14 plan 2
   * Dan Anderssonsgatan 4
   * 42204 Hisingsbacka
   * 
   * ex3.
   * Dan Anderssonsgatan 4
   * 422 04 Hisingsbacka
   * Gröna stråket
   * 
   * ex4.
   * Dan Anderssonsgatan 4
   * 422 04 Hisingsbacka
   * Till vänster via Huvudentré
   * Hiss A
   * Plan 10
   * </pre>
   * 
   * @param origAddressList The list of strings which the address is built from.
   * @return A populated Address-object.
   */
  public static Address convertToStreetAddress(List<String> origAddressList) {
    Address address = new Address();
    boolean foundCity = false;
    boolean foundZipCode = false;
    boolean foundStreet = false;

    if (!Evaluator.isEmpty(origAddressList)) {

      // let´s make a copy to handle this case
      List<String> tempAdressList = new ArrayList<String>(origAddressList);

      int size = tempAdressList.size();

      if (size <= 1) {
        // no evaluation
        address = createNewUnparsedAddress(origAddressList);
      } else {
        // 1. rip out the street
        // *********************
        // this is a quite tricky one since sometimes the street name is a part of additional information
        // so first check if street address exists twice
        if (isStreetMoreThanOnce(tempAdressList)) {
          // we have street name in two different locations we give up
          address = createNewUnparsedAddress(origAddressList);
        } else {
          int streetRow = findStreet(tempAdressList);

          if (streetRow == -1) {
            // if there was no street we can´t do it
            address = createNewUnparsedAddress(origAddressList);
          } else {
            // Street was found, set it in address and remove from list of adress lines.
            foundStreet = true;
            String street = tempAdressList.get(streetRow);
            address.setStreet(street);
            tempAdressList.remove(streetRow);

            size = tempAdressList.size();

            // 2. rip out postal code (always 5 characters)
            // *******************************************
            int zipRow = findZipAndCity(tempAdressList);

            if (zipRow != -1) {
              // Zip and city found
              foundZipCode = true;
              String zipAndCity = tempAdressList.get(zipRow);
              String zipCode = getZipcode(zipAndCity);
              address.setZipCode(new ZipCode(zipCode));

              String city = getCity(zipAndCity);
              address.setCity(city);
              foundCity = !StringUtil.isEmpty(city);

              if (foundZipCode) {
                // ok remove this row
                tempAdressList.remove(zipRow);
                if (foundCity) {
                  address.setAdditionalInfo(tempAdressList);
                }
              }
            }

            if (!foundStreet || !foundCity) {
              // if there was no street or no city found
              address = createNewUnparsedAddress(origAddressList);
            }
          }
        }
      }
    }
    return address;
  }

  /**
   * Helper-method to get the city from an address line.
   * 
   * @param zipAndCity The address line containing zip and city.
   * @return The city from the provided address line.
   */
  private static String getCity(String zipAndCity) {
    String tempCity = zipAndCity;
    String city = null;

    if (tempCity.length() > ZIPCODE_LENGTH) {
      tempCity = tempCity.substring(ZIPCODE_LENGTH + 1);

      int position;
      position = getCityPosition(tempCity);
      tempCity = tempCity.substring(position);
      if (!StringUtil.isEmpty(tempCity)) {
        city = tempCity;
      }
    }
    return city;
  }

  /**
   * Helper-method to get the zipcode from an address line.
   * 
   * @param zipAndCity The address line containing zip and city.
   * @return The zipcode from the provided address line.
   */
  private static String getZipcode(String zipAndCity) {
    String tempZip = zipAndCity.replaceAll(" ", "");
    String zip = null;

    if (tempZip.length() == ZIPCODE_LENGTH && StringUtil.containsOnlyNumbers(tempZip, false)) {
      zip = tempZip;
    } else if (tempZip.length() > ZIPCODE_LENGTH) {
      tempZip = tempZip.substring(0, ZIPCODE_LENGTH);
      if (StringUtil.containsOnlyNumbers(tempZip, false)) {
        // ok we have a postalCode here...
        zip = tempZip;
      }
    }

    return zip;
  }

  /**
   * Helper-method to find which row (if any) contains the zip and city.
   * 
   * @param addressList The list of strings in the address.
   * @return The index of the row in the provided list of strings that contains the zip and city.
   */
  private static int findZipAndCity(List<String> addressList) {
    int foundRow = -1;

    String temp = "";
    for (int row = addressList.size() - 1; row >= 0; row--) {
      temp = addressList.get(row);
      temp = temp.replaceAll(" ", "");
      int tempSize = temp.length();
      if (tempSize == ZIPCODE_LENGTH) {
        // there might be a zipCode here
        if (StringUtil.containsOnlyNumbers(temp, false)) {
          // ok we have a postalCode here...
          foundRow = row;
          break;
        }
      } else {
        if (tempSize > ZIPCODE_LENGTH) {
          // there might be a zipCode here
          String tempZip = temp.substring(0, ZIPCODE_LENGTH);
          if (StringUtil.containsOnlyNumbers(tempZip, false)) {
            // ok we have a postalCode here...
            foundRow = row;
          }
        }
      }
    }

    return foundRow;
  }

  /**
   * Helper-method to find which row (if any) is the street address.
   * 
   * @param addressList The list of strings in the address.
   * @return The index of the row in the provided list of strings that is the street address.
   */
  private static int findStreet(List<String> addressList) {
    int foundRow = -1;

    String temp = "";
    for (int row = addressList.size() - 1; row >= 0; row--) {
      temp = addressList.get(row);
      if (AddressHelper.isStreet(temp)) {
        foundRow = row;
        break;
      }
    }

    return foundRow;
  }

  /**
   * Helper-method to find the position within the provided string where the city starts.
   * 
   * @param zipAndCity The string containing both zip and city.
   * @return The position in the provided string where the city starts.
   */
  private static int getCityPosition(String zipAndCity) {
    int position = 0;
    boolean leadingDigits = true;
    for (int i = 0; i < zipAndCity.length() && leadingDigits; i++) {
      String c = zipAndCity.substring(i, i + 1);
      if (StringUtil.isEmpty(c)) {
        position++;
        continue;
      }
      if (StringUtil.isInteger(c)) {
        position++;
      } else {
        leadingDigits = false;
      }
    }
    return position;
  }
}
