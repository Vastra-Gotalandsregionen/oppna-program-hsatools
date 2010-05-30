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

package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import se.vgregion.kivtools.search.util.MvkClient;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class UnitDetailsServiceImplTest {
  private static final String UNIT_NAME = "UnitName";
  private static final String UNIT_HSA_IDENTITY = "UnitHsaIdentity";
  private UnitDetailsServiceImpl unitDetailsService;
  private HttpFetcherMock httpFetcher;
  private Calendar calendar;
  private final SearchServiceMock searchService = new SearchServiceMock();

  @Before
  public void setup() throws Exception {
    httpFetcher = new HttpFetcherMock();

    setupTimeSource();

    MvkClient mvkClient = new MvkClient();
    mvkClient.setHttpFetcher(httpFetcher);
    mvkClient.setMvkUrl("http://localhost?mvk=1");
    mvkClient.setMvkGuid("uid123");

    unitDetailsService = new UnitDetailsServiceImpl();
    unitDetailsService.setSearchService(searchService);
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
    this.searchService.setExceptionToThrow(new KivException("Test"));

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

  @Test
  public void shortNameIsSetCorrectly() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=dummy&guid=uid123", "<xml></xml>");

    Unit unit = new UnitBuilder().hsaIdentity("dummy").shortname("tandreg varberg").build();
    unitDetailsService.setSearchService(new OneUnitSearchService(unit));

    Organization organization = unitDetailsService.getUnitDetails("dummy");
    assertEquals("short name", "tandreg varberg", organization.getUnit().get(0).getShortName());
  }

  @Test
  public void drivingDirectionsAreSetCorrectly() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=dummy&guid=uid123", "<xml></xml>");

    Unit unit = new UnitBuilder().hsaIdentity("dummy").routePart("rakt fram").routePart("till höger").build();
    unitDetailsService.setSearchService(new OneUnitSearchService(unit));

    Organization organization = unitDetailsService.getUnitDetails("dummy");
    List<String> drivingDirections = organization.getUnit().get(0).getDrivingDirections();
    assertEquals("driving directions", 2, drivingDirections.size());
    assertEquals("driving directions part 1", "rakt fram", drivingDirections.get(0));
    assertEquals("driving directions part 2", "till höger", drivingDirections.get(1));
  }

  @Test
  public void careTypeIsSetCorrectly() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=dummy&guid=uid123", "<xml></xml>");

    Unit unit = new UnitBuilder().hsaIdentity("dummy").careType("02").build();
    unitDetailsService.setSearchService(new OneUnitSearchService(unit));

    Organization organization = unitDetailsService.getUnitDetails("dummy");
    assertEquals("care type", "02", organization.getUnit().get(0).getCareType());
  }

  @Test
  public void unitCoordinatesAreSetCorrectly() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=dummy&guid=uid123", "<xml></xml>");

    Unit unit = new UnitBuilder().hsaIdentity("dummy").address("storgatan 1").rt90(123456, 789012).wgs84(12.3456, 78.9012).build();
    unitDetailsService.setSearchService(new OneUnitSearchService(unit));

    Organization organization = unitDetailsService.getUnitDetails("dummy");
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Address address = organization.getUnit().get(0).getAddress().get(0);
    assertEquals("rt90 x", "123456", address.getGeoCoordinates().getXpos().get(0));
    assertEquals("rt90 y", "789012", address.getGeoCoordinates().getYpos().get(0));
    assertEquals("wgs84 lat", "12.3456", address.getGeoCoordinatesWGS84().getLatitude().get(0));
    assertEquals("wgs84 long", "78.9012", address.getGeoCoordinatesWGS84().getLongitude().get(0));
  }

  @Test
  public void mvkServicesAreSetCorrectly() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=dummy&guid=uid123", "<?xml version=\"1.0\"?><casetypes><casetype>abc</casetype><casetype>def</casetype></casetypes>");

    Unit unit = new UnitBuilder().hsaIdentity("dummy").build();
    unitDetailsService.setSearchService(new OneUnitSearchService(unit));

    Organization organization = unitDetailsService.getUnitDetails("dummy");
    List<String> mvkServices = organization.getUnit().get(0).getMvkServices();
    assertEquals("mvk case types", 2, mvkServices.size());
    assertEquals("case type 1", "abc", mvkServices.get(0));
    assertEquals("case type 2", "def", mvkServices.get(1));
  }

  @Test
  public void addressWithoutStreetInformationIsConcatenated() {
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=dummy&guid=uid123", "<xml></xml>");

    Unit unit = new UnitBuilder().hsaIdentity("dummy").address("", "abc", "def").build();
    unitDetailsService.setSearchService(new OneUnitSearchService(unit));

    Organization organization = unitDetailsService.getUnitDetails("dummy");
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Address address = organization.getUnit().get(0).getAddress().get(0);
    assertTrue("isConcatenated", address.isIsConcatenated());
    assertEquals("concatenated address", "abcdef", address.getConcatenatedAddress());
  }

  private static class UnitBuilder {
    private String shortName;
    private String hsaIdentity;
    private List<String> route;
    private String careType;
    private int rt90x;
    private int rt90y;
    private double wgs84Lat;
    private double wgs84Long;
    private String street;
    private List<String> additionalInfo;

    public Unit build() {
      Unit unit = new Unit();
      unit.setHsaIdentity(hsaIdentity);
      unit.setOrganizationalUnitNameShort(shortName);
      unit.addHsaRoute(route);
      unit.setCareType(careType);
      unit.setRt90X(rt90x);
      unit.setRt90Y(rt90y);
      unit.setWgs84Lat(wgs84Lat);
      unit.setWgs84Long(wgs84Long);
      if (this.street != null) {
        Address address = new Address();
        address.setStreet(street);
        unit.setHsaStreetAddress(address);
        address.setAdditionalInfo(additionalInfo);
      }
      return unit;
    }

    public UnitBuilder address(String street, String... additionalInfo) {
      this.street = street;
      if (additionalInfo != null) {
        this.additionalInfo = new ArrayList<String>();
        for (String string : additionalInfo) {
          this.additionalInfo.add(string);
        }
      }
      return this;
    }

    public UnitBuilder wgs84(double wgs84Lat, double wgs84Long) {
      this.wgs84Lat = wgs84Lat;
      this.wgs84Long = wgs84Long;
      return this;
    }

    public UnitBuilder rt90(int rt90x, int rt90y) {
      this.rt90x = rt90x;
      this.rt90y = rt90y;
      return this;
    }

    public UnitBuilder careType(String careType) {
      this.careType = careType;
      return this;
    }

    public UnitBuilder routePart(String routePart) {
      if (this.route == null) {
        this.route = new ArrayList<String>();
      }
      this.route.add(routePart);
      return this;
    }

    public UnitBuilder hsaIdentity(String hsaIdentity) {
      this.hsaIdentity = hsaIdentity;
      return this;
    }

    public UnitBuilder shortname(String shortName) {
      this.shortName = shortName;
      return this;
    }
  }

  private static class OneUnitSearchService extends SearchServiceMockBase {
    private final Unit unit;

    public OneUnitSearchService(final Unit unit) {
      this.unit = unit;
    }

    @Override
    public Unit getUnitByHsaId(String hsaId) throws KivException {
      return this.unit;
    }
  }

  private static class SearchServiceMock extends SearchServiceMockBase {
    private Map<String, Unit> units;
    private KivException exceptionToThrow;

    public SearchServiceMock() {
      this.units = createUnitMocks(generateUnitAddress());
    }

    public void setExceptionToThrow(KivException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public Unit getUnitByHsaId(String hsaId) throws KivException {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
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
        unit.addHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("1111"));
        unit.setLabeledURI("http://unit" + i);
        unit.setMail("unit" + i + "@vgregion.se");

        unit.setHsaVisitingRules("Ingen parfym tack");
        try {
          unit.addHsaDropInHours(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0), new WeekdayTime(6, 6, 10, 0, 14, 0), new WeekdayTime(7, 7, 10, 0, 12, 0)));
          unit.addHsaTelephoneTime(new WeekdayTime(1, 5, 8, 0, 17, 0));
          unit.addHsaSurgeryHours(Arrays.asList(new WeekdayTime(1, 5, 8, 0, 17, 0), new WeekdayTime(6, 6, 10, 0, 14, 0)));
        } catch (InvalidFormatException e) {
          throw new RuntimeException(e);
        }
        unit.setHsaManagementText("Landsting/region");
        unit.addHealthcareTypes(Arrays.asList(new HealthcareType(null, "Vårdcentral", false, 0), new HealthcareType(null, "Akutmottagning", false, 1)));

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
