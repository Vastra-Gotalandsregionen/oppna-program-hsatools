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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.naming.directory.SearchControls;
import javax.xml.ws.BindingProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfFunction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfString;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionDirectory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebService;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebService_Service;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.thoughtworks.xstream.XStream;

@Ignore
public class KivwsOfUnitRepositoryTest  {
    private UnitRepository unitRepository;
    private UnitMapper unitMapper;
    private LdapTemplate ldapTemplate;
    private VGRegionWebService_Service regionWebServiceService;
    private VGRegionWebService vgRegionWebServiceImplPort;
    private static final String[] ATTRIBUTES = new String[] { "*", "objectClass", "createTimestamp" };
    private CodeTablesServiceImpl codeTablesService;
    private DisplayValueTranslator displayValueTranslator;

    @Before
    public void setUp() throws Exception {
        setupTimeSource();
        // Instantiate HealthcareTypeConditionHelper
        HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
            {
                super.resetInternalCache();
            }
        };
        healthcareTypeConditionHelper.setImplResourcePath("basic_healthcaretypeconditionhelper");

        displayValueTranslator = new DisplayValueTranslator();
        displayValueTranslator.setTranslationMap(new HashMap<String, String>());

        // Load propety file for ldap connection.
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader
                .getResource("classpath:se/vgregion/kivtools/search/svc/impl/kiv/ldap/search-composite-svc-connection.properties");
        Properties loadAllProperties = PropertiesLoaderUtils.loadProperties(resource);

        // Create LdapTemplate
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("ldap://" + loadAllProperties.getProperty("hsatools.search.svc.ldap.ldaphost"));
        ldapContextSource.setPassword(loadAllProperties.getProperty("hsatools.search.svc.ldap.password"));
        ldapContextSource.setUserDn(loadAllProperties.getProperty("hsatools.search.svc.ldap.logindn"));
        ldapContextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate(ldapContextSource);
        codeTablesService = new CodeTablesServiceImpl(ldapTemplate);

        // Create KIVWS webservice.
        regionWebServiceService = new VGRegionWebService_Service();
        vgRegionWebServiceImplPort = regionWebServiceService.getVGRegionWebServiceImplPort();

        // Setup username and password authentication for webservice.
        BindingProvider bindingProvider = (BindingProvider) vgRegionWebServiceImplPort;
        bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
                loadAllProperties.getProperty("hsatools.search.svc.kivws.username"));
        bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
                loadAllProperties.getProperty("hsatools.search.svc.kivws.password"));

        // Create unitmapper to be used in UnitRepository.
        unitMapper = new UnitMapper(codeTablesService, displayValueTranslator);
        unitRepository = new UnitRepository();
        // unitRepository.setLdapTemplate(ldapTemplateMock);
        unitRepository.setUnitMapper(unitMapper);
    }
    
    public static void main(String[] args) throws Exception {
        KivwsOfUnitRepositoryTest kivwsOfUnitRepositoryTest = new KivwsOfUnitRepositoryTest();
        kivwsOfUnitRepositoryTest.setUp();
        // Snapshot of Akutvard.
        kivwsOfUnitRepositoryTest.writeObjectToXml("(|(hsaBusinessClassificationCode=1000)(hsaBusinessClassificationCode=1100)(hsaBusinessClassificationCode=1500)(hsaBusinessClassificationCode=1600)(hsaBusinessClassificationCode=1800)(hsaBusinessClassificationCode=1801)(hsaBusinessClassificationCode=1812))","emergencyUnits.xml", false);
        // Snapshot of Vardcentral.
        kivwsOfUnitRepositoryTest.writeObjectToXml("(hsaBusinessType=02)","careUnits.xml", false);
        // Snapshot of Tandvard
        kivwsOfUnitRepositoryTest.writeObjectToXml("(|(hsaBusinessClassificationCode=1000)(hsaBusinessClassificationCode=1100)(hsaBusinessClassificationCode=1500)(hsaBusinessClassificationCode=1600)(hsaBusinessClassificationCode=1800)(hsaBusinessClassificationCode=1801)(hsaBusinessClassificationCode=1812))","dentalUnits.xml", false);
    }

    @After
    public void tearDown() {
        new HealthcareTypeConditionHelper() {
            {
                super.resetInternalCache();
            }
        };
    }

    private void setupTimeSource() {
        TimeUtil.setTimeSource(new TimeSource() {
            private long millis;

            {
                Calendar cal = Calendar.getInstance();
                cal.set(2009, 0, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                millis = cal.getTimeInMillis();
            }

            @Override
            public long millis() {
                return millis;
            }
        });
    }

    @Test
    public void testKivwsResponseTime() throws VGRException_Exception {
        long startTime = System.currentTimeMillis();
        ArrayOfUnit searchUnit = vgRegionWebServiceImplPort.searchUnit("(ou=Vårdcentralen Majorna)",
                new ArrayOfString(), VGRegionDirectory.KIV, null);
        long endTime = System.currentTimeMillis();
        System.out.println("Antal ms :" + (endTime - startTime));
        assertTrue(searchUnit.getUnit().size() > 0);
    }

    // Test return all public Units
    @Test
    public void testGetAllPublicUnits() throws VGRException_Exception {
        String allUnitsQuery = getAllUnitsQuery(true);
        String kivwsQuery = "(hsaDestinationIndicator=03)";
        compareKivwsAndLdapResults(allUnitsQuery, kivwsQuery, kivwsQuery);
    }

    // Test return all Units
    @Test
    public void testGetAllUnits() throws VGRException_Exception {
        String allUnitsQuery = getAllUnitsQuery(false);
        String kivwsQuery = "(ou=*)";
        compareKivwsAndLdapResults(allUnitsQuery, kivwsQuery, kivwsQuery);
    }

    private String getAllUnitsQuery(boolean onlyPublicUnits) {
        String searchFilter = "(|(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")(objectclass="
                + Constants.OBJECT_CLASS_FUNCTION_SPECIFIC + "))";
        List<String> filterList = new ArrayList<String>();
        if (onlyPublicUnits) {
            filterList.add("(hsaDestinationIndicator=03)");
        }
        filterList.add(searchFilter);
        // (&(par3=value3)(par4=value4
        searchFilter = makeAnd(filterList);
        return searchFilter;
    }

    private String makeAnd(List<String> orFilterList) {
        String orCriterias = "";
        if (!orFilterList.isEmpty()) {
            orCriterias += "(&";
            for (String s : orFilterList) {
                orCriterias += s;
            }
            orCriterias += ")";
        }
        return orCriterias;
    }

    // Search for unit with Sahlgrenska as name and Göteborg as location.
    @Test
    public void testSearchUnit() throws KivException, VGRException_Exception {
        String kivwsLdapQueryWithOU = "(&(ou=*Sahlgrenska*)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*))))";
        String kivwsLdapQueryWithCN = "(&(cn=*Sahlgrenska*)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*))))";
        String originalLdapQuery = "(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*Sahlgrenska*)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))))(&(objectclass=vgrOrganizationalRole)(&(cn=*Sahlgrenska*)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*))))))";
        compareKivwsAndLdapResults(originalLdapQuery, kivwsLdapQueryWithOU, kivwsLdapQueryWithCN);
    }

    // Fungerar inte vet inte varför wildcard sökning av Göteborg inte fungerar i hsaPostalAddress arrayen likaså
    // hsaPostalAddress
    @Test
    public void testSearchUnitExactMatch() throws KivException, VGRException_Exception {
        String kivwsLdapQueryWithOU = "(&(ou=Bårhus Sahlgrenska)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=Göteborg$*$*$*$*$*)(hsaPostalAddress=*$Göteborg$*$*$*$*)(hsaPostalAddress=*$*$Göteborg$*$*$*)(hsaPostalAddress=*$*$*$Göteborg$*$*)(hsaPostalAddress=*$*$*$*$Göteborg$*)(hsaPostalAddress=*$*$*$*$*$Göteborg))(|(hsaStreetAddress=Göteborg$*$*$*$*$*)(hsaStreetAddress=*$Göteborg$*$*$*$*)(hsaStreetAddress=*$*$Göteborg$*$*$*)(hsaStreetAddress=*$*$*$Göteborg$*$*)(hsaStreetAddress=*$*$*$*$Göteborg$*)(hsaStreetAddress=*$*$*$*$*$Göteborg))))";
        String kivwsLdapQueryWithCN = "(&(cn=Bårhus Sahlgrenska)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=Göteborg$*$*$*$*$*)(hsaPostalAddress=*$Göteborg$*$*$*$*)(hsaPostalAddress=*$*$Göteborg$*$*$*)(hsaPostalAddress=*$*$*$Göteborg$*$*)(hsaPostalAddress=*$*$*$*$Göteborg$*)(hsaPostalAddress=*$*$*$*$*$Göteborg))(|(hsaStreetAddress=Göteborg$*$*$*$*$*)(hsaStreetAddress=*$Göteborg$*$*$*$*)(hsaStreetAddress=*$*$Göteborg$*$*$*)(hsaStreetAddress=*$*$*$Göteborg$*$*)(hsaStreetAddress=*$*$*$*$Göteborg$*)(hsaStreetAddress=*$*$*$*$*$Göteborg))))";
        String originalLdapQuery = "(|(&(objectclass=vgrOrganizationalUnit)(&(ou=Bårhus Sahlgrenska)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=Göteborg$*$*$*$*$*)(hsaPostalAddress=*$Göteborg$*$*$*$*)(hsaPostalAddress=*$*$Göteborg$*$*$*)(hsaPostalAddress=*$*$*$Göteborg$*$*)(hsaPostalAddress=*$*$*$*$Göteborg$*)(hsaPostalAddress=*$*$*$*$*$Göteborg))(|(hsaStreetAddress=Göteborg$*$*$*$*$*)(hsaStreetAddress=*$Göteborg$*$*$*$*)(hsaStreetAddress=*$*$Göteborg$*$*$*)(hsaStreetAddress=*$*$*$Göteborg$*$*)(hsaStreetAddress=*$*$*$*$Göteborg$*)(hsaStreetAddress=*$*$*$*$*$Göteborg)))))(&(objectclass=vgrOrganizationalRole)(&(cn=Bårhus Sahlgrenska)(|(hsaMunicipalityCode=1480)(|(hsaPostalAddress=Göteborg$*$*$*$*$*)(hsaPostalAddress=*$Göteborg$*$*$*$*)(hsaPostalAddress=*$*$Göteborg$*$*$*)(hsaPostalAddress=*$*$*$Göteborg$*$*)(hsaPostalAddress=*$*$*$*$Göteborg$*)(hsaPostalAddress=*$*$*$*$*$Göteborg))(|(hsaStreetAddress=Göteborg$*$*$*$*$*)(hsaStreetAddress=*$Göteborg$*$*$*$*)(hsaStreetAddress=*$*$Göteborg$*$*$*)(hsaStreetAddress=*$*$*$Göteborg$*$*)(hsaStreetAddress=*$*$*$*$Göteborg$*)(hsaStreetAddress=*$*$*$*$*$Göteborg))))))";
        compareKivwsAndLdapResults(originalLdapQuery, kivwsLdapQueryWithOU, kivwsLdapQueryWithCN);
    }

    // Fungerar ej vgrA03 saknas
    @Test
    public void testSearchUnitOtherParams() throws KivException, VGRException_Exception {
        String ldapQuery = "(|(&(objectclass=vgrOrganizationalUnit)(&(vgrAO3kod=603)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1502)(vgrCareType=01)))(&(objectclass=vgrOrganizationalRole)(&(vgrAO3kod=603)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1502)(vgrCareType=01))))";
        String kivwsQuery = "(&(vgrAO3kod=603)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1502)(vgrCareType=01))";
        compareKivwsAndLdapResults(ldapQuery, kivwsQuery, null);
    }

    // @Test
    // public void testSearchAdvancedUnits() throws Exception {
    //
    // String ldapQuery =
    // "(&(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*Sahlgrenska*)(|(hsaMunicipalityCode=10032)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2)))))(&(objectclass=vgrOrganizationalRole)(&(cn=*unitName*)(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2)))))))";
    // String kivwsQuery =
    // "(&(|(&(ou=*unitName*)(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2)))))(&(objectclass=vgrOrganizationalRole)(&(cn=*unitName*)(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2))))))";
    //
    // compareKivwsAndLdapResults(ldapQuery, kivwsQuery, null);
    // }

    @Test
    public void testSearchUnits() throws Exception {

        String kivwsWithOU = "(&(hsaIdentity=*SE2321000131*E000000000109*)(ou=*Sahlgrenska*)(hsaBusinessClassificationCode=3022))";
        String kivwsWithCN = "(&(hsaIdentity=*SE2321000131*E000000000109*)(cn=*Sahlgrenska*)(hsaBusinessClassificationCode=3022))";

        String ldapQuery = "(|(&(objectclass=vgrOrganizationalUnit)(&(hsaIdentity=*SE2321000131*E000000000109*)(ou=*Sahlgrenska*)(hsaBusinessClassificationCode=3022)))(&(objectclass=vgrOrganizationalRole)(&(hsaIdentity=*SE2321000131*E000000000109*)(cn=*Sahlgrenska*)(hsaBusinessClassificationCode=3022))))";
        compareKivwsAndLdapResults(ldapQuery, kivwsWithOU, kivwsWithCN);
    }

    // TODO: Don't know what to do!!
    // Test fetching sub units for a chosen unit
    @Test
    public void testGetSubUnits() throws Exception {
        String base = "ou=Folktandvården Fyrbodal,ou=Folktandvården Västra Götaland,ou=Org,o=vgr";

        DistinguishedName parentDn = new DistinguishedName(base);

        // Since UnitMapper return a Unit we are certain that the cast to List<Unit> is ok
        @SuppressWarnings("unchecked")
        List<Unit> unitsFromLdap = ldapTemplate
                .search(parentDn.toString(), "(objectClass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")",
                        SearchControls.SUBTREE_SCOPE, ATTRIBUTES, unitMapper);

        ArrayOfUnit unitsFromkivsw = vgRegionWebServiceImplPort.searchUnit("(ou=*)", new ArrayOfString(),
                VGRegionDirectory.KIV, base);

        assertEquals(unitsFromkivsw.getUnit().size(), unitsFromLdap.size());
    }

    @Test
    public void testGetUnitByDN() throws KivException, VGRException_Exception {
        String dn = "ou=Vårdcentralen Angered";// ,ou=Org,o=VGR";

        // ldapTemplateMock.addBoundDN(new DistinguishedName("ou=Vårdcentralen Angered"), new
        // DirContextOperationsMock());
        // unitRepository.getUnitByDN(DN.createDNFromString(dn));
        // assertEquals(DN.createDNFromString(dn).toString(), ldapTemplateMock.getDn());

        compareKivwsAndLdapResults(DN.createDNFromString(dn).toString(), DN.createDNFromString(dn).toString(),
                null);
    }

    @SuppressWarnings("unchecked")
    private void compareKivwsAndLdapResults(String originalLdapQuery, String kivwsLdapQueryWithOU,
            String kivwsLdapQueryWithCN) throws VGRException_Exception {

        // Make query agianst ldap.
        List<Unit> unitsFromldap = ldapTemplate.search(unitRepository.getSearchBase(), originalLdapQuery,
                SearchControls.SUBTREE_SCOPE, ATTRIBUTES, unitMapper);
        // Make query against kivws
        ArrayOfUnit kivswSearchUnit = vgRegionWebServiceImplPort.searchUnit(kivwsLdapQueryWithOU,
                new ArrayOfString(), VGRegionDirectory.KIV, null);

        int kivwsSearchFunctionSize = 0;
        // Make query agianst kivws function method if no result from above
        ArrayOfFunction kivswSearchFunctions = new ArrayOfFunction();
        if (kivwsLdapQueryWithCN != null) {
            kivswSearchFunctions = vgRegionWebServiceImplPort.searchFunction(kivwsLdapQueryWithCN,
                    new ArrayOfString(), VGRegionDirectory.KIV, null);
            kivwsSearchFunctionSize = kivswSearchFunctions.getFunction().size();
        }
        int kivwsSearchSize = kivswSearchUnit.getUnit().size();
        kivwsSearchSize += kivwsSearchFunctionSize;

        int ldapSearchSize = unitsFromldap.size();
        assertEquals(ldapSearchSize, kivwsSearchSize);

        Collections.sort(unitsFromldap);
        List<se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit> kivwsunit = kivswSearchUnit.getUnit();
        List<Function> kivwsFunction = kivswSearchFunctions.getFunction();

        KivwsUnitMapper kivwsUnitMapper = new KivwsUnitMapper(codeTablesService, displayValueTranslator);
        List<Unit> kivwsMappedUnits = new ArrayList<Unit>();

        // Populate Unit from function.
        for (Function function : kivwsFunction) {
            Unit mapFromContext = kivwsUnitMapper.mapFromContext(function);
            kivwsMappedUnits.add(mapFromContext);
        }

        // Populate Unit from unit
        for (se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit unit2 : kivwsunit) {
            Unit mapFromContext = kivwsUnitMapper.mapFromContext(unit2);
            kivwsMappedUnits.add(mapFromContext);
        }
        Collections.sort(kivwsMappedUnits);

        for (int i = 0; i < unitsFromldap.size(); i++) {
            Unit unitFromLdap = unitsFromldap.get(i);
            Unit unitFromKivws = kivwsMappedUnits.get(i);
            assertTrue(unitFromLdap.equals(unitFromKivws));
        }
    }

    private void writeObjectToXml(String searchQuery, String fileName, boolean generateFromKivldap) {

        if (generateFromKivldap) {  
            // Make query agianst ldap.
            ldapTemplate.search(unitRepository.getSearchBase(), searchQuery, SearchControls.SUBTREE_SCOPE,
                    ATTRIBUTES, new UnitMapperWriteToFile(fileName, codeTablesService, displayValueTranslator));
        } else {

            
            try {
                
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file);
                XStream xStream = new XStream();
                ObjectOutputStream createObjectOutputStream = xStream.createObjectOutputStream(fileWriter);
               
                
                if (searchQuery.contains("cn=")){
                    // Make query against kivws
                    ArrayOfFunction kivswSearchFunction = vgRegionWebServiceImplPort.searchFunction(searchQuery,
                            new ArrayOfString(), VGRegionDirectory.KIV, null);
                    createObjectOutputStream.writeObject(kivswSearchFunction);
                }else {
                    
                    // Make query against kivws
                    ArrayOfUnit kivswSearchUnit = vgRegionWebServiceImplPort.searchUnit(searchQuery,
                            new ArrayOfString(), VGRegionDirectory.KIV, null);
                    createObjectOutputStream.writeObject(kivswSearchUnit);
                }

     
                createObjectOutputStream.flush();
                createObjectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (VGRException_Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    class UnitMapperWriteToFile extends UnitMapper {

        private final String fileName;

        public UnitMapperWriteToFile(String fileName, CodeTablesService codeTablesService,
                DisplayValueTranslator displayValueTranslator) {
            super(codeTablesService, displayValueTranslator);
            this.fileName = fileName;
        }

        @Override
        public Unit mapFromContext(Object ctx) {
            try {
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file);
                XStream xStream = new XStream();
                ObjectOutputStream createObjectOutputStream = xStream.createObjectOutputStream(fileWriter);
                createObjectOutputStream.writeObject(ctx);
                createObjectOutputStream.flush();
                createObjectOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return super.mapFromContext(ctx);
        }
    }
}
