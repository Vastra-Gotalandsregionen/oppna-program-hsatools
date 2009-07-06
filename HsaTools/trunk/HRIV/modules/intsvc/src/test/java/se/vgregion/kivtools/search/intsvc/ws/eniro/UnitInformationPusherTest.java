package se.vgregion.kivtools.search.intsvc.ws.eniro;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
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
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
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
  private MockFtpClient mockFtpClient;

  @Before
  public void setUp() throws Exception {
    dateSynchFile = new File("dateSynch.txt");
    unitExistFile = new File("unitExistList");
    dateSynchFile.delete();
    unitExistFile.delete();

    setupInformationPusher();
  }

  private void setupInformationPusher() throws Exception {
    informationPusher = new InformationPusherEniro();
    UnitRepository unitRepository = createMockUnitRepositoryFull(getDefaultTestUnits());
    CodeTablesService mockCodeTableService = EasyMock.createMock(CodeTablesService.class);
    org.easymock.EasyMock.expect(mockCodeTableService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "")).andReturn("");
    org.easymock.EasyMock.expectLastCall().anyTimes();
    org.easymock.EasyMock.expect(mockCodeTableService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "1604")).andReturn("test");
    org.easymock.EasyMock.expectLastCall().anyTimes();
    org.easymock.EasyMock.expect(mockCodeTableService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "1460")).andReturn("test2");
    org.easymock.EasyMock.expectLastCall().anyTimes();
    mockFtpClient = new MockFtpClient();
    // org.easymock.EasyMock.expect(mockFtpClient.sendFile(new File("src/test/VGR.xml"))).andReturn(true);
    // org.easymock.EasyMock.expectLastCall().anyTimes();
    EasyMock.replay(mockCodeTableService);

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
    Organization organizationFromXml = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Array should contain 4 root units", organizationFromXml.getUnit().size() == 4);
    // the "new" unit.
    List<Unit> units = getDefaultTestUnits();
    Unit unitLeaf7 = new Unit();
    unitLeaf7.setDn(DN.createDNFromString("ou=leaf7,ou=child5,ou=root4,ou=org,o=VGR"));
    unitLeaf7.setName("leaf7");
    unitLeaf7.setHsaIdentity("leaf7-id");

    String dateStr = Constants.formatDateToZuluTime(new Date());
    unitLeaf7.setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    unitLeaf7.setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    fillMockData(unitLeaf7);
    units.add(unitLeaf7);

    informationPusher.setUnitRepository(createMockUnitRepositoryFull(units));
    informationPusher.doService();
    organizationFromXml = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Array should contain 1 unit but are: " + organizationFromXml.getUnit().size(), organizationFromXml.getUnit().size() == 1);
    // HsaId 8 corresponds to child5 in unitsInRepository, i.e parent of leaf7
    Assert.assertEquals("child5-id", organizationFromXml.getUnit().get(0).getParentUnitId());
  }

  @Test
  public void testErrorHandlingForSaveLastSynchedModifyDateMethod() {
    informationPusher.setLastSynchedModifyDateFile(null);
    informationPusher.doService();
  }

  @Test
  public void testErrorHandlingForLastExistingUnitsFile() {
    File mockLastExistingUnitsFile = EasyMock.createMock(File.class);
    org.easymock.EasyMock.expect(mockLastExistingUnitsFile.exists()).andReturn(false);
    org.easymock.EasyMock.expect(mockLastExistingUnitsFile.getPath()).andReturn("");
    EasyMock.replay(mockLastExistingUnitsFile);
    try {
      informationPusher.setLastExistingUnitsFile(mockLastExistingUnitsFile);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(byteArrayOutputStream);
      System.setErr(printStream);
      informationPusher.doService();
      System.err.println("tets");
      String f = byteArrayOutputStream.toString();
      f = "";

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Test
  public void testUseLastSynchedModifyDateFile() throws Exception {
    // First time we don't have last synched modify date information, should collect all (15) units (regarded as new) but only 4 root units.
    informationPusher.doService();
    Organization organizationFromXml = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Array should contain 4 root units", organizationFromXml.getUnit().size() == 4);
    // When "resetting", last synched modify date information should be read from file and thus we will not find any new units.
    setupInformationPusher();
    informationPusher.doService();
    Assert.assertNull("No file should have been generated since no organizations were updated.", this.mockFtpClient.getFileContent());
  }

  @Test
  public void testTestRemovedUnitsInOrganisation() throws Exception {
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
    for (se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit unit : organization.getUnit()) {
      Assert.assertEquals("create", unit.getOperation());
    }
    // Remove one unit
    List<Unit> units = getDefaultTestUnits();
    units.remove(0);
    informationPusher.setUnitRepository(createMockUnitRepositoryFull(units));
    informationPusher.doService();
    organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 1 units, not " + organization.getUnit().size() + ".", organization.getUnit().size() == 1);
    Assert.assertEquals("remove", organization.getUnit().get(0).getOperation());
  }

  @Test
  public void testDoPushInformation() throws Exception {
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
  }

  @Test
  /*
   * * If ftp fails to send file then doService method shall continue to generate full tree structure xml file
   */
  public void testFtpFailure() throws Exception {
    this.mockFtpClient.setReturnValue(false);
    // FtpClient mockFtpClient = EasyMock.createMock(FtpClient.class);
    // org.easymock.EasyMock.expect(mockFtpClient.sendFile(new File("src/test/VGR.xml"))).andReturn(false);
    // org.easymock.EasyMock.expectLastCall().anyTimes();
    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);

    informationPusher.doService();
    organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
  }

  private Organization getOrganizationFromFile(String fileContent) {
    Organization organization = null;
    try {
      // File generatedXml = new File("src/test/VGR.xml");
      JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.search.intsvc.ws.domain.eniro");
      Unmarshaller unmarshaller = context.createUnmarshaller();
      organization = (Organization) unmarshaller.unmarshal(new StringReader(fileContent));
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return organization;
  }

  @Test
  public void testGenerateOrganisationTree() throws Exception {

    informationPusher.setUnitRepository(createMockUnitRepositoryFull(getDefaultTestUnits()));
    informationPusher.doService();
    Organization generatedOrganization = getOrganizationFromFile(this.mockFtpClient.getFileContent());

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

  /*
   * Not used remove 06082009 db private static se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit getEniroUnit(Unit domainUnit) { se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit
   * eniroUnit = new se.vgregion.kivtools.search.intsvc.ws.domain.eniro.Unit(); eniroUnit.setName(domainUnit.getName()); eniroUnit.setId(domainUnit.getHsaIdentity()); return eniroUnit; }
   */

  private static void setCreateAndModifyTimestampForUnit(int i, Unit unit) {
    Calendar calendar = Calendar.getInstance();
    // Newest is assumed to be the day before yesterday
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - (i + 1) * 2), 00, 00, 00);
    String dateStr = Constants.formatDateToZuluTime(calendar.getTime());
    unit.setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    unit.setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
  }

  private static List<Unit> getDefaultTestUnits() {

    // Test data for unit repository
    Unit unitRoot1 = new Unit();
    unitRoot1.setDn(DN.createDNFromString("ou=root1,ou=org,o=VGR"));
    unitRoot1.setName("root1");
    unitRoot1.setHsaIdentity("root1-id");
    setCreateAndModifyTimestampForUnit(1, unitRoot1);
    Unit unitRoot2 = new Unit();
    unitRoot2.setDn(DN.createDNFromString("ou=root2,ou=org,o=VGR"));
    unitRoot2.setName("root2");
    unitRoot2.setHsaIdentity("root2-id");
    setCreateAndModifyTimestampForUnit(2, unitRoot2);
    Unit unitRoot3 = new Unit();
    unitRoot3.setDn(DN.createDNFromString("ou=root3,ou=org,o=VGR"));
    unitRoot3.setName("root3");
    unitRoot3.setHsaIdentity("root3-id");
    setCreateAndModifyTimestampForUnit(3, unitRoot3);
    Unit unitRoot4 = new Unit();
    unitRoot4.setDn(DN.createDNFromString("ou=root4,ou=org,o=VGR"));
    unitRoot4.setName("root4");
    unitRoot4.setHsaIdentity("root4-id");
    setCreateAndModifyTimestampForUnit(4, unitRoot4);

    Unit unitChild1 = new Unit();
    unitChild1.setDn(DN.createDNFromString("ou=child1,ou=root1,ou=org,o=VGR"));
    unitChild1.setName("child1");
    unitChild1.setHsaIdentity("child1-id");
    setCreateAndModifyTimestampForUnit(5, unitChild1);
    Unit unitChild2 = new Unit();
    unitChild2.setDn(DN.createDNFromString("ou=child2,ou=root2,ou=org,o=VGR"));
    unitChild2.setName("child2");
    unitChild2.setHsaIdentity("child2-id");
    setCreateAndModifyTimestampForUnit(6, unitChild2);
    Unit unitChild3 = new Unit();
    unitChild3.setDn(DN.createDNFromString("ou=child3,ou=root3,ou=org,o=VGR"));
    unitChild3.setName("child3");
    unitChild3.setHsaIdentity("child3-id");
    setCreateAndModifyTimestampForUnit(7, unitChild3);
    Unit unitChild4 = new Unit();
    unitChild4.setDn(DN.createDNFromString("ou=child4,ou=root4,ou=org,o=VGR"));
    unitChild4.setName("child4");
    unitChild4.setHsaIdentity("child4-id");
    setCreateAndModifyTimestampForUnit(8, unitChild4);
    Unit unitChild5 = new Unit();
    unitChild5.setDn(DN.createDNFromString("ou=child5,ou=root4,ou=org,o=VGR"));
    unitChild5.setName("child5");
    unitChild5.setHsaIdentity("child5-id");
    setCreateAndModifyTimestampForUnit(9, unitChild5);

    Unit unitLeaf1 = new Unit();
    unitLeaf1.setDn(DN.createDNFromString("ou=leaf1,ou=child1,ou=root1,ou=org,o=VGR"));
    unitLeaf1.setName("leaf1");
    unitLeaf1.setHsaIdentity("leaf1-id");
    setCreateAndModifyTimestampForUnit(10, unitLeaf1);
    Unit unitLeaf2 = new Unit();
    unitLeaf2.setDn(DN.createDNFromString("ou=leaf2,ou=child2,ou=root2,ou=org,o=VGR"));
    unitLeaf2.setName("leaf2");
    unitLeaf2.setHsaIdentity("leaf2-id");
    setCreateAndModifyTimestampForUnit(11, unitLeaf2);
    Unit unitLeaf3 = new Unit();
    unitLeaf3.setDn(DN.createDNFromString("ou=leaf3,ou=child3,ou=root3,ou=org,o=VGR"));
    unitLeaf3.setName("leaf3");
    unitLeaf3.setHsaIdentity("leaf3-id");
    setCreateAndModifyTimestampForUnit(12, unitLeaf3);
    Unit unitLeaf4 = new Unit();
    unitLeaf4.setDn(DN.createDNFromString("ou=leaf4,ou=child3,ou=root3,ou=org,o=VGR"));
    unitLeaf4.setName("leaf4");
    unitLeaf4.setHsaIdentity("leaf4-id");
    setCreateAndModifyTimestampForUnit(13, unitLeaf4);
    Unit unitLeaf5 = new Unit();
    unitLeaf5.setDn(DN.createDNFromString("ou=leaf5,ou=child4,ou=root4,ou=org,o=VGR"));
    unitLeaf5.setName("leaf5");
    unitLeaf5.setHsaIdentity("leaf5-id");
    setCreateAndModifyTimestampForUnit(14, unitLeaf5);
    Unit unitLeaf6 = new Unit();
    unitLeaf6.setDn(DN.createDNFromString("ou=leaf6,ou=child5,ou=root4,ou=org,o=VGR"));
    unitLeaf6.setName("leaf6");
    unitLeaf6.setHsaIdentity("leaf6-id");
    setCreateAndModifyTimestampForUnit(0, unitLeaf6);
    // This is to test that informationpusher can handle a unit with only modify date set.
    unitLeaf6.setCreateTimestamp(null);

    return new ArrayList<Unit>(Arrays.asList(unitRoot1, unitRoot2, unitRoot3, unitRoot4, unitChild1, unitChild2, unitChild3, unitChild4, unitChild5, unitLeaf1, unitLeaf2, unitLeaf3, unitLeaf4,
        unitLeaf5, unitLeaf6));
  }

  private UnitRepository createMockUnitRepositoryFull(List<Unit> units) throws Exception {
    UnitRepository mockUnitRepository = EasyMock.createMock(UnitRepository.class);
    org.easymock.EasyMock.expect(mockUnitRepository.getAllUnits()).andReturn(units);
    org.easymock.EasyMock.expectLastCall().anyTimes();
    for (int i = 0; i < units.size(); i++) {
      org.easymock.EasyMock.expect(mockUnitRepository.getUnitByDN(DN.createDNFromString(units.get(i).getDn().toString()))).andReturn(units.get(i));
      org.easymock.EasyMock.expectLastCall().anyTimes();
      fillMockData(units.get(i));
    }
    EasyMock.replay(mockUnitRepository);
    return mockUnitRepository;
  }

  private void fillMockData(Unit unit) {
    unit.setHsaStreetAddress(new Address("Test avenue 1", new ZipCode("41323"), "Gothenburg", new ArrayList<String>()));
    unit.setHsaPostalAddress(new Address("Test avenue 1", new ZipCode("41323"), "Gothenburg", new ArrayList<String>()));
    unit.setHsaPublicTelephoneNumber(Arrays.asList(new PhoneNumber("11111"), new PhoneNumber("22222")));
    try {
      unit.setHsaDropInHours(Arrays.asList(new WeekdayTime(1, 3, 8, 0, 17, 0), new WeekdayTime(4, 5, 10, 0, 17, 0)));
      unit.setHsaTelephoneTime(Arrays.asList(new WeekdayTime(1, 3, 8, 0, 17, 0), new WeekdayTime(4, 5, 10, 0, 17, 0)));
      unit.setHsaSurgeryHours(Arrays.asList(new WeekdayTime(1, 3, 8, 0, 17, 0), new WeekdayTime(4, 5, 10, 0, 17, 0)));
    } catch (InvalidFormatException e) {
      throw new RuntimeException();
    }
    unit.setHsaBusinessClassificationCode(Arrays.asList("1604", "1460"));
  }

  class MockFtpClient implements FtpClient {
    private String fileContent;
    private boolean returnValue = true;

    @Override
    public boolean sendFile(String fileContent) {
      this.fileContent = fileContent;
      return this.returnValue;
    }

    public void setReturnValue(boolean returnValue) {
      this.returnValue = returnValue;
    }

    public String getFileContent() {
      return this.fileContent;
    }
  }
}
