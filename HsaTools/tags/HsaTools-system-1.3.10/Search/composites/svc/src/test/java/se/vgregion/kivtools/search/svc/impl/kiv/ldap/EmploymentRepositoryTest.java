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

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;
import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class EmploymentRepositoryTest {
  private static final DN TEST_DN = DN.createDNFromString("cn=nipet10,ou=Personal,o=VGR");
  private final EmploymentRepository employmentRepository = new EmploymentRepository();
  private final LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();
  private final CodeTablesServiceImpl codeTablesService = new CodeTablesServiceImpl(this.ldapTemplateMock);
  private final String expectedFilter = "(&(objectclass=vgrAnstallning)(|(!(hsaEndDate=*))(hsaEndDate>=20090101235959Z))(|(hsaStartDate<=20090101000000Z)(!(hsaStartDate=*))))";

  @Before
  public void setUp() {
    this.employmentRepository.setCodeTablesService(this.codeTablesService);
    this.employmentRepository.setLdapTemplate(this.ldapTemplateMock);

    this.setupTimeSource();
  }

  @After
  public void tearDown() {
    TimeUtil.reset();
  }

  private void setupTimeSource() {
    TimeUtil.setTimeSource(new TimeSource() {
      private long millis;

      {
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.millis = cal.getTimeInMillis();
      }

      @Override
      public long millis() {
        return this.millis;
      }
    });
  }

  @Test
  public void getEmploymentsReturnEmptyListIfNoEmploymentsAreFound() throws KivException {
    SikSearchResultList<Employment> employments = this.employmentRepository.getEmployments(TEST_DN);
    assertNotNull(employments);
    assertEquals(0, employments.size());
    this.ldapTemplateMock.assertSearchFilter(this.expectedFilter);
  }

  @Test
  public void getEmploymentsReturnsReconsitutedEmploymentsIfFound() throws KivException {
    DirContextOperationsMock responsibleEditor = new DirContextOperationsMock();
    responsibleEditor.addAttributeValue("hsaIdentity", "user1");
    responsibleEditor.addAttributeValue("cn", "nipet10,ou=Personal,o=VGR");
    this.ldapTemplateMock.addDirContextOperationForSearch(responsibleEditor);
    SikSearchResultList<Employment> employments = this.employmentRepository.getEmployments(TEST_DN);
    this.ldapTemplateMock.assertSearchFilter(this.expectedFilter);
    assertNotNull(employments);
    assertEquals(1, employments.size());
  }
}
