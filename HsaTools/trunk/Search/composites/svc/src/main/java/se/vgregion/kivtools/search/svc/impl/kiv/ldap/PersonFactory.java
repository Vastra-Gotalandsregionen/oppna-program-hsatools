/**
 * Copyright 2009 Västa Götalandsregionen
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
/**
 * 
 */
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.TimeZone;

import se.vgregion.kivtools.search.svc.domain.Person;

import com.domainlanguage.time.TimePoint;
import com.novell.ldap.LDAPEntry;

/**
 * @author Anders Asplund - KnowIT
 *
 */
public class PersonFactory {

    /**
     * Reconstitutes a person object from an LDAPEntry
     * 
     * @param personEntry
     * @return Person
     */
    public static Person reconstitute(LDAPEntry personEntry) {
        Person person = new Person();

        if(personEntry == null) {
            return person;
        }
        
        person.setDn(personEntry.getDN());
        
        // Common Name, Hela Namnet (e.g. )
        person.setCn(LdapORMHelper.getSingleValue(personEntry.getAttribute("cn")));

        // vgr-id samma v�rde som cn (e.g. rogul999)
        person.setVgrId(LdapORMHelper.getSingleValue(personEntry.getAttribute("vgr-id")));

        // Person-id (e.g. 196712085983)
        person.setHsaPersonIdentityNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaPersonIdentityNumber")));
        
        // tilltalsnamn (e.g. Christina)
        person.setGivenName(LdapORMHelper.getSingleValue(personEntry.getAttribute("givenName")));

        // efternamn (e.g. Svensson)
        person.setSn(LdapORMHelper.getSingleValue(personEntry.getAttribute("sn")));

        // Mellannamn (e.g. Anna)
        person.setHsaMiddleName(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaMiddleName")));

        // Initialer (e.g. K R)
        person.setInitials(LdapORMHelper.getSingleValue(personEntry.getAttribute("initials")));

        // Smeknamn (e.g. Rolle)
        person.setHsaNickName(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaNickName")));

        // Fullst�ndigt Namn (e.g. Christina Svensson)
        person.setFullName(LdapORMHelper.getSingleValue(personEntry.getAttribute("fullName")));

        // A list of dn�s to Units where this person is employed e.g ou=Sandl�dan,ou=Org,o=VGR
        person.setVgrStrukturPersonDN(LdapORMHelper.getMultipleValues(personEntry.getAttribute("vgrStrukturPersonDN")));

        // A list of HsaIdentities to the Units where the person is employed e.g. SE2321000131-E000000000101
        person.setVgrOrgRel(LdapORMHelper.getMultipleValues(personEntry.getAttribute("vgrOrgRel")));

        // Anst�llningsform (e.g. 1)
        person.setVgrAnstform(LdapORMHelper.getMultipleValues(personEntry.getAttribute("vgrAnstform")));

        // HSA identitet (e.g. SE2321000131-P000000101458)
        person.setHsaIdentity(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaIdentity")));

        // E-postadress (e.g. jessica.isegran@vgregion.se)
        person.setMail(LdapORMHelper.getSingleValue(personEntry.getAttribute("mail")));

        // Specialitetskod klartext e.g. Klinisk cytologi , Klinisk patologi
        person.setHsaSpecialityName(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaSpecialityName")));

        // Specialitetskod e.g. 1024 , 1032
        person.setHsaSpecialityCode(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaSpecialityCode")));

        // Ansvarsomr�des kod e.g. 602, 785
        person.setVgrAO3kod(LdapORMHelper.getMultipleValues(personEntry.getAttribute("vgrAO3kod")));

        // Ansvarsnumer e.g. 1, 2 
        person.setVgrAnsvarsnummer(LdapORMHelper.getMultipleValues(personEntry.getAttribute("vgrAnsvarsnummer")));

        // List of Languages that the person speaks e.g. PL, RO
        person.setHsaLanguageKnowledgeCode(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaLanguageKnowledgeCode")));

        // List of Languages that the person speaks e.g. Polska, Romanska
        person.setHsaLanguageKnowledgeText(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaLanguageKnowledgeText")));

        // Legitimerade Yrkesgrupper e.g Biomedicinsk analytiker
        person.setHsaTitle(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaTitle")));
        
        // hsaPersonPrescriptionCode
        person.setHsaPersonPrescriptionCode(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaPersonPrescriptionCode")));

        //Anst�llningsperiod
        person.setEmploymentPeriod(
                TimePoint.parseFrom(
                        LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaStartDate")),
                        ""/*TODO Add pattern*/,
                        TimeZone.getDefault()
                ),
                TimePoint.parseFrom(
                        LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaEndDate")),
                        ""/*TODO Add pattern*/,
                        TimeZone.getDefault()
                )
        );
                
        return person;
    }
}
