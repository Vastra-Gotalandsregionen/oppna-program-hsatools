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

package se.vgregion.kivtools.search.svc.impl.kiv.ws;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.impl.kiv.ws.SingleAttributeMapper;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfAnyType;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;

public class SingleAttributeMapperTest {

  private static final String HSA_IDENTITY = "hsaIdentity";
  private SingleAttributeMapper singleAttributeMapper;
  private ObjectFactory objectFactory;
  private final String expected = "valueTest";
  private ArrayOfAnyType arrayOfAnyType;
  private Entry entry;
  private String2ArrayOfAnyTypeMap string2ArrayOfAnyTypeMap;
  private JAXBElement<String2ArrayOfAnyTypeMap> serverAttributes;;

  @Before
  public void setUp() throws Exception {
    singleAttributeMapper = new SingleAttributeMapper(HSA_IDENTITY);
    objectFactory = new ObjectFactory();
    arrayOfAnyType = objectFactory.createArrayOfAnyType();
    arrayOfAnyType.getAnyType().add(expected);
    entry = new String2ArrayOfAnyTypeMap.Entry();
    entry.setKey(HSA_IDENTITY);
    entry.setValue(arrayOfAnyType);
    string2ArrayOfAnyTypeMap = objectFactory.createString2ArrayOfAnyTypeMap(); 
    string2ArrayOfAnyTypeMap.getEntry().add(entry);
    serverAttributes = objectFactory.createServerAttributes(string2ArrayOfAnyTypeMap);
  }

  @Test
    public void testMapFunction() {
      Function function = new Function();
      function.setAttributes(serverAttributes);
      String mapFromContext = singleAttributeMapper.map(function);
      assertEquals(expected, mapFromContext);
    }

  @Test
    public void testMapUnit() {
      Unit unit = new Unit();
      unit.setAttributes(serverAttributes);
      String mapFromContext = singleAttributeMapper.map(unit);
      assertEquals(expected, mapFromContext);
    }
  
  @Test(expected=RuntimeException.class)
  public void testException() {
    singleAttributeMapper.map("Wrong class type");
  }
}
