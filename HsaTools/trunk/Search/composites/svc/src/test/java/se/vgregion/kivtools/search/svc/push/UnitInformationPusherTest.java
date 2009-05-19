package se.vgregion.kivtools.search.svc.push;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.SearchCondition;
import se.vgregion.kivtools.search.svc.push.impl.eniro.InformationPusherEniro;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

import com.domainlanguage.time.TimePoint;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.novell.ldap.LDAPConnection;

public class UnitInformationPusherTest {

	private static String GET_ALL_UNIT_FILTER = "(&(|(objectclass=vgrOrganizationalUnit)(objectclass=vgrOrganizationalRole)))";
	private static final SearchCondition FILTER_CONDITION_ALL_UNITS = new SearchCondition(UnitRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, GET_ALL_UNIT_FILTER);
	private static InformationPusherEniro informationPusher;
	private static LDAPConnectionMock ldapConnection;
	private static String unitname = "unitName";
	private static File testFile;
	private String ftpHost = "";
	private String ftpUser = "";
	private String ftpPassword = "";

	private static List<Unit> unitsInRepository;
	private static Organization organization;

	@Before
	public void setUp() throws Exception {
		testFile = new File("test.txt");
		// UnitRepository unitRepository = new UnitRepository();
		UnitRepository unitRepository = createMockUnitRepository(10, false);
		// ldapConnection = new LDAPConnectionMock();
		// fillLDAPEntries(ldapConnection);
		// unitRepository.setLdapConnectionPool(new
		// LdapConnectionPoolMock(ldapConnection));
		// unitRepository.setCodeTablesService(new CodeTablesServiceImpl());
		informationPusher = new InformationPusherEniro();

		JSch jschMock = createEasymockObjects();
		informationPusher.setJsch(jschMock);
		informationPusher.setUnitRepository(unitRepository);
		informationPusher.setLastSynchedModifyDateFile(testFile);
		informationPusher.setDestinationFolder(new File("src/test"));
		informationPusher.setFtpDestinationFolder("test/vgr.xml");
		informationPusher.setFtpHost(ftpHost);
		informationPusher.setFtpUser(ftpUser);
		informationPusher.setFtpPassword(ftpPassword);
	}

	private JSch createEasymockObjects() {
		JSch jschMock = EasyMock.createMock(JSch.class);
		Session sessionMock = EasyMock.createMock(Session.class);
		ChannelSftp channelSftpMock = EasyMock.createMock(ChannelSftp.class);
		try {
			EasyMock.expect(jschMock.getSession(ftpUser, ftpHost, 22)).andReturn(sessionMock);
			sessionMock.setPassword(ftpPassword);
			sessionMock.setConfig("StrictHostKeyChecking", "no");
			sessionMock.connect();
			sessionMock.disconnect();
			EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(channelSftpMock);
			EasyMock.replay(jschMock, sessionMock);
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return jschMock;
	}

	@After
	public void deleteTestFile() {
		testFile.delete();
	}


	@Test
	public void testIncrementalSynchronizaton() throws Exception {
		List<Unit> unitInformations = informationPusher.doPushInformation();
		Assert.assertTrue("Array should contain 10 units", unitInformations.size() == 10);
		// fillLDAPEntries(ldapConnection);
		informationPusher.setJsch(createEasymockObjects());
		informationPusher.setUnitRepository(createMockUnitRepository(10, false));
		unitInformations = informationPusher.doPushInformation();
		Assert.assertTrue("Array should contain 0 units", unitInformations.size() == 0);
		// fillLDAPEntries(ldapConnection);
		//Map<SearchCondition, LinkedList<LDAPEntryMock>> availableSearchEntries = ldapConnection.getAvailableSearchEntries();
		// addNewUnitToLdap(availableSearchEntries);

		// Reset date in file to the day before yesterday since we want to find
		// the "new" unit.
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 2);
		informationPusher.setJsch(createEasymockObjects());
		informationPusher.setUnitRepository(createMockUnitRepository(11, true));
		unitInformations = informationPusher.doPushInformation();
		Assert.assertTrue("Array should contain 1 units", unitInformations.size() == 1);

	}

	@Ignore
	@Test
	public void testUseLastSynchedModifyDateFile() throws Exception {
		// First time we don't have last synched modify date information, should
		// collect all (10) units (regarded as new).
		List<Unit> unitInformations = informationPusher.doPushInformation();
		Assert.assertTrue("Array should contain 10 units", unitInformations.size() == 10);
		// createDateInTestFile(new Date());
		// When "resetting", last synched modify date information should be read
		// from file and thus we will not find any new units.
		setUp();
		unitInformations = informationPusher.doPushInformation();
		Assert.assertTrue("Array should contain 0 units", unitInformations.size() == 0);
	}

	@Ignore
	@Test
	public void testDoPushInformation() throws Exception {

		informationPusher.doPushInformation();
		File generatedXml = new File("src/test/VGR.xml");
		JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Organization organization = (Organization) unmarshaller.unmarshal(generatedXml);
		Assert.assertTrue("Organization should contain 10 units", organization.getUnit().size() == 10);
	}

	@Ignore
	@Test
	public void testGetUnitDetail() {
		informationPusher.getUnitDetail(unitname + "hsaId1");
	}

	/*
	 * private static void fillLDAPEntries(LDAPConnectionMock ldapConnection) {
	 * LinkedList<LDAPEntryMock> allUnitListSearchEntries = new
	 * LinkedList<LDAPEntryMock>(); // Create search result for search of all
	 * units in a Ldap for (int i = 0; i < 10; i++) {
	 * allUnitListSearchEntries.add(new UnitLdapEntryMock(unitname + i, unitname
	 * + "hsaId" + i, "vgrOrganizationalUnit", generateCalendar(i).getTime(),
	 * generateCalendar(i).getTime())); }
	 * 
	 * ldapConnection.addLdapEntries(FILTER_CONDITION_ALL_UNITS,
	 * allUnitListSearchEntries); // Create search result for a unit in a ldap
	 * for (int i = 0; i < 10; i++) { LinkedList<LDAPEntryMock> tmp = new
	 * LinkedList<LDAPEntryMock>(); tmp.add(new UnitLdapEntryMock("unitName" +
	 * i, "unitName" + "hsaId" + i, "vgrOrganizationalUnit",
	 * generateCalendar(i).getTime(), generateCalendar(i).getTime()));
	 * ldapConnection.addLdapEntries(new
	 * SearchCondition(UnitRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB,
	 * "(hsaIdentity=" + "unitName" + "hsaId" + i + ")"), tmp); } }
	 */
	/*
	 * private void addNewUnitToLdap(Map<SearchCondition,
	 * LinkedList<LDAPEntryMock>> availableSearchEntries) { Calendar calendar =
	 * Calendar.getInstance(); calendar.set(Calendar.DATE,
	 * calendar.get(Calendar.DATE) - 1); // Add the new unit to the unit list
	 * for search all units in ldap LinkedList<LDAPEntryMock> oldEntries =
	 * availableSearchEntries.get(FILTER_CONDITION_ALL_UNITS);
	 * oldEntries.add(new UnitLdapEntryMock(unitname + 11, unitname + "hsaId" +
	 * 11, "vgrOrganizationalUnit", calendar.getTime(), calendar.getTime()));
	 * 
	 * LinkedList<LDAPEntryMock> tmp = new LinkedList<LDAPEntryMock>();
	 * tmp.add(new UnitLdapEntryMock("unitName" + 11, "unitName" + "hsaId" + 11,
	 * "vgrOrganizationalUnit", calendar.getTime(), calendar.getTime()));
	 * ldapConnection.addLdapEntries(new
	 * SearchCondition(UnitRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB,
	 * "(hsaIdentity=" + "unitName" + "hsaId" + 11 + ")"), tmp); }
	 */

	@Test
	public void testGenerateOrganisationTree() {

		Organization generatedOrganization = informationPusher.generateOrganisationTree(unitsInRepository);

		for (int i = 0; i < generatedOrganization.getUnit().size(); i++) {
			se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit rootUnit = generatedOrganization.getUnit().get(i);
			se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit expectedRootUnit = organization.getUnit().get(i);
			Assert.assertEquals(rootUnit.getName(), expectedRootUnit.getName());
			for (int j = 0; j < rootUnit.getUnit().size(); j++) {
				se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit childUnit = rootUnit.getUnit().get(j);
				se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit expectedChildUnit = organization.getUnit().get(i).getUnit().get(j);
				Assert.assertEquals(childUnit.getName(), expectedChildUnit.getName());
				for (int x = 0; x < childUnit.getUnit().size(); x++) {
					se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit leafUnit = childUnit.getUnit().get(x);
					se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit expectedLeafUnit = organization.getUnit().get(i).getUnit().get(j).getUnit().get(x);
					Assert.assertEquals(leafUnit.getName(), expectedLeafUnit.getName());
				}
			}
		}
	}

	private static se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit getEniroUnit(Unit domainUnit) {
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit eniroUnit = new se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit();
		eniroUnit.setName(domainUnit.getName());
		eniroUnit.setId(domainUnit.getHsaIdentity());
		return eniroUnit;
	}

	/**
	 * Generate test units
	 */
	@BeforeClass
	public static void getTestOrganisation() {
		// Test data for unit repository
		Unit unitRoot1 = new Unit();
		unitRoot1.setDn(DN.createDNFromString("o=root1"));
		unitRoot1.setName("root1");
		Unit unitRoot2 = new Unit();
		unitRoot2.setDn(DN.createDNFromString("o=root2"));
		unitRoot2.setName("root2");
		Unit unitRoot3 = new Unit();
		unitRoot3.setDn(DN.createDNFromString("o=root3"));
		unitRoot3.setName("root3");
		Unit unitRoot4 = new Unit();
		unitRoot4.setDn(DN.createDNFromString("o=root4"));
		unitRoot4.setName("root4");

		Unit unitChild1 = new Unit();
		unitChild1.setDn(DN.createDNFromString("ou=child1,o=root1"));
		unitChild1.setName("child1");
		Unit unitChild2 = new Unit();
		unitChild2.setDn(DN.createDNFromString("ou=child2,o=root2"));
		unitChild2.setName("child2");
		Unit unitChild3 = new Unit();
		unitChild3.setDn(DN.createDNFromString("ou=child3,o=root3"));
		unitChild3.setName("child3");
		Unit unitChild4 = new Unit();
		unitChild4.setDn(DN.createDNFromString("ou=child4,o=root4"));
		unitChild4.setName("child4");
		Unit unitChild5 = new Unit();
		unitChild5.setDn(DN.createDNFromString("ou=child5,o=root4"));
		unitChild5.setName("child5");

		Unit unitLeaf1 = new Unit();
		unitLeaf1.setDn(DN.createDNFromString("ou=leaf1,ou=child1,o=root1"));
		unitLeaf1.setName("leaf1");
		Unit unitLeaf2 = new Unit();
		unitLeaf2.setDn(DN.createDNFromString("ou=leaf2,ou=child2,o=root2"));
		unitLeaf2.setName("leaf2");
		Unit unitLeaf3 = new Unit();
		unitLeaf3.setDn(DN.createDNFromString("ou=leaf3,ou=child3,o=root3"));
		unitLeaf3.setName("leaf3");
		Unit unitLeaf4 = new Unit();
		unitLeaf4.setDn(DN.createDNFromString("ou=leaf4,ou=child3,o=root3"));
		unitLeaf4.setName("leaf4");
		Unit unitLeaf5 = new Unit();
		unitLeaf5.setDn(DN.createDNFromString("ou=leaf5,ou=child4,o=root4"));
		unitLeaf5.setName("leaf5");
		Unit unitLeaf6 = new Unit();
		unitLeaf6.setDn(DN.createDNFromString("ou=leaf6,ou=child5,o=root4"));
		unitLeaf6.setName("leaf6");

		unitsInRepository = Arrays.asList(unitRoot1, unitRoot2, unitRoot3, unitRoot4, unitChild1, unitChild2, unitChild3, unitChild4, unitChild5, unitLeaf1, unitLeaf2, unitLeaf3, unitLeaf4,
				unitLeaf5, unitLeaf6);

		// Generate test units for verifying test result data
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitRoot1Eniro = getEniroUnit(unitRoot1);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitRoot2Eniro = getEniroUnit(unitRoot2);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitRoot3Eniro = getEniroUnit(unitRoot3);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitRoot4Eniro = getEniroUnit(unitRoot4);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitChild1Eniro = getEniroUnit(unitChild1);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitChild2Eniro = getEniroUnit(unitChild2);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitChild3Eniro = getEniroUnit(unitChild3);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitChild4Eniro = getEniroUnit(unitChild4);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitChild5Eniro = getEniroUnit(unitChild5);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitLeaf1Eniro = getEniroUnit(unitLeaf1);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitLeaf2Eniro = getEniroUnit(unitLeaf2);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitLeaf3Eniro = getEniroUnit(unitLeaf3);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitLeaf4Eniro = getEniroUnit(unitLeaf4);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitLeaf5Eniro = getEniroUnit(unitLeaf5);
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitLeaf6Eniro = getEniroUnit(unitLeaf6);

		organization = new Organization();
		organization.getUnit().addAll(Arrays.asList(unitRoot1Eniro, unitRoot2Eniro, unitRoot3Eniro, unitRoot4Eniro));
		unitRoot1Eniro.getUnit().add(unitChild1Eniro);
		unitChild1Eniro.getUnit().add(unitLeaf1Eniro);

		unitRoot2Eniro.getUnit().add(unitChild2Eniro);
		unitChild2Eniro.getUnit().add(unitLeaf2Eniro);

		unitRoot3Eniro.getUnit().add(unitChild3Eniro);
		unitChild3Eniro.getUnit().add(unitLeaf3Eniro);
		unitChild3Eniro.getUnit().add(unitLeaf4Eniro);

		unitRoot4Eniro.getUnit().add(unitChild4Eniro);
		unitRoot4Eniro.getUnit().add(unitChild5Eniro);
		unitChild4Eniro.getUnit().add(unitLeaf5Eniro);
		unitChild5Eniro.getUnit().add(unitLeaf6Eniro);
	}

	private static Calendar generateCalendar(int i) {
		Calendar calendar = Calendar.getInstance();
		// Newest is assumed to be the day before yesterday
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - (i + 1) * 2), 00, 00, 00);
		return calendar;
	}

	// This is a beginning of a mock test
	private UnitRepository createMockUnitRepository(int unitsToCreate, boolean createOneWithFreshDate) throws Exception {
		UnitRepository mockUnitRepository = EasyMock.createMock(UnitRepository.class);
		// Generate unit hsaidentity mocks
		String[] nbOfUnits = new String[unitsToCreate];
		for (int i = 0; i < unitsToCreate; i++) {
			nbOfUnits[i] = "" + i;
		}
		EasyMock.expect(mockUnitRepository.getAllUnitsHsaIdentity()).andReturn(Arrays.asList(nbOfUnits));
		// Generate unit mocks
		for (int i = 0; i < unitsToCreate; i++) {
			Unit unit = new Unit();
			unit.setHsaIdentity(Integer.toString(i));
			String dateStr = "";
			if (i == 0 && createOneWithFreshDate) {
				dateStr = Constants.zuluTimeFormatter.format(new Date());
			} else {
				dateStr = Constants.zuluTimeFormatter.format(generateCalendar(i).getTime());
			}
			unit.setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
			unit.setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
			EasyMock.expect(mockUnitRepository.getUnitByHsaId(Integer.toString(i))).andReturn(unit);
		}
		EasyMock.replay(mockUnitRepository);
		return mockUnitRepository;
	}
}
