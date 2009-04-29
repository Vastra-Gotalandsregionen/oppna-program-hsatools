package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.HashMap;
import java.util.Map;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;

public class LDAPEntryMock extends LDAPEntry {

	private Map<String, LDAPAttribute> attributes = new HashMap<String, LDAPAttribute>();

	public Map<String, LDAPAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, LDAPAttribute> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(String key, String value){
		attributes.put(key, new LDAPAttribute(key, value));
	}
	
	public void addAttribute(String key, String[] values){
		attributes.put(key, new LDAPAttribute(key, values));
	}

	@Override
	public LDAPAttribute getAttribute(String attrName) {
		LDAPAttribute attribute = attributes.get(attrName);
		return attribute;
	}
}
