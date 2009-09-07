package se.vgregion.kivtools.search.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

public class KivNoDataFoundExceptionTest {
  private static final String ERROR_MESSAGE_1 = "This is an error message";
  private static final String ERROR_MESSAGE_2 = "S\u00F6kningen resulterade inte i n\u00E5gra tr\u00E4ffar.";

  @Test
  public void testErrorMessage(){
    assertEquals(ERROR_MESSAGE_1, new KivNoDataFoundException(ERROR_MESSAGE_1).getMessage());
    assertEquals(ERROR_MESSAGE_2, new KivNoDataFoundException().toString());
  }
}
