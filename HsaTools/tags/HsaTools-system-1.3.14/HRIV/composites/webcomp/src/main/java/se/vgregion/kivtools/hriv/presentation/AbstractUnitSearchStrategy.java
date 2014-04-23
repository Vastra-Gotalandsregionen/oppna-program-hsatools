package se.vgregion.kivtools.hriv.presentation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;

public abstract class AbstractUnitSearchStrategy {
  private final Log logger = LogFactory.getLog(this.getClass());

  protected String cleanUnitName(String unitName) {
    String cleanedName = unitName;
    int lastComma = cleanedName.lastIndexOf(",");
    if (lastComma != -1) {
      cleanedName = cleanedName.substring(0, lastComma);
    }

    return cleanedName;
  }

  protected Unit mapSearchCriteriaToUnit(UnitSearchSimpleForm theForm) {
    this.logger.debug(this.getClass().getName() + ".mapSearchCriteriaToUnit(...)");
    Unit unit = new Unit();

    // unit name
    unit.setName(theForm.getUnitName());
    // hsaStreetAddress
    List<String> list = new ArrayList<String>();
    list.add(theForm.getMunicipality());
    unit.setHsaStreetAddress(AddressHelper.convertToAddress(list));

    List<String> hsaBusinessClassificationCode = new ArrayList<String>();
    hsaBusinessClassificationCode.add(theForm.getUnitName());
    unit.setHsaBusinessClassificationCode(hsaBusinessClassificationCode);

    // hsaPostalAddress
    list = new ArrayList<String>();
    list.add(theForm.getMunicipality());
    Address adress = new Address();
    // we stuff in the text in the additionalInfo
    adress.setAdditionalInfo(list);
    unit.setHsaPostalAddress(adress);

    // hsaMunicipalityCode
    unit.setHsaMunicipalityCode(theForm.getMunicipality());

    // Assign health care types
    Integer healthcareTypeIndex;
    try {
      healthcareTypeIndex = Integer.parseInt(theForm.getHealthcareType());
    } catch (NumberFormatException nfe) {
      // No health care type was chosen.
      healthcareTypeIndex = null;
    }
    if (healthcareTypeIndex != null) {
      HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
      HealthcareType ht = htch.getHealthcareTypeByIndex(healthcareTypeIndex);
      if (ht != null) {
        unit.addHealthcareType(ht);
      }
    }

    return unit;
  }
}
