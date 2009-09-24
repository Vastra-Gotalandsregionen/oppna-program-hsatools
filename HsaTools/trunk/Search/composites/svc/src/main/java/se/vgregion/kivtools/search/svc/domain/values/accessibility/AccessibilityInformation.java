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
package se.vgregion.kivtools.search.svc.domain.values.accessibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for accessibility information.
 */
public class AccessibilityInformation implements Serializable {
  private static final long serialVersionUID = 1L;

  private final AccessibilityObject businessObject;
  private final ArrayList<AccessibilityObject> subObjects;

  /**
   * Constructs a new AccessibilityInformation with the provided business object and sub objects.
   * 
   * @param businessObject The business object to assign to this accessibility information.
   * @param subObjects The sub objects to assign to this accessibility information.
   */
  public AccessibilityInformation(AccessibilityObject businessObject, List<AccessibilityObject> subObjects) {
    if (businessObject == null) {
      throw new IllegalArgumentException("businessObject may not be null");
    }
    if (subObjects == null) {
      throw new IllegalArgumentException("subObjects may not be null");
    }

    this.businessObject = businessObject;
    this.subObjects = new ArrayList<AccessibilityObject>(subObjects);
  }

  /**
   * Getter for the sub objects of the accessibility information.
   * 
   * @return A list of sub objects of the accessibility information.
   */
  public List<AccessibilityObject> getSubObjects() {
    return Collections.unmodifiableList(subObjects);
  }

  /**
   * Getter for the business object of the accessibility information.
   * 
   * @return The business object of the accessibility information.
   */
  public AccessibilityObject getBusinessObject() {
    return businessObject;
  }
}
