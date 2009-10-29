package se.vgregion.kivtools.search.svc.impl.hak;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;

/**
 * ContextMapper for fetching all responsible editors from a responsible editor node.
 * 
 * @author Joakim Olsson
 */
final class ResponsibleEditorMapper implements ContextMapper {

  /**
   * Maps the member-attribute to a list of strings containing the common names (user id's) of the responsible editors.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return A list of common names of the responsible editors or an empty list if no editors are found.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    List<String> result = new ArrayList<String>();
    DirContextOperations dirContext = (DirContextOperations) ctx;
    String[] members = dirContext.getStringAttributes("member");
    if (members != null) {
      for (String member : members) {
        result.add(new DistinguishedName(member).getValue("cn"));
      }
    }
    return result;
  }
}
