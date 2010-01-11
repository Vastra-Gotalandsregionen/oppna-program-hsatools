package se.vgregion.kivtools.search.svc.kiv.organizationtree;

import java.util.List;

import se.vgregion.kivtools.search.domain.util.OrganizationChangeReport;
import se.vgregion.kivtools.search.interfaces.UnitComposition;
/**
 * 
 * @author David Bennehult
 * @author Ulf Carlsson
 *
 */
public interface OrganizationChangeReporter<T> {
    
    /**
     * 
     * @param <T> Unit type used in UnitComposition
     * @param oldOrganizationTree Organization tree that is used at the moment 
     * @param newOrganizationTree Organization tree that the old organization tree should be updated to, if any changes is done in new organization tree.
     * @return {@link OrganizationChangeReport} that contains changes in the new organization compared to the old.
     */
   OrganizationChangeReport<T> createOrganizationChangeReport(List<UnitComposition<T>> oldFlatOrganization, List<UnitComposition<T>> newFlatOrganization);

}
