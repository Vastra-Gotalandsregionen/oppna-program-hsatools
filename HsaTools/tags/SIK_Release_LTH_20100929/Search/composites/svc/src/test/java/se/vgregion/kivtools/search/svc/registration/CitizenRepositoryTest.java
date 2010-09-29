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

package se.vgregion.kivtools.search.svc.registration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.AttributesMapper;

import se.vgregion.kivtools.search.svc.registration.CitizenRepository.CitizenMapper;

public class CitizenRepositoryTest {

  private CitizenRepository repository;
  private LdapTemplateMock ldapTemplateMock;
  private CitizenMapper citizenMapper;

  @Before
  public void setUp() throws Exception {
    repository = new CitizenRepository();
    ldapTemplateMock = new LdapTemplateMock();
    repository.setLdapTemplate(ldapTemplateMock);
    citizenMapper = repository.new CitizenMapper();
  }

  @Test
  public void testGetCitizenNameFromSsn() {
    this.ldapTemplateMock.setCitizenName("Kalle");

    String result = this.repository.getCitizenNameFromSsn("1234");
    assertEquals("Kalle", result);
  }

  @Test
  public void testCitizenMapper() throws NamingException {
    AttributesMock attributes = new AttributesMock();
    attributes.put(new AttributeMock("cn", "Kalle"));
    attributes.put(new AttributeMock("sn", "Kula"));
    assertEquals("Kalle Kula", citizenMapper.mapFromAttributes(attributes));

    attributes = new AttributesMock();
    attributes.put(new AttributeMock("cn", "Kalle Kula"));
    attributes.put(new AttributeMock("sn", "Kula"));
    assertEquals("Kalle Kula", citizenMapper.mapFromAttributes(attributes));
  }

  class AttributeMock implements Attribute {
    private static final long serialVersionUID = 4183770609309613682L;

    private String value;
    private String id;

    public AttributeMock(String id, String value) {
      this.id = id;
      this.value = value;
    }

    @Override
    public String getID() {
      return this.id;
    }

    @Override
    public Object get() throws NamingException {
      return this.value;
    }

    // Unimplemented methods

    @Override
    public Object clone() {
      return null;
    }

    @Override
    public boolean add(Object attrVal) {
      return false;
    }

    @Override
    public void add(int ix, Object attrVal) {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean contains(Object attrVal) {
      return false;
    }

    @Override
    public Object get(int ix) throws NamingException {
      return null;
    }

    @Override
    public NamingEnumeration<?> getAll() throws NamingException {
      return null;
    }

    @Override
    public DirContext getAttributeDefinition() throws NamingException {
      return null;
    }

    @Override
    public DirContext getAttributeSyntaxDefinition() throws NamingException {
      return null;
    }

    @Override
    public boolean isOrdered() {
      return false;
    }

    @Override
    public boolean remove(Object attrval) {
      return false;
    }

    @Override
    public Object remove(int ix) {
      return null;
    }

    @Override
    public Object set(int ix, Object attrVal) {
      return null;
    }

    @Override
    public int size() {
      return 0;
    }
  }

  class AttributesMock implements Attributes {
    private static final long serialVersionUID = 1620433933434314397L;
    Map<String, Attribute> attributes = new HashMap<String, Attribute>();

    public Attribute put(Attribute attribute) {
      return this.attributes.put(attribute.getID(), attribute);
    }

    @Override
    public Attribute get(String attrID) {
      return this.attributes.get(attrID);
    }

    // Unimplemented methods

    @Override
    public Object clone() {
      return null;
    }

    @Override
    public NamingEnumeration<? extends Attribute> getAll() {
      return null;
    }

    @Override
    public NamingEnumeration<String> getIDs() {
      return null;
    }

    @Override
    public boolean isCaseIgnored() {
      return false;
    }

    @Override
    public Attribute put(String attrID, Object val) {
      return null;
    }

    @Override
    public Attribute remove(String attrID) {
      return null;
    }

    @Override
    public int size() {
      return 0;
    }
  }

  private static class LdapTemplateMock extends se.vgregion.kivtools.mocks.ldap.LdapTemplateMock {
    private String citizenName;

    public void setCitizenName(String citizenName) {
      this.citizenName = citizenName;
    }

    @Override
    public Object lookup(String dn, AttributesMapper mapper) {
      return this.citizenName;
    }
  }
}
