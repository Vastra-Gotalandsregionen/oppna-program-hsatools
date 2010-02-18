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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;

public class UnitRepositoryHRIATest {
  private LDAPConnectionMock ldapConnection;
  private LdapConnectionPoolMock ldapConnectionPool;
  private UnitRepositoryHRIA unitRepository;

  @Before
  public void setUp() throws Exception {
    ldapConnection = new LDAPConnectionMock();
    ldapConnectionPool = new LdapConnectionPoolMock(ldapConnection);

    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    healthcareTypeConditionHelper.setImplResourcePath("basic_healthcaretypeconditionhelper");

    final SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();

    Unit resultUnit1 = new Unit();
    resultUnit1.setHsaIdentity("abc-123");
    resultUnit1.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit1.setVgrAnsvarsnummer(Arrays.asList("11223"));

    Unit resultUnit2 = new Unit();
    resultUnit2.setHsaIdentity("abc-456");
    resultUnit2.setHsaBusinessClassificationCode(Arrays.asList("1504"));

    Unit resultUnit3 = new Unit();
    resultUnit3.setHsaIdentity("SE6460000000-E000000000222");
    resultUnit3.setHsaBusinessClassificationCode(Arrays.asList("abc"));

    Unit resultUnit4 = new Unit();
    resultUnit4.setHsaIdentity("abc-789");
    resultUnit4.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit4.setVgrAnsvarsnummer(Arrays.asList("12345"));

    result.add(resultUnit1);
    result.add(resultUnit2);
    result.add(resultUnit3);
    result.add(resultUnit4);

    unitRepository = new UnitRepositoryHRIA() {
      @Override
      protected SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws KivException {
        return result;
      }
    };
    unitRepository.setLdapConnectionPool(ldapConnectionPool);
  }

  @Test
  public void testSearchAdvancedUnitsMethod() throws Exception {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.addCondition("conditionKey", "value1,value2");

    Unit searchUnit = new Unit();
    searchUnit.setName("unitName");
    searchUnit.setHsaMunicipalityName("Göteborg");
    searchUnit.setHsaMunicipalityCode("10032");
    searchUnit.setHsaIdentity("hsaId-1");
    searchUnit.setHealthcareTypes(Arrays.asList(healthcareType));
    searchUnit.setVgrVardVal(true);

    int maxResults = 10;
    UnitNameComparator sortOrder = new UnitNameComparator();
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(searchUnit, maxResults, sortOrder, Arrays.asList(Integer.valueOf(1504)));
    assertNotNull(units);
    assertEquals(1, units.size());
  }
}
