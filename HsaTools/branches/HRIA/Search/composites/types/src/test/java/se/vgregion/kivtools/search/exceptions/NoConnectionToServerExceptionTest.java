package se.vgregion.kivtools.search.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

public class NoConnectionToServerExceptionTest {

  private static final String ERROR_MESSAGE_1 = "This is an error message";
  private static final String ERROR_MESSAGE_2 = "Ingen anslutning till servern. Var god f\u00F6rs\u00F6k senare.";

  @Test
  public void testErrorMessage(){
    assertEquals(ERROR_MESSAGE_1, new NoConnectionToServerException(ERROR_MESSAGE_1).getMessage());
    assertEquals(ERROR_MESSAGE_2, new NoConnectionToServerException().toString());
  }
}
