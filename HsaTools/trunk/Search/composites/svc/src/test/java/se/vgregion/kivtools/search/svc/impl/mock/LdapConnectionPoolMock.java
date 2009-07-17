package se.vgregion.kivtools.search.svc.impl.mock;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.LdapConnectionPool;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

public class LdapConnectionPoolMock extends LdapConnectionPool {

  private LDAPConnectionMock connectionMock;

  public LdapConnectionPoolMock(LDAPConnectionMock connectionMock) {
    this.connectionMock = connectionMock;
  }

  @Override
  public synchronized LDAPConnection getConnection() throws LDAPException, NoConnectionToServerException, SikInternalException {
    return connectionMock;
  }
}
