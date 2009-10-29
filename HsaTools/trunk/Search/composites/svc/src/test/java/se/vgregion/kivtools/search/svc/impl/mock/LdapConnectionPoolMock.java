package se.vgregion.kivtools.search.svc.impl.mock;

import static org.junit.Assert.*;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

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
  public synchronized LDAPConnection getConnection() throws LDAPException, NoConnectionToServerException, SikInternalException {
    getConnectionCalls++;
    return connectionMock;
  }

  @Override
  public synchronized LDAPConnection getConnection(long timeout) throws LDAPException, NoConnectionToServerException, SikInternalException {
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
