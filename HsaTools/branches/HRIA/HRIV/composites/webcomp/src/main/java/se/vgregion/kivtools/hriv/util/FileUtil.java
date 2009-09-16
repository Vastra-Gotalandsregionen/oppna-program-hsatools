/**
 * Copyright 2009 Västa Götalandsregionen
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
 */
package se.vgregion.kivtools.hriv.util;

import java.io.File;

/**
 * Utility for reading and writing files.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public interface FileUtil {
  /**
   * Reads the contents of a file in the file system.
   * 
   * @param fileName The absolute filename for the file to read.
   * @return The content of the file as a String.
   * @throws FileUtilException if the file could not be read.
   */
  public String readFile(String fileName);

  /**
   * Reads the contents of a file in the file system.
   * 
   * @param file A File object for the file to read.
   * @return The content of the file as a String.
   * @throws FileUtilException if the file could not be read.
   */
  public String readFile(File file);

  /**
   * Writes the provided fileContent to a file in the file system.
   * 
   * @param fileName The absolute filename for the file to write.
   * @param fileContent The content to be written to the file.
   * @throws FileUtilException if the file could not be written.
   */
  public void writeFile(String fileName, String fileContent);

  /**
   * Writes the provided fileContent to a file in the file system.
   * 
   * @param file A File object for the file to write.
   * @param fileContent The content to be written to the file.
   * @throws FileUtilException if the file could not be written.
   */
  public void writeFile(File file, String fileContent);

  /**
   * Creates the directory with the provided fileName if it doesn't exist.
   * 
   * @param directoryName The name of the directory to create.
   * @throws IllegalArgumentException if the specified directoryName is null.
   */
  public void createDirectoryIfNoExist(String directoryName);

  /**
   * Creates the directory for the provided File object if it doesn't exist.
   * 
   * @param directory The file object describing the directory to create.
   * @throws IllegalArgumentException if the specified directory is null.
   */
  public void createDirectoryIfNoExist(File directory);
}
