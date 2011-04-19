package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class FileClientImplTest {
  private final FileClientImpl client = new FileClientImpl("target");

  @Before
  public void setUp() {
    TimeUtil.setTimeSource(new TimeSource() {

      @Override
      public long millis() {
        try {
          return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2011-04-19 22:20:06").getTime();
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  @Test
  public void fileIsWrittenCorrectly() throws Exception {
    this.client.sendFile("yadda", "file", "txt");

    File file = new File("target/file-20110419222006.txt");
    assertTrue("file exists", file.exists());
    String fileContent = FileUtils.readFileToString(file);
    assertEquals("file content", "yadda", fileContent);
  }
}
