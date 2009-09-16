package se.vgregion.kivtools.hriv.presentation.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.MunicipalityHelper;

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
}
