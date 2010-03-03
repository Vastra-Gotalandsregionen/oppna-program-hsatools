/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

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
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;

/**
 * Mock-class to use when unit testing Spring LDAP ContextMapper-implementations.
 * 
 * @author David Bennehult
 */
public class DirContextOperationsMock implements DirContextOperations {

    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Name dn = DistinguishedName.EMPTY_PATH;

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
        String[] result = null;
        Object value = attributes.get(name);
        if (value != null) {
            if (value instanceof String[]) {
                result = (String[]) value;
            } else if (value instanceof String) {
                result = new String[] { (String) value };
            } else {
                throw new ClassCastException("Cannot cast " + value.getClass().getName() + " to String[]");
            }
        }
        return result;
    }

    @Override
    public Object getObjectAttribute(String name) {
        return attributes.get(name);
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

    @Override
    public Attributes getAttributes() {
        return new AttributesMock(attributes);
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
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes)
            throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes,
            String[] attributesToReturn) throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes,
            String[] attributesToReturn) throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons)
            throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons)
            throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs,
            SearchControls cons) throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs,
            SearchControls cons) throws NamingException {
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
    public String getNameInNamespace() {
        return null;
    }

    @Override
    public String[] getNamesOfModifiedAttributes() {
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
        attributes.remove(name);
        attributes.put(name, "");
    }

    @Override
    public void setAttributeValues(String name, Object[] values) {
    }

    @Override
    public void setAttributeValues(String name, Object[] values, boolean orderMatters) {
    }

    /**
     * Mock class for attributes on an LDAP entry.
     */
    private static class AttributesMock implements Attributes {
        private static final long serialVersionUID = 5567444651855789466L;
        private Map<String, Attribute> attributes = new HashMap<String, Attribute>();

        /**
         * Construct a new AttributesMock.
         * 
         * @param attributes
         *            the attributes to populate the AttributesMock with.
         */
        public AttributesMock(Map<String, Object> attributes) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                AttributeMock attribute = new AttributeMock(entry.getKey(), entry.getValue());
                this.attributes.put(entry.getKey(), attribute);
            }
        }

        @Override
        public Attribute get(String attrID) {
            return this.attributes.get(attrID);
        }

        // Not implemented

        @Override
        public NamingEnumeration<? extends Attribute> getAll() {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public NamingEnumeration<String> getIDs() {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public boolean isCaseIgnored() {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Attribute put(Attribute attr) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Attribute put(String attrID, Object val) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Attribute remove(String attrID) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Attributes clone() {
            throw new UnsupportedOperationException("Not implemented!");
        }
    }

    /**
     * Mock class for one single attribute.
     */
    private static class AttributeMock implements Attribute {
        private static final long serialVersionUID = 6268634553966629756L;
        private final String name;
        private final Object value;

        /**
         * Constructs a new AttributeMock.
         * 
         * @param name
         *            the name of the attribute.
         * @param value
         *            the value of the attribute.
         */
        public AttributeMock(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getID() {
            return this.name;
        }

        @Override
        public Object get() throws NamingException {
            return this.value;
        }

        // Not implemented

        @Override
        public boolean add(Object attrVal) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public void add(int ix, Object attrVal) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public boolean contains(Object attrVal) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Object get(int ix) throws NamingException {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public NamingEnumeration<?> getAll() throws NamingException {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public DirContext getAttributeDefinition() throws NamingException {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public DirContext getAttributeSyntaxDefinition() throws NamingException {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public boolean isOrdered() {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public boolean remove(Object attrval) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Object remove(int ix) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Object set(int ix, Object attrVal) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public Attribute clone() {
            throw new UnsupportedOperationException("Not implemented!");
        }
    }
}
