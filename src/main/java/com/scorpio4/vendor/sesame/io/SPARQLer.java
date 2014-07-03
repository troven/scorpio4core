package com.scorpio4.vendor.sesame.io;
/*
 *   Scorpio4 - CONFIDENTIAL
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *  25/04/2014 re-licensed BSD License [Lee Curtis].
 */
import com.scorpio4.fact.FactSpace;
import com.scorpio4.oops.FactException;
import com.scorpio4.vocab.COMMON;
import org.openrdf.model.*;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.n3.N3Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Scorpio4 (c) 2010-2013
 *
 *
 */

public class SPARQLer {
	private static final Logger log = LoggerFactory.getLogger(SPARQLer.class);
	RepositoryConnection from = null;
    String ctx = null;
    boolean isInferred = false;

	public SPARQLer(RepositoryConnection from, String ctx) {
		this.from = from;
        this.ctx = ctx;
	}

    public SPARQLer(FactSpace factSpace) {
        this.from = factSpace.getConnection();
        this.ctx = factSpace.getIdentity();
    }

    public void clean(RepositoryConnection to) throws RepositoryException {
        URI context = to.getValueFactory().createURI(ctx);
        to.clear(context);
    }

//	public int copy(String query, String ctx) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
//		URI context = from.getValueFactory().createURI(ctx);
//		return copy(from, context, query, false);
//	}

    public int copy(String query) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
        URI context = from.getValueFactory().createURI(ctx);
        return copy(from, context, query, isInferred);
    }

    public int copy(RepositoryConnection to, String query) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
        URI context = from.getValueFactory().createURI(ctx);
        return copy(to, context, query, isInferred);
    }

	public int copy(RepositoryConnection to, String query, String ctx) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
		URI context = from.getValueFactory().createURI(ctx);
		return copy(to, context, query, isInferred);
	}

	public int copy(RepositoryConnection to, Resource context, String query, boolean remove) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
		GraphQuery graphQuery = from.prepareGraphQuery(QueryLanguage.SPARQL, query);
log.debug("GraphQuery: "+query+"\n -> from: "+graphQuery.getClass());
		GraphQueryResult result = graphQuery.evaluate();
		return copy(to, context, result, remove);
	}

	public int copy(RepositoryConnection to, Resource context, GraphQueryResult result , boolean remove) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
		int count = 0;
        copyNamespaces(to);
		ValueFactory valueFactory = to.getRepository().getValueFactory();
		while (result.hasNext()) {
			Statement stmt = result.next();
			if (remove) from.remove(stmt.getSubject(), stmt.getPredicate(), null);
			Statement new_stmt = valueFactory.createStatement(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
			to.add(new_stmt, context);
			count++;
		}
        log.debug("Copied "+count+" GraphQuery into: "+context.toString());

		result.close();
		to.commit();
		return count;
	}

	public void copy(String query, File file) throws FactException, IOException, QueryEvaluationException, RDFHandlerException {
		N3Writer n3_writer = new N3Writer(new FileWriter(file));
		copy(query, n3_writer);
	}

	public void copy(String query, N3Writer n3_writer) throws FactException, QueryEvaluationException, RDFHandlerException {
		if (query==null) throw new FactException("Can't query query: " + query);
		try {
			GraphQuery gq = from.prepareGraphQuery(QueryLanguage.SPARQL, query);
			gq.evaluate(n3_writer);
		} catch (RepositoryException re) {
			throw new FactException("Reading failed", re);
		} catch (MalformedQueryException mqe) {
			throw new FactException("Query failed", mqe);
		}

	}

	public void copyNamespaces(RepositoryConnection to) throws RepositoryException {
		copyNamespaces(from, to);
	}

    public static void copyNamespaces(RepositoryConnection from, RepositoryConnection to) throws RepositoryException {
        defaultNamespaces(to);

        RepositoryResult<Namespace> namespaces = from.getNamespaces();

        for(org.openrdf.model.Namespace namespace:namespaces.asList()) {
            if (!namespace.getPrefix().equals(""))
                to.setNamespace(namespace.getPrefix(), namespace.getName());
        }
    }

    public static void defaultNamespaces(RepositoryConnection to) throws RepositoryException {
        to.setNamespace("rdf", COMMON.RDF);
        to.setNamespace("rdfs", COMMON.RDFS);
        to.setNamespace("owl", COMMON.OWL);
        to.setNamespace("skos", COMMON.SKOS);
        to.setNamespace("dc", COMMON.DC);
        to.setNamespace("xsd", COMMON.XSD);
        to.setNamespace("acl", COMMON.ACL);
    }
}
