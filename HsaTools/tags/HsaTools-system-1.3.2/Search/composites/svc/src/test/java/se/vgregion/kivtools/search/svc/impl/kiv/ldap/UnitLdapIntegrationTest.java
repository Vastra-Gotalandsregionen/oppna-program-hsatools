/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "/services-config_VGR.xml" })
public class UnitLdapIntegrationTest {

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private CodeTablesService codeTablesService;

	@Autowired
	private DisplayValueTranslator displayValueTranslator;

	@Ignore
	@Test
	public void test() {
		UnitMapper unitMapper = new UnitMapper(codeTablesService, displayValueTranslator);
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_UNIT_SPECIFIC));
		andFilter.and(new EqualsFilter(UnitLdapAttributes.HSA_IDENTITY, "SE2321000131-E000000006737"));

		@SuppressWarnings("unchecked")
		List<Unit> unitsList = ldapTemplate.search("", andFilter.encode(), SearchControls.SUBTREE_SCOPE, unitMapper);
		Assert.assertEquals(1, unitsList.size());
	}
}
