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

package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimePoint;

/**
 * Mock implementation of the SearchService to use when no connection to an LDAP-server is available.
 */
public class SearchServiceMockImpl implements SearchService {
  private final SikSearchResultList<Person> personList = new SikSearchResultList<Person>();
  private final SikSearchResultList<Unit> unitList = new SikSearchResultList<Unit>();

  /**
   * Constructs a new mock-service.
   */
  public SearchServiceMockImpl() {
    this.init();
  }

  @Override
  public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException {
    SikSearchResultList<Employment> employments = new SikSearchResultList<Employment>();
    Employment e;

    e = new Employment();
    e.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("031-123456"));
    e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
    e.setModifyTimestamp(TimePoint.from(new Date()));
    employments.add(e);

    e = new Employment();
    e.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("031-23 23 23"));
    e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
    e.setModifyTimestamp(TimePoint.from(new Date()));
    employments.add(e);

    e = new Employment();
    e.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("08-2283393"));
    e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
    e.setModifyTimestamp(TimePoint.from(new Date()));
    employments.add(e);

    e = new Employment();
    e.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("030012350"));
    e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
    e.setModifyTimestamp(TimePoint.from(new Date()));
    employments.add(e);

    e = new Employment();
    e.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("+46822412350"));
    e.setVgrStrukturPerson(DN.createDNFromString("ou=Systemutveckling,ou=Systemintegration,ou=VGR IT,ou=Regionservice,ou=Org,o=vgr"));
    e.setModifyTimestamp(TimePoint.from(new Date()));
    employments.add(e);

    return employments;
  }

  @Override
  public Person getPersonById(String vgrId) throws KivException {
    Person p = null;

    for (Person person : this.personList) {
      if (person.getVgrId().equalsIgnoreCase(vgrId)) {
        p = person;
        break;
      }
    }
    return p;
  }

  @Override
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    Unit u = null;
    for (Unit unit : this.unitList) {
      if (hsaId.equals(unit.getHsaIdentity())) {
        u = unit;
        break;
      }
    }
    return u;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxResult) throws KivException {
    SikSearchResultList<Person> tempList = new SikSearchResultList<Person>();
    for (Person p : this.personList) {
      if (p.getVgrId().indexOf(vgrId) >= 0) {
        tempList.add(p);
      }
    }
    return tempList;
  }

  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions unit, int maxSearchResult) throws KivException {
    return this.unitList;
  }

  @Override
  public List<String> getAllPersonsId() throws KivException {
    List<String> result = new ArrayList<String>();
    result.add("anders1");
    result.add("hangy2");
    result.add("pj3");
    return result;
  }

  @Override
  public List<String> getAllUnitsHsaIdentity() throws KivException {
    List<String> result = new ArrayList<String>();
    result.add("ABC001");
    result.add("SE2321000131-E000000007190");
    result.add("SE2321000131-E000000000420");
    return result;
  }

  @Override
  public Unit getUnitByDN(String dn) throws KivException {

    Unit u = new Unit();
    ArrayList<PhoneNumber> p;
    ArrayList<String> a;

    u.setName("VGR IT");
    u.setHsaIdentity("ABC001");
    p = new ArrayList<PhoneNumber>();
    p.add(PhoneNumber.createPhoneNumber("031-123456"));
    p.add(PhoneNumber.createPhoneNumber("031-654321"));
    u.addHsaTelephoneNumber(p);
    a = new ArrayList<String>();
    a.add("Storgatan 1");
    a.add("411 01 Göteborg");
    u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
    u.setLdapDistinguishedName("VGR IT/Org C/Org B/Org A");
    return u;
  }

  private void init() {
    this.initPersons(this.personList);
    this.initUnits(this.unitList);
  }

  private void initPersons(SikSearchResultList<Person> list) {
    list.add(this.createPerson("Anders", "Sandin", "Asplund", "anders.asplund@knowit.se", "anders1", "anders1"));
    list.add(this.createPerson("Hasse", "", "Asplöv", "hasse.asplov@knowit.se", "anders2", "anders2"));
    list.add(this.createPerson("Pelle", "", "Aspkvist", "pelle.aspkvist@knowit.se", "anders3", "anders3"));
    list.add(this.createPerson("Jonas", "", "Aspblad", "jonas.aspblad@knowit.se", "anders4", "anders4"));
    list.add(this.createPerson("Nisse", "", "Aspgren", "nisse.aspgren@knowit.se", "anders5", "anders5"));
    list.add(this.createPerson("Pecka", "", "Aspquist", "pecka.aspquist@knowit.se", "anders6", "anders6"));
    list.add(this.createPerson("Hans", "", "Gyllensten", "hans.gyllensten@knowit.se", "hangy2", "hangy2"));
    list.add(this.createPerson("Peo", "", "Gyllenkvist", "peo.gyllenkvist@knowit.se", "hangy3", "hangy3"));
    list.add(this.createPerson("Jonathan", "", "Gyllengren", "jonathan.gyllengren@knowit.se", "hangy4", "hangy4"));
    list.add(this.createPerson("Anna", "", "Gyllenhammar", "anna.gyllenhammar@knowit.se", "hangy5", "hangy5"));
    list.add(this.createPerson("Niklas", "", "Gyllenblad", "niklas.gyllenblad@knowit.se", "hangy6", "hangy6"));
    list.add(this.createPerson("Petrov", "", "Gyllelöv", "petrov.gyllenlov@knowit.se", "hangy7", "hangy7"));
    list.add(this.createPerson("Per-Johan", "", "Andersson", "per.johan.andersson@knowit.se", "cn=ulfsa3,ou=Personal,o=vgr", "pj3"));
    list.add(this.createPerson("Per-Olof", "", "Anderssen", "per.olof.anderssen@knowit.se", "cn=ulfsa4,ou=Personal,o=vgr", "pj4"));
    list.add(this.createPerson("Joakim", "", "Anderson", "joakim.anderson@knowit.se", "cn=ulfsa5,ou=Personal,o=vgr", "pj5"));
    list.add(this.createPerson("Anton", "", "Andersen", "anton.andersen@knowit.se", "cn=ulfsa6,ou=Personal,o=vgr", "pj6"));
    list.add(this.createPerson("Nicklas", "", "Anderssund", "nicklas.anderssund@knowit.se", "cn=ulfsa7,ou=Personal,o=vgr", "pj7"));
    list.add(this.createPerson("Pierre", "", "Anderslind", "pierre.anderslind@knowit.se", "cn=ulfsa8,ou=Personal,o=vgr", "pj8"));
    list.add(this.createPerson("Susanna", "", "Anderslind", "susanna.anderslind@knowit.se", "cn=ulfsa9,ou=Personal,o=vgr", "pj9"));
    list.add(this.createPerson("Susanne", "", "Anderslind", "susanne.anderslind@knowit.se", "cn=ulfs10,ou=Personal,o=vgr", "pj10"));
    list.add(this.createPerson("Susan", "", "Anderslind", "susan.anderslind@knowit.se", "cn=ulfs11,ou=Personal,o=vgr", "pj11"));
    list.add(this.createPerson("Sanna", "", "Anderslind", "sanna.anderslind@knowit.se", "cn=ulfs12,ou=Personal,o=vgr", "pj12"));
    list.add(this.createPerson("Ann-Sofie", "", "Anderslind", "ann.sofie.anderslind@knowit.se", "cn=ulfs13,ou=Personal,o=vgr", "pj13"));
  }

  private Person createPerson(String givenName, String middleName, String surname, String email, String dn, String vgrId) {
    Person p = new Person();
    p.setGivenName(givenName);
    p.setSn(surname);
    p.setFullName(StringUtil.concatenate(Arrays.asList(givenName, middleName, surname), " "));
    p.setMail(email);
    p.setDn(dn);
    p.setVgrId(vgrId);
    return p;
  }

  private void initUnits(SikSearchResultList<Unit> list) {
    Unit u;
    List<String> description = Arrays.asList("Fusce elementum enim id lacus fringilla mollis. Aliquam et libero leo, at sollicitudin purus. "
        + "In hac habitasse platea dictumst. Donec nec aliquam leo. Sed dui lorem, aliquam id placerat id, posuere sed metus. "
        + "Integer feugiat ultrices nisl at congue. Sed at posuere lorem. Class aptent taciti sociosqu ad litora torquent per "
        + "conubia nostra, per inceptos himenaeos. Phasellus at turpis mi. Praesent sit amet diam diam. Integer in dolor sed erat dapibus malesuada quis eu nulla.");
    u = new Unit();
    u.setName("VGR IT");
    u.setHsaIdentity("ABC001");
    u.setLocality("Göteborg");
    u.setHsaMunicipalityCode("1401");
    u.setVgrVardVal(false);
    u.setHsaBusinessClassificationCode(Arrays.asList("1102", "1812"));
    u.addDescription(description);
    u.addInternalDescription(description);
    u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Område 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
    u.setHsaManagementText("Offentlig vårdgivare");
    u.setHsaVisitingRuleAge("0-60");
    u.setHsaVisitingRules("Endast tidsbokade besök");
    u.setShowVisitingRules(true);
    u.setShowAgeInterval(true);
    u.setLabeledURI("http://localhost:8180");
    u.setWgs84Lat(57.6696);
    u.setWgs84Long(12.572);
    u.setMvkCaseTypes(Arrays.asList("yadda"));
    u.addHsaRoute(Arrays.asList("Från riksväg 40, avfart Bollebygd.", "Kör mot Bollebygds centrum och parkera på Gästgivartorget.",
        "Om man går till hörnan vid Systembolaget och står med ryggen mot gamla riksväg 40 ser man Vårdcentralen."));
    this.initHealthcareTypes(u);
    this.initUnitPhoneNumbers(u);
    this.initUnitAddresses(u, "Storgatan 1", "411 01 Göteborg");
    this.initUnitHours(u);
    list.add(u);

    u = new Unit();
    u.setHsaIdentity("SE2321000131-E000000007190");
    u.setName("Sahlgrenska Sjukhuset");
    u.setLocality("Göteborg");
    u.setHsaMunicipalityCode("1405");
    u.setVgrVardVal(true);
    u.setHsaBusinessClassificationCode(Arrays.asList("1211", "1310"));
    u.addDescription(description);
    u.addInternalDescription(description);
    u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Område 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
    u.setHsaManagementText("Offentlig vårdgivare");
    u.setHsaVisitingRuleAge("0-60");
    u.setHsaVisitingRules("Endast tidsbokade besök");
    u.setShowVisitingRules(true);
    u.setShowAgeInterval(true);
    u.setLabeledURI("http://localhost:8180");
    u.setWgs84Lat(57.6696);
    u.setWgs84Long(12.572);
    u.setMvkCaseTypes(Arrays.asList("yadda"));
    u.addHsaRoute(Arrays.asList("Från riksväg 40, avfart Bollebygd.", "Kör mot Bollebygds centrum och parkera på Gästgivartorget.",
        "Om man går till hörnan vid Systembolaget och står med ryggen mot gamla riksväg 40 ser man Vårdcentralen."));
    this.initHealthcareTypes(u);
    this.initUnitPhoneNumbers(u);
    this.initUnitAddresses(u, "Storgatan 1", "411 01 Göteborg");
    this.initUnitHours(u);
    list.add(u);

    u = new Unit();
    u.setHsaIdentity("SE2321000131-E000000000420");
    u.setName("Uddevalla vårdcentral");
    u.setLocality("Uddevalla");
    u.setHsaMunicipalityCode("1443");
    u.setVgrVardVal(true);
    u.setHsaBusinessClassificationCode(Arrays.asList("1102", "1812"));
    u.addDescription(description);
    u.addInternalDescription(description);
    u.setVgrTempInfo("20090108-20990118 temporary information");
    u.setVgrRefInfo("Hänvisning till baksidan");
    u.setDn(DN.createDNFromString("ou=Akutmottagning,ou=Verksamhet Akutmottagning,ou=Område 2,ou=Sahlgrenska Universitetssjukhuset,ou=Org,o=vgr"));
    u.setHsaManagementText("Offentlig vårdgivare");
    u.setHsaVisitingRuleAge("0-60");
    u.setHsaVisitingRules("Endast tidsbokade besök");
    u.setShowVisitingRules(true);
    u.setShowAgeInterval(true);
    u.setLabeledURI("http://localhost:8180");
    u.setWgs84Lat(57.6696);
    u.setWgs84Long(12.572);
    u.setAccessibilityDatabaseId(123);
    u.setMvkCaseTypes(Arrays.asList("yadda"));
    u.addHsaRoute(Arrays.asList("Från riksväg 40, avfart Bollebygd.", "Kör mot Bollebygds centrum och parkera på Gästgivartorget.",
        "Om man går till hörnan vid Systembolaget och står med ryggen mot gamla riksväg 40 ser man Vårdcentralen."));
    this.initHealthcareTypes(u);
    this.initUnitPhoneNumbers(u);
    this.initUnitAddresses(u, "Storgatan 1", "411 01 Uddevalla");
    this.initUnitHours(u);
    list.add(u);
  }

  private void initHealthcareTypes(Unit u) {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Vårdcentral");
    u.addHealthcareType(healthcareType);
  }

  private void initUnitHours(Unit unit) {
    List<WeekdayTime> hours = WeekdayTime.createWeekdayTimeList(Arrays.asList("1-5#08:00#12:00", "1-5#13:00#16:30"));
    unit.addHsaSurgeryHours(hours);
    unit.addHsaDropInHours(hours);
    unit.addHsaTelephoneTimes(hours);
  }

  private void initUnitAddresses(Unit u, String street, String zipCity) {
    List<String> a;
    a = new ArrayList<String>();
    a.add(street);
    a.add(zipCity);
    u.setHsaStreetAddress(AddressHelper.convertToStreetAddress(a));
  }

  private void initUnitPhoneNumbers(Unit u) {
    List<PhoneNumber> p;
    p = new ArrayList<PhoneNumber>();
    p.add(PhoneNumber.createPhoneNumber("031-123456"));
    p.add(PhoneNumber.createPhoneNumber("031-654321"));
    u.addHsaTelephoneNumber(p);
    u.addHsaPublicTelephoneNumber(p.get(0));
  }

  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
    return this.unitList;
  }

  @Override
  public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException {
    return this.getAllUnitsHsaIdentity();
  }

  @Override
  public List<Employment> getEmploymentsForPerson(Person person) {
    return null;
  }

  @Override
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    this.initPersons(result);
    return result;
  }

  @Override
  public Person getPersonByDn(String personDn) {
    return null;
  }

  @Override
  public byte[] getProfileImageByDn(String dn) throws KivException {
    return null;
  }

  @Override
  public List<Person> getAllPersons() throws KivException {
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    this.initPersons(result);
    return result;
  }

  @Override
  public List<Unit> getAllUnits(boolean onlyPublicUnits) throws KivException {
    return this.unitList;
  }

  @Override
  public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException {
    return null;
  }

  @Override
  public Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId) throws KivException {
    return this.getUnitByHsaId(hsaId);
  }

  @Override
  public List<String> getUnitAdministratorVgrIds(String hsaId) throws KivException {
    return null;
  }
}
