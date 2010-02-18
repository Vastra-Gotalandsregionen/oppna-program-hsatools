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
package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import java.net.URL;
import java.util.ResourceBundle;

import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import se.vgregion.kivtools.hriv.intsvc.ws.eniro.FtpClient;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.InformationPusherEniro;

public class LdapEniroIntergrationTest {

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    ResourceBundle ldapConnectionResourceBundle = ResourceBundle.getBundle("se.vgregion.kivtools.hriv.intsvc.ldap.eniro.ldap");
    String ldapUrl = ldapConnectionResourceBundle.getString("ldapUrl");
    String ldapBase = ldapConnectionResourceBundle.getString("ldapBase");

    ResourceBundle ldapCredentialResourceBundle = ResourceBundle.getBundle("se.vgregion.kivtools.hriv.intsvc.ldap.eniro.security");
    String userDn = ldapCredentialResourceBundle.getString("userDn");
    String password = ldapCredentialResourceBundle.getString("password");

    LdapContextSource ldapContextSource = new LdapContextSource();
    ldapContextSource.setUrl(ldapUrl);
    ldapContextSource.setBase(ldapBase);
    ldapContextSource.setUserDn(userDn);
    ldapContextSource.setPassword(password);
    ldapContextSource.setPooled(true);
    ldapContextSource.afterPropertiesSet();
    LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
    InformationPusherEniro informationPusherEniro = new InformationPusherEniro();
    ldapTemplate.afterPropertiesSet();
    informationPusherEniro.setLdapTemplate(ldapTemplate);
    ftpClientMock clientMock = new LdapEniroIntergrationTest().new ftpClientMock();
    informationPusherEniro.setFtpClient(clientMock);
    informationPusherEniro.doService();
  }

  class ftpClientMock implements FtpClient {

    String fileContent;

    @Override
    public boolean sendFile(String fileContent) {
      this.fileContent = fileContent;
      return true;
    }

  }

  @Test
  public void fake() {

  }
}
