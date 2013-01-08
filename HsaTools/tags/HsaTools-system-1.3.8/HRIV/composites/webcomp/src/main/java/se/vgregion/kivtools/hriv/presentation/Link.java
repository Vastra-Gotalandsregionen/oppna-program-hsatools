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

package se.vgregion.kivtools.hriv.presentation;

import java.io.Serializable;

/**
 * Hyperlink representation.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class Link implements Serializable {
  private static final long serialVersionUID = 1252630862103552549L;
  private final String href;
  private final String name;
  private final String toParamName;
  private final String fromParamName;

  /**
   * Constructs a new Link object.
   * 
   * @param href The hyperlink reference to use.
   * @param name The name of the link.
   * @param toParamName The to-parameter name of the link.
   * @param fromParamName The from-parameter name of the link.
   */
  public Link(String href, String name, String toParamName, String fromParamName) {
    super();
    this.href = href;
    this.name = name;
    this.toParamName = toParamName;
    this.fromParamName = fromParamName;
  }

  public String getToParamName() {
    return toParamName;
  }

  public String getHref() {
    return href;
  }

  public String getName() {
    return name;
  }

  public String getFromParamName() {
    return fromParamName;
  }
}
