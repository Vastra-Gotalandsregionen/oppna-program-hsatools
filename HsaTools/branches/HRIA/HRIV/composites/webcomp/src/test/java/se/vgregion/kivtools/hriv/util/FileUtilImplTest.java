package se.vgregion.kivtools.hriv.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilImplTest {
  private FileUtilImpl fileUtilImpl;
  private File nonExistingFile;
  private File testDirectory;

  @Before
  public void setUp() {
    fileUtilImpl = new FileUtilImpl();
    nonExistingFile = new File("this_directory_does_not_exist/dummy_file");
    testDirectory = new File("target/testdir");
    testDirectory.delete();
  }

  @After
  public void tearDown() {
    testDirectory.delete();
  }

  @Test
  public void testInstantiation() {
    FileUtilImpl fileUtilImpl = new FileUtilImpl();
    assertNotNull(fileUtilImpl);
  }

  @Test
  public void testReadFileString() {
    String content = fileUtilImpl.readFile("target/test-classes/testxml/emptydoc.xml");
    assertNotNull(content);
    assertEquals("<?xml version=\"1.0\"?>\n<Node id=\"aa\">\n</Node>", content);
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
    fileUtilImpl.writeFile("target/testfile", "abc");

    String content = fileUtilImpl.readFile("target/testfile");
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

    fileUtilImpl.createDirectoryIfNoExist("target/testdir");

    assertTrue(testDirectory.exists());

    // Run one more time to get 100% conditional coverage
    fileUtilImpl.createDirectoryIfNoExist("target/testdir");

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
}
