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
package se.vgregion.kivtools.hriv.presentation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se.vgregion.kivtools.hriv.presentation.forms.AccessibilityDatabaseFilterForm;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.AccessibilityObject;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.AccessibilityPackage;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.Block;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.Criteria;

/**
 * Support for accessibility information for units.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
@SuppressWarnings("serial")
public class DisplayAccessibilityDatabaseBean implements Serializable {
  private static final String CLASS_NAME = DisplayAccessibilityDatabaseBean.class.getName();

  private Log logger = LogFactory.getLog(this.getClass());
  private String useAccessibilityDatabaseIntegration;
  private String accessibilityDatabaseIntegrationGetIdUrl;
  private String accessibilityDatabaseIntegrationGetInfoUrl;
  private String formerLanguageId;

  public String getFormerLanguageId() {
    return formerLanguageId;
  }

  public void setFormerLanguageId(String formerLanguageId) {
    this.formerLanguageId = formerLanguageId;
  }

  /**
   * Look up database accessibility information for specified unit.
   * 
   * @param u The unit which should get accessibility info assigned to it.
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void assignAccessibilityDatabaseInfo(Unit u, AccessibilityDatabaseFilterForm accessibilityDatabaseFilterForm) throws IOException, SAXException, ParserConfigurationException {
    // Don't reassign accessibility info, only on first visit
    // if (u.getAccessibilityInformation() != null)
    // return;

    // First find out the accessibility database id
    if (!assignAccessibilityDatabaseId(u)) {
      return;
    }

    int languageId = Integer.parseInt(accessibilityDatabaseFilterForm.getLanguageId());

    URL url = new URL(accessibilityDatabaseIntegrationGetInfoUrl + languageId + "&facilityId=" + u.getAccessibilityDatabaseId());
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    int responseCode = urlConnection.getResponseCode();
    BufferedInputStream in;
    if (responseCode == 200 || responseCode == 201) {
      in = new BufferedInputStream(urlConnection.getInputStream());
    } else {
      in = new BufferedInputStream(urlConnection.getErrorStream());
    }

    // Now read the buffered stream and get accessibility info
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(in);
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
    u.setAccessibilityInformation(accessibilityInformation);
    urlConnection.disconnect();
  }

  /**
   * Find out and assign the accessibility database id.
   * 
   * @param u return true if succeeded, false otherwise
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public boolean assignAccessibilityDatabaseId(Unit u) {
    try {
      // Get accessibility Id
      URL url = new URL(accessibilityDatabaseIntegrationGetIdUrl + u.getHsaIdentity());
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      int responseCode = urlConnection.getResponseCode();
      BufferedInputStream in;
      if (responseCode == 200 || responseCode == 201) {
        in = new BufferedInputStream(urlConnection.getInputStream());
      } else {
        in = new BufferedInputStream(urlConnection.getErrorStream());
      }

      // Now read the buffered stream into a XML document and get
      // accessibility id
      try {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(in);
        NodeList ids = doc.getElementsByTagName("string");
        for (int i = 0; i < ids.getLength(); i++) {
          String textContent = ids.item(i).getTextContent();
          int accessabilityId = Integer.parseInt(textContent);
          u.setAccessibilityDatabaseId(accessabilityId);
        }
      } catch (NumberFormatException e) {
        logger.error("We did not get a valid accessability database id. Skip it.");
        return false;
      } catch (Exception e) {
        // Most likely we could not parse the answer as a valid XML
        // structure.
        return false;
      }
      urlConnection.disconnect();
      return true;
    } catch (Exception e) {
      // If we could not find server and get accessibility db id there is
      // nothing we can do about it. The show must go on, handle it the
      // same way as DisplayUnitDetailsFlowSupportBean#getUnitDetails()
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Filters the accessibility info depending on the form input. Eg "stairs" are maybe only relevant for some disabled individuals and not for others.
   * 
   * @param form
   * @param u
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public void filterAccessibilityDatabaseInfo(Unit u, AccessibilityDatabaseFilterForm form) throws IOException, SAXException, ParserConfigurationException {
    // We need to keep track of submissions in order to know when to show "no result".
    form.setSubmitted(true);

    // If language has changed we need to download new data
    if (!form.getLanguageId().equals(formerLanguageId)) {
      assignAccessibilityDatabaseInfo(u, form);
      formerLanguageId = form.getLanguageId();
    }

    // Create array with selected disabilities
    boolean[] selectedDisabilities = new boolean[5];

    selectedDisabilities[0] = form.getHear();
    selectedDisabilities[1] = form.getSee();
    selectedDisabilities[2] = form.getMove();
    selectedDisabilities[3] = form.getSubstances();
    selectedDisabilities[4] = form.getInfo();

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

  public void logger(String msg) {
    logger.info(msg);
  }

  public String getUseAccessibilityDatabaseIntegration() {
    return useAccessibilityDatabaseIntegration;
  }

  public void setUseAccessibilityDatabaseIntegration(String useAccessibilityDatabaseIntegration) {
    this.useAccessibilityDatabaseIntegration = useAccessibilityDatabaseIntegration;
  }

  public String getAccessibilityDatabaseIntegrationGetIdUrl() {
    return accessibilityDatabaseIntegrationGetIdUrl;
  }

  public void setAccessibilityDatabaseIntegrationGetIdUrl(String accessibilityDatabaseIntegrationGetIdUrl) {
    this.accessibilityDatabaseIntegrationGetIdUrl = accessibilityDatabaseIntegrationGetIdUrl;
  }

  public String getAccessibilityDatabaseIntegrationGetInfoUrl() {
    return accessibilityDatabaseIntegrationGetInfoUrl;
  }

  public void setAccessibilityDatabaseIntegrationGetInfoUrl(String accessibilityDatabaseIntegrationGetInfoUrl) {
    this.accessibilityDatabaseIntegrationGetInfoUrl = accessibilityDatabaseIntegrationGetInfoUrl;
  }
}
