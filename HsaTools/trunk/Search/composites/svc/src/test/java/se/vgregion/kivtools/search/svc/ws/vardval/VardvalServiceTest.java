package se.vgregion.kivtools.search.svc.ws.vardval;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalService;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalServiceImpl;

public class VardvalServiceTest {

	Date upcomingValidFromDate = new Date (2009, 6, 17);
	Date currentValidFromDate = new Date(2002,12,10);
	final String currentHsaId = "204";
	final String upcomingHsaId = "116";
	final String ssn = "194509259257";

	@Test
	@Ignore
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
	@Ignore
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
