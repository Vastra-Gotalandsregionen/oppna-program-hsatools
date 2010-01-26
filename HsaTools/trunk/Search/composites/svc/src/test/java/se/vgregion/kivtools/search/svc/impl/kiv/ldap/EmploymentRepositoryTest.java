/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.novell.ldap.LDAPException;

public class EmploymentRepositoryTest {
  private static final DN TEST_DN = DN.createDNFromString("cn=nipet10,ou=Personal,o=VGR");
  private final EmploymentRepository employmentRepository = new EmploymentRepository();
  private final LDAPConnectionMock ldapConnection = new LDAPConnectionMock();
  private final LdapConnectionPoolMock ldapConnectionPool = new LdapConnectionPoolMock(ldapConnection);
  private final CodeTablesServiceImpl codeTablesService = new CodeTablesServiceImpl();
  private final String expectedFilter = "(&(objectclass=vgrAnstallning)(|(!(hsaEndDate=*))(hsaEndDate>=20090101000000Z))(|(hsaStartDate<=20090101000000Z)(!(hsaStartDate=*))))";

  @Before
  public void setUp() {
    employmentRepository.setLdapConnectionPool(ldapConnectionPool);
    employmentRepository.setCodeTablesService(codeTablesService);

    setupTimeSource();
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
        millis = cal.getTimeInMillis();
      }

      @Override
      public long millis() {
        return millis;
      }
    });
  }

  @Test
  public void getEmploymentsReturnEmptyListIfNoEmploymentsAreFound() throws KivException {
    SikSearchResultList<Employment> employments = this.employmentRepository.getEmployments(TEST_DN);
    assertNotNull(employments);
    assertEquals(0, employments.size());
    ldapConnection.assertFilter(expectedFilter);
    ldapConnectionPool.assertCorrectConnectionHandling();
  }

  @Test
  public void getEmploymentsReturnsReconsitutedEmploymentsIfFound() throws KivException {
    LDAPSearchResultsMock ldapSearchResults = new LDAPSearchResultsMock();
    ldapSearchResults.addLDAPEntry(new LDAPEntryMock("cn", "nipet10,ou=Personal,o=VGR"));
    this.ldapConnection.addLDAPSearchResults(expectedFilter, ldapSearchResults);

    SikSearchResultList<Employment> employments = this.employmentRepository.getEmployments(TEST_DN);
    assertNotNull(employments);
    assertEquals(1, employments.size());
    ldapConnection.assertFilter(expectedFilter);
    ldapConnectionPool.assertCorrectConnectionHandling();
  }

  @Test(expected = KivException.class)
  public void getEmploymentsThrowsKivExceptionOnLdapExceptions() throws KivException {
    this.ldapConnection.setLdapException(new LDAPException());

    this.employmentRepository.getEmployments(TEST_DN);
  }

  @Test(expected = KivException.class)
  public void extractResultsAbortsAtLdapTimeoutException() throws KivException {
    LDAPSearchResultsMock ldapSearchResults = new LDAPSearchResultsMock();
    ldapSearchResults.addLDAPEntry(new LDAPEntryMock());
    ldapSearchResults.setLdapException(new LDAPException("message", LDAPException.LDAP_TIMEOUT, "server message"));
    this.ldapConnection.addLDAPSearchResults(expectedFilter, ldapSearchResults);

    // Use getEmployments since that method calls extractResults
    this.employmentRepository.getEmployments(TEST_DN);
  }

  @Test(expected = KivException.class)
  public void extractResultsAbortsAtLdapConnectError() throws KivException {
    LDAPSearchResultsMock ldapSearchResults = new LDAPSearchResultsMock();
    ldapSearchResults.addLDAPEntry(new LDAPEntryMock());
    ldapSearchResults.setLdapException(new LDAPException("message", LDAPException.CONNECT_ERROR, "server message"));
    this.ldapConnection.addLDAPSearchResults(expectedFilter, ldapSearchResults);

    // Use getEmployments since that method calls extractResults
    this.employmentRepository.getEmployments(TEST_DN);
  }

  @Test(expected = SikInternalException.class)
  public void getConnectionThrowsSikInternalExceptionIfConnectionCannotBeRetrieved() throws KivException {
    this.employmentRepository.setLdapConnectionPool(new LdapConnectionPoolMock(null));

    // Use getEmployments since that method calls getConnection
    this.employmentRepository.getEmployments(TEST_DN);
  }
}
