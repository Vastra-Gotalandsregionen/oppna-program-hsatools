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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

/**
 * Default iplementation of FileUtil.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public class FileUtilImpl implements FileUtil {

  /**
   * {@inheritDoc}
   */
  @Override
  public String readFile(String fileName) {
    return readFile(new File(fileName));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String readFile(File file) {
    StringWriter writer = new StringWriter();
    try {
      Reader reader = new FileReader(file);
      char[] buff = new char[1024];
      int readLen = -1;
      while ((readLen = reader.read(buff)) != -1) {
        writer.write(buff, 0, readLen);
      }
    } catch (IOException e) {
      throw new FileUtilException(e);
    }
    return writer.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeFile(String fileName, String fileContent) {
    writeFile(new File(fileName), fileContent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeFile(File file, String fileContent) {
    try {
      FileWriter writer = new FileWriter(file);
      writer.write(fileContent);
      writer.close();
    } catch (IOException e) {
      throw new FileUtilException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createDirectoryIfNoExist(String directoryName) {
    if (directoryName == null) {
      throw new IllegalArgumentException("Parameter directoryName may not be null");
    }
    createDirectoryIfNoExist(new File(directoryName));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createDirectoryIfNoExist(File directory) {
    if (directory == null) {
      throw new IllegalArgumentException("Parameter directory may not be null");
    }
    if (!directory.exists()) {
      directory.mkdir();
    }
  }
}
