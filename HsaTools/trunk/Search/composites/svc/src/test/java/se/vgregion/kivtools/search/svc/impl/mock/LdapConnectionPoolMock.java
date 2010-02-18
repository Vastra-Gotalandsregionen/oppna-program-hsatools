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

package se.vgregion.kivtools.search.svc.impl.mock;

import static org.junit.Assert.*;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;

import com.novell.ldap.LDAPConnection;

public class LdapConnectionPoolMock extends LdapConnectionPool {
  private int getConnectionCalls;
  private int freeConnectionCalls;

  private LDAPConnectionMock connectionMock;

  public LdapConnectionPoolMock(LDAPConnectionMock connectionMock) {
    this.connectionMock = connectionMock;
  }

  public void assertCorrectConnectionHandling() {
    assertEquals("A difference was found in the number of calls to getConnection and freeConnection.", getConnectionCalls, freeConnectionCalls);
  }

  @Override
  public synchronized LDAPConnection getConnection() throws KivException {
    getConnectionCalls++;
    return connectionMock;
  }

  @Override
  public synchronized LDAPConnection getConnection(long timeout) throws KivException {
    getConnectionCalls++;
    return connectionMock;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void freeConnection(LDAPConnection con) {
    freeConnectionCalls++;
  }
}
