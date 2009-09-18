package se.vgregion.kivtools.hriv.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class LogoutTest {

  private static final String URL = "localhost";
  private static final String REQUESTDISPATCHER_URL = "startpage.jsp?startpage=1";
  private MockHttpServletResponse mockHttpServletResponse;
  private MockHttpServletRequest mockHttpServletRequest;
  private Logout logout;
  private boolean throwIoException;
  private boolean throwServletException;
  private LogFactoryMock logFactoryMock;
  private Log log = new LogMock();

  @Before
  public void setup() {
    logFactoryMock = LogFactoryMock.createInstance();
    logFactoryMock.setLog(log);
    throwIoException = false;
    throwServletException = false;
    mockHttpServletResponse = new MockHttpServletResponse();
    mockHttpServletRequest = new MockHttpServletRequest("get", URL) {
      public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {

        return new MockRequestDisp(path);

      }
    };
    logout = new Logout();
  }

  @Test
  public void testDoGetHttpServletRequestHttpServletResponse() {
    logout.doGet(mockHttpServletRequest, mockHttpServletResponse);
  }

  @Test
  public void testIoException() {
    throwIoException = true;
    logout.doGet(mockHttpServletRequest, mockHttpServletResponse);
  }

  @Test
  public void testServletException() {
    throwServletException = true;
    logout.doGet(mockHttpServletRequest, mockHttpServletResponse);
  }

  class MockRequestDisp implements RequestDispatcher {

    public MockRequestDisp(String url) {
      assertEquals(REQUESTDISPATCHER_URL, url);
    }

    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
      if (throwIoException) {
        throw new IOException();
      }
      if (throwServletException) {
        throw new ServletException();
      }

      assertEquals(mockHttpServletRequest, request);
      assertEquals(mockHttpServletResponse, response);
    }

    @Override
    public void include(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
      // TODO Auto-generated method stub

    }

  }

  class LogMock implements Log {

    @Override
    public void debug(Object message) {
      // TODO Auto-generated method stub

    }

    @Override
    public void debug(Object message, Throwable t) {
      // TODO Auto-generated method stub

    }

    @Override
    public void error(Object message) {
      assertNotNull(message);

    }

    @Override
    public void error(Object message, Throwable t) {
      // TODO Auto-generated method stub

    }

    @Override
    public void fatal(Object message) {
      // TODO Auto-generated method stub

    }

    @Override
    public void fatal(Object message, Throwable t) {
      // TODO Auto-generated method stub

    }

    @Override
    public void info(Object message) {
      // TODO Auto-generated method stub

    }

    @Override
    public void info(Object message, Throwable t) {
      // TODO Auto-generated method stub

    }

    @Override
    public boolean isDebugEnabled() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean isErrorEnabled() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean isFatalEnabled() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean isInfoEnabled() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean isTraceEnabled() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean isWarnEnabled() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public void trace(Object message) {
      // TODO Auto-generated method stub

    }

    @Override
    public void trace(Object message, Throwable t) {
      // TODO Auto-generated method stub

    }

    @Override
    public void warn(Object message) {
      // TODO Auto-generated method stub

    }

    @Override
    public void warn(Object message, Throwable t) {
      // TODO Auto-generated method stub

    }

  }
}
