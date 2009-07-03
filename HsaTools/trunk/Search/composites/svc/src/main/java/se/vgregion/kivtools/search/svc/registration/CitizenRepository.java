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
			String name = (String) attrs.get("cn").get();
			return name;
		}

	}
}
