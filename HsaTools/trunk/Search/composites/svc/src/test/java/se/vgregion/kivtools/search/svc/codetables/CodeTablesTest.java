package se.vgregion.kivtools.search.svc.codetables;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;

import com.novell.ldap.LDAPException;

public class CodeTablesTest {

	private static CodeTablesService codeTablesService;

	@BeforeClass
	public static void setup() {
		codeTablesService = new CodeTablesServiceImpl();
		((CodeTablesServiceImpl) codeTablesService).setLdapConnection(new LDAPConnectionMock());
	}

	@Test
	public void testInitMethod() {
		try {
			codeTablesService.init();
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testGetValueFromCodeMethod() {
		Assert.assertEquals("Landsting/Region", codeTablesService.getValueFromCode(CodeTableName.HSA_MANAGEMENT_CODE, "1"));
	}

	@Test
	public void testAgainstRealLDAPConnection() throws UnsupportedEncodingException, NoConnectionToServerException, SikInternalException, LDAPException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("services-config.xml");
		LdapConnectionPool SearchService = (LdapConnectionPool) applicationContext.getBean("Search.LdapConnectionPool");
		((CodeTablesServiceImpl) codeTablesService).setLdapConnection(SearchService.getConnection());
		codeTablesService.init();

		Assert.assertEquals("Landsting/Region", codeTablesService.getValueFromCode(CodeTableName.HSA_MANAGEMENT_CODE, "1"));

	}
}
