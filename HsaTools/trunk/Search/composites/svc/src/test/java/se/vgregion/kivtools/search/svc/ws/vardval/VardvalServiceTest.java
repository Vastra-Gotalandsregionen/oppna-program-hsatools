/**
 * Copyright 2009 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
package se.vgregion.kivtools.search.svc.ws.vardval;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.easymock.classextension.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdvalVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalEntry;

public class VardvalServiceTest {

  private static Date upcomingValidFromDate;
  private static Date currentValidFromDate;
  private static final String currentHsaId = "204";
  private static final String upcomingHsaId = "116";
  private static final String ssn = "194509259257";
  ObjectFactory objectFactory = new ObjectFactory();

  @BeforeClass
  public static void setupSimpleDateFormat() throws ParseException {
    upcomingValidFromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2009-06-17");
    currentValidFromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2002-12-10");
  }

  @Test
  /*
   * * Test getting Vardval information for a person.
   */
  public void testGetVardvalMethod() throws Exception {
    IVårdvalService mockService = EasyMock.createMock(IVårdvalService.class);
    GetVårdvalResponse getVardvalResponse = new GetVårdvalResponse();
    generateResponse(getVardvalResponse);

    org.easymock.EasyMock.expect(mockService.getVårdval(org.easymock.EasyMock.isA(GetVårdvalRequest.class))).andReturn(getVardvalResponse);
    EasyMock.replay(mockService);

    VardvalService vardvalService = new VardvalServiceImpl();

    ((VardvalServiceImpl) vardvalService).setService(mockService);
    VardvalInfo vardvalInfo = vardvalService.getVardval(ssn);

    assertEquals(currentHsaId, vardvalInfo.getCurrentHsaId());
    assertEquals(currentValidFromDate, vardvalInfo.getCurrentValidFromDate());
    assertEquals(upcomingHsaId, vardvalInfo.getUpcomingHsaId());
    assertEquals(upcomingValidFromDate, vardvalInfo.getUpcomingValidFromDate());
  }

  @Test
  /*
   * * Test assigning new listing and check retrieved Vardval information.
   */
  public void testSetVardvalMethod() throws IVårdvalServiceSetVårdvalVårdvalServiceErrorFaultFaultMessage, DatatypeConfigurationException {
    IVårdvalService mockService = EasyMock.createMock(IVårdvalService.class);
    SetVårdvalResponse setVardvalResponse = new SetVårdvalResponse();
    generateResponse(setVardvalResponse);

    org.easymock.EasyMock.expect(mockService.setVårdval(org.easymock.EasyMock.isA(SetVårdvalRequest.class))).andReturn(setVardvalResponse);
    EasyMock.replay(mockService);

    VardvalService vardvalService = new VardvalServiceImpl();
    ((VardvalServiceImpl) vardvalService).setService(mockService);
    VardvalInfo vardvalInfo = vardvalService.setVardval(ssn, upcomingHsaId, new byte[] {});

    assertEquals(upcomingHsaId, vardvalInfo.getUpcomingHsaId());
    assertEquals(currentValidFromDate, vardvalInfo.getCurrentValidFromDate());
    assertEquals(upcomingValidFromDate, vardvalInfo.getUpcomingValidFromDate());
  }

  private void generateResponse(GetVårdvalResponse getVardvalResponse) throws DatatypeConfigurationException {
    VårdvalEntry currentEntry = new VårdvalEntry();
    VårdvalEntry upcomingEntry = new VårdvalEntry();
    // Set current and upcoming dates for vårdval entries
    GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
    calendar.setTime(currentValidFromDate);
    XMLGregorianCalendar currentGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    calendar.setTime(upcomingValidFromDate);
    XMLGregorianCalendar upcomingGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    currentEntry.setGiltigFrån(currentGregorianCalendar);
    upcomingEntry.setGiltigFrån(upcomingGregorianCalendar);

    // Set hsaId:s
    currentEntry.setVårdcentralHsaId(currentHsaId);
    upcomingEntry.setVårdcentralHsaId(upcomingHsaId);

    JAXBElement<VårdvalEntry> currentJaxbElement = objectFactory.createVårdvalEntry(currentEntry);
    JAXBElement<VårdvalEntry> upcomingJaxbElement = objectFactory.createVårdvalEntry(upcomingEntry);

    getVardvalResponse.setAktivtVårdval(currentJaxbElement);
    getVardvalResponse.setKommandeVårdval(upcomingJaxbElement);

  }

  private void generateResponse(SetVårdvalResponse setVardvalResponse) throws DatatypeConfigurationException {
    VårdvalEntry currentEntry = new VårdvalEntry();
    VårdvalEntry upcomingEntry = new VårdvalEntry();
    // Set current and upcoming dates for vårdval entries
    GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
    calendar.setTime(currentValidFromDate);
    XMLGregorianCalendar currentGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    calendar.setTime(upcomingValidFromDate);
    XMLGregorianCalendar upcomingGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    currentEntry.setGiltigFrån(currentGregorianCalendar);
    upcomingEntry.setGiltigFrån(upcomingGregorianCalendar);

    // Set hsaId:s
    currentEntry.setVårdcentralHsaId(currentHsaId);
    upcomingEntry.setVårdcentralHsaId(upcomingHsaId);

    JAXBElement<VårdvalEntry> currentJaxbElement = objectFactory.createVårdvalEntry(currentEntry);
    JAXBElement<VårdvalEntry> upcomingJaxbElement = objectFactory.createVårdvalEntry(upcomingEntry);

    setVardvalResponse.setAktivtVårdval(currentJaxbElement);
    setVardvalResponse.setKommandeVårdval(upcomingJaxbElement);

  }

}
