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

import se.vgregion.kivtools.search.common.Constants;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.presentation.forms.PersonSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.util.LogUtils;

/**
 * @author Anders Asplund - KnowIt
 *
 */
@SuppressWarnings("serial")
public class SearchPersonFlowSupportBean implements Serializable {
    Log logger = LogFactory.getLog(this.getClass());
    private static final String CLASS_NAME = SearchPersonFlowSupportBean.class.getName();
    private SearchService searchService;
    private int pageSize;
    
    private int maxSearchResult;
    
        
    public void setMaxSearchResult(int maxSearchResult) {
        this.maxSearchResult = maxSearchResult;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void initalLoad() {
        logger.info(CLASS_NAME + ".initalLoad()");
    }

    protected boolean isVgrIdSearch(PersonSearchSimpleForm theForm) throws KivNoDataFoundException  {
        if (theForm==null) {
            logger.error("ERROR: " + CLASS_NAME + "::isVgrIdSearch(...) detected that theForm is null");
            throw new KivNoDataFoundException("Internt fel har uppst�tt.");
        }
        if (theForm.getSearchType().trim().equalsIgnoreCase(Constants.PERSON_SEARCH_TYPE_VGRID)) {
            return true;
        }
        return false;
    }

    public SikSearchResultList<Person> doSearch(PersonSearchSimpleForm theForm) throws KivNoDataFoundException {
        logger.info(CLASS_NAME + ".doSearch()");
        
        try {
            SikSearchResultList<Person> list = new SikSearchResultList<Person>();
            TimeMeasurement overAllTime = new TimeMeasurement();
            overAllTime.start(); // start measurement
            if (!theForm.isEmpty()) {
                // perform a search
                list = getSearchService().searchPersons(theForm.getGivenName(), theForm.getSirName(), theForm.getVgrId(), maxSearchResult);
            }
            // fetch all employments
            SikSearchResultList <Employment> empList=null;
            for (Person pers : list) {
                empList = getSearchService().getEmployments(pers.getDn());
                pers.setEmployments(empList);
                // add the datasource time for fetching employments 
                list.addDataSourceSearchTime(new TimeMeasurement(empList.getTotalDataSourceSearchTimeInMilliSeconds()));
            }
            overAllTime.stop(); // stop measurement
            
            LogUtils.printSikSearchResultListToLog(this, "doSearch", overAllTime, logger, list);     
            if (list.size()==0) {
                throw new KivNoDataFoundException();
            }
            return list;
        } catch (Exception e) {
            if (e instanceof KivNoDataFoundException) {
               throw (KivNoDataFoundException)e;
            }
            e.printStackTrace();
            return new SikSearchResultList<Person>();
        }
    }
    
    public SikSearchResultList<Person> getOrganisation(String hsaIdentity) throws KivNoDataFoundException {
        logger.info(CLASS_NAME + ".getOrganisation()");
        
        try {
            TimeMeasurement overAllTime = new TimeMeasurement();
            overAllTime.start(); // start measurement
            SikSearchResultList<Person> list = new SikSearchResultList<Person>();
            
            list = getSearchService().getAllPersonsInUnit(hsaIdentity);

            // fetch all employments
            SikSearchResultList <Employment> empList=null;
            for (Person pers : list) {
                empList = getSearchService().getEmployments(pers.getDn());
                pers.setEmployments(empList);
                // add the datasource time for fetching employments                list.addDataSourceSearchTime(new TimeMeasurement(empList.getTotalDataSourceSearchTimeInMilliSeconds()));
            }
            
            overAllTime.stop(); // stop measurement

            LogUtils.printSikSearchResultListToLog(this, "getOrganisation", overAllTime, logger, list);            
            if (list.size()==0) {
                throw new KivNoDataFoundException();
            }
            return list;
        } catch (Exception e) {
            if (e instanceof KivNoDataFoundException) {
                throw (KivNoDataFoundException)e;
             }
            e.printStackTrace();
            return new SikSearchResultList<Person>();
        }   
    }

    
    public List<String> getAllPersonsVgrId() throws KivNoDataFoundException {
        try {
            List<String> listOfVgrIds = getSearchService().getAllPersonsId();
            return listOfVgrIds;
        } catch (Exception e) {
            if (e instanceof KivNoDataFoundException) {
                throw (KivNoDataFoundException)e;
             }
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    /**
     * Return a list of vgrIds corresponding to startIndex->endIndex of persons
     * @param startIndex
     * @param endIndex
     * @return
     * @throws KivNoDataFoundException
     */
    public List<String> getRangePersonsVgrIdPageList(Integer startIndex, Integer endIndex) throws KivNoDataFoundException {
        try {
            List<String> list = getSearchService().getAllPersonsId();
            if (startIndex < 0 || startIndex>endIndex || endIndex < 0 ) {
                throw new SikInternalException(this, "getRangeUnitsPageList(startIndex=" + startIndex +
                        ", endIndex=" + endIndex + ")", "Error input parameters are wrong (result list size=" + list.size() + ")");
            }
            if (endIndex > (list.size()-1)) {
                // It is wrong but let�s continue anyway
                logger.error("MethodName=" + CLASS_NAME + "::" + "getRangeUnitsPageList(startIndex=" + startIndex +
                             ", endIndex=" + endIndex + ") detected that endIndex > ");
                endIndex=list.size()-1;
            }
            List<String> result = new ArrayList<String>();
            for (int position=startIndex; position<=endIndex; position++) {
                try {
                    result.add(list.get(position));
                }
                catch (Exception e) {
                    logger.error("Error in " + CLASS_NAME + "::getRangeUnitsPageList(startIndex=" + startIndex +
                            ", endIndex=" + endIndex + ") index position=" + position + " does not exist as expected", e);
                }
            }            
            return result;
        } catch (Exception e) {
            if (e instanceof KivNoDataFoundException) {
                throw (KivNoDataFoundException)e;
             }
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }
    
    /**
     * Return a list of PagedSearchMetaData objects which chops up the full list in to minor chunks
     * Used in case of indexing all persons. Returns a list of page meta data.
     * @param pageSizeString
     * @return
     * @throws KivNoDataFoundException
     */
    public List<PagedSearchMetaData> getAllPersonsVgrIdPageList(String pageSizeString) throws KivNoDataFoundException {
        try {
            PagedSearchMetaData metaData;
            List<PagedSearchMetaData> result = new ArrayList<PagedSearchMetaData>();
            List<String> personVgrIdList = getSearchService().getAllPersonsId();
            int size = personVgrIdList.size();
            if (isInteger(pageSizeString)) {
                int temp = Integer.parseInt(pageSizeString);
                if (temp>pageSize) {
                    pageSize=temp;// we can only increase the page size
                }
            }
            int index=0;
            if (size > 0) {
                while (index < size) {
                    metaData = new PagedSearchMetaData();
                    metaData.setStartIndex(index); // 0 the first time
                    int endIndex = index+pageSize>size?size-1:(index+pageSize-1);
                    metaData.setEndIndex(endIndex); // e.g. 274 the first time
                    result.add(metaData);
                    index = index + pageSize; // e.g. 275 the first time
                }
            }
            return result;
        } catch (Exception e) {
            if (e instanceof KivNoDataFoundException) {
                throw (KivNoDataFoundException)e;
             }
            e.printStackTrace();
            return new ArrayList<PagedSearchMetaData>();
        }
    }
    
    private Person mapSearchCriteriaToPerson(PersonSearchSimpleForm theForm) throws Exception{
        final String methodName = CLASS_NAME + ".mapSearchCriteriaToPerson(...)";
        logger.info(methodName);
        Person person = new Person();
        
        
        person.setGivenName(theForm.getGivenName()); // given name
        person.setSn(theForm.getSirName()); // sir name
        
        return person;
    }
    
    public void logger() {
        logger.info("Logger");
    }
    
    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }        
}
