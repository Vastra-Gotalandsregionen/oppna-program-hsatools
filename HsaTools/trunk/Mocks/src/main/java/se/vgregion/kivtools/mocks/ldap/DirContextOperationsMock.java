package se.vgregion.kivtools.mocks.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedSet;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Mock-class to use when unit testing Spring LDAP ContextMapper-implementations.
 * 
 * @author David Bennehult
 */
public class DirContextOperationsMock implements DirContextOperations {

  private Map<String, Object> attributes = new HashMap<String, Object>();
  private Name dn;

  @Override
  public void addAttributeValue(String name, Object value) {
    attributes.put(name, value);
  }

  @Override
  public Name getDn() {
    return dn;
  }

  @Override
  public String getStringAttribute(String name) {
    return (String) attributes.get(name);
  }

  @Override
  public String[] getStringAttributes(String name) {
    String[] strings = null;
    String string = (String) attributes.get(name);
    if (!StringUtil.isEmpty(string)) {
      strings = string.split("\\$");
    }
    return strings;
  }

  @Override
  public void setAttributeValue(String name, Object value) {
    attributes.remove(name);
    attributes.put(name, value);
  }

  @Override
  public void setDn(Name dn) {
    this.dn = dn;
  }

  // Unimplemented methods

  @Override
  public void update() {
  }

  @Override
  public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
  }

  @Override
  public void bind(String name, Object obj, Attributes attrs) throws NamingException {
  }

  @Override
  public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
    return null;
  }

  @Override
  public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
    return null;
  }

  @Override
  public Attributes getAttributes(Name name) throws NamingException {
    return null;
  }

  @Override
  public Attributes getAttributes(String name) throws NamingException {
    return null;
  }

  @Override
  public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
    return null;
  }

  @Override
  public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
    return null;
  }

  @Override
  public DirContext getSchema(Name name) throws NamingException {
    return null;
  }

  @Override
  public DirContext getSchema(String name) throws NamingException {
    return null;
  }

  @Override
  public DirContext getSchemaClassDefinition(Name name) throws NamingException {
    return null;
  }

  @Override
  public DirContext getSchemaClassDefinition(String name) throws NamingException {
    return null;
  }

  @Override
  public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
  }

  @Override
  public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
  }

  @Override
  public void modifyAttributes(Name name, int modOp, Attributes attrs) throws NamingException {
  }

  @Override
  public void modifyAttributes(String name, int modOp, Attributes attrs) throws NamingException {
  }

  @Override
  public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
  }

  @Override
  public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
    return null;
  }

  @Override
  public Object addToEnvironment(String propName, Object propVal) throws NamingException {
    return null;
  }

  @Override
  public void bind(Name name, Object obj) throws NamingException {
  }

  @Override
  public void bind(String name, Object obj) throws NamingException {
  }

  @Override
  public void close() throws NamingException {
  }

  @Override
  public Name composeName(Name name, Name prefix) throws NamingException {
    return null;
  }

  @Override
  public String composeName(String name, String prefix) throws NamingException {
    return null;
  }

  @Override
  public Context createSubcontext(Name name) throws NamingException {
    return null;
  }

  @Override
  public Context createSubcontext(String name) throws NamingException {
    return null;
  }

  @Override
  public void destroySubcontext(Name name) throws NamingException {
  }

  @Override
  public void destroySubcontext(String name) throws NamingException {
  }

  @Override
  public Hashtable<?, ?> getEnvironment() throws NamingException {
    return null;
  }

  @Override
  public NameParser getNameParser(Name name) throws NamingException {
    return null;
  }

  @Override
  public NameParser getNameParser(String name) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
    return null;
  }

  @Override
  public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
    return null;
  }

  @Override
  public Object lookup(Name name) throws NamingException {
    return null;
  }

  @Override
  public Object lookup(String name) throws NamingException {
    return null;
  }

  @Override
  public Object lookupLink(Name name) throws NamingException {
    return null;
  }

  @Override
  public Object lookupLink(String name) throws NamingException {
    return null;
  }

  @Override
  public void rebind(Name name, Object obj) throws NamingException {
  }

  @Override
  public void rebind(String name, Object obj) throws NamingException {
  }

  @Override
  public Object removeFromEnvironment(String propName) throws NamingException {
    return null;
  }

  @Override
  public void rename(Name oldName, Name newName) throws NamingException {
  }

  @Override
  public void rename(String oldName, String newName) throws NamingException {
  }

  @Override
  public void unbind(Name name) throws NamingException {
  }

  @Override
  public void unbind(String name) throws NamingException {
  }

  @Override
  public ModificationItem[] getModificationItems() {
    return null;
  }

  @Override
  public void addAttributeValue(String name, Object value, boolean addIfDuplicateExists) {
  }

  @Override
  @SuppressWarnings("unchecked")
  public SortedSet getAttributeSortedStringSet(String name) {
    return null;
  }

  @Override
  public Attributes getAttributes() {
    return null;
  }

  @Override
  public String getNameInNamespace() {
    return null;
  }

  @Override
  public String[] getNamesOfModifiedAttributes() {
    return null;
  }

  @Override
  public Object getObjectAttribute(String name) {
    return null;
  }

  @Override
  public Object[] getObjectAttributes(String name) {
    return null;
  }

  @Override
  public String getReferralUrl() {
    return null;
  }

  @Override
  public boolean isReferral() {
    return false;
  }

  @Override
  public boolean isUpdateMode() {
    return false;
  }

  @Override
  public void removeAttributeValue(String name, Object value) {
  }

  @Override
  public void setAttributeValues(String name, Object[] values) {
  }

  @Override
  public void setAttributeValues(String name, Object[] values, boolean orderMatters) {
  }
}