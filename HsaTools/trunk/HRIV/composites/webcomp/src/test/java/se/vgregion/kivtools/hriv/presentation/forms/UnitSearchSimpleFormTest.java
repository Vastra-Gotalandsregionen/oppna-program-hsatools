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

package se.vgregion.kivtools.hriv.presentation.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.MunicipalityHelper;

public class UnitSearchSimpleFormTest {

  private UnitSearchSimpleForm form;

  @Before
  public void setUp() throws Exception {
    MunicipalityHelper municipalityHelper = new MunicipalityHelper();
    municipalityHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-municipalities");
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    form = new UnitSearchSimpleForm();
  }

  @Test
  public void testIsEmpty() {
    assertTrue(form.isEmpty());

    form.setHealthcareType(null);
    assertTrue(form.isEmpty());

    form.setMunicipality(null);
    assertTrue(form.isEmpty());

    form.setUnitName(null);
    assertTrue(form.isEmpty());

    form.setUnitName("angered");
    assertFalse(form.isEmpty());
  }

  @Test
  public void testGetMunicipalityItems() {
    assertNotNull(form.getMunicipalityItems());
    assertTrue(form.getMunicipalityItems().length > 0);
  }

  @Test
  public void testSetUnitName() {
    assertNotNull(form.getUnitName());
    assertEquals("", form.getUnitName());

    form.setUnitName("angered");
    assertNotNull(form.getUnitName());
    assertEquals("angered", form.getUnitName());

    form.setUnitName(null);
    assertNotNull(form.getUnitName());
    assertEquals("angered", form.getUnitName());
  }

  @Test
  public void testSetMunicipality() {
    assertNotNull(form.getMunicipality());
    assertEquals("", form.getMunicipality());

    form.setMunicipality("kungälv");
    assertNotNull(form.getMunicipality());
    assertEquals("kungälv", form.getMunicipality());

    form.setMunicipality(null);
    assertNotNull(form.getMunicipality());
    assertEquals("kungälv", form.getMunicipality());
  }

  @Test
  public void testSetHealthcareType() {
    assertNotNull(form.getHealthcareType());
    assertEquals("", form.getHealthcareType());

    form.setHealthcareType("vårdcentral");
    assertNotNull(form.getHealthcareType());
    assertEquals("vårdcentral", form.getHealthcareType());

    form.setHealthcareType(null);
    assertNotNull(form.getHealthcareType());
    assertEquals("vårdcentral", form.getHealthcareType());
  }

  @Test
  public void testSetResultType() {
    assertNotNull(form.getResultType());
    assertEquals("1", form.getResultType());

    form.setResultType("2");
    assertNotNull(form.getResultType());
    assertEquals("2", form.getResultType());

    form.setResultType(null);
    assertNotNull(form.getResultType());
    assertEquals("2", form.getResultType());
  }

  @Test
  public void testGetResultTypeItems() {
    assertNotNull(form.getResultTypeItems());
    assertEquals(2, form.getResultTypeItems().length);
  }

  @Test
  public void testSetSortOrder() {
    assertNotNull(form.getSortOrder());
    assertEquals("UNIT_NAME", form.getSortOrder());

    form.setSortOrder("CARETYPE");
    assertNotNull(form.getSortOrder());
    assertEquals("CARETYPE", form.getSortOrder());

    form.setSortOrder(null);
    assertNotNull(form.getSortOrder());
    assertEquals("CARETYPE", form.getSortOrder());
  }

  @Test
  public void testGetHealthcareTypeItems() {
    assertNotNull(form.getHealthcareTypeItems());
    assertTrue(form.getHealthcareTypeItems().length > 0);
  }

  @Test
  public void testShowAll() {
    assertNull(form.getShowAll());

    form.setShowAll("true");
    assertEquals("true", form.getShowAll());

    form.setShowAll("false");
    assertEquals("false", form.getShowAll());
  }
}
