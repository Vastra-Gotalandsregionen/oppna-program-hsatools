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

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.hriv.intsvc.ws.eniro.FtpClient;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.InformationPusherEniro;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr.UnitFetcherVGR;

public class LdapEniroIntergrationTest {

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    ApplicationContext applicationContext = new FileSystemXmlApplicationContext("src/test/resources/services-config.xml");
    LdapTemplate ldapTemplate = (LdapTemplate) applicationContext.getBean("ldapTemplateOrganisation");
    InformationPusherEniro informationPusherEniro = new InformationPusherEniro();
    ldapTemplate.afterPropertiesSet();
    informationPusherEniro.setUnitFetcher(new UnitFetcherVGR(ldapTemplate, null, null));
    FtpClientMock clientMock = new LdapEniroIntergrationTest().new FtpClientMock();
    informationPusherEniro.setFtpClient(clientMock);
    informationPusherEniro.doService();
  }

  class FtpClientMock implements FtpClient {

    String fileContent;

    @Override
    public boolean sendFile(String fileContent, final String basename, final String suffix) {
      this.fileContent = fileContent;
      return true;
    }

  }

  @Test
  public void fake() {

  }
}
