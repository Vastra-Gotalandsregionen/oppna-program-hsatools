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

package se.vgregion.kivtools.search.domain.values.accessibility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.search.domain.values.accessibility.Criteria;

public class CriteriaTest {
  private static final String TEST_STRING = "test";
  private static final Document DOC_WITH_CRITERIA = XmlHelper.getDocumentFromResource("testxml/doc_with_criteria.xml");
  private NodeList nodeList;
  private Criteria criteria;

  @Before
  public void setUp() {
    nodeList = DOC_WITH_CRITERIA.getElementsByTagName("criteria");
  }

  @Test
  public void testConstructor() {
    Node node = nodeList.item(0);
    criteria = new Criteria(node);
    assertNotNull("Unexpected null", criteria);
    assertTrue("Unexpected name", criteria.getName().startsWith("crit1_"));
    assertEquals("Unexpected size of disabilities", 2, criteria.getDisabilities().size());
    assertEquals("Unexpected size of additional criterias", 1, criteria.getAdditionalCriterias().size());
    assertEquals("Unexpected description", "input", criteria.getDescription());

    node = nodeList.item(1);
    criteria = new Criteria(node);
    assertNotNull("Unexpected null", criteria);
    assertTrue("Unexpected name", criteria.getName().startsWith("crit2_"));
    assertEquals("Unexpected size of disabilities", 2, criteria.getDisabilities().size());
    assertEquals("Unexpected size of additional criterias", 1, criteria.getAdditionalCriterias().size());
    assertEquals("Unexpected description", "input2", criteria.getDescription());

    node = nodeList.item(2);
    criteria = new Criteria(node);
    assertNotNull("Unexpected null", criteria);
    assertEquals("Unexpected name", "", criteria.getName());
  }

  @Test
  public void testSetters() {
    Node node = nodeList.item(2);
    criteria = new Criteria(node);

    criteria.setName(TEST_STRING);
    assertEquals(TEST_STRING, criteria.getName());

    assertNull(criteria.getDescription());
    criteria.setDescription(TEST_STRING);
    assertEquals(TEST_STRING, criteria.getDescription());

    assertFalse(criteria.getShow());
    criteria.setShow(true);
    assertTrue(criteria.getShow());

    assertFalse(criteria.isHidden());
    criteria.setHidden(true);
    assertTrue(criteria.isHidden());

    assertFalse(criteria.isNotice());
    criteria.setNotice(true);
    assertTrue(criteria.isNotice());

    assertEquals(0, criteria.getAdditionalCriterias().size());

    assertEquals(0, criteria.getDisabilities().size());
  }
}
