package se.vgregion.hsatools.testtools.signicatws.servlets;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import se.vgregion.hsatools.testtools.signicatws.servlets.SignServlet;

public class SignServletTest {

  private SignServlet signServlet = new SignServlet();
  private String SSN = "197702201111";
  private String ARTIFACT = "artifact_43243242";
  private String TARGET_URL = "http://test/";

  @Test
  public void testDoPost() throws IOException, ServletException {
    HttpServletRequest mockHttpServletRequest = createMock(HttpServletRequest.class);
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    expect(mockHttpServletRequest.getParameter("ssn")).andReturn(SSN);
    expect(mockHttpServletRequest.getParameter("documentArtifact")).andReturn(ARTIFACT);
    expect(mockHttpServletRequest.getParameter("target")).andReturn(TARGET_URL);
    replay(mockHttpServletRequest);
    signServlet.doPost(mockHttpServletRequest, mockHttpServletResponse);
    String redirect = mockHttpServletResponse.getRedirectedUrl();
    assertEquals(TARGET_URL, redirect);
  }
}
