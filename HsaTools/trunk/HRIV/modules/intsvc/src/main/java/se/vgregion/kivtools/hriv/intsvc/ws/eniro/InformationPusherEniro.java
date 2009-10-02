package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;

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
 
  // /**
  // * Constants for Eniro operation.
  // *
  // * @author David Bennehult & Joakim Olsson
  // *
  // */
  // private enum Operation {
  // CREATE("create"), MOVE("move"), REMOVE("remove"), UPDATE("update");
  // private String value;
  //
  // Operation(String value) {
  // this.value = value;
  // }
  // }
  //
  // /**
  // *
  // * @author david
  // *
  // */
  // private enum LOAD_TYPE {
  // FULL("Full"), INCREMENT("Increment");
  // private String value;
  //
  // private LOAD_TYPE(String value) {
  // this.value = value;
  // }
  // }
  
  

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
    // Get units that belongs to the organization.
    List<UnitComposition> collectedUnits = getUnitsFromLdap();
    // Generate organization tree object.
    Organization organization = eniroOrganisationBuilder.generateOrganisation(collectedUnits);
    // create XML presentation of organization tree object.
    String generatedUnitDetailsXmlFile = generateUnitDetailsXmlFile(organization);
    sendFileToFtpServer(generatedUnitDetailsXmlFile);
  }

  private void sendFileToFtpServer(String generatedUnitDetailsXmlFile) {
    if (ftpClient.sendFile(generatedUnitDetailsXmlFile)) {
      logger.info("Unit details pusher: Completed with success.");
    } else {
      logger.error("Unit details pusher: Completed with failure.");
    }
  }

  /**
   * Generate xml file with newly updated units.
   * 
   */
  private String generateUnitDetailsXmlFile(Organization organization) {
    StringWriter fileContent = new StringWriter();
    try {
      JAXBContext context = JAXBContext.newInstance(organization.getClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.marshal(organization, fileContent);
    } catch (JAXBException e) {
      logger.error("Error in doService", e);
    }
    return fileContent.toString();
  }

  private List<UnitComposition> getUnitsFromLdap() {
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    Filter healthcareTypeFilter = KivLdapFilterHelper.createHealthcareTypeFilter(healthcareTypeConditionHelper.getAllHealthcareTypes());
    AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter("hsaMunicipalityCode", 1480));
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
