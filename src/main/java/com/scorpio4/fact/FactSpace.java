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
