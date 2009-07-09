package se.vgregion.kivtools.search.presentation;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.webflow.executor.jsf.JsfExternalContext;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;

import com.novell.ldap.util.Base64;

public class RegisterOnUnitControllerTest {

	RegisterOnUnitController registerOnUnitController;
	private final String SELECTED_UNIT_ID = "SE2321000131-E000000001302";
	private final String CURRENT_UNIT_ID = "SE2321000131-E000000001303";
	private final String UPCOMING_UNIT_ID = "SE2321000131-E000000001304";
	private final String SELECTED_UNIT_NAME = "Selected unit name";
	private final String CURRENT_UNIT_NAME = "Current unit name";
	private final String UPCOMING_UNIT_NAME = "Upcoming unit name";

	private final String SSN = "197702200101";
	private final Unit selectedUnit = new Unit();
	private final Unit currentUnit = new Unit();
	private final Unit upcomingUnit = new Unit();

	@Before
	public void setup() throws Exception {
		registerOnUnitController = new RegisterOnUnitController();
		registerOnUnitController.setVardValService(generateVardvalServiceMock());
		registerOnUnitController.setSearchService(generateSearchServiceMock());
		selectedUnit.setName(SELECTED_UNIT_NAME);
		currentUnit.setName(CURRENT_UNIT_NAME);
		upcomingUnit.setName(UPCOMING_UNIT_NAME);
	}

	@Test
	@Ignore
	public void testExtractSessionInformation() throws Exception {

		VardvalInfo vardvalInfo = registerOnUnitController.getUnitRegistrationInformation(generateContextMock(), SELECTED_UNIT_ID);
		// Check selected unit info
		assertEquals(SELECTED_UNIT_NAME, vardvalInfo.getSelectedUnitName());
		assertEquals(SELECTED_UNIT_ID, vardvalInfo.getSelectedUnitId());
		// Check current unit info
		assertEquals(CURRENT_UNIT_NAME, vardvalInfo.getCurrentUnitName());
		assertEquals(CURRENT_UNIT_ID, vardvalInfo.getCurrentHsaId());
		// Check upcoming unit info
		assertEquals(UPCOMING_UNIT_NAME, vardvalInfo.getUpcomingUnitName());
		assertEquals(UPCOMING_UNIT_ID, vardvalInfo.getUpcomingHsaId());
	}

	@Test
	public void testGetBase64DecodedSsn() throws Exception {
		assertEquals("kalle banan", registerOnUnitController.getBase64DecodedSsn("a2FsbGUgYmFuYW4="));
	}

	@Test
	public void testGetDecryptedSsn() throws Exception {
		String encryptedString = Base64.encode(getEncryptedByteArray("197407185656"));
		assertEquals("197407185656", registerOnUnitController.getDecryptedSsn(Base64.decode(encryptedString)));
	}

	private byte[] getEncryptedByteArray(String inputString) throws Exception {
		byte[] preSharedKey = "ACME1234ACME1234QWERT123".getBytes();
		SecretKey aesKey = new SecretKeySpec(preSharedKey, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		String initialVector = "vardval0";
		IvParameterSpec ivs = new IvParameterSpec(initialVector.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivs);
		byte[] cipherText = cipher.doFinal(inputString.getBytes());
		return cipherText;
	}

	private JsfExternalContext generateContextMock() {
		JsfExternalContext mockJsfExternalContext = createMock(JsfExternalContext.class);
		FacesContext mockFacesContext = createMock(FacesContext.class);
		javax.faces.context.ExternalContext mockExternalContext = createMock(javax.faces.context.ExternalContext.class);
		expect(mockJsfExternalContext.getFacesContext()).andReturn(mockFacesContext);
		expect(mockFacesContext.getExternalContext()).andReturn(mockExternalContext);
		Map<String, String> requestHeaderMap = new HashMap<String, String>();
		requestHeaderMap.put("iv-user", SSN);
		expect(mockExternalContext.getRequestHeaderMap()).andReturn(requestHeaderMap);
		replay(mockJsfExternalContext, mockFacesContext, mockExternalContext);
		return mockJsfExternalContext;
	}

	private SearchService generateSearchServiceMock() throws Exception {
		SearchService mockSearchService = createMock(SearchService.class);
		expect(mockSearchService.getUnitByHsaId(SELECTED_UNIT_ID)).andReturn(selectedUnit);
		expect(mockSearchService.getUnitByHsaId(CURRENT_UNIT_ID)).andReturn(currentUnit);
		expect(mockSearchService.getUnitByHsaId(UPCOMING_UNIT_ID)).andReturn(upcomingUnit);
		replay(mockSearchService);
		return mockSearchService;
	}

	private VardvalService generateVardvalServiceMock() {
		VardvalInfo vardvalInfo = new VardvalInfo();
		vardvalInfo.setCurrentHsaId(CURRENT_UNIT_ID);
		vardvalInfo.setUpcomingHsaId(UPCOMING_UNIT_ID);
		VardvalService mockVardvalService = createMock(VardvalService.class);
		expect(mockVardvalService.getVardval(SSN)).andReturn(vardvalInfo);
		replay(mockVardvalService);
		return mockVardvalService;
	}

}
