package com.scorpio4.vendor.sesame.util;

import org.openrdf.model.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.sesame.util
 * User  : lee
 * Date  : 30/06/2014
 * Time  : 10:53 PM
 */
public class RDFScalars {
	private static final Logger log = LoggerFactory.getLogger(RDFScalars.class);

	RepositoryConnection connection = null;
	ValueFactory vf = null;
	URI context = null;
	boolean useInferred = true;

	public RDFScalars(RepositoryConnection connection) {
		this(connection, null);
	}

	public RDFScalars(RepositoryConnection connection, String context) {
		this.connection=connection;
		vf = connection.getValueFactory();
		if (context!=null) this.context = vf.createURI(context);
	}

	public Literal getLiteral(Resource s, URI p, String xsdType) throws RepositoryException {
		Literal found = null;
		RepositoryResult<Statement> statements = connection.getStatements(s, p, null, useInferred, context);
		URI xsdURI = vf.createURI(xsdType);
		while(statements.hasNext()) {
			Statement statement = statements.next();
			Value o = statement.getObject();
			if (o instanceof Literal) {
				Literal l = (Literal)o;
				if (l.getDatatype()==null && found == null) found = l;
				else if (l.getDatatype().equals(xsdURI)) found = l;
			}
		}
		return found;
	}

	public URI getURI(Resource s, URI p) throws RepositoryException {
		RepositoryResult<Statement> statements = connection.getStatements(s, p, null, useInferred, context);
		while(statements.hasNext()) {
			Statement statement = statements.next();
			if (statement.getObject() instanceof URI) return (URI)statement.getObject();
		}
		return null;
	}

	public Value getValue(Resource s, URI p) throws RepositoryException {
		RepositoryResult<Statement> statements = connection.getStatements(s, p, null, useInferred, context);
		while(statements.hasNext()) {
			Statement statement = statements.next();
			return statement.getObject();
		}
		return null;
	}
}
