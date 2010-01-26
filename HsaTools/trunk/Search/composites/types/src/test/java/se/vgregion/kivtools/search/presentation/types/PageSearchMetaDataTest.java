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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PageSearchMetaDataTest {

  PagedSearchMetaData pagedSearchMetaData = new PagedSearchMetaData();
  private static final int START_INDEX = 0;
  private static final int END_INDEX = 4;
  private static final String MESSAGE = "startIndex=%1d&endIndex=%1d";
  
  @Before
  public void setup(){
    pagedSearchMetaData.setStartIndex(START_INDEX);
    pagedSearchMetaData.setEndIndex(END_INDEX);
  }
  
  @Test
  public void testGetAsLinkMethod(){
    assertEquals(String.format(MESSAGE, START_INDEX, END_INDEX), pagedSearchMetaData.getAsLink());
  }
  
  @Test
  public void testGetMethods(){
    assertEquals(START_INDEX, pagedSearchMetaData.getStartIndex());
    assertEquals(END_INDEX, pagedSearchMetaData.getEndIndex());
  }
}
