package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.util.Map;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.executor.jsf.JsfExternalContext;

import se.vgregion.kivtools.search.presentation.forms.RegistrationConfirmationForm;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;

public class RegisterOnUnitController implements Serializable {

	private static final long serialVersionUID = 1L;
	VardvalService vardValService;
	SearchService searchService;

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setVardValService(VardvalService vardValService) {
		this.vardValService = vardValService;
	}

	public VardvalInfo getUnitRegistrationInformation(ExternalContext externalContext, String selectedUnitId) throws Exception {

		JsfExternalContext jsfExternalContext = (JsfExternalContext) externalContext;

		// WAS returns null. Maybe because the url is not protected in web.xml?
		// Principal remoteUser = externalContext.getUserPrincipal();

		Map<String, String> requestHeaderMap = jsfExternalContext.getFacesContext().getExternalContext().getRequestHeaderMap();

		String ssn = requestHeaderMap.get("iv-user");
		// String ldapPath = requestHeaderMap.get("iv-user-1");

		// FIXME Remove this line
		ssn = "194509259257";

		// Request information about the listing from Vårdvals system's
		// webservice
		VardvalInfo vardvalInfo = new VardvalInfo();
		try {
			vardvalInfo = vardValService.getVardval(ssn);

			// Lookup unit names in order to show real names instead of hsa ids
			// TODO Exception handling
			Unit selectedUnit = searchService.getUnitByHsaId(selectedUnitId);
			if (selectedUnit != null) {
				vardvalInfo.setSelectedUnitName(selectedUnit.getName());
			}
			vardvalInfo.setSelectedUnitId(selectedUnitId);
			Unit currentUnit = searchService.getUnitByHsaId(vardvalInfo.getCurrentHsaId());
			Unit upcomingUnit = searchService.getUnitByHsaId(vardvalInfo.getUpcomingHsaId());

			// Set values in DTO
			if (currentUnit != null) {
				vardvalInfo.setCurrentUnitName(currentUnit.getName());
			}
			if (upcomingUnit != null) {
				vardvalInfo.setUpcomingUnitName(upcomingUnit.getName());
			}
			vardvalInfo.setVardvalInfo(vardvalInfo);
		} catch (Exception e) {
			// FIXME Proper exception handling. Show information page if we
			// could not find unit with given hsa id
		}

		vardvalInfo.setSsn(ssn);
		return vardvalInfo;
	}

	public String commitRegistrationOnUnit(VardvalInfo vardvalInfo) {
		// signature should be byte[]
		vardValService.setVardval(vardvalInfo.getSsn(), vardvalInfo.getSelectedUnitId(), "".getBytes());//vardvalInfo.getSignature().getBytes());
		return "success";
	}

}
