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
import java.util.List;
import java.util.TimeZone;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimePoint;

/**
 * Context mapper for person entries.
 */
public class PersonMapper implements ContextMapper {
  private DirContextOperationsHelper dirContext;
  private CodeTablesService codeTablesService;

  /**
   * Constructs a new PersonMapper.
   * 
   * @param codeTablesService The CodeTablesService to use.
   */
  public PersonMapper(CodeTablesService codeTablesService) {
    super();
    this.codeTablesService = codeTablesService;
  }

  @Override
  public Person mapFromContext(Object ctx) {

    Person person = new Person();
    dirContext = new DirContextOperationsHelper((DirContextOperations) ctx);
    person.setDn(dirContext.getDnString());

    // Common Name, Hela Namnet (e.g. )
    person.setCn(dirContext.getString("cn"));

    // vgr-id samma v�rde som cn (e.g. rogul999)
    person.setVgrId(dirContext.getString(LDAPPersonAttributes.USER_ID.toString()));

    // Person-id (e.g. 196712085983)
    person.setHsaPersonIdentityNumber(dirContext.getString(LDAPPersonAttributes.PERSON_IDENTITY_NUMBER.toString()));

    // tilltalsnamn (e.g. Christina)
    person.setGivenName(dirContext.getString(LDAPPersonAttributes.GIVEN_NAME.toString()));

    // efternamn (e.g. Svensson)
    person.setSn(dirContext.getString(LDAPPersonAttributes.SURNAME.toString()));

    // Mellannamn (e.g. Anna)
    person.setHsaMiddleName(dirContext.getString(LDAPPersonAttributes.MIDDLE_NAME.toString()));

    // Initialer (e.g. K R)
    person.setInitials(dirContext.getString(LDAPPersonAttributes.INITIALS.toString()));

    // Smeknamn (e.g. Rolle)
    person.setHsaNickName(dirContext.getString(LDAPPersonAttributes.NICK_NAME.toString()));

    // Fullst�ndigt Namn (e.g. Christina Svensson)
    person.setFullName(dirContext.getString(LDAPPersonAttributes.FULL_NAME.toString()));

    // A list of dn�s to Units where this person is employed e.g ou=Sandl�dan,ou=Org,o=VGR
    person.setVgrStrukturPersonDN(dirContext.getStrings(LDAPPersonAttributes.STRUCTURE_PERSON_DN.toString()));

    // A list of HsaIdentities to the Units where the person is employed e.g. SE2321000131-E000000000101
    person.setVgrOrgRel(dirContext.getStrings(LDAPPersonAttributes.VGR_ORG_REL.toString()));

    // Anst�llningsform (e.g. 1)
    person.setVgrAnstform(dirContext.getStrings(LDAPPersonAttributes.VGR_ANST_FORM.toString()));

    // HSA identitet (e.g. SE2321000131-P000000101458)
    person.setHsaIdentity(dirContext.getString(LDAPPersonAttributes.HSA_IDENTITY.toString()));

    // E-postadress (e.g. jessica.isegran@vgregion.se)
    person.setMail(dirContext.getString(LDAPPersonAttributes.E_MAIL.toString()));

    // Specialitetskod e.g. 1024 , 1032
    List<String> hsaSpecialityCode = dirContext.getStrings(LDAPPersonAttributes.SPECIALITY_AREA_CODE.toString());
    person.setHsaSpecialityCode(hsaSpecialityCode);

    List<String> hsaSpecialityName = translateCodeTables(hsaSpecialityCode, CodeTableName.HSA_SPECIALITY_CODE, codeTablesService);
    // Specialitetskod klartext e.g. Klinisk cytologi , Klinisk patologi
    person.setHsaSpecialityName(hsaSpecialityName);

    // Ansvarsomr�des kod e.g. 602, 785
    person.setVgrAO3kod(dirContext.getStrings(LDAPPersonAttributes.ADMINISTRATION.toString()));

    // Ansvarsnumer e.g. 1, 2
    person.setVgrAnsvarsnummer(dirContext.getStrings(LDAPPersonAttributes.VGR_ANSVARSNUMMER.toString()));

    // List of Languages that the person speaks e.g. PL, RO
    List<String> hsaLanguageKnowledgeCode = dirContext.getStrings(LDAPPersonAttributes.LANGUAGE_KNOWLEDGE_CODE.toString());
    person.setHsaLanguageKnowledgeCode(hsaLanguageKnowledgeCode);

    List<String> hsaLanguageKnowledgeText = translateCodeTables(hsaLanguageKnowledgeCode, CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, codeTablesService);
    // List of Languages that the person speaks e.g. Polska, Romanska
    person.setHsaLanguageKnowledgeText(hsaLanguageKnowledgeText);

    // Legitimerade Yrkesgrupper e.g Biomedicinsk analytiker
    person.setHsaTitle(dirContext.getString(LDAPPersonAttributes.PROFESSION.toString()));

    // hsaPersonPrescriptionCode
    person.setHsaPersonPrescriptionCode(dirContext.getString(LDAPPersonAttributes.HSA_PERSON_PRESCRIPTION_CODE.toString()));

    // Anst�llningsperiod
    person.setEmploymentPeriod(parseDateTime(dirContext.getString(LDAPPersonAttributes.HSA_START_DATE.toString())), parseDateTime(dirContext.getString(LDAPPersonAttributes.HSA_END_DATE.toString())));

    person.setCreateTimestamp(parseDateTime(dirContext.getString(LDAPPersonAttributes.CREATE_TIMESTAMP.toString())));
    person.setModifyTimestamp(parseDateTime(dirContext.getString(LDAPPersonAttributes.MODIFY_TIMESTAMP.toString())));

    person.setVgrAdminTypes(dirContext.getStrings(LDAPPersonAttributes.VGR_ADMIN_TYPE.toString()));

    person.setHsaManagerCode(dirContext.getString(LDAPPersonAttributes.HSA_MANAGER_CODE.toString()));

    return person;
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

  private TimePoint parseDateTime(final String dateTime) {
    TimePoint result = null;

    if (!StringUtil.isEmpty(dateTime)) {
      result = TimePoint.parseFrom(dateTime, "yyyyMMddHHmmss'Z'", TimeZone.getDefault());
    }

    return result;
  }
}
