package se.vgregion.kivtools.search.svc.impl.mock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UnitLdapEntryMock extends LDAPEntryMock {

  public UnitLdapEntryMock(String name, String hsaIdentity, String objectClass, Date vgrCreateTimestamp, Date vgrModifyTimeStamp) {
    super();
    addAttribute("name", name);
    addAttribute("hsaIdentity", hsaIdentity);
    addAttribute("objectClass", objectClass);
    addAttribute("createTimestamp", getDateInZuloFormat(vgrCreateTimestamp));
    addAttribute("vgrModifyTimestamp", getDateInZuloFormat(vgrModifyTimeStamp));
  }

  private String getDateInZuloFormat(Date date) {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
    return dateFormatter.format(date);
  }
}
