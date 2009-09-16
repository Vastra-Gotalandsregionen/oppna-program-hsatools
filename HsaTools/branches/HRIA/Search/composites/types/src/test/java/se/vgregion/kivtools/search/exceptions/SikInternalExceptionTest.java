package se.vgregion.kivtools.search.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

public class SikInternalExceptionTest {

  private static final String METHOD_NAME = "method name";
  private static final String ERROR_MESSAGE = "This is an error message";
  private static final String EXPECTED_ERROR_MESSAGE = "se.vgregion.kivtools.search.exceptions.SikInternalException, message=This is an error message, Method=java.lang.Object..method name";

  @Test
  public void testErrorMessage(){
   
    Object obj = new Object();
    assertEquals(EXPECTED_ERROR_MESSAGE, new SikInternalException(obj,METHOD_NAME,ERROR_MESSAGE).getMessage());
  }
}
