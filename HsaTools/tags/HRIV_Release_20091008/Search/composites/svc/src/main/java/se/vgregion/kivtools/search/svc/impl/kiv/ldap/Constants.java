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
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Constants specific to KIV.
 */
public class Constants {
  public static final String OBJECT_CLASS_UNIT_STANDARD = "organizationalUnit";
  public static final String OBJECT_CLASS_FUNCTION_STANDARD = "organizationalRole";
  public static final String OBJECT_CLASS_UNIT_SPECIFIC = "vgrOrganizationalUnit";
  public static final String OBJECT_CLASS_FUNCTION_SPECIFIC = "vgrOrganizationalRole";
  public static final String LDAP_PROPERTY_UNIT_NAME = "ou";
  public static final String LDAP_PROPERTY_FUNCTION_NAME = "cn";
  private static final String ZULUTIMEFORMATSTRING = "yyyyMMddHHmmss'Z'";
  private static final String SCIENTIFICTIMEFORMATSTRING = "yyyyMMddHHmmss";
  private static final String NORMALTIMEFORMATSTRING = "dd MMMM, yyyy";

  /**
   * Formats dates in this format: yyyyMMddHHmmss.
   * 
   * @param date to format.
   * @return String value of formatted date.
   */
  public static final String formatDateToScientificTime(Date date) {
    return new SimpleDateFormat(SCIENTIFICTIMEFORMATSTRING).format(date);
  }

  /**
   * Formats dates in this format: dd MMMM, yyyy.
   * 
   * @param date to format.
   * @return String value of formatted date.
   */
  public static final String formatDateToNormalTime(Date date) {
    return new SimpleDateFormat(NORMALTIMEFORMATSTRING).format(date);
  }

  /**
   * Formats dates in this format: yyyyMMddHHmmss'Z'.
   * 
   * @param date to format.
   * @return String value of formatted date.
   */
  public static final String formatDateToZuluTime(Date date) {
    return new SimpleDateFormat(ZULUTIMEFORMATSTRING).format(date);
  }

  /**
   * Parses a string formatted yyyyMMddHHmmss'Z' into a date object.
   * 
   * @param dateStr to parse
   * @return Date parsed object
   */
  public static final Date parseStringToZuluTime(String dateStr) {
    if (dateStr != null && dateStr.length() > 0) {
      try {
        return new SimpleDateFormat(ZULUTIMEFORMATSTRING).parse(dateStr);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
