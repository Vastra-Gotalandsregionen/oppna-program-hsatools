package se.vgregion.kivtools.search.presentation.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UserMessageContainerTest {
  
  private static final String USER_MESSAGE = "userMessage";
  UserMessageContainer userMessageContainer = new UserMessageContainer();
  
  @Before
  public void setup(){
    userMessageContainer.setUserMessage(USER_MESSAGE);
  }
  
  @Test
  public void testGetConsumeUserMessage(){
    assertEquals(USER_MESSAGE, userMessageContainer.getUserMessage());
    assertEquals(USER_MESSAGE,userMessageContainer.getConsumeUserMessage());
    assertEquals("", userMessageContainer.getUserMessage());
  }
  
  @Test
  public void testAddUserMessage(){
    userMessageContainer.setUserMessage(USER_MESSAGE);
    userMessageContainer.addUserMessage(USER_MESSAGE);
    assertEquals(USER_MESSAGE + USER_MESSAGE, userMessageContainer.getConsumeUserMessage());
  }

}
