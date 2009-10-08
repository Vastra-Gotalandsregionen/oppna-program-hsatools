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
package se.vgregion.kivtools.search.presentation.types;

import java.io.Serializable;

/**
 * POJO describing meta data for a specific page in a paged search.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class PagedSearchMetaData implements Serializable {
  private static final long serialVersionUID = 7591443288898242356L;
  private int startIndex;
  private int endIndex;

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public void setEndIndex(int endIndex) {
    this.endIndex = endIndex;
  }

  /**
   * Gets the pages meta data as HTML link parameters.
   * 
   * @return The pages meta data as HTML link parameters.
   */
  public String getAsLink() {
    StringBuffer buf = new StringBuffer("startIndex=");
    buf.append(startIndex);
    buf.append("&endIndex=");
    buf.append(endIndex);
    return buf.toString();
  }
}
