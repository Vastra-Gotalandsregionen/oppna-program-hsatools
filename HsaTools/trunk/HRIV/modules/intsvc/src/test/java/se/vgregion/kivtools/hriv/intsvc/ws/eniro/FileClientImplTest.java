package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileClientImplTest {
  private final FileClientImpl client = new FileClientImpl("target");

  @Test
  public void fileIsWrittenCorrectly() throws Exception {
    this.client.sendFile("yadda", "file", "txt");

    File file = new File("target/file.txt");
    assertTrue("file exists", file.exists());
    String fileContent = FileUtils.readFileToString(file);
    assertEquals("file content", "yadda", fileContent);
  }
}
