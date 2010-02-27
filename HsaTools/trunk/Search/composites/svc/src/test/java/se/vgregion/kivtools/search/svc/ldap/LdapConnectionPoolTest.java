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

package se.vgregion.kivtools.search.svc.ldap;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;

import com.novell.ldap.LDAPConnection;

public class LdapConnectionPoolTest {

  private LdapConnectionPool connectionPool;
  private static LogFactoryMock logFactoryMock;

  @BeforeClass
  public static void setupClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Before
  public void setUp() throws Exception {
    connectionPool = new LdapConnectionPool();
  }

  @Test
  public void testGetConnection() throws KivException {
    assertNull(connectionPool.getConnection());

    connectionPool.setMaxConn(null);
    assertEquals("10", connectionPool.getMaxConn());

    connectionPool.setMaxConn("");
    assertEquals("10", connectionPool.getMaxConn());

    connectionPool.setMaxConn("a");
    assertEquals("10", connectionPool.getMaxConn());

    connectionPool.setMaxConn("0");
    assertEquals("0", connectionPool.getMaxConn());

    try {
      connectionPool.getConnection();
      fail("SikInternalException expected");
    } catch (SikInternalException e) {
      // Expected exception
    }

    connectionPool.setLdapHost("");
    try {
      connectionPool.getConnection();
      fail("SikInternalException expected");
    } catch (SikInternalException e) {
      // Expected exception
    }

    connectionPool.setLdapHost("localhost");
    try {
      connectionPool.getConnection();
      fail("SikInternalException expected");
    } catch (SikInternalException e) {
      // Expected exception
    }

    connectionPool.setLoginDN("DN=a");
    try {
      connectionPool.getConnection();
      fail("SikInternalException expected");
    } catch (SikInternalException e) {
      // Expected exception
    }

    connectionPool.setPassword("password");
    try {
      connectionPool.getConnection();
      fail("SikInternalException expected");
    } catch (NoConnectionToServerException e) {
      assertEquals("Can't create a new connection for ldapHost=localhost, ldapport=389\n", logFactoryMock.getError(true));
    }
  }

  @Test
  public void testGetConnectionWithTimeout() throws Exception {
    connectionPool = new LdapConnectionPoolMock();
    connectionPool.setMaxConn("1");

    LDAPConnection connection = connectionPool.getConnection(1000);
    assertNotNull(connection);
    // LDAPConnection connection2 = connectionPool.getConnection(1000);
    // assertNull(connection2);
  }

  @Test
  public void testFreeConnectionNullValues() throws Exception {
    connectionPool = new LdapConnectionPoolMock();
    connectionPool.setMaxConn("1");

    LDAPConnection connection = connectionPool.getConnection();
    assertNotNull(connection);
    connectionPool.freeConnection(null);
    assertNull(connectionPool.getConnection());
    connectionPool.freeConnection(connection);
    assertNotNull(connectionPool.getConnection());
  }

  private static class LdapConnectionPoolMock extends LdapConnectionPool {
    @Override
    protected LDAPConnection newConnection() throws KivException {
      return new LDAPConnection();
    }
  }
}
