package se.vgregion.kivtools.search.svc.push;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.push.impl.eniro.InformationPusherEniro;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

import com.domainlanguage.time.TimePoint;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class UnitInformationPusherTest {

	private static InformationPusherEniro informationPusher;
	private static File dateSynchFile;
	private static File unitExistFile;
	private String ftpHost = "";
	private String ftpUser = "";
	private String ftpPassword = "";

	private static List<Unit> unitsInRepository;
	private static Organization organization;

	@Before
	public void setUp() throws Exception {
		UnitRepository unitRepository = createMockUnitRepository(10, false);
		informationPusher = new InformationPusherEniro();

		JSch jschMock = createEasymockObjects();
		informationPusher.setJsch(jschMock);
		informationPusher.setUnitRepository(unitRepository);
		informationPusher.setLastSynchedModifyDateFile(dateSynchFile);
		informationPusher.setLastExistingUnitsFile(unitExistFile);
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
		dateSynchFile.delete();
		unitExistFile.delete();
	}

	@Test
	public void testIncrementalSynchronizaton() throws Exception {
		informationPusher.doService();
		Organization organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 10 units", organizationFromXml.getUnit().size() == 10);
		informationPusher.setJsch(createEasymockObjects());
		// the "new" unit.
		informationPusher.setJsch(createEasymockObjects());
		informationPusher.setUnitRepository(createMockUnitRepository(11, true));
		informationPusher.doService();
		organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 1 units but are: " + organizationFromXml.getUnit().size(), organizationFromXml.getUnit().size() == 1);

	}

	@Test
	public void testUseLastSynchedModifyDateFile() throws Exception {
		// First time we don't have last synched modify date information, should
		// collect all (10) units (regarded as new).
		informationPusher.doService();
		Organization organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 10 units", organizationFromXml.getUnit().size() == 10);
		// When "resetting", last synched modify date information should be read
		// from file and thus we will not find any new units.
		setUp();
		informationPusher.doService();
		organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 0 units", organizationFromXml.getUnit().size() == 0);
	}

	@Test
	public void testTestRemovedUnitsInOrganisation() throws Exception {
		informationPusher.doService();
		Organization organization = getOrganizationFromFile();
		Assert.assertTrue("Organization should contain 10 units", organization.getUnit().size() == 10);
		informationPusher.setUnitRepository(createMockUnitRepository(9, false));
		informationPusher.doService();
		organization = getOrganizationFromFile();
		Assert.assertTrue("Organization should contain 10 units", organization.getUnit().size() == 1);

	}

	@Test
	public void testDoPushInformation() throws Exception {
		informationPusher.doService();
		Organization organization = getOrganizationFromFile();
		Assert.assertTrue("Organization should contain 10 units", organization.getUnit().size() == 10);
	}

	private Organization getOrganizationFromFile() {
		Organization organization = null;
		try {
			File generatedXml = new File("src/test/VGR.xml");
			JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			organization = (Organization) unmarshaller.unmarshal(generatedXml);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return organization;
	}

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
		dateSynchFile = new File("dateSynch.txt");
		unitExistFile = new File("unitExistList");
		dateSynchFile.delete();
		unitExistFile.delete();

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
		Unit[] units = new Unit[unitsToCreate];
		// Generate unit mocks
		for (int i = 0; i < unitsToCreate; i++) {
			Unit unit = new Unit();
			unit.setHsaIdentity(Integer.toString(i));
			DN dn = DN.createDNFromString("o=vgr,ou=" + i);
			unit.setDn(dn);
			String dateStr = "";
			if (i == 0 && createOneWithFreshDate) {
				dateStr = Constants.zuluTimeFormatter.format(new Date());
			} else {
				dateStr = Constants.zuluTimeFormatter.format(generateCalendar(i).getTime());
			}
			unit.setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
			unit.setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
			units[i] = unit;
		}
		EasyMock.expect(mockUnitRepository.getAllUnits()).andReturn(Arrays.asList(units));
		EasyMock.expect(mockUnitRepository.getUnitByDN(DN.createDNFromString("o=vgr"))).andReturn(null);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.replay(mockUnitRepository);
		return mockUnitRepository;
	}
}
