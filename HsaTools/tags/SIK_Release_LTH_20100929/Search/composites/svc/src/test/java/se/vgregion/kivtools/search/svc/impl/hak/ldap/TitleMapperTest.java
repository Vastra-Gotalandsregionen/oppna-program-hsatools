/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.svc.TitleCache;

public class TitleMapperTest {
  private TitleCache titleCache;
  private TitleMapper titleMapper;
  private DirContextOperationsMock dirContextOperations;

  @Before
  public void setUp() throws Exception {
    titleCache = new TitleCache();
    titleMapper = new TitleMapper(titleCache);
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    TitleMapper titleMapper = new TitleMapper(titleCache);
    assertNotNull(titleMapper);
  }

  @Test
  public void testValidTitle() {
    dirContextOperations.addAttributeValue("title", "Assistent");
    titleMapper.mapFromContext(dirContextOperations);
    List<String> matchingTitles = titleCache.getMatchingTitles("sis");
    assertEquals(1, matchingTitles.size());
    assertEquals("Assistent", matchingTitles.get(0));
  }

  @Test
  public void testNullTitle() {
    titleMapper.mapFromContext(dirContextOperations);
    List<String> matchingTitles = titleCache.getMatchingTitles("");
    assertEquals(0, matchingTitles.size());
  }

  @Test
  public void testWhitespaceTitle() {
    dirContextOperations.addAttributeValue("title", "    ");
    titleMapper.mapFromContext(dirContextOperations);
    List<String> matchingTitles = titleCache.getMatchingTitles("");
    assertEquals(0, matchingTitles.size());
  }
}
