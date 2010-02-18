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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro;
/**
 * 
 * @author David Bennehult
 *
 */
/**
 * Describes the methods an FtpClient needs to implement.
 * 
 * @author David Bennehult & Jonas Liljenfeldt.
 */
public interface FtpClient {
  /**
   * Send file to source.
   * @param fileContent content to send.
   * @return true if operation was successful.
   */
  /**
   * Sends the provided file content to the configured server.
   * 
   * @param fileContent The file content to send.
   * @return True if sending was successful, otherwise false.
   */
  boolean sendFile(String fileContent);
}
