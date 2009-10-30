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

import java.util.List;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.search.svc.ErrorReportingService;
import se.vgregion.kivtools.search.svc.ResponsibleEditorEmailFinder;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.email.EmailSender;

/**
 * Implementation of the ErrorReportingService for LTH.
 * 
 * @author Joakim Olsson
 */
public class ErrorReportingServiceImpl implements ErrorReportingService {
  private EmailSender emailSender;
  private ResponsibleEditorEmailFinder responsibleEditorEmailFinder;

  /**
   * {@inheritDoc}
   */
  @Override
  public void reportError(String dn, String reportText, String detailLink) {
    DistinguishedName affectedNode = new DistinguishedName(dn);
    String nodeName = affectedNode.removeLast().getValue();

    // Find the responsible editors for the unit or person.
    List<String> recipients = responsibleEditorEmailFinder.findResponsibleEditors(dn);

    // Construct email text.
    String bodyText = "Detta e-postmeddelande har skickats till dig eftersom du är uppdateringsansvarig i Hallandskatalogen.\n\n" + "Inrapporterat fel gäller: " + nodeName
        + "\n\nKommentar från användaren:\n" + reportText + "\n\nKlicka här för att justera informationen i Hallandskatalogen om " + nodeName
        + ": <http://hak.lthalland.se/nordicedge/jsp/login.jsp?loginDN=" + StringUtil.base64Encode(dn) + ">\n\n" + "Klicka här för att se kontaktkortet för " + nodeName + ": <" + detailLink + ">";

    String from = "hallandskatalogen@lthalland.se";
    String subject = "Fel i Hallandskatalogen för " + nodeName + ".";

    // Send email.
    emailSender.sendEmail(from, recipients, subject, bodyText);
  }

  public void setEmailSender(EmailSender emailSender) {
    this.emailSender = emailSender;
  }

  public void setResponsibleEditorEmailFinder(ResponsibleEditorEmailFinder responsibleEditorEmailFinder) {
    this.responsibleEditorEmailFinder = responsibleEditorEmailFinder;
  }
}
