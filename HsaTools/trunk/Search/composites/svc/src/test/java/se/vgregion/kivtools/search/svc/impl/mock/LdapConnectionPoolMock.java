package se.vgregion.kivtools.search.svc.impl.mock;

import java.io.UnsupportedEncodingException;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.LdapConnectionPool;

public class LdapConnectionPoolMock extends LdapConnectionPool {

	private LDAPConnectionMock connectionMock;

	public LdapConnectionPoolMock(LDAPConnectionMock connectionMock) {
		this.connectionMock = connectionMock;
	}

	@Override
	public synchronized LDAPConnection getConnection() throws LDAPException, UnsupportedEncodingException, NoConnectionToServerException, SikInternalException {
		return connectionMock;
	}
}
