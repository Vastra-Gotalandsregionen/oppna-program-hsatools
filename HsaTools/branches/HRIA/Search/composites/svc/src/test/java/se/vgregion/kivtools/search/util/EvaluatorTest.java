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
  public void testIsInteger() {
    assertFalse(Evaluator.isInteger(null));
    assertFalse(Evaluator.isInteger(""));
    assertTrue(Evaluator.isInteger("1"));
    assertFalse(Evaluator.isInteger("a"));
    assertFalse(Evaluator.isInteger("1.0"));
    assertFalse(Evaluator.isInteger(String.valueOf(Long.valueOf(Integer.MAX_VALUE) + 1)));
  }

  @Test
  public void testIsBoolean() {
    assertFalse(Evaluator.isBoolean(null));
    assertFalse(Evaluator.isBoolean(""));
    assertTrue(Evaluator.isBoolean("true"));
    assertFalse(Evaluator.isBoolean("TRUE"));
    assertTrue(Evaluator.isBoolean("false"));
    assertFalse(Evaluator.isBoolean("FALSE"));
    assertFalse(Evaluator.isBoolean("asas"));
  }

  @Test
  public void testIsDouble() {
    assertFalse(Evaluator.isDouble(null));
    assertFalse(Evaluator.isDouble(""));
    assertTrue(Evaluator.isDouble("0"));
    assertTrue(Evaluator.isDouble("0.0"));
    assertTrue(Evaluator.isDouble("1.0"));
    assertFalse(Evaluator.isDouble("a"));
  }

  @Test
  public void testIsEmptyString() {
    assertTrue(Evaluator.isEmpty((String) null));
    assertTrue(Evaluator.isEmpty(""));
    assertTrue(Evaluator.isEmpty(" "));
    assertFalse(Evaluator.isEmpty("aaa"));
  }

  @Test
  public void testIsEmptyIsEmptyMarker() {
    IsEmptyMarker marker = new IsEmptyMarkerMock(false);
    assertFalse(Evaluator.isEmpty(marker));
    marker = new IsEmptyMarkerMock(true);
    assertTrue(Evaluator.isEmpty(marker));
  }

  @Test
  public void testIsNegative() {
    assertFalse(Evaluator.isNegative(null));
    assertFalse(Evaluator.isNegative(""));
    assertFalse(Evaluator.isNegative("0"));
    assertTrue(Evaluator.isNegative("-0.1"));
    assertTrue(Evaluator.isNegative("-1.0"));
    assertFalse(Evaluator.isNegative("a"));
  }

  @Test
  public void testCheckDouble() {
    assertTrue(Evaluator.checkDouble(null, true));
    assertTrue(Evaluator.checkDouble("", true));
    assertTrue(Evaluator.checkDouble("0", true));
    assertTrue(Evaluator.checkDouble("0.0", true));
    assertTrue(Evaluator.checkDouble("-1.0", true));
    assertTrue(Evaluator.checkDouble("21.0", true));
    assertFalse(Evaluator.checkDouble("a", true));
    assertFalse(Evaluator.checkDouble(null, false));
    assertFalse(Evaluator.checkDouble("", false));
  }

  @Test
  public void testStringToBoolean() {
    assertTrue(Evaluator.stringToBoolean("true"));
    assertFalse(Evaluator.stringToBoolean("false"));
    assertFalse(Evaluator.stringToBoolean(null));
    assertFalse(Evaluator.stringToBoolean(""));
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

  @Test
  public void testContainsNoNumbers() {
    assertFalse(Evaluator.containsNoNumbers(null));
    assertFalse(Evaluator.containsNoNumbers(""));
    assertTrue(Evaluator.containsNoNumbers("a"));
    assertFalse(Evaluator.containsNoNumbers("a1"));
    assertFalse(Evaluator.containsNoNumbers("1"));
    assertFalse(Evaluator.containsNoNumbers("a 1"));
    assertFalse(Evaluator.containsNoNumbers("a 1"));
    assertTrue(Evaluator.containsNoNumbers("a  b"));
  }

  @Test
  public void testContainsOnlyNumbers() {
    assertFalse(Evaluator.containsOnlyNumbers(null, false));
    assertFalse(Evaluator.containsOnlyNumbers("", false));
    assertFalse(Evaluator.containsOnlyNumbers("a", false));
    assertFalse(Evaluator.containsOnlyNumbers("a1", false));
    assertTrue(Evaluator.containsOnlyNumbers("1", false));
    assertFalse(Evaluator.containsOnlyNumbers("a 1", false));
    assertFalse(Evaluator.containsOnlyNumbers("a 1", true));
    assertFalse(Evaluator.containsOnlyNumbers("a  b", true));
    assertFalse(Evaluator.containsOnlyNumbers("1 2 3    4", false));
    assertTrue(Evaluator.containsOnlyNumbers("1 2 3    4", true));
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
