import java.util.Arrays;

import net.sourceforge.jwebunit.junit.WebTestCase;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyTest extends WebTestCase {

	//Server server = new Server();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT); // use HtmlUnit

		// WebAppContext webAppContext = new WebAppContext("src/main/webapp", "/test");
		// server.addHandler(webAppContext);
		// SelectChannelConnector selectChannelConnector = new SelectChannelConnector();
		// selectChannelConnector.setPort(8180);
		// server.setConnectors(new SelectChannelConnector[]{selectChannelConnector});
		// server.start();
		setBaseUrl("http://kivsearch.vgregion.se/kivsearch");
	}
	@Ignore
	public void test1() {
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

}
