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
/**
 * 
 */
package se.vgregion.kivtools.search.presentation.forms;

import java.io.Serializable;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 */
public class UserMessageContainer implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String userMessage = "";

  public String getUserMessage() {
    return userMessage;
  }

  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  /**
   * clear userMessage to "".
   */
  public void clear() {
    userMessage = "";
  }

  /**
   * 
   * @return userMessage and clear it after returning.
   */
  public String getConsumeUserMessage() {
    String s = userMessage;
    clear();
    return s;
  }

  /**
   * Append userMessage param to current userMessage in UserMessageContainer.
   * 
   * @param message to append.
   */
  public void addUserMessage(String message) {
    this.userMessage += message;
  }

}
