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

package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.context.ExternalContext;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.svc.SearchService;

/**
 * Support bean for displaying person details.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
@SuppressWarnings("serial")
public class DisplayPersonDetailsFlowSupportBean implements Serializable {
  private static final String CLASS_NAME = DisplayPersonDetailsFlowSupportBean.class.getName();
  private static final Log LOGGER = LogFactory.getLog(SearchPersonFlowSupportBean.class);

  private SearchService searchService;

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Retrieves person details for the provided vgrId.
   * 
   * @param vgrId The unique identifier for the person to retrieve details for.
   * @param externalContext The external Faces-context.
   * @return A populated Person-object.
   * @throws KivException if there is a problem retrieving the person from the LDAP directory.
   */
  public Person getPersonDetails(String vgrId, ExternalContext externalContext) throws KivException {
    LOGGER.debug(CLASS_NAME + "::getPersonDetails(vgrId=" + vgrId + ")");
    try {
      Person person = searchService.getPersonById(vgrId);
      if (person.getEmployments() == null) {
        List<Employment> employments = searchService.getEmploymentsForPerson(person);
        person.setEmployments(employments);
      }
      return person;
    } catch (KivNoDataFoundException e) {
      if (externalContext.getNativeResponse() instanceof HttpServletResponse) {
        ((HttpServletResponse) externalContext.getNativeResponse()).setStatus(404);
      }
      throw e;
    }
  }

  /**
   * Retrieves person details for the provided distinguished name.
   * 
   * @param personDn The distinguished name for the person to retrieve details for.
   * @param externalContext The external Faces-context.
   * @return A populated Person-object.
   * @throws KivException if there is a problem retrieving the person from the LDAP directory.
   */
  public Person getPersonDetailsByDn(String personDn, ExternalContext externalContext) throws KivException {
    LOGGER.debug(CLASS_NAME + "::getPersonDetails(personDn=" + personDn + ")");
    try {
      Person person = searchService.getPersonByDn(personDn);
      if (person.getEmployments() == null) {
        List<Employment> employments = searchService.getEmploymentsForPerson(person);
        person.setEmployments(employments);
      }
      return person;
    } catch (KivNoDataFoundException e) {
      if (externalContext.getNativeResponse() instanceof HttpServletResponse) {
        ((HttpServletResponse) externalContext.getNativeResponse()).setStatus(404);
      }
      throw e;
    }
  }
}
