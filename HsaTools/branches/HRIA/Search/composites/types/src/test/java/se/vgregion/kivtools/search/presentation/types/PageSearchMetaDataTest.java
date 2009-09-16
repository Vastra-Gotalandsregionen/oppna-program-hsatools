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
