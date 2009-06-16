package se.vgregion.kivtools.search.intsvc.ws.vardval;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import se.vgregion.kivtools.search.intsvc.ws.domain.vardval.VårdvalService;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class VardvalServiceTest {
	
	@Test
	public void testGetVardvalMethod() throws ParseException{
		
		VardvalService vardvalService = new VardvalServiceImpl(); 
		((VardvalServiceImpl)vardvalService).setVardvalService(new VårdvalService());
		VardvalInfo vardvalInfo = vardvalService.getVardval("194509259257");
		
		XMLGregorianCalendar gregorianCalendar =  XMLGregorianCalendarImpl.parse("2002-12-10T00:00:00");
		XMLGregorianCalendar upcomingDate =  XMLGregorianCalendarImpl.parse("2009-06-17T00:00:00");
		Date upDate = upcomingDate.toGregorianCalendar().getTime();
		Date date = gregorianCalendar.toGregorianCalendar().getTime();
		assertEquals("204", vardvalInfo.getCurrentHsaId());
		assertEquals(date, vardvalInfo.getCurrentValidFromDate());
		assertEquals("116", vardvalInfo.getUpcomingHsaId());
		assertEquals(upDate, vardvalInfo.getUpcomingValidFromDate());
		
	}

}
