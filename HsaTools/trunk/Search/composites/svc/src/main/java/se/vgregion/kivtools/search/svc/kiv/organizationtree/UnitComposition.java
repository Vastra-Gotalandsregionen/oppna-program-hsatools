package se.vgregion.kivtools.search.svc.kiv.organizationtree;

import java.util.List;

/**
 * Container for a unit which also hold some meta data about the unit which is used by the vgr domain structure.
 * 
 * @author David Bennehult & Joakim Olsson
 * @param <T> Unit type to be used in UnitComposition instance.
 */
public interface UnitComposition<T> {

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
