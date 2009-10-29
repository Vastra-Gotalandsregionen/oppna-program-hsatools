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
package se.vgregion.kivtools.search.svc.impl.hak;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.svc.ResponsibleEditorEmailFinder;
import se.vgregion.kivtools.util.Arguments;

/**
 * Implementation of the ResponsibleEditorEmailFinder interface for LTH. Searches for CN=Uppdateringsansvarig from the provided DN and as long as no node is found, the tree is traversed towards the
 * root.
 * 
 * @author Joakim Olsson
 */
public class ResponsibleEditorEmailFinderImpl implements ResponsibleEditorEmailFinder {
  private LdapTemplate ldapTemplate;
  private Log log = LogFactory.getLog(this.getClass());

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<String> findResponsibleEditors(String dn) {
    Arguments.notEmpty("dn", dn);

    List<String> responsibleEditors = new ArrayList<String>();
    DistinguishedName distinguishedName = new DistinguishedName(dn);
    while (responsibleEditors.size() == 0 && !distinguishedName.isEmpty()) {
      // Suppressing since the ResponsibleEditorMapper returns a list of strings.
      List<String> editors = null;
      try {
        DistinguishedName lookupName = new DistinguishedName(distinguishedName).append("CN", "Uppdateringsansvarig");
        editors = (List<String>) ldapTemplate.lookup(lookupName, new ResponsibleEditorMapper());
      } catch (NameNotFoundException e) {
        log.debug("Could not find entry in LDAP directory, trying next level");
      }
      if (editors != null) {
        responsibleEditors.addAll(editors);
      }
      distinguishedName.removeLast();
    }

    List<String> responsibleEditorEmails = new ArrayList<String>();
    if (responsibleEditors.size() > 0) {
      // Retrieve email addresses for all found editors.
      Filter searchFilter = createSearchFilter(responsibleEditors);
      List<String> emailAddresses = ldapTemplate.search("ou=Landstinget Halland,dc=lthallandhsa,dc=se", searchFilter.encode(), new PersonEmailMapper());
      responsibleEditorEmails.addAll(emailAddresses);
    }
    return responsibleEditorEmails;
  }

  /**
   * Creates a filter to use when finding the email addresses of all responsible editors.
   * 
   * @param responsibleEditors The list of responsible editors userids.
   * @return A populated filter using the provided editors userids.
   */
  private Filter createSearchFilter(List<String> responsibleEditors) {
    AndFilter filter = new AndFilter();
    filter.and(new EqualsFilter("objectClass", "hkatPerson"));
    OrFilter regionNameFilter = new OrFilter();
    for (String responsibleEditor : responsibleEditors) {
      regionNameFilter.or(new EqualsFilter("regionName", responsibleEditor));
    }
    filter.and(regionNameFilter);

    return filter;
  }

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }
}
