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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfAnyType;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfDeletedObject;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfDeliveryPoint;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfFunction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfPerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfResource;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfServer;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfString;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfTransaction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnsurePerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Person;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionDirectory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebServiceImplPortType;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;

public class KivwsSearchServiceTest {
  private KivwsSearchService kivwsSearchService;
  private VGRegionWebServiceMock vgRegionWebServiceMock;
  private KivwsMapperMock kivwsMapperMock;
  private static LogFactoryMock createInstance;
  private final DistinguishedName base = new DistinguishedName("ou=vgr, o=org");

  @BeforeClass
  public static void setupBeforeClass() {
    createInstance = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void after() {
    LogFactoryMock.createInstance();
  }

  @Before
  public void setUp() throws Exception {
    this.kivwsSearchService = new KivwsSearchService();
    this.vgRegionWebServiceMock = new VGRegionWebServiceMock();
    this.kivwsSearchService.setVgregionWebService(this.vgRegionWebServiceMock);
    this.kivwsMapperMock = new KivwsMapperMock();
    this.kivwsSearchService.setKivwsUnitMapper(this.kivwsMapperMock);
  }

  @Test
  public void testLookupUnit() {
    DistinguishedName distinguishedName = new DistinguishedName("ou=Primärvårdsrehab Majorna,ou=PVO Centrum Göteborg,ou=Primärvården Göteborg,ou=org,o=VGR");
    this.kivwsSearchService.lookupUnit(distinguishedName, Arrays.asList("attr1", "attr2"));
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr1"));
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr2"));
    assertEquals("(ou=Primärvårdsrehab Majorna)", this.vgRegionWebServiceMock.filter);
  }

  @Test
  public void testVGRException_Exception() {
    this.vgRegionWebServiceMock.throwException = true;
    this.kivwsSearchService.lookupUnit(new DistinguishedName("ou=test"), new ArrayList<String>());
    assertEquals("Exception searchUnit\n", createInstance.getError(true));
    this.kivwsSearchService.searchFunctionUnits(new DistinguishedName("ou=vgr, o=org"), "(ou=*)", 2, new ArrayList<String>());
    assertEquals("Exception searchFunction\n", createInstance.getError(true));
    this.kivwsSearchService.searchSingleAttribute(new DistinguishedName("ou=vgr, o=org"), "(ou=*)", 2, new ArrayList<String>(), "hsaIdentity");
    assertEquals("Exception searchFunction\n", createInstance.getError(true));
    this.kivwsSearchService.searchUnits(new DistinguishedName("ou=vgr, o=org"), "(ou=*)", 2, new ArrayList<String>());
    assertEquals("Exception searchUnit\n", createInstance.getError(true));
  }

  @Test
  public void testSearchFunctionUnits() {
    this.kivwsSearchService.searchFunctionUnits(this.base, "ou=*", 2, Arrays.asList("attr1", "attr2"));
    assertEquals(this.vgRegionWebServiceMock.filter, "ou=*");
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr1"));
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr2"));
    assertEquals(this.vgRegionWebServiceMock.base, this.base.toString());
    assertEquals(this.vgRegionWebServiceMock.searchScope, "2");
    assertEquals(this.vgRegionWebServiceMock.vgRegionDirectory, VGRegionDirectory.KIV);
  }

  @Test
  public void testSearchSingleAttribute() {
    List<String> result = this.kivwsSearchService.searchSingleAttribute(this.base, "ou=test", 2, Arrays.asList("attr1", "attr2"), "hsaIdentity");
    assertEquals(2, result.size());
    assertEquals(this.vgRegionWebServiceMock.filter, "ou=test");
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr1"));
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr2"));
    assertEquals(this.vgRegionWebServiceMock.base, this.base.toString());
    assertEquals(this.vgRegionWebServiceMock.searchScope, "2");
    assertEquals(this.vgRegionWebServiceMock.vgRegionDirectory, VGRegionDirectory.KIV);
  }

  @Test
  public void testSearchUnits() {
    this.kivwsSearchService.searchUnits(this.base, "ou=unit1", 2, Arrays.asList("attr1", "attr2"));
    assertEquals(this.vgRegionWebServiceMock.filter, "ou=unit1");
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr1"));
    assertTrue(this.vgRegionWebServiceMock.attrs.getString().contains("attr2"));
    assertEquals(this.vgRegionWebServiceMock.base, this.base.toString());
    assertEquals(this.vgRegionWebServiceMock.searchScope, "2");
    assertEquals(this.vgRegionWebServiceMock.vgRegionDirectory, VGRegionDirectory.KIV);
  }

  class KivwsMapperMock extends KivwsUnitMapper {
    public KivwsMapperMock() {
      super(null, null);
    }

    public KivwsMapperMock(CodeTablesService codeTablesService, DisplayValueTranslator displayValueTranslator) {
      super(codeTablesService, displayValueTranslator);
    }

    @Override
    public se.vgregion.kivtools.search.domain.Unit mapFromContext(Object ctx) {
      return new se.vgregion.kivtools.search.domain.Unit();
    }
  }

  class VGRegionWebServiceMock implements VGRegionWebServiceImplPortType {
    private final ObjectFactory objectFactory = new ObjectFactory();
    private VGRegionDirectory vgRegionDirectory;
    private String filter;
    private ArrayOfString attrs;
    private boolean throwException;
    private String base;
    private String searchScope;

    @Override
    public String2StringMap getAttributeCodesAndCleartexts(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfDeletedObject getDeletedUnits(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public Function getFunctionAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getFunctionTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public Person getPersonAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public Person getPersonEmploymentAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getPersonTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForEmployment(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForFunction(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForPerson(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForResource() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForServer() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForUnit(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForUnsurePerson() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForEmployment(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForFunction(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForPerson(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForResource() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForServer() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForUnit(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForUnsurePerson() throws VGRException_Exception {
      return null;
    }

    @Override
    public Unit getUnitAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getUnitTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfFunction searchFunction(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      this.shouldThrowException("Exception searchFunction");
      this.base = arg3;
      this.filter = arg0;
      this.attrs = arg1;
      this.vgRegionDirectory = arg2;
      this.searchScope = arg4;

      ArrayOfFunction arrayOfFunction = new ArrayOfFunction();

      // Create mock function
      Function function = new Function();
      String2ArrayOfAnyTypeMap string2ArrayOfAnyTypeMap = new String2ArrayOfAnyTypeMap();
      Entry entry = new String2ArrayOfAnyTypeMap.Entry();
      entry.setKey("hsaIdentity");
      ArrayOfAnyType arrayOfAnyType = new ArrayOfAnyType();
      arrayOfAnyType.getAnyType().add("vakue123");
      entry.setValue(arrayOfAnyType);
      string2ArrayOfAnyTypeMap.getEntry().add(entry);
      function.setAttributes(this.objectFactory.createEmploymentAttributes(string2ArrayOfAnyTypeMap));
      arrayOfFunction.getFunction().add(function);
      return arrayOfFunction;
    }

    @Override
    public ArrayOfPerson searchPerson(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfPerson searchPersonEmployment(String arg0, ArrayOfString arg1, String arg2, ArrayOfString arg3, VGRegionDirectory arg4, String arg5, String arg6) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfResource searchResource(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfServer searchServer(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfUnit searchUnit(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      this.shouldThrowException("Exception searchUnit");
      this.filter = arg0;
      this.vgRegionDirectory = arg2;
      this.attrs = arg1;
      this.base = arg3;
      this.searchScope = arg4;
      ArrayOfUnit arrayOfUnit = new ArrayOfUnit();
      Unit unit = new Unit();
      String2ArrayOfAnyTypeMap createString2ArrayOfAnyTypeMap = this.objectFactory.createString2ArrayOfAnyTypeMap();
      Entry entry = new String2ArrayOfAnyTypeMap.Entry();
      entry.setKey("hsaIdentity");
      ArrayOfAnyType anyType = this.objectFactory.createArrayOfAnyType();
      anyType.getAnyType().add("value123");
      entry.setValue(anyType);
      createString2ArrayOfAnyTypeMap.getEntry().add(entry);
      unit.setAttributes(this.objectFactory.createServerAttributes(createString2ArrayOfAnyTypeMap));
      arrayOfUnit.getUnit().add(unit);
      return arrayOfUnit;
    }

    @Override
    public ArrayOfUnsurePerson searchUnsurePerson(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    private void shouldThrowException(String message) throws VGRException_Exception {
      if (this.throwException) {
        throw new VGRException_Exception(message, new VGRException());
      }
    }

    @Override
    public ArrayOfString getSearchAttributesForDeliveryPoint() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfDeletedObject getDeletedEmployees(String timestamp) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForDeliveryPoint() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfDeliveryPoint searchDeliveryPoint(String filter, ArrayOfString attributes) throws VGRException_Exception {
      return null;
    }
  }
}
