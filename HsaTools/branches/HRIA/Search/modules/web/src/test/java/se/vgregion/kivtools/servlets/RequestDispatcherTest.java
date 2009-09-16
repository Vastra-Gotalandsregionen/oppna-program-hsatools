package se.vgregion.kivtools.servlets;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class RequestDispatcherTest {

  RequestDispatcher requestDispatcher = new RequestDispatcher();
  private static final String VISA_PERSON_URL = "displayPersonDetails.jsf?_flowId=Search.searchperson-flow";
  private static final String VISA_ENHET_URL =  "displayUnitDetails.jsf?_flowId=Search.searchunit-flow";
  private static final String VISA_ENHET_DN_URL = "displayUnitDetails.jsf?_flowId=Search.searchunit-flow";
  private static final String VISA_ORGANISATION_URL = "displayPersonSearchResult.jsf?_flowId=Search.searchperson-flow";
  private static final String VISAPERSON_SERVLET_NAME = "visaperson";
  private static final String VISAENHET_SERVLET_NAME = "ew=visaenhet";
  private static final String VISAENHETDN_SERVLET_NAME = "visaenhetdn";
  private static final String VISAORGANISATION_SERVLET_NAME = "visaorganisation";
  
  @Test
  public void testDoGet() throws ServletException, IOException{
    javax.servlet.RequestDispatcher requestDispatcherMock = createMock(javax.servlet.RequestDispatcher.class);
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);
  
    // Test VISA_PERSON_URL case
    expect(request.getServletPath()).andReturn(VISAPERSON_SERVLET_NAME);
    expect(request.getServletPath()).andReturn(VISAENHET_SERVLET_NAME);
    expect(request.getServletPath()).andReturn(VISAENHETDN_SERVLET_NAME);
    expect(request.getServletPath()).andReturn(VISAORGANISATION_SERVLET_NAME);
    expect(request.getRequestDispatcher(VISA_PERSON_URL)).andReturn(requestDispatcherMock);
    expect(request.getRequestDispatcher(VISA_ENHET_URL)).andReturn(requestDispatcherMock);
    expect(request.getRequestDispatcher(VISA_ENHET_DN_URL)).andReturn(requestDispatcherMock);
    expect(request.getRequestDispatcher(VISA_ORGANISATION_URL)).andReturn(requestDispatcherMock);
    request.setCharacterEncoding("UTF-8");
    expectLastCall().times(4);
    requestDispatcherMock.forward(request, response);
    expectLastCall().times(4);
    replay(requestDispatcherMock,request);
    // Test all 4 if cases
    for (int i = 0; i < 4; i++) {
      requestDispatcher.doGet(request, response);
    }
    verify(requestDispatcherMock,request);
  }
}
