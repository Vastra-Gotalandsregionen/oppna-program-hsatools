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

package se.vgregion.kivtools.util.file;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class FileUtilImplTest {
  private static final String TARGET_TESTFILE = "target/testfile";
  private static final String TARGET_TESTDIR = "target/testdir/testdir2";
  private FileUtilImpl fileUtilImpl;
  private File nonExistingFile;
  private File testDirectory;
  private Map<String, Object> mapToReadWrite = new HashMap<String, Object>();
  private File testFile;

  @Before
  public void setUp() {
    fileUtilImpl = new FileUtilImpl();
    nonExistingFile = new File("this_directory_does_not_exist/dummy_file");
    testDirectory = new File(TARGET_TESTDIR);
    testDirectory.delete();
    testFile = new File(TARGET_TESTFILE);
    testFile.delete();
    mapToReadWrite.put("test1", "teststring");
    mapToReadWrite.put("test2", Integer.valueOf(123));
  }

  @Test
  public void testInstantiation() {
    FileUtilImpl fileUtilImpl = new FileUtilImpl();
    assertNotNull(fileUtilImpl);
  }

  @Test
  public void testReadFileString() {
    String content = fileUtilImpl.readFile("target/test-classes/testfile.log");
    assertNotNull(content);
    assertEquals("Testfile content\n", content);
  }

  @Test
  public void testReadFileWithException() {
    try {
      fileUtilImpl.readFile(nonExistingFile);
      fail("RuntimeException expected");
    } catch (RuntimeException e) {
      // Expected exception
    }
  }

  @Test
  public void testWriteFileString() {
    fileUtilImpl.writeFile(TARGET_TESTFILE, "abc");

    String content = fileUtilImpl.readFile(TARGET_TESTFILE);
    assertNotNull(content);
    assertEquals("abc", content);
  }

  @Test
  public void testWriteFileWithException() {
    try {
      fileUtilImpl.writeFile(nonExistingFile, "abc");
      fail("RuntimeException expected");
    } catch (RuntimeException e) {
      // Expected exception
    }
  }

  @Test
  public void testCreateDirectoryIfNotExist() {
    assertFalse(testDirectory.exists());

    fileUtilImpl.createDirectoryIfNoExist(TARGET_TESTDIR);

    assertTrue(testDirectory.exists());

    // Run one more time to get 100% conditional coverage
    fileUtilImpl.createDirectoryIfNoExist(TARGET_TESTDIR);

    assertTrue(testDirectory.exists());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateDirectoryIfNotExistNullFileInput() {
    fileUtilImpl.createDirectoryIfNoExist((File) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateDirectoryIfNotExistNullStringInput() {
    fileUtilImpl.createDirectoryIfNoExist((String) null);
  }

  @Test
  public void testReadWriteObject() {
    fileUtilImpl.writeObjectToFile(TARGET_TESTFILE, mapToReadWrite);
    Map<String, Object> readMap = fileUtilImpl.readObjectFromFile(TARGET_TESTFILE);
    assertEquals(mapToReadWrite, readMap);
    // Test with File-object as well.
    readMap = fileUtilImpl.readObjectFromFile(new File(TARGET_TESTFILE));
    assertEquals(mapToReadWrite, readMap);
  }

  @Test(expected = FileUtilException.class)
  public void testWriteObjectFromFileWithException() {
    fileUtilImpl.writeObjectToFile(nonExistingFile.getAbsolutePath(), mapToReadWrite);
  }

  @Test(expected = FileUtilException.class)
  public void testReadObjectFromFileWithException() {
    fileUtilImpl.readObjectFromFile(nonExistingFile.getAbsolutePath());
  }

  @Test(expected = FileUtilException.class)
  public void testReadObjectFromFileClassNotFound() {
    fileUtilImpl.readObjectFromFile("src/test/resources/streamedDummyClass.bin");
  }

  @Test
  public void testFileExists() {
    assertTrue(fileUtilImpl.fileExists("target/test-classes/testxml/emptydoc.xml"));
  }

  @Test
  public void testFileExistsNonExisting() {
    assertFalse(fileUtilImpl.fileExists(nonExistingFile));
  }
}
