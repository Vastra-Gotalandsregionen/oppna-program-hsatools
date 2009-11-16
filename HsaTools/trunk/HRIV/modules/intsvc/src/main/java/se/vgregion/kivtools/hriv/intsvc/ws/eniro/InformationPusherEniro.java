package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
   * 
   * @inheritDoc
   */
  public void doService() {
    Map<String, List<String>> localityMunicipalityMap = new HashMap<String, List<String>>();
    localityMunicipalityMap.put("Göteborg", Arrays.asList("1440", "1480", "1401", "1488", "1441", "1463", "1481", "1402", "1415", "1407"));
    localityMunicipalityMap.put("Borås", Arrays.asList("1489", "1443", "1490", "1466", "1463", "1465", "1452", "1491", "1442"));
    localityMunicipalityMap.put("Uddevalla", Arrays.asList("1460", "1438", "1439", "1462", "1484", "1461", "1430", "1421", "1427", "1486", "1435", "1419", "1488", "1485", "1487", "1492"));
    localityMunicipalityMap.put("Skövde", Arrays.asList("1445", "1499", "1444", "1447", "1471", "1497", "1446", "1494", "1493", "1495", "1496", "1472", "1498", "1473", "1470"));

    // Get units that belongs to the organization.
    List<UnitComposition> collectedUnits = new ArrayList<UnitComposition>();

    for (Entry<String, List<String>> entry : localityMunicipalityMap.entrySet()) {
      List<UnitComposition> units = getUnitsFromLdap(entry.getValue());
      updateUnitsWithLocality(units, entry.getKey());
      collectedUnits.addAll(units);
    }

    // Generate organization tree object.
    Organization organization = eniroOrganisationBuilder.generateOrganisation(collectedUnits);
    // create XML presentation of organization tree object.
    String generatedUnitDetailsXmlFile = XmlMarshaller.generateXmlContentOfObject(organization);
    sendFileToFtpServer(generatedUnitDetailsXmlFile);
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

  private void sendFileToFtpServer(String generatedUnitDetailsXmlFile) {
    if (ftpClient.sendFile(generatedUnitDetailsXmlFile)) {
      logger.info("Unit details pusher: Completed with success.");
    } else {
      logger.error("Unit details pusher: Completed with failure.");
    }
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
