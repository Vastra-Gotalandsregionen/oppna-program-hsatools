package se.vgregion.kivtools.hriv.intsvc.utils;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * 
 * @author David Bennehult
 * 
 */
public final class XmlMarshaller {
  /**
   * Generate xml content of current object, by using jaxb marshaller.
   * @param <T> type of the objectToMarshaller object.
   * @param objectToMarshaller the object to generate xml content of.
   * @return String containing the generated xml content.
   */
  public static <T> String generateXmlContentOfObject(T objectToMarshaller) {
    StringWriter fileContent = new StringWriter();
    try {
      JAXBContext context = JAXBContext.newInstance(objectToMarshaller.getClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.marshal(objectToMarshaller, fileContent);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
    return fileContent.toString();
  }

}
