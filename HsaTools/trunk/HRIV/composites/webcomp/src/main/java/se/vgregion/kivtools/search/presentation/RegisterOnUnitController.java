package se.vgregion.kivtools.search.presentation;


import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;

public class RegisterOnUnitController extends MultiAction {

	VardvalService vardValService;
	
	public void setVardValService(VardvalService vardValService) {
		this.vardValService = vardValService;
	}

	public Event getUnitRegistrationInformation(RequestContext requestContext) {
		// Put ssn in sessionscope object
		ServletExternalContext externalContext = (ServletExternalContext) requestContext.getExternalContext();
		//Cookie[] cookies = externalContext.getRequest().getCookies();
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
		//String remoteUser = externalContext.getRequest().getRemoteUser();
		
		// Find ssn as http header (put there by WebSphere)
		String ssn = externalContext.getRequest().getHeader("iv-user");
		
		//VardvalInfo vardval = vardValService.getVardval(ssn);
		
		// Set the results in "request scope"
		//requestContext.getRequestScope().put("vardval", vardval);
		requestContext.getRequestScope().put("ssn", ssn);
		
		Event event = new Event(this, "success");
		return event;
	}

	public Event commitRegistrationOnUnit(RequestContext requestContext) {
		// we need String socialSecurityNumber, String hsaIdentity) {
		return success();
	}

}
