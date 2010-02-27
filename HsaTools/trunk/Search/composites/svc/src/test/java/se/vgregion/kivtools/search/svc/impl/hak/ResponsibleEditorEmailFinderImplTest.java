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

package se.vgregion.kivtools.search.svc.impl.hak;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;

public class ResponsibleEditorEmailFinderImplTest {
  private ResponsibleEditorEmailFinderImpl responsibleEditorEmailFinder;
  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    ldapTemplate = new LdapTemplateMock();
    responsibleEditorEmailFinder = new ResponsibleEditorEmailFinderImpl();
    responsibleEditorEmailFinder.setLdapTemplate(ldapTemplate);
  }

  @Test
  public void testInstantiation() {
    ResponsibleEditorEmailFinderImpl responsibleEditorEmailFinderImpl = new ResponsibleEditorEmailFinderImpl();
    assertNotNull(responsibleEditorEmailFinderImpl);
  }

  @Test
  public void testFindResponsibleEditorsNoneFound() {
    List<String> responsibleEditors = responsibleEditorEmailFinder.findResponsibleEditors("ou=Centrumkliniken,ou=Landstinget Halland,o=LTH");
    assertNotNull(responsibleEditors);
    assertEquals(0, responsibleEditors.size());
  }

  @Test
  public void testFindResponsibleEditorsFoundOneAtLowestLevel() {
    DirContextOperationsMock responsibleEditor = new DirContextOperationsMock();
    responsibleEditor.addAttributeValue("member", "cn=abc123,ou=test1,ou=test2");
    this.ldapTemplate.addBoundDN(new DistinguishedName("cn=Uppdateringsansvarig, ou=Centrumkliniken, ou=Landstinget Halland, o=LTH"), responsibleEditor);
    DirContextOperationsMock responsibleEditorEmail = new DirContextOperationsMock();
    responsibleEditorEmail.addAttributeValue("mail", "anders.ask@lthalland.se");
    this.ldapTemplate.addDirContextOperationForSearch(responsibleEditorEmail);
    List<String> responsibleEditors = responsibleEditorEmailFinder.findResponsibleEditors("ou=Centrumkliniken,ou=Landstinget Halland,o=LTH");
    assertNotNull(responsibleEditors);
    assertEquals(1, responsibleEditors.size());
    assertEquals("anders.ask@lthalland.se", responsibleEditors.get(0));
    ldapTemplate.assertSearchFilter("(&(objectClass=hkatPerson)(regionName=abc123))");
  }

  @Test
  public void testFindResponsibleEditorsFoundMultipleAtHigherLevel() {
    DirContextOperationsMock responsibleEditor = new DirContextOperationsMock();
    responsibleEditor.addAttributeValue("member", "cn=abc123,ou=test1,ou=test2$cn=def456,ou=test2");
    this.ldapTemplate.addBoundDN(new DistinguishedName("cn=Uppdateringsansvarig, ou=Landstinget Halland, o=LTH"), responsibleEditor);
    DirContextOperationsMock responsibleEditorEmail1 = new DirContextOperationsMock();
    responsibleEditorEmail1.addAttributeValue("mail", "anders.ask@lthalland.se");
    DirContextOperationsMock responsibleEditorEmail2 = new DirContextOperationsMock();
    responsibleEditorEmail2.addAttributeValue("mail", "beatrice.boll@lthalland.se");
    this.ldapTemplate.addDirContextOperationForSearch(responsibleEditorEmail1);
    this.ldapTemplate.addDirContextOperationForSearch(responsibleEditorEmail2);
    List<String> responsibleEditors = responsibleEditorEmailFinder.findResponsibleEditors("ou=Centrumkliniken,ou=Landstinget Halland,o=LTH");
    assertNotNull(responsibleEditors);
    assertEquals(2, responsibleEditors.size());
    assertTrue(responsibleEditors.contains("anders.ask@lthalland.se"));
    assertTrue(responsibleEditors.contains("beatrice.boll@lthalland.se"));
    ldapTemplate.assertSearchFilter("(&(objectClass=hkatPerson)(|(regionName=abc123)(regionName=def456)))");
  }
}
