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
package se.vgregion.kivtools.util.presentation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.util.presentation.PresentationHelper;

public class PresentationHelperTest {
  private PresentationHelper helper;

  @Before
  public void setUp() {
    helper = new PresentationHelper();
  }

  @Test
  public void testInstantiation() {
    PresentationHelper presentationHelper = new PresentationHelper();
    assertNotNull(presentationHelper);
  }

  @Test
  public void testReplaceNewlineWithBr() {
    String input = "abc\ndef\nghi";
    String expected = "abc<br/>def<br/>ghi";
    String result = helper.replaceNewlineWithBr(input);
    assertEquals(expected, result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReplaceNewlineWithBrNullInput() {
    helper.replaceNewlineWithBr(null);
  }

  @Test
  public void testGetTextWithEllipsis() {
    String input = "Lorem ipsum dolor sit amet posuere.";
    String expected = "Lorem ipsum dolor sit amet posuere.";
    String result = helper.getTextWithEllipsis(input, 40);
    assertEquals(expected, result);

    expected = "Lorem ipsum dolor sit ame...";
    result = helper.getTextWithEllipsis(input, 25);
    assertEquals(expected, result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetTextWithEllipsisNullInput() {
    helper.getTextWithEllipsis(null, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetTextWithEllipsisLengthLessThanOne() {
    helper.getTextWithEllipsis("", 0);
  }
}
