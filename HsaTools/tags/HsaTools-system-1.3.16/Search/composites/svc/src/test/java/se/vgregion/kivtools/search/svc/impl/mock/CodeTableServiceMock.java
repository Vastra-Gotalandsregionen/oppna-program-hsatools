package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;

public class CodeTableServiceMock implements CodeTablesService {
  private final Map<CodeTableNameInterface, List<String>> codeTables = new HashMap<CodeTableNameInterface, List<String>>();

  public void addListToMap(CodeTableNameInterface key, List<String> list) {
    this.codeTables.put(key, list);
  }

  @Override
  public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
    return this.codeTables.get(codeTableName);
  }

  @Override
  public String getValueFromCode(CodeTableNameInterface codeTableName, String string) {
    return null;
  }

  @Override
  public List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
    return null;
  }

  @Override
  public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
    return null;
  }
}