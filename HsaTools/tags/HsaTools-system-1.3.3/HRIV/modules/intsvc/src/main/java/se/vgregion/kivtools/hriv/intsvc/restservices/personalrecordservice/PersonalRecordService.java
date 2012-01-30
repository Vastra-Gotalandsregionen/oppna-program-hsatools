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

package se.vgregion.kivtools.hriv.intsvc.restservices.personalrecordservice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import se.vgregion.kivtools.hriv.intsvc.utils.XmlMarshaller;
import se.vgregion.kivtools.util.StringUtil;

/**
 * REST service for retrieving person information based on user id.
 * 
 * @author David Bennehult & Joakim Olsson
 * 
 */
@Controller
public class PersonalRecordService {

  private LdapTemplate ldapTemplate;

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * Look up user name from vgrid.
   * 
   * @param request http request.
   * @param response http response.
   * @return personal record in xml format.
   * @throws IOException if no writer could be retrived.
   * 
   */
  @RequestMapping("/personalrecord/*")
  public String getPersonalRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userId = getPersonId(request.getPathInfo());
    PersonalRecord personRecord = getPersonRecordFromLdap(userId);
    String generateXmlContentOfObject = XmlMarshaller.generateXmlContentOfObject(personRecord);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(generateXmlContentOfObject);
    return null;
  }

  private PersonalRecord getPersonRecordFromLdap(String userId) {
    PersonalRecord person = null;
    if (!StringUtil.isEmpty(userId)) {
      try {
        person = (PersonalRecord) ldapTemplate.lookup(new LdapRdn("cn", userId).toString(), new PersonalRecordMapper());
      } catch (NameNotFoundException e) {
        // person not found return empty personalRecord object
        person = new PersonalRecord();
      }
    }
    if (person == null) {
      person = new PersonalRecord();
    }
    return person;
  }

  private String getPersonId(String requestPath) {
    return requestPath.substring(requestPath.lastIndexOf("/") + 1);
  }

}
