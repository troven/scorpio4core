package com.scorpio4.fact;

import com.scorpio4.util.Identifiable;
import org.openrdf.repository.RepositoryConnection;

/**
 * Fact:Core (c) 2014
 * Module: com.scorpio4.fact
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 7:13 PM
 */
public class FactSpace implements Identifiable {
    RepositoryConnection connection;
    String context;

    public FactSpace(RepositoryConnection connection, String context) {
        this.connection=connection;
        this.context=context;
    }

    public RepositoryConnection getConnection() {
        return connection;
    }

    @Override
    public String getIdentity() {
        return context;
    }
}
