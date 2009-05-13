package se.vgregion.kivtools.search.intsvc.ws;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

public class UnitDetailsServiceImpl implements UnitDetailsService {

	public org.w3c.dom.Element getUnitDetails(String hsaIdentity) {
		try {
			Organization organization = new Organization();
			organization.setName("vgr");
			JAXBContext context = JAXBContext.newInstance(organization.getClass());
			Marshaller marshaller = context.createMarshaller();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			org.w3c.dom.Document doc = dbf.newDocumentBuilder().newDocument();
			marshaller.marshal(organization, doc);
			return (org.w3c.dom.Element) doc.getFirstChild();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
