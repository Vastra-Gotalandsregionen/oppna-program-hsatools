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
package se.vgregion.kivtools.search.interfaces;

import java.io.Serializable;
import java.util.List;

/**
 * Container for a unit which also hold some meta data about the unit which is used by the vgr domain structure.
 * 
 * @author David Bennehult & Joakim Olsson
 * @param <T>
 *            Unit type to be used in UnitComposition instance.
 */
public interface UnitComposition<T> extends Serializable, Comparable<UnitComposition<T>> {

    /**
     * 
     * @return Unit of type T
     */
    T getUnit();

    /**
     * 
     * @return {@link List} of child units of type UnitComposition<T>
     */
    List<UnitComposition<T>> getChildUnits();

    /**
     * Gets the distinguished name of the units parent.
     * 
     * @return The distinguished name of the units parent.
     */
    String getParentDn();

    /**
     * 
     * @return The distinguished name of the unit.
     */
    String getDn();
}
