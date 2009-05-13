package se.vgregion.kivtools.search.intsvc.ws;

import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.springframework.ws.server.endpoint.AbstractDomPayloadEndpoint;
import org.w3c.dom.Document;

public class UnitDetailsEndpoint extends AbstractDomPayloadEndpoint{
	XPath xpath;
	String xpathExpression;
	String namespacePrefix;
	String namespaceUri;
	UnitDetailsService unitDetailsService;

	public UnitDetailsService getUnitDetailsService() {
		return unitDetailsService;
	}

	public void setUnitDetailsService(UnitDetailsService unitDetailsService) {
		this.unitDetailsService = unitDetailsService;
	}

	public void setXpathExpression(String xpathExpression) {
		this.xpathExpression = xpathExpression;
	}

	public void setNamespacePrefix(String namespacePrefix) {
		this.namespacePrefix = namespacePrefix;
	}

	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = namespaceUri;
	}

	private XPath getXpath() {
		if (xpath == null) {
			try {
				xpath = XPath.newInstance(xpathExpression);
				xpath.addNamespace(Namespace.getNamespace(namespacePrefix, namespaceUri));
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}
		return xpath;
	}

	@Override
	protected org.w3c.dom.Element invokeInternal(org.w3c.dom.Element unitDetailsRequest, Document arg1) throws Exception {
		String hsaIdentity = getXpath().valueOf(unitDetailsRequest);
		return unitDetailsService.getUnitDetails(hsaIdentity);
	}
}
