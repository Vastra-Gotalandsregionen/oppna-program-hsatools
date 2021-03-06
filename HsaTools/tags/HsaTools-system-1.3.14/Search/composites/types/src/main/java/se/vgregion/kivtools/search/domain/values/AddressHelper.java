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

import org.apache.log4j.Logger;

import se.vgregion.kivtools.search.domain.util.Evaluator;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Helper utilities
 */

public class AddressHelper implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = Logger.getLogger(AddressHelper.class);

  // to any special address info
  private static final List<String> VALID_STREET_SUFFIX = new LinkedList<String>();
  private static final List<String> EXCEPTIONED_STREET_SUFFIX = new LinkedList<String>();
  private static final List<String> INVALID_CITY_WORDS = new LinkedList<String>();
  private static final int ZIPCODE_LENGTH = 5;

  // Define all valid street suffix as well as all exceptioned street suffix
  static {
    VALID_STREET_SUFFIX.add("gata");
    VALID_STREET_SUFFIX.add("v\u00E4g");
    VALID_STREET_SUFFIX.add("plats");
    VALID_STREET_SUFFIX.add("torg");
    VALID_STREET_SUFFIX.add("park");
    VALID_STREET_SUFFIX.add("leden");
    VALID_STREET_SUFFIX.add("liden");
    VALID_STREET_SUFFIX.add("str\u00E5ket");
    VALID_STREET_SUFFIX.add("backe");
    VALID_STREET_SUFFIX.add("g\u00E5ngen");
    VALID_STREET_SUFFIX.add("\u00F6sterled");
    VALID_STREET_SUFFIX.add("k\u00E4rnsjukhuset");
    VALID_STREET_SUFFIX.add("dalslands sjukhus");
    VALID_STREET_SUFFIX.add("stig");
    VALID_STREET_SUFFIX.add("centrum");
    VALID_STREET_SUFFIX.add("aveny");
    VALID_STREET_SUFFIX.add("allé");
    VALID_STREET_SUFFIX.add("gr\u00E4nd");
    VALID_STREET_SUFFIX.add("lillek\u00E4rr s\u00F6dra");
    VALID_STREET_SUFFIX.add("kung\u00E4lvs sjukhus");
    VALID_STREET_SUFFIX.add("m\u00F6lndals sjukhus");
    VALID_STREET_SUFFIX.add("g\u00E4rdet");
    VALID_STREET_SUFFIX.add("jullen 3");
    VALID_STREET_SUFFIX.add("jullen 4");
    VALID_STREET_SUFFIX.add("m\u00f6lnlycke fabriker");
    VALID_STREET_SUFFIX.add("carlanderska sjukhemmet");
    VALID_STREET_SUFFIX.add("lilla bommen");
    VALID_STREET_SUFFIX.add("\u00F6stra sjukhuset");
    VALID_STREET_SUFFIX.add("sj\u00F6porten");
    VALID_STREET_SUFFIX.add("uddevalla sjukhus");
    VALID_STREET_SUFFIX.add("lysekils sjukhus");
    VALID_STREET_SUFFIX.add("v\u00E5rdcentralen styrs\u00F6");
    VALID_STREET_SUFFIX.add("sj\u00F6kanten");
    VALID_STREET_SUFFIX.add("h\u00E4lle lider");
    VALID_STREET_SUFFIX.add("amund\u00F6n");
    VALID_STREET_SUFFIX.add("sjukhuset i lidk\u00F6ping");
    VALID_STREET_SUFFIX.add("s\u00F6dra \u00E4lvsborgs sjukhus");
    VALID_STREET_SUFFIX.add("alings\u00E5s lasarett");
    VALID_STREET_SUFFIX.add("sjukhuset i falk\u00F6ping");
    VALID_STREET_SUFFIX.add("hestra midg\u00E5rd");
    VALID_STREET_SUFFIX.add("billeberg eriksg\u00E5rden");
    VALID_STREET_SUFFIX.add("ytterj\u00E4rna");
    VALID_STREET_SUFFIX.add("hedeg\u00E5rden");
    VALID_STREET_SUFFIX.add("tanumstrand");
    VALID_STREET_SUFFIX.add("esplanaden");

    EXCEPTIONED_STREET_SUFFIX.add("bruna str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("vita str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("r\u00F6da str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("gr\u00F6na str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("bl\u00E5 str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("gula str\u00E5ket");
    EXCEPTIONED_STREET_SUFFIX.add("ing\u00E5ngen");
    EXCEPTIONED_STREET_SUFFIX.add("bussh\u00E5llsplats");
    EXCEPTIONED_STREET_SUFFIX.add("\u00F6stra sjukhuset");
    EXCEPTIONED_STREET_SUFFIX.add("m\u00F6lndals sjukhus");
    EXCEPTIONED_STREET_SUFFIX.add("kung\u00E4lvs sjukhus");
    EXCEPTIONED_STREET_SUFFIX.add("uddevalla sjukhus");
    EXCEPTIONED_STREET_SUFFIX.add("lysekils sjukhus");
    EXCEPTIONED_STREET_SUFFIX.add("bl\u00E5 gata");
    EXCEPTIONED_STREET_SUFFIX.add("v\u00E4nerparken");
    EXCEPTIONED_STREET_SUFFIX.add("v\u00E4stra gatan");
    EXCEPTIONED_STREET_SUFFIX.add("s\u00F6dra gatan");
    EXCEPTIONED_STREET_SUFFIX.add("\u00F6stra gatan");
    EXCEPTIONED_STREET_SUFFIX.add("norra gatan");
    EXCEPTIONED_STREET_SUFFIX.add("sjukhusparken");
    EXCEPTIONED_STREET_SUFFIX.add("hemsjukv\u00E5rdsl\u00E4karna");
    EXCEPTIONED_STREET_SUFFIX.add("v\u00E5rdcentrum");
    EXCEPTIONED_STREET_SUFFIX.add("parkeringstillst\u00E5nd");
    EXCEPTIONED_STREET_SUFFIX.add("sjukhuset i lidk\u00F6ping");
    EXCEPTIONED_STREET_SUFFIX.add("s\u00F6dra \u00E4lvsborgs sjukhus");
    EXCEPTIONED_STREET_SUFFIX.add("alings\u00E5s lasarett");
    EXCEPTIONED_STREET_SUFFIX.add("sjukhuset i falk\u00F6ping");

    INVALID_CITY_WORDS.add("bussh\u00E5llsplats");
    INVALID_CITY_WORDS.add("bottenv\u00E5n");
    INVALID_CITY_WORDS.add("gata");
    INVALID_CITY_WORDS.add("v\u00E4nerparken");
    INVALID_CITY_WORDS.add("sjukhus");
    INVALID_CITY_WORDS.add("hemsjukv\u00E5rdsl\u00E4karna");
    INVALID_CITY_WORDS.add("v\u00E5rdcentrum");
    INVALID_CITY_WORDS.add("parkeringstillst\u00E5nd");
    INVALID_CITY_WORDS.add("r\u00F6ntgen");
  }

  /**
   * Convert a raw ldap address string to an Address object.
   * 
   * @param address - raw ldap address.
   * @return A populated Address object.
   */
  public static Address convertToAddress(String adrstr) {
    List<String> components = new ArrayList<String>();
    String[] split = adrstr.split("\\$");
    for (String string : split) {
      components.add(string.trim());
    }
    return convertToAddress(components);
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
          address = createNewUnparsedAddress(addressList, "postal");
        }
      } else {
        address = createNewUnparsedAddress(addressList, "postal");
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
   * @param origAddressRows The strings which the address is built from.
   * @return A populated Address-object.
   */
  public static Address convertToStreetAddress(List<String> origAddressRows) {
    Address address = new Address();
    boolean foundCity = false;
    boolean foundZipCode = false;
    String zipCode = null;
    String city = null;

    if (!Evaluator.isEmpty(origAddressRows)) {
      // let´s make a copy to handle this case
      List<String> tempAdressList = new ArrayList<String>(origAddressRows);

      int size = tempAdressList.size();

      if (size <= 1) {
        // no evaluation
        address = createNewUnparsedAddress(origAddressRows, "street");
      } else {
        // 1. rip out the street
        // *********************
        // this is a quite tricky one since sometimes the street name is a part of additional information
        // so first check if street address exists twice
        int streetRow = findStreet(tempAdressList);
        if (streetRow == -1) {
          // if there was no street we can´t do it
          address = createNewUnparsedAddress(origAddressRows, "street");
        } else {
          // Street was found, set it in address and remove from list of adress lines.
          String street = tempAdressList.get(streetRow);
          tempAdressList.remove(streetRow);

          size = tempAdressList.size();

          // 2. rip out postal code (always 5 characters)
          // *******************************************
          if (!isZipAndCityInSeperateRowsUpdateAddress(tempAdressList, address)) {
            int zipRow = findZipAndCity(tempAdressList);

            if (zipRow != -1) {
              // Zip and city found
              foundZipCode = true;
              String zipAndCity = tempAdressList.get(zipRow);
              zipCode = getZipcode(zipAndCity);
              city = getCity(zipAndCity);
              foundCity = !StringUtil.isEmpty(city);

              if (foundZipCode) {
                // ok remove this row
                tempAdressList.remove(zipRow);
                if (foundCity) {
                  address.setAdditionalInfo(tempAdressList);
                }
              }
              address.setZipCode(new ZipCode(zipCode));
              address.setCity(city);
            }

            if (!foundCity) {
              // if there was no city found
              address = createNewUnparsedAddress(origAddressRows, "street");
            }
          }

          address.setStreet(street);
        }
      }
    }
    return address;
  }

  private static boolean isZipAndCityInSeperateRowsUpdateAddress(List<String> tempAddressList, Address address) {
    boolean foundZipCode = false;
    boolean foundCity = false;
    boolean successFoundCityAndZipCode = false;
    int zipRow = findZipCode(tempAddressList);
    String city = null;
    String zipCode = null;
    if (zipRow != -1) {
      zipCode = tempAddressList.get(zipRow);
      foundZipCode = !StringUtil.isEmpty(zipCode);
      int cityRow = findCity(tempAddressList);
      if (cityRow != -1) {
        city = tempAddressList.get(cityRow);
        foundCity = !StringUtil.isEmpty(city);
      }
      if (foundZipCode && foundCity) {
        address.setZipCode(new ZipCode(zipCode));
        address.setCity(city);
        tempAddressList.remove(zipCode);
        tempAddressList.remove(city);
        address.setAdditionalInfo(tempAddressList);
        successFoundCityAndZipCode = true;
      }
    }
    return successFoundCityAndZipCode;
  }

  /**
   * Helper-method to find which row (if any) contains the zip.
   * 
   * @param addressList The list of strings in the address.
   * @return The index of the row in the provided list of strings that contains the zip.
   */
  private static int findZipCode(List<String> addressList) {
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
      }
    }

    return foundRow;
  }

  /**
   * Helper-method to find which row (if any) contains the city.
   * 
   * @param addressList The list of strings in the address.
   * @return The index of the row in the provided list of strings that contains the city.
   */
  private static int findCity(List<String> addressList) {
    int foundRow = -1;

    String temp = "";
    for (int row = addressList.size() - 1; row >= 0; row--) {
      temp = addressList.get(row);
      temp = temp.replaceAll(" ", "");
      if (StringUtil.containsNoNumbers(temp) && !containsListWord(temp, INVALID_CITY_WORDS)) {
        foundRow = row;
        break;
      }
    }

    return foundRow;
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

      tempCity = tempCity.replaceAll("^[0-9\\s]+", "");
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
    List<Integer> foundRows = getRowsMatchingValidStreetSuffixes(addressList);

    int foundRow = -1;

    if (foundRows.size() == 1) {
      foundRow = foundRows.get(0);
    } else if (foundRows.size() > 1) {
      foundRow = getStreetRowAfterExceptionedStreetSuffixesAreFiltered(addressList, foundRows);
      // Special handling if all previously found rows have exceptioned words. Lets take the first row.
      if (foundRow == -1) {
        foundRow = foundRows.get(foundRows.size() - 1);
      }
    }

    return foundRow;
  }

  private static int getStreetRowAfterExceptionedStreetSuffixesAreFiltered(List<String> addressList, List<Integer> foundRows) {
    int foundRow = -1;
    for (Integer row : foundRows) {
      if (!containsListWord(addressList.get(row), EXCEPTIONED_STREET_SUFFIX)) {
        if (foundRow == -1) {
          foundRow = row;
        } else {
          foundRow = -1;
          break;
        }
      }
    }
    return foundRow;
  }

  private static List<Integer> getRowsMatchingValidStreetSuffixes(List<String> addressList) {
    List<Integer> foundRows = new ArrayList<Integer>();

    for (int row = addressList.size() - 1; row >= 0; row--) {
      if (AddressHelper.containsListWord(addressList.get(row), VALID_STREET_SUFFIX)) {
        foundRows.add(row);
      }
    }
    return foundRows;
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

  private static Address createNewUnparsedAddress(List<String> origAddressList, String type) {
    LOG.debug("Unable to parse " + type + "-address: " + StringUtil.concatenate(origAddressList, "$"));
    Address address = new Address();
    address.setAdditionalInfo(origAddressList);
    return address;
  }
}
