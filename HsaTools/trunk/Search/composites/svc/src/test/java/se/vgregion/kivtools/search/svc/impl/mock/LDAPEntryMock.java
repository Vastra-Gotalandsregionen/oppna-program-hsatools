package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.HashMap;
import java.util.Map;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;

public class LDAPEntryMock extends LDAPEntry {
  private String dn = "";
  private Map<String, LDAPAttribute> attributes = new HashMap<String, LDAPAttribute>();

  public LDAPEntryMock() {
  
  }
  
  public LDAPEntryMock(String key, String[] values){
    attributes.put(key, new LDAPAttribute(key, values));
  }
  
  public LDAPEntryMock(String key, String value) {
    attributes.put(key, new LDAPAttribute(key, value));
  }
  
  public void addAttribute(String key, String value) {
    attributes.put(key, new LDAPAttribute(key, value));
  }

  public void addAttribute(String key, String[] values) {
    attributes.put(key, new LDAPAttribute(key, values));
  }

  public void setDn(String dn) {
    this.dn = dn;
  }

  @Override
  public LDAPAttribute getAttribute(String attrName) {
    LDAPAttribute attribute = attributes.get(attrName);
    return attribute;
  }

  @Override
  public String getDN() {
    return this.dn;
  }
}
