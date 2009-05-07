package se.vgregion.kivtools.search.svc.push;

import java.util.List;

import org.junit.Test;
import org.springframework.util.Assert;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.push.impl.eniro.InformationPusherEniro;

public class UnitInformationPusherTest {

	@Test
	public void testPush() {
		
	}
	
	@Test
	public void testCollectData() throws Exception {
		InformationPusher pusher = new InformationPusherEniro();
		List<Unit> unitInformations = pusher.collectData();
		Assert.notNull(unitInformations);
	}
}
