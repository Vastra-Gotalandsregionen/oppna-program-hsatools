package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.filter.Filter;

import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;

public class KivLdapFilterHelperTest {

  private static final String HSA_BUSINESS_CLASSIFICATION_CODE = "cond1";
  private static final String HSA_BUSINESS_CLASSIFICATION_VALUE = "1000,1100,1500";
  private static final String VGR_CARE_TYPE = "cond2";
  private static final String VGR_CARE_TYPE_VALUE = "01";
  private List<HealthcareType> healthcareTypes;

  @Before
  public void setup() {
    HealthcareType healthcareType1 = new HealthcareType();
    Map<String, String> conditions = new HashMap<String, String>();
    healthcareType1.setConditions(conditions);
    conditions.put(HSA_BUSINESS_CLASSIFICATION_CODE, HSA_BUSINESS_CLASSIFICATION_VALUE);
    conditions.put(VGR_CARE_TYPE, VGR_CARE_TYPE_VALUE);

    HealthcareType healthcareType2 = new HealthcareType();
    healthcareType2.setConditions(conditions);
    healthcareTypes = new ArrayList<HealthcareType>();
    healthcareTypes.add(healthcareType1);
    healthcareTypes.add(healthcareType2);
  }

  @Test
  public void testInstance() {
    assertNotNull(new KivLdapFilterHelper());
  }

  @Test
  public void testCreateHealthcareTypeFilter() {
    Filter createHealthcareTypeFilter = KivLdapFilterHelper.createHealthcareTypeFilter(healthcareTypes);
    String filter = createHealthcareTypeFilter.encode();
    assertEquals("(|(&(cond2=01)(|(cond1=1000)(cond1=1100)(cond1=1500)))(&(cond2=01)(|(cond1=1000)(cond1=1100)(cond1=1500))))", filter);
  }
}
