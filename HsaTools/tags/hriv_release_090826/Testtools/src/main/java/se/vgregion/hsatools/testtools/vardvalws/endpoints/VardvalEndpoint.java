package se.vgregion.hsatools.testtools.vardvalws.endpoints;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import se.vgregion.hsatools.testtools.services.Service;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.GetVårdvalRequest;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.GetVårdvalResponse;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.ObjectFactory;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.SetVårdvalRequest;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.SetVårdvalResponse;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.VårdvalEntry;

public class VardvalEndpoint extends AbstractMarshallingPayloadEndpoint {

  private ObjectFactory objectFactory = new ObjectFactory();
  
  public VardvalEndpoint(Marshaller marshaller, Unmarshaller unmarshaller) {
    super(marshaller, unmarshaller);
  }
  
  @Override
  protected Object invokeInternal(Object requestObject) throws Exception {
    Object response = null;
    String ssn = null;
    String unitId = null;
    if (requestObject instanceof GetVårdvalRequest) {
      GetVårdvalRequest vardvalRequest = (GetVårdvalRequest) requestObject;
      ssn = vardvalRequest.getPersonnummer().getValue();
      unitId = Service.getUnit(ssn);
      GetVårdvalResponse vardvalResponse = new GetVårdvalResponse();
      VårdvalEntry vardvalEntry = objectFactory.createVårdvalEntry();
      vardvalEntry.setVårdcentralHsaId(unitId);
      vardvalResponse.setAktivtVårdval(objectFactory.createVårdvalEntry(vardvalEntry));
      vardvalResponse.setKommandeVårdval(objectFactory.createVårdvalEntry(vardvalEntry));
      response = vardvalResponse;
    } else if (requestObject instanceof SetVårdvalRequest) {
      SetVårdvalRequest setVardvalrequest = (SetVårdvalRequest) requestObject;
      ssn = setVardvalrequest.getPersonnummer().getValue();
      unitId = setVardvalrequest.getVårdcentralHsaId().getValue();
      String currentUnitId = Service.getUnit(ssn);
      Service.setUnit(ssn, unitId);

      XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance());
      VårdvalEntry currentVardvalEntry = objectFactory.createVårdvalEntry();
      currentVardvalEntry.setPersonnummer(ssn);
      currentVardvalEntry.setGiltigFrån(xmlGregorianCalendar);
      currentVardvalEntry.setVårdcentralHsaId(currentUnitId);

      VårdvalEntry upcomingVardvalEntry = objectFactory.createVårdvalEntry();
      upcomingVardvalEntry.setPersonnummer(ssn);
      upcomingVardvalEntry.setGiltigFrån(xmlGregorianCalendar);
      upcomingVardvalEntry.setVårdcentralHsaId(unitId);

      SetVårdvalResponse setVardvalresponse = new SetVårdvalResponse();
      setVardvalresponse.setAktivtVårdval(objectFactory.createVårdvalEntry(currentVardvalEntry));
      setVardvalresponse.setKommandeVårdval(objectFactory.createVårdvalEntry(upcomingVardvalEntry));
      response = setVardvalresponse;
    }
    return response;
  }

}
