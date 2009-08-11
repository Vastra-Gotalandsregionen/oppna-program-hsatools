package se.vgregion.kivtools.search.svc.registration;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Repository for citizens.
 * 
 * @author Jonas Liljenfeldt, Know IT & Joakim Olsson, Unbound
 * 
 */
public class CitizenRepository {

	private LdapTemplate ldapTemplate;

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	public String getCitizenNameFromSsn(String ssn) {
		String name = (String) ldapTemplate.lookup("uid=" + ssn, new CitizenMapper());
		return name;
	}

	class CitizenMapper implements AttributesMapper {
		@Override
		public Object mapFromAttributes(Attributes attrs) throws NamingException {
			String givenName = (String) attrs.get("cn").get();
			String surName = (String) attrs.get("sn").get();
			String name = "";
			if (givenName.contains(surName)) {
				name = givenName;
			} else {
				name = givenName + " " + surName;
			}
			return name;
		}
	}
}
