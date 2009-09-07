package se.vgregion.kivtools.search.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

public class InvalidFormatExceptionTest {
  
  private static final String A_ERROR_MESSAGE = "A error message";
  
  @Test
  public void testInvalidFormatException(){
    assertEquals(A_ERROR_MESSAGE, new InvalidFormatException(A_ERROR_MESSAGE).getMessage());
  }

}
