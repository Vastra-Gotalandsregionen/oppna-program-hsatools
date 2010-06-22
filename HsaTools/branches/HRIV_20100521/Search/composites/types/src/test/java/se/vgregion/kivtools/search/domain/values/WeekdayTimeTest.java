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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.InvalidFormatException;

public class WeekdayTimeTest {

  @Test
  public void testEqualsWeekdayTime() throws InvalidFormatException {
    WeekdayTime weekdayTime = new WeekdayTime(1, 1, 1, 1, 1, 1);

    assertFalse(weekdayTime.equals(null));

    assertFalse(weekdayTime.equals(""));

    WeekdayTime other = new WeekdayTime(2, 2, 2, 2, 2, 2);
    assertFalse(weekdayTime.equals(other));

    other = new WeekdayTime(1, 1, 1, 1, 1, 1);
    assertTrue(weekdayTime.equals(other));
  }

  /**
   * Test of createWeekdayTimeList method, of class WeekdayTime.
   */
  @Test
  public void testCreateWeekdayTimeList() throws InvalidFormatException {

    List<String> saveValues = new ArrayList();
    saveValues.add("1-5#13:00#16:30");
    saveValues.add("1-5#08:00#12:00");
    List<WeekdayTime> expResult = new ArrayList<WeekdayTime>();
    expResult.add(new WeekdayTime("1-5#08:00#12:00"));
    expResult.add(new WeekdayTime("1-5#13:00#16:30"));

    // Build a String for expected result
    StringBuffer exp = new StringBuffer();
    for (WeekdayTime wdt : expResult) {
      exp.append(wdt.getDisplayValue());
    }

    // Build a String for actual result
    List<WeekdayTime> result = WeekdayTime.createWeekdayTimeList(saveValues);
    // Build a String for expected result
    StringBuffer res = new StringBuffer();
    for (WeekdayTime wdt : result) {
      res.append(wdt.getDisplayValue());
    }
    assertEquals("WeekdayTimes should be sorted on day/time", exp.toString(), res.toString());
  }

  @Test
  public void testOpenAllTheTimeDisplay() {
    List<String> saveValues = new ArrayList<String>();
    saveValues.add("1-7#00:00#24:00");
    saveValues.add("1-7#00:00#00:00");
    List<WeekdayTime> weekdayTimeList = WeekdayTime.createWeekdayTimeList(saveValues);
    for (WeekdayTime weekdayTime : weekdayTimeList) {
      assertEquals("Dygnet runt", weekdayTime.getDisplayValue());
    }
  }

  @Test
  @Ignore("Ignored until user story for handling additional info is created and approved")
  public void additionalInfoDoesNotBreakProcessing() throws InvalidFormatException {
    WeekdayTime weekdayTime = new WeekdayTime("1-5#08:00#12:00#Udda veckor");
    assertNotNull("weekday time", weekdayTime);
  }
}
