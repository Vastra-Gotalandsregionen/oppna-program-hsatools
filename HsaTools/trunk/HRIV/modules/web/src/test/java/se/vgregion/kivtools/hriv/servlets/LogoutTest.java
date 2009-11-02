package se.vgregion.kivtools.hriv.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import se.vgregion.kivtools.mocks.LogFactoryMock;

public class LogoutTest {

  private static final String URL = "localhost";
  private static final String REQUESTDISPATCHER_URL = "logout.jsp";
  private static LogFactoryMock logFactoryMock;
  private MockHttpServletResponse mockHttpServletResponse;
  private MockHttpServletRequest mockHttpServletRequest;
  private MockHttpSession mockHttpSession;
  private Logout logout;

  @BeforeClass
  public static void beforeClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Before
  public void setup() {
    mockHttpSession = new MockHttpSession();
    mockHttpServletResponse = new MockHttpServletResponse();
    mockHttpServletRequest = new MockHttpServletRequest("get", URL);
    mockHttpServletRequest.setSession(mockHttpSession);
    logout = new Logout();
  }

  @Test
  public void testDoGetHttpServletRequestHttpServletResponse() {
    logout.doGet(mockHttpServletRequest, mockHttpServletResponse);
    assertEquals(REQUESTDISPATCHER_URL, mockHttpServletResponse.getRedirectedUrl());
    assertTrue(mockHttpSession.isInvalid());
  }

  @Test
  public void testIoException() {
    mockHttpServletResponse = new MockHttpServletResponse() {
      @Override
      public void sendRedirect(String url) throws IOException {
        throw new IOException("Unable to redirect");
      }
    };
    logout.doGet(mockHttpServletRequest, mockHttpServletResponse);
    assertTrue(mockHttpSession.isInvalid());
    assertNull(mockHttpServletResponse.getRedirectedUrl());
    assertEquals("java.io.IOException: Unable to redirect\n", logFactoryMock.getError(true));
  }
}
