package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class RegisterOnUnitControllerTest {

	RegisterOnUnitController registerOnUnitController;

	@Before
	public void setup() {
		registerOnUnitController = new RegisterOnUnitController();

	}

	@Test
	@Ignore
	public void testExtractSessionInformation() {
		
		RequestContext mockRequestContext = EasyMock.createMock(RequestContext.class);
		HttpServletRequest mockHttpServletRequest = EasyMock.createMock(HttpServletRequest.class);	
		
		Cookie[] cookies = {new Cookie("LtpaToken","base64encodedName" )};
		
		ServletExternalContext mockServletExternalContext = EasyMock.createMock(ServletExternalContext.class);
		
		// 1 Get servletexternalcontext
		EasyMock.expect(mockRequestContext.getExternalContext()).andReturn(mockServletExternalContext);
		
		// 2 Get httpservletrequest
		EasyMock.expect(mockServletExternalContext.getRequest()).andReturn(mockHttpServletRequest);

		// 3 Get cookies
		EasyMock.expect(mockHttpServletRequest.getCookies()).andReturn(cookies);
		
		EasyMock.replay(mockRequestContext, mockServletExternalContext, mockHttpServletRequest);
		
		Event event = registerOnUnitController.extractSessionInformation(mockRequestContext);
		assertEquals("success", event.getId());
	}
}
