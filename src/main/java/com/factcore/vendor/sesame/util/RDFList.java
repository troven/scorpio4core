package com.factcore.vendor.sesame.util;

import com.factcore.vocab.COMMON;
import org.openrdf.model.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.vendor.sesame.util
 * User  : lee
 * Date  : 27/05/2014
 * Time  : 11:04 PM
 * Apache Licensed.
 */
public class RDFList {
    private static final Logger log = LoggerFactory.getLogger(RDFList.class);

    RepositoryConnection connection = null;
    URI rdfFirst = null, rdfRest = null, rdfNil = null, context = null;
    boolean useInferred = true;
    Map seen = new HashMap();

    public RDFList(RepositoryConnection connection) {
        this(connection, null);
    }

    public RDFList(RepositoryConnection connection, String context) {
        this.connection=connection;
        ValueFactory vf = connection.getValueFactory();
        this.rdfFirst = vf.createURI(COMMON.RDF+"first");
        this.rdfRest = vf.createURI(COMMON.RDF+"rest");
        this.rdfNil = vf.createURI(COMMON.RDF+"nil");
        if (context!=null) this.context = vf.createURI(context);
    }

    public Collection<Value> getList(String head, String predicate) throws RepositoryException {
        ValueFactory vf = connection.getValueFactory();
        return getList(vf.createURI(head),vf.createURI(predicate));
    }

    public Collection<Value> getList(Resource head, URI predicate) throws RepositoryException {
        List<Value> list = new ArrayList();
        log.debug("getList: "+head+" -> "+predicate+" @ "+context);
        RepositoryResult<Statement> statements = connection.getStatements(head, predicate, null, useInferred, context);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            log.debug("Found: "+statement);
            Object object = statement.getObject();
            if (object instanceof Resource) {
                addToList(list, (Resource) object);
            }
        }
        return list;
    }

    protected void addToList(Collection<Value> list, Resource head) throws RepositoryException {
        if (seen.containsKey(head.stringValue())) return;
        seen.put(head.stringValue(), true);
        RepositoryResult<Statement> statements = connection.getStatements(head, rdfFirst, null, useInferred, context);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            list.add(statement.getObject());
        }
        RepositoryResult<Statement> nexts = connection.getStatements(head, rdfRest, null, useInferred, context);
        while (nexts.hasNext()) {
            Statement statement = nexts.next();
            Object object = statement.getObject();
            if (object!=null && !object.equals(rdfNil) && object instanceof Resource) {
                addToList(list, (Resource) object);
            }
        }
    }

}
