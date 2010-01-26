/**
 * Copyright 2009 Västra Götalandsregionen
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
 */
package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.mocks.http.HttpFetcherMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.domain.values.ZipCode;
import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.util.MvkClient;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class UnitDetailsServiceImplTest {
  private static final String UNIT_NAME = "UnitName";
  private static final String UNIT_HSA_IDENTITY = "UnitHsaIdentity";
  private UnitDetailsServiceImpl unitDetailsService;
  private HttpFetcherMock httpFetcher;
  private Calendar calendar;

  @Before
  public void setup() throws Exception {
    httpFetcher = new HttpFetcherMock();

    setupTimeSource();

    MvkClient mvkClient = new MvkClient();
    mvkClient.setHttpFetcher(httpFetcher);
    mvkClient.setMvkUrl("http://localhost?mvk=1");
    mvkClient.setMvkGuid("uid123");

    unitDetailsService = new UnitDetailsServiceImpl();
    unitDetailsService.setUnitRepository(new UnitRepositoryMock());
    unitDetailsService.setMvkClient(mvkClient);
  }

  private void setupTimeSource() {
    calendar = Calendar.getInstance();
    calendar.set(2009, 8, 19, 16, 23);
    calendar.set(Calendar.SECOND, 48);
    calendar.set(Calendar.MILLISECOND, 0);
    TimeSource timeSource = new TimeSource() {
      @Override
      public long millis() {
        return calendar.getTimeInMillis();
      }
    };
    TimeUtil.setTimeSource(timeSource);
  }

  @Test
  public void testGetUnitDetails() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=" + UNIT_HSA_IDENTITY + 1 + "&guid=uid123", "<xml></xml>");

    Organization organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 1);
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit unit = organization.getUnit().get(0);
    assertEquals(UNIT_HSA_IDENTITY + 1, unit.getId());
  }

  @Test
  public void testGetUnitDetailsKivException() throws Exception {
    UnitRepository unitRepositoryMock = new UnitRepository() {
      @Override
      public Unit getUnitByHsaId(String hsaId) throws KivException {
        throw new KivException("Test");
      }
    };
    unitDetailsService.setUnitRepository(unitRepositoryMock);

    try {
      unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetUnitDetailsWithNullAndEmptyString() {
    assertNotNull(unitDetailsService.getUnitDetails(null));
    assertNotNull(unitDetailsService.getUnitDetails(""));
  }

  @Test
  public void testUnitInfo() {
    // Check Unit 0

    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=" + UNIT_HSA_IDENTITY + 0 + "&guid=uid123", "<xml></xml>");
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=" + UNIT_HSA_IDENTITY + 1 + "&guid=uid123", "<xml></xml>");
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=" + UNIT_HSA_IDENTITY + 2 + "&guid=uid123", "<xml></xml>");
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=" + UNIT_HSA_IDENTITY + 3 + "&guid=uid123", "<xml></xml>");

    Organization organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 0);
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit unit = organization.getUnit().get(0);
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Address addressWs = unit.getAddress().get(0);
    assertEquals("En trevlig mottagning", unit.getDescription().get(0).getValue());
    assertEquals("Temp info", unit.getTemporaryInformation().get(0).getValue());
    assertEquals("Ref info", unit.getReferralInformation().get(0).getValue());
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals(null, addressWs.getStreetNumber());
    assertEquals("1111", unit.getTelephone().get(0).getTelephoneNumber().get(0));
    assertEquals("http://unit0", unit.getEAlias().get(0).getAlias());
    assertEquals("unit0@vgregion.se", unit.getEAlias().get(1).getAlias());
    assertEquals("Ingen parfym tack", unit.getVisitingConditions().get(0).getVisitingRules());
    assertEquals("Måndag-Fredag 08:00-17:00, Lördag 10:00-14:00", unit.getVisitingConditions().get(0).getVisitingHours());
    assertEquals("Måndag-Fredag 08:00-17:00, Lördag 10:00-14:00, Söndag 10:00-12:00", unit.getVisitingConditions().get(0).getDropInHours());
    assertEquals("Måndag-Fredag 08:00-17:00", unit.getVisitingConditions().get(0).getTelephoneHours());
    assertEquals("Landsting/region", unit.getManagement().getValue());
    assertEquals("Vårdcentral", unit.getBusinessClassification().get(0).getValue());
    assertEquals(2, unit.getBusinessClassification().size());
    assertEquals("Götlaborg", unit.getLocality().getValue());

    // Check Unit 1
    organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 1);
    unit = organization.getUnit().get(0);
    addressWs = unit.getAddress().get(0);
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals("12", addressWs.getStreetNumber());
    assertEquals(0, unit.getTemporaryInformation().size());
    assertEquals(0, unit.getReferralInformation().size());
    assertNull(unit.getLocality());

    // Check Unit 2
    organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 2);
    unit = organization.getUnit().get(0);
    addressWs = unit.getAddress().get(0);
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals("1B", addressWs.getStreetNumber());
    assertEquals(0, unit.getTemporaryInformation().size());
    assertEquals(0, unit.getReferralInformation().size());

    // Check Unit 3
    organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 3);
    unit = organization.getUnit().get(0);
    addressWs = unit.getAddress().get(0);
    assertEquals("Desc1, Desc2, Teststreet", addressWs.getStreetName());
    assertEquals("12b", addressWs.getStreetNumber());
    assertEquals(0, unit.getTemporaryInformation().size());
    assertEquals(0, unit.getReferralInformation().size());
  }

  @Test
  public void testMvkEnable() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=" + UNIT_HSA_IDENTITY + 0 + "&guid=uid123",
        "<?xml version=\"1.0\"?><casetypes><casetype>abc</casetype><casetype>def</casetype></casetypes>");

    Organization organization = unitDetailsService.getUnitDetails(UNIT_HSA_IDENTITY + 0);
    assertTrue(organization.getUnit().get(0).isMvkEnable());
  }

  private static class UnitRepositoryMock extends UnitRepository {
    private Map<String, Unit> units;

    public UnitRepositoryMock() {
      this.units = createUnitMocks(generateUnitAddress());
    }

    @Override
    public Unit getUnitByHsaId(String hsaId) throws KivException {
      return units.get(hsaId);
    }

    // Create 4 units with hsaIdentity UnitHsaIdentity<0 to 3>
    private Map<String, Unit> createUnitMocks(List<Address> addressList) {
      Map<String, Unit> units = new LinkedHashMap<String, Unit>();
      for (int i = 0; i < addressList.size(); i++) {
        Unit unit = new Unit();
        unit.setName(UNIT_NAME + i);
        unit.setHsaIdentity(UNIT_HSA_IDENTITY + i);
        unit.setDescription(Arrays.asList("En trevlig mottagning"));
        unit.setHsaStreetAddress(addressList.get(i));
        unit.setHsaPostalAddress(addressList.get(i));
        unit.setHsaPublicTelephoneNumber(Arrays.asList(PhoneNumber.createPhoneNumber("1111")));
        unit.setLabeledURI("http://unit" + i);
        unit.setMail("unit" + i + "@vgregion.se");

        unit.setHsaVisitingRules("Ingen parfym tack");
        try {
          unit.setHsaDropInHours(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0), new WeekdayTime(6, 6, 10, 0, 14, 0), new WeekdayTime(7, 7, 10, 0, 12, 0)));
          unit.setHsaTelephoneTime(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0)));
          unit.setHsaSurgeryHours(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0), new WeekdayTime(6, 6, 10, 0, 14, 0)));
        } catch (InvalidFormatException e) {
          throw new RuntimeException(e);
        }
        unit.setHsaManagementText("Landsting/region");
        unit.setHealthcareTypes(Arrays.asList(new HealthcareType(null, "Vårdcentral", false, 0), new HealthcareType(null, "Akutmottagning", false, 1)));

        switch (i) {
          case 0:
            unit.setHsaMunicipalityName("Götlaborg");
            unit.setVgrTempInfo("20090701-20091130 Temp info");
            unit.setVgrRefInfo("Ref info");
            break;
          case 1:
            unit.setVgrTempInfo("20090701-20090830 Temp info");
            unit.setVgrRefInfo("Ref info");
            break;
        }

        units.put(unit.getHsaIdentity(), unit);
      }
      return units;
    }

    private List<Address> generateUnitAddress() {
      Address addressWithoutNb = new Address("Teststreet", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
      Address addressWithNb = new Address("Teststreet 12", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
      Address addressWithNbAndChar = new Address("Teststreet 1B", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
      Address addressWithNbAndCharNoSpace = new Address("Teststreet12b", new ZipCode("414 57"), "Göteborg", Arrays.asList("Desc1", "Desc2"));
      return Arrays.asList(addressWithoutNb, addressWithNb, addressWithNbAndChar, addressWithNbAndCharNoSpace);
    }
  }
}
