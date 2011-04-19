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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import se.vgregion.kivtools.util.time.TimeUtil;
import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;

public class FileClientImpl implements FtpClient {
  private final String directory;

  public FileClientImpl(final String directory) {
    this.directory = directory;
  }

  @Override
  public boolean sendFile(String fileContent, String basename, String suffix) {
    try {
      FileWriter writer = new FileWriter(this.directory + File.separator + basename + "-" + TimeUtil.getCurrentTimeFormatted(DateTimeFormat.SCIENTIFIC_TIME) + "." + suffix);
      writer.write(fileContent);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }
}
