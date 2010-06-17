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
 */
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.domain.values.ZipCode;
import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimePoint;

/**
 * ContextMapper for person entries. This mapper collects Person-objects to be able to add their employments.
 */
public class PersonMapper implements ContextMapper {
  private final List<Person> persons = new ArrayList<Person>();
  private final Map<String, Person> regionNameMap = new HashMap<String, Person>();
  private final EmploymentComparator employmentComparator = new EmploymentComparator();

  /**
   * {@inheritDoc}
   */
  @Override
  public Object mapFromContext(Object ctx) {
    Person person;

    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);
    String regionName = context.getString("regionName");

    if (regionNameMap.containsKey(regionName)) {
      person = regionNameMap.get(regionName);
    } else {
      person = new Person();
      person.setEmployments(new ArrayList<Employment>());

      person.setDn(context.getDnString());

      // Common Name, Hela Namnet (e.g. )
      person.setCn(context.getString("cn"));

      // vgr-id samma värde som cn (e.g. rogul999)
      person.setVgrId(regionName);

      // Person-id (e.g. 196712085983)
      person.setHsaPersonIdentityNumber(context.getString("personalIdentityNumber"));

      // tilltalsnamn (e.g. Christina)
      person.setGivenName(context.getString("givenName"));

      // efternamn (e.g. Svensson)
      person.setSn(context.getString("sn"));

      // Mellannamn (e.g. Anna)
      person.setHsaMiddleName(context.getString("middleName"));

      // Smeknamn (e.g. Rolle)
      // Should be multiple values but Person does not support that yet
      person.setHsaNickName(context.getString("nickname"));

      // Fullständigt Namn (e.g. Christina Svensson)
      person.setFullName(context.getString("fullName"));

      // A list of dn's to Units where this person is employed e.g ou=Sandlådan,ou=Org,o=VGR
      person.setVgrStrukturPersonDN(context.getStrings("distinguishedName"));

      // A list of HsaIdentities to the Units where the person is employed e.g. SE2321000131-E000000000101
      person.setVgrOrgRel(context.getStrings("hsaIdentity"));

      // HSA identitet (e.g. SE2321000131-P000000101458)
      person.setHsaIdentity(context.getString("hsaIdentity"));

      // E-postadress (e.g. jessica.isegran@vgregion.se)
      person.setMail(context.getString("mail"));

      // Specialitetskod klartext e.g. Klinisk cytologi , Klinisk patologi
      person.setHsaSpecialityName(context.getStrings("specialityName"));

      // Specialitetskod e.g. 1024 , 1032
      person.setHsaSpecialityCode(context.getStrings("hsaSpecialityCode"));

      // List of Languages that the person speaks e.g. PL, RO
      person.setHsaLanguageKnowledgeCode(context.getStrings("hsaLanguageKnowledgeCode"));

      // List of Languages that the person speaks e.g. Polska, Romanska
      person.setHsaLanguageKnowledgeText(context.getStrings("hsaLanguageKnowledgeText"));

      // Legitimerade Yrkesgrupper e.g Biomedicinsk analytiker
      List<String> titles = context.getStrings("hsaTitle");
      person.setHsaTitle(StringUtil.concatenate(titles));

      // hsaPersonPrescriptionCode
      person.setHsaPersonPrescriptionCode(context.getString("hsaPersonPrescriptionCode"));

      // Anställningsperiod
      String startDateString = context.getString("hsaStartDate");
      String endDateString = context.getString("hsaEndDate");
      TimePoint startDate = TimePoint.parseFrom(startDateString, "", TimeZone.getDefault());
      TimePoint endDate = TimePoint.parseFrom(endDateString, "", TimeZone.getDefault());
      person.setEmploymentPeriod(startDate, endDate);

      if (context.hasAttribute("jpegPhoto")) {
        person.setProfileImagePresent(true);
      }

      regionNameMap.put(person.getVgrId(), person);
      persons.add(person);
    }

    List<Employment> employments = person.getEmployments();
    employments.add(extractEmployment(context));
    Collections.sort(employments, employmentComparator);

    // Always return null since mapper collects all persons
    return null;
  }

  private static Employment extractEmployment(DirContextOperationsHelper context) {
    Employment employment = new Employment();

    employment.setCn(context.getString("cn"));
    employment.setOu(context.getString("ou"));
    employment.setHsaPersonIdentityNumber(context.getString("hsaIdentity"));
    employment.setHsaStreetAddress(AddressHelper.convertToAddress(context.getStrings("street")));
    employment.setHsaInternalAddress(AddressHelper.convertToAddress(context.getStrings("hsaInternalAddress")));
    employment.setHsaPostalAddress(AddressHelper.convertToAddress(context.getStrings("postalAddress")));
    employment.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(context.getStrings("hsaDeliveryAddress")));
    employment.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(context.getStrings("hsaInvoiceAddress")));
    employment.setHsaConsigneeAddress(AddressHelper.convertToAddress(context.getStrings("hsaConsigneeAddress")));
    employment.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("facsimileTelephoneNumber")));
    employment.setLabeledUri(context.getString("labeledUri"));

    employment.setTitle(context.getString("title"));

    employment.setDescription(context.getStrings("description"));
    employment.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(context.getString("hsaSwitchboardNumber")));
    employment.setName(context.getString("company"));
    employment.addHsaTelephoneNumbers(PhoneNumber.createPhoneNumberList(context.getStrings("telephoneNumber")));
    employment.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("hsaTelephoneNumber")));
    employment.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("mobile")));
    employment.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(context.getString("hsaInternalPagerNumber")));
    employment.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("pager")));
    employment.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(context.getString("hsaTextPhoneNumber")));
    if (context.hasAttribute("whenChanged")) {
      employment.setModifyTimestamp(TimePoint.parseFrom(context.getString("whenChanged"), "yyyyMMddHHmmss", TimeZone.getDefault()));
    } else if (context.hasAttribute("whenCreated")) {
      employment.setModifyTimestamp(TimePoint.parseFrom(context.getString("whenCreated"), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }
    // employment.setModifyersName(LdapORMHelper.getSingleValue(personEntry.getAttribute("modifyersName")));
    employment.addHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(context.getStrings("telephoneHours")));
    DN employmentDn = DN.createDNFromString(context.getString("distinguishedName"));
    employment.setDn(employmentDn);
    employment.setVgrStrukturPerson(employmentDn.getUnit());
    employment.setZipCode(new ZipCode(context.getString("postalCode")));
    if ("Ja".equals(context.getString("mainNode"))) {
      employment.setPrimaryEmployment(true);
    }
    return employment;
  }

  /**
   * Retrieves the first person from the mapper-result.
   * 
   * @return the first person from the mapper-result or null if no persons where found.
   */
  public Person getFirstPerson() {
    Person person = null;

    if (persons.size() > 0) {
      person = persons.get(0);
    }

    return person;
  }

  public List<Person> getPersons() {
    return Collections.unmodifiableList(persons);
  }

  /**
   * Comparator which sorts the primary employment first.
   */
  private static class EmploymentComparator implements Comparator<Employment> {
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Employment o1, Employment o2) {
      int result = 0;

      if (o1.isPrimaryEmployment()) {
        result = -1;
      } else if (o2.isPrimaryEmployment()) {
        result = 1;
      }
      return result;
    }
  }
}
