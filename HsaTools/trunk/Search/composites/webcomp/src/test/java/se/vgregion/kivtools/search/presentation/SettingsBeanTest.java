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
