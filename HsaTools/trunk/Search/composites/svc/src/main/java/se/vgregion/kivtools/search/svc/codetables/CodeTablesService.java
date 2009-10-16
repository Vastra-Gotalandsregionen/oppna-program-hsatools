package se.vgregion.kivtools.search.svc.codetables;

import java.util.List;

import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;

public interface CodeTablesService {

  void init();

  String getValueFromCode(CodeTableName codeTableName, String string);
  
  List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue);
}
