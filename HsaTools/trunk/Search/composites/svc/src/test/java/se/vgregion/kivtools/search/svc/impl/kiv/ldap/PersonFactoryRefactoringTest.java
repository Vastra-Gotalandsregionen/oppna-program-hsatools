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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.NameMock;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;

public class PersonFactoryRefactoringTest {
	private static final String DN = "cn=abcd12,ou=Personal,o=vgr";
	private static final String TEST = "Test";
	private LDAPEntryMock ldapEntry;
	private DirContextOperationsMock dirContextOperationsMock;
	private se.vgregion.kivtools.search.svc.impl.kiv.ldap.PersonFactoryRefactoringTest.CodeTablesServiceMock codeTablesServiceMock;

	@Before
	public void setUp() throws Exception {
		codeTablesServiceMock = new CodeTablesServiceMock();
		dirContextOperationsMock = new DirContextOperationsMock();
		dirContextOperationsMock.setDn(new NameMock(DN));
		ldapEntry = new LDAPEntryMock();
		ldapEntry.setDn(DN);
		ldapEntry.addAttribute("cn", TEST);
		ldapEntry.addAttribute("vgr-id", TEST);
		ldapEntry.addAttribute("hsaPersonIdentityNumber", TEST);
		ldapEntry.addAttribute("givenName", TEST);
		ldapEntry.addAttribute("sn", TEST);
		ldapEntry.addAttribute("hsaMiddleName", TEST);
		ldapEntry.addAttribute("initials", TEST);
		ldapEntry.addAttribute("hsaNickName", TEST);
		ldapEntry.addAttribute("fullName", TEST);
		ldapEntry.addAttribute("vgrStrukturPersonDN", TEST);
		ldapEntry.addAttribute("vgrOrgRel", TEST);
		ldapEntry.addAttribute("vgrAnstform", TEST);
		ldapEntry.addAttribute("hsaIdentity", TEST);
		ldapEntry.addAttribute("mail", TEST);
		ldapEntry.addAttribute("hsaSpecialityName", TEST);
		ldapEntry.addAttribute("hsaSpecialityCode", TEST);
		ldapEntry.addAttribute("vgrAO3kod", TEST);
		ldapEntry.addAttribute("vgrAnsvarsnummer", TEST);
		ldapEntry.addAttribute("hsaLanguageKnowledgeCode", TEST);
		ldapEntry.addAttribute("hsaLanguageKnowledgeText", TEST);
		ldapEntry.addAttribute("hsaTitle", TEST);
		ldapEntry.addAttribute("hsaPersonPrescriptionCode", TEST);
		ldapEntry.addAttribute("hsaStartDate", TEST);
		ldapEntry.addAttribute("hsaEndDate", TEST);
		ldapEntry.addAttribute("hsaLanguageKnowledgeCode", TEST);

		dirContextOperationsMock.addAttributeValue("cn", TEST);
		dirContextOperationsMock.addAttributeValue("vgr-id", TEST);
		dirContextOperationsMock.addAttributeValue("hsaPersonIdentityNumber", TEST);
		dirContextOperationsMock.addAttributeValue("givenName", TEST);
		dirContextOperationsMock.addAttributeValue("sn", TEST);
		dirContextOperationsMock.addAttributeValue("hsaMiddleName", TEST);
		dirContextOperationsMock.addAttributeValue("initials", TEST);
		dirContextOperationsMock.addAttributeValue("hsaNickName", TEST);
		dirContextOperationsMock.addAttributeValue("fullName", TEST);
		dirContextOperationsMock.addAttributeValue("vgrStrukturPersonDN", TEST);
		dirContextOperationsMock.addAttributeValue("vgrOrgRel", TEST);
		dirContextOperationsMock.addAttributeValue("vgrAnstform", TEST);
		dirContextOperationsMock.addAttributeValue("hsaIdentity", TEST);
		dirContextOperationsMock.addAttributeValue("mail", TEST);
		dirContextOperationsMock.addAttributeValue("hsaSpecialityName", TEST);
		dirContextOperationsMock.addAttributeValue("hsaSpecialityCode", TEST);
		dirContextOperationsMock.addAttributeValue("vgrAO3kod", TEST);
		dirContextOperationsMock.addAttributeValue("vgrAnsvarsnummer", TEST);
		dirContextOperationsMock.addAttributeValue("hsaLanguageKnowledgeCode", TEST);
		dirContextOperationsMock.addAttributeValue("hsaLanguageKnowledgeText", TEST);
		dirContextOperationsMock.addAttributeValue("hsaTitle", TEST);
		dirContextOperationsMock.addAttributeValue("hsaPersonPrescriptionCode", TEST);
		dirContextOperationsMock.addAttributeValue("hsaStartDate", TEST);
		dirContextOperationsMock.addAttributeValue("hsaEndDate", TEST);
		dirContextOperationsMock.addAttributeValue("hsaLanguageKnowledgeCode", TEST);
	}

	@Test
	public void testPersonMapper() {
		PersonMapper personMapper = new PersonMapper(codeTablesServiceMock);
		Person person = personMapper.mapFromContext(dirContextOperationsMock);
		assertPersonResult(person);
	}

	@Test
	public void testInstantiation() {
		PersonFactory personFactory = new PersonFactory();
		assertNotNull(personFactory);
	}

	@Test
	public void testNullLDAPEntry() {
		Person person = PersonFactory.reconstitute(null, null);
		assertNotNull(person);
		assertNull(person.getDn());
	}

	@Test
	public void testReconstitute() {
		Person person = PersonFactory.reconstitute(ldapEntry, new CodeTablesServiceMock());
		assertPersonResult(person);
	}

	private void assertPersonResult(Person person) {
		assertNotNull(person);
		assertEquals(DN, person.getDn());
		assertEquals(TEST, person.getCn());
		assertEquals(TEST, person.getVgrId());
		assertEquals(TEST, person.getHsaPersonIdentityNumber());
		assertEquals(TEST, person.getGivenName());
		assertEquals(TEST, person.getSn());
		assertEquals(TEST, person.getHsaMiddleName());
		assertEquals(TEST, person.getInitials());
		assertEquals(TEST, person.getHsaNickName());
		assertEquals(TEST, person.getFullName());
		assertEquals("[" + TEST + "]", person.getVgrStrukturPersonDN().toString());
		assertEquals("[" + TEST + "]", person.getVgrOrgRel().toString());
		assertEquals("[" + TEST + "]", person.getVgrAnstform().toString());
		assertEquals(TEST, person.getHsaIdentity());
		assertEquals(TEST, person.getMail());
		assertEquals("[Translated " + TEST + "]", person.getHsaSpecialityName().toString());
		assertEquals("[" + TEST + "]", person.getHsaSpecialityCode().toString());
		assertEquals("[" + TEST + "]", person.getVgrAO3kod().toString());
		assertEquals("[" + TEST + "]", person.getVgrAnsvarsnummer().toString());
		assertEquals("[" + TEST + "]", person.getHsaLanguageKnowledgeCode().toString());
		assertEquals("[Translated " + TEST + "]", person.getHsaLanguageKnowledgeText().toString());
		assertEquals(TEST, person.getHsaTitle());
		assertEquals(TEST, person.getHsaPersonPrescriptionCode());
		assertNotNull(person.getEmploymentPeriod());
	}

	class CodeTablesServiceMock implements CodeTablesService {
		@Override
		public String getValueFromCode(CodeTableName codeTableName, String string) {
			return "Translated " + string;
		}

		@Override
		public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
			return null;
		}

		@Override
		public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
			return null;
		}

		@Override
		public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
			return null;
		}
	}
}
