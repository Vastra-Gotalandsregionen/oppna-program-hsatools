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
package se.vgregion.kivtools.search.svc.domain.values;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.search.util.Evaluator;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Represents an adress entry
 * 
 *         example: street=Per Dubbsgatan 15 zipCode=45145 city=Uddevalla additional info=Ta vänster vid norra porten, hiss till plan 7
 */

public class Address implements Serializable, IsEmptyMarker {
  private static final long serialVersionUID = 1L;
  // to any special address info
  private static final List<String> VALID_STREET_SUFFIX = new LinkedList<String>();

  private String street = "";
  private ZipCode zipCode = new ZipCode("");
  private String city = "";
  // the rest of the address stuff that can't be converted
  private List<String> additionalInfo = new ArrayList<String>();

  // Define all valid street suffix
  static {
    VALID_STREET_SUFFIX.add("gata");
    VALID_STREET_SUFFIX.add("väg");
    VALID_STREET_SUFFIX.add("plats");
    VALID_STREET_SUFFIX.add("torg");
    VALID_STREET_SUFFIX.add("park");
    VALID_STREET_SUFFIX.add("leden");
    VALID_STREET_SUFFIX.add("stråket");
    VALID_STREET_SUFFIX.add("backe");
  }

  public Address() {
    super();
  }

  public Address(String street, ZipCode zipCode, String city, List<String> additionalInfo) {
    super();
    this.street = street;
    this.zipCode = zipCode == null ? new ZipCode("") : zipCode;
    this.city = city;
    this.additionalInfo = additionalInfo;
  }

  public boolean isEmpty() {
    if (!isEmpty(street) || !isEmpty(zipCode.getZipCode()) || !isEmpty(city)) {
      return false;
    }
    // we only get here if street, postal code, city is empty
    if (additionalInfo == null || additionalInfo.size() == 0) {
      return true;
    }
    for (int i = 0; i < additionalInfo.size(); i++) {
      if (!isEmpty(additionalInfo.get(i))) {
        return false;
      }
    }
    return true;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public ZipCode getZipCode() {
    return zipCode;
  }

  public void setZipCode(ZipCode zipCode) {
    this.zipCode = zipCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public List<String> getAdditionalInfo() {
    return additionalInfo;
  }

  public String getAdditionalInfoToString() {
    String result = null;
    if (additionalInfo != null && additionalInfo.size() > 0) {
      StringBuffer buf = new StringBuffer();
      for (String s : additionalInfo) {
        buf.append(s);
      }
      result = buf.toString();
    }
    return result;
  }

  public void setAdditionalInfo(List<String> additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public boolean isEmpty(String s) {
    if (s == null) {
      return true;
    }
    s = s.trim();
    if (s.equalsIgnoreCase("")) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Used to inform if Vcard can be used.
   * 
   * @return
   */
  public boolean getHasVcardData() {
    if (Evaluator.isEmpty(street) || Evaluator.isEmpty(zipCode.getZipCode()) || Evaluator.isEmpty(city)) {
      return false;
    }
    return true;
  }

  public String getEncodedAddress() {
    String addressStr = this.getStreet() + ", " + this.getCity();
    try {
      return URLEncoder.encode(addressStr, "iso-8859-1");
    } catch (UnsupportedEncodingException e) {
      System.err.println("Unable to encode string: " + addressStr);
      return addressStr;
    }
  }

  /**
   * Nice to be able to get the hole string at once in facelet.
   * 
   * @return
   */
  public String getConcatenatedAdditionalInfo() {
    String concatenatedAdditionalInfo = "";
    if (additionalInfo != null && additionalInfo.size() > 0) {
      for (String s : additionalInfo) {
        concatenatedAdditionalInfo += s.trim() + ", ";
      }
    }

    if (concatenatedAdditionalInfo.endsWith(", ")) {
      concatenatedAdditionalInfo = concatenatedAdditionalInfo.substring(0, concatenatedAdditionalInfo.length() - 2);
    }
    return concatenatedAdditionalInfo;
  }
}
