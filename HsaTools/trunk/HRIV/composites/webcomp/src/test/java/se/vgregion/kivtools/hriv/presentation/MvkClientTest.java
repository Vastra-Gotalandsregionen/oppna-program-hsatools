package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.domain.Unit;

public class MvkClientTest {
  private static LogFactoryMock logFactoryMock;
  private MvkClient mvkClient;
  private HttpFetcherMock httpFetcher;

  @BeforeClass
  public static void setup() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Before
  public void setUp() throws Exception {
    httpFetcher = new HttpFetcherMock();
    mvkClient = new MvkClient();
    mvkClient.setHttpFetcher(httpFetcher);
    mvkClient.setMvkUrl("http://localhost?mvk=1");
    mvkClient.setMvkGuid("uid123");
  }

  @Test
  public void testInstantiation() {
    MvkClient mvkClient = new MvkClient();
    assertNotNull(mvkClient);
  }

  @Test
  public void testAssignCaseTypes() {
    try {
      this.mvkClient.assignCaseTypes(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=ABC-123&guid=uid123", "<xml></xml>");
    this.mvkClient.assignCaseTypes(unit);
    this.httpFetcher.assertUrlsFetched("http://localhost?mvk=1&hsaid=ABC-123&guid=uid123");

    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=ABC-123&guid=uid123", "<?xml version=\"1.0\"?><casetypes><casetype>abc</casetype><casetype>def</casetype></casetypes>");
    this.mvkClient.assignCaseTypes(unit);
    assertEquals(2, unit.getMvkCaseTypes().size());
  }
}
