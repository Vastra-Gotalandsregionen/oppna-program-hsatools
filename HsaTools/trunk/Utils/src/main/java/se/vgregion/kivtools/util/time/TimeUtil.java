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

package se.vgregion.kivtools.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class for all date/time related functions.
 * 
 * @author Joakim Olsson & David Bennehult
 */
public class TimeUtil {
  private static final TimeSource DEFAULTSRC = new TimeSource() {
    public long millis() {
      return System.currentTimeMillis();
    }
  };

  private static AtomicReference<TimeSource> source = new AtomicReference<TimeSource>();

  /**
   * Enumeration of handled date/time formats.
   * 
   * @author Joakim Olsson & David Bennehult
   */
  public enum DateTimeFormat {
    ZULU_TIME("yyyyMMddHHmmss'Z'"), SCIENTIFIC_TIME("yyyyMMddHHmmss"), NORMAL_TIME("dd MMMM, yyyy"), W3C_TIME("yyyy-MM-dd'T'HH:mm:ssZ");

    private final String format;

    private DateTimeFormat(String format) {
      this.format = format;
    }

    public String getFormat() {
      return format;
    }

    /**
     * Representation of toString for the date/time format.
     * 
     * @return The format-string for this date/time format.
     */
    @Override
    public String toString() {
      return getFormat();
    }
  }

  private static TimeSource getTimeSource() {
    TimeSource answer;
    if (source.get() == null) {
      answer = DEFAULTSRC;
    } else {
      answer = source.get();
    }
    return answer;
  }

  /**
   * Sets the TimeSource to use.
   * 
   * @param timeSource The TimeSource to use.
   */
  public static void setTimeSource(final TimeSource timeSource) {
    TimeUtil.source.set(timeSource);
  }

  /**
   * Resets the used TimeSource.
   */
  public static void reset() {
    setTimeSource(null);
  }

  /**
   * Retrieves current date/time in milliseconds.
   * 
   * @return Current date/time in milliseconds.
   */
  public static long asMillis() {
    return getTimeSource().millis();
  }

  /**
   * Retrieves current date/time as a {@link Date} object.
   * 
   * @return Current date/time as a {@link Date} object.
   */
  public static Date asDate() {
    return new Date(asMillis());
  }

  /**
   * Formats the provided date using the provided DateTimeFormat.
   * 
   * @param date The date to format.
   * @param format The DateTimeFormat to use.
   * @return A string-representation of the provided Date using the provided DateTimeFormat.
   */
  public static final String formatDate(Date date, DateTimeFormat format) {
    return new SimpleDateFormat(format.getFormat()).format(date);
  }

  /**
   * Formats the provided date using the W3C format.
   * 
   * @param date The date to format.
   * @return A string-representation of the provided Date using the W3C format.
   */
  public static final String formatDateW3C(Date date) {
    String timeStamp = formatDate(date, DateTimeFormat.W3C_TIME);
    return timeStamp.substring(0, 22) + ":" + timeStamp.substring(22);
  }

  /**
   * Formats the current date/time using the provided DateTimeFormat.
   * 
   * @param format The DateTimeFormat to use.
   * @return A string-representation of the current date/time using the provided DateTimeFormat.
   */
  public static final String getCurrentTimeFormatted(DateTimeFormat format) {
    return formatDate(asDate(), format);
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
        return new SimpleDateFormat(DateTimeFormat.ZULU_TIME.getFormat()).parse(dateStr);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
