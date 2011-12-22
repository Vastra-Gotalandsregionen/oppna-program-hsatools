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

package se.vgregion.kivtools.search.svc.codetables;

import java.util.List;

import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;

/**
 * Provides mapping between codes and descriptions for values.
 * 
 * @author argoyle
 */
public interface CodeTablesService {

  /**
   * Retrieves the description for the provided code in the table identified by the provided code table name.
   * 
   * @param codeTableName The name of the code table to use for the lookup.
   * @param code The code to lookup.
   * @return The description for the provided code.
   */
  String getValueFromCode(CodeTableNameInterface codeTableName, String code);

  /**
   * Retrieves a list of codes where the description in the table identified by the provided code table name matches the provided text value.
   * 
   * @param codeTableName The name of the code table to use for the lookup.
   * @param textValue The text value to get matches for.
   * @return A list of codes for the entries where the description matched the provided value.
   */
  List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue);

  /**
   * Retrieves a list of descriptions that matches the provided text value from the table identified by the provided code table name.
   * 
   * @param codeTableName The name of the code table to use for the lookup.
   * @param textValue The text value to get matches for.
   * @return A list of descriptions for the entries where the description matched the provided value.
   */
  List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue);

  /**
   * This method will return all item values for a chosen code table.
   * 
   * @param codeTableName Name of the code table to get value items from.
   * @return A list of all value items for chosen code table.
   * @throws IllegalArgumentException If an invalid code table name is used.
   */
  List<String> getAllValuesItemsFromCodeTable(String codeTableName);
}
