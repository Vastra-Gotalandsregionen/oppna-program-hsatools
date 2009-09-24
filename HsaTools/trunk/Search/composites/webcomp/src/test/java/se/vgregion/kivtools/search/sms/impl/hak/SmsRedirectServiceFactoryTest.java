package se.vgregion.kivtools.search.sms.impl.hak;

import static org.junit.Assert.*;

import org.junit.Test;

public class SmsRedirectServiceFactoryTest {

  @Test
  public void testInstance(){
    assertNotNull(new SmsRedirectServiceFactory());
  }
  
  @Test
  public void testGetSmsRedirectSoap() {
    assertNotNull(SmsRedirectServiceFactory.getSmsRedirectSoap());
  }

}
