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

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.util.time.TimeUtil;
import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * @author Anders Asplund - KnowIT
 */
public class EmploymentRepository {
  private static final int POOL_WAIT_TIME_MILLISECONDS = 2000;
  // Get LDAP entries that have hsaEndDate greater or equal current date and hsaStartDate less or equal current date.
  private static final String ALL_EMPLOYMENT_FILTER = "(&(objectclass=vgrAnstallning)(|(!(hsaEndDate=*))(hsaEndDate>=%1$s))(|(hsaStartDate<=%1$s)(!(hsaStartDate=*))))";

  private LdapConnectionPool theConnectionPool;
  private CodeTablesService codeTablesService;

  public void setLdapConnectionPool(LdapConnectionPool lp) {
    this.theConnectionPool = lp;
  }

  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  /**
   * 
   * @param dn Dn of the employments.
   * @return A list of employments.
   * @throws KivException If something goes wrong.
   */
  public SikSearchResultList<Employment> getEmployments(DN dn) throws KivException {
    LDAPSearchResults searchResults = null;
    SikSearchResultList<Employment> result = new SikSearchResultList<Employment>();
    int maxResult = 0;

    try {
      LDAPConnection lc = getLDAPConnection();
      try {
        LDAPSearchConstraints constraints = new LDAPSearchConstraints();
        constraints.setMaxResults(0);

        searchResults = lc.search(dn.toString(), LDAPConnection.SCOPE_ONE, generateLDAPFilter(), null, false, constraints);
        result = extractResult(searchResults, maxResult);

      } finally {
        theConnectionPool.freeConnection(lc);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }

    return result;

  }

  private SikSearchResultList<Employment> extractResult(LDAPSearchResults searchResults, int maxResult) throws LDAPException {
    SikSearchResultList<Employment> result = new SikSearchResultList<Employment>();
    int count = 0;
    while (searchResults.hasMore() && (++count < maxResult || maxResult == 0)) {
      try {
        result.add(EmploymentFactory.reconstitute(searchResults.next(), codeTablesService));
      } catch (LDAPException e) {
        if (e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR) {
          throw e;
        } else {
          continue;
        }
      }
    }
    return result;
  }

  private LDAPConnection getLDAPConnection() throws KivException {
    LDAPConnection lc = theConnectionPool.getConnection(POOL_WAIT_TIME_MILLISECONDS);
    if (lc == null) {
      throw new SikInternalException(this, "getLDAPConnection()", "Could not get a connection after waiting " + POOL_WAIT_TIME_MILLISECONDS + " ms.");
    }
    return lc;
  }

  /**
   * Create LDAP filter string with a condition that hsaEndDate must be greater or equal current date.
   */
  private String generateLDAPFilter() {
    String zuluTime = TimeUtil.getCurrentTimeFormatted(DateTimeFormat.ZULU_TIME);
    String filterString = String.format(ALL_EMPLOYMENT_FILTER, zuluTime);
    return filterString;
  }
}
