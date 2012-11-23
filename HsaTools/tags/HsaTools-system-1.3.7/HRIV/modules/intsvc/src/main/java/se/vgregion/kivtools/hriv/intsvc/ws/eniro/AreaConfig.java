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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AreaConfig {
  private final String organizationid;
  private final String organizationName;
  private final String locality;
  private final List<String> municipalities = new ArrayList<String>();
  private final String basename;

  public AreaConfig(String organizationId, String organizationName, String locality, List<String> municipalities, String basename) {
    this.organizationid = organizationId;
    this.organizationName = organizationName;
    this.locality = locality;
    this.municipalities.addAll(municipalities);
    this.basename = basename;
  }

  public String getOrganizationid() {
    return this.organizationid;
  }

  public String getOrganizationName() {
    return this.organizationName;
  }

  public String getLocality() {
    return this.locality;
  }

  public List<String> getMunicipalities() {
    return Collections.unmodifiableList(this.municipalities);
  }

  public String getBasename() {
    return this.basename;
  }
}
