package se.vgregion.kivtools.search.svc.push;

import java.io.File;
import java.util.ArrayList;
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
import org.junit.Ignore;
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
		UnitRepository unitRepository = createMockUnitRepositoryFull(false, getTestUnits());

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
			EasyMock.expectLastCall().anyTimes();
			sessionMock.setPassword(ftpPassword);
			EasyMock.expectLastCall().anyTimes();
			sessionMock.setConfig("StrictHostKeyChecking", "no");
			EasyMock.expectLastCall().anyTimes();
			sessionMock.connect();
			EasyMock.expectLastCall().anyTimes();
			sessionMock.disconnect();
			EasyMock.expectLastCall().anyTimes();
			EasyMock.expect(sessionMock.openChannel("sftp")).andReturn(channelSftpMock);
			EasyMock.expectLastCall().anyTimes();
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
	public void testIncrementalSynchronization() throws Exception {
		informationPusher.doService();
		Organization organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 4 root units", organizationFromXml.getUnit().size() == 4);
		informationPusher.setJsch(createEasymockObjects());
		// the "new" unit.
		informationPusher.setJsch(createEasymockObjects());

		Unit unitLeaf7 = new Unit();
		unitLeaf7.setDn(DN.createDNFromString("ou=leaf7,ou=child5,o=root4"));
		unitLeaf7.setName("leaf7");
		List<Unit> units = new ArrayList<Unit>(unitsInRepository);
		units.add(unitLeaf7);
		unitsInRepository = units;

		informationPusher.setUnitRepository(createMockUnitRepositoryFull(true, getTestUnits()));
		informationPusher.doService();
		organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 1 unit but are: " + organizationFromXml.getUnit().size(), organizationFromXml.getUnit().size() == 1);
		// HsaId 8 corresponds to child5 in unitsInRepository, i.e parent of
		// leaf7
		Assert.assertEquals("child5-id", organizationFromXml.getUnit().get(0).getParentUnitId());
	}

	@Test
	public void testUseLastSynchedModifyDateFile() throws Exception {
		// First time we don't have last synched modify date information, should
		// collect all (15) units (regarded as new) but only 4 root units.
		informationPusher.doService();
		Organization organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 4 root units", organizationFromXml.getUnit().size() == 4);
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
		Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
		for (se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unit : organization.getUnit()) {
			Assert.assertEquals("create", unit.getOperation());
		}
		// Remove one unit
		List<Unit> units = getTestUnits();
		units.remove(0);
		informationPusher.setUnitRepository(createMockUnitRepositoryFull(false,units));
		informationPusher.doService();
		organization = getOrganizationFromFile();
		Assert.assertTrue("Organization should contain 1 units, not " + organization.getUnit().size() + ".", organization.getUnit().size() == 1);
		Assert.assertEquals("remove", organization.getUnit().get(0).getOperation());
	}

	@Test
	public void testDoPushInformation() throws Exception {
		informationPusher.doService();
		Organization organization = getOrganizationFromFile();
		Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
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
	public void testGenerateOrganisationTree() throws Exception {

		informationPusher.setUnitRepository(createMockUnitRepositoryFull(false, getTestUnits()));
		informationPusher.doService();
		Organization generatedOrganization = getOrganizationFromFile();

//		int nbrOfRootUnits = 0;
//		int nbrOfChildUnits = 0;
//		int nbrOfLeafUnits = 0;
//		for (se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unit : generatedOrganization.getUnit()) {
//			nbrOfRootUnits++;
//			Assert.assertEquals("root" + nbrOfRootUnits + "-id", unit.getId());
//			for (se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit childUnit : unit.getUnit()) {
//				nbrOfChildUnits++;
//				Assert.assertEquals("child" + nbrOfRootUnits + "-id", childUnit.getId());
//				for (int i = 0; i < childUnit.getUnit().size() ; i++) {
//					nbrOfLeafUnits++;
//					se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit leafUnit = childUnit.getUnit().get(i);
//					Assert.assertEquals("leaf" + (i+1) + "-id", leafUnit.getId());
//				}
//
//			}
//		}

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
		DN.setAdministrationLevel(-1);

		// Test data for unit repository
		Unit unitRoot1 = new Unit();
		unitRoot1.setDn(DN.createDNFromString("o=root1"));
		unitRoot1.setName("root1");
		unitRoot1.setHsaIdentity("root1-id");
		Unit unitRoot2 = new Unit();
		unitRoot2.setDn(DN.createDNFromString("o=root2"));
		unitRoot2.setName("root2");
		unitRoot2.setHsaIdentity("root2-id");
		Unit unitRoot3 = new Unit();
		unitRoot3.setDn(DN.createDNFromString("o=root3"));
		unitRoot3.setName("root3");
		unitRoot3.setHsaIdentity("root3-id");
		Unit unitRoot4 = new Unit();
		unitRoot4.setDn(DN.createDNFromString("o=root4"));
		unitRoot4.setName("root4");
		unitRoot4.setHsaIdentity("root4-id");

		Unit unitChild1 = new Unit();
		unitChild1.setDn(DN.createDNFromString("ou=child1,o=root1"));
		unitChild1.setName("child1");
		unitChild1.setHsaIdentity("child1-id");
		Unit unitChild2 = new Unit();
		unitChild2.setDn(DN.createDNFromString("ou=child2,o=root2"));
		unitChild2.setName("child2");
		unitChild2.setHsaIdentity("child2-id");
		Unit unitChild3 = new Unit();
		unitChild3.setDn(DN.createDNFromString("ou=child3,o=root3"));
		unitChild3.setName("child3");
		unitChild3.setHsaIdentity("child3-id");
		Unit unitChild4 = new Unit();
		unitChild4.setDn(DN.createDNFromString("ou=child4,o=root4"));
		unitChild4.setName("child4");
		unitChild4.setHsaIdentity("child4-id");
		Unit unitChild5 = new Unit();
		unitChild5.setDn(DN.createDNFromString("ou=child5,o=root4"));
		unitChild5.setName("child5");
		unitChild5.setHsaIdentity("child5-id");

		Unit unitLeaf1 = new Unit();
		unitLeaf1.setDn(DN.createDNFromString("ou=leaf1,ou=child1,o=root1"));
		unitLeaf1.setName("leaf1");
		unitLeaf1.setHsaIdentity("leaf1-id");
		Unit unitLeaf2 = new Unit();
		unitLeaf2.setDn(DN.createDNFromString("ou=leaf2,ou=child2,o=root2"));
		unitLeaf2.setName("leaf2");
		unitLeaf2.setHsaIdentity("leaf2-id");
		Unit unitLeaf3 = new Unit();
		unitLeaf3.setDn(DN.createDNFromString("ou=leaf3,ou=child3,o=root3"));
		unitLeaf3.setName("leaf3");
		unitLeaf3.setHsaIdentity("leaf3-id");
		Unit unitLeaf4 = new Unit();
		unitLeaf4.setDn(DN.createDNFromString("ou=leaf4,ou=child3,o=root3"));
		unitLeaf4.setName("leaf4");
		unitLeaf4.setHsaIdentity("leaf4-id");
		Unit unitLeaf5 = new Unit();
		unitLeaf5.setDn(DN.createDNFromString("ou=leaf5,ou=child4,o=root4"));
		unitLeaf5.setName("leaf5");
		unitLeaf5.setHsaIdentity("leaf5-id");
		Unit unitLeaf6 = new Unit();
		unitLeaf6.setDn(DN.createDNFromString("ou=leaf6,ou=child5,o=root4"));
		unitLeaf6.setName("leaf6");
		unitLeaf6.setHsaIdentity("leaf6-id");

		unitsInRepository = new ArrayList<Unit>(Arrays.asList(unitRoot1, unitRoot2, unitRoot3, unitRoot4, unitChild1, unitChild2, unitChild3, unitChild4, unitChild5, unitLeaf1, unitLeaf2, unitLeaf3,
				unitLeaf4, unitLeaf5, unitLeaf6));

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

	private static List<Unit> getTestUnits() {

		// Test data for unit repository
		Unit unitRoot1 = new Unit();
		unitRoot1.setDn(DN.createDNFromString("o=root1"));
		unitRoot1.setName("root1");
		unitRoot1.setHsaIdentity("root1-id");
		Unit unitRoot2 = new Unit();
		unitRoot2.setDn(DN.createDNFromString("o=root2"));
		unitRoot2.setName("root2");
		unitRoot2.setHsaIdentity("root2-id");
		Unit unitRoot3 = new Unit();
		unitRoot3.setDn(DN.createDNFromString("o=root3"));
		unitRoot3.setName("root3");
		unitRoot3.setHsaIdentity("root3-id");
		Unit unitRoot4 = new Unit();
		unitRoot4.setDn(DN.createDNFromString("o=root4"));
		unitRoot4.setName("root4");
		unitRoot4.setHsaIdentity("root4-id");

		Unit unitChild1 = new Unit();
		unitChild1.setDn(DN.createDNFromString("ou=child1,o=root1"));
		unitChild1.setName("child1");
		unitChild1.setHsaIdentity("child1-id");
		Unit unitChild2 = new Unit();
		unitChild2.setDn(DN.createDNFromString("ou=child2,o=root2"));
		unitChild2.setName("child2");
		unitChild2.setHsaIdentity("child2-id");
		Unit unitChild3 = new Unit();
		unitChild3.setDn(DN.createDNFromString("ou=child3,o=root3"));
		unitChild3.setName("child3");
		unitChild3.setHsaIdentity("child3-id");
		Unit unitChild4 = new Unit();
		unitChild4.setDn(DN.createDNFromString("ou=child4,o=root4"));
		unitChild4.setName("child4");
		unitChild4.setHsaIdentity("child4-id");
		Unit unitChild5 = new Unit();
		unitChild5.setDn(DN.createDNFromString("ou=child5,o=root4"));
		unitChild5.setName("child5");
		unitChild5.setHsaIdentity("child5-id");

		Unit unitLeaf1 = new Unit();
		unitLeaf1.setDn(DN.createDNFromString("ou=leaf1,ou=child1,o=root1"));
		unitLeaf1.setName("leaf1");
		unitLeaf1.setHsaIdentity("leaf1-id");
		Unit unitLeaf2 = new Unit();
		unitLeaf2.setDn(DN.createDNFromString("ou=leaf2,ou=child2,o=root2"));
		unitLeaf2.setName("leaf2");
		unitLeaf2.setHsaIdentity("leaf2-id");
		Unit unitLeaf3 = new Unit();
		unitLeaf3.setDn(DN.createDNFromString("ou=leaf3,ou=child3,o=root3"));
		unitLeaf3.setName("leaf3");
		unitLeaf3.setHsaIdentity("leaf3-id");
		Unit unitLeaf4 = new Unit();
		unitLeaf4.setDn(DN.createDNFromString("ou=leaf4,ou=child3,o=root3"));
		unitLeaf4.setName("leaf4");
		unitLeaf4.setHsaIdentity("leaf4-id");
		Unit unitLeaf5 = new Unit();
		unitLeaf5.setDn(DN.createDNFromString("ou=leaf5,ou=child4,o=root4"));
		unitLeaf5.setName("leaf5");
		unitLeaf5.setHsaIdentity("leaf5-id");
		Unit unitLeaf6 = new Unit();
		unitLeaf6.setDn(DN.createDNFromString("ou=leaf6,ou=child5,o=root4"));
		unitLeaf6.setName("leaf6");
		unitLeaf6.setHsaIdentity("leaf6-id");

		return new ArrayList<Unit>(Arrays.asList(unitRoot1, unitRoot2, unitRoot3, unitRoot4, unitChild1, unitChild2, unitChild3, unitChild4, unitChild5, unitLeaf1, unitLeaf2, unitLeaf3, unitLeaf4,
				unitLeaf5, unitLeaf6));
	}

	private UnitRepository createMockUnitRepositoryFull(boolean createOneWithFreshDate, List<Unit> units) throws Exception {
		UnitRepository mockUnitRepository = EasyMock.createMock(UnitRepository.class);
		EasyMock.expect(mockUnitRepository.getAllUnits()).andReturn(units);
		for (int i = 0; i < units.size(); i++) {
			EasyMock.expect(mockUnitRepository.getUnitByDN(DN.createDNFromString(units.get(i).getDn().toString()))).andReturn(units.get(i));
			EasyMock.expectLastCall().anyTimes();
			String dateStr = "";
			if (i == units.size() - 1 && createOneWithFreshDate) {
				dateStr = Constants.zuluTimeFormatter.format(new Date());
			} else {
				dateStr = Constants.zuluTimeFormatter.format(generateCalendar(i).getTime());
			}
			units.get(i).setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
			units.get(i).setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
		}
		EasyMock.replay(mockUnitRepository);
		return mockUnitRepository;
	}
}
