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
package se.vgregion.kivtools.search.domain.values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.InvalidFormatException;

/**
 * Represents a time intervall. Used for Telephone times and hours.
 * 
 * <h4>Katalogformat</h4> <li><code>1-5#08:30#10:00</code></li> <li><code>2-2#07:15#16:45</code></li>
 * 
 * <h4>Presented format</h4> <br>
 * <li><code>Mondag-Fredag 08:30-10:00</code></li> <li><code>Tisdag 07:15-16:45</code></li>
 * 
 * @author JENJA13
 * @author ULFSA3
 * 
 */
public class WeekdayTime implements Comparable<WeekdayTime>, Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Minvärde för veckodagskod, f.n. 0.
   */
  private static final int MIN_DAY_CODE = 0;
  /**
   * Maxvärde för veckodagskod, f.n. 6.
   */
  private static final int MAX_DAY_CODE = 7;
  /**
   * Minvärde för timmar, f.n. 0.
   */
  private static final int MIN_HOUR = 0;
  /**
   * Maxvärde för timmar, f.n. 23.
   */
  private static final int MAX_HOUR = 24;
  /**
   * Minvärde för timmar, f.n. 0.
   */
  private static final int MIN_MINUTE = 0;
  /**
   * Minvärde för minuter, f.n. 59.
   */
  private static final int MAX_MINUTE = 59;

  private static final Log LOG = LogFactory.getLog(WeekdayTime.class);

  private int endDay;
  private int endHour;
  private int endMin;
  private int startDay;
  private int startHour;
  private int startMin;

  /**
   * Skapar en nytt tidsintervall.
   * 
   * @param startDay kod för startveckodag.
   * @param endDay kod för slutveckodag
   * @param startHour starttimme
   * @param startMin startminut
   * @param endHour sluttimme
   * @param endMin slutminut
   * @throws InvalidFormatException om någon av inparametrarna ligger utanför tillåtna intervall.
   */
  public WeekdayTime(int startDay, int endDay, int startHour, int startMin, int endHour, int endMin) throws InvalidFormatException {
    this.setStartDay(startDay);
    this.setEndDay(endDay);
    this.setStartHour(startHour);
    this.setStartMin(startMin);
    this.setEndHour(endHour);
    this.setEndMin(endMin);
  }

  /**
   * Skapar en nytt tidsintervall utifrån en sträng på katalogformat.
   * 
   * @param saveValue -
   * 
   * @throws InvalidFormatException om någon av inparametrarna ligger utanför tillåtna intervall.
   */
  public WeekdayTime(String saveValue) throws InvalidFormatException {
    this(0, 0, 0, 0, 0, 0);

    String[] splits = saveValue.split("#");
    if (3 == splits.length) {
      String[] daySplits = splits[0].split("-");

      if (2 == daySplits.length) {
        this.setStartDay(daySplits[0]);
        this.setEndDay(daySplits[1]);
      } else {
        throw new InvalidFormatException("Felaktigt antal -");
      }

      String[] startTimeSplits = splits[1].split(":");
      if (2 == startTimeSplits.length) {
        this.setStartHour(startTimeSplits[0]);
        this.setStartMin(startTimeSplits[1]);
      } else {
        throw new InvalidFormatException("Felaktigt antal :");
      }

      String[] endTimeSplits = splits[2].split(":");
      if (2 == endTimeSplits.length) {
        this.setEndHour(endTimeSplits[0]);
        this.setEndMin(endTimeSplits[1]);
      } else {
        throw new InvalidFormatException("Felaktigt antal :");
      }

    } else {
      throw new InvalidFormatException("Felaktigt antal #");
    }

  }

  /**
   * Skapar en nytt tidsintervall.
   * 
   * @param startDay kod för startveckodag.
   * @param endDay kod för slutveckodag
   * @param startHour starttimme
   * @param startMin startminut
   * @param endHour sluttimme
   * @param endMin slutminut
   * 
   * @throws InvalidFormatException om någon av inparametrarna ligger utanför tillåtna intervall.
   */
  public WeekdayTime(String startDay, String endDay, String startHour, String startMin, String endHour, String endMin) throws InvalidFormatException {
    this.setStartDay(startDay);
    this.setEndDay(endDay);
    this.setStartHour(startHour);
    this.setStartMin(startMin);
    this.setEndHour(endHour);
    this.setEndMin(endMin);
  }

  /**
   * Creates a list of WeekdayTime objects based on a list of save values (representation from an LDAP entry).
   * 
   * @param saveValues The save values to create WeekdayTime objects from.
   * @return A list of WeekdayTime objects.
   */
  public static List<WeekdayTime> createWeekdayTimeList(List<String> saveValues) {
    List<WeekdayTime> timeList = new ArrayList<WeekdayTime>();
    if (saveValues != null) {
      for (String telephoneTime : saveValues) {
        try {
          timeList.add(new WeekdayTime(telephoneTime));
        } catch (InvalidFormatException e) {
          LOG.warn("Unable to parse provided telephoneTime to a valid WeekdayTime", e);
        }
      }
    }
    Collections.sort(timeList);
    return timeList;
  }

  /**
   * Jämför detta tidsintervall med det specificerade tidsintervallet för ordning. Returnerar ett negativt heltal, noll, eller ett positivt heltal om detta tidsintervall ligger före, är lika, eller
   * ligger efter det specificerade tidsintervallet.
   * 
   * <p>
   * 
   * 
   * </p>
   * 
   * @param other tidsintervallet som skall jämföras
   * @return ett negativt heltal, noll, eller ett positivt heltal om detta tidsintervall är ligger före, är lika med eller ligger efter det specificerade tidsintervallet.
   */
  public int compareTo(WeekdayTime other) {
    return this.getSaveValue().compareTo(other.getSaveValue());

  }

  /**
   * Kontrollerar om två tidsintervall är lika. Två tidsintervall är lika om deras dagar och tider är samma.
   * 
   * @param obj The object to compare.
   * @return True if the objects are equal, otherwise false.
   */
  @Override
  public boolean equals(Object obj) {
    boolean equal = true;

    if (this != obj) {
      if (obj == null) {
        equal = false;
      } else {
        if (getClass() != obj.getClass()) {
          equal = false;
        } else {
          WeekdayTime other = (WeekdayTime) obj;
          equal = this.getSaveValue().equals(other.getSaveValue());
        }
      }
    }

    return equal;
  }

  /**
   * Returnerar en hashkod för tidsintervallet. Överskuggingen beror på att {@link #equals(WeekdayTime)} överskuggas och att två objekt som är lika enligt equals skall generera samma hashkod.
   * 
   * @return ett hashkodsvärde för detta objekt.
   */
  @Override
  public int hashCode() {
    return this.getSaveValue().hashCode();
  }

  /**
   * Hämtar representation av tidsintervall till format som presenteras för användaren.
   * 
   * @return A representation of a time interval that is presentable to a user.
   */
  public String getDisplayValue() {

    // If open all the time, return "Dygnet runt"
    if (endDay == 7 && (endHour == 24 || endHour == 00) && endMin == 0 && endMin == 0 && startDay == 1 && startHour == 0 && startMin == 0) {
      return "Dygnet runt";
    }

    String returnString = "";
    returnString += WeekdayTime.getDayName(this.getStartDay());

    if (this.getStartDay() != this.getEndDay()) {
      // om över flera dagar...
      returnString += "-" + WeekdayTime.getDayName(this.getEndDay());
    }

    returnString += " ";
    returnString += this.getTwoDigitNumber(this.getStartHour());
    returnString += ":";
    returnString += this.getTwoDigitNumber(this.getStartMin());
    returnString += "-";
    returnString += this.getTwoDigitNumber(this.getEndHour());
    returnString += ":";
    returnString += this.getTwoDigitNumber(this.getEndMin());

    return returnString;

  }

  /**
   * Hämtar kod för slut-veckodag. {@link Parse#getDayName(int)}.
   * 
   * @return kod för slut-veckodag
   */
  public int getEndDay() {
    return endDay;
  }

  /**
   * Hämtar sluttidens timmesdel.
   * 
   * @return sluttidens timmesdel
   */
  public int getEndHour() {
    return endHour;
  }

  /**
   * Hämtar sluttidens minutdel.
   * 
   * @return sluttidens minutdel
   */
  public int getEndMin() {
    return endMin;
  }

  /**
   * Hämtar sträng med hur tidsintervallet skall lagras i katalogen.
   * 
   * @return sträng med tidintervallet så som det sparas i katalogen. Exempel: <code>1-5#08:15#16:30</code>.
   */
  public String getSaveValue() {
    return this.getStartDay() + "-" + this.getEndDay() + "#" + this.getTwoDigitNumber(this.getStartHour()) + ":" + this.getTwoDigitNumber(this.getStartMin()) + "#"
        + this.getTwoDigitNumber(this.getEndHour()) + ":" + this.getTwoDigitNumber(this.getEndMin());
  }

  /**
   * Hämtar kod för start-veckodag. {@link Parse#getDayName(int)}.
   * 
   * @return kod för start-veckodag
   */
  public int getStartDay() {
    return startDay;
  }

  /**
   * Hämtar starttidens timmesdel.
   * 
   * @return starttidens timmesdel
   */
  public int getStartHour() {
    return startHour;
  }

  /**
   * Hämtar starttidens minutdel.
   * 
   * @return starttidens minutdel
   */
  public int getStartMin() {
    return startMin;
  }

  /**
   * Sätter kod för slut-veckodagen. Kontrollerar så den är i intervallet {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
   * 
   * @param endDay The new value for the endDay property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setEndDay(int endDay) throws InvalidFormatException {
    if (MIN_DAY_CODE <= endDay && endDay <= MAX_DAY_CODE) {
      this.endDay = mapSunday(endDay);
    } else {
      throw new InvalidFormatException("");
    }

  }

  /**
   * Sätter kod för slut-veckodagen. Kontrollerar så den är i intervallet {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
   * 
   * @param endDay The new value for the endDay property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setEndDay(String endDay) throws InvalidFormatException {
    int parseDay = Integer.parseInt(endDay);
    this.setEndDay(parseDay);
  }

  /**
   * Sätter sluttidens timmesdel. Kontrollerar så den är i intervallet {@link #MIN_HOUR}-{@link #MAX_HOUR}.
   * 
   * @param endHour The new value for the endHour property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setEndHour(int endHour) throws InvalidFormatException {
    if (MIN_HOUR <= endHour && endHour <= MAX_HOUR) {
      this.endHour = endHour;
    } else {
      throw new InvalidFormatException("");
    }
  }

  /**
   * Sätter sluttidens timmesdel. Kontrollerar så den är i intervallet {@link #MIN_HOUR}-{@link #MAX_HOUR}.
   * 
   * @param endHour The new value for the endHour property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setEndHour(String endHour) throws InvalidFormatException {
    int parse = Integer.parseInt(endHour);
    this.setEndHour(parse);
  }

  /**
   * Sätter sluttidens minutdel. Kontrollerar så den är i intervallet {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
   * 
   * @param endMin The new value for the endMin property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setEndMin(int endMin) throws InvalidFormatException {
    if (MIN_MINUTE <= endMin && endMin <= MAX_MINUTE) {
      this.endMin = endMin;
    } else {
      throw new InvalidFormatException("");
    }

  }

  /**
   * Sätter sluttidens minutdel. Kontrollerar så den är i intervallet {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
   * 
   * @param endMin The new value for the endMin property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setEndMin(String endMin) throws InvalidFormatException {
    int parse = Integer.parseInt(endMin);
    this.setEndMin(parse);
  }

  /**
   * Sätter kod för start-veckodagen. Kontrollerar så den är i intervallet {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
   * 
   * @param startDay The new value for the startDay property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setStartDay(int startDay) throws InvalidFormatException {
    if (MIN_DAY_CODE <= startDay && startDay <= MAX_DAY_CODE) {
      this.startDay = mapSunday(startDay);
    } else {
      throw new InvalidFormatException("");
    }
  }

  /**
   * Sätter kod för start-veckodagen. Kontrollerar så den är i intervallet {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
   * 
   * @param startDay The new value for the startDay property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setStartDay(String startDay) throws InvalidFormatException {

    int parseDay = Integer.parseInt(startDay);
    this.setStartDay(parseDay);

  }

  /**
   * Sätter starttidens timmesdel. Kontrollerar så den är i intervallet {@link #MIN_HOUR}-{@link #MAX_HOUR}.
   * 
   * @param startHour The new value for the startHour property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setStartHour(int startHour) throws InvalidFormatException {
    if (MIN_HOUR <= startHour && startHour <= MAX_HOUR) {
      this.startHour = startHour;
    } else {
      throw new InvalidFormatException("");
    }
  }

  /**
   * Sätter starttidens timmesdel. Kontrollerar så den är i intervallet {@link #MIN_HOUR}-{@link #MAX_HOUR}.
   * 
   * @param startHour The new value for the startHour property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setStartHour(String startHour) throws InvalidFormatException {
    int parse = Integer.parseInt(startHour);
    this.setStartHour(parse);
  }

  /**
   * Sätter starttidens minutdel. Kontrollerar så den är i intervallet {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
   * 
   * @param startMin The new value for the startMin property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setStartMin(int startMin) throws InvalidFormatException {
    if (MIN_MINUTE <= startMin && startMin <= MAX_MINUTE) {
      this.startMin = startMin;
    } else {
      throw new InvalidFormatException("");
    }

    this.startMin = startMin;
  }

  /**
   * Sätter starttidens minutdel. Kontrollerar så den är i intervallet {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
   * 
   * @param startMin The new value for the startMin property.
   * @throws InvalidFormatException if the provided value is outside the possible range of values.
   */
  public void setStartMin(String startMin) throws InvalidFormatException {
    int parse = Integer.parseInt(startMin);
    this.setStartMin(parse);
  }

  /**
   * Mappar om det gamla värdet för söndagar - 0 till det nya - 7. Denna funktion kan tas bort när katalogen blivit städad.
   * 
   * @param day nummer som representerar dagen
   * @return om day är 0 returneras 7, annars returneras day
   */
  private int mapSunday(int day) {
    if (0 == day) {
      return 7;
    }
    return day;
  }

  /**
   * Översätter ett nummer till dag i klartext. 0 och 7 översätts båda till Söndag.
   * 
   * @param day - int day mellan 0-7 representerar varsin dag.
   * @return - String returnerar dag ex. "6" blir "Lördag".
   */
  public static String getDayName(int day) {
    String dayString;
    switch (day) {
      case 1:
        dayString = "M\u00E5ndag";
        break;
      case 2:
        dayString = "Tisdag";
        break;
      case 3:
        dayString = "Onsdag";
        break;
      case 4:
        dayString = "Torsdag";
        break;
      case 5:
        dayString = "Fredag";
        break;
      case 6:
        dayString = "L\u00F6rdag";
        break;
      case 7:
        dayString = "S\u00F6ndag";
        break;
      case 0:
        dayString = "S\u00F6ndag";
        break;
      default:
        dayString = "";
    }
    return dayString;
  }

  /**
   * Konverterar siffror, t ex timmar, så att de visas med två siffror. T ex blir 9 "09".
   * 
   * @param number
   * @return en sträng med två siffor.
   */
  private String getTwoDigitNumber(int number) {
    if (number > 9) {
      return Integer.toString(number);
    }
    return "0" + Integer.toString(number);

  }
}
