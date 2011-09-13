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

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.SingleAttributeMapper;

/**
 * 
 * This class implement Spring LDAP as search service.
 * 
 * @author davidbennehult
 * 
 */

public class SpringLdapSearchService implements SearchService {

  private LdapTemplate ldapTemplate;
  private UnitMapper unitMapper;

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  public void setUnitMapper(UnitMapper unitMapper) {
    this.unitMapper = unitMapper;
  }

  @Override
  public List<String> searchSingleAttribute(Name base, String filter, int searchScope, List<String> attrs, String mappingAttribute) {
    
    List<String> result = this.ldapTemplate.search(base, filter, searchScope, attrs.toArray(new String[0]), new SingleAttributeMapper(mappingAttribute));
    return result;
  }

  @Override
  public List<Unit> searchUnits(Name base, String filter, int searchScope, List<String> attrs) {
    List<Unit> result = this.ldapTemplate.search(base, filter, searchScope, attrs.toArray(new String[0]), this.unitMapper);
    return result;
  }

  @Override
  public List<Unit> searchFunctionUnits(Name base, String filter, int searchScope, List<String> attrs) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Unit lookupUnit(Name name, List<String> attrs) {
    Unit unit = (Unit) this.ldapTemplate.lookup(name, attrs.toArray(new String[0]), this.unitMapper);
    return unit;
  }


}
