package com.scorpio4.fact;

import com.scorpio4.util.Identifiable;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Scorpio4 (c) 2014
 * Module: com.scorpio4.fact
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 7:13 PM
 *
 * @author lee
 *
 * Represents a Sesame Connection to a named graph.
 *
 * The user of the FactSpace is free to determine how to interpret it's identify
 * as a context (quad) or simply as the name of the default graph at the given repository.
 *
 *
 *
 */
public class FactSpace implements Identifiable {
	Repository repository;
    String context;
	RepositoryConnection connection;

	public FactSpace(String context, Repository repository) throws RepositoryException {
        this.context=context;
		this.repository=repository;
		this.connection = repository.getConnection();
    }

    public RepositoryConnection  getConnection() throws RepositoryException {
	    return connection;
    }

	public void close() throws RepositoryException {
		if (connection!=null) connection.close();
	}

    @Override
    public String getIdentity() {
        return context;
    }
}
