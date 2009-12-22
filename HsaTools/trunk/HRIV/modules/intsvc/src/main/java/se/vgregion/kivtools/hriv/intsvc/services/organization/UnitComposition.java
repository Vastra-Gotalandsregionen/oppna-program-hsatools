package se.vgregion.kivtools.hriv.intsvc.services.organization;

/**
 * Container for a unit which also hold some meta data about the unit which is used by the vgr domain structure.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public interface UnitComposition<T> {

    /**
     * 
     * @return Unit of type T
     */
    T getUnit();

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
