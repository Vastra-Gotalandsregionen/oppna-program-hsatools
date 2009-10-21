package se.vgregion.kivtools.hriv.servlets;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;

import se.vgregion.kivtools.hriv.presentation.SuggestionsSupportBean;
import se.vgregion.kivtools.hriv.servlets.Suggestions;
import se.vgregion.kivtools.search.domain.Unit;

public class SuggestionsTest {

  private static final String TEXT_XML = "text/xml";
  private static final String TEXT_HTML = "text/html";
  private static final String TEXT_PLAIN = "text/plain";
  private static final String OUT_PUT_FORMAT_TEXT = "text";
  private static final String OUT_PUT_FORMAT_XML = "xml";
  private static final String OUT_PUT_FORMAT_OTHER = "other";
  private static final String USER_INPUT_UNIT_NAME = "userInputUnitName";
  private Suggestions suggestions = new Suggestions();
  private ServletConfig servletConfigMock;
  private ServletContext servletContextMock;
  private ByteArrayOutputStream byteArrayOutputStream;
  private HttpServletRequest httpServletRequestMock;
  private HttpServletResponse httpServletResponseMock;
  private SuggestionsSupportBean suggestionsSupportBeanMock;

  @Before
  public void setup() throws ServletException, IOException {
    byteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
    
    servletConfigMock = createMock(ServletConfig.class);
    servletContextMock = createMock(ServletContext.class);
    WebApplicationContext webApplicationContextmock = createMock(WebApplicationContext.class);
    suggestionsSupportBeanMock = createMock(SuggestionsSupportBean.class);
    expect(servletContextMock.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT")).andReturn(webApplicationContextmock);
    expect(webApplicationContextmock.getBean("Search_SuggestionsSupportBean")).andReturn(suggestionsSupportBeanMock);
    expect(servletConfigMock.getServletContext()).andReturn(servletContextMock);
    
    
    httpServletResponseMock = createMock(HttpServletResponse.class);
    httpServletRequestMock = createMock(HttpServletRequest.class);
    expect(httpServletResponseMock.getWriter()).andReturn(printWriter);
    expect(httpServletRequestMock.getParameter("query")).andReturn(USER_INPUT_UNIT_NAME);

    
    replay(servletConfigMock, servletContextMock, webApplicationContextmock);
    suggestions.init(servletConfigMock);
  }

  @Test
  public void testDoGetForPlainText() throws IOException, ServletException {
    httpServletResponseMock.setContentType(TEXT_PLAIN);
    expect(httpServletRequestMock.getParameter("output")).andReturn(OUT_PUT_FORMAT_TEXT);
    expect(suggestionsSupportBeanMock.getSuggestions(USER_INPUT_UNIT_NAME, OUT_PUT_FORMAT_TEXT)).andReturn(createSuggestionPlainTextResult());
    replay(httpServletRequestMock, httpServletResponseMock, suggestionsSupportBeanMock); 
    suggestions.doGet(httpServletRequestMock, httpServletResponseMock);
    String result = byteArrayOutputStream.toString();
    verify(httpServletResponseMock);
    assertEquals(createSuggestionPlainTextResult(), result);
    
  }
  
  @Test
  public void testDoGetForHtml() throws ServletException, IOException{
    httpServletResponseMock.setContentType(TEXT_XML);
    expect(httpServletRequestMock.getParameter("output")).andReturn(OUT_PUT_FORMAT_XML);
    expect(suggestionsSupportBeanMock.getSuggestions(USER_INPUT_UNIT_NAME, OUT_PUT_FORMAT_XML)).andReturn(createSuggestionPlainTextResult());
    replay(httpServletRequestMock, httpServletResponseMock, suggestionsSupportBeanMock); 
    suggestions.doGet(httpServletRequestMock, httpServletResponseMock);
    String result = byteArrayOutputStream.toString();
    verify(httpServletResponseMock);
    assertEquals(createSuggestionPlainTextResult(), result);
  }
  
  @Test
  public void testDoGetForOther() throws ServletException, IOException{
    httpServletResponseMock.setContentType(TEXT_HTML);
    expect(httpServletRequestMock.getParameter("output")).andReturn(OUT_PUT_FORMAT_OTHER);
    expect(suggestionsSupportBeanMock.getSuggestions(USER_INPUT_UNIT_NAME, OUT_PUT_FORMAT_OTHER)).andReturn(createSuggestionPlainTextResult());
    replay(httpServletRequestMock, httpServletResponseMock, suggestionsSupportBeanMock); 
    suggestions.doGet(httpServletRequestMock, httpServletResponseMock);
    String result = byteArrayOutputStream.toString();
    verify(httpServletResponseMock);
    assertEquals(createSuggestionPlainTextResult(), result);
  }

  private String createSuggestionPlainTextResult() {
    StringBuilder suggestions = new StringBuilder();
    suggestions.append("<?xml version='1.0' standalone='yes'?>\n<units>\n");

    for (int i = 0; i < 10; i++) {
      String description = "unit_" + i + "description";
      suggestions.append("<unit description=\"" + description + "\" id=\"" + "unit_id_" + i + "\" />\n");
    }
    return suggestions.toString();
  }
}
