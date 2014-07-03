package com.scorpio4.test;

import com.scorpio4.oops.FactException;
import com.scorpio4.vendor.sesame.store.MemoryRDFSRepository;
import groovy.util.GroovyTestCase;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.test
 * User  : lee
 * Date  : 3/07/2014
 * Time  : 10:59 PM
 */
public class Scorpio4TestCase extends GroovyTestCase {

	public MemoryRDFSRepository repository;
	public RepositoryConnection connection;
	public ApplicationContext applicationContext;

	public void initialize() throws RepositoryException, IOException, FactException {
		repository = new MemoryRDFSRepository();
		connection = repository.getConnection();
		applicationContext = new GenericApplicationContext();
	}

	public void provision(String resourcePath) throws RepositoryException, FactException, IOException {
		if (repository==null) initialize();
		repository.deploy(resourcePath);
	}

	public void finalize() throws RepositoryException {
		if (connection!=null) connection.close();
		if (repository!=null) repository.shutDown();
	}
}
