package se.vgregion.kivtools.search.svc.kiv.organizationtree.springldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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

public class MockDirContext implements DirContext {

    private NamingEnumeration<SearchResult> results;
    private boolean throwNamingException = false;

    public void setThrowNamingException(boolean throwNamingException) {
        this.throwNamingException = throwNamingException;
    }

    public MockDirContext(List<SearchResult> results) {
        super();
        this.results = new ResultList(results);
    }

    public NamingEnumeration<SearchResult> getResults() {
        return results;
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
        if (!throwNamingException) {
            return results;
        } else {
            throw new NamingException();
        }
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
    public String getNameInNamespace() throws NamingException {
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

    class ResultList implements NamingEnumeration<SearchResult> {

        List<SearchResult> searchResults;

        public ResultList(List<SearchResult> searchResult) {
            this.searchResults = new ArrayList<SearchResult>(searchResult);
        }

        @Override
        public void close() throws NamingException {
        }

        @Override
        public boolean hasMore() throws NamingException {
            return searchResults.size() > 0;
        }

        @Override
        public SearchResult next() throws NamingException {
            return searchResults.remove(0);
        }

        @Override
        public boolean hasMoreElements() {
            return searchResults.size() > 0;
        }

        @Override
        public SearchResult nextElement() {
            return searchResults.remove(0);
        }

    }
}
