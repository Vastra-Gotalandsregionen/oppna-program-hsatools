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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se.vgregion.kivtools.search.svc.domain.Unit;

/**
 * Simple mvk client.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class MvkClient {
	private String mvkGuid;
	private String mvkUrl;
	Log logger = LogFactory.getLog(this.getClass());

	public MvkClient(String mvkGuid, String mvkUrl) {
		this.mvkGuid = mvkGuid;
		this.mvkUrl = mvkUrl;
	}

	public String getMvkUrl() {
		return mvkUrl;
	}

	public void setMvkUrl(String mvkUrl) {
		this.mvkUrl = mvkUrl;
	}

	public String getMvkGuid() {
		return mvkGuid;
	}

	public void setMvkGuid(String mvkGuid) {
		this.mvkGuid = mvkGuid;
	}

	public void assignCaseTypes(Unit u) {
		// Get accessibility info
		URL url = null;
		String mvkUrlString = mvkUrl + "&hsaid=" + u.getHsaIdentity()
				+ "&guid=" + getMvkGuid();
		try {
			url = new URL(mvkUrlString);
		} catch (MalformedURLException e) {
			logger.error("MVK url no good: " + mvkUrlString, e);
		}

		trustAllHttpsCertificates();
		HttpURLConnection urlConnection;
		BufferedInputStream in = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			((HttpsURLConnection) urlConnection)
					.setHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String hostname,
								SSLSession session) {
							return true;
						}
					});

			int responseCode = urlConnection.getResponseCode();
			if (responseCode == 200 || responseCode == 201) {
				in = new BufferedInputStream(urlConnection.getInputStream());
			} else {
				in = new BufferedInputStream(urlConnection.getErrorStream());
			}
		} catch (IOException e) {
			logger.error("Error when retrieving MVK xml response", e);
		}

		// Now read the buffered stream and get mvk info
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(in);
			doc.normalize();
		} catch (ParserConfigurationException e) {
			logger.error("Error when parsing MVK xml response", e);
		} catch (SAXException e) {
			logger.error("Error when parsing MVK xml response", e);
		} catch (IOException e) {
			logger.error("Error when parsing MVK xml response", e);
		}

		// Get and assign case types
		NodeList caseTypesNodeList = doc.getElementsByTagName("casetype");
		List<String> caseTypes = new ArrayList<String>();
		for (int i = 0; i < caseTypesNodeList.getLength(); i++) {
			caseTypes.add(caseTypesNodeList.item(i).getTextContent());
		}
		u.setMvkCaseTypes(caseTypes);
	}

	public static class miTM implements javax.net.ssl.TrustManager,	javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

	/**
	 * @see http://www.java-samples.com/showtutorial.php?tutorialid=211
	 */
	private static void trustAllHttpsCertificates() {
		// Create a trust manager that does not validate certificate chains:
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = null;
		try {
			sc = javax.net.ssl.SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(
		sc.getSocketFactory());
	}
}
