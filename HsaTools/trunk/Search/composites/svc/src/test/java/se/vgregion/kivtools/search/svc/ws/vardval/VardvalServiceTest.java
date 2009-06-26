package se.vgregion.kivtools.search.svc.ws.vardval;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.easymock.classextension.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalEntry;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalService;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class VardvalServiceTest {


	private static Date upcomingValidFromDate;
	private static Date currentValidFromDate;
	private static final String currentHsaId = "204";
	private static final String upcomingHsaId = "116";
	private static final String ssn = "194509259257";
	ObjectFactory objectFactory = new ObjectFactory();
	VårdvalService mockVardvalService;

	@BeforeClass
	public static void setupSimpleDateFormat() throws ParseException{
		upcomingValidFromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2009-06-17");
		currentValidFromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2002-12-10");
	}
	
@Test
	/**
	 * Test getting Vardval information for a person.
	 */
public void testGetVardvalMethod() throws ParseException {
		mockVardvalService = EasyMock.createMock(VårdvalService.class);
		IVårdvalService mockService = EasyMock.createMock(IVårdvalService.class);
		GetVårdvalResponse getVardvalResponse = new GetVårdvalResponse();
		generateResponse(getVardvalResponse);

		EasyMock.expect(mockVardvalService.getBasicHttpBindingIVårdvalService()).andReturn(mockService);
		EasyMock.expect(mockService.getVårdVal(EasyMock.isA(GetVårdvalRequest.class))).andReturn(getVardvalResponse);
		EasyMock.replay(mockVardvalService, mockService);

		VardvalService vardvalService = new VardvalServiceImpl();
		((VardvalServiceImpl) vardvalService).setVardvalService(mockVardvalService);
		VardvalInfo vardvalInfo = vardvalService.getVardval(ssn);

		assertEquals(currentHsaId, vardvalInfo.getCurrentHsaId());
		assertEquals(currentValidFromDate, vardvalInfo.getCurrentValidFromDate());
		assertEquals(upcomingHsaId, vardvalInfo.getUpcomingHsaId());
		assertEquals(upcomingValidFromDate, vardvalInfo.getUpcomingValidFromDate());
}

	@Test
	/**
	 * Test assigning new listing and check retrieved Vardval information. 
	 */
	public void testSetVardvalMethod() {
		mockVardvalService = EasyMock.createMock(VårdvalService.class);
		IVårdvalService mockService = EasyMock.createMock(IVårdvalService.class);
		SetVårdvalResponse setVardvalResponse = new SetVårdvalResponse();
		generateResponse(setVardvalResponse);

		EasyMock.expect(mockVardvalService.getBasicHttpBindingIVårdvalService()).andReturn(mockService);
		EasyMock.expect(mockService.setVårdVal(EasyMock.isA(SetVårdvalRequest.class))).andReturn(setVardvalResponse);
		EasyMock.replay(mockVardvalService, mockService);

		VardvalService vardvalService = new VardvalServiceImpl();
		((VardvalServiceImpl) vardvalService).setVardvalService(mockVardvalService);
		VardvalInfo vardvalInfo = vardvalService.setVardval(ssn, upcomingHsaId, new byte[] {});

		assertEquals(upcomingHsaId, vardvalInfo.getUpcomingHsaId());
		assertEquals(currentValidFromDate, vardvalInfo.getCurrentValidFromDate());
		assertEquals(upcomingValidFromDate, vardvalInfo.getUpcomingValidFromDate());
	}
	
	private void generateResponse(GetVårdvalResponse getVardvalResponse) {
		VårdvalEntry currentEntry = new VårdvalEntry();
		VårdvalEntry upcomingEntry = new VårdvalEntry();
		// Set current and upcoming dates for vårdval entries
		GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
		calendar.setTime(currentValidFromDate);
		XMLGregorianCalendar currentGregorianCalendar = new XMLGregorianCalendarImpl(calendar);		
		calendar.setTime(upcomingValidFromDate);
		XMLGregorianCalendar upcomingGregorianCalendar = new XMLGregorianCalendarImpl(calendar);		
		currentEntry.setGiltigFrån(currentGregorianCalendar);
		upcomingEntry.setGiltigFrån(upcomingGregorianCalendar);
		
		// Set hsaId:s
		currentEntry.setVårdcentralHsaId(currentHsaId);
		upcomingEntry.setVårdcentralHsaId(upcomingHsaId);
		
		JAXBElement<VårdvalEntry> currentJaxbElement = objectFactory.createVårdvalEntry(currentEntry);
		JAXBElement<VårdvalEntry> upcomingJaxbElement =objectFactory.createVårdvalEntry(upcomingEntry);
		
		getVardvalResponse.setAktivtVårdval(currentJaxbElement);
		getVardvalResponse.setKommandeVårdval(upcomingJaxbElement);
	
	}
	
	private void generateResponse(SetVårdvalResponse setVardvalResponse) {
		VårdvalEntry currentEntry = new VårdvalEntry();
		VårdvalEntry upcomingEntry = new VårdvalEntry();
		// Set current and upcoming dates for vårdval entries
		GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
		calendar.setTime(currentValidFromDate);
		XMLGregorianCalendar currentGregorianCalendar = new XMLGregorianCalendarImpl(calendar);		
		calendar.setTime(upcomingValidFromDate);
		XMLGregorianCalendar upcomingGregorianCalendar = new XMLGregorianCalendarImpl(calendar);		
		currentEntry.setGiltigFrån(currentGregorianCalendar);
		upcomingEntry.setGiltigFrån(upcomingGregorianCalendar);
		
		// Set hsaId:s
		currentEntry.setVårdcentralHsaId(currentHsaId);
		upcomingEntry.setVårdcentralHsaId(upcomingHsaId);
		
		JAXBElement<VårdvalEntry> currentJaxbElement = objectFactory.createVårdvalEntry(currentEntry);
		JAXBElement<VårdvalEntry> upcomingJaxbElement =objectFactory.createVårdvalEntry(upcomingEntry);
		
		setVardvalResponse.setAktivtVårdval(currentJaxbElement);
		setVardvalResponse.setKommandeVårdval(upcomingJaxbElement);
	
	}

}
