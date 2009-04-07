/**
 * Copyright 2009 Västa Götalandsregionen
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
/**
 * 
 */
package se.vgregion.kivtools.search.svc.domain;

import java.util.Comparator;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 */
public class EmploymenPeriodComparator implements Comparator<Employment> {
    /*
     * Returns <0 if employment1<employment2 Returns 0 if
     * employment1==employment2 Returns >0 if employment1>employment2
     */
    public int compare(Employment employment1, Employment employment2) {
        if (employment1 == null) {
            employment1 = new Employment();
        }
        if (employment2 == null) {
            employment2 = new Employment();
        }
        return employment1.getEmploymentPeriod().start().compareTo(
                employment2.getEmploymentPeriod().start());
    }

}
