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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.xml.ws.BindingProvider;

import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebServiceImpl;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebServiceImplPortType;

public class KivwsFactoryBean {

  private Properties properties;

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public VGRegionWebServiceImplPortType createWebService() throws MalformedURLException {

    // This will set login for basic http auth used in webservice
    Authenticator.setDefault(new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(KivwsFactoryBean.this.properties.getProperty("hsatools.search.svc.kivws.username"), KivwsFactoryBean.this.properties.getProperty(
            "hsatools.search.svc.kivws.password").toCharArray());
      }
    });

    VGRegionWebServiceImpl vgRegionWebServiceService = new VGRegionWebServiceImpl();
    VGRegionWebServiceImplPortType vgRegionWebServiceImplPort = vgRegionWebServiceService.getVGRegionWebServiceImplPort();
    // Setup username and password authentication for webservice.
    BindingProvider bindingProvider = (BindingProvider) vgRegionWebServiceImplPort;
    bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, this.properties.getProperty("hsatools.search.svc.kivws.username"));
    bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, this.properties.getProperty("hsatools.search.svc.kivws.password"));
    return vgRegionWebServiceImplPort;
  }

}
