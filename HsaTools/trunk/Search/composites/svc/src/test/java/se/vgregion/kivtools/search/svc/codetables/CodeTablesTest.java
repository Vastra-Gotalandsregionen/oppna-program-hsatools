package se.vgregion.kivtools.search.svc.codetables;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

public class CodeTablesTest {

	private static CodeTablesService codeTablesService;
	private static String[] managementCodeAttributes = { "1;Landsting/Region", "2;Kommun", "3;Statlig", "4;Privat, vårdavtal", "5;Privat, enl lag om läkarvårdsersättning" };
	private static String[] specialityCodeAttributes = { "1000;Europaläkare, allmänpraktiserande läkare", "10100;Kirurgi", "10101;Urologi", "10102;Barn- och ungdomskirurgi" };
	private static String[] vgrAO3KodAttributes = { "010;Moderförvaltning;10", "020;Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks);10", "030;Revisorskollegiet",
			"040;Patientnämnd U-a, Borås, Gbg o Mariestad" };
	private static String[] vgrCareTypeAttributes = { "01;Öppenvård", "02;Slutenvård", "03;Hemsjukvård" };
	private static Map<String, String[]> attributeLists = new HashMap<String, String[]>();
	static {
		attributeLists.put(CodeTableName.HSA_MANAGEMENT_CODE.name(), managementCodeAttributes);
		attributeLists.put(CodeTableName.HSA_SPECIALITY_CODE.name(), specialityCodeAttributes);
		attributeLists.put(CodeTableName.VGR_AO3_CODE.name(), vgrAO3KodAttributes);
		attributeLists.put(CodeTableName.VGR_CARE_TYPE.name(), vgrCareTypeAttributes);
	}

	@BeforeClass
	public static void setup() {
		codeTablesService = new CodeTablesServiceImpl();
		((CodeTablesServiceImpl) codeTablesService).setLdapConnectionPool(new LdapConnectionPoolMock());
	}

	// Generate LDAPConnectionMock and fill it with search alternatives
	public static LDAPConnectionMock generateConnectionMock() {
		LDAPConnectionMock connectionMock = new LDAPConnectionMock();
		CodeTablesServiceImpl codeTablesServiceImpl = new CodeTablesServiceImpl();
		for (Entry<String, String[]> attributeListEntry : attributeLists.entrySet()) {
			LinkedList<LDAPEntryMock> ldapEntries = new LinkedList<LDAPEntryMock>();
			LDAPEntryMock entryMock = new LDAPEntryMock();
			entryMock.addAttribute("description", attributeListEntry.getValue());
			ldapEntries.add(entryMock);
			connectionMock.addLdapEntries(new LDAPConnectionMock().new SearchCondition(codeTablesServiceImpl.getCodeTablesBase(), LDAPConnection.SCOPE_SUB, "(cn=" +  CodeTableName.valueOf(attributeListEntry.getKey()) + ")"), ldapEntries);
		}
		return connectionMock;
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
		for (Entry<String, String[]> attributeListEntry : attributeLists.entrySet()) {
			String[] attributes = attributeListEntry.getValue();
			String[] fistCheckcodeValue = attributes[0].split(";");
			String[] secondCheckcodeValue = attributes[attributes.length - 1].split(";");
			CodeTableName codeTableName = CodeTableName.valueOf(attributeListEntry.getKey());
			Assert.assertEquals(fistCheckcodeValue[1], codeTablesService.getValueFromCode(codeTableName, fistCheckcodeValue[0]));
			Assert.assertEquals(secondCheckcodeValue[1], codeTablesService.getValueFromCode(codeTableName, secondCheckcodeValue[0]));
		}
	}

	// Test with a real Ldap connection
	@Test
	@Ignore
	public void testAgainstRealLDAPConnection() throws UnsupportedEncodingException, NoConnectionToServerException, SikInternalException, LDAPException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("services-config.xml");
		LdapConnectionPool ldapConnectionPool = (LdapConnectionPool) applicationContext.getBean("Search.LdapConnectionPool");
		((CodeTablesServiceImpl) codeTablesService).setLdapConnectionPool(ldapConnectionPool);
		Assert.assertEquals("Landsting/Region", codeTablesService.getValueFromCode(CodeTableName.HSA_MANAGEMENT_CODE, "1"));
	}

	public static class LdapConnectionPoolMock extends LdapConnectionPool {
		@Override
		public synchronized LDAPConnection getConnection() throws LDAPException, UnsupportedEncodingException, NoConnectionToServerException, SikInternalException {
			return generateConnectionMock();
		}
	}

}
