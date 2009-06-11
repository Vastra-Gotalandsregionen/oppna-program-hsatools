package se.vgregion.kivtools.search.intsvc.ws.eniro;

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
import org.junit.Test;

import se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.domain.values.ZipCode;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;

import com.domainlanguage.time.TimePoint;

public class UnitInformationPusherTest {

	private InformationPusherEniro informationPusher;
	private static File dateSynchFile;
	private static File unitExistFile;
	
	@Before
	public void setUp() throws Exception {
		informationPusher = new InformationPusherEniro();
		UnitRepository unitRepository = createMockUnitRepositoryFull(getDefaultTestUnits());
		CodeTablesService mockCodeTableService = EasyMock.createMock(CodeTablesService.class);
		EasyMock.expect(mockCodeTableService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "")).andReturn("");
		EasyMock.expectLastCall().anyTimes();
		FtpClient mockFtpClient = EasyMock.createMock(FtpClient.class);
		EasyMock.expect(mockFtpClient.sendFile(new File("src/test/VGR.xml"))).andReturn(true);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.replay(mockCodeTableService, mockFtpClient);
		informationPusher.setFtpClient(mockFtpClient);
		informationPusher.setCodeTablesService(mockCodeTableService);
		informationPusher.setUnitRepository(unitRepository);
		informationPusher.setLastSynchedModifyDateFile(dateSynchFile);
		informationPusher.setLastExistingUnitsFile(unitExistFile);
		informationPusher.setDestinationFolder(new File("src/test"));
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
		// the "new" unit.
		List<Unit> units = getDefaultTestUnits();
		Unit unitLeaf7 = new Unit();
		unitLeaf7.setDn(DN.createDNFromString("ou=leaf7,ou=child5,ou=root4,ou=org,o=VGR"));
		unitLeaf7.setName("leaf7");
		unitLeaf7.setHsaIdentity("leaf7-id");

		String dateStr = Constants.formateDateToZuluTime(new Date());
		unitLeaf7.setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
		unitLeaf7.setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
		fillMockData(unitLeaf7);
		units.add(unitLeaf7);

		informationPusher.setUnitRepository(createMockUnitRepositoryFull(units));
		informationPusher.doService();
		organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 1 unit but are: " + organizationFromXml.getUnit().size(), organizationFromXml.getUnit().size() == 1);
		// HsaId 8 corresponds to child5 in unitsInRepository, i.e parent of leaf7
		Assert.assertEquals("child5-id", organizationFromXml.getUnit().get(0).getParentUnitId());
	}

	@Test
	public void testUseLastSynchedModifyDateFile() throws Exception {
		// First time we don't have last synched modify date information, should collect all (15) units (regarded as new) but only 4 root units.
		informationPusher.doService();
		Organization organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 4 root units", organizationFromXml.getUnit().size() == 4);
		// When "resetting", last synched modify date information should be read from file and thus we will not find any new units.
		setUp();
		informationPusher.doService();
		organizationFromXml = getOrganizationFromFile();
		Assert.assertTrue("Array should contain 0 units, not " + organizationFromXml.getUnit().size() + ".", organizationFromXml.getUnit().size() == 0);
	}

	@Test
	public void testTestRemovedUnitsInOrganisation() throws Exception {
		informationPusher.doService();
		Organization organization = getOrganizationFromFile();
		Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
		for (se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit unit : organization.getUnit()) {
			Assert.assertEquals("create", unit.getOperation());
		}
		// Remove one unit
		List<Unit> units = getDefaultTestUnits();
		units.remove(0);
		informationPusher.setUnitRepository(createMockUnitRepositoryFull(units));
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
			JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.search.intsvc.ws.domain.eniro");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			organization = (Organization) unmarshaller.unmarshal(generatedXml);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return organization;
	}

	@Test
	public void testGenerateOrganisationTree() throws Exception {

		informationPusher.setUnitRepository(createMockUnitRepositoryFull(getDefaultTestUnits()));
		informationPusher.doService();
		Organization generatedOrganization = getOrganizationFromFile();

		int nbrOfRootUnits = 0;
		int nbrOfChildUnits = 0;
		int nbrOfLeafUnits = 0;
		for (se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit unit : generatedOrganization.getUnit()) {
			nbrOfRootUnits++;
			Assert.assertEquals("root" + nbrOfRootUnits + "-id", unit.getId());
			for (se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit childUnit : unit.getUnit()) {
				nbrOfChildUnits++;
				Assert.assertEquals("child" + nbrOfChildUnits + "-id", childUnit.getId());
				for (int i = 0; i < childUnit.getUnit().size(); i++) {
					nbrOfLeafUnits++;
					se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit leafUnit = childUnit.getUnit().get(i);
					Assert.assertEquals("leaf" + nbrOfLeafUnits + "-id", leafUnit.getId());
				}

			}
		}
	}
	
	/* Not used remove 06082009 db
	private static se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit getEniroUnit(Unit domainUnit) {
		se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit eniroUnit = new se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit();
		eniroUnit.setName(domainUnit.getName());
		eniroUnit.setId(domainUnit.getHsaIdentity());
		return eniroUnit;
	}
	*/
	/**
	 * Generate test units
	 */
	@BeforeClass
	public static void getTestOrganisation() {
		dateSynchFile = new File("dateSynch.txt");
		unitExistFile = new File("unitExistList");
		dateSynchFile.delete();
		unitExistFile.delete();
	}

	private static Calendar generateCalendar(int i) {
		Calendar calendar = Calendar.getInstance();
		// Newest is assumed to be the day before yesterday
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - (i + 1) * 2), 00, 00, 00);
		return calendar;
	}

	private static List<Unit> getDefaultTestUnits() {

		// Test data for unit repository
		Unit unitRoot1 = new Unit();
		unitRoot1.setDn(DN.createDNFromString("ou=root1,ou=org,o=VGR"));
		unitRoot1.setName("root1");
		unitRoot1.setHsaIdentity("root1-id");
		Unit unitRoot2 = new Unit();
		unitRoot2.setDn(DN.createDNFromString("ou=root2,ou=org,o=VGR"));
		unitRoot2.setName("root2");
		unitRoot2.setHsaIdentity("root2-id");
		Unit unitRoot3 = new Unit();
		unitRoot3.setDn(DN.createDNFromString("ou=root3,ou=org,o=VGR"));
		unitRoot3.setName("root3");
		unitRoot3.setHsaIdentity("root3-id");
		Unit unitRoot4 = new Unit();
		unitRoot4.setDn(DN.createDNFromString("ou=root4,ou=org,o=VGR"));
		unitRoot4.setName("root4");
		unitRoot4.setHsaIdentity("root4-id");

		Unit unitChild1 = new Unit();
		unitChild1.setDn(DN.createDNFromString("ou=child1,ou=root1,ou=org,o=VGR"));
		unitChild1.setName("child1");
		unitChild1.setHsaIdentity("child1-id");
		Unit unitChild2 = new Unit();
		unitChild2.setDn(DN.createDNFromString("ou=child2,ou=root2,ou=org,o=VGR"));
		unitChild2.setName("child2");
		unitChild2.setHsaIdentity("child2-id");
		Unit unitChild3 = new Unit();
		unitChild3.setDn(DN.createDNFromString("ou=child3,ou=root3,ou=org,o=VGR"));
		unitChild3.setName("child3");
		unitChild3.setHsaIdentity("child3-id");
		Unit unitChild4 = new Unit();
		unitChild4.setDn(DN.createDNFromString("ou=child4,ou=root4,ou=org,o=VGR"));
		unitChild4.setName("child4");
		unitChild4.setHsaIdentity("child4-id");
		Unit unitChild5 = new Unit();
		unitChild5.setDn(DN.createDNFromString("ou=child5,ou=root4,ou=org,o=VGR"));
		unitChild5.setName("child5");
		unitChild5.setHsaIdentity("child5-id");

		Unit unitLeaf1 = new Unit();
		unitLeaf1.setDn(DN.createDNFromString("ou=leaf1,ou=child1,ou=root1,ou=org,o=VGR"));
		unitLeaf1.setName("leaf1");
		unitLeaf1.setHsaIdentity("leaf1-id");
		Unit unitLeaf2 = new Unit();
		unitLeaf2.setDn(DN.createDNFromString("ou=leaf2,ou=child2,ou=root2,ou=org,o=VGR"));
		unitLeaf2.setName("leaf2");
		unitLeaf2.setHsaIdentity("leaf2-id");
		Unit unitLeaf3 = new Unit();
		unitLeaf3.setDn(DN.createDNFromString("ou=leaf3,ou=child3,ou=root3,ou=org,o=VGR"));
		unitLeaf3.setName("leaf3");
		unitLeaf3.setHsaIdentity("leaf3-id");
		Unit unitLeaf4 = new Unit();
		unitLeaf4.setDn(DN.createDNFromString("ou=leaf4,ou=child3,ou=root3,ou=org,o=VGR"));
		unitLeaf4.setName("leaf4");
		unitLeaf4.setHsaIdentity("leaf4-id");
		Unit unitLeaf5 = new Unit();
		unitLeaf5.setDn(DN.createDNFromString("ou=leaf5,ou=child4,ou=root4,ou=org,o=VGR"));
		unitLeaf5.setName("leaf5");
		unitLeaf5.setHsaIdentity("leaf5-id");
		Unit unitLeaf6 = new Unit();
		unitLeaf6.setDn(DN.createDNFromString("ou=leaf6,ou=child5,ou=root4,ou=org,o=VGR"));
		unitLeaf6.setName("leaf6");
		unitLeaf6.setHsaIdentity("leaf6-id");

		return new ArrayList<Unit>(Arrays.asList(unitRoot1, unitRoot2, unitRoot3, unitRoot4, unitChild1, unitChild2, unitChild3, unitChild4, unitChild5, unitLeaf1, unitLeaf2, unitLeaf3, unitLeaf4,
				unitLeaf5, unitLeaf6));
	}

	private UnitRepository createMockUnitRepositoryFull(List<Unit> units) throws Exception {
		UnitRepository mockUnitRepository = EasyMock.createMock(UnitRepository.class);
		EasyMock.expect(mockUnitRepository.getAllUnits()).andReturn(units);
		for (int i = 0; i < units.size(); i++) {
			EasyMock.expect(mockUnitRepository.getUnitByDN(DN.createDNFromString(units.get(i).getDn().toString()))).andReturn(units.get(i));
			EasyMock.expectLastCall().anyTimes();
			// If createTimestamp and modifyTimestamp is null set values
			if (units.get(i).getCreateTimestamp() == null || units.get(i).getModifyTimestamp() == null) {
				String dateStr = Constants.formateDateToZuluTime(generateCalendar(i).getTime());
				units.get(i).setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
				units.get(i).setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
			}
			fillMockData(units.get(i));
		}
		EasyMock.replay(mockUnitRepository);
		return mockUnitRepository;
	}

	private void fillMockData(Unit unit) {
		unit.setHsaStreetAddress(new Address("Test avenue 1", new ZipCode("41323"), "Gothenburg", new ArrayList<String>()));
		unit.setHsaPostalAddress(new Address("Test avenue 1", new ZipCode("41323"), "Gothenburg", new ArrayList<String>()));
		unit.setHsaPublicTelephoneNumber(new ArrayList<PhoneNumber>());
		unit.setHsaDropInHours(new ArrayList<WeekdayTime>());
		unit.setHsaTelephoneTime(new ArrayList<WeekdayTime>());
		unit.setHsaSurgeryHours(new ArrayList<WeekdayTime>());
		unit.setHsaBusinessClassificationCode(new ArrayList<String>());
	}
}
