package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.naming.directory.SearchControls;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.EniroOrganisationBuilder;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.mocks.LogFactoryMock;

public class InformationPusherEniroTest {
  private InformationPusherEniro informationPusher = new InformationPusherEniro();;
  private FtpClientMock mockFtpClient = new FtpClientMock();
  private LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();
  private static LogFactoryMock logFactoryMock;
  private final String EXPECTED_FILECONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Organization Type=\"Municipality\" LoadType=\"Full\"><Unit><Id>Vardcentral</Id><Name>Vårdcentral</Name><Locality>Göteborg</Locality></Unit><Unit><Id>Ovrig_primarvard</Id><Name>Övrig primärvård</Name><Locality>Göteborg</Locality></Unit></Organization>";

  @BeforeClass
  public static void beforeClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @Before
  public void setUp() throws Exception {
    EniroOrganisationBuilder eniroOrganisationBuilder = new EniroOrganisationBuilder();
    eniroOrganisationBuilder.setRootUnits(Arrays.asList(""));
    eniroOrganisationBuilder.setCareCenter("Vårdcentral");
    eniroOrganisationBuilder.setOtherCare("Övrig primärvård");

    List<UnitComposition> unitslist = Arrays.asList(createUnit("unit1", "unit1-id", "ou=unit1,ou=org,o=VGR"), createUnit("unit2", "unit2-id", "ou=unit2,ou=unit1,ou=org,o=VGR"));
    ldapTemplateMock.setUnitComposite(unitslist);
    informationPusher.setLdapTemplate(ldapTemplateMock);
    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.setEniroOrganisationBuilder(eniroOrganisationBuilder);
    informationPusher.setAllowedUnitBusinessClassificationCodes(new String[] { "1", "2" });
    informationPusher.setOtherCareTypeBusinessCodes(new String[] { "3", "4" });
  }

  @AfterClass
  public static void tearDown() {
    LogFactoryMock.resetInstance();
  }

  @Test
  public void testDoService() {
    informationPusher.doService();
    String fileContent = mockFtpClient.getFileContent();
    assertEquals(EXPECTED_FILECONTENT, fileContent);
    assertEquals("Unit details pusher: Completed with success.\n", logFactoryMock.getInfo(true));
  }

  @Test
  public void testExceptionHandling() {
    mockFtpClient.returnValue = false;
    informationPusher.doService();
    assertEquals("Unit details pusher: Completed with failure.\n", logFactoryMock.getError(true));
  }

  // Helper-methods
  private Organization getOrganizationFromFile(String fileContent) {
    Organization organization = null;
    try {
      JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro");
      Unmarshaller unmarshaller = context.createUnmarshaller();
      organization = (Organization) unmarshaller.unmarshal(new StringReader(fileContent));
    } catch (JAXBException e) {
      // 
    }
    return organization;
  }

  private static UnitComposition createUnit(String name, String identity, String dn) {
    UnitComposition unit = new UnitComposition();
    unit.setDn(dn);
    unit.getEniroUnit().setName(name);
    unit.getEniroUnit().setId(identity);
    return unit;
  }

  // Mocks
  class FtpClientMock implements FtpClient {
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

    @Override
    @SuppressWarnings("unchecked")
    public List search(String base, String filter, int searchScope, ContextMapper mapper) {
      assertEquals(SearchControls.SUBTREE_SCOPE, searchScope);
      return unitComposite;
    }
  }
}
