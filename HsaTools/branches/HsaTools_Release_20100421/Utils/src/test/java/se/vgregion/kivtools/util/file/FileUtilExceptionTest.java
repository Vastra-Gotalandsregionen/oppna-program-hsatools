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
