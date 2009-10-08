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
package se.vgregion.kivtools.search.util;

import static java.lang.System.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.util.StringUtil;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;

/**
 * Explore the available address information in the catalog. Helpful when the address data is a mess and you need to know what could be parsed.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class AddressExplorer {

  static final String KIV_SEARCH_BASE = "ou=Org,o=vgr";
  static final int POOL_WAIT_TIME_MILLISECONDS = 2000;
  private SearchService searchService;
  private LdapConnectionPool theConnectionPool = null;

  public LdapConnectionPool getTheConnectionPool() {
    return theConnectionPool;
  }

  public void setTheConnectionPool(LdapConnectionPool theConnectionPool) {
    this.theConnectionPool = theConnectionPool;
  }

  public SearchService getSearchService() {
    return searchService;
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public static void main(String[] args) throws Exception {
    /*
     * Kick start our own bean factory. Reuse ordinary connection settings etc from HRIV.
     */
    ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "services-config.xml" });
    BeanFactory factory = appContext;

    AddressExplorer ae = new AddressExplorer();
    ae.setSearchService((SearchService) factory.getBean("Search_SearchService"));
    ae.setTheConnectionPool((LdapConnectionPool) factory.getBean("Search_LdapConnectionPool"));
    ae.printStreetAddressReport();
  }

  private void printStreetAddressReport() throws Exception {
    List<Unit> units = getAllUnits();

    /* Print all units and their address info from the HRIV perspective */
    int invalidUnits = 0;
    for (Unit u : units) {
      out.print(u.getHsaIdentity() + ": ");
      if (!StringUtil.isEmpty(u.getHsaStreetAddress().getStreet()) && !StringUtil.isEmpty(u.getHsaStreetAddress().getCity())) {
        out.print("OK! ");
        out.println(u.getHsaStreetAddress().getStreet().trim() + ", " + u.getHsaStreetAddress().getZipCode() + " " + u.getHsaStreetAddress().getCity() + "\n");
      } else {
        out.print("INVALID! ");
        invalidUnits++;

        /*
         * Check it out directly by looking in the LDAP directory ourselves!
         */
        String searchFilter = "(hsaIdentity=" + u.getHsaIdentity() + ")";
        LDAPEntry entry = getEntry(searchFilter);
        out.println("hsaStreetAddress in catalog: " + entry.getAttribute("hsaStreetAddress") + "\n");
      }
    }

    if (invalidUnits != 0) {
      int percentInvalid = (int) Math.round((double) invalidUnits / (double) units.size() * 100);
      out.println("\n Bad ratio: " + percentInvalid + "% (" + invalidUnits + " of " + units.size() + ").");
    } else {
      out.println("\n All street addresses OK!");
    }
  }

  private List<Unit> getAllUnits() throws Exception {
    List<String> allHsaIds = searchService.getAllUnitsHsaIdentity();
    List<Unit> units = new ArrayList<Unit>();
    for (String hsaId : allHsaIds) {
      units.add(searchService.getUnitByHsaId(hsaId));
    }
    return units;
  }

  private LDAPEntry getEntry(String searchFilter) throws Exception {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    String[] attributes = { LDAPConnection.ALL_USER_ATTRS, "createTimeStamp" }; // Get all attributes, including
    // operational attribute createTimeStamp

    LDAPConnection lc = getLDAPConnection();
    try {
      return lc.search(KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, searchFilter, attributes, false, constraints).next();
    } finally {
      theConnectionPool.freeConnection(lc);
    }
  }

  private LDAPConnection getLDAPConnection() throws LDAPException, UnsupportedEncodingException, SikInternalException, NoConnectionToServerException {
    LDAPConnection lc = theConnectionPool.getConnection(POOL_WAIT_TIME_MILLISECONDS);
    if (lc == null) {
      throw new SikInternalException(this, "getLDAPConnection()", "Could not get a connection after waiting " + POOL_WAIT_TIME_MILLISECONDS + " ms.");
    }
    return lc;
  }
}
