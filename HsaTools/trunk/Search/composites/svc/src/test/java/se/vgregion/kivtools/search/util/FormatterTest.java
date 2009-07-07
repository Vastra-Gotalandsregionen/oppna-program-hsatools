package se.vgregion.kivtools.search.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FormatterTest {

	@Test
	public void testConcatenate() {
		assertEquals("Vårdcentral Angered, Angered", Formatter.concatenate("Vårdcentral Angered", "Angered"));
		assertEquals("Vårdcentral Angered, Göteborg, Angered", Formatter.concatenate("Vårdcentral Angered", "Göteborg", "Angered"));
		assertEquals("Vårdcentral Angered, Göteborg", Formatter.concatenate("Vårdcentral Angered", "Göteborg", ""));
	}
}
