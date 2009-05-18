package se.vgregion.kivtools.search.presentation;

import javax.servlet.http.Cookie;

import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import se.vgregion.kivtools.search.common.Base64;

public class RegisterOnUnitController extends MultiAction {

	public Event extractSessionInformation(RequestContext requestContext) {
		// Put ssn in sessionscope object
		ServletExternalContext externalContext = (ServletExternalContext) requestContext.getExternalContext();
		Cookie[] cookies = externalContext.getRequest().getCookies();

		/*
		String ltpaCookieToken = null;
		String ltpaCookieName = "LtpaToken";

		// Get LTPA token
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(ltpaCookieName)) {
				ltpaCookieToken = cookies[i].getValue();
			}
		}
		
		// LTPA token is base64 encoded
		byte[] ltpaTokenKey = Base64.decode(ltpaCookieToken);
		*/

		String remoteUser = externalContext.getRequest().getRemoteUser();
		
		System.out.println(requestContext);

		return success();
	}

	public Event commitRegistrationOnUnit(RequestContext requestContext) {
		// we need String socialSecurityNumber, String hsaIdentity) {
		return success();
	}

}
