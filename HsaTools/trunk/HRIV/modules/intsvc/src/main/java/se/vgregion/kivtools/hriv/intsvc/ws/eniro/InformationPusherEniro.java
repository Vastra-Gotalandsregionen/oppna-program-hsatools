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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.utils.XmlMarshaller;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;

/**
 * 
 * @author david
 * 
 */
public class InformationPusherEniro implements InformationPusher {
  private UnitFetcher unitFetcher;
  private EniroConfiguration eniroConfiguration;
  private final Log logger = LogFactory.getLog(this.getClass());
  private FtpClient ftpClient;
  private EniroOrganisationBuilder eniroOrganisationBuilder;

  public void setUnitFetcher(UnitFetcher unitFetcher) {
    this.unitFetcher = unitFetcher;
  }

  @Required
  public void setFtpClient(FtpClient ftpClient) {
    this.ftpClient = ftpClient;
  }

  public void setEniroConfiguration(EniroConfiguration eniroConfiguration) {
    this.eniroConfiguration = eniroConfiguration;
  }

  public void setEniroOrganisationBuilder(EniroOrganisationBuilder eniroOrganisationBuilder) {
    this.eniroOrganisationBuilder = eniroOrganisationBuilder;
  }

  /**
   * Trigger service for generate unit tree xml file and push it to chosen ftp server.
   */
  @Override
  public void doService() {
    boolean success = true;

    for (AreaConfig config : this.eniroConfiguration.getConfiguration()) {
      success &= this.handleLocality(config);
    }

    if (success) {
      this.logger.info("Unit details pusher: Completed with success.");
    } else {
      this.logger.error("Unit details pusher: Completed with failure.");
    }
  }

  private boolean handleLocality(final AreaConfig config) {
    boolean success = true;

    // Get units that belongs to the organization.
    List<UnitComposition> units = this.unitFetcher.fetchUnits(config.getMunicipalities());
    if (!units.isEmpty()) {
      this.updateUnitsWithLocality(units, config.getLocality());

      // Generate organization tree object.
      Organization organization = this.eniroOrganisationBuilder.generateOrganisation(units);
      organization.setId(config.getOrganizationid());
      organization.setName(config.getOrganizationName());

      // create XML presentation of organization tree object.
      String generatedUnitDetailsXmlFile = XmlMarshaller.generateXmlContentOfObject(organization);

      success = this.sendFileToFtpServer(generatedUnitDetailsXmlFile, config.getBasename(), "xml");
    }

    return success;
  }

  /**
   * Updates all units in the provided list with the provided locality.
   * 
   * @param units The list of units to update with locality.
   * @param locality The locality to set on the units.
   */
  private void updateUnitsWithLocality(List<UnitComposition> units, String locality) {
    for (UnitComposition unitComposition : units) {
      unitComposition.getEniroUnit().setLocality(locality);
    }
  }

  private boolean sendFileToFtpServer(String generatedUnitDetailsXmlFile, final String basename, final String suffix) {
    return this.ftpClient.sendFile(generatedUnitDetailsXmlFile, basename, suffix);
  }
}
