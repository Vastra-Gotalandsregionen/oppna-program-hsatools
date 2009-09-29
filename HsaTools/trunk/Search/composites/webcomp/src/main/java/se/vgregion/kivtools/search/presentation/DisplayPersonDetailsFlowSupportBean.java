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
package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;

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
   * @return A populated Person-object.
   */
  public Person getPersonDetails(String vgrId) {
    LOGGER.debug(CLASS_NAME + "::getPersonDetails(vgrId=" + vgrId + ")");
    try {
      Person person = searchService.getPersonById(vgrId);
      if (person.getEmployments() == null) {
        List<Employment> employments = searchService.getEmploymentsForPerson(person);
        person.setEmployments(employments);
      }
      return person;
    } catch (Exception e) {
      LOGGER.error(e);
      return new Person();
    }
  }
}
