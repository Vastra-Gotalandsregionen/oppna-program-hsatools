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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of EmailSender that just logs the email to the console instead of sending it.
 * 
 * @author Joakim Olsson
 */
public class EmailLoggerImpl implements EmailSender {
  private Log log = LogFactory.getLog(this.getClass());

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendEmail(String fromAddress, List<String> recipientAddresses, String subject, String body) {
    log.info("Email to send:");
    log.info("From: " + fromAddress);
    log.info("To: " + recipientAddresses.toString());
    log.info("Subject: " + subject);
    log.info("Body: " + body);
  }
}
