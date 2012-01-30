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

package se.vgregion.kivtools.search.presentation.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UnitSearchSimpleFormTest {
  private static final String SPACE = " ";
  private static final String TEST = "Test";
  private UnitSearchSimpleForm form;

  @Before
  public void setUp() {
    form = new UnitSearchSimpleForm();
  }

  @Test
  public void testIsEmpty() {
    form.setUnitName(null);
    form.setLocation(null);

    assertTrue(form.isEmpty());
    form.setUnitName(TEST);
    assertFalse(form.isEmpty());
    form.setLocation(TEST);
    assertFalse(form.isEmpty());

    form.setUnitName(SPACE);
    form.setLocation(SPACE);
    assertTrue(form.isEmpty());
    form.setUnitName(TEST);
    assertFalse(form.isEmpty());
    form.setLocation(TEST);
    assertFalse(form.isEmpty());
    form.setUnitName(SPACE);
    assertFalse(form.isEmpty());
  }
}
