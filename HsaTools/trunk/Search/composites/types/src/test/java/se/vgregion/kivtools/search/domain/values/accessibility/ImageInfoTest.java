package se.vgregion.kivtools.search.domain.values.accessibility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.search.domain.values.accessibility.ImageInfo;

public class ImageInfoTest {
  private static final Document DOC_WITH_CRITERIA = XmlHelper.getDocumentFromResource("testxml/doc_with_imageinfo.xml");
  private NodeList nodeList;
  private ImageInfo imageInfo;

  @Before
  public void setUp() {
    nodeList = DOC_WITH_CRITERIA.getElementsByTagName("imageinfo");
  }

  @Test
  public void testCreateBlockFromNode() {
    Node node = nodeList.item(0);
    imageInfo = ImageInfo.createImageInfoFromNode(node);
    assertNotNull(imageInfo);
    assertNull(imageInfo.getLongDescription());
    assertNull(imageInfo.getShortDescription());
    assertNull(imageInfo.getUrl());
    assertNull(imageInfo.getUrlLarge());

    node = nodeList.item(1);
    imageInfo = ImageInfo.createImageInfoFromNode(node);
    assertNotNull(imageInfo);
    assertEquals("longDesc", imageInfo.getLongDescription());
    assertEquals("shortDesc", imageInfo.getShortDescription());
    assertEquals("http://image.url.com", imageInfo.getUrl());
    assertNull(imageInfo.getUrlLarge());

    node = nodeList.item(2);
    imageInfo = ImageInfo.createImageInfoFromNode(node);
    assertNotNull(imageInfo);
    assertEquals("http://small.image.url.com/small_image.png", imageInfo.getUrl());
    assertEquals("http://large.image.url.com/large_image.png", imageInfo.getUrlLarge());
  }
}
