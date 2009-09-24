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
package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;

public class EvaluatorTest {

  @Test
  public void testIsEmptyIsEmptyMarker() {
    IsEmptyMarker marker = new IsEmptyMarkerMock(false);
    assertFalse(Evaluator.isEmpty(marker));
    marker = new IsEmptyMarkerMock(true);
    assertTrue(Evaluator.isEmpty(marker));
  }

  @Test
  public void testIsEmptyAdress() {
    assertTrue(Evaluator.isEmptyAdress(null));
    List<Address> list = new ArrayList<Address>();
    assertTrue(Evaluator.isEmptyAdress(list));
    list.add(new Address());
    assertTrue(Evaluator.isEmptyAdress(list));
    Address address = new Address("street", null, null, null);
    list.add(address);
    assertFalse(Evaluator.isEmptyAdress(list));
  }

  @Test
  public void testIsEmptyPhoneNumber() {
    assertTrue(Evaluator.isEmptyPhoneNumber(null));
    List<PhoneNumber> list = new ArrayList<PhoneNumber>();
    assertTrue(Evaluator.isEmptyPhoneNumber(list));
    list.add(new PhoneNumber(""));
    assertTrue(Evaluator.isEmptyPhoneNumber(list));
    PhoneNumber phoneNumber = new PhoneNumber("031-123456");
    list.add(phoneNumber);
    assertFalse(Evaluator.isEmptyPhoneNumber(list));
  }

  @Test
  public void testIsEmptyWeekDayTime() throws InvalidFormatException {
    assertTrue(Evaluator.isEmptyWeekDayTime(null));
    List<WeekdayTime> list = new ArrayList<WeekdayTime>();
    assertTrue(Evaluator.isEmptyWeekDayTime(list));
    list.add(new WeekdayTime(1, 1, 1, 1, 1, 1));
    assertFalse(Evaluator.isEmptyWeekDayTime(list));
  }

  @Test
  public void testIsEmptyBusinessClassification() {
    assertTrue(Evaluator.isEmptyBusinessClassification(null));
    List<HealthcareType> list = new ArrayList<HealthcareType>();
    assertTrue(Evaluator.isEmptyBusinessClassification(list));
    list.add(new HealthcareType());
    assertTrue(Evaluator.isEmptyBusinessClassification(list));
    HealthcareType healthcareType = new HealthcareType(null, "type", false, 2);
    list.add(healthcareType);
    assertFalse(Evaluator.isEmptyBusinessClassification(list));
  }

  @Test
  public void testIsEmptyListOfString() {
    assertTrue(Evaluator.isEmpty((List<String>) null));
    List<String> list = new ArrayList<String>();
    assertTrue(Evaluator.isEmpty(list));
    list.add("string");
    assertFalse(Evaluator.isEmpty(list));
  }

  class IsEmptyMarkerMock implements IsEmptyMarker {
    private final boolean empty;

    public IsEmptyMarkerMock(boolean empty) {
      this.empty = empty;
    }

    @Override
    public boolean isEmpty() {
      return empty;
    }
  }
}
