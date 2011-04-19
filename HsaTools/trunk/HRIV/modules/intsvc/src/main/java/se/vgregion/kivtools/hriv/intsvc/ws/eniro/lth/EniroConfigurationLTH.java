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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro.lth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.vgregion.kivtools.hriv.intsvc.ws.eniro.AreaConfig;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.EniroConfiguration;

public class EniroConfigurationLTH implements EniroConfiguration {
  @Override
  public List<AreaConfig> getConfiguration() {
    List<AreaConfig> config = new ArrayList<AreaConfig>();
    config.add(new AreaConfig("232100-0115 LTH", "Region Halland", "Halland", Arrays.asList("1382", "1380", "1315", "1384", "1381", "1383"), "Region Halland"));
    return config;
  }
}
