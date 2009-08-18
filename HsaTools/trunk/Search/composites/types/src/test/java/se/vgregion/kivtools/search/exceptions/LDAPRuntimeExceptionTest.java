package se.vgregion.kivtools.search.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

public class LDAPRuntimeExceptionTest {

  private static final String ERROR_MESSAGE = "This is an error message";

  @Test
  public void testErrorMessage(){
    assertEquals(ERROR_MESSAGE, new LDAPRuntimeExcepton(ERROR_MESSAGE).getMessage());
  }
}
