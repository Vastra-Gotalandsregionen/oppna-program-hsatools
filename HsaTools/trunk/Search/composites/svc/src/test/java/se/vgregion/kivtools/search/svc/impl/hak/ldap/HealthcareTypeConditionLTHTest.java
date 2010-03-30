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

package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;

public class HealthcareTypeConditionLTHTest {
  private HealthcareTypeConditionHelper helper;

  @Before
  public void setUp() throws Exception {
    helper = new HealthcareTypeConditionHelper() {
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
    helper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.hak.ldap.search-composite-svc-healthcare-type-conditions");

    HealthcareType healthcareType = helper.getHealthcareTypeByName("Vårdcentral/allmänläkare");
    assertNotNull(healthcareType);
    assertEquals("1502,1503", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(1), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Administration");
    assertNotNull(healthcareType);
    assertEquals(
        "SE2321000115-O55547,SE2321000115-O65770,SE2321000115-O67161,SE2321000115-O19987,SE2321000115-O89397,SE2321000115-O46701,SE2321000115-O49549,SE2321000115-O87701,SE2321000115-O32743,SE2321000115-O50056,SE2321000115-O24469,SE2321000115-O22768,SE2321000115-O42957,SE2321000115-O76057,SE2321000115-O13240",
        healthcareType.getConditions().get("hsaIdentity"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(2), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Barnavårdscentral");
    assertNotNull(healthcareType);
    assertEquals("1504", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(3), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Blodcentral");
    assertNotNull(healthcareType);
    assertEquals("1202", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(4), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Distriktssköterskemottagning");
    assertNotNull(healthcareType);
    assertEquals("1506", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(5), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Handikappverksamhet");
    assertNotNull(healthcareType);
    assertEquals("1005,1007,1015,1304,1404,1405,2005,2006,2017,2019", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(6), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Kvälls- och helgmottagning");
    assertNotNull(healthcareType);
    assertEquals("1500", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(7), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Mödrahälsovård");
    assertNotNull(healthcareType);
    assertEquals("1505", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(8), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Patientservice");
    assertNotNull(healthcareType);
    assertEquals("SE2321000115-O52092,SE2321000115-O13498", healthcareType.getConditions().get("hsaIdentity"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(9), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Privata specialister");
    assertNotNull(healthcareType);
    assertEquals(
        "SE2321000115-O75730,SE2321000115-O20869,SE2321000115-O46502,SE2321000115-O67694,SE2321000115-O90329,SE2321000115-O54336,SE2321000115-O59854,SE2321000115-O91409,SE2321000115-O63266,SE2321000115-O18860,SE2321000115-O94534,SE2321000115-O47029,SE2321000115-O44474,SE2321000115-O52082,SE2321000115-O76548,SE2321000115-O64158,SE2321000115-O12253,SE2321000115-O24721,SE2321000115-O10293,SE2321000115-O36589,SE2321000115-O98488,SE2321000115-O95467,SE2321000115-O88226,SE2321000115-O25815,SE2321000115-O26371,SE2321000115-O90000,SE2321000115-O42633,SE2321000115-O47241,SE2321000115-O59252,SE2321000115-O81912,SE2321000115-O88562,SE2321000115-O39205,SE2321000115-O64706",
        healthcareType.getConditions().get("hsaIdentity"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(10), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Psykiatri");
    assertNotNull(healthcareType);
    assertEquals("1004,1020,1024,1027,1139,1412,1600,1603,1604,1605,1607,1608,1609,1611,1614,1615,1616,1617,1618,1619", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(11), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Sjukgymnastik");
    assertNotNull(healthcareType);
    assertEquals("1413", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(12), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Sjukhus/specialistvård");
    assertNotNull(healthcareType);
    assertEquals(
        "1000,1001,1002,1003,1006,1008,1009,1010,1011,1012,1013,1014,1016,1017,1018,1019,1021,1022,1023,1025,1026,1028,1100,1101,1102,1105,1106,1107,1108,1109,1110,1111,1112,1114,1115,1116,1117,1118,1119,1120,1121,1122,1123,1124,1125,1126,1127,1128,1129,1130,1131,1132,1133,1134,1136,1137,1138,1203,1204,1205,1206,1207,1208,1209,1210,1211,1212,1213,1214,1215,1216,1217,1218,1219,1220,1221,1222,1223,1224,1227,1228,1229,1230,1231,1232,1302,1303,1306,1307,1308,1310,1311,1312,1313,1314,1315,1316,1317,1318,1319,1320,1321,1322,1323,1324,1325,1326,1327,1328,1329,1330,1331,1332,1333,1334,1335,1336,1337,1339,1340,1341,1342,1400,1406,1408,1409,1410,1412,1414,1507,1509,1512,1513,1514,1515,1702,1703,1704,1705,1706,1707,1708,1709,1710,1711,1712,1713,1714,1715,1716,1717,1718,1719,2001,2002,2004,2007,2008,2009,2010,2011,2012,2016,2018,2020,2021,2102,2103,2106,2107,2108,2205",
        healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(13), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Tandvård");
    assertNotNull(healthcareType);
    assertEquals("1800,1802,1804,1801,1807,1808,1810,1812", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(14), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Ungdomsmottagning");
    assertNotNull(healthcareType);
    assertEquals("1519", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(15), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Vårdval Halland");
    assertNotNull(healthcareType);
    assertEquals("1502", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(16), healthcareType.getIndex());

    healthcareType = helper.getHealthcareTypeByName("Övriga mottagningar");
    assertNotNull(healthcareType);
    assertEquals("1103,1104,1113,1338,1343,1402,1403,1407,1411,1518,1606,2003,2202,2203,2204", healthcareType.getConditions().get("businessClassificationCode"));
    assertFalse(healthcareType.isFiltered());
    assertEquals(Integer.valueOf(17), healthcareType.getIndex());
  }
}
