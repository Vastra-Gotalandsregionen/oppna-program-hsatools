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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

/**
 * 
 * Constants to use in LDAP classes.
 * 
 */
public class Constants {
  public static final String SEARCH_BASE = "ou=Landstinget Halland,dc=lthallandhsa,dc=se";
  public static final String OBJECT_CLASS_UNIT_STANDARD = "organizationalUnit";
  public static final String OBJECT_CLASS_FUNCTION_STANDARD = "organizationalRole";
  public static final String OBJECT_CLASS_UNIT_SPECIFIC = "organizationalUnit";
  public static final String OBJECT_CLASS_FUNCTION_SPECIFIC = "organizationalRole";
  public static final String LDAP_PROPERTY_UNIT_NAME = "ou";
  public static final String LDAP_PROPERTY_FUNCTION_NAME = "cn";
  public static final String LDAP_PROPERTY_DESCRIPTION = "description";
}
