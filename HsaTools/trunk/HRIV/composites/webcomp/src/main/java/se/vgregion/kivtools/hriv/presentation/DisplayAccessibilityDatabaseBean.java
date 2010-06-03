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

package se.vgregion.kivtools.hriv.presentation;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.hriv.presentation.forms.AccessibilityDatabaseFilterForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityObject;
import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityPackage;
import se.vgregion.kivtools.search.domain.values.accessibility.Block;
import se.vgregion.kivtools.search.domain.values.accessibility.Criteria;
import se.vgregion.kivtools.util.dom.DocumentHelper;
import se.vgregion.kivtools.util.http.HttpFetcher;

/**
 * Support for accessibility information for units.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
@SuppressWarnings("serial")
public class DisplayAccessibilityDatabaseBean implements Serializable {
  private HttpFetcher httpFetcher;
  private Log logger = LogFactory.getLog(this.getClass());
  private Boolean useAccessibilityDatabaseIntegration = Boolean.TRUE;
  private String accessibilityDatabaseIntegrationGetIdUrl;
  private String accessibilityDatabaseIntegrationGetInfoUrl;

  /**
   * Look up database accessibility information for specified unit.
   * 
   * @param unit The unit which should get accessibility info assigned to it.
   * @param form The AccessibilityDatabaseFilterForm to fetch the language id to use.
   */
  public void assignAccessibilityDatabaseInfo(Unit unit, AccessibilityDatabaseFilterForm form) {
    if (this.useAccessibilityDatabaseIntegration) {
      // First find out the accessibility database id
      if (assignAccessibilityDatabaseId(unit)) {

        int languageId = Integer.parseInt(form.getLanguageId());

        String url = accessibilityDatabaseIntegrationGetInfoUrl + languageId + "&facilityId=" + unit.getAccessibilityDatabaseId();

        String content = httpFetcher.fetchUrl(url);

        // Now read the content and get accessibility info
        Document doc = DocumentHelper.getDocumentFromString(content);
        doc.normalize();

        // Get business object (there is only one)
        NodeList businessObjectNodeList = doc.getElementsByTagName("businessobject");
        AccessibilityObject businessObject = null;
        for (int i = 0; i < businessObjectNodeList.getLength(); i++) {
          businessObject = AccessibilityObject.createAccessibilityObjectFromNode(businessObjectNodeList.item(i));
        }

        // Get sub objects
        NodeList subObjectNodeList = doc.getElementsByTagName("subobject");
        ArrayList<AccessibilityObject> subObjects = new ArrayList<AccessibilityObject>();
        for (int i = 0; i < subObjectNodeList.getLength(); i++) {
          AccessibilityObject subObject = AccessibilityObject.createAccessibilityObjectFromNode(subObjectNodeList.item(i));
          subObjects.add(subObject);
        }

        AccessibilityInformation accessibilityInformation = new AccessibilityInformation(businessObject, subObjects);
        unit.setAccessibilityInformation(accessibilityInformation);
      }
    }
  }

  /**
   * Find out and assign the accessibility database id.
   * 
   * @param u The Unit to assign accessibility information to.
   * @return true if succeeded, false otherwise
   */
  public boolean assignAccessibilityDatabaseId(Unit u) {
    boolean result = true;

    if (this.useAccessibilityDatabaseIntegration) {
      // Get accessibility Id
      String url = accessibilityDatabaseIntegrationGetIdUrl + u.getHsaIdentity();
      String content = httpFetcher.fetchUrl(url);

      // Now read the content into a XML document and get accessibility id
      Document doc = DocumentHelper.getDocumentFromString(content);
      try {
        NodeList ids = doc.getElementsByTagName("string");
        for (int i = 0; i < ids.getLength(); i++) {
          String textContent = ids.item(i).getTextContent();
          int accessabilityId = Integer.parseInt(textContent);
          u.setAccessibilityDatabaseId(accessabilityId);
        }
      } catch (NumberFormatException e) {
        logger.error("We did not get a valid accessability database id. Skip it.");
        result = false;
      }
    }
    return result;
  }

  /**
   * Filters the accessibility info depending on the form input. Eg "stairs" are maybe only relevant for some disabled individuals and not for others.
   * 
   * @param u The Unit to filter accessibility information for.
   * @param form The form with the input parameters.
   */
  public void filterAccessibilityDatabaseInfo(Unit u, AccessibilityDatabaseFilterForm form) {
    // We need to keep track of submissions in order to know when to show "no result".
    form.setSubmitted(true);

    // If language has changed we need to download new data
    if (!form.getLanguageId().equals(form.getFormerLanguageId())) {
      assignAccessibilityDatabaseInfo(u, form);
      form.setFormerLanguageId(form.getLanguageId());
    }

    // Create array with selected disabilities
    boolean[] selectedDisabilities = new boolean[5];
    setAllDisabilitiesWhenNoSelection(selectedDisabilities, form);
   
   // Attentive or Available?
    String listType = form.getListType();
    boolean attentive = "attentive".equals(listType);

    if (u.getAccessibilityInformation() != null) {
      // Business object
      AccessibilityObject businessObject = u.getAccessibilityInformation().getBusinessObject();
      updateCriteriasInAccesibilityObject(businessObject, selectedDisabilities, attentive);

      // Sub objects
      for (AccessibilityObject subObject : u.getAccessibilityInformation().getSubObjects()) {
        updateCriteriasInAccesibilityObject(subObject, selectedDisabilities, attentive);
      }
    }
  }

  private void setAllDisabilitiesWhenNoSelection(boolean[] selectedDisabilities, AccessibilityDatabaseFilterForm form){
	  if(form.getHear()==false && form.getSee()==false && form.getMove()==false && form.getSubstances()==false && form.getInfo()==false){
		  
		  for(int i = 0; i<selectedDisabilities.length; i++){
			  selectedDisabilities[i] = true;
		  }
		  
		  form.setHear(true);
		  form.setSee(true);
		  form.setMove(true);
		  form.setSubstances(true);
		  form.setInfo(true);
		  
	  }else{
		  	selectedDisabilities[0] = form.getHear();
		    selectedDisabilities[1] = form.getSee();
		    selectedDisabilities[2] = form.getMove();
		    selectedDisabilities[3] = form.getSubstances();
		    selectedDisabilities[4] = form.getInfo();

	  }
	
  }
  /**
   * Gets the message bundle for the provided language.
   * 
   * @param languageId The id of the language to get a message bundle for.
   * @return The message bundle for the provided language id.
   */
  public Properties getMessageBundle(int languageId) {
    Properties properties = null;

    switch (languageId) {
      case 1:
        // Fall through
      case 5:
        properties = loadProperties("tdb_messages.properties");
        break;
      case 4:
        properties = loadProperties("tdb_messages_de.properties");
        break;
      default:
        properties = loadProperties("tdb_messages_en.properties");
    }

    return properties;
  }

  /**
   * Helper method for reading properties-files. Removes the need to catch IOException in calling code.
   * 
   * @param resource The name of the resource to read.
   * @return A populated Properties object or null if the provided resource could not be read.
   */
  private Properties loadProperties(String resource) {
    Properties properties = null;

    try {
      Resource res = new UrlResource(this.getClass().getResource(resource));
      properties = PropertiesLoaderUtils.loadProperties(res);
    } catch (IOException e) {
      logger.debug("Unable to read properties file for resource '" + resource + "'");
    }

    return properties;
  }

  /**
   * Updates criterias in the retrieved AccessibilityObjects based on if we should be attentive to the users selected disabilities and the users selected disabilities.
   * 
   * @param accessibilityObject The AccessibilityObject to update criterias in.
   * @param selectedDisabilities The users selected disabilities.
   * @param attentive True if we should be attentive to the users selected disabilities.
   */
  private void updateCriteriasInAccesibilityObject(AccessibilityObject accessibilityObject, boolean[] selectedDisabilities, boolean attentive) {
    for (Block b : accessibilityObject.getBlocks()) {
      for (AccessibilityPackage p : b.getPackages()) {
        for (Criteria c : p.getCriterias()) {
          // Take Attentive/Available choice into consideration.
          // Request should match the criteria's "attentive/available belonging".
          // We should not show hidden criterias either.
          if (!(attentive == c.isNotice()) || c.isHidden()) {
            c.setShow(false);
            continue;
          }

          boolean disabilities = checkDisabilities(c, selectedDisabilities);
          c.setShow(disabilities);
        }
      }
    }
  }

  /**
   * Checks if the provided criteria has any of the selected disabilities.
   * 
   * @param criteria Criteria to check disabilities for.
   * @param selectedDisabilities The selected disabilities to look for.
   * @return True if any of the selected disabilities are found.
   */
  private boolean checkDisabilities(Criteria criteria, boolean[] selectedDisabilities) {
    boolean result = false;

    for (String disability : criteria.getDisabilities()) {
      // Since we don't want to add the accessibility information multiple times, move on to next criteria when it is added.
      result |= "hear".equals(disability) && selectedDisabilities[0];
      result |= "see".equals(disability) && selectedDisabilities[1];
      result |= "move".equals(disability) && selectedDisabilities[2];
      result |= "substances".equals(disability) && selectedDisabilities[3];
      result |= "information".equals(disability) && selectedDisabilities[4];
    }

    return result;
  }

  public void setUseAccessibilityDatabaseIntegration(Boolean useAccessibilityDatabaseIntegration) {
    this.useAccessibilityDatabaseIntegration = useAccessibilityDatabaseIntegration;
  }

  public void setAccessibilityDatabaseIntegrationGetIdUrl(String accessibilityDatabaseIntegrationGetIdUrl) {
    this.accessibilityDatabaseIntegrationGetIdUrl = accessibilityDatabaseIntegrationGetIdUrl;
  }

  public void setAccessibilityDatabaseIntegrationGetInfoUrl(String accessibilityDatabaseIntegrationGetInfoUrl) {
    this.accessibilityDatabaseIntegrationGetInfoUrl = accessibilityDatabaseIntegrationGetInfoUrl;
  }

  public void setHttpFetcher(HttpFetcher httpFetcher) {
    this.httpFetcher = httpFetcher;
  }
}
