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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.EniroOrganisationBuilder;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.EniroUnitMapper;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.KivLdapFilterHelper;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.utils.XmlMarshaller;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;

/**
 * 
 * @author david
 * 
 */
public class InformationPusherEniro implements InformationPusher {

  private String[] allowedUnitBusinessClassificationCodes;
  private String[] otherCareTypeBusinessCodes;
  private Log logger = LogFactory.getLog(this.getClass());
  private FtpClient ftpClient;
  private LdapTemplate ldapTemplate;
  private EniroOrganisationBuilder eniroOrganisationBuilder;

  @Required
  public void setFtpClient(FtpClient ftpClient) {
    this.ftpClient = ftpClient;
  }

  public void setAllowedUnitBusinessClassificationCodes(String[] allowedUnitBusinessClassificationCodes) {
    this.allowedUnitBusinessClassificationCodes = allowedUnitBusinessClassificationCodes;
  }

  public void setOtherCareTypeBusinessCodes(String[] otherCareTypeBusinessCodes) {
    this.otherCareTypeBusinessCodes = otherCareTypeBusinessCodes;
  }

  public void setEniroOrganisationBuilder(EniroOrganisationBuilder eniroOrganisationBuilder) {
    this.eniroOrganisationBuilder = eniroOrganisationBuilder;
  }

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * Trigger service for generate unit tree xml file and push it to chosen ftp server.
   */
  public void doService() {
    boolean success = true;

    success &= handleLocality("Göteborg", Arrays.asList("1440", "1480", "1401", "1488", "1441", "1463", "1481", "1402", "1415", "1407"), "Vastra Gotalandsregionen Goteborg");
    success &= handleLocality("Borås", Arrays.asList("1489", "1443", "1490", "1466", "1463", "1465", "1452", "1491", "1442"), "Vastra Gotalandsregionen Boras");
    success &= handleLocality("Uddevalla", Arrays.asList("1460", "1438", "1439", "1462", "1484", "1461", "1430", "1421", "1427", "1486", "1435", "1419", "1488", "1485", "1487", "1492"),
        "Vastra Gotalandsregionen Uddevalla");
    success &= handleLocality("Skövde", Arrays.asList("1445", "1499", "1444", "1447", "1471", "1497", "1446", "1494", "1493", "1495", "1496", "1472", "1498", "1473", "1470"),
        "Vastra Gotalandsregionen Skovde");

    if (success) {
      logger.info("Unit details pusher: Completed with success.");
    } else {
      logger.error("Unit details pusher: Completed with failure.");
    }
  }

  private boolean handleLocality(final String locality, final List<String> municipalities, final String basename) {
    boolean success = true;

    // Get units that belongs to the organization.
    List<UnitComposition> units = getUnitsFromLdap(municipalities);
    if (!units.isEmpty()) {
      updateUnitsWithLocality(units, locality);

      // Generate organization tree object.
      Organization organization = eniroOrganisationBuilder.generateOrganisation(units);
      organization.setId("232100-0131 VGR " + locality);
      organization.setName("Västra Götalandsregionen " + locality);

      // create XML presentation of organization tree object.
      String generatedUnitDetailsXmlFile = XmlMarshaller.generateXmlContentOfObject(organization);

      success = sendFileToFtpServer(generatedUnitDetailsXmlFile, basename, "xml");
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
    return ftpClient.sendFile(generatedUnitDetailsXmlFile, basename, suffix);
  }

  private List<UnitComposition> getUnitsFromLdap(List<String> municipalityCodes) {
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    Filter healthcareTypeFilter = KivLdapFilterHelper.createHealthcareTypeFilter(healthcareTypeConditionHelper.getAllHealthcareTypes());
    AndFilter andFilter = new AndFilter();
    andFilter.and(createMunicipalityFilter(municipalityCodes));
    OrFilter orBusinessCodes = new OrFilter();
    for (String businessCode : allowedUnitBusinessClassificationCodes) {
      orBusinessCodes.or(new EqualsFilter("hsaBusinessClassificationCode", businessCode));
    }
    orBusinessCodes.or(healthcareTypeFilter);
    andFilter.and(orBusinessCodes);
    EniroUnitMapper eniroUnitMapper = new EniroUnitMapper(Arrays.asList(otherCareTypeBusinessCodes));
    @SuppressWarnings("unchecked")
    List<UnitComposition> unitsList = ldapTemplate.search("", andFilter.encode(), SearchControls.SUBTREE_SCOPE, eniroUnitMapper);
    setParentIdsForUnits(unitsList);
    return unitsList;
  }

  private Filter createMunicipalityFilter(List<String> municipalityCodes) {
    OrFilter filter = new OrFilter();

    for (String municipalityCode : municipalityCodes) {
      filter.or(new EqualsFilter("hsaMunicipalityCode", municipalityCode));
    }
    return filter;
  }

  private void setParentIdsForUnits(List<UnitComposition> compositions) {
    Map<String, UnitComposition> unitsMap = new HashMap<String, UnitComposition>();
    for (UnitComposition unitComposition : compositions) {
      unitsMap.put(unitComposition.getDn(), unitComposition);
    }
    for (UnitComposition unitComposition : compositions) {
      UnitComposition parentUnit = unitsMap.get(unitComposition.getParentDn());
      if (parentUnit != null) {
        unitComposition.getEniroUnit().setParentUnitId(parentUnit.getEniroUnit().getId());
      }
    }
  }
}
