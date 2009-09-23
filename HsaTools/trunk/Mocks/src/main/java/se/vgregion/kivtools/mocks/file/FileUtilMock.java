package se.vgregion.kivtools.mocks.file;

import static org.junit.Assert.*;

import java.io.File;

import se.vgregion.kivtools.util.file.FileUtil;
import se.vgregion.kivtools.util.file.FileUtilException;

public class FileUtilMock implements FileUtil {
  private Object content;
  private File fileRead;
  private File fileWrite;
  private File dirCreated;
  private FileUtilException exceptionToThrow;

  public void setContent(Object content) {
    this.content = content;
  }

  public void setExceptionToThrow(FileUtilException exceptionToThrow) {
    this.exceptionToThrow = exceptionToThrow;
  }

  public void assertContent(Object expected) {
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
    return (String) content;
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

  @Override
  @SuppressWarnings("unchecked")
  public <T> T readObjectFromFile(String fileName) {
    fileRead = new File(fileName);
    return (T) content;
  }

  @Override
  public void writeObjectToFile(String fileName, Object object) {
    content = object;
    fileWrite = new File(fileName);
  }

  @Override
  public <T> T readObjectFromFile(File file) {
    fileRead = file;
    return (T) content;
  }

  @Override
  public void writeObjectToFile(File file, Object object) {
    content = object;
    fileWrite = file;
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

  @Override
  public boolean fileExists(String fileName) {
    return content != null;
  }

  @Override
  public boolean fileExists(File file) {
    return content != null;
  }
}
