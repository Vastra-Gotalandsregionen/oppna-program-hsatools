package se.vgregion.kivtools.search.svc.push;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.impl.mock.SearchCondition;
import se.vgregion.kivtools.search.svc.impl.mock.UnitLdapEntryMock;
import se.vgregion.kivtools.search.svc.push.impl.eniro.InformationPusherEniro;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

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

	@Before
	public void setUp() {
		testFile = new File("test.txt");
		UnitRepository unitRepository = new UnitRepository();
		ldapConnection = new LDAPConnectionMock();
		fillLDAPEntries(ldapConnection);
		unitRepository.setLdapConnectionPool(new LdapConnectionPoolMock(ldapConnection));
		unitRepository.setCodeTablesService(new CodeTablesServiceImpl());
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
			org.easymock.EasyMock.expect(jschMock.getSession(ftpUser, ftpHost, 22)).andReturn(sessionMock);
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
		fillLDAPEntries(ldapConnection);
		informationPusher.setJsch(createEasymockObjects());
		unitInformations = informationPusher.doPushInformation();
		Assert.assertTrue("Array should contain 0 units", unitInformations.size() == 0);
		fillLDAPEntries(ldapConnection);
		Map<SearchCondition, LinkedList<LDAPEntryMock>> availableSearchEntries = ldapConnection.getAvailableSearchEntries();
		addNewUnitToLdap(availableSearchEntries);

		// Reset date in file to the day before yesterday since we want to find
		// the "new" unit.
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 2);
		informationPusher.setJsch(createEasymockObjects());
		unitInformations = informationPusher.doPushInformation();
		Assert.assertTrue("Array should contain 1 units", unitInformations.size() == 1);

	}

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

	@Test
	public void testDoPushInformation() throws Exception {

		informationPusher.doPushInformation();
		File generatedXml = new File("src/test/VGR.xml");
		JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Organization organization = (Organization) unmarshaller.unmarshal(generatedXml);
		Assert.assertTrue("Organization should contain 10 units", organization.getUnit().size() == 10);
	}

	@Test
	public void testGetUnitDetail() {
		informationPusher.getUnitDetail(unitname+"hsaId1");
	}

	private static void fillLDAPEntries(LDAPConnectionMock ldapConnection) {
		LinkedList<LDAPEntryMock> allUnitListSearchEntries = new LinkedList<LDAPEntryMock>();
		// Create search result for search of all units in a Ldap
		for (int i = 0; i < 10; i++) {
			allUnitListSearchEntries.add(new UnitLdapEntryMock(unitname + i, unitname + "hsaId" + i, "vgrOrganizationalUnit", generateCalendar(i).getTime(), generateCalendar(i).getTime()));
		}

		ldapConnection.addLdapEntries(FILTER_CONDITION_ALL_UNITS, allUnitListSearchEntries);
		// Create search result for a unit in a ldap
		for (int i = 0; i < 10; i++) {
			LinkedList<LDAPEntryMock> tmp = new LinkedList<LDAPEntryMock>();
			tmp.add(new UnitLdapEntryMock("unitName" + i, "unitName" + "hsaId" + i, "vgrOrganizationalUnit", generateCalendar(i).getTime(), generateCalendar(i).getTime()));
			ldapConnection.addLdapEntries(new SearchCondition(UnitRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, "(hsaIdentity=" + "unitName" + "hsaId" + i + ")"), tmp);
		}
	}

	private void addNewUnitToLdap(Map<SearchCondition, LinkedList<LDAPEntryMock>> availableSearchEntries) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
		// Add the new unit to the unit list for search all units in ldap
		LinkedList<LDAPEntryMock> oldEntries = availableSearchEntries.get(FILTER_CONDITION_ALL_UNITS);
		oldEntries.add(new UnitLdapEntryMock(unitname + 11, unitname + "hsaId" + 11, "vgrOrganizationalUnit", calendar.getTime(), calendar.getTime()));

		LinkedList<LDAPEntryMock> tmp = new LinkedList<LDAPEntryMock>();
		tmp.add(new UnitLdapEntryMock("unitName" + 11, "unitName" + "hsaId" + 11, "vgrOrganizationalUnit", calendar.getTime(), calendar.getTime()));
		ldapConnection.addLdapEntries(new SearchCondition(UnitRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, "(hsaIdentity=" + "unitName" + "hsaId" + 11 + ")"), tmp);
	}

	private static Calendar generateCalendar(int i) {
		Calendar calendar = Calendar.getInstance();
		// Newest is assumed to be the day before yesterday
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - (i + 1) * 2), 00, 00, 00);
		return calendar;
	}
}
