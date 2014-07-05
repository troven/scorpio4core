package com.scorpio4.vendor.spring;

import org.openrdf.repository.RepositoryConnection;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.vendor.spring
 * User  : lee
 * Date  : 6/07/2014
 * Time  : 5:17 AM
 */
public class Scorpio4BeanFactory extends DefaultListableBeanFactory{
	RepositoryConnection connection;

	public Scorpio4BeanFactory(RepositoryConnection connection) {
		this.connection=connection;
	}



}
