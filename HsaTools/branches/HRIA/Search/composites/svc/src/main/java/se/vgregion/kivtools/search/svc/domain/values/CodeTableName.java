package se.vgregion.kivtools.search.svc.domain.values;

/**
 * Enum types of valid code table names.
 * 
 * @author David & Jonas
 * 
 */
public enum CodeTableName {
  HSA_ADMINISTRATION_FORM("list_hsaAdministrationForm"), HSA_BUSINESSCLASSIFICATION_CODE("list_hsaBusinessClassificationCode"), HSA_COUNTY_CODE("list_hsaCountyCode"), HSA_MUNICIPALITY_CODE(
      "list_hsaMunicipalityCode"), HSA_MANAGEMENT_CODE("list_hsaManagementCode"), HSA_SPECIALITY_CODE("list_hsaSpecialityCode"), VGR_AO3_CODE("list_vgrAO3kod"), VGR_CARE_TYPE("list_vgrCareType");
  private final String codeTableName;

  private CodeTableName(String s) {
    codeTableName = s;
  }

  @Override
  public String toString() {
    return codeTableName;
  }
}
