package se.vgregion.kivtools.search.presentation;


import java.io.Serializable;
import java.util.Map;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.executor.jsf.JsfExternalContext;

import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;

public class RegisterOnUnitController implements Serializable {

	private static final long serialVersionUID = 1L;
	VardvalService vardValService;
	
	public void setVardValService(VardvalService vardValService) {
		this.vardValService = vardValService;
	}

	public RegistrationSupportBean getUnitRegistrationInformation(ExternalContext externalContext) {
		
		JsfExternalContext jsfExternalContext = (JsfExternalContext) externalContext;
		
		// Put ssn in sessionscope object
		////JsfExternalContext externalContext = (JsfExternalContext) requestContext.getExternalContext();
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
		
		// WAS returns null. Maybe because the url is not protected in web.xml?
		////Principal remoteUser = externalContext.getUserPrincipal();
		//String name = remoteUser.getName();
		
		// Find ssn as http header (put there by WebSphere)
		//Map<String, String> requestHeaderMap = externalContext.getFacesContext().getExternalContext().getRequestHeaderMap();
		
		Map<String, String> requestHeaderMap = jsfExternalContext.getFacesContext().getExternalContext().getRequestHeaderMap();
		
		String ssn = requestHeaderMap.get("iv-user");
		//String ldapPath = requestHeaderMap.get("iv-user-1");
		System.out.println("ssn: " + ssn);
		//System.out.println("ldapPath: " + ldapPath);
		//System.out.print("name: " + name);
		
		//VardvalInfo vardval = vardValService.getVardval(ssn);
		
		// Set the results in "request scope"
		//requestContext.getRequestScope().put("vardval", vardval);
		//requestContext.getFlowScope().put("ssn", ssn);
		//requestContext.getFlowScope().put("ldapPath", ldapPath);
		//requestContext.getRequestScope().put("name", name);
		
		//Event event = new Event(this, "success");
		//return event;
		
		RegistrationSupportBean rsb = new RegistrationSupportBean();
		rsb.setSsn(ssn);
		return rsb;
	}

	//public void commitRegistrationOnUnit() {
		// we need String socialSecurityNumber, String hsaIdentity) {
//		return success();
	//}

}
