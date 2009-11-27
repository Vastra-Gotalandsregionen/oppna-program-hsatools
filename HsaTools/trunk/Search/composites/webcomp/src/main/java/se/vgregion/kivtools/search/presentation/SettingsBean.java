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
package se.vgregion.kivtools.search.presentation;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

/**
 * Holds SIK settings for different organizations.
 * 
 * @author David Bennehult, Know IT
 * @author Jonas Liljenfeldt, Know IT
 */
public class SettingsBean implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Log LOGGER = LogFactory.getLog(SearchPersonFlowSupportBean.class);
  private Properties settings = new Properties();
  private String useTrackingCode;
  private String trackingCodeOnServer;
  private String trackingCode;

  /**
   * Constructs a new SettingsBean using the provided Resource.
   * 
   * @param resource The Resource to read settings from.
   */
  public SettingsBean(Resource resource) {
    try {
      settings.load(resource.getInputStream());
    } catch (IOException e) {
      LOGGER.error(e);
    }
  }

  public String getUseTrackingCode() {
    return useTrackingCode;
  }

  public void setUseTrackingCode(String useTrackingCode) {
    this.useTrackingCode = useTrackingCode;
  }

  public String getTrackingCodeOnServer() {
    return trackingCodeOnServer;
  }

  public void setTrackingCodeOnServer(String trackingCodeOnServer) {
    this.trackingCodeOnServer = trackingCodeOnServer;
  }

  public String getTrackingCode() {
    return trackingCode;
  }

  public void setTrackingCode(String trackingCode) {
    this.trackingCode = trackingCode;
  }

  public Map<Object, Object> getSettings() {
    return settings;
  }
}
