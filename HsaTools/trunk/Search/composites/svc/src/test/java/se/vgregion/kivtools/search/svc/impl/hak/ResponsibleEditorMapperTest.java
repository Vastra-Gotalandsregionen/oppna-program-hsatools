package se.vgregion.kivtools.search.svc.impl.hak;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class ResponsibleEditorMapperTest {
  private DirContextOperationsMock dirContextOperations;
  private ResponsibleEditorMapper responsibleEditorMapper;

  @Before
  public void setUp() throws Exception {
    responsibleEditorMapper = new ResponsibleEditorMapper();
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    ResponsibleEditorMapper mapper = new ResponsibleEditorMapper();
    assertNotNull(mapper);
  }

  @Test
  public void testNoMemberAttributeSet() {
    @SuppressWarnings("unchecked")
    List<String> responsibleEditors = (List<String>) responsibleEditorMapper.mapFromContext(dirContextOperations);
    assertNotNull(responsibleEditors);
  }

  @Test
  public void testOneAttributeSet() {
    dirContextOperations.addAttributeValue("member", "cn=abc123,ou=test1,ou=test2");
    @SuppressWarnings("unchecked")
    List<String> responsibleEditors = (List<String>) responsibleEditorMapper.mapFromContext(dirContextOperations);
    assertEquals(1, responsibleEditors.size());
    assertEquals("abc123", responsibleEditors.get(0));
  }

  @Test
  public void testMultipleAttributesSet() {
    dirContextOperations.addAttributeValue("member", "cn=abc123,ou=test1,ou=test2$cn=def456,ou=test2$cn=ghi789");
    @SuppressWarnings("unchecked")
    List<String> responsibleEditors = (List<String>) responsibleEditorMapper.mapFromContext(dirContextOperations);
    assertEquals(3, responsibleEditors.size());
    assertTrue(responsibleEditors.contains("abc123"));
    assertTrue(responsibleEditors.contains("def456"));
    assertTrue(responsibleEditors.contains("ghi789"));
  }
}
