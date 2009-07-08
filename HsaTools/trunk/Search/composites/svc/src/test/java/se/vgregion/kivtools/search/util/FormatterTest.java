package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FormatterTest {

  @Test
  public void testConcatenate() {
    assertEquals("Vårdcentral Angered, Angered", Formatter.concatenate("Vårdcentral Angered", "Angered"));
    assertEquals("Vårdcentral Angered, Göteborg, Angered", Formatter.concatenate("Vårdcentral Angered", "Göteborg", "Angered"));
    assertEquals("Vårdcentral Angered, Göteborg", Formatter.concatenate("Vårdcentral Angered", "Göteborg", ""));

    assertEquals("Empty string expected for null List", "", Formatter.concatenate((List) null));
    List<String> list = new ArrayList<String>();
    list.add("Vårdcentral Angered");
    list.add("Angered");
    assertEquals("Unexpected result", "Vårdcentral Angered, Angered", Formatter.concatenate(list));
  }
}
