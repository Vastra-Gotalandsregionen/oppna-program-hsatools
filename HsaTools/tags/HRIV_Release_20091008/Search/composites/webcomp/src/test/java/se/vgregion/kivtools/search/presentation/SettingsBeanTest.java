package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.scheduling.quartz.ResourceLoaderClassLoadHelper;

public class SettingsBeanTest {

  private static final String PROPERTY_VALUE = "propValue";
  private static final String PROPERTY_KEY = "propKey";

  @Test
  public void testGetSettings() throws IOException {
    Resource resourceMock = EasyMock.createMock(Resource.class);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((PROPERTY_KEY + "=" + PROPERTY_VALUE).getBytes());
    EasyMock.expect(resourceMock.getInputStream()).andReturn(byteArrayInputStream);
    EasyMock.replay(resourceMock);
    SettingsBean settingsBean = new SettingsBean(resourceMock);
    assertEquals(PROPERTY_VALUE, settingsBean.getSettings().get(PROPERTY_KEY));
  }
  
  @Test
  public void testExceptionHandling() throws IOException{
    Resource resourceMock = EasyMock.createMock(Resource.class);
    EasyMock.expect(resourceMock.getInputStream()).andThrow(new IOException());
    EasyMock.replay(resourceMock);
    SettingsBean settingsBean = new SettingsBean(resourceMock);
    assertNotNull(settingsBean.getSettings());
  }
}
