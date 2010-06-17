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

package se.vgregion.kivtools.search.svc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.vgregion.kivtools.util.Arguments;

/**
 * Representation of an entry in a sitemap.
 */
public final class SitemapEntry implements Iterable<Object> {
  private final String location;
  private final String lastModified;
  private final String changeFrequency;
  private final List<Object> extraInformation = new ArrayList<Object>();

  /**
   * Constructs a new SitemapEntry.
   * 
   * @param location the location of the entry.
   * @param lastModified the last modified time of the entry.
   * @param changeFrequency the change frequency of the entry.
   */
  public SitemapEntry(String location, String lastModified, String changeFrequency) {
    Arguments.notNull("location", location);
    this.location = location;
    this.lastModified = lastModified;
    this.changeFrequency = changeFrequency;
  }

  public String getLocation() {
    return location;
  }

  public String getLastModified() {
    return lastModified;
  }

  public String getChangeFrequency() {
    return changeFrequency;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    return prime + location.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    boolean equal = false;

    if (this == obj) {
      equal = true;
    } else {
      if (obj != null) {
        if (getClass() == obj.getClass()) {
          SitemapEntry other = (SitemapEntry) obj;
          equal = location.equals(other.location);
        }
      }
    }
    return equal;
  }

  /**
   * Adds extra information to the sitemap entry.
   * 
   * @param extraInformationObject The object with extra information to add.
   */
  public void addExtraInformation(Object extraInformationObject) {
    this.extraInformation.add(extraInformationObject);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<Object> iterator() {
    return extraInformation.iterator();
  }
}
