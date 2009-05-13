package se.vgregion.kivtools.search.intsvc.ws;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

public class UnitDetailsEndPointTest {

	UnitDetailsEndpoint unitDetailsEndpoint;

	@Test
	public void testInvokeInternal() throws Exception {
		Document document = new Document(generateTestData());
		
		DOMOutputter outputter = new DOMOutputter();
		org.w3c.dom.Document w3cDocument = outputter.output(document);
		
		
		//Element responseElement = unitDetailsEndpoint.invokeInternal((org.w3c.dom.Element) w3cDocument.getFirstChild(), w3cDocument);
		//XMLOutputter outputter = new XMLOutputter();
		//String outputString = outputter.outputString(responseElement);
		//checkResponseData(new StringReader(outputString));
		//Assert.assertEquals("testSuccess", responseElement.getText());
	}

	@Before
	public void setup() {
		unitDetailsEndpoint = new UnitDetailsEndpoint();
		unitDetailsEndpoint.setNamespacePrefix("hr");
		unitDetailsEndpoint.setNamespaceUri("http://mycompany.com/hr/schemas");
		unitDetailsEndpoint.setXpathExpression("//hr:hsaIdentity");
	}

	private Element generateTestData() {
		Namespace namespace = Namespace.getNamespace("hr", "http://mycompany.com/hr/schemas");
		Element unit = new Element("unit", namespace);
		Element hsaIdentity = new Element("hsaIdentity", namespace);
		hsaIdentity.setText("testSuccess");
		unit.addContent(hsaIdentity);
		return unit;
	}
	
	private void checkResponseData(Reader inputReader) throws Exception{
		JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Organization organization= (Organization) unmarshaller.unmarshal(inputReader);
		Assert.assertEquals("vgr", organization.getName());
	}
}
