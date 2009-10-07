package se.vgregion.kivtools.hriv.intsvc.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.test.annotation.ExpectedException;

public class XmlMarshallerTest {

  private final String EXPECTED_XML_RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><item id=\"0\"/>";

  @Test
  public void testInstance() {
    assertNotNull(new XmlMarshaller());
  }

  @Test
  public void testGenerateXmlContentOfObject() {
    MarshallerClass item = new MarshallerClass();
    String generateXmlContentOfObject = XmlMarshaller.generateXmlContentOfObject(item);
    assertEquals(EXPECTED_XML_RESULT, generateXmlContentOfObject);
  }

  @ExpectedException(RuntimeException.class)
  public void testExceptionHandling() {
    XmlMarshaller.generateXmlContentOfObject(new String());
  }

}
