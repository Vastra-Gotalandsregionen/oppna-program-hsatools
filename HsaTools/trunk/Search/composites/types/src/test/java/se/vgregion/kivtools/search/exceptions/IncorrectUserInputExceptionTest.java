package se.vgregion.kivtools.search.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

public class IncorrectUserInputExceptionTest {
  private static final String ERROR_MESSAGE_1 = "Du m\u00E5ste fylla i minst ett av f\u00E4lten.";
  private static final String ERROR_MESSAGE_2 = "Du m\u00E5ste fylla i minst ett av f\u00E4lten.";

  @Test
  public void testErrorMessage(){
    assertEquals(ERROR_MESSAGE_1, new IncorrectUserInputException(ERROR_MESSAGE_1).getMessage());
    assertEquals(ERROR_MESSAGE_2, new IncorrectUserInputException().getMessage());
  }
}
