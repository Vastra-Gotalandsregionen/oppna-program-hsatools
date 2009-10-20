package se.vgregion.kivtools.util.time;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;

public class TimeUtilTest {

  private Calendar calendar;

  @Before
  public void setUp() throws Exception {
    calendar = Calendar.getInstance();
    calendar.set(2009, 8, 19, 16, 23);
    calendar.set(Calendar.SECOND, 48);
    calendar.set(Calendar.MILLISECOND, 0);
    TimeSource timeSource = new TimeSource() {
      @Override
      public long millis() {
        return calendar.getTimeInMillis();
      }
    };
    TimeUtil.setTimeSource(timeSource);
  }

  @After
  public void tearDown() {
    TimeUtil.reset();
  }

  @Test
  public void testInstantiation() {
    TimeUtil timeUtil = new TimeUtil();
    assertNotNull(timeUtil);
  }

  @Test
  public void testAsMillis() {
    assertEquals(1253370228000L, TimeUtil.asMillis());
  }

  @Test
  public void testAsDate() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String result = format.format(TimeUtil.asDate());
    assertEquals("2009-09-19 16:23:48", result);
  }

  @Test
  public void testFormatDate() {
    String result = TimeUtil.formatDate(TimeUtil.asDate(), DateTimeFormat.ZULU_TIME);
    assertEquals("20090919162348Z", result);
    result = TimeUtil.formatDate(TimeUtil.asDate(), DateTimeFormat.SCIENTIFIC_TIME);
    assertEquals("20090919162348", result);
    result = TimeUtil.formatDate(TimeUtil.asDate(), DateTimeFormat.NORMAL_TIME);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
    String expected = dateFormat.format(calendar.getTime());
    assertEquals(expected, result);
  }

  @Test
  public void testGetCurrentTimeFormatted() {
    String result = TimeUtil.getCurrentTimeFormatted(DateTimeFormat.ZULU_TIME);
    assertEquals("20090919162348Z", result);
    result = TimeUtil.getCurrentTimeFormatted(DateTimeFormat.SCIENTIFIC_TIME);
    assertEquals("20090919162348", result);
    result = TimeUtil.getCurrentTimeFormatted(DateTimeFormat.NORMAL_TIME);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
    String expected = dateFormat.format(calendar.getTime());
    assertEquals(expected, result);
  }

  @Test
  public void testParseStringToZuluTime() {
    Date parsedDate = TimeUtil.parseStringToZuluTime("20090919162348Z");
    assertEquals(calendar.getTime(), parsedDate);
  }

  @Test
  public void testParseStringToZuluTimeUnparseableDate() {
    Date parsedDate = TimeUtil.parseStringToZuluTime("2xx90x19162348Z");
    assertNull(parsedDate);
  }

  @Test
  public void testDateTimeFormatToString() {
    String result = DateTimeFormat.ZULU_TIME.toString();
    assertEquals("yyyyMMddHHmmss'Z'", result);
  }

  @Test
  public void testDefaultTimeSource() {
    TimeUtil.reset();
    Date now = TimeUtil.asDate();
    assertTrue(now.after(calendar.getTime()));
  }
}
