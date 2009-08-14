package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConverterTest {

  @Test
  public void testInstantiation() {
    Converter converter = new Converter();
    assertNotNull(converter);
  }

  @Test
  public void testGetIntegerArrayList() {
    List<String> input = new ArrayList<String>();
    List<Integer> result = Converter.getIntegerArrayList(input);
    assertNotNull(result);
    assertEquals(0, result.size());

    input.add("abc");
    result = Converter.getIntegerArrayList(input);
    assertNotNull(result);
    assertEquals(0, result.size());

    input.add("123");
    result = Converter.getIntegerArrayList(input);
    assertNotNull(result);
    assertEquals(1, result.size());
  }
}
