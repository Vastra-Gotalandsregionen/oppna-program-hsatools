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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.easymock.classextension.EasyMock;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;

import com.domainlanguage.time.TimePoint;

public class InformationPusherEniroTest {
  private InformationPusherEniro informationPusher = new InformationPusherEniro();;
  private static File unitExistFile = new File("unitExistList");
  private MockFtpClient mockFtpClient = new MockFtpClient();
  private LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();

  @Before
  public void setUp() throws Exception {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.remove("LastSynchedModifyDate");
    unitExistFile.delete();
    ldapTemplateMock.setUnitComposite(getDefaultTestUnits());
    informationPusher.setLdapTemplate(ldapTemplateMock);
    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.setLastExistingUnitsFile(unitExistFile);
    informationPusher.setParentDn("ou=org,o=VGR");
    // Emulate spring bean initialization
    informationPusher.initLoadLastExistingUnitsInRepository();
  }
  
  @AfterClass
  public static void tearDown(){
    unitExistFile.delete();
    String homeFolder = System.getProperty("user.home");
    File hrivSettingsFolder = new File(homeFolder, ".hriv");
    hrivSettingsFolder.delete();
  }
  

  @Test
  public void testIncrementalSynchronization() throws Exception {
    informationPusher.doService();
    Organization organizationFromXml = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Array should contain 4 root units", organizationFromXml.getUnit().size() == 4);

    // the "new" unit.
    UnitComposition unit16 = new UnitComposition();
    unit16.setDn("ou=unit16,ou=unit5,ou=unit1,ou=org,o=VGR");
    unit16.getEniroUnit().setName("unit16");
    unit16.getEniroUnit().setId("unit16-id");
    String dateStr = Constants.formatDateToZuluTime(new Date());
    // add new unit to repository.
    List<UnitComposition> defaultTestUnits = getDefaultTestUnits();
    defaultTestUnits.add(unit16);
    ldapTemplateMock.setUnitComposite(defaultTestUnits);
    unit16.setCreateTimePoint(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    unit16.setModifyTimePoint(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));

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
    mockFtpClient = new MockFtpClient();
    informationPusher = new InformationPusherEniro();
    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.setLdapTemplate(ldapTemplateMock);
    informationPusher.setLastExistingUnitsFile(unitExistFile);
    informationPusher.initLoadLastExistingUnitsInRepository();
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
    ldapTemplateMock.removeUnitInList("unit14-id");
    informationPusher.doService();
    organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 1 units, not " + organization.getUnit().size() + ".", organization.getUnit().size() == 1);
    Assert.assertEquals("remove", organization.getUnit().get(0).getOperation());
  }

  @Test
  public void testMoveUnitInRepository() throws Exception {
    informationPusher.doService();
    // Move leaf unit.
    String unitDnToMoveTo = "ou=unit10,ou=unit6,ou=unit1,ou=org,o=VGR";
    ldapTemplateMock.moveUnitInList("unit10-id", unitDnToMoveTo);
    informationPusher.doService();
    Organization organization = getOrganizationFromFile(this.mockFtpClient.getFileContent());
    Assert.assertTrue("Organization should contain 1 units", organization.getUnit().size() == 1);
    assertEquals("unit10-id", organization.getUnit().get(0).getId());
    assertEquals("move", organization.getUnit().get(0).getOperation());
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

  private static void setCreateAndModifyTimestampForUnit(int i, UnitComposition unit) {
    Calendar calendar = Calendar.getInstance();
    // Newest is assumed to be the day before yesterday
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH) - (i + 1) * 2), 00, 00, 00);
    String dateStr = Constants.formatDateToZuluTime(calendar.getTime());
    unit.setCreateTimePoint(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    unit.setModifyTimePoint(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
  }

  private static List<UnitComposition> getDefaultTestUnits() {
    // Test data for unit repository
    // Root units.
    UnitComposition unitRoot1 = createUnit("unit1", "unit1-id", "ou=unit1,ou=org,o=VGR", 1);
    UnitComposition unitRoot2 = createUnit("unit2", "unit2-id", "ou=unit2,ou=org,o=VGR", 2);
    UnitComposition unitRoot3 = createUnit("unit3", "unit3-id", "ou=unit3,ou=org,o=VGR", 3);
    UnitComposition unitRoot4 = createUnit("unit4", "unit4-id", "ou=unit4,ou=org,o=VGR", 4);

    // Children to root units.
    UnitComposition unitChild1 = createUnit("unit5", "unit5-id", "ou=unit5,ou=unit1,ou=org,o=VGR", 5);
    UnitComposition unitChild2 = createUnit("unit6", "unit6-id", "ou=unit6,ou=unit2,ou=org,o=VGR", 6);
    UnitComposition unitChild3 = createUnit("unit7", "unit7-id", "ou=unit7,ou=unit3,ou=org,o=VGR", 7);
    UnitComposition unitChild4 = createUnit("unit8", "unit8-id", "ou=unit8,ou=unit4,ou=org,o=VGR", 8);
    UnitComposition unitChild5 = createUnit("unit9", "unit9-id", "ou=unit9,ou=unit2,ou=org,o=VGR", 9);

    // Leaf to children units.
    UnitComposition unitLeaf1 = createUnit("unit10", "unit10-id", "ou=unit10,ou=unit5,ou=unit1,ou=org,o=VGR", 10);
    UnitComposition unitLeaf2 = createUnit("unit11", "unit11-id", "ou=unit11,ou=unit6,ou=unit2,ou=org,o=VGR", 11);
    UnitComposition unitLeaf3 = createUnit("unit12", "unit12-id", "ou=unit12,ou=unit7,ou=unit3,ou=org,o=VGR", 12);
    UnitComposition unitLeaf4 = createUnit("unit13", "unit13-id", "ou=unit13,ou=unit8,ou=unit4,ou=org,o=VGR", 13);
    UnitComposition unitLeaf5 = createUnit("unit14", "unit14-id", "ou=unit14,ou=unit9,ou=unit2,ou=org,o=VGR", 14);
    UnitComposition unitLeaf6 = createUnit("unit15", "unit15-id", "ou=unit15,ou=unit5,ou=unit1,ou=org,o=VGR", 0);

    return new ArrayList<UnitComposition>(Arrays.asList(unitRoot1, unitRoot2, unitRoot3, unitRoot4, unitChild1, unitChild2, unitChild3, unitChild4, unitChild5, unitLeaf1, unitLeaf2, unitLeaf3,
        unitLeaf4, unitLeaf5, unitLeaf6));
  }

  private static UnitComposition createUnit(String name, String identity, String dn, int i) {
    UnitComposition unit = new UnitComposition();
    unit.setDn(dn);
    unit.getEniroUnit().setName(name);
    unit.getEniroUnit().setId(identity);
    setCreateAndModifyTimestampForUnit(i, unit);
    return unit;
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

  class LdapTemplateMock extends LdapTemplate {

    private List<UnitComposition> unitComposite;

    public void setUnitComposite(List<UnitComposition> unitComposite) {
      this.unitComposite = unitComposite;
    }

    public void removeUnitInList(String unitId) {
      unitComposite.remove(findUnitPositionInList(unitId));
    }

    public void moveUnitInList(String unitId, String toDn) {
      UnitComposition unit = unitComposite.get(findUnitPositionInList(unitId));
      unit.setDn(toDn);
      String dateStr = Constants.formatDateToZuluTime(new Date());
      unit.setModifyTimePoint(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
      unit.setCreateTimePoint(TimePoint.parseFrom(dateStr, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    private int findUnitPositionInList(String unitId) {
      Collections.sort(unitComposite);
      UnitComposition unitComposition = new UnitComposition();
      unitComposition.getEniroUnit().setId(unitId);
      int binarySearch = Collections.binarySearch(unitComposite, unitComposition);
      return binarySearch;
    }

    @Override
    public List search(String base, String filter, AttributesMapper mapper) {
      return unitComposite;
    }
    
    @Override
    public List search(String base, String filter, ContextMapper mapper) {
      return unitComposite;
    }

  }
}
