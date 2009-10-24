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
package se.vgregion.kivtools.search.svc.ldap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.util.StringUtil;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Object pool to handle LDAPConnection usage: getConnection() ... releaseConnection()
 * 
 *         ... at the end release()
 */
public class LdapConnectionPool {
  private static final String CLASS_NAME = LdapConnectionPool.class.getName();
  private static final int NOT_INITIALIZED = -123456;
  private static final int MAX_CONN_DEFAULT = 10;

  private Log logger = LogFactory.getLog(LdapConnectionPool.class);

  // not synchronized since all access already is synchronized
  private List<LDAPConnection> freeConnections = new ArrayList<LDAPConnection>();
  private int checkedOut;
  private int maxConn = NOT_INITIALIZED;

  private final int ldapport = LDAPConnection.DEFAULT_PORT;

  private final int ldapVersion = LDAPConnection.LDAP_V3;
  // "138.233.20.80:389";
  private String ldapHost;
  // "cn=sokso1,ou=Resurs,o=VGR";
  private String loginDN;
  // "6wuz8zab";
  private String password;

  /**
   * Constructs a new LdapConnectionPool.
   */
  public LdapConnectionPool() {
    logger.debug("entering " + CLASS_NAME + "::LdapConnectionPool(), freeConnections=" + freeConnections.size() + ", checkedOut=" + checkedOut);
  }

  protected String getLdapHost() {
    return ldapHost;
  }

  /**
   * Setter for the ldapHost property.
   * 
   * @param ldapHost The new value of the ldapHost property.
   */
  public void setLdapHost(String ldapHost) {
    this.ldapHost = ldapHost;
  }

  protected String getLoginDN() {
    return loginDN;
  }

  /**
   * Setter for the loginDN property.
   * 
   * @param loginDN The new value of the loginDN property.
   */
  public void setLoginDN(String loginDN) {
    this.loginDN = loginDN;
  }

  protected String getPassword() {
    return password;
  }

  /**
   * Setter for the password property.
   * 
   * @param password The new value of the password property.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  protected String getMaxConn() {
    return "" + this.maxConn;
  }

  /**
   * Sets the maximum number of connections to keep in the pool.
   * 
   * @param maxConn The maximum number of connections.
   */
  public void setMaxConn(String maxConn) {
    if (!StringUtil.isInteger(maxConn)) {
      logger.warn("maxConn=" + maxConn + ", should be a number. Is defaulted to " + MAX_CONN_DEFAULT);
      this.maxConn = MAX_CONN_DEFAULT;
    } else {
      this.maxConn = Integer.parseInt(maxConn);
    }
  }

  private void checkInit() throws SikInternalException {
    String methodName = "::checkInit()";
    if (StringUtil.isEmpty(ldapHost)) {
      throw new SikInternalException(this, methodName, "ldapHost is not initialized.");
    }
    if (StringUtil.isEmpty(loginDN)) {
      throw new SikInternalException(this, methodName, "loginDN is not initialized.");
    }
    if (StringUtil.isEmpty(password)) {
      throw new SikInternalException(this, methodName, "password is not initialized.");
    }
    if (maxConn == NOT_INITIALIZED) {
      throw new SikInternalException(this, methodName, "maxConn is not initialized.");
    }

  }

  /**
   * Checks in a connection to the pool. Notify other Threads that may be waiting for a connection.
   * 
   * @param con The connection to check in
   */
  public synchronized void freeConnection(LDAPConnection con) {
    if (con == null) {
      logger.error("Trying to free a null-connection. Fix the code!!!", new NullPointerException());
    } else {
      // Put the connection at the end of the Vector
      freeConnections.add(con);
      checkedOut--;
      notifyAll();
      logger.debug("free connection, free=" + freeConnections.size() + ", checkedOut=" + checkedOut);
    }
  }

  /**
   * Checks out a connection from the pool.
   * 
   * @return An LDAPConnection from the pool.
   * @throws NoConnectionToServerException if it wasn't possible to connect to the server.
   * @throws LDAPException if there is a problem connecting to the LDAP server.
   * @throws SikInternalException if any of the init-parameters are not set.
   */
  public synchronized LDAPConnection getConnection() throws LDAPException, NoConnectionToServerException, SikInternalException {
    LDAPConnection con = internalGetConnection();
    if (con != null) {
      checkedOut++;
    }
    logger.debug("get connection, free=" + freeConnections.size() + ", checkedOut=" + checkedOut);
    return con;
  }

  /**
   * Internal method that performs the actual checkout of connections from the pool and verifying that the connection is alive before returning it to the client. If no free connection is available, a
   * new connection is created unless the max number of connections has been reached. If a free connection has been closed by the database, it's removed from the pool and this method is called again
   * recursively.
   * 
   * @return An LDAPConnection from the pool.
   * @throws NoConnectionToServerException if it wasn't possible to connect to the server.
   * @throws LDAPException if there is a problem connecting to the LDAP server.
   * @throws SikInternalException if any of the init-parameters are not set.
   */
  private LDAPConnection internalGetConnection() throws LDAPException, NoConnectionToServerException, SikInternalException {
    LDAPConnection con = null;
    if (freeConnections.size() > 0 && freeConnections.get(0) != null) {
      // Pick the first LDAPConnection in the Vector
      // to get round-robin usage
      con = freeConnections.remove(0);
      if (!con.isConnectionAlive()) {
        con.disconnect();
        logger.info("Removed bad connection from Pool");
        // Try again recursively
        con = internalGetConnection();
      }
    } else if (maxConn == 0 || checkedOut < maxConn) {
      con = newConnection();
    }
    return con;
  }

  /**
   * Checks out a connection from the pool. If no free connection is available, a new connection is created unless the max number of connections has been reached. If a free connection has been closed
   * by the database, it's removed from the pool and this method is called again recursively.
   * <P>
   * If no connection is available and the max number has been reached, this method waits the specified time for one to be checked in.
   * 
   * @param timeout The timeout value in milliseconds
   * @return An LDAPConnection from the pool.
   * @throws NoConnectionToServerException if it wasn't possible to connect to the server.
   * @throws LDAPException if there is a problem connecting to the LDAP server.
   * @throws SikInternalException if any of the init-parameters are not set.
   */
  public synchronized LDAPConnection getConnection(long timeout) throws LDAPException, NoConnectionToServerException, SikInternalException {

    long startTime = new Date().getTime();
    LDAPConnection con;
    while ((con = internalGetConnection()) == null) {
      try {
        wait(timeout);
      } catch (InterruptedException e) {
        // No specific handling
      }
      if (new Date().getTime() - startTime >= timeout) {
        // Timeout has expired
        return null;
      }
    }
    if (con != null) {
      checkedOut++;
    }
    logger.debug("getconnection(timeout), free=" + freeConnections.size() + ", checkedOut=" + checkedOut);
    return con;
  }

  /**
   * Creates a new connection.
   * 
   * @throws NoConnectionToServerException
   */
  protected LDAPConnection newConnection() throws LDAPException, SikInternalException, NoConnectionToServerException {
    checkInit();
    LDAPConnection lc = new LDAPConnection();
    try {
      // connect to the server
      lc.connect(ldapHost, ldapport);

      // bind to the server
      lc.bind(ldapVersion, loginDN, StringUtil.getBytes(password, "UTF8"));

      logger.debug("create physichal connection");
    } catch (LDAPException e) {
      logger.error("Can't create a new connection for ldapHost=" + ldapHost + ", ldapport=" + ldapport, e);
      if (e.getResultCode() == LDAPException.CONNECT_ERROR) {
        throw new NoConnectionToServerException();
      }
      throw e;
    }
    return lc;
  }
}
