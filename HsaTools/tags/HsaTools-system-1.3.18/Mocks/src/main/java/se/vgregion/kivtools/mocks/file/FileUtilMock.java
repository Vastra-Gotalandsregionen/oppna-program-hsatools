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

package se.vgregion.kivtools.mocks.file;

import static org.junit.Assert.*;

import java.io.File;

import se.vgregion.kivtools.util.file.FileUtil;
import se.vgregion.kivtools.util.file.FileUtilException;

/**
 * Mock FileUtil implementation to use during unit testing.
 * 
 * @author David Bennehult & Joakim Olsson
 */
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

  /**
   * Asserts that the content written is correct.
   * 
   * @param expected The expected content.
   */
  public void assertContent(Object expected) {
    assertEquals(expected, content);
  }

  /**
   * Asserts that the file that has been read is the correct file.
   * 
   * @param file The expected file object.
   */
  public void assertFileRead(File file) {
    assertEquals(file, fileRead);
  }

  /**
   * Asserts that the file that has been written is the correct file.
   * 
   * @param file The expected file object.
   */
  public void assertFileWrite(File file) {
    assertEquals(file, fileWrite);
  }

  /**
   * Asserts that the directory that has been created is the correct file.
   * 
   * @param file The expected file object.
   */
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
  public void writeFile(String fileName, String fileContent) {
    if (exceptionToThrow != null) {
      throw exceptionToThrow;
    }
    fileWrite = new File(fileName);
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
  @SuppressWarnings("unchecked")
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
