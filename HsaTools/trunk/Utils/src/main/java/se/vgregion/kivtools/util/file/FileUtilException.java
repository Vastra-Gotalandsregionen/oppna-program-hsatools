/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.util.file;

/**
 * Runtime exception thrown from FileUtil.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public class FileUtilException extends RuntimeException {
  private static final long serialVersionUID = -860959014068277675L;

  /**
   * Constructs a new FileUtilException.
   */
  public FileUtilException() {
    super();
  }

  /**
   * Constructs a new FileUtilException using the provided message and cause.
   * 
   * @param message The message to use for this exception.
   * @param cause The Throwable that caused this exception.
   */
  public FileUtilException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new FileUtilException using the provided message.
   * 
   * @param message The message to use for this exception.
   */
  public FileUtilException(String message) {
    super(message);
  }

  /**
   * Constructs a new FileUtilException using the provided cause.
   * 
   * @param cause The Throwable that caused this exception.
   */
  public FileUtilException(Throwable cause) {
    super(cause);
  }
}
