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
import java.util.List;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Represents an adress entry.
 * 
 * example: street=Per Dubbsgatan 15 zipCode=45145 city=Uddevalla additional info=Ta vänster vid norra porten, hiss till plan 7
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class Address implements Serializable, IsEmptyMarker {
  private static final long serialVersionUID = 1L;

  private String street = "";
  private ZipCode zipCode = new ZipCode("");
  private String city = "";
  // the rest of the address stuff that can't be converted
  private List<String> additionalInfo = new ArrayList<String>();

  /**
   * Empty constructor.
   */
  public Address() {
    super();
  }

  /**
   * Constructor with possibility to populate the address.
   * 
   * @param street The street information.
   * @param zipCode The zipcode.
   * @param city The city.
   * @param additionalInfo Any additional information.
   */
  public Address(String street, ZipCode zipCode, String city, List<String> additionalInfo) {
    super();
    setStreet(street);
    setZipCode(zipCode);
    this.city = city;
    this.additionalInfo = additionalInfo;
  }

  /**
   * @inheritDoc
   * @return True if the address is empty.
   */
  @Override
  public boolean isEmpty() {
    boolean empty = true;

    empty &= StringUtil.isEmpty(street);
    empty &= StringUtil.isEmpty(zipCode.getZipCode());
    empty &= StringUtil.isEmpty(city);

    if (additionalInfo != null && additionalInfo.size() > 0) {
      for (int i = 0; i < additionalInfo.size(); i++) {
        empty &= StringUtil.isEmpty(additionalInfo.get(i));
      }
    }
    return empty;
  }

  /**
   * Getter for the street property.
   * 
   * @return The value of the street property.
   */
  public String getStreet() {
    return street;
  }

  /**
   * Setter for the street property.
   * 
   * @param street The new value of the street property.
   */
  public void setStreet(String street) {
    this.street = street;
  }

  /**
   * Getter for the zipCode property.
   * 
   * @return The value of the zipCode property.
   */
  public ZipCode getZipCode() {
    return zipCode;
  }

  /**
   * Setter for the zipCode property.
   * 
   * @param zipCode The new value of the zipCode property.
   */
  public void setZipCode(ZipCode zipCode) {
    if (zipCode == null) {
      this.zipCode = new ZipCode("");
    } else {
      this.zipCode = zipCode;
    }
  }

  /**
   * Getter for the city property.
   * 
   * @return The value of the city property.
   */
  public String getCity() {
    return city;
  }

  /**
   * Setter for the city property.
   * 
   * @param city The new value of the city property.
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * Getter for the additionalInfo property.
   * 
   * @return The value of the additional info property.
   */
  public List<String> getAdditionalInfo() {
    return additionalInfo;
  }

  /**
   * Gets the additional info as a string.
   * 
   * @return All the lines from the additional info concatenated to a string.
   */
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

  /**
   * Setter for the additionalInfo property.
   * 
   * @param additionalInfo The new value of the additionalInfo property.
   */
  public void setAdditionalInfo(List<String> additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  /**
   * Used to inform if Vcard can be used.
   * 
   * @return True if the address has data in street, zipcode and city, otherwise false.
   */
  public boolean getHasVcardData() {
    if (StringUtil.isEmpty(street) || StringUtil.isEmpty(zipCode.getZipCode()) || StringUtil.isEmpty(city)) {
      return false;
    }
    return true;
  }

  /**
   * Gets an URLEncoded version of the street and city.
   * 
   * @return An URLEncoded string which consists of street and city.
   */
  public String getEncodedAddress() {
    String addressStr = this.getStreet() + ", " + this.getCity();
    return StringUtil.urlEncode(addressStr, "iso-8859-1");
  }

  /**
   * Gets a comma-separated string of all additional info strings. Nice to be able to get the hole string at once in a facelet.
   * 
   * @return A comma-separated string of all additional info strings.
   */
  public String getConcatenatedAdditionalInfo() {
    return StringUtil.concatenate(additionalInfo);
  }

  @Override
  public String toString() {
	return "Address [street=" + street + ", zipCode=" + zipCode.getZipCode() + ", city="
				+ city + ", additionalInfo=" + getConcatenatedAdditionalInfo() + "]";
  }
  
}
