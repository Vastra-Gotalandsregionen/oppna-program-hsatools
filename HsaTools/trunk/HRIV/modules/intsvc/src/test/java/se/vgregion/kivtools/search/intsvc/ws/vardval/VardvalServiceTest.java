package se.vgregion.kivtools.search.intsvc.ws.vardval;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import se.vgregion.kivtools.search.intsvc.ws.domain.vardval.VårdvalService;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class VardvalServiceTest {

	final Date upcomingValidFromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2009-06-17");
	final Date currentValidFromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2002-12-10");
	final String currentHsaId = "204";
	final String upcomingHsaId = "116";
	final String ssn = "194509259257";

	@Test
	/**
	 * Test getting Vardval information for a person.
	 */
	public void testGetVardvalMethod() throws ParseException {

		VardvalService vardvalService = new VardvalServiceImpl();
		((VardvalServiceImpl) vardvalService).setVardvalService(new VårdvalService());
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
		VardvalService vardvalService = new VardvalServiceImpl();
		((VardvalServiceImpl) vardvalService).setVardvalService(new VårdvalService());
		VardvalInfo vardvalInfo = vardvalService.setVardval(ssn, upcomingHsaId, new byte[] {});

		assertEquals(upcomingHsaId, vardvalInfo.getUpcomingHsaId());
		assertEquals(currentValidFromDate, vardvalInfo.getCurrentValidFromDate());
		assertEquals(upcomingValidFromDate, vardvalInfo.getUpcomingValidFromDate());
	}
}
