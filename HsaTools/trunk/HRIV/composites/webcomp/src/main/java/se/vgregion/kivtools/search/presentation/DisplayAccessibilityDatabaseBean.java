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
package se.vgregion.kivtools.search.presentation;

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

import se.vgregion.kivtools.search.presentation.forms.AccessibilityDatabaseFilterForm;
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
	Log logger = LogFactory.getLog(this.getClass());
	private static final String CLASS_NAME = DisplayAccessibilityDatabaseBean.class
			.getName();
	private String useAccessibilityDatabaseIntegration;
	private String accessibilityDatabaseIntegrationGetIdUrl;
	private String accessibilityDatabaseIntegrationGetInfoUrl;

	/**
	 * Look up database accessibility information for specified unit.
	 * 
	 * @param u
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public void assignAccessibilityDatabaseInfo(Unit u) throws IOException,
			SAXException, ParserConfigurationException {
		// Don't reassign accessibility info, only on first visit
		if (u.getAccessibilityInformation() != null)
			return;

		// First find out the accessibility database id
		if (!assignAccessibilityDatabaseId(u))
			return;

		// Get accessibility info
		URL url = new URL(accessibilityDatabaseIntegrationGetInfoUrl
				+ u.getAccessibilityDatabaseId());
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		int responseCode = urlConnection.getResponseCode();
		BufferedInputStream in;
		if (responseCode == 200 || responseCode == 201) {
			in = new BufferedInputStream(urlConnection.getInputStream());
		} else {
			in = new BufferedInputStream(urlConnection.getErrorStream());
		}

		// Now read the buffered stream and get accessibility info
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(in);
		doc.normalize();

		// Get business object (there is only one)
		NodeList businessObjectNodeList = doc
				.getElementsByTagName("businessobject");
		AccessibilityObject businessObject = null;
		for (int i = 0; i < businessObjectNodeList.getLength(); i++) {
			businessObject = new AccessibilityObject(businessObjectNodeList
					.item(i));
		}

		// Get sub objects
		NodeList subObjectNodeList = doc.getElementsByTagName("subobject");
		ArrayList<AccessibilityObject> subObjects = new ArrayList<AccessibilityObject>();
		for (int i = 0; i < subObjectNodeList.getLength(); i++) {
			AccessibilityObject subObject = new AccessibilityObject(
					subObjectNodeList.item(i));
			subObjects.add(subObject);
		}

		AccessibilityInformation accessibilityInformation = new AccessibilityInformation();
		accessibilityInformation.setBusinessObject(businessObject);
		accessibilityInformation.setSubObjects(subObjects);
		u.setAccessibilityInformation(accessibilityInformation);
		urlConnection.disconnect();
	}

	/**
	 * Find out and assign the accessibility database id.
	 * 
	 * @param u
	 *            return true if succeeded, false otherwise
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public boolean assignAccessibilityDatabaseId(Unit u) {
		try {
			// Get accessibility Id
			URL url = new URL(accessibilityDatabaseIntegrationGetIdUrl
					+ u.getHsaIdentity());
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
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
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory
						.newDocumentBuilder();
				Document doc = docBuilder.parse(in);
				NodeList ids = doc.getElementsByTagName("string");
				for (int i = 0; i < ids.getLength(); i++) {
					String textContent = ids.item(i).getTextContent();
					int accessabilityId = Integer.parseInt(textContent);
					u.setAccessibilityDatabaseId(accessabilityId);
				}
			} catch (NumberFormatException e) {
				logger
						.error("We did not get a valid accessability database id. Skip it.");
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
	 * Filters the accessibility info depending on the form input. Eg "stairs"
	 * are maybe only relevant for some disabled individuals and not for others.
	 * 
	 * @param form
	 * @param u
	 */
	public void filterAccessibilityDatabaseInfo(
			AccessibilityDatabaseFilterForm form, Unit u) {
		// We need to keep track of submissions in order to know when to show
		// "no result".
		form.setSubmitted(true);

		// Create array with selected disabilities
		int[] selectedDisabilities = new int[5];

		if (form.getHear() == true)
			selectedDisabilities[0] = 1;
		if (form.getSee() == true)
			selectedDisabilities[1] = 1;
		if (form.getMove() == true)
			selectedDisabilities[2] = 1;
		if (form.getSubstances() == true)
			selectedDisabilities[3] = 1;
		if (form.getInfo() == true)
			selectedDisabilities[4] = 1;

		// Attentive or Available?
		boolean attentive = false;
		String listType = form.getListType();
		if ("attentive".equals(listType)) {
			attentive = true;
		}

		// Business object
		if (u.getAccessibilityInformation() != null) {
			for (Block b : u.getAccessibilityInformation().getBusinessObject()
					.getBlocks()) {
				for (AccessibilityPackage p : b.getPackages()) {
					criteriaIteration: for (Criteria c : p.getCriterias()) {
						// Take Attentive/Available choice into consideration.
						// Request should match the criteria's
						// "attentive/available belonging".
						// We should not show hidden criterias either.
						if (!(attentive == c.isNotice()) || c.isHidden()) {
							c.setShow(false);
							continue;
						}

						// Reset show flag
						c.setShow(false);

						for (String disability : c.getDisabilities()) {
							// Since we don't want to add the accessibility
							// information multiple times, move on to next
							// criteria
							// when it is added.
							if ("hear".equals(disability)
									&& selectedDisabilities[0] == 1) {
								c.setShow(true);
								continue criteriaIteration;
							}
							if ("see".equals(disability)
									&& selectedDisabilities[1] == 1) {
								c.setShow(true);
								continue criteriaIteration;
							}
							if ("move".equals(disability)
									&& selectedDisabilities[2] == 1) {
								c.setShow(true);
								continue criteriaIteration;
							}
							if ("substances".equals(disability)
									&& selectedDisabilities[3] == 1) {
								c.setShow(true);
								continue criteriaIteration;
							}
							if ("information".equals(disability)
									&& selectedDisabilities[4] == 1) {
								c.setShow(true);
								continue criteriaIteration;
							}
						}
					}
				}
			}

			// Sub objects
			for (AccessibilityObject o : u.getAccessibilityInformation()
					.getSubObjects()) {
				for (Block b : o.getBlocks()) {
					for (AccessibilityPackage p : b.getPackages()) {
						criteriaIteration: for (Criteria c : p.getCriterias()) {
							// Take Attentive/Available choice into
							// consideration.
							// Request should match the criteria's
							// "attentive/available belonging".
							// We should not show hidden criterias either.
							if (!(attentive == c.isNotice()) || c.isHidden()) {
								c.setShow(false);
								continue;
							}

							// Reset show flag
							c.setShow(false);

							for (String disability : c.getDisabilities()) {
								// Since we don't want to add the accessibility
								// information multiple times, move on to next
								// criteria when it is added.
								if ("hear".equals(disability)
										&& selectedDisabilities[0] == 1) {
									c.setShow(true);
									continue criteriaIteration;
								}
								if ("see".equals(disability)
										&& selectedDisabilities[1] == 1) {
									c.setShow(true);
									continue criteriaIteration;
								}
								if ("move".equals(disability)
										&& selectedDisabilities[2] == 1) {
									c.setShow(true);
									continue criteriaIteration;
								}
								if ("substances".equals(disability)
										&& selectedDisabilities[3] == 1) {
									c.setShow(true);
									continue criteriaIteration;
								}
								if ("information".equals(disability)
										&& selectedDisabilities[4] == 1) {
									c.setShow(true);
									continue criteriaIteration;
								}
							}
						}
					}
				}
			}
		}
	}

	public void logger(String msg) {
		logger.info(msg);
	}

	public String getUseAccessibilityDatabaseIntegration() {
		return useAccessibilityDatabaseIntegration;
	}

	public void setUseAccessibilityDatabaseIntegration(
			String useAccessibilityDatabaseIntegration) {
		this.useAccessibilityDatabaseIntegration = useAccessibilityDatabaseIntegration;
	}

	public String getAccessibilityDatabaseIntegrationGetIdUrl() {
		return accessibilityDatabaseIntegrationGetIdUrl;
	}

	public void setAccessibilityDatabaseIntegrationGetIdUrl(
			String accessibilityDatabaseIntegrationGetIdUrl) {
		this.accessibilityDatabaseIntegrationGetIdUrl = accessibilityDatabaseIntegrationGetIdUrl;
	}

	public String getAccessibilityDatabaseIntegrationGetInfoUrl() {
		return accessibilityDatabaseIntegrationGetInfoUrl;
	}

	public void setAccessibilityDatabaseIntegrationGetInfoUrl(
			String accessibilityDatabaseIntegrationGetInfoUrl) {
		this.accessibilityDatabaseIntegrationGetInfoUrl = accessibilityDatabaseIntegrationGetInfoUrl;
	}
}
