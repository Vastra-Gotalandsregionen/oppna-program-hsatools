package se.vgregion.kivtools.hriv.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

public class LogoutTest {

  private static final String URL = "localhost";
  private static final String REQUESTDISPATCHER_URL = "startpage.jsp?startpage=1";
  private MockHttpServletResponse mockHttpServletResponse;
  private MockHttpServletRequest mockHttpServletRequest;
  private MockHttpSession mockHttpSession;
  private Logout logout;
  private LogFactoryMock logFactoryMock;
  private Log log = new LogMock();

  @Before
  public void setup() {
    logFactoryMock = LogFactoryMock.createInstance();
    logFactoryMock.setLog(log);
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
        throw new IOException();
      }
    };
    logout.doGet(mockHttpServletRequest, mockHttpServletResponse);
    assertTrue(mockHttpSession.isInvalid());
    assertNull(mockHttpServletResponse.getRedirectedUrl());
  }

  class LogMock implements Log {

    @Override
    public void debug(Object message) {
    }

    @Override
    public void debug(Object message, Throwable t) {
    }

    @Override
    public void error(Object message) {
      assertNotNull(message);
    }

    @Override
    public void error(Object message, Throwable t) {
    }

    @Override
    public void fatal(Object message) {
    }

    @Override
    public void fatal(Object message, Throwable t) {
    }

    @Override
    public void info(Object message) {
    }

    @Override
    public void info(Object message, Throwable t) {
    }

    @Override
    public boolean isDebugEnabled() {
      return false;
    }

    @Override
    public boolean isErrorEnabled() {
      return false;
    }

    @Override
    public boolean isFatalEnabled() {
      return false;
    }

    @Override
    public boolean isInfoEnabled() {
      return false;
    }

    @Override
    public boolean isTraceEnabled() {
      return false;
    }

    @Override
    public boolean isWarnEnabled() {
      return false;
    }

    @Override
    public void trace(Object message) {
    }

    @Override
    public void trace(Object message, Throwable t) {
    }

    @Override
    public void warn(Object message) {
    }

    @Override
    public void warn(Object message, Throwable t) {
    }
  }
}
