package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.util.PagedSearchMetaDataHelper;

public class PagedSearchMetaDataHelperTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testInstantiation() {
    PagedSearchMetaDataHelper helper = new PagedSearchMetaDataHelper();
    assertNotNull(helper);
  }

  @Test
  public void testBuildPagedSearchMetaData() {
    try {
      PagedSearchMetaDataHelper.buildPagedSearchMetaData(null, 0);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    List<PagedSearchMetaData> metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(null, 1);
    assertNotNull(metaData);
    assertEquals(0, metaData.size());

    List<Object> data = new ArrayList<Object>();
    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 1);
    assertNotNull(metaData);
    assertEquals(0, metaData.size());

    data.add(new Object());
    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 1);
    assertNotNull(metaData);
    assertEquals(1, metaData.size());

    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 3);
    assertNotNull(metaData);
    assertEquals(1, metaData.size());

    data.add(new Object());
    data.add(new Object());
    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 1);
    assertNotNull(metaData);
    assertEquals(3, metaData.size());
  }
}
