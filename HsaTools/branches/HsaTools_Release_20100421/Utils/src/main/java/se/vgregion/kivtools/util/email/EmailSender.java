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

package se.vgregion.kivtools.util.email;

import java.util.List;

/**
 * Simple helper for sending email.
 * 
 * @author Joakim Olsson
 */
public interface EmailSender {
  /**
   * Sends an email using the provided information.
   * 
   * @param fromAddress The email address to use as from-address.
   * @param recipientAddresses A list of recipient addresses.
   * @param subject The subject of the email to send.
   * @param body The body of the email to send.
   */
  public void sendEmail(String fromAddress, List<String> recipientAddresses, String subject, String body);
}
