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

package se.vgregion.kivtools.search.svc.impl;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.util.Arguments;

/**
 * ContextMapper for retrieving a single attribute from an entry.
 */
public class SingleAttributeMapper implements ContextMapper {
  private final String attributeName;

  /**
   * Constructs a new SingleAttributeMapper.
   * 
   * @param attributeName the name of the attribute to retrieve.
   */
  public SingleAttributeMapper(String attributeName) {
    Arguments.notEmpty("attributeName", attributeName);
    this.attributeName = attributeName;
  }

  /**
   * Retrieves the value of the attribute and returns it as a string.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return The value of the attribute as a string.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContext = (DirContextOperations) ctx;
    String value = dirContext.getStringAttribute(attributeName);
    return value;
  }
}
