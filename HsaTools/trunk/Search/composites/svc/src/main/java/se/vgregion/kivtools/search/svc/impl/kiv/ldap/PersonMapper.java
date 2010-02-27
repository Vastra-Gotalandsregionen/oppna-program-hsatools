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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimePoint;

public class PersonMapper implements ContextMapper {

  private DirContextOperations dirContext;
  private CodeTablesService codeTablesService;

  public PersonMapper(CodeTablesService codeTablesService) {
    super();
    this.codeTablesService = codeTablesService;
  }

  @Override
  public Person mapFromContext(Object ctx) {

    Person person = new Person();
    dirContext = (DirContextOperations) ctx;
    person.setDn(dirContext.getDn().toString());

    // Common Name, Hela Namnet (e.g. )
    person.setCn(getStringValue("cn"));

    // vgr-id samma v�rde som cn (e.g. rogul999)
    person.setVgrId(getStringValue(LDAPPersonAttributes.USER_ID.toString()));

    // Person-id (e.g. 196712085983)
    person.setHsaPersonIdentityNumber(getStringValue(LDAPPersonAttributes.PERSON_IDENTITY_NUMBER.toString()));

    // tilltalsnamn (e.g. Christina)
    person.setGivenName(getStringValue(LDAPPersonAttributes.GIVEN_NAME.toString()));

    // efternamn (e.g. Svensson)
    person.setSn(getStringValue(LDAPPersonAttributes.SURNAME.toString()));

    // Mellannamn (e.g. Anna)
    person.setHsaMiddleName(getStringValue(LDAPPersonAttributes.MIDDLE_NAME.toString()));

    // Initialer (e.g. K R)
    person.setInitials(getStringValue(LDAPPersonAttributes.INITIALS.toString()));

    // Smeknamn (e.g. Rolle)
    person.setHsaNickName(getStringValue(LDAPPersonAttributes.NICK_NAME.toString()));

    // Fullst�ndigt Namn (e.g. Christina Svensson)
    person.setFullName(getStringValue(LDAPPersonAttributes.FULL_NAME.toString()));

    // A list of dn�s to Units where this person is employed e.g ou=Sandl�dan,ou=Org,o=VGR
    person.setVgrStrukturPersonDN(getListFromArrayAttributes(dirContext.getStringAttributes(LDAPPersonAttributes.STRUCTURE_PERSON_DN.toString())));

    // A list of HsaIdentities to the Units where the person is employed e.g. SE2321000131-E000000000101
    person.setVgrOrgRel(getListFromArrayAttributes(dirContext.getStringAttributes(LDAPPersonAttributes.VGR_ORG_REL.toString())));

    // Anst�llningsform (e.g. 1)
    person.setVgrAnstform(getListFromArrayAttributes(dirContext.getStringAttributes(LDAPPersonAttributes.VGR_ANST_FORM.toString())));

    // HSA identitet (e.g. SE2321000131-P000000101458)
    person.setHsaIdentity(getStringValue(LDAPPersonAttributes.HSA_IDENTITY.toString()));

    // E-postadress (e.g. jessica.isegran@vgregion.se)
    person.setMail(getStringValue(LDAPPersonAttributes.E_MAIL.toString()));

    // Specialitetskod e.g. 1024 , 1032
    List<String> hsaSpecialityCode = getListFromArrayAttributes(dirContext.getStringAttributes(LDAPPersonAttributes.SPECIALITY_AREA_CODE.toString()));
    person.setHsaSpecialityCode(hsaSpecialityCode);

    List<String> hsaSpecialityName = translateCodeTables(hsaSpecialityCode, CodeTableName.HSA_SPECIALITY_CODE, codeTablesService);
    // Specialitetskod klartext e.g. Klinisk cytologi , Klinisk patologi
    person.setHsaSpecialityName(hsaSpecialityName);

    // Ansvarsomr�des kod e.g. 602, 785
    person.setVgrAO3kod(getListFromArrayAttributes(dirContext.getStringAttributes(LDAPPersonAttributes.ADMINISTRATION.toString())));

    // Ansvarsnumer e.g. 1, 2
    person.setVgrAnsvarsnummer(getListFromArrayAttributes(dirContext.getStringAttributes(LDAPPersonAttributes.VGR_ANSVARSNUMMER.toString())));

    // List of Languages that the person speaks e.g. PL, RO
    List<String> hsaLanguageKnowledgeCode = getListFromArrayAttributes(dirContext.getStringAttributes(LDAPPersonAttributes.LANGUAGE_KNOWLEDGE_CODE.toString()));
    person.setHsaLanguageKnowledgeCode(hsaLanguageKnowledgeCode);

    List<String> hsaLanguageKnowledgeText = translateCodeTables(hsaLanguageKnowledgeCode, CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, codeTablesService);
    // List of Languages that the person speaks e.g. Polska, Romanska
    person.setHsaLanguageKnowledgeText(hsaLanguageKnowledgeText);

    // Legitimerade Yrkesgrupper e.g Biomedicinsk analytiker
    person.setHsaTitle(getStringValue(LDAPPersonAttributes.PROFESSION.toString()));

    // hsaPersonPrescriptionCode
    person.setHsaPersonPrescriptionCode(getStringValue(LDAPPersonAttributes.HSA_PERSON_PRESCRIPTION_CODE.toString()));

    // Anst�llningsperiod
    person.setEmploymentPeriod(TimePoint.parseFrom(getStringValue(LDAPPersonAttributes.HSA_START_DATE.toString()), "", TimeZone.getDefault()), TimePoint.parseFrom(
        getStringValue(LDAPPersonAttributes.HSA_END_DATE.toString()), "", TimeZone.getDefault()));

    return person;
  }

  private String getStringValue(String attributeKey) {
    String stringAttribute = dirContext.getStringAttribute(attributeKey);
    if (StringUtil.isEmpty(stringAttribute)) {
      stringAttribute = "";
    }
    return stringAttribute;
  }

  private List<String> getListFromArrayAttributes(String[] arrayAttributes) {
    List<String> stringList = null;
    if (arrayAttributes == null) {
      stringList = new ArrayList<String>();
    } else {
      stringList = Arrays.asList(arrayAttributes);
    }
    return stringList;
  }

  /**
   * Translates codes to a more readable form using the provided CodeTablesService.
   * 
   * @param codes The list of codes to translate.
   * @param codeTable The actual code table to use for translation.
   * @param codeTablesService The code tables service implementation to use.
   */
  private static List<String> translateCodeTables(List<String> codes, CodeTableName codeTable, CodeTablesService codeTablesService) {
    List<String> translations = new ArrayList<String>();
    for (String code : codes) {
      String translation = codeTablesService.getValueFromCode(codeTable, code);
      translations.add(translation);
    }
    return translations;
  }

}
