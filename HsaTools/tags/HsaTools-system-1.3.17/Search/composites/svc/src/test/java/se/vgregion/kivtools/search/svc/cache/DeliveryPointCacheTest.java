package se.vgregion.kivtools.search.svc.cache;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import java.util.List;

import org.junit.Before;

import se.vgregion.kivtools.search.domain.Deliverypoint;

public class DeliveryPointCacheTest {

	private DeliveryPointCache deliverypointcache;

	@Before
	public void setUp() {
		deliverypointcache = new DeliveryPointCache();
		Deliverypoint dp = new Deliverypoint();
		dp.setHsaIdentity("SE2321000131-S000000000791");
		dp.setVgrEanCode("7332784010238");
		List<String> vgrOrgRel = new ArrayList<String>();
		vgrOrgRel.add("SE2321000131-E000000002505");
		vgrOrgRel.add("SE2321000131-E000000002506");
		vgrOrgRel.add("SE2321000131-E000000002508");
		dp.setVgrOrgRel(vgrOrgRel);
		deliverypointcache.add(dp);
		
		Deliverypoint dp2 = new Deliverypoint();
		dp2.setHsaIdentity("SE2321000131-S000000000792");
		dp2.setVgrEanCode("6662784010238");
		vgrOrgRel = new ArrayList<String>();
		vgrOrgRel.add("SE2321000131-E000000002505");
		vgrOrgRel.add("SE2321000131-E000000002506");
		vgrOrgRel.add("SE2321000131-E000000002508");

		dp2.setVgrOrgRel(vgrOrgRel);

		deliverypointcache.add(dp2);
		deliverypointcache.createUnitHsaIdentityMatchOnDeliverPoint();
	}

	public void testInstantiation() {
		deliverypointcache = new DeliveryPointCache();
		Deliverypoint dp = new Deliverypoint();
		assertNotNull(dp);
	}



	public void test() {
	//	fail("Not yet implemented");
	}

}
