import net.sourceforge.jwebunit.junit.WebTestCase;

import org.junit.Ignore;
import org.mortbay.jetty.Server;

public class JettyTest extends WebTestCase {

	Server server = new Server();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		// // use
		// // HtmlUnit
		//
		// WebAppContext webAppContext = new WebAppContext("src/main/webapp",
		// "/test");
		// server.addHandler(webAppContext);
		// SelectChannelConnector selectChannelConnector = new
		// SelectChannelConnector();
		// selectChannelConnector.setPort(8980);
		// server.setConnectors(new SelectChannelConnector[] {
		// selectChannelConnector });
		// server.start();
		// setBaseUrl("http://localhost:8980/test");
	}

	@Ignore
	public void testPersonSearch() {
		try {
			beginAt("/");
			setTextField("searchPersonForm:givenName", "hans");
			submit();
			assertLinkPresentWithExactText("Ackerot, Hans");
			clickLinkWithExactText("Ackerot, Hans");
			assertTextPresent("Överläkare");
			// server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Ignore
	public void testRegistrationLinkPresent() {
		// beginAt("/");
		// setTextField("unitName", "Vårdcentral Angered");
		// submit();
		// assertTextPresent("Vårdcentral Angered, Göteborg");
		// clickLinkWithExactText("Vårdcentral Angered");
		// assertLinkPresentWithExactText("Välj/Byt vårdcentral");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// server.stop();
	}

}
