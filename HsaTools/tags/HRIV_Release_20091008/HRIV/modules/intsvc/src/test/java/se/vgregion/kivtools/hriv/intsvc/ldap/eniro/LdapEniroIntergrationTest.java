package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import se.vgregion.kivtools.hriv.intsvc.ws.eniro.FtpClient;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.InformationPusherEniro;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;

public class LdapEniroIntergrationTest {

  /**
   * @param args
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    new HealthcareTypeConditionHelper().setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");
     LdapContextSource ldapContextSource = new LdapContextSource();
     ldapContextSource.setUrl("ldap://kivldap01.vgregion.se:389");
     ldapContextSource.setBase("ou=Org,o=vgr");
     ldapContextSource.setUserDn("cn=sokso1,ou=Resurs,o=VGR");
     ldapContextSource.setPassword("6wuz8zab");
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
  public void fake(){
    
  }
}
