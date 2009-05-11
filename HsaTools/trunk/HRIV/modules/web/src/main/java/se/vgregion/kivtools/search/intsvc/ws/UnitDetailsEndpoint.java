package se.vgregion.kivtools.search.intsvc.ws;

import org.jdom.Element;
import org.springframework.ws.server.endpoint.AbstractJDomPayloadEndpoint;


public class UnitDetailsEndpoint extends AbstractJDomPayloadEndpoint {

	@Override
	protected Element invokeInternal(Element unitDetailsRequest) throws Exception {
		System.out.print("Success!");
		return null;
	}
}
