package se.vgregion.kivtools.search.svc.impl.mock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.novell.ldap.LDAPAttribute;

public class UnitLdapEntryMock extends LDAPEntryMock {

	public UnitLdapEntryMock(String name, String hsaIdentity, String objectClass, Date vgrCreateTimestamp, Date vgrModifyTimeStamp) {
		super();
		Map<String, LDAPAttribute> attributes = new HashMap<String, LDAPAttribute>();
		attributes.put("name", new LDAPAttribute("name", name));
		attributes.put("hsaIdentity", new LDAPAttribute("hsaIdentity", hsaIdentity));
		attributes.put("objectClass", new LDAPAttribute("objectClass", objectClass));
		attributes.put("createTimestamp", new LDAPAttribute("createTimestamp", getDateInZuloFormat(vgrCreateTimestamp)));
		attributes.put("vgrModifyTimestamp", new LDAPAttribute("vgrModifyTimestamp", getDateInZuloFormat(vgrModifyTimeStamp)));
		setAttributes(attributes);
	}

	private String getDateInZuloFormat(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
		return dateFormatter.format(date);
	}
}
