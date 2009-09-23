package se.vgregion.kivtools.search.userevent.impl.hak;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.userevent.UserEventInfo;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.ArrayOfEvent;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.Event;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.Resultset;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.UserEventSoap;

public class UserEventServiceNetwiseTest {
  private static final String CODE = "Möte";
  private static final String STATUS = "ejvidarekopplad";
  private static final String INFORMATION = "Gått för dagen";
  private static final String SIGNATURE = "/Kalle";
  private static final String FROM_TIME = "2009-09-09 12:00";
  private static final String TO_TIME = "2009-09-09 17:00";
  private static final String BAD_TIME = "200909091700";

  private static LogFactoryMock logFactoryMock;

  private UserEventServiceNetwise userEventServiceNetwise;
  private UserEventSoapMock userEventSoapMock;

  @BeforeClass
  public static void beforeClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Before
  public void setUp() {
    userEventSoapMock = new UserEventSoapMock();
    userEventServiceNetwise = new UserEventServiceNetwise();
    userEventServiceNetwise.setService(userEventSoapMock);
  }

  @Test
  public void testInstantiation() {
    UserEventServiceNetwise userEventServiceNetwise = new UserEventServiceNetwise();
    assertNotNull(userEventServiceNetwise);
  }

  @Test
  public void testRetrievalNoEvents() {
    List<UserEventInfo> userEvents = userEventServiceNetwise.retrieveUserEvents(null, null, null, null);
    assertNotNull(userEvents);
    assertEquals(0, userEvents.size());
  }

  @Test
  public void testRetrievalTwoEvents() throws ParseException {
    List<Event> events = createEvents();
    userEventSoapMock.setEvents(events);

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    List<UserEventInfo> userEvents = userEventServiceNetwise.retrieveUserEvents("Kalle", "Kula", "031-123456", "070-123456");
    assertEquals(2, userEvents.size());
    assertEquals(CODE, userEvents.get(0).getCode());
    assertEquals(STATUS, userEvents.get(0).getStatus());
    assertEquals(INFORMATION, userEvents.get(0).getInformation());
    assertEquals(SIGNATURE, userEvents.get(0).getSignature());
    assertEquals(format.parse(FROM_TIME), userEvents.get(0).getFromDateTime());
    assertEquals(format.parse(TO_TIME), userEvents.get(0).getToDateTime());
    assertEquals("Unable to parse date from event object\n", logFactoryMock.getError(true));
  }

  private List<Event> createEvents() {
    List<Event> events = new ArrayList<Event>();

    Event event1 = new Event();
    event1.setCode(CODE);
    event1.setStatus(STATUS);
    event1.setInformation(INFORMATION);
    event1.setSignature(SIGNATURE);
    event1.setFrom(FROM_TIME);
    event1.setTo(TO_TIME);
    events.add(event1);
    Event event2 = new Event();
    event2.setFrom(FROM_TIME);
    event2.setTo(TO_TIME);
    events.add(event2);
    Event event3 = new Event();
    event3.setFrom(FROM_TIME);
    event3.setTo(BAD_TIME);
    events.add(event3);
    return events;
  }

  public class UserEventSoapMock implements UserEventSoap {
    private List<Event> events = Collections.emptyList();

    public void setEvents(List<Event> events) {
      this.events = events;
    }

    @Override
    public Resultset getResultsetFromPersonInfo(String telno, String cordless, String firstName, String lastName) {
      Resultset resultset = new Resultset();
      ArrayOfEvent arrayOfEvent = new ArrayOfEvent();
      arrayOfEvent.getEvent().addAll(this.events);
      resultset.setEventList(arrayOfEvent);

      return resultset;
    }

    // Not implemented methods

    @Override
    public ArrayOfEvent getEventListFromNRecNo(String nRecno) {
      return null;
    }

    @Override
    public ArrayOfEvent getEventListFromPersonInfo(String telno, String cordless, String firstName, String lastName) {
      return null;
    }

    @Override
    public int getNRecNoFromCordLess(String cordless) {
      return 0;
    }

    @Override
    public int getNRecNoFromName(String firstName, String lastName) {
      return 0;
    }

    @Override
    public int getNRecNoFromPersonInfo(String telno, String cordless, String firstName, String lastName) {
      return 0;
    }

    @Override
    public int getNRecNoFromTelNo(String telno) {
      return 0;
    }
  }
}
