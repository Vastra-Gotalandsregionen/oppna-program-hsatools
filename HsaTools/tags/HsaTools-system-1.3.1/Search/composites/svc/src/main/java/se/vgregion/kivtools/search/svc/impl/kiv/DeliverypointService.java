/**
 * 
 */
package se.vgregion.kivtools.search.svc.impl.kiv;

import java.util.List;

import se.vgregion.kivtools.search.domain.Deliverypoint;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;

/**
 * @author attra
 *
 */
public interface DeliverypointService {
	  /**
	   * Search the deliveryPoints of a given Unit .
	   * 
	   * @param unit - Unit domain object
	   * @return List of found deliveryPoints where the unit is registered.
	   * @throws KivException .
	   */
	  List<Deliverypoint> searchDeliveryPointsForUnit(Unit unit) throws KivException;

	  /**
	   * Search for the deliveryPoints of a unit given its hsaidentity.
	   * 
	   * @param unitHsaid - unit hsaIdentity
	   * @return List of found deliveryPoints where the unit is registered.
	   * @throws KivException .
	   */
	  List<Deliverypoint> searchDeliveryPointsForUnit(String unitHsaid) throws KivException;

	  /**
	   * Fetch delivery point object given its hsaid.
	   * 
	   * @param hsaId The hsaid of the deliveryPoint.
	   * @return The DeliveryPoint with the given hsaid.
	   * @throws KivException .
	   */
	  Deliverypoint findDeliveryPointById(String hsaId) throws KivException;

}
