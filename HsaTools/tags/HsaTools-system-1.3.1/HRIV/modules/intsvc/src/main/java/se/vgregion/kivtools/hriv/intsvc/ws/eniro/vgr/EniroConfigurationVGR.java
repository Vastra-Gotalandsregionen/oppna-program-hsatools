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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.vgregion.kivtools.hriv.intsvc.ws.eniro.AreaConfig;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.EniroConfiguration;

public class EniroConfigurationVGR implements EniroConfiguration {
  @Override
  public List<AreaConfig> getConfiguration() {
    List<AreaConfig> config = new ArrayList<AreaConfig>();
    config.add(new AreaConfig("232100-0131 VGR Göteborg", "Västra Götalandsregionen Göteborg", "Göteborg", Arrays
        .asList("1440", "1480", "1401", "1488", "1441", "1463", "1481", "1402", "1415", "1407"), "Vastra Gotalandsregionen Goteborg"));
    config.add(new AreaConfig("232100-0131 VGR Borås", "Västra Götalandsregionen Borås", "Borås", Arrays.asList("1489", "1443", "1490", "1466", "1463", "1465", "1452", "1491", "1442"),
        "Vastra Gotalandsregionen Boras"));
    config.add(new AreaConfig("232100-0131 VGR Uddevalla", "Västra Götalandsregionen Uddevalla", "Uddevalla", Arrays.asList("1460", "1438", "1439", "1462", "1484", "1461", "1430", "1421", "1427",
        "1486", "1435", "1419", "1488", "1485", "1487", "1492"), "Vastra Gotalandsregionen Uddevalla"));
    config.add(new AreaConfig("232100-0131 VGR Skövde", "Västra Götalandsregionen Skövde", "Skövde", Arrays.asList("1445", "1499", "1444", "1447", "1471", "1497", "1446", "1494", "1493", "1495",
        "1496", "1472", "1498", "1473", "1470"), "Vastra Gotalandsregionen Skovde"));
    return config;
  }
}
