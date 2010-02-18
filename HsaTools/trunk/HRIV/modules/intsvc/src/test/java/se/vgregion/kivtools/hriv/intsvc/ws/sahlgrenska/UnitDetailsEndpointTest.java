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

package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import static org.easymock.EasyMock.*;

import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.ObjectFactory;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitRequest;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitResponse;

public class UnitDetailsEndpointTest {
  UnitDetailsEndpoint unitDetailsEndpoint;

  // Setup a UnitDetailsEndpoint object
  @Before
  public void setup() {
    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setContextPath("se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb");
    unitDetailsEndpoint = new UnitDetailsEndpoint(jaxb2Marshaller, jaxb2Marshaller);
    unitDetailsEndpoint.setUnitDetailsService(createUnitDetailsServiceMock("hsaId1"));
  }

  @Test
  public void testInvoke() throws Exception {

    // Create a UnitRequest object
    UnitRequest unitRequest_valid_Id = new UnitRequest();
    unitRequest_valid_Id.setHsaIdentity("hsaId1");
    UnitRequest unitRequest_invalid_id = new UnitRequest();
    unitRequest_invalid_id.setHsaIdentity(null);

    // Test with valid id
    UnitResponse unitResponse = (UnitResponse) unitDetailsEndpoint.invokeInternal(unitRequest_valid_Id);
    Assert.assertEquals("hsaId1", unitResponse.getOrganization().getUnit().get(0).getId());

    // Test with invalid id
    // unitResponse = (UnitResponse) unitDetailsEndpoint.invokeInternal(unitRequest_invalid_id);
    // Assert.assertEquals("", unitResponse.getOrganization().getUnit().get(0).getId());
  }

  // Create a mock object with a containing a unit with the unitId parameter
  private UnitDetailsService<Organization> createUnitDetailsServiceMock(String unitId) {
    ObjectFactory objectFactory = new ObjectFactory();
    Organization organization = objectFactory.createOrganization();
    Unit unit = objectFactory.createUnit();
    unit.setId(unitId);
    organization.getUnit().add(unit);
    UnitDetailsService<Organization> unitDetailsServiceMock = EasyMock.createMock(UnitDetailsServiceImpl.class);
    expect(unitDetailsServiceMock.getUnitDetails(unitId)).andReturn(organization);
    EasyMock.replay(unitDetailsServiceMock);
    return unitDetailsServiceMock;
  }
}
