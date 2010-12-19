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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Test;
import org.springframework.ldap.core.ContextMapper;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.util.StringUtil;

public class UnitFetcherLTHTest {
  private final LdapTemplateMock ldapTemplate = new LdapTemplateMock();
  private final UnitFetcherLTH unitFetcher = new UnitFetcherLTH(this.ldapTemplate, new String[] { "1", "2" }, new String[] { "3", "4" });

  @Test
  public void unitsAreReturnedFromFetchUnits() {
    List<UnitComposition> units = this.unitFetcher.fetchUnits(Arrays.asList("1010", "1012"));
    assertNotNull("units", units);
    assertEquals("unit count", 2, units.size());
  }

  private static class LdapTemplateMock extends se.vgregion.kivtools.mocks.ldap.LdapTemplateMock {
    @Override
    public List<?> search(String base, String filter, int searchScope, ContextMapper mapper) {
      assertEquals(SearchControls.SUBTREE_SCOPE, searchScope);
      List<UnitComposition> unitslist = Arrays.asList(createUnit("unit1", "unit1-id", "ou=unit1,ou=org,o=VGR", UnitType.CARE_CENTER, null),
          createUnit("unit2", "unit2-id", "ou=unit2,ou=unit1,ou=org,o=VGR", UnitType.OTHER_CARE, "Göteborg"));

      return unitslist;
    }

    private static UnitComposition createUnit(String name, String identity, String dn, UnitType careType, String city) {
      UnitComposition unit = new UnitComposition();
      unit.setDn(dn);
      unit.getEniroUnit().setName(name);
      unit.getEniroUnit().setId(identity);
      unit.setCareType(careType);
      if (!StringUtil.isEmpty(city)) {
        Address address = new Address();
        address.setCity(city);
        unit.getEniroUnit().getTextOrImageOrAddress().add(address);
      }
      return unit;
    }
  }
}
