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
package se.vgregion.kivtools.hriv.servlets;

/**
 * Holds information about a unit needed when generating sitemap. More light weight than full Unit objects.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class UnitSitemapInformation {
  private String hsaId;

  private String modifyTimestampFormattedInW3CDatetimeFormat;
  private String createTimestampFormattedInW3CDatetimeFormat;

  /**
   * Constructs a new UnitSitemapInformation using the provided values.
   * 
   * @param hsaId The hsaIdentity of the unit.
   * @param modifyTimestampFormattedInW3CDatetimeFormat The timestamp when the unit was last modified.
   * @param createTimestampFormattedInW3CDatetimeFormat The timestamp when the unit was created.
   */
  public UnitSitemapInformation(String hsaId, String modifyTimestampFormattedInW3CDatetimeFormat, String createTimestampFormattedInW3CDatetimeFormat) {
    super();
    this.hsaId = hsaId;
    this.modifyTimestampFormattedInW3CDatetimeFormat = modifyTimestampFormattedInW3CDatetimeFormat;
    this.createTimestampFormattedInW3CDatetimeFormat = createTimestampFormattedInW3CDatetimeFormat;
  }

  public String getCreateTimestampFormattedInW3CDatetimeFormat() {
    return createTimestampFormattedInW3CDatetimeFormat;
  }

  public void setCreateTimestampFormattedInW3CDatetimeFormat(String createTimestampFormattedInW3CDatetimeFormat) {
    this.createTimestampFormattedInW3CDatetimeFormat = createTimestampFormattedInW3CDatetimeFormat;
  }

  public String getModifyTimestampFormattedInW3CDatetimeFormat() {
    return modifyTimestampFormattedInW3CDatetimeFormat;
  }

  public void setModifyTimestampFormattedInW3CDatetimeFormat(String modifyTimestampFormattedInW3CDatetimeFormat) {
    this.modifyTimestampFormattedInW3CDatetimeFormat = modifyTimestampFormattedInW3CDatetimeFormat;
  }

  public String getHsaId() {
    return hsaId;
  }

  public void setHsaId(String hsaId) {
    this.hsaId = hsaId;
  }
}
