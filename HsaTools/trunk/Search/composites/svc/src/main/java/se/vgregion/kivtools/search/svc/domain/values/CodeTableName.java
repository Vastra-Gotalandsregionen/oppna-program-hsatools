package se.vgregion.kivtools.search.svc.domain.values;

/**
 * Enum types of valid code table names.
 * 
 * @author David & Jonas
 * 
 */
public enum CodeTableName {
  /** Administration Form. */
  HSA_ADMINISTRATION_FORM("list_hsaAdministrationForm"),
  /** Business classification code. */
  HSA_BUSINESSCLASSIFICATION_CODE("list_hsaBusinessClassificationCode"),
  /** County code. */
  HSA_COUNTY_CODE("list_hsaCountyCode"),
  /** Municipality code. */
  HSA_MUNICIPALITY_CODE("list_hsaMunicipalityCode"),
  /** Management code. */
  HSA_MANAGEMENT_CODE("list_hsaManagementCode"),
  /** Speciality code. */
  HSA_SPECIALITY_CODE("list_hsaSpecialityCode"),
  /** AO3 code (responsibility area code). */
  VGR_AO3_CODE("list_vgrAO3kod"),
  /** Care type. */
  VGR_CARE_TYPE("list_vgrCareType"),
  /** Language knowledge. */
  HSA_LANGUAGE_KNOWLEDGE_CODE("list_hsaLanguageKnowledgeCode"),
  /** Employment titles. */
  PA_TITLE_CODE("list_paTitleCode"), 
  /** Profession title. */
  HSA_TITLE("list_hsaTitle");

  private final String codeTableName;

  private CodeTableName(String s) {
    codeTableName = s;
  }

  @Override
  public String toString() {
    return codeTableName;
  }
}
