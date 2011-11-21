package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.Comparator;
import java.util.List;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

public interface UnitRepository {

  /**
   * Advanced means that it also handles healthcareTypeConditions in the search filter.
   * 
   * @param unit - unit to search for.
   * @param maxResult - max result of found units to return.
   * @param sortOrder - sort order for the result list.
   * @param onlyPublicUnits - show units that should be displayed to the public.
   * 
   * @return - a list of found units.
   * @throws KivException If an error occur.
   */
  SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException;

  /**
   * Search for selected unit.
   * 
   * @param searchUnitCriterions The unit to search for.
   * @param maxResult Max number of units to contain in the result.
   * @return List of found units.
   * @throws KivException .
   */
  SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxResult) throws KivException;

  /**
   * Fetch unit by the unit hsa id.
   * 
   * @param hsaId The hsa id of the unit.
   * @return The unit with the given hsa id.
   * @throws KivException .
   */
  Unit getUnitByHsaId(String hsaId) throws KivException;

  /**
   * Fetch unit by the unit hsa id and does not have careType inpatient.
   * 
   * @param hsaId The hsa id of the unit.
   * @return The unit with the given hsa id.
   * @throws KivException .
   */
  Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId) throws KivException;

  /**
   * Fetch unit by the unit dn.
   * 
   * @param dn The dn for the unit.
   * @return The unit with the given dn.
   * @throws KivException .
   */
  Unit getUnitByDN(DN dn) throws KivException;

  /**
   * Get all hsa ids to all units.
   * 
   * @return List of hsa ids.
   * @throws KivException .
   */
  List<String> getAllUnitsHsaIdentity() throws KivException;

  /**
   * Get all hsa ids for units with chosen businessClassification codes.
   * 
   * @param onlyPublicUnits List only units that should be displayed to the public.
   * @return List of found units.
   * @throws KivException .
   */
  List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException;

  /**
   * Retrieves a list of all Units and functions filtered based on if only units for public display should be retrieved.
   * 
   * @param onlyPublicUnits Only select units from search that should be displayed to the public.
   * @return A list of units.
   */
  List<Unit> getAllUnits(boolean onlyPublicUnits);

  /**
   * 
   * @param parentUnit - unit to get subunits for
   * @param maxResult - maximum of unit to be return in the result
   * @return A list of subunits for current unit.
   * @throws KivException If something goes wrong doing search.
   */
  SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxResult) throws KivException;

  /**
   * 
   * @param parentUnit - unit to get subunits for
   * @param maxResult - maximum of unit to be return in the result
   * @return A list the first level of subunits for current unit.
   * @throws KivException If something goes wrong doing search.
   */
  SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException;

  List<String> getUnitAdministratorVgrIds(String hsaId) throws KivException;

}