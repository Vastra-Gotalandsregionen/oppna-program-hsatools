package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.HashMap;
import java.util.Map;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;

public class LDAPEntryMock extends LDAPEntry {

	private Map<String, String> attributes = new HashMap<String, String>();

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(String key, String value){
		attributes.put(key, value);
	}

	@Override
	public LDAPAttribute getAttribute(String attrName) {
		LDAPAttribute attribute = new LDAPAttribute(attrName, attributes.get(attrName));
		return attribute;
	}
}
