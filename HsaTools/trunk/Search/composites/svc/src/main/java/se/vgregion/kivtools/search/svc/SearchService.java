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
package se.vgregion.kivtools.search.svc;

import java.util.Comparator;
import java.util.List;

import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.DN;

/**
 * @author Hans and Anders
 * @author Jonas Liljenfeldt, Know IT
 *
 */
public interface SearchService {
    /**
     * @param unit
     * OBS! for parameters of type Address (e.g. hsaPostalAddress) the criteria is 
     * considered to be in AdditionalInfo
     * 
     * @return List<Unit> A List of Units
     */
    public SikSearchResultList<Unit> searchUnits(Unit unit) throws Exception;

    /**
     * @param unit
     * OBS! for parameters of type Address (e.g. hsaPostalAddress) the criteria is 
     * considered to be in AdditionalInfo
     * 
     * @return List<Unit> A List of Units
     */
    public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, Comparator<Unit> sortOrder) throws Exception;


    /**
     * @param unit
     * @param maxSearchResult
     * OBS! for parameters of type Address (e.g. hsaPostalAddress) the criteria is 
     * considered to be in AdditionalInfo
     * 
     * @return List<Unit> A List of Units
     */
    public SikSearchResultList<Unit> searchUnits(Unit unit, int maxSearchResult) throws Exception;

    /**
     * @param unit
     * @param maxSearchResult
     * OBS! for parameters of type Address (e.g. hsaPostalAddress) the criteria is 
     * considered to be in AdditionalInfo
     * 
     * @return List<Unit> A List of Units
     */
    public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception;

    /**
     * @param hsaId        hsaId for a Unit
     * @return          the corresponding Unit 
     * @throws Exception
     */
    public Unit getUnitByHsaId(String hsaId) throws Exception;
    
    /**
     * @param dn        dn for a Unit
     * @return          the corresponding Unit 
     * @throws Exception
     */
    public Unit getUnitByDN(DN dn) throws Exception;
    
    /**
     * @param id is complete or part of a person identifier. That is why this method can return a list.
     * 
     * @param id E.g. in case of VGR the id is a vgrId
     * @return
     * @throws Exception
     */
    public SikSearchResultList<Person> searchPersons(String id) throws Exception;

    /**
     * @param id is complete or part of a person identifier. That is why this method can return a list.
     * @param id E.g. in case of VGR the id is a vgrId
     * @param maxSearchResult max number of returned items in the result list
     * @return
     * @throws Exception
     */
    public SikSearchResultList<Person> searchPersons(String id, int maxSearchResult) throws Exception;
    
    /**
     * @param id can be a complete or parts of a vgrId. That is why we can return a list od Persons
     * @param givenName
     * @param familyName
     * @param id E.g. in case of VGR the id is a (part of) a vgrId
     * @return
     * @throws Exception
     */
    public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String id) throws Exception;

    /**
     * @param id can be a complete or parts of a vgrId. That is why we can return a list of Persons
     * @param givenName
     * @param familyName
     * @param maxResult max number of returned items in the result list
     * @return
     * @throws Exception
     */
    public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String id, int maxResult) throws Exception;

    
    /**
     * @param  a person id for the Person (E.g vgrId in case of VGR)
     * @return The Person matching the search criteria (must be exactly one)
     * @throws Exception
     */
    public Person getPersonById(String id) throws Exception;
    
    /**
     * @param personDn  Distinguished name for a Person
     * @return          The Person matching the personDn
     * @throws Exception
     */
    public SikSearchResultList<Employment> getEmployments(String personDn) throws Exception;
       
    /**
     * Returns a list with hsaIdentities of all Units and functions
     * @return
     * @throws Exception
     */
    public List<String> getAllUnitsHsaIdentity() throws Exception;
    
    /**
     * Returns a list with hsaIdentities of all Units and functions filtered with showUnitsWithTheseHsaBussinessClassificationCodes
     * @param showUnitsWithTheseHsaBussinessClassificationCodes
     * @return
     * @throws Exception
     */
    public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception;
    
    /**
     * Returns a list of unique identifiers for all persons.
     * E.g. vgrId in case of VGR
     * @return
     * @throws Exception
     */
    public List<String> getAllPersonsId() throws Exception;
    
    
    /**
     * Returns a list of all persons employed at a Unit
     * @param hsaIdentity the hsaId of the Unit
     * @return
     * @throws Exception
     */
    public SikSearchResultList<Person> getAllPersonsInUnit(String hsaIdentity) throws Exception;
    
    /**
     * Returns a list of all caretypes that is used for current implementation.
     * @return 
     * @throws Exception
     */
    public List<HealthcareType> getHealthcareTypesList() throws Exception;
}