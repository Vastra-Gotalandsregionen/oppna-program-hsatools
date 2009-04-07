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
package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Person;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 */
@SuppressWarnings("serial")
public class DisplayPersonDetailsFlowSupportBean implements Serializable{
    Log logger = LogFactory.getLog(this.getClass());
    private static final String CLASS_NAME = DisplayPersonDetailsFlowSupportBean.class.getName();
    private SearchService searchService;
    
    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public Person getPersonDetails(String vgrId) {
        logger.info(CLASS_NAME + "::getPersonDetails(vgrId=" + vgrId + ")");
        try {
            return getSearchService().getPersonById(vgrId);
        } catch (Exception e) {
            e.printStackTrace();
            return new Person();
        }
    }
    
    public void logger(String msg) {
        logger.info(msg);
    }

}
