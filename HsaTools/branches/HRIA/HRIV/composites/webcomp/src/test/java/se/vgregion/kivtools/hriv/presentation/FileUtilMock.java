package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.io.File;

import se.vgregion.kivtools.hriv.util.FileUtil;
import se.vgregion.kivtools.hriv.util.FileUtilException;

public class FileUtilMock implements FileUtil {
  private String content;
  private File fileRead;
  private File fileWrite;
  private File dirCreated;
  private FileUtilException exceptionToThrow;

  public void setContent(String content) {
    this.content = content;
  }

  public void setExceptionToThrow(FileUtilException exceptionToThrow) {
    this.exceptionToThrow = exceptionToThrow;
  }

  public void assertContent(String expected) {
    assertEquals(expected, content);
  }

  public void assertFileRead(File file) {
    assertEquals(file, fileRead);
  }

  public void assertFileWrite(File file) {
    assertEquals(file, fileWrite);
  }

  public void assertDirCreated(File file) {
    assertEquals(file, dirCreated);
  }

  @Override
  public String readFile(File file) {
    if (exceptionToThrow != null) {
      throw exceptionToThrow;
    }
    fileRead = file;
    return content;
  }

  @Override
  public void writeFile(File file, String fileContent) {
    if (exceptionToThrow != null) {
      throw exceptionToThrow;
    }
    fileWrite = file;
    content = fileContent;
  }

  @Override
  public void createDirectoryIfNoExist(File directory) {
    dirCreated = directory;
  }

  // Not implemented methods

  @Override
  public String readFile(String fileName) {
    return null;
  }

  @Override
  public void writeFile(String fileName, String fileContent) {
  }

  @Override
  public void createDirectoryIfNoExist(String directoryName) {
  }
}
