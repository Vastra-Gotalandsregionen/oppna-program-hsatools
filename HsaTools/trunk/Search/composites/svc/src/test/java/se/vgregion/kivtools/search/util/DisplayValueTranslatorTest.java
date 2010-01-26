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
package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class DisplayValueTranslatorTest {
  private static final String OFFENTLIG_VARDGIVARE = "Offentlig vårdgivare";
  private static final String PRIVAT_VARDGIVARE = "Privat vårdgivare";
  private static final String OVRIGT = "Övrigt";
  private DisplayValueTranslator displayValueTranslator;

  @Before
  public void setUp() {
    displayValueTranslator = new DisplayValueTranslator();
    Map<String, String> translationMap = new HashMap<String, String>();
    translationMap.put("1", OFFENTLIG_VARDGIVARE);
    translationMap.put("2", OFFENTLIG_VARDGIVARE);
    translationMap.put("3", OFFENTLIG_VARDGIVARE);
    translationMap.put("7", OFFENTLIG_VARDGIVARE);
    translationMap.put("4", PRIVAT_VARDGIVARE);
    translationMap.put("5", PRIVAT_VARDGIVARE);
    translationMap.put("6", PRIVAT_VARDGIVARE);
    translationMap.put("9", OVRIGT);
    displayValueTranslator.setTranslationMap(translationMap);
  }
  
  @Test
  public void testTranslateManagementCode() {
    String resultPublic = displayValueTranslator.translateManagementCode("1");
    String resultPrivate = displayValueTranslator.translateManagementCode("5");
    String resultOvrigt = displayValueTranslator.translateManagementCode("9");
    assertEquals("Unexpected translation", OFFENTLIG_VARDGIVARE, resultPublic);
    assertEquals("Unexpected translation", PRIVAT_VARDGIVARE, resultPrivate);
    assertEquals("Unexpected translation", OVRIGT, resultOvrigt);
  }
  
  @Test
  public void testTranslateManagementCodeWithNull(){
    assertEquals("Should be empty String", "", displayValueTranslator.translateManagementCode(null)); 
  }
}
