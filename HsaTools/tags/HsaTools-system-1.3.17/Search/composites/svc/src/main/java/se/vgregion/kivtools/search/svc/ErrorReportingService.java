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

package se.vgregion.kivtools.search.svc;

/**
 * Service for reporting errors in the LDAP directory to the responsible editors for the unit or person.
 * 
 * @author Joakim Olsson
 */
public interface ErrorReportingService {
  /**
   * Report an error for the unit or person with the provided DN. The provided report text is sent as an email to the responsible editors for the unit or person.
   * 
   * @param dn The DN of the unit or person to report an error for.
   * @param reportText The text to send with the error report.
   * @param detailLink A link to the detail-page of the unit or person.
   */
  void reportError(String dn, String reportText, String detailLink);
}
