package se.vgregion.kivtools.util.file;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileUtilExceptionTest {

  @Test
  public void testFileUtilException() {
    FileUtilException exception = new FileUtilException();
    assertNull(exception.getMessage());
  }

  @Test
  public void testFileUtilExceptionStringThrowable() {
    FileUtilException exception = new FileUtilException("Test", new RuntimeException("RuntimeTest"));
    assertEquals("Test", exception.getMessage());
    assertEquals(RuntimeException.class, exception.getCause().getClass());
    assertEquals("RuntimeTest", exception.getCause().getMessage());
  }

  @Test
  public void testFileUtilExceptionString() {
    FileUtilException exception = new FileUtilException("Test");
    assertEquals("Test", exception.getMessage());
  }

  @Test
  public void testFileUtilExceptionThrowable() {
    FileUtilException exception = new FileUtilException(new RuntimeException("RuntimeTest"));
    assertEquals(RuntimeException.class, exception.getCause().getClass());
    assertEquals("RuntimeTest", exception.getCause().getMessage());
  }
}
