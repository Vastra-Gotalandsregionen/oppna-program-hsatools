package se.vgregion.kivtools.search.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

public class KivExceptionTest {

  private static final String ERROR_MESSAGE = "This is an error message";

  @Test
  public void testErrorMessage(){
    assertEquals(ERROR_MESSAGE, new KivException(ERROR_MESSAGE).getMessage());
    assertEquals(ERROR_MESSAGE, new KivException(ERROR_MESSAGE).toString());
  }
}
