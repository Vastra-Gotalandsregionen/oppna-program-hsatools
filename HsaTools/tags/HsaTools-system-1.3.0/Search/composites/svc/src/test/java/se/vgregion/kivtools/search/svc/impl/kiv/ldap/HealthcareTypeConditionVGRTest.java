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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;

public class HealthcareTypeConditionVGRTest {
  private HealthcareTypeConditionHelper helper;

  @Before
  public void setUp() throws Exception {
    this.helper = new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
  }

  @After
  public void tearDown() throws Exception {
    new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
  }

  @Test
  public void testRealFile() {
    this.helper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    HealthcareType healthcareType = this.helper.getHealthcareTypeByName("Akutmottagning");
    assertNotNull(healthcareType);
    assertEquals("1000,1100,1500,1600,1800,1801,1812", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertEquals("01", healthcareType.getConditions().get(KivwsCodeTableName.CARE_TYPE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(1), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Arbetsterapi");
    assertNotNull(healthcareType);
    assertEquals("1402", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(2), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Barnmorskemottagning");
    assertNotNull(healthcareType);
    assertEquals("1505", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(3), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Barnavårdscentral");
    assertNotNull(healthcareType);
    assertEquals("1504", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(4), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Barn- och ungdomsmedicin");
    assertNotNull(healthcareType);
    assertEquals("1013", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(5), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Dietist");
    assertNotNull(healthcareType);
    assertEquals("1403", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(6), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Gynekologi");
    assertNotNull(healthcareType);
    assertEquals("1311", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertNull("vgrAO3kod", healthcareType.getConditions().get(KivwsCodeTableName.VGR_AO3_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(7), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Habilitering");
    assertNotNull(healthcareType);
    assertEquals("1007,1404", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(8), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Jourcentral");
    assertNotNull(healthcareType);
    assertEquals("1500", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(9), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Kuratorsmottagning");
    assertNotNull(healthcareType);
    assertEquals("1407,1411", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(10), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Mottagningar på sjukhus");
    assertNotNull(healthcareType);
    assertEquals(
        "1002,1003,1005,1006,1009,1010,1011,1012,1014,1015,1016,1017,1018,1019,1021,1023,1025,1028,1102,1103,1104,1105,1106,1108,1109,1110,1112,1113,1114,1115,1116,1117,1119,1120,1121,1122,1123,1124,1125,1126,1127,1128,1129,1130,1131,1133,1134,1135,1136,1137,1138,1202,1204,1212,1302,1303,1304,1306,1307,1308,1310,1312,1313,1314,1315,1316,1317,1318,1319,1320,1321,1324,1325,1326,1328,1329,1330,1331,1332,1333,1334,1335,1336,1338,1339,1340,1342,1343,1344,1345,1346,1406,1408,1409,1410,1414,1512,1513,1514,1515,1606,1702,1703,1704,1706,1707,1708,1709,1710,1711,1712,1713,1714,1715,1716,1717,1718,1719,2002,2004,2006,2007,2008,2009,2011,2012,2016,2017,2021,2102,2103,2107,2205",
        healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(11), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Psykiatri");
    assertNotNull(healthcareType);
    assertEquals("1004,1020,1024,1027,1139,1412,1604,1605,1607,1609,1611,1614,1615,1616,1617,1618,1619",
        healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(12), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Psykologi, Psykisk hälsa");
    assertNotNull(healthcareType);
    assertEquals("1411", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(13), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Sjukgymnastik");
    assertNotNull(healthcareType);
    assertEquals("1413", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(14), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Sjukhus");
    assertNotNull(healthcareType);
    assertEquals("01", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESS_TYPE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(15), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Tandvård");
    assertNotNull(healthcareType);
    assertEquals("1800,1801,1802,1803,1804,1805,1806,1807,1808,1809,1810,1811,1812", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(16), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Ungdomsmottagning");
    assertNotNull(healthcareType);
    assertEquals("1519", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(17), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Vårdcentral");
    assertNotNull(healthcareType);
    assertEquals("02", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESS_TYPE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(18), healthcareType.getIndex());

    healthcareType = this.helper.getHealthcareTypeByName("Övriga mottagningar");
    assertNotNull(healthcareType);
    assertEquals("1103,1104,1113,1133,1338,1343,1502,1506,1513,1514,1515,1606,2103", healthcareType.getConditions().get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.toString()));
    assertNull("vgrAO3kod", healthcareType.getConditions().get(KivwsCodeTableName.VGR_AO3_CODE.toString()));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(19), healthcareType.getIndex());
  }
}
