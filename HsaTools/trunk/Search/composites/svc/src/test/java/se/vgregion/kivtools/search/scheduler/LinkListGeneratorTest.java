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
package se.vgregion.kivtools.search.scheduler;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.file.FileUtilMock;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;

/**
 * Made test for this class, but doesn't know if it still is used in production.
 * 
 * @author David
 */
public class LinkListGeneratorTest {
  LinkListGenerator linkListGenerator;
  SearchService mockSearchService;
  List<String> unitList;
  List<String> personsIds;
  private FileUtilMock fileUtilMock;

  @Before
  public void setup() throws Exception {

    unitList = Arrays.asList("U1", "U2", "U3");
    personsIds = Arrays.asList("P1", "P2", "P3");
    mockSearchService = createMock(SearchService.class);
    // record mock
    expect(mockSearchService.getAllUnitsHsaIdentity()).andReturn(unitList);
    expect(mockSearchService.getAllPersonsId()).andReturn(personsIds);
    replay(mockSearchService);

    fileUtilMock = new FileUtilMock();

    linkListGenerator = new LinkListGenerator();
    linkListGenerator.setSearchService(mockSearchService);
    linkListGenerator.setFileUtil(fileUtilMock);
  }

  @Test
  public void testExecuteInternal() {
    linkListGenerator.executeInternal(null);
    this.fileUtilMock
        .assertContent("<html><head><title>Lista med anv�ndare</title></head><body><div><a href=\"http://kivsearch.vgregion.se/kivsearch/visaenhet?hsaidentity=P1\">P1</a></div><div><a href=\"http://kivsearch.vgregion.se/kivsearch/visaenhet?hsaidentity=P2\">P2</a></div><div><a href=\"http://kivsearch.vgregion.se/kivsearch/visaenhet?hsaidentity=P3\">P3</a></div></body></html>");
  }

  @Test
  public void testExecuteInternalException() throws Exception {
    mockSearchService = createMock(SearchService.class);
    // record mock
    expect(mockSearchService.getAllUnitsHsaIdentity()).andThrow(new KivException("Test"));
    replay(mockSearchService);

    linkListGenerator.setSearchService(mockSearchService);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteArrayOutputStream);
    System.setErr(printStream);
    linkListGenerator.executeInternal(null);
    String exceptionResult = byteArrayOutputStream.toString();
    assertEquals(true, exceptionResult.startsWith("Test"));
  }
}
