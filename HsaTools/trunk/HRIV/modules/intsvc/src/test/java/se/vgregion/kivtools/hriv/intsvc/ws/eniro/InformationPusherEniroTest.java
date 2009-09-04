package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
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

public class InformationPusherEniroTest {
  private InformationPusherEniro informationPusher = new InformationPusherEniro();;
  private static File unitExistFile = new File("unitExistList");
  private MockFtpClient mockFtpClient = new MockFtpClient();
  private UnitRepositoryMock unitRepositoryMock = new UnitRepositoryMock();
  private CodeTableMock codeTableMock = new CodeTableMock();

  @Before
  public void setUp() throws Exception {
    resetUnitRepositoryMock();
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.remove("LastSynchedModifyDate");
    unitExistFile.delete();
    codeTableMock.init();

    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.setCodeTablesService(codeTableMock);
    informationPusher.setUnitRepository(unitRepositoryMock);
    informationPusher.setLastExistingUnitsFile(unitExistFile);
    // Emulate spring bean initialization
    informationPusher.initLoadLastExistingUnitsInRepository();
  }

  private void resetUnitRepositoryMock() {
    unitRepositoryMock.clearMockUnitsMap();

    List<Unit> units = getDefaultTestUnits();
    for (Unit unit : units) {
      fillMockData(unit);
      unitRepositoryMock.addMockUnit(unit);
    }
  }

  @Test
  public void testIncrementalSynchronization() throws Exception {
    informationPusher.doService();
    Organization organizationFromXml = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Array should contain 4 root units", organizationFromXml.getUnit().size() == 4);

    // the "new" unit.
    Unit unit16 = new Unit();
    unit16.setDn(DN.createDNFromString("ou=unit16,ou=unit5,ou=unit1,ou=org,o=VGR"));
    unit16.setName("unit16");
    unit16.setHsaIdentity("unit16-id");
    String dateStr = Constants.formatDateToZuluTime(new Date());
    unit16.setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    unit16.setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    fillMockData(unit16);

    // add new unit to repository.
    unitRepositoryMock.addMockUnit(unit16);
    informationPusher.doService();

    organizationFromXml = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Array should contain 1 unit but are: " + organizationFromXml.getUnit().size(), organizationFromXml.getUnit().size() == 1);
    // HsaId 8 corresponds to child5 in unitsInRepository, i.e parent of leaf7
    Assert.assertEquals("unit5-id", organizationFromXml.getUnit().get(0).getParentUnitId());
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
    // setupInformationPusher();
    mockFtpClient = new MockFtpClient();
    informationPusher = new InformationPusherEniro();
    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.setCodeTablesService(codeTableMock);
    informationPusher.setUnitRepository(unitRepositoryMock);
    informationPusher.setLastExistingUnitsFile(unitExistFile);
    informationPusher.doService();
    Assert.assertNull("No file should have been generated since no organizations were updated.", this.mockFtpClient.getFileContent());
  }

  @Test
  public void testUseDefaultLastSynchedModifyDateFile() {
    // Delete folder before tests.
    deleteLeufFile();
    informationPusher.setLastExistingUnitsFile(null);
    informationPusher.doService();
  }

  private void deleteLeufFile() {
    unitExistFile.delete();
    File leuf = new File(System.getProperty("user.home") + File.separator + ".hriv", "leuf.serialized");
    leuf.delete();
    leuf = new File(System.getProperty("user.home"), ".hriv");
    if (leuf.exists()) {
      leuf.delete();
    }

  }

  @Test
  public void testTestRemovedUnitsInOrganisation() throws Exception {
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
    for (se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit unit : organization.getUnit()) {
      Assert.assertEquals("create", unit.getOperation());
    }
    unitRepositoryMock.removeMockUnit(DN.createDNFromString("ou=unit14,ou=unit9,ou=unit2,ou=org,o=VGR"));
    informationPusher.doService();
    organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 1 units, not " + organization.getUnit().size() + ".", organization.getUnit().size() == 1);
    Assert.assertEquals("remove", organization.getUnit().get(0).getOperation());
  }

  @Test
  public void testMoveUnitInRepository() throws Exception {
    informationPusher.doService();
    // Move leaf unit.
    resetUnitRepositoryMock();
    DN unitDnToMoveFrom = DN.createDNFromString("ou=unit10,ou=unit5,ou=unit1,ou=org,o=VGR");
    DN unitDnToMoveTo = DN.createDNFromString("ou=unit10,ou=unit6,ou=unit1,ou=org,o=VGR");
    Unit unit = unitRepositoryMock.getUnitByDN(unitDnToMoveFrom);
    unitRepositoryMock.moveMockUnit(unitDnToMoveFrom, unitDnToMoveTo);
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 1 units", organization.getUnit().size() == 1);
    assertEquals(unit.getHsaIdentity(), organization.getUnit().get(0).getId());
    assertEquals("move", organization.getUnit().get(0).getOperation());
  }

  @Test
  public void testUpdatedUnit() throws Exception {
    informationPusher.doService();
    // resetUnitRepositoryMock();
    Unit unit = unitRepositoryMock.getUnitByDN(DN.createDNFromString("ou=unit13,ou=unit8,ou=unit4,ou=org,o=VGR"));
    Address hsaPostalAddress = unit.getHsaPostalAddress();
    hsaPostalAddress.setStreet("New updated street");
    setNewModifyDate(unit);
    unit.setNew(false);
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address address = (se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address) organization.getUnit().get(0).getDescriptionOrImageOrAddress().get(2);
    assertEquals("New updated street", address.getStreetName());
    assertEquals("update", organization.getUnit().get(0).getOperation());
  }

  @Test
  public void testUnitRepositoryThrowsException() throws Exception {
    UnitRepository mockUnitRepository = EasyMock.createMock(UnitRepository.class);
    org.easymock.EasyMock.expect(mockUnitRepository.getAllUnits()).andThrow(new Exception());
    informationPusher.doService();
  }

  // This test should be changed or removed
  @Test
  public void testDoPushInformation() throws Exception {
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
  }

  /*
   * * If ftp fails to send file then doService method shall continue to generate full tree structure xml file
   */
  @Test
  public void testFtpFailure() throws Exception {
    this.mockFtpClient.setReturnValue(false);
    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);

    informationPusher.doService();
    organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 4 root units", organization.getUnit().size() == 4);
  }

  @Test
  public void testGenerateOrganisationTree() throws Exception {
    informationPusher.doService();
    Organization generatedOrganization = getOrganizationFromFile(this.mockFtpClient.getFileContent());

    // Root units.
    assertEquals("unit1-id", generatedOrganization.getUnit().get(0).getId());
    assertEquals("unit2-id", generatedOrganization.getUnit().get(1).getId());
    assertEquals("unit3-id", generatedOrganization.getUnit().get(2).getId());
    assertEquals("unit4-id", generatedOrganization.getUnit().get(3).getId());

    // Child units.
    assertEquals("unit5-id", generatedOrganization.getUnit().get(0).getUnit().get(0).getId());
    assertEquals("unit6-id", generatedOrganization.getUnit().get(1).getUnit().get(0).getId());
    assertEquals("unit7-id", generatedOrganization.getUnit().get(2).getUnit().get(0).getId());
    assertEquals("unit8-id", generatedOrganization.getUnit().get(3).getUnit().get(0).getId());
    assertEquals("unit9-id", generatedOrganization.getUnit().get(1).getUnit().get(1).getId());

    // Leaf units.
    assertEquals("unit10-id", generatedOrganization.getUnit().get(0).getUnit().get(0).getUnit().get(0).getId());
    assertEquals("unit11-id", generatedOrganization.getUnit().get(1).getUnit().get(0).getUnit().get(0).getId());
    assertEquals("unit12-id", generatedOrganization.getUnit().get(2).getUnit().get(0).getUnit().get(0).getId());
    assertEquals("unit13-id", generatedOrganization.getUnit().get(3).getUnit().get(0).getUnit().get(0).getId());
    assertEquals("unit14-id", generatedOrganization.getUnit().get(1).getUnit().get(1).getUnit().get(0).getId());
    assertEquals("unit15-id", generatedOrganization.getUnit().get(0).getUnit().get(0).getUnit().get(1).getId());

    mockFtpClient.reset();
    // No changes made, so no units to push.
    informationPusher.doService();
    generatedOrganization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    // No file should be generated.
    assertNull(generatedOrganization);

    deleteLeufFile();
    mockFtpClient.reset();
    informationPusher.doService();

    generatedOrganization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    // Leuf file deleted so last synch is reseted and service should generate full organization tree.
    assertEquals(4, generatedOrganization.getUnit().size());

  }

  // Helper-methods

  private Organization getOrganizationFromFile(String fileContent) {
    Organization organization = null;
    try {
      JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro");
      Unmarshaller unmarshaller = context.createUnmarshaller();
      organization = (Organization) unmarshaller.unmarshal(new StringReader(fileContent));
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return organization;
  }

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
    // Root units.
    Unit unitRoot1 = createUnit("unit1", "unit1-id", "ou=unit1,ou=org,o=VGR", 1);
    Unit unitRoot2 = createUnit("unit2", "unit2-id", "ou=unit2,ou=org,o=VGR", 2);
    Unit unitRoot3 = createUnit("unit3", "unit3-id", "ou=unit3,ou=org,o=VGR", 3);
    Unit unitRoot4 = createUnit("unit4", "unit4-id", "ou=unit4,ou=org,o=VGR", 4);

    // Children to root units.
    Unit unitChild1 = createUnit("unit5", "unit5-id", "ou=unit5,ou=unit1,ou=org,o=VGR", 5);
    Unit unitChild2 = createUnit("unit6", "unit6-id", "ou=unit6,ou=unit2,ou=org,o=VGR", 6);
    Unit unitChild3 = createUnit("unit7", "unit7-id", "ou=unit7,ou=unit3,ou=org,o=VGR", 7);
    Unit unitChild4 = createUnit("unit8", "unit8-id", "ou=unit8,ou=unit4,ou=org,o=VGR", 8);
    Unit unitChild5 = createUnit("unit9", "unit9-id", "ou=unit9,ou=unit2,ou=org,o=VGR", 9);

    // Leaf to children units.
    Unit unitLeaf1 = createUnit("unit10", "unit10-id", "ou=unit10,ou=unit5,ou=unit1,ou=org,o=VGR", 10);
    Unit unitLeaf2 = createUnit("unit11", "unit11-id", "ou=unit11,ou=unit6,ou=unit2,ou=org,o=VGR", 11);
    Unit unitLeaf3 = createUnit("unit12", "unit12-id", "ou=unit12,ou=unit7,ou=unit3,ou=org,o=VGR", 12);
    Unit unitLeaf4 = createUnit("unit13", "unit13-id", "ou=unit13,ou=unit8,ou=unit4,ou=org,o=VGR", 13);
    Unit unitLeaf5 = createUnit("unit14", "unit14-id", "ou=unit14,ou=unit9,ou=unit2,ou=org,o=VGR", 14);
    Unit unitLeaf6 = createUnit("unit15", "unit15-id", "ou=unit15,ou=unit5,ou=unit1,ou=org,o=VGR", 0);

    // This is to test that informationpusher can handle a unit with only modify date set.
    // unitLeaf6.setCreateTimestamp(null);

    return new ArrayList<Unit>(Arrays.asList(unitRoot1, unitRoot2, unitRoot3, unitRoot4, unitChild1, unitChild2, unitChild3, unitChild4, unitChild5, unitLeaf1, unitLeaf2, unitLeaf3, unitLeaf4,
        unitLeaf5, unitLeaf6));
  }

  private static Unit createUnit(String name, String identity, String dn, int i) {
    Unit unit = new Unit();
    unit.setDn(DN.createDNFromString(dn));
    unit.setName(name);
    unit.setHsaIdentity(identity);
    setCreateAndModifyTimestampForUnit(i, unit);

    return unit;
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

  // Mocks

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

    public void reset() {
      fileContent = "";
    }
  }

  class UnitRepositoryMock extends UnitRepository {

    private Map<DN, Unit> mockUnits = new TreeMap<DN, Unit>(new Comparator<DN>() {
      @Override
      public int compare(DN o1, DN o2) {
        return o1.compareTo(o2);
      }
    });

    public void clearMockUnitsMap() {
      mockUnits.clear();
    }

    public void addMockUnit(Unit mockUnit) {
      mockUnits.put(mockUnit.getDn(), mockUnit);
    }

    public void removeMockUnit(DN unitDN) {
      mockUnits.remove(unitDN);
    }

    public void moveMockUnit(DN fromDN, DN toDN) {
      Unit mockUnit = mockUnits.remove(fromDN);
      mockUnit.setDn(toDN);
      setNewCreateDate(mockUnit);
      addMockUnit(mockUnit);
    }

    @Override
    public List<Unit> getAllUnits() throws Exception {
      return new ArrayList<Unit>(mockUnits.values());
    }

    @Override
    public Unit getUnitByDN(DN dn) throws Exception {
      return mockUnits.get(dn);
    }
  }

  private void setNewCreateDate(Unit mockUnit) {
    Calendar calendar = Calendar.getInstance();
    String dateStr = Constants.formatDateToZuluTime(calendar.getTime());
    mockUnit.setCreateTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
  }

  private void setNewModifyDate(Unit mockUnit) {
    Calendar calendar = Calendar.getInstance();
    String dateStr = Constants.formatDateToZuluTime(calendar.getTime());
    mockUnit.setModifyTimestamp(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
  }

  class CodeTableMock implements CodeTablesService {

    Map<CodeTableName, Map<String, String>> codeTableMap = new HashMap<CodeTableName, Map<String, String>>();

    @Override
    public void init() {
      codeTableMap.clear();
      Map<String, String> codeValues = new HashMap<String, String>();
      codeValues.put("", "");
      codeValues.put("1604", "test");
      codeValues.put("1460", "test2");
      codeTableMap.put(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, codeValues);
    }

    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return codeTableMap.get(codeTableName).get(string);
    }
  }
}
