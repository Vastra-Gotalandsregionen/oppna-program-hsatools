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

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;

/**
 * Implementation of the UnitRepository for Folktandvården.
 */
public class UnitRepositoryFTV extends BaseUnitRepository {
  private static final DistinguishedName KIV_SEARCH_BASE = new DistinguishedName("ou=Folktandvården Västra Götaland,ou=Org,o=vgr");

  /**
   * {@inheritDoc}
   */
  @Override
  protected DistinguishedName getSearchBase() {
    return UnitRepositoryFTV.KIV_SEARCH_BASE;
  }

@Override
public Unit getUnitByHsaIdtWihoutDeliverypoints(String hsaId)
		throws KivException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public SikSearchResultList<Person> setUnitOnEmployments(
		SikSearchResultList<Person> persons) {
	// TODO Auto-generated method stub
	return null;
}
}
