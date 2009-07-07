package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Unit;

public class UnitRepositoryTest {

	private UnitRepository unitRepository;
	private Unit resultUnit;

	@Before
	public void setUp() throws Exception {
		final SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
		resultUnit = new Unit();
		resultUnit.setHsaBusinessClassificationCode(Arrays.asList("1"));
		result.add(resultUnit);
		unitRepository = new UnitRepository() {
			@Override
			protected SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws Exception {
				return result;
			}
		};
	}

	@Test
	public void testHsaEndDate() throws Exception {
		// No hsaEndDate set, ie unit should be returned
		SikSearchResultList<Unit> resultList = unitRepository.searchAdvancedUnits(new Unit(), 1, null, Arrays.asList(1));
		assertNotNull("Result should not be null!", resultList);
		assertEquals(1, resultList.size());

		// hsaEndDate set to a "past date", ie unit should NOT be returned
		resultUnit.setHsaEndDate(Constants.parseStringToZuluTime("20090101000000Z"));
		resultList = unitRepository.searchAdvancedUnits(new Unit(), 1, null, Arrays.asList(1));
		assertEquals(0, resultList.size());
	}
}
