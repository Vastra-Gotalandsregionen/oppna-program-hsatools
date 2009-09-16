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
package se.vgregion.kivtools.search.svc.domain.values;

import static org.junit.Assert.*;

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
}
