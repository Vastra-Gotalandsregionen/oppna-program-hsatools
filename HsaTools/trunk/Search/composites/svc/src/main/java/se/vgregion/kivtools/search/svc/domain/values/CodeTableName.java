package se.vgregion.kivtools.search.svc.domain.values;

public enum CodeTableName {
	HSA_ADMINISTRATION_FORM("list_hsaAdministrationForm"), HSA_BUSINESSCLASSIFICATION_CODE("list_hsaBusinessClassificationCode"), HSA_COUNTY_CODE("list_hsaCountyCode"), HSA_MANAGEMENT_CODE("list_hsaManagementCode"), HSA_SPECIALITY_CODE(
			"list_hsaSpecialityCode");
	private final String codeTableName;

	private CodeTableName(String s) {
		codeTableName = s;
	}

	@Override
	public String toString() {
		return codeTableName;
	}

}
