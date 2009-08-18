package se.vgregion.kivtools.search.scheduler;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.SearchService;
/**
 * Made test for this class, but doesn't know if it still is used in production.
 * @author david
 *
 */
public class LinkListGeneratorTest {
  LinkListGenerator linkListGenerator;
  SearchService mockSearchService;
  List<String> unitList;
  List<String> personsIds;
  

  @Before
  public void setup() throws Exception {
   
    unitList = Arrays.asList("U1", "U2", "U3");
    personsIds = Arrays.asList("P1","P2","P3");
    mockSearchService = createMock(SearchService.class);
    // record mock
    expect(mockSearchService.getAllUnitsHsaIdentity()).andReturn(unitList);
    expect(mockSearchService.getAllPersonsId()).andReturn(personsIds);
    replay(mockSearchService);
    
    linkListGenerator = new LinkListGenerator();
    linkListGenerator.setSearchService(mockSearchService);
    // Don't know why there is a file set property
    linkListGenerator.setFile(new File("test"));
  }
 
  
  
  @Test
  public void testExecuteInternal() {
    File checkFile = new File(System.getProperty("user.dir"), "units.html");
    // If file already exists it must be deleted before test.
    if (checkFile.exists()) {
      assertTrue(checkFile.delete());
    }
    linkListGenerator.executeInternal(null);
    assertEquals(true, checkFile.exists());
  }
  
  @Test
  public void testExecuteInternalException() throws IOException{
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteArrayOutputStream);
    System.setErr(printStream);
    linkListGenerator.setSearchService(null);
    linkListGenerator.executeInternal(null);
    String exceptionResult = byteArrayOutputStream.toString();
    assertEquals(true, exceptionResult.startsWith("java.lang.NullPointerException"));
  }

}
