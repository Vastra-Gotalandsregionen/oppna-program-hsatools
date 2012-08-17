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
     * @param oldFlatOrganization
     *            Organization tree that is used at the moment
     * @param newFlatOrganization
     *            Organization tree that the old organization tree should be updated to, if any changes is done in
     *            new organization tree.
     * @return {@link OrganizationChangeReport} that contains changes in the new organization compared to the old.
     */
    OrganizationChangeReport<T> createOrganizationChangeReport(List<UnitComposition<T>> oldFlatOrganization,
            List<UnitComposition<T>> newFlatOrganization);

}
